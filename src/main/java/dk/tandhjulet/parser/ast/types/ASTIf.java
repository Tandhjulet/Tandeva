package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTIf extends ASTNode {

    public ASTIf(ASTNode cond, ASTNode then, ASTNode otherwise) {
        super(ASTType.IF);
        super.node.put("cond", cond);
        super.node.put("then", then);
        super.node.put("else", otherwise);
    }

    public ASTIf(ASTNode cond, ASTNode then) {
        super(ASTType.IF);
        super.node.put("cond", cond);
        super.node.put("then", then);
    }

    public ASTNode getCond() {
        return (ASTNode) super.node.get("cond");
    }

    public ASTNode getThen() {
        return (ASTNode) super.node.get("then");
    }

    public ASTNode getElse() {
        return (ASTNode) super.node.get("otherwise");
    }
}
