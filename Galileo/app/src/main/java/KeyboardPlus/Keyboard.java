package KeyboardPlus;

import com.microsoft.anonymousknights.galileo.MainActivity;
import com.microsoft.anonymousknights.galileo.Touch;

/**
 * Created by arnabmi on 7/28/2015.
 */
public class Keyboard
{
    public RollingBuffer getKey(int i)
    {
        return key[i];
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
        if(rho2 - 1 == 0)
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

    public int getKey(Touch touch)
    {
        double maxProbability = -1;
        double probability;
        int bestKey = 0;

        for (int i = 0; i < key.length; i++)
        {
            probability = gaussian2(touch.pos_x, touch.pos_y, key[i].getxMean(), key[i].getyMean(), key[i].getxVar(), key[i].getyVar(), key[i].getCoVar());

            if(probability < 0)
            {
                return -1;
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
