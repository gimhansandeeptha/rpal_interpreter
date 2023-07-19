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
            
        case WHERE:
            equal = node.getChild().getSibling();
            node.getChild().setSibling(null);
            equal.setSibling(node.getChild());
            node.setChild(equal);
            node.setType(ASTNodeType.LET);
            standardize(node);
            break;

        case FCNFORM:
            ASTNode childSibling = node.getChild().getSibling();
            node.getChild().setSibling(lambdaChain(childSibling));
            node.setType(ASTNodeType.EQUAL);
        
        case AT:
            ASTNode e1 = node.getChild();
            ASTNode n = e1.getSibling();
            ASTNode e2 = n.getSibling();
            ASTNode gamma = new ASTNode();
            gamma.setType(ASTNodeType.GAMMA);
            gamma.setChild(n);
            e1.setSibling(null);
            n.setSibling(e1);
            gamma.setSibling(e2);
            node.setChild(gamma);
            node.setChild(gamma);
            node.setType(ASTNodeType.GAMMA);
            break;

        case WITHIN:
            ASTNode x1 = node.getChild().getChild();
            e1 = x1.getSibling();
            ASTNode x2 = node.getChild().getSibling().getChild();
            e2 = x2.getSibling();
            gamma = new ASTNode();
            gamma.setType(ASTNodeType.GAMMA);
            ASTNode lambda = new ASTNode();
            lambda.setType(ASTNodeType.LAMBDA);
            node.setType(ASTNodeType.EQUAL);
            x1.setSibling(e2);
            lambda.setChild(x1);
            lambda.setSibling(e1);
            gamma.setChild(lambda);
            x2.setSibling(gamma);
            node.setChild(x2);
        
        

        default:
            break;
    }
    }

    
}
