package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Day12Test {

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day12test.txt");
        var lines = Files.readAllLines(path);

        var sum = 0L;
        for (String line : lines) {
            sum += calculateVariants(line).size();
        }
        assertEquals(21, sum);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day12input.txt");
        var lines = Files.readAllLines(path);

        var sum = 0L;
        for (String line : lines) {
            System.out.println(line);
            sum += calculateVariants(line).size();
        }
        assertEquals(21, sum);
    }

    @Test
    void testOneLine() {
        final var variants = calculateVariants("???.### 1,1,3");
        assertEquals(1, variants.size());
        assertEquals(4, calculateVariants(".??..??...?##. 1,1,3").size());
        assertEquals(1, calculateVariants("?#?#?#?#?#?#?#? 1,3,1,6").size());
        assertEquals(10, calculateVariants("?###???????? 3,2,1").size());
    }

    private List<String> calculateVariants(String line) {
        var conditionRecord = line.substring(0, line.indexOf(" "));
        var checksum = line.substring(line.indexOf(" ")+1);
        return variantsOf(conditionRecord, checksum);
    }

    private List<String> variantsOf(String conditionRecord, String checksum) {
        List<String> variants = new ArrayList<>();
        char[] replacements = new char[count('?', conditionRecord)];
        Arrays.fill(replacements, '.');

        while(true) {
            final var variant = createVariant(replacements, conditionRecord);
            if(isPossible(variant, checksum)) {
                variants.add(variant);
            }
            boolean done = nextReplacement(replacements);
            if (done == true) return variants;
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

    private int count(char c, String conditionRecord) {
        var count = 0;
        for (int i = 0; i < conditionRecord.length(); i++) {
            if (conditionRecord.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}
