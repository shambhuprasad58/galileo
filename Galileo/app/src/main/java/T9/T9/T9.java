package T9;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import Contacts.ContactData;
import Trie.Trie;
import Trie.Node;
import Trie.NodeList;

/**
 * Created by Administrator on 7/2/2015.
 */
public class T9
{
    public Trie getDictionary() {
        return dictionary;
    }

    Trie dictionary;

    public String getCurrentString() {
        return currentString;
    }

    String currentString;
    public T9()
    {
        dictionary = new Trie();
        currentString = "";
    }

    public void addToDictionary(String id, ContactData value)
    {
        dictionary.insert(id, value);
    }

    public void clear()
    {
        dictionary.reset();
        currentString = "";
    }

    public int filter(char key)
    {
        if(key == '\b' && currentString.length() == 0)
        {
            return dictionary.getHead().getSubTreeSize();
        }
        if(key == '\b')
        {
            currentString = currentString.subSequence(0, currentString.length() - 1).toString();
        }
        else
        {
            currentString = currentString + key;
        }
        Node node = dictionary.filter(key);
        if(node == null)
            return 0;
        return node.getSubTreeSize();
    }
    public LinkedList<ContactData> traverseDictionary()
    {
        LinkedList<ContactData> masterList = new LinkedList<ContactData>();
        Stack<Integer> stack = new Stack<Integer>();
        Stack<Node> nodeStack = new Stack<Node>();
        Node[] list;

        Node current = dictionary.getHead();
        int temp = 0;

        //empty filter results
        if(current == null)
        {
            return null;
        }

        stack.push(-1);
        nodeStack.push(current);

        do
        {
            temp = stack.pop();
            current = nodeStack.pop();

            //add node content
            //masterList.addAll(current.getEntries());

            //singleton branch
            while (current != null && current.getNextChar() > 0)
            {
                //add node content
                masterList.addAll(current.getEntries());
                //update current
                current = current.getNext();
            }

            //general branch
            if(current != null && current.getNext() != null)
            {
                list = ((NodeList)current.getNext()).getList();
                for(temp++; temp < 12 && list[temp] == null; temp++);
                if(temp < 12)
                {
                    stack.push(temp);
                    nodeStack.push(current);
                    //current = list[temp];
                    stack.push(-1);
                    nodeStack.push(list[temp]);
                }
                else
                {
                    //add node content
                    masterList.addAll(current.getEntries());
                }
            }
            else if(current != null)
            {
                //add node content
                masterList.addAll(current.getEntries());
            }

        }while(!stack.isEmpty());

        return masterList;
    }
}
