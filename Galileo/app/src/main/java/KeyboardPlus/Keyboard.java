package KeyboardPlus;

import com.microsoft.anonymousknights.galileo.AppConstants;
import com.microsoft.anonymousknights.galileo.MainActivity;
import com.microsoft.anonymousknights.galileo.Touch;

import java.io.Serializable;

/**
 * Created by arnabmi on 7/28/2015.
 */
public class Keyboard implements Serializable
{
    static int minPresses = 10;
    public Touch getKey(int i)
    {
        Touch t = new Touch();
        if(key[i].count >= 1)
        {
            t.pos_x = key[i].getxMean();
            t.pos_y = key[i].getyMean();
        }
        else
        {
            t.pos_x = AppConstants.TEXTVIEW_POSITION_X[i];
            t.pos_y = AppConstants.TEXTVIEW_POSITION_Y[i];
        }
        return t;
    }

    RollingBuffer key[];
    int lastKey;

    public Keyboard(int nKeys)
    {
        lastKey = -1;
        key = new RollingBuffer[nKeys];
        for (int i = 0; i < nKeys; i++)
        {
            key[i] = new RollingBuffer(1000);
            //burn in
            /*
            for(int j = 0; j < 10; j++)
            {
                key[i].insert(AppConstants.TEXTVIEW_POSITION_X[i] + AppConstants.TEXTVIEW_WIDTH/2, AppConstants.TEXTVIEW_POSITION_Y[i] + AppConstants.TEXTVIEW_HEIGHT/2);
            }
            */
        }


    }
    double gaussian2(double x, double y, double xMean, double yMean, double xVar, double yVar, double coVar)
    {
        if(xVar == 0 || yVar == 0)
        {
            return  -1;
        }
        double xSD = Math.sqrt(xVar);
        double ySD = Math.sqrt(yVar);
        double rho2 = coVar * coVar/ (xVar * yVar);
        if(rho2 >= 1)
        {
            return -1;
        }
        double output = (x - xMean) / xSD - (y - yMean) / ySD;
        output = output * output;
        output = -output / (2 * (1 - rho2));
        output = Math.exp(output);
        output /= xSD;
        output /= ySD;
        output /= Math.sqrt(1 - rho2);
        return output;
    }

    double dist2(Touch t, double xMean, double yMean)
    {
        return (t.pos_x - xMean) * (t.pos_x - xMean) + (t.pos_y - yMean) * (t.pos_y - yMean);
    }

    public int getKey(Touch touch)
    {
        double maxProbability = -1;
        double probability;
        int bestKey = 0;

        for (int i = 0; i < key.length; i++)
        {
            //min key
            if(dist2(touch, key[i].getxMean(), key[i].getyMean()) < AppConstants.MIN_KEY_RADIUS)
            {
                return i;
            }
            probability = gaussian2(touch.pos_x, touch.pos_y, key[i].getxMean(), key[i].getyMean(), key[i].getxVar(), key[i].getyVar(), key[i].getCoVar());

            //fallback to hard keys
            //if(key[i].getCount() < minPresses || probability < 0)
            if(true)
            {
                for (int j = 0; j < key.length; j++)
                {
                    if(touch.pos_x > AppConstants.TEXTVIEW_POSITION_X[j] && touch.pos_y > AppConstants.TEXTVIEW_POSITION_Y[j] && touch.pos_x < (AppConstants.TEXTVIEW_POSITION_X[j] + AppConstants.TEXTVIEW_WIDTH) && touch.pos_y < (AppConstants.TEXTVIEW_POSITION_Y[j] + AppConstants.TEXTVIEW_HEIGHT))
                        return j;
                }
                return 11;
            }

            if(probability > maxProbability)
            {
                maxProbability = probability;
                bestKey = i;
            }
        }

        return bestKey;
    }

    public void train(Touch touch, int key)
    {
            lastKey = key;
            this.key[key].insert(touch.pos_x, touch.pos_y);
    }

    public void rollBackLastTrain()
    {
        if(lastKey >= 0)
        {
            lastKey = -1;
            this.key[lastKey].back();
        }
    }
}
