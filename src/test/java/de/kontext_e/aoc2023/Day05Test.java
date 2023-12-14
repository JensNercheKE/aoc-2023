package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;

class Day05Test {

    @Test
    void testCardsWithTest() throws IOException {
        Almanac almanac = Almanac.readInputFile("src/test/resources/day05test.txt");
        almanac.mapSeeds();

        var lowestLocationNumber = almanac.getLowestLocationNumber();

        assertEquals(35, lowestLocationNumber);

        assertEquals(79, almanac.mapReverse(82));
        assertEquals(14, almanac.mapReverse(43));
        assertEquals(55, almanac.mapReverse(86));
        assertEquals(13, almanac.mapReverse(35));

        assertFalse(almanac.isInSeedRange(78));
        assertTrue(almanac.isInSeedRange(79));
        assertTrue(almanac.isInSeedRange(92));
        assertFalse(almanac.isInSeedRange(93));

        assertFalse(almanac.isInSeedRange(54));
        assertTrue(almanac.isInSeedRange(55));
        assertTrue(almanac.isInSeedRange(67));
        assertFalse(almanac.isInSeedRange(68));

        for (int i = 0; i < 100; i++) {
            var candidate = almanac.mapReverse(i);
            if (almanac.isInSeedRange(candidate)) {
                break;
            }
        }
    }

    @Test
    void testCardsWithInput() throws IOException {
        Almanac almanac = Almanac.readInputFile("src/test/resources/day05input.txt");
        almanac.mapSeeds();

        var lowestLocationNumber = almanac.getLowestLocationNumber();

        assertEquals(282277027, lowestLocationNumber);

        long nearst = 0;
        for (long i = 0; i < 100000000; i++) {
            var candidate = almanac.mapReverse(i);
            if (almanac.isInSeedRange(candidate)) {
                nearst = i;
                break;
            }
        }
        assertEquals(11554135, nearst);
    }

    @Test
    void testCategoryMap() {
        CategoryMap map = new CategoryMap(States.IDLE);
        map.addRange(Range.parse("50 98 2"));
        map.addRange(Range.parse("52 50 48"));

        assertEquals(81, map.map(79));
        assertEquals(10, map.map(10));
        assertEquals(49, map.map(49));
        assertEquals(52, map.map(50));
        assertEquals(53, map.map(51));
        assertEquals(99, map.map(97));
        assertEquals(50, map.map(98));
        assertEquals(51, map.map(99));

        assertEquals(79, map.mapReverse(81));
        assertEquals(10, map.mapReverse(10));
        assertEquals(49, map.mapReverse(49));
        assertEquals(50, map.mapReverse(52));
        assertEquals(51, map.mapReverse(53));
        assertEquals(97, map.mapReverse(99));
        assertEquals(98, map.mapReverse(50));
        assertEquals(99, map.mapReverse(51));
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
        assertFalse(range.containsDestination(49));
        assertFalse(range.containsDestination(52));
        assertTrue(range.containsDestination(50));
        assertTrue(range.containsDestination(51));
    }

    @Test
    void testRangeMapToDestination() {
        var range = Range.parse("50 98 2");
        assertEquals(-1, range.map(97));
        assertEquals(50, range.map(98));
        assertEquals(51, range.map(99));
        assertEquals(-1, range.map(100));
        assertEquals(98, range.mapReverse(50));
        assertEquals(99, range.mapReverse(51));
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
        assertFalse(seedRange.contains(4));
        assertTrue(seedRange.contains(5));
        assertTrue(seedRange.contains(6));
        assertTrue(seedRange.contains(7));
        assertFalse(seedRange.contains(8));
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
            mapSeedRanges();
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

        public long mapReverse(long number) {
            var current = number;
            for (int i = categoryMaps.size() - 1; i >= 0; i--) {
                var map = categoryMaps.get(i);
                current = map.mapReverse(current);
            }
            return current;
        }

        private final List<SeedRange> seedRanges = new ArrayList<>();
        public void mapSeedRanges() {
            for (int i = 0; i < seeds.size(); i += 2) {
                seedRanges.add(new SeedRange((Long) seeds.get(i), (Long) seeds.get(i + 1)));
            }
        }
        public boolean isInSeedRange(long number) {
            for (SeedRange seedRange : seedRanges) {
                if(seedRange.contains(number)) return true;
            }

            return false;
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
    }

    private enum States{SEED_TO_SOIL, SOIL_TO_FERTILIZER, FERTILIZER_TO_WATER, WATER_TO_LIGHT, LIGHT_TO_TEMPERATURE, TEMPERATURE_TO_HUMIDITY, HUMIDITY_TO_LOCATION, IDLE}

    private static class CategoryMap {
        private final States category;
        private final List<Range> ranges = new ArrayList<>();

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

        public long mapReverse(long input) {
            for (Range range : ranges) {
                if (range.containsDestination(input)) {
                    return range.mapReverse(input);
                }
            }

            return input;
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
        public boolean containsDestination(long number) {
            return destinationRangeStart <= number && (destinationRangeStart + rangeLength) > number;
        }

        public long map(long number) {
            if(containsSource(number) == false) return -1;
            return number - sourceRangeStart + destinationRangeStart;
        }

        public long mapReverse(long number) {
            if(containsDestination(number) == false) return -1;
            return number - destinationRangeStart + sourceRangeStart;
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


    private static class SeedRange {
        private final long start;
        private final long length;

        public SeedRange(long start, long length) {
            this.start = start;
            this.length = length;
        }

        public boolean contains(long number) {
            return number >= start && number < (start + length);
        }
    }
}
