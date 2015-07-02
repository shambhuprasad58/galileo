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
    Trie dictionary;

    public T9()
    {
        dictionary = new Trie();
    }

    public void addToDictionary(String id, ContactData value)
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
        LinkedList<ContactData> masterList = new LinkedList<ContactData>();
        Stack<Integer> stack = new Stack<Integer>();
        Stack<Node> nodeStack = new Stack<Node>();
        Node[] list;

        Node current = dictionary.getHead();
        int temp;

        //empty filter results
        if(current == null)
        {
            return null;
        }

        if(current.getNextChar() > 0)
        {
        }
        else
        {
            list = ((NodeList)current.getNext()).getList();
            for(temp = 0; temp < 12 && list[temp] == null; temp++);
            if (temp < 12)
            {
                stack.push(temp);
                nodeStack.push(current);
                current = list[temp];
            }
        }
        nodeStack.push(current);


        do
        {
            temp = stack.pop();
            current = nodeStack.pop();

            //add node content
            masterList.addAll(current.getEntries());

            //singleton branch
            if(current.getNextChar() > 0)
            {
                current = current.getNext();
            }
            //general branch
            else
            {
                list = ((NodeList)current.getNext()).getList();
                for(temp++; temp < 12 && list[temp] == null; temp++);
                if(temp < 12)
                {
                    stack.push(temp);
                    nodeStack.push(current);
                    current = list[temp];
                }
            }
        }while(!stack.isEmpty());

        return null;
    }
}
