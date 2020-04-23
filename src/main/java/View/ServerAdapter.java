package View;

import Controller.VCEvent;
import Controller.VCEvent.Event;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
//sarebbe l'observable, ossia il server
/*
 setup_request,
        username_request,
        not_your_turn,
        update,
        send_cells_move,
        send_cells_build,
        you_lost,
        send_all_cards,
        send_chosen_cards

 */
public class ServerAdapter implements Runnable {



    private Socket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;


    private List<ServerObserver> observers = new ArrayList<ServerObserver>();

    public ServerAdapter(Socket server)
    {
        this.server = server;
    }



    public void run()
    {
        try {
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());
           handleServerConnection();

        } catch (IOException e) {
            System.out.println("server has died");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("Eccezione di classe");
        }

    }

    private synchronized void handleServerConnection() throws IOException, ClassNotFoundException
    {
        List<ServerObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<ServerObserver>(observers);
        }
        VCEvent evento = null;
            while (true)
            {

                try {

                        evento = (VCEvent) input.readObject();
                        System.out.println(evento.getCommand());
                        for (ServerObserver observer : observersCpy)
                            observer.didReceiveVCEvent(evento);


                }
                catch(IOException e)
                {

                }

            }



    }



    public void addObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void removeObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.remove(observer);
        }
    }



}
