//import required libraries

import java.util.ArrayDeque;
import java.util.Stack;

public class AST {

    private  ASTNode root;
    private boolean standardized;
    private Delta current;
    private Delta rootDelta;
    private int index;
    private ArrayDeque<DeltaBody> deltaBodyQueue;

    public AST(ASTNode root){
        this.root = root;
    }

    //prints the tree nodes in pre order traversal
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

    //Standardize the AST
    public void standardize(){
        standardize(root);
        standardized = true;
    }

    public boolean isStandardized(){
        return standardized;
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
        case LET://Standardizing 'let'
            ASTNode equal = node.getChild();

            if (equal.getType() != ASTNodeType.EQUAL){
                System.out.println("left child not equal");
            }

            ASTNode e = equal.getChild().getSibling();
            equal.getChild().setSibling(equal.getSibling());
            equal.setSibling(e);
            node.setType(ASTNodeType.GAMMA);
            equal.setType(ASTNodeType.LAMBDA);
        break;
            
        case WHERE://Standardizing 'where'
            equal = node.getChild().getSibling();
            node.getChild().setSibling(null);
            equal.setSibling(node.getChild());
            node.setChild(equal);
            node.setType(ASTNodeType.LET);
            standardize(node);
        break;

        case FCNFORM://Standardizing 'fcn_form'
            ASTNode childSibling = node.getChild().getSibling();
            node.getChild().setSibling(lambdaChain(childSibling));
            node.setType(ASTNodeType.EQUAL);
        break;
        
        case AT://Standardizing the '@' Operator
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
            node.setType(ASTNodeType.GAMMA);
        break;

        case WITHIN://Standardizing the 'within'
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
        break;

        case LAMBDA://
            childSibling = node.getChild().getSibling();
            node.getChild().setSibling(lambdaChain(childSibling));
        break;

        case AND://Standardizing Simultaneous Definitions
            ASTNode comma = new ASTNode();
            ASTNode tau = new ASTNode();
            comma.setType(ASTNodeType.COMMA);
            tau.setType(ASTNodeType.TAU);
            ASTNode child = node.getChild();

            while (child != null){
                populateCommaTau(child, comma, tau);
                child = child.getSibling();
            }

            node.setType(ASTNodeType.EQUAL);
            comma.setSibling(tau);
            node.setChild(comma);
        break;
        
        case REC://Standardizing 'rec'
            child = node.getChild();
            ASTNode x = child.getChild();
            lambda = new ASTNode();
            gamma = new ASTNode();
            ASTNode y = new ASTNode();
            lambda.setType(ASTNodeType.LAMBDA);
            gamma.setType(ASTNodeType.GAMMA);
            y.setType(ASTNodeType.Y);
            node.setType(ASTNodeType.EQUAL);
            lambda.setChild(x);
            y.setSibling(lambda);
            gamma.setChild(y);
            ASTNode childX = new ASTNode();
            childX.setType(x.getType());
            childX.setValue(x.getValue());
            childX.setChild(x.getChild());
            childX.setSibling(gamma);
            node.setChild(childX);
        break;

        default:
            //Do not standrdize
            //Unary operators
            //Binary operators
            //conditional operator
            //tau
            //","
            break;
        }
    }

    private void populateCommaTau(ASTNode equal, ASTNode comma, ASTNode tau){
        if (equal.getType() != ASTNodeType.EQUAL){
            System.out.println("Child is not EQUAL node");
        }
        ASTNode x= equal.getChild();
        ASTNode e = x.getSibling();
        setChild(comma, x);
        setChild(tau, e);
    }

    private ASTNode lambdaChain(ASTNode node){
        if (node.getSibling() == null){
            return node;
        }
        ASTNode lambda = new ASTNode();
        lambda.setType(ASTNodeType.LAMBDA);
        lambda.setChild(node);

        if (node.getSibling().getSibling() != null){
            node.setSibling(lambdaChain(node.getSibling()));
        }
        return lambda;
    }

    private void setChild(ASTNode parent, ASTNode child){
        if (parent.getChild() == null){ //if parent does not have a create child node
            parent.setChild(child);
        }
        else{//if parent has a child passes child to last sibling of the parent's children
            ASTNode sibling = parent.getChild();

            while (sibling.getSibling() != null){
                sibling = sibling.getSibling();
            }
            sibling.setSibling(child);
        }
        child.setSibling(null);
    }

    //Create delta structures from the standardized tree
    public Delta createDeltas(){
        deltaBodyQueue = new ArrayDeque<DeltaBody>();
        index =0;
        current = createDelta(root);//
        processdDeltaBodyQueue();
        return rootDelta;
    }

    private Delta createDelta(ASTNode startASTNode){
        DeltaBody newDeltaBody = new DeltaBody();
        newDeltaBody.startNode = startASTNode;
        newDeltaBody.body = new Stack<ASTNode>();
        deltaBodyQueue.add(newDeltaBody);

        Delta delta = new Delta();
        delta.setBody(newDeltaBody.body);
        delta.setIndex(index++);
        current = delta;

        if(startASTNode == root) rootDelta = current;

        return delta;
    }

    private void processdDeltaBodyQueue(){
        while( ! deltaBodyQueue.isEmpty()){
            DeltaBody deltaBody = deltaBodyQueue.pop();
            buildDeltaBody(deltaBody.startNode, deltaBody.body);
        }
    }

    private void buildDeltaBody(ASTNode node, Stack<ASTNode> body){
        if( node.getType() == ASTNodeType.LAMBDA ){
            Delta delta = createDelta( node.getChild().getSibling() );

            if( node.getChild().getType() == ASTNodeType.COMMA ){
                ASTNode comma = node.getChild();
                ASTNode variableNode = comma.getChild();
                while(variableNode != null){
                    delta.addBoundVars(variableNode.getValue());
                    variableNode = variableNode.getSibling();
                }
            } else delta.addBoundVars(node.getChild().getValue());
            
            body.push(delta);
            return;
        }

        else if(node.getType() == ASTNodeType.CONDITIONAL){
            ASTNode condition = node.getChild();
            ASTNode thenNode = condition.getSibling();
            ASTNode elseNode = thenNode.getSibling();

            Beta newBeta = new Beta();
            buildDeltaBody( thenNode, newBeta.getThenBody());
            buildDeltaBody( elseNode, newBeta.getElseBody());

            body.push(newBeta);
            buildDeltaBody( condition, body);

            return;
        }

        body.push(node);
        ASTNode child = node.getChild();
        while(child != null){
            buildDeltaBody(child, body);
            child = child.getSibling();
        }
    }

    private class DeltaBody{
        Stack<ASTNode> body;
        ASTNode startNode;
    }
    
}
