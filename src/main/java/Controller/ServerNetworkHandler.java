package Controller;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ServerNetworkHandler implements Runnable, ClientObserver {


    private static ServerSocket server;
    private static Socket[] clients;
    private static ClientAdapter[] adapters;
    private static VCEvent fromClient1;
    private static VCEvent fromClient2;
    private static VCEvent fromClient3;
    private static Integer pingFromClient1;
    private static Integer pingFromClient2;
    private static Integer pingFromClient3;
    private static String[] usernames;
    private static Integer numberOfPlayers;
    public final static int SOCKET_PORT = 7777;
    private static  ObjectOutputStream[] outputs;
    private static ObjectInputStream[] inputs;
    private static boolean[] canWrite;

    public ServerNetworkHandler()
    {
        clients = new Socket[3];
        canWrite = new boolean[3];
        adapters = new ClientAdapter[3];
        outputs = new ObjectOutputStream[3];
        inputs = new ObjectInputStream[3];
    }

    public static void main(String args[])
    {
        ServerNetworkHandler snw = new ServerNetworkHandler();
        snw.run();
    }


    public void run() {
        try{
            server = new ServerSocket(SOCKET_PORT);
        }catch(IOException E)
        {
            System.out.println("cannot open server socket");
            System.exit(1);
            return;
        }
        //per ogni client che prendiamo dobbiamo creare salvarci l'adapter nell'array adapters e la sua socket nell'array clientse aggiungere il server come observer
        int counter = 0;
        Runnable runClient1 = ()->{
            while(true)
            {
                synchronized (this)
                {
                    fromClient1 = null;
                    while (fromClient1 == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }
                    //qui ho un evento dal client 1 da mandare alla VirtualView oppure un ping
                    System.out.println("Il client1 ha scritto "+ fromClient1.getBox());
                }
            }

        };

        Runnable runPingClient1 = ()->{
            while(true)
            {


                VCEvent pingEvent = new VCEvent((Integer) 1, VCEvent.Event.ping);
                System.out.println("Sto mandando un ping al client 1");
                VCEvent ev = new VCEvent("Fanculo", VCEvent.Event.you_lost);
                //sendVCEventTo(ev, 0);
                sendPingTo(pingEvent, 0);

                System.out.println("Ho mandato il ping al client 1");

                synchronized (this) {
                    pingFromClient1 = 0;
                    while (pingFromClient1 == 0) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                    if(pingFromClient1 == 0)
                    {
                        System.out.println("Il client 1 si è scollegato");
                    }
                   System.out.println("Pong1");
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        };



        Runnable runClient2 = ()->{
            while(true) {
                synchronized (this) {
                    fromClient2 = null;
                    while (fromClient2 == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }
                    //qui ho un evento dal client 2 da mandare alla VirtualView oppure un ping
                    System.out.println("Il client2 ha scritto "+ fromClient2.getBox());
                }
            }

        };

        Runnable runPingClient2 = ()->{
            while(true) {


                System.out.println("MAndo il ping al client 2");
                VCEvent pingEvent = new VCEvent((Integer) 2, VCEvent.Event.ping);
                VCEvent event = new VCEvent("Fanculo", VCEvent.Event.you_lost);
                sendVCEventTo(event,1);
                System.out.println(canWrite[1]);
                sendPingTo(pingEvent, 1);
                System.out.println("Ho mandato il ping al client 2...");

                System.out.println("Aspetto il client 2...");
                synchronized (this) {
                    pingFromClient2 = 0;
                    while (pingFromClient2 == 0) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                    if(pingFromClient2 == 0)
                    {
                        System.out.println("Il client 2 si è scollegato");
                    }
                    System.out.println("Pong 2");
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        };

        Runnable runClient3 = ()->{
            while(true) {
                synchronized (this) {
                    fromClient3 = null;
                    while (fromClient3 == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }
                    //qui ho un evento dal client 3 da mandare alla VirtualView oppure un ping
                    System.out.println("Il client3 ha scritto "+ fromClient3.getBox());
                }
            }

        };
        Runnable runPingClient3 = ()->{
            while(true) {

                VCEvent pingEvent = new VCEvent((Integer) 3, VCEvent.Event.ping);

                sendPingTo(pingEvent, 2);


                synchronized (this) {
                    pingFromClient3 = null;
                    while (pingFromClient3 == null) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                    if(pingFromClient3 == 0)
                    {
                        System.out.println("Il client 3 si è scollegato");
                    }
                    System.out.println("Pong 3");
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        };

        Thread th1 = new Thread(runClient1);
        Thread th2 = new Thread(runClient2);
        Thread th3 = new Thread(runClient3);
        Thread thPing1 = new Thread(runPingClient1);
        Thread thPing2 = new Thread(runPingClient2);
        Thread thPing3 = new Thread(runPingClient3);
            try {
                clients[counter] = server.accept();
                clients[counter].setSoTimeout(20000);
                outputs[counter] = new ObjectOutputStream( clients[counter].getOutputStream());
                inputs[counter] = new ObjectInputStream(clients[counter].getInputStream());
                adapters[counter] = new ClientAdapter(clients[counter], counter);
                adapters[counter].setInput(inputs[counter]);
                adapters[counter].setOutput(outputs[counter]);
                adapters[counter].addObserver(this);
                canWrite[counter] = true;
                Thread thread1 = new Thread(adapters[counter]);
                thread1.start();
                counter++;

            } catch (IOException e) {
                System.out.println("connection dropped");
            }


        VCEvent e1 = new VCEvent("Give me your username", VCEvent.Event.setup_request);
        thPing1.start();

        //qui dovrò aspettare un VCEvent dal primo client connesso con il numero di giocatori perchè poi mi serve per accettare gli altri
        while(true){
            sendVCEventTo(e1,0);
            synchronized (this)
            {
                fromClient1 = null;
                while (fromClient1 == null) {
                    try {
                        wait(10000);
                    } catch (InterruptedException e) { }
                }
            }
            if (fromClient1.getBox() instanceof String && fromClient1.getCommand() == VCEvent.Event.setup_request) {
                System.out.println("Ho ricevuto il primo nome "+ (String) fromClient1.getBox());
                e1 = null;
                e1 = new VCEvent("Age", VCEvent.Event.update);
            }
            else if ((fromClient1.getBox() instanceof Integer) && e1.getCommand() == VCEvent.Event.update)
            {
                e1 = null;
                e1 = new VCEvent("NumeroGiocatori", VCEvent.Event.setup_request);
            }
            else {
                if (fromClient1.getBox() instanceof Integer) // se il box è un Integer allora vuol dire che ci avrà mandato il numero di giocatori
                    break;
            }
        }
        th1.start(); // continuo a mettermi  in ascolto del client 1
        numberOfPlayers = (Integer) fromClient1.getBox();
        Integer np = numberOfPlayers;

        while (numberOfPlayers != 1) {
            try{
                clients[counter] = server.accept();
                clients[counter].setSoTimeout(20000);
                outputs[counter] = new ObjectOutputStream( clients[counter].getOutputStream());
                inputs[counter] = new ObjectInputStream(clients[counter].getInputStream());
                adapters[counter] = new ClientAdapter(clients[counter],counter);
                adapters[counter].addObserver(this);
                adapters[counter].setInput(inputs[counter]);
                canWrite[counter] = true;
                adapters[counter].setOutput(outputs[counter]);
                Thread thread = new Thread(adapters[counter]);
                thread.start();
                counter++;
                if (counter == 2) {
                    System.out.println("Attivo il client 2");
                    thPing2.start();
                    th2.start();
                }
                if (counter == 3) {
                    System.out.println("Attivo il client 3");
                    thPing3.start();
                    th3.start();
                }
            }catch(IOException e)
            {
                System.out.println("connection dropped");
            }
            numberOfPlayers--;

        }





        Integer x = 1;
        while (true)
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("A chi vuoi mandare un messaggio?");
             x = Integer.parseInt(sc.nextLine());
            System.out.println("Scrivi il contenuto del messaggio:");
             String comando = sc.nextLine();
            VCEvent e2 = new VCEvent(null,null);
            e2.setBox(comando);
            sendVCEventTo(e2,x);



        }


    }

    public synchronized void didReceiveVCEventFrom(VCEvent eventFromClient,int n) {
        switch (n)
        {
            case 0:
                fromClient1 = eventFromClient;
                notifyAll();
                break;
            case 1:
                fromClient2 = eventFromClient;
                notifyAll();
                break;
            case 2:
                fromClient3 = eventFromClient;
                notifyAll();
            default:
                break;
        }

    }
    public synchronized void didReceivePingFrom(Integer p,int n)
    {
        switch (n)
        {
            case 0:
                pingFromClient1 = p;
                notifyAll();
                break;
            case 1:
                pingFromClient2 = p;
                notifyAll();
                break;
            case 2:
                pingFromClient3 = p;
                notifyAll();
                break;
            default:
                break;
        }

    }

    public synchronized void sendVCEventTo(VCEvent eventToClient, int clientIndex)
    {
        synchronized (this)
        {
            while (canWrite[clientIndex] == false)
            {
                try {
                    wait();
                }catch(InterruptedException e){}
            }
        }
        // qui avrò che la canWrite sarà true, quindi lo pongo a false
        canWrite[clientIndex] = false;
        System.out.println("Sto mandando "+ eventToClient.getBox());

        try {
            outputs[clientIndex].writeObject(eventToClient);
        } catch (IOException e) {
            System.out.println("client has died");
        }
        canWrite[clientIndex] = true;
        notifyAll();



    }

    public synchronized void sendPingTo(VCEvent pingEvent, int clientIndex)
    {
        synchronized (this)
        {
            while (canWrite[clientIndex] == false)
            {
                try {
                    wait();
                }catch(InterruptedException e){}
            }
        }
        // qui avrò che la canWrite sarà true, quindi lo pongo a false
        canWrite[clientIndex] = false;

        try {
            outputs[clientIndex].writeObject(pingEvent);
        } catch (IOException e) {
            System.out.println("client has died");
        }
        canWrite[clientIndex] = true;
        System.out.println("Ping mandato a "+ clientIndex);
        notifyAll();


    }






}
