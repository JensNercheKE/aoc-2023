package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class Day05Test {

    @Test
    void testCardsWithTest() throws IOException {
        Almanac almanac = Almanac.readInputFile("src/test/resources/day05test.txt");
        almanac.mapSeeds();

        var lowestLocationNumber = almanac.getLowestLocationNumber();
        var lowestLocationRange = almanac.getLowestLocationRange();

        assertEquals(35, lowestLocationNumber);
        System.out.println(lowestLocationRange);
    }

    @Test
    void testCardsWithInput() throws IOException {
        Almanac almanac = Almanac.readInputFile("src/test/resources/day05input.txt");
        almanac.mapSeeds();

        var lowestLocationNumber = almanac.getLowestLocationNumber();

        assertEquals(282277027, lowestLocationNumber);
    }

    @Test
    void testCategoryMap() {
        CategoryMap map = new CategoryMap(States.IDLE);
        map.addRange(Range.parse("50 98 2"));
        map.addRange(Range.parse("52 50 48"));

        var converted = map.map(79);

        assertEquals(81, converted);
        assertEquals(10, map.map(10));
        assertEquals(49, map.map(49));
        assertEquals(52, map.map(50));
        assertEquals(53, map.map(51));
        assertEquals(99, map.map(97));
        assertEquals(50, map.map(98));
        assertEquals(51, map.map(99));
    }

    @Test
    void testParseRange() {
        var range = Range.parse("50 98 2");
        assertEquals(50, range.getDestinationRangeStart());
        assertEquals(98, range.getSourceRangeStart());
        assertEquals(2, range.getRangeLength());
    }

    @Test
    void testRangeContainsSource() {
        var range = Range.parse("50 98 2");

        assertFalse(range.containsSource(97));
        assertTrue(range.containsSource(98));
        assertTrue(range.containsSource(99));
        assertFalse(range.containsSource(100));
    }

    @Test
    void testRangeMapToDestination() {
        var range = Range.parse("50 98 2");
        assertEquals(-1, range.map(97));
        assertEquals(50, range.map(98));
        assertEquals(51, range.map(99));
        assertEquals(-1, range.map(100));
    }

    @Test
    void testParseSeeds() {
        Almanac almanac = new Almanac();

        almanac.parseSeeds("seeds: 79 14 55 13");

        assertEquals(4, almanac.getSeeds().size());
    }

    @Test
    void testSeedRange() {
        SeedRange seedRange = new SeedRange(5, 3);
        for (var i : seedRange) {
            System.out.println(i);
        }
    }

    private static class Almanac {
        private final List<Number> seeds = new LinkedList<>();
        private States state = States.IDLE;
        private final List<CategoryMap> categoryMaps = new ArrayList<>();

        private final List<Long> locations = new ArrayList<>();

        public Almanac() {
            categoryMaps.add(new CategoryMap(States.SEED_TO_SOIL));
            categoryMaps.add(new CategoryMap(States.SOIL_TO_FERTILIZER));
            categoryMaps.add(new CategoryMap(States.FERTILIZER_TO_WATER));
            categoryMaps.add(new CategoryMap(States.WATER_TO_LIGHT));
            categoryMaps.add(new CategoryMap(States.LIGHT_TO_TEMPERATURE));
            categoryMaps.add(new CategoryMap(States.TEMPERATURE_TO_HUMIDITY));
            categoryMaps.add(new CategoryMap(States.HUMIDITY_TO_LOCATION));
        }

        CategoryMap getCurrentCategory() {
            for (CategoryMap categoryMap : categoryMaps) {
                if (categoryMap.is(state)) {
                    return categoryMap;
                }
            }

            return new CategoryMap(States.IDLE);
        }

        @Override
        public String toString() {
            return "Almanac: seeds = " + seeds;
        }

        public static Almanac readInputFile(String pathString) throws IOException {
            Almanac almanac = new Almanac();

            Path path = Paths.get(pathString);
            var lines = Files.readAllLines(path);
            for (String line : lines) {
                almanac.parse(line);
            }

            return almanac;
        }

        private void parse(String line) {
            if (line != null && line.startsWith("seeds:")) {
                parseSeeds(line);
                state = States.IDLE;
            }
            if (line != null && line.isBlank()) {
                state = States.IDLE;
            }

            if (States.IDLE.equals(state) == false) {
                parseRange(line);
            }

            if (line != null && line.startsWith("seed-to-soil map:")) {
                state = States.SEED_TO_SOIL;
            }
            if (line != null && line.startsWith("soil-to-fertilizer map:")) {
                state = States.SOIL_TO_FERTILIZER;
            }
            if (line != null && line.startsWith("fertilizer-to-water map:")) {
                state = States.FERTILIZER_TO_WATER;
            }
            if (line != null && line.startsWith("water-to-light map:")) {
                state = States.WATER_TO_LIGHT;
            }
            if (line != null && line.startsWith("light-to-temperature map:")) {
                state = States.LIGHT_TO_TEMPERATURE;
            }
            if (line != null && line.startsWith("temperature-to-humidity map:")) {
                state = States.TEMPERATURE_TO_HUMIDITY;
            }
            if (line != null && line.startsWith("humidity-to-location map:")) {
                state = States.HUMIDITY_TO_LOCATION;
            }
        }

        private void parseSeeds(String line) {
            var numberPart = line.substring("seeds:".length() + 1).trim();
            var numberStrings = numberPart.split(" ");
            for (String numberString : numberStrings) {
                if (numberString.isEmpty() == false) {
                    seeds.add(Long.parseLong(numberString));
                }
            }
        }

        private void parseRange(String line) {
            var range = Range.parse(line);
            getCurrentCategory().addRange(range);
        }

        public List<Number> getSeeds() {
            return seeds;
        }

        public void mapSeeds() {
            for (Number seed : seeds) {
                long converted = seed.longValue();
                for (CategoryMap categoryMap : categoryMaps) {
                    converted = categoryMap.map(converted);
                }
                locations.add(converted);
            }
        }

        public void mapSeedRanges() {
            for (int i = 0; i < seeds.size(); i += 2) {
                //SeedRange seedRange = new SeedRange(seeds.get(i), seeds.get(i+1));
            }
        }
        public long getLowestLocationNumber() {
            long lowest = Long.MAX_VALUE;
            for (var location : locations) {
                if (location < lowest) {
                    lowest = location;
                }
            }

            return lowest;
        }

        public Range getLowestLocationRange() {
            for (CategoryMap categoryMap : categoryMaps) {
                if (categoryMap.is(States.HUMIDITY_TO_LOCATION)) {
                    return categoryMap.getLowestRange();
                }
            }

            return new Range();
        }
    }

    private enum States{SEED_TO_SOIL, SOIL_TO_FERTILIZER, FERTILIZER_TO_WATER, WATER_TO_LIGHT, LIGHT_TO_TEMPERATURE, TEMPERATURE_TO_HUMIDITY, HUMIDITY_TO_LOCATION, IDLE}

    private static class CategoryMap {
        private States category;
        private List<Range> ranges = new ArrayList<>();

        public CategoryMap(States category) {
            this.category = category;
        }

        public void addRange(Range range) {
            ranges.add(range);
        }

        public boolean is(States state) {
            return category.equals(state);
        }

        public long map(long input) {
            for (Range range : ranges) {
                if (range.containsSource(input)) {
                    return range.map(input);
                }
            }

            return input;
        }

        public Range getLowestRange() {
            var lowestRange = new Range();

            for (Range range : ranges) {
                if (range.isLower(lowestRange)) {
                    lowestRange = range;
                }
            }

            return lowestRange;
        }
    }

    private static class Range {
        private long destinationRangeStart = Long.MAX_VALUE;
        private long sourceRangeStart = Long.MAX_VALUE;
        private long rangeLength = Long.MAX_VALUE;

        public static Range parse(String line) {
            Range range = new Range();
            var numberStrings = line.split(" ");
            if (numberStrings.length == 3) {
                range.destinationRangeStart = Long.parseLong(numberStrings[0]);
                range.sourceRangeStart = Long.parseLong(numberStrings[1]);
                range.rangeLength = Long.parseLong(numberStrings[2]);
            } else {
                throw new RuntimeException("Parsing range went wrong: " + Arrays.toString(numberStrings));
            }
            return range;
        }

        public long getDestinationRangeStart() {
            return destinationRangeStart;
        }

        public long getSourceRangeStart() {
            return sourceRangeStart;
        }

        public long getRangeLength() {
            return rangeLength;
        }

        public boolean containsSource(long number) {
            return sourceRangeStart <= number && (sourceRangeStart + rangeLength) > number;
        }
        public long map(long number) {
            if(containsSource(number) == false) return -1;
            return number - sourceRangeStart + destinationRangeStart;
        }

        public boolean isLower(Range other) {
            return destinationRangeStart < other.destinationRangeStart;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Range.class.getSimpleName() + "[", "]")
                    .add("destinationRangeStart=" + destinationRangeStart)
                    .add("sourceRangeStart=" + sourceRangeStart)
                    .add("rangeLength=" + rangeLength)
                    .toString();
        }
    }


    private static class SeedRange implements Iterable<Long> {
        private final long start;
        private final long length;

        public SeedRange(long start, long length) {
            this.start = start;
            this.length = length;
        }

        @Override
        public Iterator<Long> iterator() {
            return new SeedRangeIterator(start);
        }

        @Override
        public void forEach(Consumer<? super Long> action) {
            Iterable.super.forEach(action);
        }

        @Override
        public Spliterator<Long> spliterator() {
            return Iterable.super.spliterator();
        }

        private class SeedRangeIterator implements Iterator<Long> {
            private long current;

            public SeedRangeIterator(long start) {
                current = start;
            }

            @Override
            public boolean hasNext() {
                return current < (start + length);
            }

            @Override
            public Long next() {
                return current++;
            }
        }
    }
}
