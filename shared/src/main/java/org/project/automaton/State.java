package org.project.automaton;

import java.util.List;

/**
 * Created by Ator on 7/21/16.
 */
public class State {
    private String name;
    private List<Edge> edges;

    public State(String name, List<Edge> edges) {
        this.name = name;
        this.edges = edges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
