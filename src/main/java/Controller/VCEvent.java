package Controller;

public class VCEvent {
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


}
