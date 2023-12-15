package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day12Test {

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day12test.txt");
        var lines = Files.readAllLines(path);

        var sum = 0L;
        long result = 0L;
        for (String line : lines) {
            sum += shiftFit(line);
            result += shiftFit(fullunfold(line));
        }
        assertEquals(21, sum);
        assertEquals(525152, result);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day12input.txt");
        var lines = Files.readAllLines(path);

        var sum = 0L;
        long result = 0L;
        for (String line : lines) {
            final var shiftFitted = shiftFit(line);
            sum += shiftFitted;
            result += shiftFit(fullunfold(line));
        }
        assertEquals(7286, sum);
        assertEquals(25470469710341L, result);
    }

    @Test
    void testFullUnfold() {
        var line = "???.### 1,1,3";

        var unfolded = fullunfold(line);

        assertEquals("???.###????.###????.###????.###????.### 1,1,3,1,1,3,1,1,3,1,1,3,1,1,3", unfolded);
    }

    @Test
    void testShiftFit() {
        assertEquals(1, shiftFit("???.### 1,1,3"));
        assertEquals(4, shiftFit(".??..??...?##. 1,1,3"));
        assertEquals(1, shiftFit("?#?#?#?#?#?#?#? 1,3,1,6"));
        assertEquals(1, shiftFit("????.#...#... 4,1,1"));
        assertEquals(4, shiftFit("????.######..#####. 1,6,5"));
        assertEquals(10, shiftFit("?###???????? 3,2,1"));

        assertEquals(1, shiftFit(fullunfold("???.### 1,1,3")));
        assertEquals(16384, shiftFit(fullunfold(".??..??...?##. 1,1,3")));
        assertEquals(1, shiftFit(fullunfold("?#?#?#?#?#?#?#? 1,3,1,6")));
        assertEquals(16, shiftFit(fullunfold("????.#...#... 4,1,1")));
        assertEquals(2500, shiftFit(fullunfold("????.######..#####. 1,6,5")));
        assertEquals(506250, shiftFit(fullunfold("?###???????? 3,2,1")));

        assertEquals(12, calculateVariants(".?.?.????#???????.?. 1,1,1,7,1"));
        assertEquals(1181544648, shiftFit(fullunfold(".?.?.????#???????.?. 1,1,1,7,1")));

        assertEquals(5, calculateVariants(".???.???????????#?. 2,10"));
        assertEquals(5, shiftFit(".???.???????????#?. 2,10"));

        assertEquals(6, calculateVariants("?????????#? 1,4,1"));
        assertEquals(6, shiftFit("?????????#? 1,4,1"));
    }

    private long shiftFit(String line) {
        var conditionRecord = line.substring(0, line.indexOf(" "));
        var checksum = line.substring(line.indexOf(" ")+1);
        var parts = prepareParts(checksum);
        cache.clear();
        return fit(conditionRecord, parts);
    }

    private final Map<String, Long> cache = new HashMap<>();
    private long fit(String conditionRecord, List<String> parts) {
        long counter = 0;
        if(parts.isEmpty()) return counter;
        if(conditionRecord.isEmpty()) return counter;

        StringBuilder part = new StringBuilder(parts.get(0));

        while(part.length() <= conditionRecord.length()) {
            var tmp = part.toString();
            if (parts.size() == 1) {
                // last part, need to fill up with '.'
                final var crLength = conditionRecord.length();
                while (tmp.length() < crLength) {
                    tmp = tmp + '.';
                }
            }
            if (matches(conditionRecord, tmp)) {
                var remainingList = remaining(parts);
                if(remainingList.isEmpty() == false) {
                    var rest = conditionRecord.substring(part.length());
                    var cached = cache.get(rest + remainingList);
                    var subResult = 0L;
                    if(cached != null) {
                        subResult = cached;
                    } else {
                        subResult = fit(rest, remainingList);
                        cache.put(rest + remainingList, subResult);
                    }
                    counter += subResult;
                    part.insert(0, ".");
                } else {
                    part.insert(0, ".");
                    counter++;
                }
            } else {
                part.insert(0, ".");
            }
        }

        return counter;
    }

    private List<String> remaining(List<String> list) {
        var result = new ArrayList<String>();
        for (int i = 1; i < list.size(); i++) {
            result.add(list.get(i));
        }
        return result;
    }

    private boolean matches(String conditionRecord, String part) {
        if(part.length() > conditionRecord.length()) return false;

        for (int i = 0; i < part.length(); i++) {
            var crc = conditionRecord.charAt(i);
            var pc = part.charAt(i);
            if (crc != pc && crc != '?') {
                return false;
            }
        }

        return true;
    }

    private ArrayList<String> prepareParts(String checksum) {
        var parts = new ArrayList<String>();
        var lengths = toLongs(checksum);
        for (int i = 0; i < lengths.size(); i++) {
            var len = lengths.get(i);
            var part = "";
            for (int l = 0; l < len; l++) {
                part += "#";
            }
            if (i != lengths.size() - 1) {
                part += ".";
            }
            parts.add(part);
        }
        return parts;
    }

    private String fullunfold(String line) {
        var conditionRecord = line.substring(0, line.indexOf(" "));
        var checksum = line.substring(line.indexOf(" ")+1);

        return conditionRecord+"?"+conditionRecord+"?"+conditionRecord+"?"+conditionRecord+"?"+conditionRecord
                +" "
                +checksum+","+checksum+","+checksum+","+checksum+","+checksum;
    }

    private long calculateVariants(String line) {
        var conditionRecord = line.substring(0, line.indexOf(" "));
        var checksum = line.substring(line.indexOf(" ")+1);
        return variantsOf(conditionRecord, checksum);
    }

    private long variantsOf(String conditionRecord, String checksum) {
        long vars = 0L;
        char[] replacements = new char[count(conditionRecord)];
        Arrays.fill(replacements, '.');

        while(true) {
            final var variant = createVariant(replacements, conditionRecord);
            if(isPossible(variant, checksum)) {
                vars++;
            }
            boolean done = nextReplacement(replacements);
            if (done == true) return vars;
        }
    }

    private boolean isPossible(String variant, String checksum) {
        var groups = toLongs(checksum);
        var rest = variant;

        try {
            for (Long group : groups) {
                rest = eat(rest, group);
            }
        } catch (Exception e) {
            return false;
        }

        for (int i = 0; i < rest.length(); i++) {
            if (rest.charAt(i) == '#') {
                return false;
            }
        }

        return true;
    }

    private String eat(String rest, Long group) {
        boolean counting = false;
        int count = 0;
        for (int i = 0; i < rest.length(); i++) {
            if (rest.charAt(i) == '#') {
                counting = true;
                count++;
            }
            if (rest.charAt(i) == '.' && counting) {
                if (count == group) {
                    return rest.substring(i);
                } else {
                    throw new IllegalArgumentException("Rest does not contain group.");
                }
            }
        }

        if (count == group) {
            return "";
        } else {
            throw new IllegalArgumentException("Rest does not contain group.");
        }
    }

    List<Long> toLongs(String s) {
        return Arrays.stream(s.trim().split(",")).map(Long::valueOf).toList();
    }

    private boolean nextReplacement(char[] replacements) {
        for (int i = 0; i < replacements.length; i++) {
            boolean overrun = false;
            if (replacements[i] == '#') {
                overrun = true;
                replacements[i] = '.';
            } else {
                replacements[i] = '#';
            }
            if (overrun == false) {
                return false;
            }
        }
        return true;
    }


    private String createVariant(char[] replacements, String conditionRecord) {
        String variant = "";
        int index = 0;
        for (int i = 0; i < conditionRecord.length(); i++) {
            if (conditionRecord.charAt(i) == '?') {
                variant += replacements[index];
                index++;
            } else {
                variant += conditionRecord.charAt(i);
            }
        }
        return variant;
    }

    private int count(final String conditionRecord) {
        var count = 0;
        for (int i = 0; i < conditionRecord.length(); i++) {
            if (conditionRecord.charAt(i) == '?') {
                count++;
            }
        }
        return count;
    }
}
