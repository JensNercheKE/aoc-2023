package de.kontext_e.aoc2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day23Test {
    char[][] field;
    int[][] maxForTiles;
    private int width;
    private int height;
    private Coordinate start;
    private Coordinate end;
    private final List<Hike> hikes = new ArrayList<>();
    private final Set<Coordinate> vertexes = new HashSet<>();
    private final Set<Edge> edges = new HashSet<>();
    private int maxStepsAtEnd = 0;

    @BeforeEach
    void setUp() {
        width = 0;
        height = 0;
        start = new Coordinate(-1, -1);
        end = new Coordinate(-1, -1);
        hikes.clear();
        maxStepsAtEnd = 0;
        vertexes.clear();
        edges.clear();
    }

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day23test.txt");
        var lines = Files.readAllLines(path);
        toField(lines);
        determineStartAndEnd();

        walkMaze();

        var max = maxStepsAtEnd - 1;
        assertEquals(154, max);
    }

    @Test
    void analyzeMaze() throws IOException {
        Path path = Paths.get("src/test/resources/day23input.txt");
        var lines = Files.readAllLines(path);
        toField(lines);
        determineStartAndEnd();

        toGraph();
        var longestWalk = walkGraph();

        assertEquals(6350, longestWalk);
    }

    private List<Edge> getNeighbors(Coordinate current) {
        var neighbors = new ArrayList<Edge>();
        for (Edge edge : edges) {
            if (edge.a.equals(current)) {
                neighbors.add(edge);
            }
        }

        return neighbors;
    }

    private void toGraph() {
        vertexes.add(start);
        vertexes.add(end);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isJunction(x, y)) {
                    vertexes.add(new Coordinate(x, y));
                }
            }
        }
        //System.out.println("Vertexes: "+vertexes.size());

        for (Coordinate vertex : vertexes) {
            hikes.clear();

            var x = vertex.x;
            var y = vertex.y;

            var hike = new Hike(x, y);
            hikes.add(hike);
            while (hikes.isEmpty() == false) {
                final var hikesCopy = new ArrayList<>(hikes);
                for (Hike h : hikesCopy) {
                    h.step();
                }
            }
        }

