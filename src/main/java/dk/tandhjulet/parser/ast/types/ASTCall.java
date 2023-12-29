package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTCall extends ASTNode {

    public ASTCall(ASTNode func, ASTNode[] args) {
        super(ASTType.CALL);
        super.node.put("func", func);
        super.node.put("args", args);
    }

    public ASTNode getFunc() {
        return (ASTNode) super.node.get("func");
    }

    public ASTNode[] getArgs() {
        return (ASTNode[]) super.node.get("args");
    }

}
