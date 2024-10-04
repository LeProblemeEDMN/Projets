package Minimax;

import java.util.List;

public interface MinimaxNode {

    public List<MinimaxNode> getNextNodes();

    public boolean isLeafNode();

    public float evalNode();
}
