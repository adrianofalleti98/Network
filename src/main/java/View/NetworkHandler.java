package View;

import Controller.VCEvent;
import sun.nio.ch.Net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.IOException;


public class NetworkHandler implements Runnable, ServerObserver {

    private VCEvent fromServer;
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
        /*qua creo un'istanza della View chiamando il suo costruttore e passandogli questo network handler
        in modo tale da poi permetterci di chiamare i metodi della View sotto che dovranno gestire l'input e l'output
        */
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
                //della view corrispondente. Poi la view chiamerà il metodo sendVCEvent passandogli il VCEvent da mandare al Server

                //UNA SOLUZIONE ALTERNATIVA POTREBBE ESSERE ANCHE MANDARE DIRETTAMENTE L'EVENTO ALLA VIEW, MA DIPENDE
                //DA COME ALFREDO VUOLE IMPLEMENTARE LA CLI/GUI
                VCEvent.Event command = fromServer.getCommand();
                switch (command)
                {
                    case setup_request:
                        //chiamo metodo della view
                        break;
                    case username_request:
                        //chiamo metodo della view
                        break;
                    case not_your_turn:
                        //chiamo metodo della view
                        break;
                    case update:
                        //chiamo metodo della view
                        break;
                    case send_cells_move:
                        //chiamo metodo della view
                        break;
                    case send_cells_build:
                        //chiamo metodo della view
                        break;
                    case you_lost:
                        //chiamo metodo della view
                        break;
                    case send_all_cards:
                        //chiamo metodo della view
                        break;
                    case send_chosen_cards:
                        //chiamo metodo della view
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

    //questo metodo verrà chiamato dalla VIEW
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
