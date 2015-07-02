package Trie;

/**
 * Created by Administrator on 7/2/2015.
 */
public class Trie
{
    Node root;

    public Trie()
    {
        this.root = new Node();
    }

    public boolean insert(String entry, String content)
    {
        //current node
        Node current = root;

        //get string length
        int length = entry.length();

        //insert the entry
        int index;
        for(index = 0; index < length; index++)
        {
            //check if the node is null
            if(current.next == null)
            {
                
            }
        }
    }

    public String search(String entry)
    {

    }
}
