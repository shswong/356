// Robert Cheng
// email:  robertmc@gmail.com
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class CheckQueue
{
    private final Timer timer = new Timer();

    public void start()
    {

		timer.schedule(new TimerTask(){
		public void run()
		{
			Controller.retryQueuedRequests();
		}
	}, new Date(), 5000);
    }
}
