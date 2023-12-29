package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTProg extends ASTNode {

    public ASTProg(ASTNode[] ast) {
        super(ASTType.PROG);
        super.node.put("prog", ast);
    }

    public ASTNode[] getProg() {
        return (ASTNode[]) super.node.get("prog");
    }
}
