package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;

class Day02Test {

    @Test
    void testParseGame() {
        var gameRecord = "Game 11: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green";
        var game = Game.parse(gameRecord);

        assertEquals(11, game.id);
        assertEquals(3, game.cubeSets.size());

        var cubeSet1 = game.cubeSets.get(0);
        assertEquals(4, cubeSet1.red);
        assertEquals(0, cubeSet1.green);
        assertEquals(3, cubeSet1.blue);

        var cubeSet2 = game.cubeSets.get(1);
        assertEquals(1, cubeSet2.red);
        assertEquals(2, cubeSet2.green);
        assertEquals(6, cubeSet2.blue);

        var cubeSet3 = game.cubeSets.get(2);
        assertEquals(0, cubeSet3.red);
        assertEquals(2, cubeSet3.green);
        assertEquals(0, cubeSet3.blue);

        assertTrue(game.isPossible());
        assertEquals(new CubeSet(4, 2, 6), game.getFewestNumberOfCubes());
        assertEquals(48, game.getFewestNumberOfCubes().getPower());
    }

    @Test
    void testGame3IsNotPossible() {
        var game = Game.parse("Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red");
        assertFalse(game.isPossible());
    }

    @Test
    void testParseAllGames() throws IOException {
        Path path = Paths.get("src/test/resources/day02test.txt");
        var lines = Files.readAllLines(path);
        assertEquals(5, lines.size());

        long sum = 0;
        long powerSum = 0;
        for (String line : lines) {
            var game = Game.parse(line);
            if (game.isPossible()) {
                sum += game.id;
            }

            var fewestNumberOfCubes = game.getFewestNumberOfCubes();
            powerSum += fewestNumberOfCubes.getPower();
        }

        assertEquals(8, sum);
        assertEquals(2286, powerSum);
    }

    @Test
    void testParseDay02Input() throws IOException {
        Path path = Paths.get("src/test/resources/day02input.txt");
        var lines = Files.readAllLines(path);
        assertEquals(100, lines.size());

        long sum = 0;
        long powerSum = 0;
        for (String line : lines) {
            var game = Game.parse(line);
            if (game.isPossible()) {
                sum += game.id;
            }

            var fewestNumberOfCubes = game.getFewestNumberOfCubes();
            powerSum += fewestNumberOfCubes.getPower();
        }

        assertEquals(2204, sum);
        assertEquals(71036, powerSum);
    }

    private static class Game {
        private int id = 0;
        private final List<CubeSet> cubeSets = new LinkedList<>();

        public static Game parse(String gameRecord) {
            Game game = new Game();

            int nextDelimiter = gameRecord.indexOf(":");
            var idString = gameRecord.substring(5, nextDelimiter);
            game.id = Integer.parseInt(idString);

            boolean done = false;
            while(done == false) {
                var previousDelimiter = nextDelimiter + 1;
                nextDelimiter = gameRecord.indexOf(";", previousDelimiter);
                var cubeSetString = "";
                if (nextDelimiter != -1) {
                    cubeSetString = gameRecord.substring(previousDelimiter, nextDelimiter);
                } else {
                    cubeSetString = gameRecord.substring(previousDelimiter);
                    done = true;
                }
                game.cubeSets.add(CubeSet.parse(cubeSetString));
            }

            return game;
        }

        public boolean isPossible() {
            for (CubeSet cubeSet : cubeSets) {
                if (cubeSet.isPossible() == false) {
                    return false;
                }
            }

            return true;
        }

        public CubeSet getFewestNumberOfCubes() {
            CubeSet fewestNumbers = new CubeSet();

            for (CubeSet cubeSet : cubeSets) {
                if (cubeSet.red > fewestNumbers.red) {
                    fewestNumbers.red = cubeSet.red;
                }
                if (cubeSet.green > fewestNumbers.green) {
                    fewestNumbers.green = cubeSet.green;
                }
                if (cubeSet.blue > fewestNumbers.blue) {
                    fewestNumbers.blue = cubeSet.blue;
                }
            }
            return fewestNumbers;
        }
    }

    private static class CubeSet {
        private int red = 0;
        private int green = 0;
        private int blue = 0;

        public CubeSet() {
        }

        public CubeSet(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public static CubeSet parse(String cubeSetString) {
            String trimmed = cubeSetString.trim();

            CubeSet cubeSet = new CubeSet();

            boolean done = false;
            int previousDelimiter = 0;
            while(done == false) {
                int nextDelimiter = trimmed.indexOf(" ", previousDelimiter);
                if (nextDelimiter != -1) {
                    var countString = trimmed.substring(previousDelimiter, nextDelimiter);
                    var count = Integer.parseInt(countString);
                    previousDelimiter = nextDelimiter + 1;

                    nextDelimiter = trimmed.indexOf(",", previousDelimiter);
                    var colorString = "";
                    if (nextDelimiter != -1) {
                        colorString = trimmed.substring(previousDelimiter, nextDelimiter);
                    } else {
                        colorString = trimmed.substring(previousDelimiter);
                        done = true;
                    }
                    previousDelimiter = nextDelimiter + 2;

                    if ("red".equals(colorString)) {
                        cubeSet.red = count;
                    }
                    if ("blue".equals(colorString)) {
                        cubeSet.blue = count;
                    }
                    if ("green".equals(colorString)) {
                        cubeSet.green = count;
                    }
                }
            }

            return cubeSet;
        }

        public boolean isPossible() {
            return red <=12 && green <= 13 && blue <= 14;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CubeSet cubeSet = (CubeSet) o;
            return red == cubeSet.red && green == cubeSet.green && blue == cubeSet.blue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(red, green, blue);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", CubeSet.class.getSimpleName() + "[", "]")
                    .add("red=" + red)
                    .add("green=" + green)
                    .add("blue=" + blue)
                    .toString();
        }

        public int getPower() {
            return red * green * blue;
        }
    }
}
