package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTVariable extends ASTNode {

    public ASTVariable(String value) {
        super(ASTType.VAR);
        super.node.put("value", value);
    }

    public String getValue() {
        return (String) super.node.get("value");
    }

}
