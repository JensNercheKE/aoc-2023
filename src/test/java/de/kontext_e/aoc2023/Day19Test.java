package de.kontext_e.aoc2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day19Test {
    List<String> workflows = new ArrayList<>();
    List<String> parts = new ArrayList<>();
    List<String> accepted = new ArrayList<>();
    private final List<Workflow> wfs = new LinkedList<>();
    long overallCombinations = 0;
    @BeforeEach
    void setUp() {
        workflows.clear();
        parts.clear();
        accepted.clear();
        wfs.clear();
        overallCombinations = 0;
    }

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day19test.txt");
        var lines = Files.readAllLines(path);
        sortRulesAndParts(lines);
        System.out.println(workflows.size() + " rules, "+parts.size()+" parts");

        for (String part : parts) {
            processPart(part);
        }

        long sum = 0L;
        for (String part : accepted) {
            var ratings = part.substring(1, part.length() - 1).split(",");
            for (String rating : ratings) {
                var keyValue = rating.split("=");
                sum += Long.parseLong(keyValue[1]);
            }
        }

        assertEquals(19114, sum);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day19input.txt");
        var lines = Files.readAllLines(path);
        sortRulesAndParts(lines);
        System.out.println(workflows.size() + " rules, "+parts.size()+" parts");

        for (String part : parts) {
            processPart(part);
        }
        System.out.println(accepted.size()+" accepted parts");

        long sum = 0L;
        for (String part : accepted) {
            var ratings = part.substring(1, part.length() - 1).split(",");
            for (String rating : ratings) {
                var keyValue = rating.split("=");
                sum += Long.parseLong(keyValue[1]);
            }
        }

        assertEquals(325952, sum);
    }

    @Test
    void part2() throws IOException {
        Path path = Paths.get("src/test/resources/day19test.txt");
        var lines = Files.readAllLines(path);
        sortRulesAndParts(lines);
        System.out.println(workflows.size() + " rules, "+parts.size()+" parts");
        toWorkflows();

        var in = select("in");
        var rating = new Rating();
        in.filter(rating);

        assertEquals(167409079868000L, overallCombinations);
    }

    @Test
    void part2input() throws IOException {
        Path path = Paths.get("src/test/resources/day19input.txt");
        var lines = Files.readAllLines(path);
        sortRulesAndParts(lines);
        System.out.println(workflows.size() + " rules, "+parts.size()+" parts");
        toWorkflows();

        var in = select("in");
        var rating = new Rating();
        in.filter(rating);

        assertEquals(125744206494820L, overallCombinations);
    }

    private class Rating {
        int minX = 1;
        int maxX = 4000;
        int minM = 1;
        int maxM = 4000;
        int minA = 1;
        int maxA = 4000;
        int minS = 1;
        int maxS = 4000;

        public Rating() {}

        public Rating(int minX, int maxX, int minM, int maxM, int minA, int maxA, int minS, int maxS) {
            this.minX = minX;
            this.maxX = maxX;
            this.minM = minM;
            this.maxM = maxM;
            this.minA = minA;
            this.maxA = maxA;
            this.minS = minS;
            this.maxS = maxS;
        }

        public Rating copy() {
            return new Rating(minX, maxX, minM, maxM, minA, maxA, minS, maxS);
        }

        long combinations() {
            return (long) (maxX + 1L - minX)
                    * (long) (maxM + 1L - minM)
                    * (long) (maxA + 1L - minA)
                    * (long) (maxS + 1L - minS)
                    ;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Rating.class.getSimpleName() + "[", "]")
                    .add("minX=" + minX)
                    .add("maxX=" + maxX)
                    .add("minM=" + minM)
                    .add("maxM=" + maxM)
                    .add("minA=" + minA)
                    .add("maxA=" + maxA)
                    .add("minS=" + minS)
                    .add("maxS=" + maxS)
                    .add("combinations=" + combinations())
                    .toString();
        }

        public void lowerMaximum(String left, long max) {
            int tmp = (int) max;
            if ("X".equalsIgnoreCase(left)) {
                maxX = tmp;
            }
            if ("M".equalsIgnoreCase(left)) {
                maxM = tmp;
            }
            if ("A".equalsIgnoreCase(left)) {
                maxA = tmp;
            }
            if ("S".equalsIgnoreCase(left)) {
                maxS = tmp;
            }
        }

        public void raiseMin(String left, long min) {
            int tmp = (int) min;
            if ("X".equalsIgnoreCase(left)) {
                minX = tmp;
            }
            if ("M".equalsIgnoreCase(left)) {
                minM = tmp;
            }
            if ("A".equalsIgnoreCase(left)) {
                minA = tmp;
            }
            if ("S".equalsIgnoreCase(left)) {
                minS = tmp;
            }
        }
    }

    private class Workflow {
        String name;
        List<Rule> rules = new ArrayList<>();

        public Workflow(String string) {
            name = string.substring(0, string.indexOf("{"));
            var rulePart = string.substring(string.indexOf("{")+1, string.indexOf("}"));
            var splitted = rulePart.split(",");
            for (String split : splitted) {
                rules.add(new Rule(split));
            }
        }

        public void filter(Rating rating) {
            var notApplyingCopy = rating.copy();
            for (Rule rule : rules) {
                var applyingCopy = notApplyingCopy.copy();

                rule.adaptApplying(applyingCopy);
                rule.adaptNotApplying(notApplyingCopy);

                if (rule.isTerminal() == false) {
                    var next = select(rule.next);
                    next.filter(applyingCopy);
                } else {
                    if (rule.isAccepting() == true) {
                        if(rule.followAlways()) {
                            overallCombinations += notApplyingCopy.combinations();
                        } else {
                            overallCombinations += applyingCopy.combinations();
                        }
                    }
                }
            }
        }

        @Override
        public String toString() {
            return name+"{"+rules+"}";
        }
    }
    private class Rule {

        Expression condition;
        String next;

        public Rule(String split) {
            if (split.contains(":")) {
                var conditionAndNext = split.split(":");
                condition = asExpression(conditionAndNext[0]);
                next = conditionAndNext[1];
            } else {
                condition = new TrueExpression();
                next = split;
            }
        }

        public void adaptApplying(Rating rating) {
            condition.adaptRating(rating);
        }

        public void adaptNotApplying(Rating rating) {
            if (condition == null) {
                throw new RuntimeException("condition == null in rule " + this);
            }
            if (condition.not() == null) {
                throw new RuntimeException("condition.not() == null in rule " + this);
            }
            condition.not().adaptRating(rating);
        }

        boolean isTerminal() {
            if("A".equals(next)) return true;
            if("R".equals(next)) return true;
            return false;
        }

        public boolean isAccepting() {
            if("A".equals(next)) return true;
            return false;
        }

        @Override
        public String toString() {
            String string = "";
            if (followAlways() == false) {
                string += condition + ":";
            }
            string += next;
            return string;
        }

        public boolean followAlways() {
            return condition instanceof TrueExpression;
        }
    }

    private void processPart(String part) {
        var workflow = selectWorkflow("in");
        while(true) {
            var next = calculateNext(part, workflow);
            if ("A".equals(next)) {
                accepted.add(part);
                return;
            }
            if ("R".equals(next)) {
                return;
            }
            workflow = selectWorkflow(next);
        }
    }

    private String calculateNext(String part, String workflow) {
        var rulePart = workflow.substring(workflow.indexOf("{")+1, workflow.indexOf("}"));
        var rules = rulePart.split(",");
        for (String rule : rules) {
            if(rule.contains(":") == false) return rule;
            var conditionAndNext = rule.split(":");
            var condition = asExpression(conditionAndNext[0]);
            var next = conditionAndNext[1];
            if(fulfillsCondition(part, condition)) return next;
        }

        return "";
    }

    private Expression asExpression(String in) {
        if(in.contains("<")) return new LtExpression(in);
        if(in.contains(">")) return new GtExpression(in);
        return new DefaultExpression();
    }

    private boolean fulfillsCondition(String part, Expression condition) {
        var ratings = part.substring(1, part.length() - 1).split(",");
        for (String rating : ratings) {
            var keyValue = rating.split("=");
            if(condition.matches(keyValue)) return true;
        }

        return false;
    }

    private interface Expression {
        boolean matches(String[] keyValue);

        Expression not();

        void adaptRating(Rating rating);
    }
    private abstract class AbstractExpression implements Expression {}

    private class LtExpression extends AbstractExpression {
        String left;
        Long right;

        public LtExpression(String left, Long right) {
            this.left = left;
            this.right = right;
        }

        public LtExpression(String in) {
            left = in.substring(0, in.indexOf("<"));
            right = Long.parseLong(in.substring(in.indexOf("<") + 1));
        }

        @Override
        public boolean matches(String[] keyValue) {
            if(left.equals(keyValue[0]) == false) return false;
            var value = Long.parseLong(keyValue[1]);
            return value < right;
        }

        @Override
        public Expression not() {
            return new GeExpression(left, right);
        }

        @Override
        public void adaptRating(Rating rating) {
            rating.lowerMaximum(left, right-1);
        }

        @Override
        public String toString() {
            return left+"<"+right;
        }
    }
    private class GeExpression extends AbstractExpression {
        String left;
        Long right;

        public GeExpression(String left, Long right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean matches(String[] keyValue) {
            if(left.equals(keyValue[0]) == false) return false;
            var value = Long.parseLong(keyValue[1]);
            return value >= right;
        }

        @Override
        public Expression not() {
            return new LtExpression(left, right);
        }

        @Override
        public void adaptRating(Rating rating) {
            rating.raiseMin(left, right);
        }

        @Override
        public String toString() {
            return left+">="+right;
        }
    }
    private class LeExpression extends AbstractExpression {
        String left;
        Long right;

        public LeExpression(String left, Long right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean matches(String[] keyValue) {
            if(left.equals(keyValue[0]) == false) return false;
            var value = Long.parseLong(keyValue[1]);
            return value <= right;
        }

        @Override
        public Expression not() {
            return new GtExpression(left, right);
        }

        @Override
        public void adaptRating(Rating rating) {
            rating.lowerMaximum(left, right);
        }

        @Override
        public String toString() {
            return left+"<="+right;
        }
    }
    private class GtExpression extends AbstractExpression {
        String left;
        Long right;

        public GtExpression(String left, Long right) {
            this.left = left;
            this.right = right;
        }

        public GtExpression(String in) {
            left = in.substring(0, in.indexOf(">"));
            right = Long.parseLong(in.substring(in.indexOf(">") + 1));
        }

        @Override
        public boolean matches(String[] keyValue) {
            if(left.equals(keyValue[0]) == false) return false;
            var value = Long.parseLong(keyValue[1]);
            return value > right;
        }

        @Override
        public Expression not() {
            return new LeExpression(left, right);
        }

        @Override
        public void adaptRating(Rating rating) {
            rating.raiseMin(left, right+1);
        }

        @Override
        public String toString() {
            return left+">"+right;
        }
    }

    private class DefaultExpression extends AbstractExpression {
        @Override
        public boolean matches(String[] keyValue) {
            return false;
        }

        @Override
        public Expression not() {
            return null;
        }

        @Override
        public void adaptRating(Rating rating) {

        }

        @Override
        public String toString() {
            return "";
        }
    }
    private class TrueExpression extends AbstractExpression {
        @Override
        public boolean matches(String[] keyValue) {
            return true;
        }

        @Override
        public Expression not() {
            return new DefaultExpression();
        }

        @Override
        public void adaptRating(Rating rating) {

        }

        @Override
        public String toString() {
            return "";
        }
    }


    private void toWorkflows() {
        for (String workflow : workflows) {
            try {
                wfs.add(new Workflow(workflow));
            } catch (Exception e) {
                throw new RuntimeException("Syntax error: "+workflow);
            }
        }
    }

    private String selectWorkflow(String name) {
        for (String workflow : workflows) {
            if(workflow.startsWith(name+"{")) return workflow;
        }

        return "";
    }

    private Workflow select(String name) {
        for (var workflow : wfs) {
            if(workflow.name.equals(name)) return workflow;
        }

        throw new RuntimeException("Workflow not found: "+name);
    }

    private void sortRulesAndParts(List<String> lines) {
        boolean isRules = true;
        for (String line : lines) {
            if (line.isBlank()) {
                isRules = false;
                continue;
            }
            if (isRules) {
                workflows.add(line);
            } else {
                parts.add(line);
            }
        }
    }
}
