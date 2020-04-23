package Controller;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.net.ServerSocket;
import java.util.Scanner;

public class ServerNetworkHandler implements Runnable, ClientObserver {


    private static ServerSocket server;
    private static Socket[] clients;
    private static ClientAdapter[] adapters;
    private static VCEvent fromClient1;
    private static VCEvent fromClient2;
    private static VCEvent fromClient3;
    private static String[] usernames;
    private static Integer numberOfPlayers;
    public final static int SOCKET_PORT = 7777;
    private static  ObjectOutputStream[] outputs;
    private static ObjectInputStream[] inputs;

    public ServerNetworkHandler()
    {
        clients = new Socket[3];
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

            try {
                clients[counter] = server.accept();
                outputs[counter] = new ObjectOutputStream( clients[counter].getOutputStream());
                inputs[counter] = new ObjectInputStream(clients[counter].getInputStream());
                adapters[counter] = new ClientAdapter(clients[counter], counter);
                adapters[counter].addObserver(this);
                Thread thread1 = new Thread(adapters[counter]);
                thread1.start();
                counter++;

            } catch (IOException e) {
                System.out.println("connection dropped");
            }

        VCEvent e1 = new VCEvent("Give me your username", VCEvent.Event.setup_request);

        //qui dovrò aspettare un VCEvent dal primo client connesso con il numero di giocatori perchè poi mi serve per accettare gli altri
        while(true){
            sendVCEventTo(e1,0);
            synchronized (this)
            {
                fromClient1 = null;
                while (fromClient1 == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) { }
                }
            }
            e1.setBox("How many players?");
            sendVCEventTo(e1,0);
            if (fromClient1.getBox() instanceof Integer) // se il box è un Integer allora vuol dire che ci avrà mandato il numero di giocatori
                break;
        }
        //a questo punto ho in fromClient1 il numero dei giocatori
        numberOfPlayers = (Integer) fromClient1.getBox();

        while (numberOfPlayers != 1) {
            try{
                clients[counter] = server.accept();
                outputs[counter] = new ObjectOutputStream( clients[counter].getOutputStream());
                inputs[counter] = new ObjectInputStream(clients[counter].getInputStream());
                adapters[counter] = new ClientAdapter(clients[counter],counter);
                adapters[counter].addObserver(this);
                Thread thread = new Thread(adapters[counter]);
                thread.start();
                counter++;
            }catch(IOException e)
            {
                System.out.println("connection dropped");
            }
            numberOfPlayers--;

        }

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
                    //qui ho un evento dal client 1 da mandare alla VirtualView
                }
            }

        };

        Runnable runClient2 = ()->{
            synchronized (this)
            {
                fromClient2 = null;
                while (fromClient2 == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) { }
                }
                //qui ho un evento dal client 2 da mandare alla VirtualView
            }

        };
        Runnable runClient3 = ()->{
            synchronized (this)
            {
                fromClient3 = null;
                while (fromClient3 == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                //qui ho un evento dal client 3 da mandare alla VirtualView
            }

        };

        Thread th1 = new Thread(runClient1);
        th1.start();
        Thread th2 = new Thread(runClient2);
        th2.start();
        Thread th3 = new Thread(runClient3);
        th3.start();
        int x = 0;
        while (true)
        {
            VCEvent e2 = new VCEvent(null,null);
            Scanner sc = new Scanner(System.in);
            String comando = sc.nextLine();
            e2.setBox(comando);
            sendVCEventTo(e2,x);
            x++;
            if (x > 2)
                x = 0;
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

    public void sendVCEventTo(VCEvent eventToClient, int clientIndex)
    {
        try {
            outputs[clientIndex].writeObject(eventToClient);
            outputs[clientIndex].close();
        }catch (IOException e)
        {
            System.out.println("client has died");
        }
    }







}
