package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day13Test {
    int delimiter = 0;
    int patterns = 0;
    List<Long> summands = new ArrayList<>();
    boolean smudgeMode = false;

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day13test.txt");
        var lines = Files.readAllLines(path);

        var sum = calculateSum(lines);

        assertEquals(405, sum);
        assertEquals(2, patterns);

        // second part, start from beginning again
        delimiter = 0;
        patterns = 0;
        smudgeMode = true;
        sum = calculateSum(lines);
        assertEquals(400, sum);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day13input.txt");
        var lines = Files.readAllLines(path);

        var sum = calculateSum(lines);

        assertEquals(34772, sum);
        assertEquals(100, patterns);

        // second part, start from beginning again
        delimiter = 0;
        patterns = 0;
        smudgeMode = true;
        sum = calculateSum(lines);
        assertEquals(35554, sum); // too low
    }

    private long calculateSum(List<String> lines) {
        long sum = 0;
        while (true)
        {
            var start = delimiter;
            findNextDelimiter(lines);
            var end = delimiter;
            final var summand = findLineOfReflection(start, end, lines);
            sum += summand;
            if(smudgeMode == false) {
                summands.add((long) summand);
                //System.out.println(summand);
            }
            patterns++;

            delimiter++;
            delimiter++;
            if(delimiter >= lines.size()) break;
        }
        return sum;
    }

    private int findLineOfReflection(int start, int end, List<String> linesFromFile) {
        var summand = 0;
        var width = linesFromFile.get(start).length();
        var height = end - start + 1;

        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var lines = new ArrayList<StringWrapper>();
                for (int i = start; i <= end; i++) {
                    lines.add(new StringWrapper(linesFromFile.get(i)));
                }
                if (smudgeMode == true) {
                    var wrapped = lines.get(y);
                    final var c = wrapped.charAt(x) == '.' ? '#' : '.';
                    wrapped.replace(x, c);
                }

                summand = findLineOfReflectionInVariant(lines);

                if (smudgeMode == true) {
                    if(summand != 0) {
                        if (summand == summands.get(patterns)) {
                            //System.out.println("same reflection line");
                        } else {
                            //System.out.printf("%d new reflection line %4d x = %2d y = %2d old %4d\n", patterns, summand, x, y, summands.get(patterns));
                            return summand;
                        }
                    }
                } else {
                    return summand;
                }
            }
        }

        if (smudgeMode) {
            //System.out.println("WARNING: No new reflection line found ");
            return 0;
        }
        return summand;
    }

    private int findLineOfReflectionInVariant(ArrayList<StringWrapper> lines) {
        var start = 0;
        var end = lines.size() - 1;

        var candidate = lines.get(start);
        for (int i = end; i > start; i--) {
            final var line = lines.get(i);
            if (line.equals(candidate)) {
                boolean symmetric = checkHorizontalLineOfReflection(start, i, lines);
                if (symmetric == true) {
                    final var patternHight = (i - start) + 1;
                    final var summand = (patternHight / 2) * 100;
                    if(smudgeMode == false || (smudgeMode == true && summand != summands.get(patterns) && patternHight % 2 == 0)) {
                        return summand;
                    }
                }
            }
        }

        candidate = lines.get(end);
        for (int i = start; i < end; i++) {
            if (lines.get(i).equals(candidate)) {
                boolean symmetric = checkHorizontalLineOfReflection(i, end, lines);
                if (symmetric == true) {
                    final var firstIndexOfPattern = i - start;
                    final var patternHight = (end - i) + 1;
                    final var summand = (patternHight / 2 + firstIndexOfPattern) * 100;
                    if(smudgeMode == false || (smudgeMode == true && summand != summands.get(patterns) && patternHight % 2 == 0)) {
                        return summand;
                    }
                }
            }
        }

        int width = lines.get(start).length();

        candidate = column(0, start, end, lines);
        for (int i = width - 1; i > 0; i--) {
            var col = column(i, start, end, lines);
            if (col.equals(candidate)) {
                boolean symmetric = checkVerticalLineOfReflection(0, i, start, end, lines);
                if (symmetric == true) {
                    final var patternWidth = i + 1;
                    final var summand = patternWidth / 2;
                    if(smudgeMode == false || (smudgeMode == true && summand != summands.get(patterns) && patternWidth % 2 == 0)) {
                        return summand;
                    }
                }
            }
        }
        candidate = column(width-1, start, end, lines);
        for (int i = 0; i < width-1; i++) {
            var col = column(i, start, end, lines);
            if (col.equals(candidate)) {
                boolean symmetric = checkVerticalLineOfReflection(i, width-1, start, end, lines);
                if (symmetric == true) {
                    final var patternWidth = width - i;
                    final var summand = patternWidth / 2 + i;
                    if(smudgeMode == false || (smudgeMode == true && summand != summands.get(patterns) && patternWidth % 2 == 0)) {
                        return summand;
                    }
                }
            }
        }

        return 0;
    }

    private boolean checkVerticalLineOfReflection(int colStart, int colEnd, int start, int end, List<StringWrapper> lines) {
        for(int i = 0; i < (colEnd - colStart)/2+1; i++) {
            final var col1 = column(colStart + i, start, end, lines);
            final var col2 = column(colEnd - i, start, end, lines);
            if (col1.equals(col2) == false) return false;
        }
        return true;
    }

    private boolean checkHorizontalLineOfReflection(int start, int end, List<StringWrapper> lines) {
        for (int i = 0; i < ((end - start) / 2)+1; i++) {
            final var line1 = lines.get(start + i);
            final var line2 = lines.get(end - i);
            if(line1.equals(line2) == false) return false;
        }
        return true;
    }

    private StringWrapper column(int colNo, int start, int end, List<StringWrapper> lines) {
        String column = "";
        for (int i = start; i <= end; i++) {
            column += lines.get(i).charAt(colNo);
        }
        return new StringWrapper(column);
    }

    private void findNextDelimiter(List<String> lines) {
        for (int i = delimiter+1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                delimiter = i - 1;
                return;
            }
        }
        delimiter = lines.size() - 1;
    }
}
