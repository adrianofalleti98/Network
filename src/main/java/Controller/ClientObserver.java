package Controller;

public interface ClientObserver {
    public void didReceiveVCEventFrom(VCEvent event,int index);
    public void didReceivePingFrom(Integer p,int index);
}
