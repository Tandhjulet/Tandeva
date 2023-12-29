package dk.tandhjulet.parser.tokenizer;

import java.util.HashMap;
import java.util.Map;

public class TokenNode<T> {
    private final Class<T> clazz;
    Map<String, Object> node = new HashMap<>();

    public TokenNode(Enum<TokenType> type, Object value, Class<T> clazz) {
        this.clazz = clazz;

        node.put("type", type);
        node.put("value", type);
    }

    @Override
    public String toString() {
        return "{type:" + getType().toString() + ",value:" + getValue().toString() + "}";
    }

    // assumes the generic type from the value
    @SuppressWarnings("unchecked")
    public TokenNode(Enum<TokenType> type, Object value) {
        this.clazz = (Class<T>) value.getClass();

        node.put("type", type);
        node.put("value", value);
    }

    public TokenType getType() {
        return (TokenType) node.get("type");
    }

    public T getValue() {
        Object value = node.get("value");
        return clazz.cast(value);
    }

    public <E> E getValue(Class<E> clazz) {
        try {
            Object value = node.get("value");
            return clazz.cast(value);
        } catch (ClassCastException e) {
            System.out.println("getvalue called with invalid class. failing silently.");
            return null;
        }
    }
}
