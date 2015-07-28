package Trie;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import Contacts.ContactData;
/**
 * Created by Administrator on 7/2/2015.
 */
public class Node implements Serializable
{
    //String->Ref
    LinkedList<ContactData> entry;

    public int getSubTreeSize() {
        return subTreeSize;
    }

    public void setSubTreeSize(int subTreeSize) {
        this.subTreeSize = subTreeSize;
    }

    int subTreeSize;

    public Node()
    {
        this(null);
    }
    public Node(Node parent)
    {
        this.parent = parent;
        this.entry = new LinkedList<ContactData>();
        this.nextChar = 0;
        this.subTreeSize = 0;
    }

    //String->Ref
    public void addEntry(ContactData entry)
    {
        this.entry.addLast(entry);
    }

    public LinkedList<ContactData> getEntries()
    {
        return entry;
    }

    public char getNextChar() {
        return nextChar;
    }

    public void setNextChar(char nextChar) {
        this.nextChar = nextChar;
    }

    char nextChar;

    public Node getNext() {

        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    Node next;

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    Node parent;
}
