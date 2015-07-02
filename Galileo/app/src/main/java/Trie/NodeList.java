package Trie;

/**
 * Created by Administrator on 7/2/2015.
 */
public class NodeList extends Node
{
    public Node[] getList() {
        return list;
    }

    public void setList(Node[] list) {
        this.list = list;
    }

    public Node[] list;

    public NodeList(Node parent)
    {
        this.list = new Node[]
        {
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent,
            parent
        };
    }
}
