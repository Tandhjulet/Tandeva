package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTAssign extends ASTNode {

    public ASTAssign(ASTNode left, ASTNode right) {
        super(ASTType.ASSIGN);
        super.node.put("left", left);
        super.node.put("right", right);
    }

    public ASTNode getLeft() {
        return (ASTNode) super.node.get("left");
    }

    public ASTNode getRight() {
        return (ASTNode) super.node.get("right");
    }

}
