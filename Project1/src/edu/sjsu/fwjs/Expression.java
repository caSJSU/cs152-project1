package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;

/**
 * FWJS expressions.
 */
public interface Expression {
    /**
     * Evaluate the expression in the context of the specified environment.
     */
    public Value evaluate(Environment env);
}

// NOTE: Using package access so that all implementations of Expression
// can be included in the same file.

/**
 * FWJS constants.
 */
class ValueExpr implements Expression {
    private Value val;
    public ValueExpr(Value v) {
        this.val = v;
    }
    public Value evaluate(Environment env) {
        return this.val;
    }
}

/**
 * Expressions that are a FWJS variable.
 */
class VarExpr implements Expression {
    private String varName;
    public VarExpr(String varName) {
        this.varName = varName;
    }
    public Value evaluate(Environment env) {
        return env.resolveVar(varName);
    }
}

/**
 * A print expression.
 */
class PrintExpr implements Expression {
    private Expression exp;
    public PrintExpr(Expression exp) {
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        Value v = exp.evaluate(env);
        System.out.println(v.toString());
        return v;
    }
}
/**
 * Binary operators (+, -, *, etc).
 * Currently only numbers are supported.
 */
class BinOpExpr implements Expression {
    private Op op;
    private Expression e1;
    private Expression e2;
    public BinOpExpr(Op op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @SuppressWarnings("incomplete-switch")
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        IntVal i1 = (IntVal) e1.evaluate(env);
        IntVal i2 = (IntVal) e2.evaluate(env);
        
        int intVal1 = i1.toInt();
        int intVal2 = i2.toInt();

        if (op.equals(Op.ADD)) {
            return new IntVal(intVal1 + intVal2);
        } else if (op.equals(Op.DIVIDE)) {
            return new IntVal(intVal1 / intVal2);
        } else if (op.equals(Op.EQ)) {
            int cmp = (intVal1 == intVal2) ? 1:0;
            return new IntVal(cmp);
        } else if (op.equals(Op.GE)) {
            int cmp = (intVal1 >= intVal2) ? 1:0;
            return new IntVal(cmp);
        } else if (op.equals(Op.GT)) {
            int cmp = (intVal1 > intVal2) ? 1:0;
            return new IntVal(cmp);
        } else if (op.equals(Op.LE)) {
            int cmp = (intVal1 <= intVal2) ? 1:0;
            return new IntVal(cmp);
        } else if (op.equals(Op.LT)) {
            int cmp = (intVal1 < intVal2) ? 1:0;
            return new IntVal(cmp);
        } else if (op.equals(Op.MOD)) {
            return new IntVal(intVal1 % intVal2);
        } else if (op.equals(Op.MULTIPLY)) {
            return new IntVal(intVal1 * intVal2);
        } else if (op.equals(Op.SUBTRACT)) {
            return new IntVal(intVal1 - intVal2);
        }

        return null;
    }
}

/**
 * If-then-else expressions.
 * Unlike JS, if expressions return a value.
 */
class IfExpr implements Expression {
    private Expression cond;
    private Expression thn;
    private Expression els;
    public IfExpr(Expression cond, Expression thn, Expression els) {
        this.cond = cond;
        this.thn = thn;
        this.els = els;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        BoolVal condition = (BoolVal) cond.evaluate(env);
        if (condition.toBoolean()) {
            return thn.evaluate(env);
        } else {
            return els.evaluate(env);
        }
//        return null;
    }
}

/**
 * While statements (treated as expressions in FWJS, unlike JS).
 */
class WhileExpr implements Expression {
    private Expression cond;
    private Expression body;
    public WhileExpr(Expression cond, Expression body) {
        this.cond = cond;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        IntVal condition = (IntVal) cond.evaluate(env);
        IntVal fals = new IntVal(0);

        while (!condition.equals(fals)) {
            body.evaluate(env); 
            condition = (IntVal) cond.evaluate(env);
        }
        
        return null;
    }
}

/**
 * Sequence expressions (i.e. 2 back-to-back expressions).
 */
class SeqExpr implements Expression {
    private Expression e1;
    private Expression e2;
    public SeqExpr(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        Value v1 = e1.evaluate(env);
        Value v2 = e2.evaluate(env);
        if(!v1.equals(new NullVal()) && v2.equals(new NullVal())) {
            System.out.println("v1 returned");
            return v1;
        } else if (!v2.equals(new NullVal()))  {
            System.out.println("v2 returned");
            return v2;
        } else {
            System.out.println("elseeee");
            return new NullVal();
        }
    }
}

/**
 * Declaring a variable in the local scope.
 */
class VarDeclExpr implements Expression {
    private String varName;
    private Expression exp;
    public VarDeclExpr(String varName, Expression exp) {
        this.varName = varName;
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        Value v = exp.evaluate(env);
        env.createVar(varName, v);
        return v;
    }
}

/**
 * Updating an existing variable.
 * If the variable is not set already, it is added
 * to the global scope.
 */
class AssignExpr implements Expression {
    private String varName;
    private Expression e;
    public AssignExpr(String varName, Expression e) {
        this.varName = varName;
        this.e = e;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        env.updateVar(varName, e.evaluate(env));
        return e.evaluate(env);
    }
}

/**
 * A function declaration, which evaluates to a closure.
 */
class FunctionDeclExpr implements Expression {
    private List<String> params;
    private Expression body;
    public FunctionDeclExpr(List<String> params, Expression body) {
        this.params = params;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        //set up the closure so it will return environment ready for FuncAppExpr
        ClosureVal val = new ClosureVal(params, body, env) {};
        env.createVar(val.toString(), val);
//        return new ClosureVal(params, body, env);
        return val;
    }
}

/**
 * Function application.
 */
class FunctionAppExpr implements Expression {
    private Expression f;
    private List<Expression> args;
    public FunctionAppExpr(Expression f, List<Expression> args) {
        this.f = f; //FunctionAppExpr takes an expression (which should evaluate to a closure)
        this.args = args; //and a list of arguments.
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        
        ClosureVal cv = (ClosureVal) f.evaluate(env);
        List<Value> argsEval = new ArrayList<Value>();
        for(Expression e : args) {
            argsEval.add(e.evaluate(env));
        }
        return cv.apply(argsEval);
    }
}

