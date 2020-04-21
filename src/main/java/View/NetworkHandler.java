package View;

import Controller.VCEvent;
import sun.nio.ch.Net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.IOException;


public class NetworkHandler implements Runnable, ServerObserver {

    private VCEvent fromServer;
    private VCEvent toServer;
   private ServerAdapter adapter;
   private Socket server;

    public static void main( String[] args )
    {
        /* Instantiate a new Client which will also receive events from
         * the server by implementing the ServerObserver interface */
        NetworkHandler client = new NetworkHandler();
        client.run();
    }


    public void run() {

        try {
            server = new Socket("127.0.0.1",7777);
        } catch (IOException e) {
            System.out.println("server unreachable");
            return;
        }
        System.out.println("Connected");

         adapter = new ServerAdapter(server);
        adapter.addObserver(this);
        Thread thread = new Thread(adapter);
        thread.start();

        while (true)
        {
            synchronized (this)
            {
                fromServer = null;
                while (fromServer == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) { }

                }
                //qui l'evento dal server sarà arrivato e ora devo gestirlo con una switch sul suo comando per chiamare il metodo
                //della view, Poi la view setterà l'evento da mandare al server e chiamerà il metodo per mandarglielo
                VCEvent.Event command = fromServer.getCommand();
                switch (command)
                {
                    case setup_request:
                        break;
                    case username_request:
                        break;
                    case not_your_turn:
                        break;
                    case update:
                        break;
                    case send_cells_move:
                        break;
                    case send_cells_build:
                        break;
                    case you_lost:
                        break;
                    case send_all_cards:
                        break;
                    case send_chosen_cards:
                        break;
                    default:
                        return;
                }


            }
        }

    }


    public synchronized void didReceiveVCEvent(VCEvent eventFromServer) {
        fromServer = eventFromServer;
        notifyAll();
    }

    public void sendVCEvent(VCEvent eventToServer)
    {
        try {
            ObjectOutputStream output = new ObjectOutputStream(server.getOutputStream());
            output.writeObject(eventToServer);
        }catch (IOException e)
        {
            System.out.println("server has died");
        }
    }
}
