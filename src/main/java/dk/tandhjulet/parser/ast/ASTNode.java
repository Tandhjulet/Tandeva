package dk.tandhjulet.parser.ast;

import java.util.HashMap;
import java.util.Map;

public abstract class ASTNode {
    protected Map<String, Object> node = new HashMap<>();

    public ASTNode(ASTType type) {
        node.put("type", type);
    }

    public ASTType getType() {
        return (ASTType) node.get("type");
    }
}
