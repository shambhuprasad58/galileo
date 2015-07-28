package KeyboardPlus;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by arnabmi on 7/28/2015.
 */
public class RollingBuffer implements Serializable{
    double xBuffer[];
    double yBuffer[];

    public int getCount() {
        return count;
    }

    int count;

    public double getxMean() {
        return xMean;
    }

    double xMean;

    public double getxVar() {
        return xVar;
    }

    double xVar;

    public double getyMean() {
        return yMean;
    }

    double yMean;

    public double getyVar() {
        return yVar;
    }

    double yVar;

    public double getCoVar() {
        return coVar;
    }

    double coVar;
    int pos;
    int length;

    public RollingBuffer(int length)
    {
        this.length = length;
        xBuffer = new double[length];
        yBuffer = new double[length];

        count = 0;
        xMean = 0;
        yMean = 0;
        xVar = 0;
        yVar = 0;
        coVar = 0;
        pos = 0;
    }

    public void insert(double x, double y)
    {
        pos = (pos + 1) % length;

        int newCount = count + 1;
        newCount = newCount > length ? length : newCount;
        double newXMean = (xMean * count + x - xBuffer[pos]) / newCount;
        double newYMean = (yMean * count + y - yBuffer[pos]) / newCount;
        double newXVar = ((xVar + xMean * xMean) * count + x * x - xBuffer[pos] * xBuffer[pos]) / newCount - newXMean * newXMean;
        double newYVar = ((yVar + yMean * yMean) * count + y * y - yBuffer[pos] * yBuffer[pos]) / newCount - newYMean * newYMean;
        double newCoVar = ((coVar + xMean * yMean) * count + x * y - xBuffer[pos] * yBuffer[pos]) / newCount - newXMean * newYMean;

        xMean = newXMean;
        yMean = newYMean;
        count = newCount;
        xVar = newXVar;
        yVar = newYVar;
        coVar = newCoVar;
    }

    public void back()
    {
        int newCount = 0;
        if(count > 1)
        {
            newCount = count - 1;
        }
        else
        {
            count = 0;
            xMean = 0;
            yMean = 0;
            xVar = 0;
            yVar = 0;
            coVar = 0;
            pos = 0;
            return;
        }

        double newXMean = (xMean * count  - xBuffer[pos]) / newCount;
        double newYMean = (yMean * count  - yBuffer[pos]) / newCount;
        double newXVar = ((xVar + xMean * xMean) * count - xBuffer[pos] * xBuffer[pos]) / newCount - newXMean * newXMean;
        double newYVar = ((yVar + yMean * yMean) * count - yBuffer[pos] * yBuffer[pos]) / newCount - newYMean * newYMean;
        double newCoVar = ((coVar + xMean * yMean) * count - xBuffer[pos] * yBuffer[pos]) / newCount - newXMean * newYMean;

        xMean = newXMean;
        yMean = newYMean;
        count = newCount;
        xVar = newXVar;
        yVar = newYVar;
        coVar = newCoVar;
    }
}
