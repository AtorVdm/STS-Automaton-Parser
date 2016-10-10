package org.project.parser;

import org.project.automaton.*;
import se.lnu.prosses.securityMonitor.STSExtractor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ator on 7/21/16.
 */
public class Parser {
    public static final String INPUT_FILE_PATH = "/Users/Ator/Study/ThesisProject/casestudy.txt";
    public static final String OUTPUT_FILE_PATH = "/Users/Ator/Study/ThesisProject/casestudyOutput.txt";

    private static final String CONFID_EXPL_PREFIX = "LCX_";
    private static final String INTEGR_EXPL_PREFIX = "LIX_";
    private static final String CONFID_IMPL_PREFIX = "LCI_";
    private static final String INTEGR_IMPL_PREFIX = "LII_";

    private static Automaton automaton;

    public static void main(String[] args) throws Exception {
        /*
        automaton = new Automaton("", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<String> lines = readData();
        lines.removeAll(Collections.singleton(""));

        if (lines == null) return;

        // Read automaton name
        Pattern pattern = Pattern.compile("automaton" + EXPR + ":");
        Matcher matcher = pattern.matcher(lines.get(0));

        if (matcher.find()) {
            automaton.setName(matcher.group(1));
        }

        int line = 1;

        line = readRepeatableEvents(lines, line);
        line = readRepeatableVariables(lines, line);
        line = readRepeatableStates(lines, line);
        line = readRepeatableBadStates(lines, line);

        System.out.println("Automaton " + automaton.getName() + " was successfully parsed.");
        */
        automaton = getAutomaton();

        Graph graph = new Graph(automaton);
        //System.out.print(graph.toString());
        //graph.computeImplicitVariables();
        System.out.println("Implicit conditions were computed");

        generateSecurityData();
        System.out.println("Security variables were generated");

        new Printer().writeToFile(automaton, OUTPUT_FILE_PATH, new ArrayList<>(), 0);
        System.out.println("Output saved to a file \"" + OUTPUT_FILE_PATH + "\".");
    }

    private static Automaton getAutomaton() throws Exception {
        String directoryPath = "/Users/Ator/Projects/Java/RunningExample/src";
        String[] classPath = new String[]{"/Users/Ator/Study/ThesisProject/apache-tomcat-8.0.1/lib/servlet-api.jar",
                "/Users/Ator/Projects/Java/Automaton-Parser/lib/sts.jar"};
        ArrayList<String> includingFilter = new ArrayList<String>();
        includingFilter.add("se\\.lnu.*");
        ArrayList<String> entryPoints = new ArrayList<>();
        entryPoints.add(".*\\.doGet");
        entryPoints.add(".*\\.doPost");
        ArrayList<String> excludingFilter = new ArrayList<>();
        STSExtractor stsExtractor = new STSExtractor(includingFilter, excludingFilter , entryPoints);
        Set<String> controllableMethodNames = new HashSet<>();
        controllableMethodNames.add("se.lnu.Users.removeUser");
        controllableMethodNames.add("se.lnu.User.getFriendAt");
        controllableMethodNames.add("se.lnu.EstimateLocation.getDistance");
        controllableMethodNames.add("se.lnu.EstimateLocation.estimatLocation");
        controllableMethodNames.add("se.lnu.Users.findUserById");
        controllableMethodNames.add("se.lnu.Users.addUser");
        controllableMethodNames.add("se.lnu.Users.addFriend");
        controllableMethodNames.add("se.lnu.Users.auth");
        stsExtractor.extract(directoryPath, classPath, controllableMethodNames);
        return stsExtractor.convertToAutomaton();
    }

    private static void generateSecurityData() {
        generateSecurityVariables();
        generateSecurityUpdaters();
    }

    private static void generateSecurityVariables() {
        for (Variable variable : automaton.getVariables()) {
            automaton.getSecurityVariables().add(new Variable(Variable.VariableType.INT, CONFID_EXPL_PREFIX + variable.getName()));
        }
        for (Variable variable : automaton.getVariables()) {
            automaton.getSecurityVariables().add(new Variable(Variable.VariableType.INT, INTEGR_EXPL_PREFIX + variable.getName()));
        }
        for (Variable variable : automaton.getVariables()) {
            automaton.getSecurityVariables().add(new Variable(Variable.VariableType.INT, INTEGR_IMPL_PREFIX + variable.getName()));
        }
        for (Variable variable : automaton.getVariables()) {
            automaton.getSecurityVariables().add(new Variable(Variable.VariableType.INT, CONFID_IMPL_PREFIX + variable.getName()));
        }
    }

