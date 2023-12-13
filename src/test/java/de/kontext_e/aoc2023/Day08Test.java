package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day08Test {
    private String[] currentNodes = new String[1000];
    private int nodeCount = 0;
    private List<Long> nodeCounts = new ArrayList<>();

    @Test
    void test() throws IOException {
        Path path = Paths.get("src/test/resources/day08test.txt");
        var lines = Files.readAllLines(path);
        var instructions = lines.get(0);

        // first two lines belong not to network
        lines.remove(0);
        lines.remove(0);

        findStartNodes(lines);

        for(int i = 0; i < nodeCount; i++) {
            var currentNode = currentNodes[i];
            var count = processInstructions(instructions, lines, currentNode);
            nodeCounts.add(count);
        }

        var count = kgv();

        assertEquals(6, count);
    }

    @Test
    void testWithInput() throws IOException {
        Path path = Paths.get("src/test/resources/day08input.txt");
        var lines = Files.readAllLines(path);
        var instructions = lines.get(0);

        // first two lines belong not to network
        lines.remove(0);
        lines.remove(0);

        findStartNodes(lines);

        for(int i = 0; i < nodeCount; i++) {
            var currentNode = currentNodes[i];
            var count = processInstructions(instructions, lines, currentNode);
            nodeCounts.add(count);
        }

        var count = kgv();

        assertEquals(14321394058031L, count);
    }

    private long kgv() {
        final var a = nodeCounts.get(0);
        final var b = nodeCounts.get(1);
        var x = ggt(a, b);
        var kgv = a/x*b;
        if(nodeCounts.size() > 2) {
            for (int i = 2; i < nodeCounts.size(); i++) {
                final var b1 = nodeCounts.get(i);
                kgv = kgv/ggt(kgv, b1)*b1;
            }
        }
        return kgv;
    }

    public static long ggt(long a, long b) {
        return (a == 0) ? b : ggt(b % a, a);
    }

    private void findStartNodes(List<String> lines) {
        for (String line : lines) {
            var nodeName = line.substring(0, line.indexOf("=") - 1).trim();
            if(nodeName.endsWith("A"))
            {
                currentNodes[nodeCount++] = nodeName;
            }
        }
    }

    private long processInstructions(String instructions, List<String> network, String node) {
        final var instructionIterable = new InstructionIterable(instructions);
        long count = 0;

        var currentNode = node;
        for (int index : instructionIterable) {
            currentNode = findNextNode(currentNode, index, network);

            count++;
            if (currentNode.endsWith("Z")) {
                return count;
            }
        }

        return count;
    }

    private String findNextNode(String element, int index, List<String> network) {
        String currentNode = findCurrntNode(element, network);
        return relationShipsOf(currentNode)[index].trim();
    }

    private String[] relationShipsOf(String currentNode) {
        var relationShips = currentNode.substring(currentNode.indexOf("("));
        relationShips = relationShips.replace("(", "").replace(")", "").trim();
        return relationShips.split(",");
    }

    private String findCurrntNode(String element, List<String> network) {
        for (String s : network) {
            if (s.startsWith(element)) {
                return s;
            }
        }

        return "";
    }

    private static class InstructionIterable implements Iterable<Integer> {
        private final String instructions;
        private final Iterator<Integer> iterator = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                if (position >= instructions.length()) {
                    position = 0;
                }

                var instruction = instructions.charAt(position);
                position++;
                return instruction == 'L' ? 0 : 1;
            }
        };
        private int position = 0;

        public InstructionIterable(String instructions) {
            this.instructions = instructions;
        }

        @Override
        public void forEach(Consumer<? super Integer> action) {
            Iterable.super.forEach(action);
        }

        @Override
        public Spliterator<Integer> spliterator() {
            return Iterable.super.spliterator();
        }

        @Override
        public Iterator<Integer> iterator() {
            return iterator;
        }
    }
}

