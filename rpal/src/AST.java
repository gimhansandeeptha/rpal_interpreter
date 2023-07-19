//import required libraries

import java.util.ArrayDeque;

public class AST {

    private  ASTNode root;
    private boolean isStandardized;
    private Delta current;
    private Delta rootDelta;
    private int index;
    private ArrayDeque<DeltaBody> deltaQueue;

    public AST(ASTNode root){
        this.root = root;
    }

    public void printTree(){
        preOrderPrint(this.root, "");
    }

    private void preOrderPrint(ASTNode node, String prefix){
        if (node == null){
            return;
        }

        printASTNode(node, prefix);
        preOrderPrint(node.getChild(), prefix+'.');
        preOrderPrint(node.getSibling(), prefix);
    }

    private void printASTNode(ASTNode node, String prefix){
        if (node.getType() == ASTNodeType.IDENTIFIER ||
            node.getType() == ASTNodeType.INTEGER||
            node.getType() == ASTNodeType.STRING){
                System.out.printf(prefix + node.getType().getName() + '\n', node.getValue());
            }

        else{
            System.out.println(prefix + node.getType().getName());
        }
    }

    public void standardize(){
        standardize(root);
        isStandardized = true;
    }

    private void standardize(ASTNode node){
        if (node.getChild() != null){
            ASTNode child = node.getChild();

            while (child != null){
                standardize(child);
                child = child.getSibling();
            }
        }

        switch (node.getType()) {
        case LET:
            ASTNode equal = node.getChild();

            if (equal.getType() != ASTNodeType.EQUAL){
                throw new StandardizeException("left child not equal");
            }

            ASTNode e = equal.getChild().getSibling();
            equal.getChild().setSibling(equal.getSibling());
            equal.setSibling(e);
            node.setType(ASTNodeType.GAMMA);
            equal.setType(ASTNodeType.LAMBDA);
            break;
    
        default:
            break;
    }
    }

    
}
