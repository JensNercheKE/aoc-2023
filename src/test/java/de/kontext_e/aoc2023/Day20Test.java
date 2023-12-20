package de.kontext_e.aoc2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static de.kontext_e.aoc2023.Day08Test.ggt;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day20Test {
    private static final Module defaultModule = new DefaultModule();

    private final List<Module> modules = new ArrayList<>();
    private final List<Pulse> pulsesBacklog = new LinkedList<>();
    long highCounter = 0;
    long lowCounter = 0;
    boolean rxReceivedLow = false;

    @BeforeEach
    void setUp() {
        modules.clear();
        pulsesBacklog.clear();
        highCounter = 0;
        lowCounter = 0;
        rxReceivedLow = false;
    }

    @Test
    void intro1() throws IOException {
        Path path = Paths.get("src/test/resources/day20test1.txt");
        var lines = Files.readAllLines(path);
        toModules(lines);

        for(int i = 0; i < 1000; i++) {
            pushButton();
        }

        assertEquals(8000, lowCounter);
        assertEquals(4000, highCounter);
    }

    @Test
    void intro2() throws IOException {
        Path path = Paths.get("src/test/resources/day20test2.txt");
        var lines = Files.readAllLines(path);
        toModules(lines);

        for(int i = 0; i < 1000; i++) {
            pushButton();
        }

        assertEquals(4250, lowCounter);
        assertEquals(2750, highCounter);
        final var result = lowCounter * highCounter;
        assertEquals(11687500L, result);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day20input.txt");
        var lines = Files.readAllLines(path);
        toModules(lines);

        for(int i = 0; i < 1000; i++) {
            pushButton();
        }

        assertEquals(16908, lowCounter);
        assertEquals(43222, highCounter);
        final var result = lowCounter * highCounter;
        assertEquals(730797576L, result);
    }

    @Test
    void testkgv() {
        assertEquals(226_732_077_152_351L, kgv());
    }

    @Test @Disabled("Modify input according to tick() method")
    void inputPart2() throws IOException {
        Path path = Paths.get("src/test/resources/day20input.txt");
        var lines = Files.readAllLines(path);
        toModules(lines);

        long counter = 0;
        while(rxReceivedLow == false) {
            counter++;
            pushButton();
        }

        System.out.println("Counter: "+counter);
    }

    private void toModules(List<String> lines) {
        // first pass: create modules
        for (String line : lines) {
            createModule(line);
        }

        // second pass: Conjunction modules need to know _each_ connected input module
        for (String line : lines) {
            addInput(line);
        }
    }

    private void addInput(String line) {
        var source = line.substring(0, line.indexOf("->")).trim();
        if (source.startsWith("%")) {
            source = source.substring(1);
        }
        if (source.startsWith("&")) {
            source = source.substring(1);
        }
        var targets = line.substring(line.indexOf("->") + 2).trim();
        var targetNames = Arrays.stream(targets.split(",")).map(String::trim).toList();

        for (String targetName : targetNames) {
            findModule(targetName).addSource(source);
        }

    }

    private void createModule(String line) {
        var targets = line.substring(line.indexOf("->") + 2).trim();
        var targetNames = Arrays.stream(targets.split(",")).map(String::trim).toList();
        if (line.startsWith("broadcaster")) {
            modules.add(new Broadcaster(targetNames));
        } else if (line.startsWith("%")) {
            var name = line.substring(1, line.indexOf("->")).trim();
            modules.add(new FlipFlop(name, targetNames));
        } else if (line.startsWith("&")) {
            var name = line.substring(1, line.indexOf("->")).trim();
            modules.add(new Conjunction(name, targetNames));
        } else {
            throw new IllegalArgumentException("Unknown module type: " + line);
        }
    }

    private void pushButton() {
        pulsesBacklog.add(new Pulse("button", "low", "broadcaster"));

        while(pulsesBacklog.isEmpty() == false) {
            tick();
        }
    }

    private long kgv() {
        var nodeCounts = new ArrayList<Integer>();
        nodeCounts.add(3793);
        nodeCounts.add(3733);
        nodeCounts.add(3947);
        nodeCounts.add(4057);
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

    private void tick() {
        var pulsesToProcess = new ArrayList<>(pulsesBacklog);
        pulsesBacklog.clear();
        for (Pulse pulse : pulsesToProcess) {
            if ("low".equalsIgnoreCase(pulse.highLow)) {
                lowCounter++;
            }
            if ("high".equalsIgnoreCase(pulse.highLow)) {
                highCounter++;
            }
            if ("rxqx".equalsIgnoreCase(pulse.target)) {
                if("high".equalsIgnoreCase(pulse.highLow)) {
                    rxReceivedLow = true;
                }
            }
            processPulse(pulse);
        }
    }

    private void processPulse(Pulse pulse) {
        for (Module module : modules) {
            if (module.hasName(pulse.target)) {
                module.receive(pulse.source, pulse.highLow);
                return;
            }
        }
    }

    private Module findModule(String name) {
        for (Module module : modules) {
            if (module.hasName(name)) {
                return module;
            }
        }
        return defaultModule;
    }

    private interface Module {
        boolean hasName(String name);
        void receive(String source, String highLow);

        default void addSource(String source){}
    }
    private abstract static class AbstractModule implements Module {
        String name;
        List<String> targets = new LinkedList<>();

        public AbstractModule(String name, List<String> targets) {
            this.name = name;
            this.targets.addAll(targets);
        }

        @Override
        public boolean hasName(String requestedName) {
            return name.equalsIgnoreCase(requestedName);
        }
    }

    private class Broadcaster extends AbstractModule {

        public Broadcaster(List<String> targets) {
            super("broadcaster", targets);
        }

        @Override
        public void receive(String source, String highLow) {
            for (String target : targets) {
                pulsesBacklog.add(new Pulse(name, highLow, target));
            }
        }
    }

    private class FlipFlop extends AbstractModule {
        boolean state = false;

        public FlipFlop(String name, List<String> targets) {
            super(name, targets);
        }

        @Override
        public void receive(String source, String highLow) {
            // high pulses are ignored
            if ("low".equalsIgnoreCase(highLow)) {
                if (state == false) {
                    for (String target : targets) {
                        pulsesBacklog.add(new Pulse(name, "high", target));
                    }
                    state = true;
                } else {
                    for (String target : targets) {
                        pulsesBacklog.add(new Pulse(name, "low", target));
                    }
                    state = false;
                }
            }
        }
    }

    private class Conjunction extends AbstractModule {
        Map<String, String> nameTypeMap = new HashMap<>();

        public Conjunction(String name, List<String> targets) {
            super(name, targets);
        }

        @Override
        public void addSource(String name) {
            nameTypeMap.put(name, "low");
        }

        @Override
        public void receive(String source, String highLow) {
            nameTypeMap.put(source, highLow);
            if (allInputsHigh()) {
                for (String target : targets) {
                    pulsesBacklog.add(new Pulse(name, "low", target));
                }
            } else {
                for (String target : targets) {
                    pulsesBacklog.add(new Pulse(name, "high", target));
                }
            }
        }

        private boolean allInputsHigh() {
            for (String value : nameTypeMap.values()) {
                if ("low".equalsIgnoreCase(value)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class DefaultModule extends AbstractModule {

        public DefaultModule() {
            super("default", Collections.emptyList());
        }

        @Override
        public void receive(String source, String highLow) {

        }
    }

    private record Pulse(String source, String highLow, String target) {
        @Override
            public String toString() {
                return source + " -" + highLow + "-> " + target;
            }
        }
}
