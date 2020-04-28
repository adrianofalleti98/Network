package View;

import Controller.VCEvent;

import java.io.*;
import java.net.Socket;


public class ClientNetworkHandler implements Runnable, ServerObserver {

    private VCEvent fromServer;
    private ServerAdapter adapter;
    private Socket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean endGame;
    private String winner;
    private Integer ping;
    private boolean canWrite;
    private int PlayerID;



    private boolean updateView;

    public static void main( String[] args )
    {
        /* Instantiate a new Client which will also receive events from
         * the server by implementing the ServerObserver interface */
        ClientNetworkHandler client = new ClientNetworkHandler();
        client.run();
    }

    public boolean isUpdateView() {
        return updateView;
    }

    public void run() {

        try {
            server = new Socket("127.0.0.1",7777);
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream((server.getInputStream()));

        } catch (IOException e) {
            System.out.println("server unreachable");
            return;
        }
        System.out.println("Connected");
        /*qua creo un'istanza della View chiamando il suo costruttore e passandogli questo network handler
        in modo tale da poi permetterci di chiamare i metodi della View sotto che dovranno gestire l'input e l'output
        */

        Runnable runPing = ()->{
            while(true)
            {

               // System.out.println("Aspetto il ping dal server...");
                synchronized (this) {
                    ping = 0;
                    while (ping == 0) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                        }
                    }

                    if (ping == 0)
                        System.out.println("Il server ha mollato...");
                    else {
                        System.out.println("Ping");
                        // System.out.println("Rispondo");
                        VCEvent event = new VCEvent("Fanculo", VCEvent.Event.not_your_turn);
                       // sendVCEvent(event);
                        sendPing();//appena riceve manda indietro il ping per fargli sapere che è ancora attivo
                        System.out.println(canWrite);
                    }
                }

            }
        };

        adapter = new ServerAdapter(server);
        adapter.addObserver(this);
        adapter.setInput(input);
        adapter.setOutput(output);
        Thread thread = new Thread(adapter);
        thread.start();
        Thread threadPing = new Thread(runPing);
        threadPing.start();
        System.out.println("Ho fatto partire la ricezione del ping...");
        canWrite = true;


        while (true)
        {
            synchronized (this)
            {
                fromServer = null;
                while (fromServer == null) {
                    updateView = false;
                    try {
                        wait();
                    } catch (InterruptedException e) { }
                }
            }
            updateView = true;
            System.out.println((String)fromServer.getBox());
            VCEvent e = new VCEvent(null, VCEvent.Event.setup_request);
            BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
            String comando = null;
            try {
                comando = sc.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Integer n = 0;
            if ((comando.equals("2") || comando.equals("3")) && fromServer.getBox().equals("NumeroGiocatori")) {
                n = Integer.parseInt(comando);
                System.out.println("è un numero " + n);
                e.setBox(n);
            } else if (fromServer.getCommand() == VCEvent.Event.update) {
                n = Integer.parseInt(comando);
                e.setBox(n);
            } else
                {
                e.setBox(comando);
            }
            sendVCEvent(e);
        }

    }


    public synchronized void didReceiveVCEvent(VCEvent eventFromServer) {
        fromServer = eventFromServer;
        notifyAll();
    }
    public synchronized void didReceivePing(Integer n)
    {
       // System.out.println("Ho ricevuto il ping...");
        PlayerID = n-1;
        ping = n;
        notifyAll();
    }
    //questo metodo verrà chiamato dalla VIEW
    public synchronized void sendVCEvent(VCEvent eventToServer)
    {
        synchronized (this)
        {
            while (canWrite == false)
            {
                try {
                    wait();
                }catch(InterruptedException e){}
            }
        }
        // qui avrò che la canWrite sarà true, quindi lo pongo a false
        canWrite = false;

        try {
            output.writeObject(eventToServer);
        } catch (IOException e) {
            System.out.println("server has died for vcevent");
        }
        canWrite = true;
        notifyAll();

    }

    public synchronized void sendPing()
    {
        synchronized (this)
        {
            while (canWrite == false)
            {
                try {
                    wait();
                }catch(InterruptedException e){}
            }
        }
        // qui avrò che la canWrite sarà true, quindi lo pongo a false
        canWrite = false;

        VCEvent pingEventResponse = new VCEvent(ping, VCEvent.Event.ping);
        try {
            output.writeObject(pingEventResponse);
        } catch (IOException e) {
            System.out.println("server has died for ping");
        }

        canWrite = true;
        notifyAll();


    }
}
