package org.project.parser;

import org.project.automaton.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ator on 7/25/16.
 */
public class Graph {
    private Automaton automaton;
    private Edge[][] graph;
    private List<List<Integer>> incomingEdges;
    private List<String> stateNames;

    public Graph(Automaton automaton) {
        this.automaton = automaton;
        int dim = automaton.getStates().size();
        graph = new Edge[dim][dim];
        incomingEdges = new ArrayList<>();
        stateNames = new ArrayList<>(dim);

        for (int i = 0; i < dim; i++) {
            stateNames.add(automaton.getStates().get(i).getName());
            incomingEdges.add(new ArrayList<>());
        }
        for (State state : automaton.getStates()) {
            int posX = stateNames.indexOf(state.getName());
            for (Edge edge : state.getEdges()) {
                int posY = stateNames.indexOf(edge.getState());
                if (!edge.getState().equals("START") && !state.getName().equals("END")) { // Avoiding END-START loop
                    graph[posX][posY] = edge;
                    if (!incomingEdges.get(posY).contains(posX))
                        incomingEdges.get(posY).add(posX);
                }
            }
        }
    }

    public Automaton computeImplicitVariables() {
        System.out.println(incomingEdges);
        for (int i = 0; i < incomingEdges.size(); i++) {
            for (int j = 0; j < incomingEdges.get(i).size(); j++) {
                int stateEnd = i;
                int stateStart = incomingEdges.get(i).get(j);
                Edge currentEdge = graph[stateStart][stateEnd];
                Set<String> securityConditions = computeSecurityConditions(stateStart);
                StringBuilder sb = new StringBuilder();
                for (String condition : securityConditions) {
                    sb.append("+" + condition);
                }
                String conditions = currentEdge.getCondition() + "+" + sb.toString();
                currentEdge.setSecurityCondition(conditions);
            }
        }
        return automaton;
    }

    private Set<String> computeSecurityConditions(int stateStart) {
        Set<String> secConditions = new HashSet<>();
        List<Integer> currentIncome = incomingEdges.get(stateStart);
        if (currentIncome.size() == 0) {

        } else if (currentIncome.size() == 1) {
            Edge currentEdge = graph[currentIncome.get(0)][stateStart];
            secConditions.add(currentEdge.getCondition());
            secConditions.addAll(computeSecurityConditions(currentIncome.get(0)));
        } else {
            int currentState = currentIncome.get(0);
            boolean reachable = false;
            while (!reachable) {
                reachable = true;
                for (int i = 0; i < currentIncome.size(); i++) {
                    if (!isReachableFrom(currentState, currentIncome.get(i), new ArrayList<>())) {
                        reachable = false;
                    }
                }
                if (reachable == true) {
                    secConditions.addAll(computeSecurityConditions(currentState));
                } else if (incomingEdges.get(currentState).size() != 0) {
                    currentState = incomingEdges.get(currentState).get(0);
                } else {
                    throw new IllegalArgumentException("Reached an end while not resolving the loop problem. Use debug to have more information.");
                }
            }
        }
        return secConditions;
    }

    // Returns true if state2 is reachable from state1 (state1 = TOP, state2 = BOTTOM)
    private boolean isReachableFrom(int state1, int state2, List<Integer> statesLoop) {
        if (state1 == state2) return true;
        for (int state : incomingEdges.get(state2)) {
            if (state == state1) return true;
            if (statesLoop.contains(state)) {
                return false; // loop detected
            } else {
                statesLoop.add(state);
            }
            if (isReachableFrom(state1, state, statesLoop)) return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State names: " + stateNames.toString() + "\n");
        for (int i = 0; i < graph.length; i++) {
            sb.append("[ ");
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j] == null)
                    sb.append("0 ");
                else
                    sb.append("1 ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}
