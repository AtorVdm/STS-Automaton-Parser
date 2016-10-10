package org.project.parser;

import org.project.automaton.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Ator on 08/09/16.
 */
public class Printer {
    public void writeToFile(Automaton automaton, String filePath, List<String> lines, int line) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(filePath, "UTF-8");
        } catch (FileNotFoundException e) {
            System.err.println("Error writing to a file: " + e.getMessage());
            return;
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error writing a file: " + e.getMessage());
            return;
        }
        writer.println("automaton " + automaton.getName() + ":");
        writer.println("events:");
        for (Event event : automaton.getEvents()) {
            writer.println(event.getType() + " " + event.getName() + ";");
        }
        writer.println();
        for (Variable variable : automaton.getVariables()) {
            writer.println(variable.getType() + " " + variable.getName() + ";");
        }
        writer.println();
        for (Variable variable : automaton.getSecurityVariables()) {
            writer.println(variable.getType() + " " + variable.getName() + ";");
        }
        writer.println();
        writer.println("initial : START");
        writer.println();
        for (State state : automaton.getStates()) {
            writer.println("state " + state.getName() + " :");
            for (Edge edge : state.getEdges()) {
                writer.print("\tto " + edge.getState() + " : when " + edge.getCondition() + ", " + edge.getEvent());
                if (edge.getUpdater() != null) {
                    writer.print(" with " + edge.getUpdater());
                }
                if (edge.getSecurityUpdater() != null) {
                    writer.print(", " + edge.getSecurityUpdater());
                }
                writer.println(";");
            }
        }
        writer.println();
        writer.println("bad_states:");
        if (automaton.getBadStates() != null)
            for (BadState state : automaton.getBadStates()) {
                writer.println("state " + state.getName() + " : " + state.getCondition());
            }
        writer.println();
        for (int i = line; i < lines.size(); i++) {
            writer.println(lines.get(i));
        }
        writer.close();
    }
}
