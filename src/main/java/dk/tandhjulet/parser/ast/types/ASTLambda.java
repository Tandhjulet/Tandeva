package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTLambda extends ASTNode {

    public ASTLambda(ASTNode body, String... vars) {
        super(ASTType.LAMBDA);
        super.node.put("AST", body);
        super.node.put("vars", vars);
    }

    public ASTNode getBody() {
        return (ASTNode) super.node.get("AST");
    }

    public String[] getVars() {
        return (String[]) super.node.get("vars");
    }
}
