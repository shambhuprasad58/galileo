package Trie;

import java.io.Serializable;

/**
 * Created by Administrator on 7/2/2015.
 */
public class NodeList extends Node implements Serializable
{
    public Node[] getList() {
        return list;
    }

    public void setList(Node[] list) {
        this.list = list;
    }

    public Node[] list;

    public NodeList()
    {
        this.list = new Node[12];
    }
}
