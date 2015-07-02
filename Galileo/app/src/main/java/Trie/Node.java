package Trie;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 7/2/2015.
 */
public class Node
{
    LinkedList<String> entry;

    public Node()
    {
        this.entry = new LinkedList<String>();
        this.nextChar = 0;
    }

    public void addEntry(String entry)
    {
        this.entry.addLast(entry);
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
}
