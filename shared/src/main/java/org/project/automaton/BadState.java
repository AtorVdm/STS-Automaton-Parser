package org.project.automaton;

/**
 * Created by Ator on 7/29/16.
 */
public class BadState {
    private String name;
    private String condition;

    public BadState(String name, String condition) {
        this.name = name;
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
