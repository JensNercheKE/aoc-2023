package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Character.isDigit;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day03Test {
    @Test
    void testAdjacentNumbers() throws IOException {
        EngineSchematic engineSchematic = EngineSchematic.readInputFile("src/test/resources/day03test.txt");
        var numbersAdjacentToSymbol = engineSchematic.sumAdjacentToSymbolNumbers();
        var gearRatioSum = engineSchematic.calculateGearRationSum();
        assertEquals(4361, numbersAdjacentToSymbol);
        assertEquals(467835, gearRatioSum);
    }

    @Test
    void testAdjacentNumbersWithInput() throws IOException {
        EngineSchematic engineSchematic = EngineSchematic.readInputFile("src/test/resources/day03input.txt");
        var numbersAdjacentToSymbol = engineSchematic.sumAdjacentToSymbolNumbers();
        var gearRatioSum = engineSchematic.calculateGearRationSum();
        assertEquals(533784, numbersAdjacentToSymbol);
        assertEquals(78826761, gearRatioSum);
    }

    @Test
    void parseLineWithNumbers() {
        EngineSchematic engineSchematic = new EngineSchematic();

        engineSchematic.parseLine("467..114", 0);

        assertEquals(2, engineSchematic.numbers.size());
        assertEquals(0, engineSchematic.symbols.size());
    }

    @Test
    void parseLineWithNumbersAndSymbol() {
        EngineSchematic engineSchematic = new EngineSchematic();

        engineSchematic.parseLine("467@114", 0);

        assertEquals(2, engineSchematic.numbers.size());
        assertEquals(467, engineSchematic.numbers.get(0).value);
        assertEquals(114, engineSchematic.numbers.get(1).value);
        assertEquals(1, engineSchematic.symbols.size());
        assertEquals("@", engineSchematic.symbols.get(0).symbol);
        assertEquals(3, engineSchematic.symbols.get(0).start);
        assertEquals(3, engineSchematic.symbols.get(0).end);
    }

    @Test
    void testNumberAdjacentToSymbol() {
        EngineSchematic engineSchematic = new EngineSchematic();
        engineSchematic.parseLine("467..114..", 0);
        engineSchematic.parseLine("...*......", 1);
        assertEquals(2, engineSchematic.numbers.size());
        assertEquals(1, engineSchematic.symbols.size());

        var numbersAdjacentToSymbol = engineSchematic.filterAdjacentToSymbolNumbers();

        assertEquals(1, numbersAdjacentToSymbol.size());
        assertEquals(467, numbersAdjacentToSymbol.get(0).value);
    }

    private static class EngineSchematic {
        private final List<String> lines = new LinkedList<>();
        private final List<EngineNumber> numbers = new ArrayList<>();
        private final List<EngineSymbol> symbols = new ArrayList<>();

        int pos = 0;
        String token = "";
        int startPos = -1;
        int endPos = -1;
        TokenType tokenType = null;

        public static EngineSchematic readInputFile(String pathString) throws IOException {
            EngineSchematic engineSchematic = new EngineSchematic();

            Path path = Paths.get(pathString);
            var lines = Files.readAllLines(path);
            engineSchematic.lines.addAll(lines);

            int lineNumber = 0;
            for (String line : lines) {
                engineSchematic.parseLine(line, lineNumber++);
            }

            return engineSchematic;
        }

        public List<EngineNumber> filterAdjacentToSymbolNumbers() {
            List<EngineNumber> filteredList = new ArrayList<>();

            for (EngineNumber number : numbers) {
                if (isAdjacentToSymbol(number)) {
                    filteredList.add(number);
                }
            }

            return filteredList;
        }

        private boolean isAdjacentToSymbol(EngineNumber number) {
            for (EngineSymbol symbol : symbols) {
                if (isNeighborLine(symbol.line, number.line) && rangeIsOverlapping(symbol, number)) {
                    return true;
                }
            }

            return false;
        }

        private boolean rangeIsOverlapping(EngineSymbol symbol, EngineNumber number) {
            if(overlapps(symbol.start, number.start-1, number.end+1)) return true;
            if(overlapps(symbol.end, number.start-1, number.end+1)) return true;
            return false;
        }

        private boolean overlapps(int x, int start, int end) {
            return x >= start && x <= end;
        }

        private boolean isNeighborLine(int lineA, int lineB) {
            if(lineA == lineB) return true;
            if(lineA - 1 == lineB) return true;
            if(lineA + 1 == lineB) return true;

            return false;
        }

        private List<EngineNumber> getNumbersAdjacentToSymbol(EngineSymbol symbol) {
            List<EngineNumber> adjacentNumbers = new ArrayList<>();

            for (EngineNumber number : numbers) {
                if (isNeighborLine(number.line, symbol.line) && rangeIsOverlapping(symbol, number)) {
                    adjacentNumbers.add(number);
                }
            }

            return adjacentNumbers;
        }

        public long sumAdjacentToSymbolNumbers() {
            long sum = 0;
            final var adjacentToSymbolNumbers = filterAdjacentToSymbolNumbers();
            for (EngineNumber filterAdjacentToSymbolNumber : adjacentToSymbolNumbers) {
                sum += filterAdjacentToSymbolNumber.value;
            }

            return sum;
        }

        public long calculateGearRationSum() {
            long sum = 0;
            final var gears = filterGears();
            for (Gear gear : gears) {
                sum += gear.getRation();
            }

            return sum;
        }

        private List<Gear> filterGears() {
            final List<Gear> gears = new ArrayList<>();

            for (EngineSymbol symbol : symbols) {
                if ("*".equals(symbol.symbol)) {
                    List<EngineNumber> adjacentNumbers = getNumbersAdjacentToSymbol(symbol);
                    if (adjacentNumbers.size() == 2) {
                        gears.add(new Gear(adjacentNumbers));
                    }
                }
            }

            return gears;
        }

        private enum TokenType {NUMBER, SYMBOL}
        public void parseLine(String line, int lineNumber) {
            resetParser();
            pos = 0;

            while(pos < line.length()) {
                var candidate = line.charAt(pos);
                if (isDigit(candidate)) {
                    if (tokenType != null && TokenType.NUMBER.equals(tokenType) == false) {
                        tokenEnded(lineNumber);
                    }
                    tokenType = TokenType.NUMBER;
                    token += candidate;
                    if (startPos == -1) {
                        startPos = pos;
                    }
                } else {
                    if ('.' == candidate) {
                        tokenEnded(lineNumber);
                    } else {
                        if (tokenType != null && TokenType.SYMBOL.equals(tokenType) == false) {
                            tokenEnded(lineNumber);
                        }
                        tokenType = TokenType.SYMBOL;
                        token += candidate;
                        if (startPos == -1) {
                            startPos = pos;
                        }
                    }
                }

                pos++;
            }
            endPos = pos - 1;
            parseToken(token, lineNumber, startPos, endPos); // in case token ends with line end
        }

        private void tokenEnded(int lineNumber) {
            endPos = pos - 1;
            parseToken(token, lineNumber, startPos, endPos);
            resetParser();
        }

        private void resetParser() {
            token = "";
            startPos = -1;
            endPos = -1;
            tokenType = null;
        }

        private void parseToken(String token, int lineNumber, int startPos, int endPos) {
            if (token.isEmpty() == false) {
                if (isDigit(token.charAt(0))) {
                    int number = Integer.parseInt(token);
                    numbers.add(new EngineNumber(number, lineNumber, startPos, endPos));
                } else {
                    symbols.add(new EngineSymbol(token, lineNumber, startPos, endPos));
                }
            }
        }
    }

    private static class EngineNumber {
        private long value = 0;
        private int line = -1;
        private int start = -1;
        private int end = -1;

        public EngineNumber(int value, int line, int start, int end) {
            this.value = value;
            this.line = line;
            this.start = start;
            this.end = end;
        }
    }

    private static class EngineSymbol {
        private String symbol = "";
        private int line = -1;
        private int start = -1;
        private int end = -1;

        public EngineSymbol(String symbol, int line, int start, int end) {
            this.symbol = symbol;
            this.line = line;
            this.start = start;
            this.end = end;
        }
    }

    private static class Gear {
        private EngineNumber part1;
        private EngineNumber part2;

        public Gear(List<EngineNumber> adjacentNumbers) {
            part1 = adjacentNumbers.get(0);
            part2 = adjacentNumbers.get(1);
        }

        public long getRation() {
            if (part1 != null && part2 != null) {
                return part1.value * part2.value;
            }

            return 0;
        }
    }
}
