package Controller;
import View.ServerObserver;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.ArrayList;


public class ClientAdapter implements Runnable {

    private Socket client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private List<ClientObserver> observers = new ArrayList<ClientObserver>();
    private int number;//from 0 to 2 maximum in case there are 3 players


    public ClientAdapter(Socket client,int n){this.client = client;this.number = n;}

    public int getNumber() {
        return number;
    }


    public void addObserver(ClientObserver observer)
    {
        synchronized (observers) {
            observers.add(observer);
        }
    }


    public void removeObserver(ClientObserver observer)
    {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public void run() {
        try {

            handleClientConnection();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("Client has died(number "+ number);
        }
        catch (ClassNotFoundException e)
        {

        }
        try {
            client.close();
        } catch (IOException e) { }

    }

    public synchronized void handleClientConnection() throws IOException, ClassNotFoundException
    {
        List<ClientObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<ClientObserver>(observers);
        }

        while (true)
        {

            VCEvent evento = (VCEvent) input.readObject();

            if (evento != null)
            {

                if (evento.getCommand() != VCEvent.Event.ping) {
                    for (ClientObserver observer : observersCpy)
                        observer.didReceiveVCEventFrom(evento, this.number);
                } else {
                    for (ClientObserver observer : observersCpy)
                        observer.didReceivePingFrom((Integer) evento.getBox(), this.number);
                }
            }



        }
    }
}