/*
        System.out.println("Edges: "+edges.size());
        for (Edge edge : edges) {
            System.out.println(edge);
        }
*/

    }

    private boolean isJunction(int x, int y) {
        if(field[x][y] == '#') return false;


        var count = 0;
        if(x > 0 && field[x - 1][y] != '#') count++;
        if(x < width - 1 && field[x + 1][y] != '#') count++;
        if(y > 0 && field[x][y - 1] != '#') count++;
        if(y < height - 1 && field[x][y + 1] != '#') count++;
        return count > 2;
    }

    private int walkGraph() {
        int longestWalk = 0;
        hikes.clear();
        hikes.add(new Hike(start));

        while(hikes.isEmpty() == false) {
            var current = selectHike();
            hikes.remove(current);
            if (current.endReached()) {
                if (current.length > longestWalk) {
                    longestWalk = current.length;
                }
                System.out.println("End reached: "+current+" max "+longestWalk+" active "+hikes.size());

                int l = 0;
                for (var traveledEdge : current.traveledEdges) {
                    l += traveledEdge.size;
                }
            }
            var options = current.getOptions();
            if (options.isEmpty()) {
                //System.out.println("Hike has no options anymore: " + current);
            } else {
                for (var option : options) {
                    var newHike = current.copy(option);
                    hikes.add(newHike);
                }
            }
        }
        return longestWalk;
    }

    private Hike selectHike() {
        var selected = hikes.get(0);

        int length = 0;
        for (var hike : hikes) {
            if (hike.length > length) {
                length = hike.length;
                selected = hike;
            }
        }

        return selected;
    }

    private void walkMaze() {
        hikes.add(new Hike(start.x, start.y));
        while(hikes.isEmpty() == false) {

            final var numberOfParallel = 20_000_000;
            final var hikesCopy = new ArrayList<>(hikes);
            for (int i = 0; i < Math.min(numberOfParallel, hikesCopy.size()); i++) {
                var hike = hikesCopy.get(i);
                hike.step();
            }
            //checkCopies();

        }
    }

    private void checkCopies() {
        final var hikesCopy = new ArrayList<>(hikes);
        for (Hike hike : hikesCopy) {
            hike.checkIfStillNeeded();
        }
    }

    private class Hike {
        int x;
        int y;
        List<Coordinate> steps = new ArrayList<>();
        List<Coordinate> visited = new ArrayList<>();
        List<Edge> traveledEdges = new ArrayList<>();
        int length = 0;

        public Hike(Coordinate current) {
            this.x = current.x;
            this.y = current.y;
            steps.add(current);
            visited.add(current);
        }

        public Hike(int x, int y) {
            this.x = x;
            this.y = y;
            steps.add(new Coordinate(x, y));
            visited.add(new Coordinate(x, y));
        }

        @Override
        public String toString() {
            return "("+x+","+y+") l="+length+" v="+visited.size();
        }

        public void checkIfStillNeeded() {
            if (steps.size() < maxForTiles[x][y]) {
                System.out.printf("(check) Hiker (%d,%d) cannot win anymore, remove\n", x, y);
                hikes.remove(this);
            }
        }

        public void step() {
/*
            if (steps.size() < maxForTiles[x][y]) {
                //System.out.printf("Hiker (%d,%d) cannot win anymore, remove\n", x, y);
                //printSteps(this);
                hikes.remove(this);
                return;
            }
*/

            if(end.equals(x, y)) {
                System.out.println("Hiker reach end. Steps: "+steps.size()+" max: "+maxStepsAtEnd+" alive hikers "+hikes.size());
                if (steps.size() > maxStepsAtEnd) {
                    maxStepsAtEnd = steps.size();
                }
                hikes.remove(this);
                return;
            }

            var options = countOptions();
            if (options.isEmpty()) {
                System.out.printf("Hiker (%d,%d) reached dead end\n", x, y);
                printSteps(this);
                hikes.remove(this);
                return;
            }
            if (options.size() == 1) {
                var o = options.get(0);
                this.x = o.x;
                this.y = o.y;
                steps.add(o);
            }
            if (options.size() > 1) {
                for (int i = 1; i < options.size(); i++) {
                    hikes.add(copy(options.get(i)));
                }

                var o = options.get(0);
                this.x = o.x;
                this.y = o.y;
                steps.add(o);
            }

            final var now = new Coordinate(x, y);
            if (vertexes.contains(now)) {
                //System.out.println("Hiker reach vertex. Steps: "+steps.size()+" alive hikers "+hikes.size());
                hikes.remove(this);
                edges.add(new Edge(steps.get(0), now, steps.size() - 1));
                return;
            }

            //System.out.printf("Hiker (%d,%d): Steps until here: %d\n", x, y, steps.size());
        }

        private void printSteps(Hike hike) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if(start.equals(new Coordinate(x, y))) {
                        System.out.print('S');
                    } else if(end.equals(new Coordinate(x, y))) {
                        System.out.print('E');
                    } else if(this.x == x && this.y == y) {
                        System.out.print('H');
                    } else if(hike.contains(x, y)) {
                        System.out.print('o');
                    } else {
                        System.out.print(field[x][y]);
                    }
                }
                System.out.println();
            }
            System.out.println();

            for (Coordinate step : steps) {
                System.out.printf("(%d,%d) ", step.x, step.y);
            }
            System.out.println();
        }

        private boolean contains(int x, int y) {
            return steps.contains(new Coordinate(x, y));
        }

        private Hike copy(Coordinate coordinate) {
            final var hike = new Hike(coordinate);
            hike.steps.addAll(0, this.steps);
            hike.visited.addAll(0, this.visited);
            hike.traveledEdges.addAll(0, this.traveledEdges);
            hike.length = this.length;
            return hike;
        }

        public Hike copy(Edge option) {
            var hike = copy(option.b);
            hike.addLength(option.size());
            hike.traveledEdges.add(option);
            return hike;
        }

        private void addLength(int size) {
            length += size;
        }

        private List<Coordinate> countOptions() {
            List<Coordinate> options = new LinkedList<>();
            checkNorth(options);
            checkEast(options);
            checkSouth(options);
            checkWest(options);
            return options;
        }

        private void checkNorth(List<Coordinate> options) {
            if(y == 0) return;
            int oy = y - 1;
            if(field[x][oy] == '#') return;
            //if(steps.size() < maxForTiles[x][oy]) return;

            //if(field[x][oy] == 'v') return;
            final var coordinate = new Coordinate(x, oy);
            if(steps.contains(coordinate)) return;

            //maxForTiles[x][oy] = length() + 1;
            options.add(coordinate);
        }

        private void checkSouth(List<Coordinate> options) {
            if(y == height - 1) return;
            int oy = y + 1;
            if(field[x][oy] == '#') return;
            //if(steps.size() < maxForTiles[x][oy]) return;
            //if(field[x][oy] == '^') return;
            final var coordinate = new Coordinate(x, oy);
            if(steps.contains(coordinate)) return;

            //maxForTiles[x][oy] = length() + 1;
            options.add(coordinate);
        }

        private void checkEast(List<Coordinate> options) {
            if(x == width - 1) return;
            int ox = x + 1;
            if(field[ox][y] == '#') return;
            //if(steps.size() < maxForTiles[ox][y]) return;
            //if(field[ox][y] == '<') return;
            final var coordinate = new Coordinate(ox, y);
            if(steps.contains(coordinate)) return;

            //maxForTiles[ox][y] = length() + 1;
            options.add(coordinate);
        }

        private void checkWest(List<Coordinate> options) {
            if(x == 0) return;
            int ox = x - 1;
            if(field[ox][y] == '#') return;
            //if(steps.size() < maxForTiles[ox][y]) return;
            //if(field[ox][y] == '>') return;
            final var coordinate = new Coordinate(ox, y);
            if(steps.contains(coordinate)) return;

            //maxForTiles[ox][y] = length() + 1;
            options.add(coordinate);
        }

        public boolean isCurrentPosition(int x, int y) {
            return this.x == x && this.y == y;
        }

        public int length() {
            return steps.size();
        }

        public boolean endReached() {
            return end.equals(x, y);
        }

        public Coordinate getLastVisited() {
            return visited.get(visited.size() - 1);
        }

        public List<Edge> getOptions() {
            final List<Edge> options = new ArrayList<>();
            var lastVisited = getLastVisited();
            final var neighbors = getNeighbors(lastVisited);
            for (var neighbor : neighbors) {
                if (visited.contains(neighbor.b) == false) {
                    options.add(neighbor);
                }
            }
            return options;
        }
    }

    private void determineStartAndEnd() {
        for (int x = 0; x < width; x++) {
            if (field[x][0] == '.') {
                start = new Coordinate(x, 0);
            }
        }
        for (int x = 0; x < width; x++) {
            if (field[x][height-1] == '.') {
                end = new Coordinate(x, height-1);
            }
        }
    }


    private void printField() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(start.equals(new Coordinate(x, y))) {
                    System.out.print('S');
                } else if(end.equals(new Coordinate(x, y))) {
                    System.out.print('E');
                } else if(locatesHiker(x, y)) {
                    System.out.print('H');
                } else {
                    System.out.print(field[x][y]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private boolean locatesHiker(int x, int y) {
        for (Hike hike : hikes) {
            if(hike.isCurrentPosition(x, y)) return true;
        }

        return false;
    }

    private void toField(List<String> lines) {
        width = lines.get(0).length();
        height = lines.size();
        field = new char[width][height];
        maxForTiles = new int[width][height];

        for (int y = 0; y < height; y++) {
            var line = lines.get(y);
            for (int x = 0; x < width; x++) {
                field[x][y] = line.charAt(x);
            }
        }
    }

    private record Coordinate(int x, int y) {
        public boolean equals(int x, int y) {
            return this.x == x && this.y == y;
        }
    }

    private record Edge(Coordinate a, Coordinate b, int size){}
    
}
