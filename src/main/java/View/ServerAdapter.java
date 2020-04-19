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

    private VCEvent event;
    private VCEvent.Event command;
    private Socket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private List<ServerObserver> observers = new ArrayList<>();

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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            server.close();
        } catch (IOException e) { }
    }

    private synchronized void handleServerConnection() throws IOException, ClassNotFoundException
    {
        /* ASPETTA I COMANDI che saranno gestiti dal NetworkHandler nel metodo run, infatti QUEST'ULTIMO leggerà il VCEvent
        e vedrà il suo campo command.A quel punto chiamerà il metodo(UNO TRA QUELLI xxxRequest) corrispondente a quel comando che
        a sua volta assegnerà al campo command del ServerAdapter un certo valore. A questo punto verrà risvegliata la wait dentro
        handleServerConnection grazie alla chiamata notifyAll() nei metodi xxxRequest.
        A questo punto in ognuna delle case, dobbiamo chiamare un metodo nella view per far si che possa essere gestita
        l'interazione con l'utente. Una volta che l'interazione è finita, la view dovrà:
        - settare il VCEvent del ServerAdapter, modificando l'attributo box
        - chiamare il metodo respondToRequest
        Il metodo respondToRequest manda semplicemente il suo VCEvent al server

        */

        while (true)
        {
            command = null;
            try {
                wait();
            } catch (InterruptedException e) { }

            if (command == null)
                continue;

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

    public synchronized void setUpRequest()
    {
        command = Event.setup_request;

        notifyAll();

    }

    //questo metodo viene utilizzato per mandare il VCEvent nel ServerAdapter al Server
    public synchronized void respondToRequest() throws IOException, ClassNotFoundException
    {


    }
    public synchronized void userNameRequest()
    {
        command = Event.username_request;

        notifyAll();
    }
    public synchronized void notYourTurnRequest()
    {
        command = Event.not_your_turn;

        notifyAll();
    }
    public synchronized void updateRequest()
    {
        command = Event.update;

        notifyAll();
    }
    public synchronized void sendCellsForMoveRequest()
    {
        command = Event.send_cells_move;

        notifyAll();
    }
    public synchronized void sendCellsForBuildRequest()
    {
        command = Event.send_cells_build;

        notifyAll();
    }
    public synchronized void youLostRequest()
    {
        command = Event.you_lost;

        notifyAll();
    }
    public synchronized void allCardsReceivedRequest()
    {
        command = Event.send_all_cards;

        notifyAll();
    }
    public synchronized void chosenCardsReceivedRequest()
    {
        command = Event.send_chosen_cards;

        notifyAll();
    }



}
