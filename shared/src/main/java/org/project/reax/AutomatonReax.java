package org.project.reax;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ator on 08/09/16.
 */
public class AutomatonReax {
    private List<StateReax> state;
    private List<InputReax> input;
    private List<ControllableReax> controllable;
    private List<TransitionReax> transition;
    private List<InvariantReax> invariants;

    public List<StateReax> getState() {
        if (state == null) {
            state = new ArrayList<>();
        }
        return state;
    }

    public void setState(List<StateReax> state) {
        this.state = state;
    }

    public List<InputReax> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return input;
    }

    public void setInput(List<InputReax> input) {
        this.input = input;
    }

    public List<ControllableReax> getControllable() {
        if (controllable == null) {
            controllable = new ArrayList<>();
        }
        return controllable;
    }

    public void setControllable(List<ControllableReax> controllable) {
        this.controllable = controllable;
    }

    public List<TransitionReax> getTransition() {
        if (transition == null) {
            transition = new ArrayList<>();
        }
        return transition;
    }

    public void setTransition(List<TransitionReax> transition) {
        this.transition = transition;
    }

    public List<InvariantReax> getInvariants() {
        if (invariants == null) {
            invariants = new ArrayList<>();
        }
        return invariants;
    }

    public void setInvariants(List<InvariantReax> invariants) {
        this.invariants = invariants;
    }
}
