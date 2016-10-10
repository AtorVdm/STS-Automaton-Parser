package org.project.parser;

import org.project.automaton.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ator on 08/09/16.
 */
public class SmacsReader {
    public static final String EXPR = "\\s*(.*?)\\s*";

    private Automaton automaton;

    public SmacsReader(Automaton automaton) {
        this.automaton = automaton;
    }

    public Automaton getAutomaton() {
        return automaton;
    }

    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
    }

    private List<String> readData() {
        try {
            return Files.readAllLines(Paths.get(Parser.INPUT_FILE_PATH));
        } catch (IOException e) {
            System.err.println("Error reading a file: " + e.getMessage());
        }
        return null;
    }

    private int readRepeatableEvents(List<String> lines, int line) {
        Pattern pattern = Pattern.compile("events\\s*:");
        Matcher matcher = pattern.matcher(lines.get(1));
        line++;
        if (matcher.find()) {
            pattern = Pattern.compile("(C|U)" + EXPR + ";");
            matcher = pattern.matcher(lines.get(line));
            while (matcher.find()) {
                Event.EventType type;
                switch (matcher.group(1))
                {
                    case "C": type = Event.EventType.C; break;
                    case "U": type = Event.EventType.U; break;
                    default: throw new IllegalArgumentException("Only event types C and U supported.");
                }
                automaton.getEvents().add(new Event(type, matcher.group(2)));
                line++;
                matcher = pattern.matcher(lines.get(line));
            }
        }
        return line;
    }

    private int readRepeatableVariables(List<String> lines, int line) {
        Pattern pattern = Pattern.compile("(int|float)" + EXPR + ";");
        Matcher matcher = pattern.matcher(lines.get(line));

        while (matcher.find()) {
            Variable.VariableType type;
            switch (matcher.group(1))
            {
                case "int": type = Variable.VariableType.INT; break;
                case "float": type = Variable.VariableType.FLOAT; break;
                default: throw new IllegalArgumentException("Only variable types int and float supported.");
            }
            automaton.getVariables().add(new Variable(type, matcher.group(2)));
            line++;
            matcher = pattern.matcher(lines.get(line));
        }
        return line;
    }

    private int readRepeatableStates(List<String> lines, int line) {
        Pattern pattern;
        Matcher matcher;
        List<String> extractedStates = new ArrayList<>();
        final String EDGE_PATTERN = String.format("\\s*to" + EXPR + ":\\s*when" + EXPR + "," + EXPR + "(with(\\s*[^;]*\\s*=\\s*[^;]*\\s*)+)?;\\s*");
        while (!lines.get(line).matches("\\s*bad_states\\s*:\\s*")) {
            pattern = Pattern.compile("\\s*state\\s*(.*?)\\s*:.*");
            matcher = pattern.matcher(lines.get(line));
            if (matcher.matches()) {
                StringBuilder sb = new StringBuilder();
                sb.append(matcher.group());
                line++;
                pattern = Pattern.compile(EDGE_PATTERN);
                matcher = pattern.matcher(lines.get(line));
                while (matcher.matches()) {
                    sb.append(" " + matcher.group());
                    line++;
                    matcher = pattern.matcher(lines.get(line));
                }
                extractedStates.add(sb.toString());
            } else {
                line++;
            }
        }
        for (String extractedState : extractedStates) {
            State state;
            pattern = Pattern.compile("\\s*state\\s*(.*?)\\s*:");
            matcher = pattern.matcher(extractedState);
            if (matcher.find()) {
                state = new State(matcher.group(1), new ArrayList<>());
            } else {
                throw new IllegalArgumentException("State name wasn't found at line " + line + ".");
            }

            pattern = Pattern.compile(EDGE_PATTERN);
            matcher = pattern.matcher(extractedState);
            int position = 0;
            while (matcher.find(position)) {
                String edgeState = matcher.group(1);
                String condition = matcher.group(2);
                String event = matcher.group(3);
                String updater = matcher.group(5) != "-1"? matcher.group(5): "";
                //state.getEdges().add(new Edge(edgeState, condition, event, updater)); TODO: remake if you want to use it
                position = matcher.end();
            }
            automaton.getStates().add(state);
        }
        return line;
    }

    private int readRepeatableBadStates(List<String> lines, int line) {
        Pattern pattern = Pattern.compile("\\s*bad_states\\s*:\\s*");
        Matcher matcher = pattern.matcher(lines.get(line));
        List<BadState> badStates = new ArrayList<>();
        line++;
        if (matcher.find()) {
            while (!lines.get(line).matches("\\s*controller\\s*\\d+\\s*mask\\s*:\\s*")) {
                pattern = Pattern.compile("\\s*state\\s*(.*?)\\s*:\\s*(.*?)\\s*");
                matcher = pattern.matcher(lines.get(line));
                line++;
                if (matcher.matches()) {
                    BadState badState = new BadState(matcher.group(1), matcher.group(2));
                    badStates.add(badState);
                }
            }
        }
        automaton.setBadStates(badStates);
        return line;
    }
}
