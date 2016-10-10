package org.project.automaton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ator on 7/21/16.
 */
public class Automaton {
    private String name;
    private List<Event> events;
    private List<Variable> variables;
    private List<State> states;
    private List<BadState> badStates;
    private List<Variable> securityVariables;

    public Automaton(String name, List<Event> events, List<Variable> variables, List<State> states) {
        this.name = name;
        this.events = events;
        this.variables = variables;
        this.states = states;
        securityVariables = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<BadState> getBadStates() {
        return badStates;
    }

    public void setBadStates(List<BadState> badStates) {
        this.badStates = badStates;
    }

    public void addBadState(BadState badState) {
        this.badStates.add(badState);
    }

    public List<Variable> getSecurityVariables() {
        return securityVariables;
    }

    public void setSecurityVariables(List<Variable> securityVariables) {
        this.securityVariables = securityVariables;
    }
}
