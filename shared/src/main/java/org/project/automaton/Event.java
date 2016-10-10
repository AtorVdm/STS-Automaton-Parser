package org.project.automaton;

/**
 * Created by Ator on 7/21/16.
 */
public class Event {
    private EventType type;
    private String name;

    public Event(EventType type, String name) {
        this.type = type;
        this.name = name;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum EventType {
        U("U"), C("C");

        private final String eventType;

        EventType(String eventType) {
            this.eventType = eventType;
        }

        @Override
        public String toString() {
            return eventType;
        }
    }
}
