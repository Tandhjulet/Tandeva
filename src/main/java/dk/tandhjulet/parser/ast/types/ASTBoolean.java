package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTBoolean extends ASTNode {

    public ASTBoolean(Boolean value) {
        super(ASTType.BOOL);
        super.node.put("value", value);
    }

    public boolean getValue() {
        return (Boolean) super.node.get("value");
    }
}
