package dk.tandhjulet.environment;

import java.util.Collections;
import java.util.HashMap;

public class Environment {
    private final Environment parent;
    protected final HashMap<String, Object> vars;

    public Environment(Environment parent) {
        this.parent = parent;
        this.vars = new HashMap<>(parent != null ? parent.vars : Collections.emptyMap());
    }

    public Environment lookupVariable(String varName) {
        Environment scope = this;
        while (scope != null) {
            if (scope.vars.containsKey(varName))
                return scope;
            scope = scope.parent;
        }
        return null;
    }

    public Environment extend() {
        return new Environment(this);
    }

    public Object get(String varName) {
        if (vars.containsKey(varName))
            return vars.get(varName);
        throw new EnvironmentException("Undefined variable: " + varName);
    }

    public Object set(String varName, Object value) {
        Environment scope = lookupVariable(varName);
        if (scope != null && this.parent != null)
            throw new EnvironmentException("Cannot define global variable inside a scope");
        return (scope != null ? scope : this).vars.put(varName, value);
    }

    public Object define(String varName, Object value) {
        return this.vars.put(varName, value);
    }
}
