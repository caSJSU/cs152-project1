package edu.sjsu.fwjs;

import java.util.Map;
import java.util.HashMap;

public class Environment {
    private Map<String,Value> env = new HashMap<String,Value>();
    private Environment outerEnv;

    /**
     * Constructor for global environment
     */
    public Environment() {}

    /**
     * Constructor for local environment of a function
     */
    public Environment(Environment outerEnv) {
        this.outerEnv = outerEnv;
    }

    /**
     * Handles the logic of resolving a variable.
     * If the variable name is in the current scope, it is returned.
     * Otherwise, search for the variable in the outer scope.
     * If we are at the outermost scope (AKA the global scope)
     * null is returned (similar to how JS returns undefined.
     */
    public Value resolveVar(String varName) {
        // YOUR CODE HERE
        if (this.env.containsKey(varName)) {
            return this.env.get(varName);
        } else if (outerEnv != null) { //non-global scope
            outerEnv.resolveVar(varName);
        } else if(outerEnv == null) { //we're in the global environment 
            System.out.println("return null val 1");
            return new NullVal();
        }
        System.out.println("return null val 2");
        return new NullVal();
    }

    /**
     * Used for updating existing variables.
     * If a variable has not been defined previously in the current scope,
     * or any of the function's outer scopes, the var is stored in the global scope.
     */
    public void updateVar(String key, Value v) {
        // YOUR CODE HERE
        if (env.containsKey(key)) {
            env.put(key, v); //replace with updated key/value pair
        } else if (outerEnv != null) { //if we're in a non-global environment

//          if(outerEnv.env.containsKey(key)) {
//          outerEnv.env.put(key, v);
//      }
            System.out.println("outer env calls update var");
            outerEnv.updateVar(key, v); 
        } else if(outerEnv == null) { //we're in the global environment (outermost)
            this.createVar(key, v);
        }  
    }

    /**
     * Creates a new variable in the local scope.
     * If the variable has been defined in the current scope previously,
     * a RuntimeException is thrown.
     */
    public void createVar(String key, Value v) {
        // YOUR CODE HERE
        if (env.containsKey(key)) {
            throw new RuntimeException();
        } else {
            env.put(key, v);
        }
    }
}
