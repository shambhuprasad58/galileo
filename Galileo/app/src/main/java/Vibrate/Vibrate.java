package Vibrate;
import android.content.Context;
import android.os.Vibrator;
/**
 * Created by Administrator on 7/2/2015.
 */
public class Vibrate
{
    Vibrator vibrator;

    public Vibrate(Vibrator vibrator)
    {
        this.vibrator = vibrator;
    }

    public void vibrate(int count, int delay, int strength)
    {
        for (int i = 0; i < count; i++)
        {
            vibrator.vibrate(strength);
            try
            {
                Thread.sleep(delay);                 //100 milliseconds is one second.
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}
