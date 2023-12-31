package dk.tandhjulet.environment;

import java.math.BigDecimal;
import java.util.function.Function;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.ASTType;
import dk.tandhjulet.parser.ast.types.ASTAssign;
import dk.tandhjulet.parser.ast.types.ASTBinary;
import dk.tandhjulet.parser.ast.types.ASTBoolean;
import dk.tandhjulet.parser.ast.types.ASTCall;
import dk.tandhjulet.parser.ast.types.ASTIf;
import dk.tandhjulet.parser.ast.types.ASTLambda;
import dk.tandhjulet.parser.ast.types.ASTNumber;
import dk.tandhjulet.parser.ast.types.ASTProg;
import dk.tandhjulet.parser.ast.types.ASTString;
import dk.tandhjulet.parser.ast.types.ASTVariable;

public class Evaluator {
    public static Object evaluate(ASTNode node, Environment env) {
        switch (node.getType()) {
            case NUM:
                return ((ASTNumber) node).getValue();
            case STR:
                return ((ASTString) node).getValue();
            case BOOL:
                return ((ASTBoolean) node).getValue();
            case VAR:
                return env.get(((ASTVariable) node).getValue());
            case ASSIGN:
                ASTNode left = ((ASTAssign) node).getLeft();
                if (left.getType() != ASTType.VAR)
                    throw new EnvironmentException("Cannot assign to non-variable: " + left.toString());

                ASTNode right = ((ASTAssign) node).getRight();
                return env.set(((ASTVariable) left).getValue(), evaluate(right, env));
            case BINARY:
                ASTBinary binaryNode = (ASTBinary) node;

                return applyOperator(binaryNode.getOperator(), (Number) evaluate(binaryNode.getLeft(), env),
                        (Number) evaluate(binaryNode.getRight(), env));
            case LAMBDA:
                return makeLambda(env, (ASTLambda) node);
            case IF:
                ASTIf condNode = (ASTIf) node;
                Boolean cond = (boolean) evaluate(condNode.getCond(), env);
                if (cond != false)
                    return evaluate(condNode.getThen(), env);
                return condNode.getElse() != null ? evaluate(condNode.getElse(), env) : false;
            case PROG:
                Object val = null;
                ASTProg prog = (ASTProg) node;

                for (ASTNode pNode : prog.getProg()) {
                    val = evaluate(pNode, env);
                }
                return val != null;
            case CALL:
                ASTCall call = (ASTCall) node;
                @SuppressWarnings("unchecked")
                Function<Object[], Object> func = (Function<Object[], Object>) evaluate(call.getFunc(), env);

                Object[] evaluatedArgs = new Object[call.getArgs().length];
                for (int i = 0; i < call.getArgs().length; i++) {
                    evaluatedArgs[i] = evaluate(call.getArgs()[i], env);
                }

                return func.apply(evaluatedArgs);
            default:
                throw new EnvironmentException("Cannot evaluate " + node.getType());
        }
    }

    private static Object applyOperator(String op, Object left, Object right) {
        Function<Object, BigDecimal> num = (n) -> {
            return new BigDecimal(n.toString());
        };

        switch (op) {
            case "+":
                return num.apply(left).add(num.apply(right));
            case "-":
                return num.apply(left).subtract(num.apply(right));
            case "*":
                return num.apply(left).multiply(num.apply(right));
            case "/":
                return num.apply(left).divide(num.apply(right));
            case "%":
                return num.apply(left).remainder(num.apply(right));
            case "&&":
                return ((boolean) left) != false && (boolean) right;
            case "||":
                return ((boolean) left) != false ? (boolean) left : (boolean) right;
            case "<":
                return num.apply(left).compareTo(num.apply(right)) < 0;
            case ">":
                return num.apply(left).compareTo(num.apply(right)) > 0;
            case "<=":
                return num.apply(left).compareTo(num.apply(right)) <= 0;
            case ">=":
                return num.apply(left).compareTo(num.apply(right)) >= 0;
            case "==":
                return num.apply(left).compareTo(num.apply(right)) == 0;
            case "!=":
                return num.apply(left).compareTo(num.apply(right)) != 0;
        }
        throw new EnvironmentException("Cannot execute operator " + op);
    }

    private static Function<Object[], Object> makeLambda(Environment env, ASTLambda node) {
        Function<Object[], Object> out = (args) -> {
            String[] vars = node.getVars();
            Environment scope = env.extend();

            for (int i = 0; i < vars.length; ++i) {
                scope.define(vars[i], i < args.length ? args[i] : false);
            }

            return evaluate(node.getBody(), scope);
        };
        return out;
    }
}
