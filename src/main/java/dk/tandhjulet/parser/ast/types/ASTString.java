package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTString extends ASTNode {

    public ASTString(String value) {
        super(ASTType.STR);
        super.node.put("value", value);
    }

    public String getValue() {
        return (String) super.node.get("value");
    }
}
