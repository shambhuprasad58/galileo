package T9;

import java.util.LinkedList;
import java.util.Stack;

import Contacts.ContactData;
import Trie.Trie;
import Trie.Node;

/**
 * Created by Administrator on 7/2/2015.
 */
public class T9
{
    Trie dictionary;

    public T9()
    {
        dictionary = new Trie();
    }

    public void addToDictionary(String id, String value)
    {
        dictionary.insert(id, value);
    }

    public void clear()
    {
        dictionary.reset();
    }

    public int filter(char key)
    {
        Node node = dictionary.filter(key);
        return node.getSubTreeSize();
    }

    public LinkedList<String>[] traverseDictionary()
    {
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(0);

        Node current = dictionary.getHead();
        int temp;
        //TODO: Empty Stack
        LinkedList<ContactData> list;
        do
        {
            temp = stack.pop();

            //singleton branch
            if(current.getNextChar() > 0)
            {
                current = current.getNext();
            }
            else
            {

            }
        }while(!stack.isEmpty());

        return null;
    }
}
