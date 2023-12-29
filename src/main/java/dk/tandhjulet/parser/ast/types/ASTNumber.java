package dk.tandhjulet.parser.ast.types;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;

public class ASTNumber extends ASTNode {

    public ASTNumber(Number value) {
        super(ASTType.NUM);
        super.node.put("value", value);
    }

    public Number getValue() {
        return (Number) node.get("value");
    }
}
