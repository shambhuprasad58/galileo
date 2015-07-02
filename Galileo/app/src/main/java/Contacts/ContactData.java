package Contacts;

import android.util.Log;
import T9.*;
/**
 * Created by Administrator on 7/2/2015.
 */

public class ContactData {
    String name;
    String number;
    String name_t9format;
    String number_t9format;

    public String getNumber_t9format() {
        return number_t9format;
    }

    public void setNumber_t9format(String number_t9format) {
        this.number_t9format = number_t9format;
    }



    public String getName_t9format() {
        return name_t9format;
    }

    public void setName_t9format(String name_t9format) {
        this.name_t9format = name_t9format;
    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }



    public ContactData(String s1,String s2,T9 t9)
    {

        name = s1;
        number = s2;
        Log.d("---Contact Indo:", name);
        Log.d("---Contact Indo:", number);

        name_t9format = "";
        int nameLen = name.length();
        for(int i=0;i<nameLen;i++)
        {
            switch (name.toCharArray()[i])
            {
                case 'a':
                case 'A':
                case 'b':
                case 'B':
                case 'c':
                case 'C':name_t9format += 2;break;
                case 'd':
                case 'D':
                case 'e':
                case 'E':
                case 'f':
                case 'F':name_t9format += 3;break;
                case 'g':
                case 'G':
                case 'h':
                case 'H':
                case 'i':
                case 'I':name_t9format += 4;break;
                case 'j':
                case 'J':
                case 'k':
                case 'K':
                case 'l':
                case 'L':name_t9format += 5;break;
                case 'm':
                case 'M':
                case 'n':
                case 'N':
                case 'o':
                case 'O':name_t9format += 6;break;
                case 'p':
                case 'P':
                case 'q':
                case 'Q':
                case 'r':
                case 'R':
                case 's':
                case 'S':name_t9format += 7;break;
                case 't':
                case 'T':
                case 'u':
                case 'U':
                case 'v':
                case 'V':name_t9format += 8;break;
                case 'w':
                case 'W':
                case 'x':
                case 'X':
                case 'y':
                case 'Y':
                case 'z':
                case 'Z':name_t9format += 9;break;


            }
        }
        t9.addToDictionary(name_t9format, this);

        //now for number
        number_t9format = "";
        int numberLen = number.length();
        for(int i=0;i<numberLen;i++)
        {
            if(number.toCharArray()[i] < '0' | number.toCharArray()[i] > '9')
                continue;
            number_t9format += number.toCharArray()[i];
        }
        t9.addToDictionary(number_t9format, this);

    }

}
