package org.project.automaton;

import java.util.List;

/**
 * Created by Ator on 7/22/16.
 */
public class Edge {
    private String state;
    private String condition;
    private String event;
    private List<Expression> updater;
    private List<Expression> securityUpdater;
    private String securityCondition;

    public Edge(String state, String condition, String event, List<Expression> updater) {
        this.state = state;
        this.condition = condition;
        this.event = event;
        this.updater = updater;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<Expression> getUpdater() {
        return updater;
    }

    public void setUpdater(List<Expression> updater) {
        this.updater = updater;
    }

    public List<Expression> getSecurityUpdater() {
        return securityUpdater;
    }

    public void setSecurityUpdater(List<Expression> securityUpdater) {
        this.securityUpdater = securityUpdater;
    }

    public String getSecurityCondition() {
        return securityCondition;
    }

    public void setSecurityCondition(String securityCondition) {
        this.securityCondition = securityCondition;
    }
}
