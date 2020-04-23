package Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class VCEvent implements Serializable {

    private static final long serialVersionUID = 382104422531955291L;

    public enum Event{
        setup_request,
        username_request,
        not_your_turn,
        update,
        send_cells_move,
        send_cells_build,
        you_lost,
        send_all_cards,
        send_chosen_cards

    }

    private Object box; // potrà essere o una stringa o un array di celle, o un array di carte, o la board
    private Event command;


    public VCEvent(Object arg, Event command)
    {
        this.box = arg;
        this.command = command;

    }
    public void setCommand(Event event) {
        this.command = event;
    }

    public Event getCommand() {
        return command;
    }

    public void setBox(Object box) {
        this.box = box;
    }

    public Object getBox() {
        return box;
    }

    public void readObject(ObjectInputStream stream) throws IOException,ClassNotFoundException
    {
       stream.defaultReadObject();
    }

    public void writeObject(ObjectOutputStream stream) throws IOException
    {
        stream.defaultWriteObject();
    }

}
