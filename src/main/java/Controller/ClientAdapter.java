package Controller;
import View.ServerObserver;

import java.io.IOException;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;


public class ClientAdapter implements Runnable {

    private Socket client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private List<ClientObserver> observers = new ArrayList<ClientObserver>();
    private VCEvent eventFromClient;
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



    public void run() {
        try {
            output = new ObjectOutputStream(client.getOutputStream());
            input = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("server has died");
        }
        try {
            client.close();
        } catch (IOException e) { }

    }

    public synchronized void handleClientConnection() throws IOException,ClassNotFoundException
    {
        List<ClientObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<ClientObserver>(observers);
        }
        while (true)
        {
            VCEvent evento = (VCEvent) input.readObject();
            if (evento != null)
                for (ClientObserver observer : observersCpy)
                    observer.didReceiveVCEventFrom(evento,this.number);
        }
    }
}
