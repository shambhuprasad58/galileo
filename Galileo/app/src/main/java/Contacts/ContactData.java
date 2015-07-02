package Contacts;

import android.util.Log;

/**
 * Created by Administrator on 7/2/2015.
 */

public class ContactData {
    String name;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String number;
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public ContactData(String s1,String s2)
    {

        name = s1;
        number = s2;
        Log.d("---Contact Indo:", name);
        Log.d("---Contact Indo:", number);
    }

}
