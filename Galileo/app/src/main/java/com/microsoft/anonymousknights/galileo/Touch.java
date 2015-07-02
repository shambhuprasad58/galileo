package com.microsoft.anonymousknights.galileo;

/**
 * Created by sam on 7/2/2015.
 */
public class Touch {
    public double pos_x, pos_y;
    public int type;
    public long timestamp;

    public Touch(){}
    public Touch(double pos_x, double pos_y, int type, long timestamp)
    {
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.type = type;
        this.timestamp = timestamp;
    }
}
