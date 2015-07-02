package T9;

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
}
