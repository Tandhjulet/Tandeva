package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTBinary extends ASTNode {

    public ASTBinary(String op, ASTNode left, ASTNode right) {
        // should probably use enum instead of string for operators.
        // can't be bothered rn tho
        super(ASTType.BINARY);
        super.node.put("operator", op);
        super.node.put("left", left);
        super.node.put("right", right);
    }

    public ASTNode getLeft() {
        return (ASTNode) super.node.get("left");
    }

    public ASTNode getRight() {
        return (ASTNode) super.node.get("right");
    }

    public String getOperator() {
        return (String) super.node.get("operator");
    }

}