    private static void generateSecurityUpdaters() {
        for (State state : automaton.getStates()) {
            for (Edge edge : state.getEdges()) {
                List<Expression> updater = new ArrayList<>();
                for (Expression expr : edge.getUpdater()) {
                    updater.add(new Expression(expr.getVariable(), expr.getValue()));
                }
                if (updater.size() == 0) continue;
                // Processing updater
                Pattern pattern = Pattern.compile("[a-zA-Z|_|\\d|()|.]+\\((.*)\\)");
                Matcher matcher;
                for (Expression expr : edge.getUpdater()) {
                    matcher = pattern.matcher(expr.getValue());
                    while (matcher.find()) {
                        String arguments = matcher.group(1);
                        arguments = arguments.replace(",", "+");
                        expr.setValue(expr.getValue().replace(matcher.group(), arguments));
                    }
                    expr.setValue(expr.getValue().replace("(", "").replace(")", "").replace(" ", "").replace("[", "+").replace("]", "+"));
                    // Replacing string variables with 0
                    if (expr.getValue().contains("\"")) {
                        expr.setValue("0");
                    }
                    matcher = Pattern.compile("\\bnull\\b").matcher(expr.getValue());
                    while (matcher.find()) {
                        expr.setValue(expr.getValue().replace(matcher.group(), "0"));
                    }
                }

                List<Expression> securityUpdater = new ArrayList<>();
                for (Expression expr : edge.getUpdater()) {
                    String[] variables = expr.getValue().split("[^\\w|\\s]+");
                    StringBuilder lcxExpr = new StringBuilder();
                    StringBuilder lixExpr = new StringBuilder();

                    List<Expression> lImplicit = calculateIntegrityVariables(expr.getVariable(), edge);

                    for (int i = 0; i < variables.length; i++) {
                        String current = variables[i];
                        if (current.equals("")) continue;
                        if (!automaton.getVariables().stream().anyMatch(variable -> variable.getName().equals(current)) &&
                                !current.matches("\\d+") && !current.matches("true") && !current.matches("false")) {
                            if (automaton.getSecurityVariables().stream().anyMatch(variable -> variable.getName().equals(current))) {
                                if (variables.length == 1)
                                    generateBadState(expr.getVariable(), variables[0], edge.getState());
                                securityUpdater.add(expr);
                                break;
                            }
                            throw new IllegalArgumentException("Variable " + current + " is not declared.");
                        }

                        if (current.matches("\\d+") || current.matches("true") || current.matches("false")) {
                            lcxExpr.append("0+");
                            lixExpr.append("1*");
                        } else {
                            lcxExpr.append(CONFID_EXPL_PREFIX + current + "+");
                            lixExpr.append(INTEGR_EXPL_PREFIX + current + "*");
                        }
                    }

                    if (lcxExpr.length() == 0 || lixExpr.length() == 0) continue;
                    lcxExpr.deleteCharAt(lcxExpr.length() - 1);
                    lixExpr.deleteCharAt(lixExpr.length() - 1);
                    securityUpdater.add(new Expression(CONFID_EXPL_PREFIX + expr.getVariable(), lcxExpr.toString()));
                    securityUpdater.add(new Expression(INTEGR_EXPL_PREFIX + expr.getVariable(), lixExpr.toString()));
                    securityUpdater.addAll(lImplicit);
                }

                for (Expression expr : securityUpdater) {
                    if (edge.getUpdater().contains(expr)) {
                        edge.getUpdater().remove(expr);
                    }
                }

                edge.setSecurityUpdater(securityUpdater);
            }
        }
    }

    private static void generateBadState(String left, String right, String state) {
        if (left.startsWith(CONFID_EXPL_PREFIX) || left.startsWith(CONFID_IMPL_PREFIX)) {
            if (right.equals("0")) {
                automaton.addBadState(new BadState(state, left + ">" + right));
            }
        } else if (left.startsWith(INTEGR_EXPL_PREFIX) || left.startsWith(INTEGR_IMPL_PREFIX)) {
            if (right.equals("1")) {
                automaton.addBadState(new BadState(state, left + "<" + right));
            }
        } else {
            throw new IllegalArgumentException("Unknown security variable detected: " + left);
        }
    }

