package Trie;

import Contacts.ContactData;

/**
 * Created by Administrator on 7/2/2015.
 */
public class Trie
{
    public Node lastHead;
    public int lengthCount;
    int maxCount;
    public Node getRoot() {
        return root;
    }

    Node root;

    public Node getHead()
    {
        return head;
    }

    Node head;

    public Trie()
    {
        this.root = new Node(null);
        this.head = root;
        this.lastHead = null;
        lengthCount = 0;
        maxCount = 0;
    }

    public boolean insert(String entry, ContactData content)
    {
        //current node
        Node current = root;

        //get string length
        int length = entry.length();

        //insert the entry
        int index;
        char currentChar;
        Node temporary;
        Node[] tempList;
        NodeList tempNodeList;
        for(index = 0; index < length; index++)
        {
            currentChar = entry.charAt(index);
            //check if the node is null
            if(current.next == null)
            {
                //indicate singleton node
                current.setNextChar(currentChar);
                //set next node
                current.setNext(new Node(current));
                //update subtree size
                current.setSubTreeSize(current.getSubTreeSize() + 1);
                //traverse to it
                current = current.getNext();
            }
            //check for singleton node and need to insert new node
            else if(current.getNextChar() > 0 && current.getNextChar() != currentChar)
            {
                //store old next node
                temporary = current.getNext();

                //create node list
                tempNodeList = new NodeList();
                //get list
                tempList = tempNodeList.getList();
                //insert new node
                tempList[currentChar - '0'] = new Node(current);
                //insert old node
                tempList[current.getNextChar() - '0'] = temporary;
                //set next nodes
                current.setNext(tempNodeList);
                //indicate no longer singleton node
                current.setNextChar('\0');
                //update subtree size
                current.setSubTreeSize(current.getSubTreeSize() + 1);
                //traverse to it
                current = tempList[currentChar - '0'];
            }
            //check for singleton node and no need to insert new node
            else if(current.getNextChar() > 0)
            {
                //update subtree size
                current.setSubTreeSize(current.getSubTreeSize() + 1);
                //traverse to it
                current = current.getNext();
            }
            //node list exists
            else
            {
                //get old next node list
                tempNodeList = (NodeList)current.getNext();
                //get old list
                tempList = tempNodeList.getList();
                //need to insert new node
                if(tempList[currentChar - '0'] == null)
                {
                    tempList[currentChar - '0'] = new Node(current);
                    //add to node list
                    tempNodeList.setList(tempList);
                    //add back to next ref
                    current.setNext(tempNodeList);
                }
                //update subtree size
                current.setSubTreeSize(current.getSubTreeSize() + 1);
                //traverse to it
                current = tempList[currentChar - '0'];
            }
        }
        //reached end of list
        //add entry
        current.addEntry(content);
        //update subtree size
        current.setSubTreeSize(current.getSubTreeSize() + 1);

        return true;
    }

    public void reset()
    {
        head = root;
        maxCount = 0;
        lengthCount = 0;
    }

    public Node filter(char key)
    {
        Node[] nodeList;

        if(key == '\b')
        {
            lengthCount--;
        }
        else
        {
            lengthCount++;
        }

        if(head == null)
        {
            if(key == '\b' && lengthCount == maxCount - 1)
            {
                if(head == null)
                {
                    head = lastHead;
                    maxCount = lengthCount;
                }
            }
            else
            {
                return head;
            }
        }
        else
        {
            if(key == '\b')
            {
                maxCount--;
            }
            else
            {
                maxCount++;
            }
            //check for delete
            if(key == '\b')
            {
                if(head != root)
                {
                    head = head.getParent();
                }
            }
            //check for singleton node
            else if(head.getNextChar() > 0)
            {
                //traverse
                if(head.getNextChar() == key)
                {
                    lastHead = head;
                    head = head.getNext();
                }
                else
                {
                    lastHead = head;
                    head = null;
                }
            }
            else
            {
                nodeList = ((NodeList)head.getNext()).getList();
                lastHead = head;
                head = nodeList[key - '0'];
            }
        }

        return head;
    }

}
