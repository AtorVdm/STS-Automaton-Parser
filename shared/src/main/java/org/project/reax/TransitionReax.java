package org.project.reax;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ator on 08/09/16.
 */
public class TransitionReax {
    private StateReax variable;
    private List<Value> values;

    public StateReax getVariable() {
        return variable;
    }

    public void setVariable(StateReax variable) {
        this.variable = variable;
    }

    public List<Value> getValues() {
        if (values == null) {
            values = new ArrayList<>();
        }
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public class Value {
        private String condition;
        private EventReax event;
        private int value;

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public EventReax getEvent() {
            return event;
        }

        public void setEvent(EventReax event) {
            this.event = event;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
