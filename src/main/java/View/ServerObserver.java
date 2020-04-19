package View;
import Controller.VCEvent;

public interface ServerObserver {
    public void didReceiveVCEvent(VCEvent event);
}
