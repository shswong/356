import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

class ControllerIterator
{
    private Timer timer = new Timer();

    public void start(){ timer.schedule(new TimerTask(){ public void run(){ Controller.retryQueuedRequests(); } }, new Date()); }
}
