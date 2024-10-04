package Minimax;

import java.util.List;

public class MinimaxPruning {

    public static float minimax(MinimaxNode node, int depth,float alpha ,float beta,boolean maximisingPlayer){
        if(depth==0 || node.isLeafNode()) return node.evalNode();

        if(maximisingPlayer){
            float maxEva = -999999f;
            List<MinimaxNode> nodes = node.getNextNodes();

            for (MinimaxNode child:nodes) {
                float eva= minimax(child, depth-1, alpha, beta, false);
                maxEva= Math.max(maxEva, eva);
                alpha= Math.max(alpha, maxEva);
                if (beta<=alpha) break;
            }
            return maxEva;
        }else{
            float minEva = 999999f;
            List<MinimaxNode> nodes = node.getNextNodes();

            for (MinimaxNode child:nodes) {
                float eva= minimax(child, depth-1, alpha, beta, true);
                minEva= Math.min(minEva, eva);
                beta= Math.min(beta, minEva);
                if (beta<=alpha) break;
            }
            return minEva;
        }
    }
}
