package T9;

/**
 * Created by Administrator on 7/2/2015.
 */
public class T9
{
    int dictionarySize;
    int[] skipList;
    String[] dictionary;

    public T9()
    {
        dictionarySize = 0;
    }

    public T9(int dictionarySize)
    {
        this.dictionarySize = dictionarySize;
        //allocate skip list
        skipList = new int[dictionarySize];
        //allocate dictionary
        dictionary = new String[dictionarySize];
    }

    public void clear()
    {
        int index;
        //clear skip list
        for(index = 0; index < dictionarySize; index++)
        {
            skipList[index] = 1;
        }
    }

    public void filter(char key)
    {

    }
}
