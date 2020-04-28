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

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public void run()
    {
        try {
           handleServerConnection();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("server has died in the adapter");
        } catch(ClassNotFoundException e)
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


        while (true)
        {
            VCEvent evento = (VCEvent) input.readObject();

            if (evento  != null)
            {
                        //System.out.println("Il comando è "+ evento.getCommand() + "mentre il box è  "+ evento.getBox());
                if (evento.getCommand() != Event.ping)
                {
                    for (ServerObserver observer : observersCpy)
                        observer.didReceiveVCEvent(evento);
                }
                else
                {
                    for (ServerObserver observer : observersCpy)
                    {
                        //System.out.println("Ho ricevuto un ping, ora chiamo didReceivePing... " + evento.getBox());
                        observer.didReceivePing((Integer) evento.getBox());
                    }
                }
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
