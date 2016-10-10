package org.project.automaton;

/**
 * Created by Ator on 31/08/16.
 */
public class Expression {
    private String variable;
    private String value;

    public Expression() {}

    public Expression(String variable, String value) {
        this.variable = variable;
        this.value = value;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return variable + "=" + value;
    }
}
