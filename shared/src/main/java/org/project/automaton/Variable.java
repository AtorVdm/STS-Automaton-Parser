package org.project.automaton;

/**
 * Created by Ator on 7/21/16.
 */
public class Variable {
    private VariableType type;
    private String name;

    public Variable(VariableType type, String name) {
        this.type = type;
        this.name = name;
    }

    public VariableType getType() {
        return type;
    }

    public void setType(VariableType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum VariableType {
        INT("int"), FLOAT("float"), BOOLEAN("boolean"), UNDEFINED("undefined");

        private final String variableType;

        VariableType(String variableType) {
            this.variableType = variableType;
        }

        @Override
        public String toString() {
            return variableType;
        }
    }
}
