import java.util.ArrayList;
import java.util.Arrays;

public class MyAST {
    private ASTNode root;
    private boolean standardized;

    public MyAST(ASTNode root){
        this.root = root;
        standardized =false;
    }

    public void standardize(){
        standardize(root);
        standardized =true;
    }

    private void standardize(ASTNode node){
        ASTNode child = node.getChild();
        if(child !=null){
            standardize(child);
            ASTNode sibling = child.getSibling();
            if (sibling != null){
                standardize(sibling);
            }
        }
        standardizeNode(node);
    }

    private void standardizeNode(ASTNode node){
        if (node.getType().equals(ASTNodeType.LET)){
            ASTNode equal = node.getChild();
            if (! equal.getType().equals(ASTNodeType.EQUAL)) {System.out.println("should be EQUAL");}

            ASTNode E = equal.getChild().getSibling();
            equal.getChild().setSibling(equal.getSibling());
            equal.setSibling(E);
            node.setType(ASTNodeType.GAMMA);
            equal.setType(ASTNodeType.LAMBDA);

        }

        else if (node.getType().equals(ASTNodeType.WHERE)){
            ASTNode equal = node.getChild().getSibling();
            if (!equal.getType().equals(ASTNodeType.EQUAL)){System.out.println("should be Equal");}

            ASTNode E = equal.getChild().getSibling();
            node.getChild().setSibling(null);
            equal.getChild().setSibling(node.getChild());
            equal.setSibling(E);
            node.setChild(equal);
            equal.setType(ASTNodeType.LAMBDA);
            node.setType(ASTNodeType.GAMMA);
        }

        else if (node.getType().equals(ASTNodeType.WITHIN)){
            ASTNode eq1 = node.getChild();
            if (!eq1.getType().equals(ASTNodeType.EQUAL)){System.out.println("should be Equal");}
            ASTNode eq2 = eq1.getSibling();
            if (!eq2.getType().equals(ASTNodeType.EQUAL)){System.out.println("should be Equal");}

            node.setType(ASTNodeType.EQUAL);
            eq1.setType(ASTNodeType.LAMBDA);
            eq2.setType(ASTNodeType.GAMMA);

            ASTNode X2 = eq2.getChild();
            ASTNode E2 = X2.getSibling();
            X2.setSibling(eq2);
            eq1.setSibling(eq1.getChild().getSibling());
            eq1.getChild().setSibling(E2);

            eq2.setChild(eq1);
            node.setChild(X2);

        }

        else if (node.getType().equals(ASTNodeType.FCNFORM)){
            node.setType(ASTNodeType.EQUAL);

            while(node.getChild().getSibling() != null){
                ASTNode v = node.getChild().getSibling();
                ASTNode lambdaNode = new ASTNode();
                lambdaNode.setType(ASTNodeType.LAMBDA);
                lambdaNode.setChild(v);
                node.getChild().setSibling(lambdaNode);

                node = lambdaNode;
            }
        }

        else if (node.getType().equals(ASTNodeType.AT)){
            node.setType(ASTNodeType.GAMMA);
            ASTNode gammaNode = new ASTNode();
            gammaNode.setType(ASTNodeType.GAMMA);

            gammaNode.setSibling(node.getChild().getSibling().getSibling());
            node.getChild().getSibling().setSibling(node.getChild());
            gammaNode.setChild(node.getChild().getSibling());
            node.getChild().setSibling(null);
            node.setChild(gammaNode);
        }

        else if (node.getType().equals(ASTNodeType.AND)){
            ASTNode eq = node.getChild();
            if (!eq.getType().equals(ASTNodeType.EQUAL)){System.out.println("should be Equal");}

            ASTNode comma  = new ASTNode();
            ASTNode tau = new ASTNode();
            comma.setType(ASTNodeType.COMMA);
            tau.setType(ASTNodeType.TAU);
            tau.setChild(eq.getChild().getSibling());
            eq.getChild().setSibling(null);
            comma.setChild(eq.getChild());
            comma.setSibling(tau);

            if (eq.getSibling() == null){System.out.println("Should have at least another equal node");}

            while(eq.getSibling()!= null){
                eq = eq.getSibling();
                ASTNode commaIter = comma.getChild();
                ASTNode tauIter = tau.getChild();
                while(commaIter.getSibling() != null & tauIter.getSibling() != null){
                    commaIter = commaIter.getSibling();
                    tauIter = tauIter.getSibling();
                }
                if (commaIter.getSibling()!= null || tauIter.getSibling() != null){System.out.println("Error");}
                tauIter.setSibling(eq.getChild().getSibling());
                eq.getChild().setSibling(null);
                commaIter.setSibling(eq.getChild());
                eq = eq.getSibling();
            }

        }

        else if (node.getType().equals(ASTNodeType.REC)){
            if(!node.getChild().getType().equals(ASTNodeType.EQUAL) || node.getChild().getSibling() != null){
                System.out.println("Error: Rec should only have Equal node with no Siblings");
            }
            node.setType(ASTNodeType.EQUAL);
            ASTNode lambda = new ASTNode();
            ASTNode gamma = new ASTNode();
            ASTNode ystar = new ASTNode();

            lambda.setType(ASTNodeType.LAMBDA);
            gamma.setType(ASTNodeType.GAMMA);
            ystar.setType(ASTNodeType.Y);

            lambda.setChild(node.getChild().getChild());
            ystar.setSibling(lambda);
            gamma.setChild(ystar);

            ASTNode X = node.getChild().getChild();
            X.setSibling(gamma);
            node.setChild(X);

        }

        else if (node.getType().equals(ASTNodeType.LAMBDA)){
            ASTNode child = node.getChild();
            while(child.getSibling().getSibling() != null){
                ASTNode lambda = new ASTNode();
                lambda.setType(ASTNodeType.LAMBDA);
                lambda.setChild(child.getSibling());
                child.setSibling(lambda);
                child = child.getSibling();

            }
        }

        else{
            final ASTNodeType[] list = {ASTNodeType.OR,ASTNodeType.AND,ASTNodeType.PLUS, ASTNodeType.MINUS,ASTNodeType.MUL,
                ASTNodeType.DEV,ASTNodeType.EXP,ASTNodeType.GR,ASTNodeType.GE,ASTNodeType.LS,ASTNodeType.LE,ASTNodeType.EQ,
                ASTNodeType.NE, ASTNodeType.NOT, ASTNodeType.NEG,ASTNodeType.CONDITIONAL,ASTNodeType.TAU,ASTNodeType.COMMA};
            final ArrayList<ASTNodeType> arraylist = new ArrayList<>(Arrays.asList(list));

            if (!arraylist.contains(node.getType())){
                System.out.println("Error: ASTNode type is not in the standardization.");
            }
        }

    }

    public void printST(){
        if (!standardized){System.out.println("Error: First standardize the AST");}
        else{
            printAST(root);
        }
    }
    private void printAST(ASTNode node){
        if (node.getValue() == null) System.out.println(node.getName());
        else System.out.println(node.getValue());
        
        if (node.getChild() != null ) {
            printAST(node.getChild());
        }
        if (node.getSibling() != null ) {
            printAST(node.getSibling());
        }
    }

    
}