    private static List<Expression> calculateIntegrityVariables(String current, Edge edge) {
        String conditions = edge.getSecurityCondition();
        if (conditions == null) return new ArrayList<>();
        for (String pattern : getRemovePatterns()) {
            conditions = conditions.replace(pattern, "");
        }
        for (String pattern : getReplacePatterns()) {
            conditions = conditions.replace(pattern, "+");
        }
        StringBuilder lciExpr = new StringBuilder();
        StringBuilder liiExpr = new StringBuilder();
        String[] influentialVariables = conditions.split("\\+");
        for (String infVar : influentialVariables) {
            if (!infVar.equals("") && !infVar.equals("true") && !infVar.equals("false") && !infVar.matches("\\d+")) {
                if (automaton.getVariables().stream().anyMatch(variable -> variable.getName().equals(infVar))) {
                    lciExpr.append(CONFID_IMPL_PREFIX + infVar + "+");
                    liiExpr.append(INTEGR_IMPL_PREFIX + infVar + "*");
                }
                else {
                    throw new IllegalArgumentException("Variable " + infVar + " is not declared.");
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        if (lciExpr.charAt(lciExpr.length() - 1) != '=')
            sb.append(lciExpr.deleteCharAt(lciExpr.length() - 1)).append(", ");
        if (liiExpr.charAt(liiExpr.length() - 1) != '=')
            sb.append(liiExpr.deleteCharAt(liiExpr.length() - 1)).append(", ");

        List<Expression> expressions = new ArrayList<>();
        expressions.add(new Expression(CONFID_IMPL_PREFIX + current, lciExpr.toString()));
        expressions.add(new Expression(INTEGR_IMPL_PREFIX + current, liiExpr.toString()));
        return expressions;
    }

    private static List<String> getRemovePatterns() {
        List<String> patterns = new ArrayList<>();
        patterns.add(" and ");
        patterns.add(" or ");
        patterns.add(")and(");
        patterns.add(")or(");
        patterns.add("not(");
        patterns.add("(");
        patterns.add(")");
        patterns.add(" ");
        return patterns;
    }

    private static List<String> getReplacePatterns() {
        List<String> patterns = new ArrayList<>();
        patterns.add("==");
        patterns.add("!=");
        patterns.add("<=");
        patterns.add(">=");
        patterns.add("<");
        patterns.add(">");
        patterns.add("-");
        patterns.add("*");
        patterns.add("/");
        return patterns;
    }

    private void oldCode() {
        /*
                StringBuilder securityUpdater = new StringBuilder();
                String[] expressions = updater.split(",");
                List<String> securityExpressions = new ArrayList<>();
                // Working with updater to extract variables;
                for (String expression : expressions) {
                    String[] variables = expression.split("\\=\\-|\\*|\\/|\\-|\\+|\\=");
                    StringBuilder lcxExpr = new StringBuilder();
                    StringBuilder lixExpr = new StringBuilder();

                    String lImplicit = "";

                    for (int i = 0; i < variables.length; i++) {
                        String current = variables[i];
                        if (!automaton.getVariables().stream().anyMatch(variable -> variable.getName().equals(current)) && !current.matches("\\d+")) {
                            if (automaton.getSecurityVariables().stream().anyMatch(variable -> variable.getName().equals(current))) {
                                if (variables.length == 2) generateBadState(variables[0], variables[1], edge.getState());
                                securityExpressions.add(expression);
                                break;
                            }
                            throw new IllegalArgumentException("Variable " + current + " is not declared.");
                        }

                        if (i == 0) {
                            lcxExpr.append(CONFID_EXPL_PREFIX + current + "=");
                            lixExpr.append(INTEGR_EXPL_PREFIX + current + "=");
                            lImplicit = calculateIntegrityVariables(current, edge);
                        } else {
                            if (current.matches("\\d+")) {
                                lcxExpr.append("0+");
                                lixExpr.append("1*");
                            } else {
                                lcxExpr.append(CONFID_EXPL_PREFIX + current + "+");
                                lixExpr.append(INTEGR_EXPL_PREFIX + current + "*");
                            }
                        }
                    }
                    if (lcxExpr.length() == 0 || lixExpr.length() == 0) continue;
                    lcxExpr.deleteCharAt(lcxExpr.length() - 1);
                    lixExpr.deleteCharAt(lixExpr.length() - 1);
                    securityUpdater.append(lcxExpr).append(", ").append(lixExpr).append(", ").append(lImplicit);
                }
                if (securityUpdater.length() == 0) continue;
                securityUpdater.delete(securityUpdater.length() - 2, securityUpdater.length());
                edge.setSecurityUpdater(securityUpdater.toString());
                for (String expression : securityExpressions) {
                    if (edge.getSecurityUpdater().contains(expression.substring(0, expression.indexOf("=") + 1))) {
                        edge.setUpdater(edge.getUpdater().replace(expression + ", ", ""));
                        edge.setUpdater(edge.getUpdater().replace(", " + expression, ""));
                        edge.setUpdater(edge.getUpdater().replace(expression, ""));
                    }
                }*/
    }
}
