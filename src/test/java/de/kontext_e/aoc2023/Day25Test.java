package de.kontext_e.aoc2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class Day25Test {
    private final Map<String, Component> components = new HashMap<>();
    private final List<Connection> connections = new ArrayList<>();
    private final List<Component> clusterA = new ArrayList<>();
    private final List<Component> clusterB = new ArrayList<>();

    @BeforeEach
    void setUp() {
        components.clear();
        connections.clear();
        clusterA.clear();
        clusterB.clear();
    }

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day25test.txt");
        var lines = Files.readAllLines(path);
        toGraph(lines);
        calculateClusteringCoefficient();
        clusterA.add(findComponent("cmg"));
        clusterB.add(findComponent("bvb"));
        clusterWithSeed();
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day25input.txt");
        var lines = Files.readAllLines(path);
        toGraph(lines);
        calculateClusteringCoefficient();
        clusterA.add(findComponent("lcz"));
        clusterB.add(findComponent("fmp"));
        clusterWithSeed();

        // 580800 is right
    }

    private void cluster() {
        clusterA.addAll(components.values());
        for (int i = 0; i < components.size() / 2; i++) {
            var c = clusterA.remove(0);
            clusterB.add(c);
        }

        System.out.println("A: "+clusterA);
        System.out.println("B: "+clusterB);

        for(int i = 0; i < 10; i++) {
            iterateRecluster();
        }

        System.out.println("card(A) = "+clusterA.size()+" card(B) = "+clusterB.size());
    }

    private int clusterWithSeed() {
        var all = new ArrayList<>(components.values());
        all.removeAll(clusterA);
        all.removeAll(clusterB);

        while(false == all.isEmpty()) {
            for (int i = 0; i < all.size(); i++) {
                var c = all.remove(0);
                var a = countConnectionsTo(c, clusterA);
                var b = countConnectionsTo(c, clusterB);
                if (a > b) {
                    clusterA.add(c);
                } else if (b > a) {
                    clusterB.add(c);
                } else {
                    all.add(c);
                }
            }

            System.out.println("A: " + clusterA.size());
            System.out.println("B: " + clusterB.size());
            System.out.println("all: " + all.size());
            System.out.println("A * B: " + clusterA.size() * clusterB.size());
        }

        return countConnectionsBetweenClusters();
    }

    private Component findComponent(String name) {
        for (var comp : components.values()) {
            if(name.equalsIgnoreCase(comp.name)) return comp;
        }
        throw new RuntimeException("Component not found: "+name);
    }

    private int iterateRecluster() {
        for (int i = 0; i < clusterA.size(); i++) {
            var c = clusterA.remove(0);
            var a = countConnectionsTo(c, clusterA);
            var b = countConnectionsTo(c, clusterB);
            if(a > b) clusterA.add(c);
            else clusterB.add(c);
        }
        for (int i = 0; i < clusterB.size(); i++) {
            var c = clusterB.remove(0);
            var a = countConnectionsTo(c, clusterA);
            var b = countConnectionsTo(c, clusterB);
            if(a > b) clusterA.add(c);
            else clusterB.add(c);
        }
        System.out.println("A: "+clusterA);
        System.out.println("B: "+clusterB);

        return countConnectionsBetweenClusters();
    }

    private int countConnectionsBetweenClusters() {
        int result1 = -1;
        int result2 = -2;
        int toA = 0;
        int toB = 0;
        for (int i = 0; i < clusterA.size(); i++) {
            var c = clusterA.get(i);
            var a = countConnectionsTo(c, clusterA);
            var b = countConnectionsTo(c, clusterB);
            toA += a;
            toB += b;
        }
        result1 = toB;
        System.out.println("Connections cluster A: intra "+toA +" inter "+toB);

        toA = 0;
        toB = 0;
        for (int i = 0; i < clusterB.size(); i++) {
            var c = clusterB.get(i);
            var a = countConnectionsTo(c, clusterA);
            var b = countConnectionsTo(c, clusterB);
            toA += a;
            toB += b;
        }
        result2 = toA;
        System.out.println("Connections cluster B: intra "+toB +" inter "+toA);

        if(result1 != result2) throw new IllegalStateException("result1 = " + result1 + " result2 = " + result2);

        return result1;
    }

    private int countConnectionsTo(Component c, List<Component> cluster) {
        int count = 0;
        for (var comp : c.connections) {
            if(cluster.contains(comp)) count++;
        }
        return count;
    }

    private void printOrderedByClusterCoefficient() {
        var list = new ArrayList<>(components.values());
        list.sort(Comparator.comparingDouble(Component::getCoefficient));
        for (Component component : list) {
            System.out.println(component.name+" - "+component.getCoefficient());
        }
    }

    private void toDot() throws IOException {
        var out = new StringBuilder();
        out.append("graph {\n");
        for (var c : components.values()) {
            for (var o : c.connections) {
                out.append(c.name + " -- " + o.name + ";\n");
            }
        }
        out.append("}\n");
        Files.write(Paths.get("day25.dot"), out.toString().getBytes(StandardCharsets.UTF_8));
    }


    private void printGraph() {
        for (var comp : components.values()) {
            System.out.println(comp.name+" has coefficient "+comp.getCoefficient()+" number connections "+comp.connections.size());
        }
    }

    private void calculateClusteringCoefficient() {
        for (var component : components.values()) {
            component.countTriangles();
        }
    }

    private void toGraph(List<String> lines) {
        for (var line : lines) {
            var names = line.split(" ");
            var headName = names[0].replace(":","");
            var head = toComponent(headName);
            for (int i = 1; i < names.length; i++) {
                var component = toComponent(names[i]);
                head.connect(component);
                component.connect(head);
                connections.add(new Connection(head, component));
                connections.add(new Connection(component, head));
            }
        }

        System.out.println("Created components : "+components.size());
        System.out.println("Created connections: "+connections.size());
    }

    private Component toComponent(String name) {
        if(components.containsKey(name)) return components.get(name);

        var component = new Component(name);
        components.put(name, component);
        return component;
    }

    private static class Component implements Comparable<Component> {
        final String name;
        final Set<Component> connections = new HashSet<>();
        final Set<Component> connectionsNotInTriangles = new HashSet<>();
        int triangleCount = 0;

        public Component(String name) {
            this.name = name;
        }

        public void connect(Component component) {
            connections.add(component);
        }

        private boolean isConnected(Component component) {
            return connections.contains(component);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Component component = (Component) o;
            return Objects.equals(name, component.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        public void countTriangles() {
            triangleCount = 0;
            connectionsNotInTriangles.clear();
            connectionsNotInTriangles.addAll(connections);

            if(connections.size() <= 1) return;

            var list = new ArrayList<>(connections);
            for (int i = 0; i < list.size() - 1; i++) {
                for (int k = i+1; k < list.size(); k++) {
                    final var a = list.get(i);
                    final var b = list.get(k);
                    if (a.isConnected(b)) {
                        triangleCount++;
                        connectionsNotInTriangles.remove(a);
                        connectionsNotInTriangles.remove(b);
                    }
                }
            }
        }

        @Override
        public String toString() {
            return name;
        }

        public double getCoefficient() {
            final double k = connections.size();
            return (2 * triangleCount)/(k * (k - 1));
        }

        @Override
        public int compareTo(Component o) {
            return Integer.compare(connections.size(), o.connections.size());
        }
    }

    record Connection(Component a, Component b) {}
}
