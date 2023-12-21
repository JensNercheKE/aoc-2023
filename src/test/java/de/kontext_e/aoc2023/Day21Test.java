package de.kontext_e.aoc2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day21Test {
    char[][] field;
    private int width;
    private int height;
    private int originalWidth;
    private int originalHeight;
    private int stepCount;
    int scale = 1;
    
    @BeforeEach
    void setUp() {
        width = 0;
        height = 0;
        originalHeight = 0;
        originalWidth = 0;
        stepCount = 0;
        scale = 1;
    }

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day21test.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        field = new char[width][height];
        toField(lines);

        for (int i = 0; i < 6; i++) {
            step();
        }
        var plots = countPlots();

        assertEquals(16, plots);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day21input.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        field = new char[width][height];
        toField(lines);

        for (int i = 0; i < 64; i++) {
            step();
        }
        var plots = countPlots();

        assertEquals(3751, plots);
    }

    /*
    Original:
 128  7567
 129  7568
 130  7567
 131  7568
 132  7567
 133  7568

Period found at 131: [7567, 7568, 7567, 7568]
leave north 65 0 66
leave west 0 65 66
leave east 130 65 66
leave south 65 130 66

3x3 copies:
=3*131
Period found at 393: [68107, 68108, 68107, 68108]
leave north 196 0 197 1*131+66
9,0005286110744020087220827276331

5x5 copies:
=5*131
Period found at 655: [189187, 189188, 189187, 189188]
leave north 327 0 328 2*131+66

7x7 copies:
=7*131
Period found at 917: [370807, 370808, 370807, 370808]
leave north 458 0 459 3*131+66

9x9 copies:
=9*131
Period found at 1179: []
leave north 589 0 590 4*131+66


404601x404601
=404601*131
Period found at 53.002.731: [, , , ]
leave north 26_501_365 0 26_501_366 202.300*131+66


(26_501_365 - 65) / 131 = 202300

scale  1:   65 steps  3867 plots

scale  3:  196 steps 34253 plots
    963     5718      947
   5724     7568     5710
    952     5716      955

scale  5:  327 steps 94909 plots
      0      963     5718      947        0
    963     6658     7568     6627      947
   5724     7568     7567     7568     5710
    952     6633     7568     6650      955
      0      952     5716      955        0

scale  7:  458 steps 185835 plots
      0        0      963     5718      947        0        0
      0      963     6658     7568     6627      947        0
    963     6658     7568     7567     7568     6627      947
   5724     7568     7567     7568     7567     7568     5710
    952     6633     7568     7567     7568     6650      955
      0      952     6633     7568     6650      955        0
      0        0      952     5716      955        0        0

scale 9:  589 steps 307031 plots

      0        0        0      963     5718      947        0        0         0
      0        0      963     6658     7568     6627
      0      963     6658     7568     7567     7568
    963     6658     7568     7567     7568     7567
   5724     7568     7567     7568     7567     7568     7567     7568     5710
    952     6633     7568     7567     7568     7567
      0      952     6633     7568     7567     7568
      0        0      952     6633     7568     6650
      0        0        0      952     5716      955


scale 11:  720 steps 458497 plots 9 seconds
      0        0        0        0      963     5718      947        0        0        0        0
      0        0        0      963     6658     7568     6627      947        0        0        0
      0        0      963     6658     7568     7567     7568     6627      947        0        0
      0      963     6658     7568     7567     7568     7567     7568     6627      947        0
    963     6658     7568     7567     7568     7567     7568     7567     7568     6627      947
   5724     7568     7567     7568     7567     7568     7567     7568     7567     7568     5710
    952     6633     7568     7567     7568     7567     7568     7567     7568     6650      955
      0      952     6633     7568     7567     7568     7567     7568     6650      955        0
      0        0      952     6633     7568     7567     7568     6650      955        0        0
      0        0        0      952     6633     7568     6650      955        0        0        0
      0        0        0        0      952     5716      955        0        0        0        0

row 0: starting X/2 - (1+0) 0s, then always 963     5718      947
row 1: starting X/2 - (1+1) 0s, then always 963     6658     7568     6627      947
row 2: starting x/2 - (1+2) 0s, then always 963     6658     7568, then X-6 times 7567 or 7568, then always 7568     6627      947

     */

    @Test
    void testConstructRow() {
        scale = 9;
        assertEquals("[963, 5718, 947]", constructRow(0).toString());
        assertEquals("[963, 6658, 7568, 6627, 947]", constructRow(1).toString());
        assertEquals("[963, 6658, 7568, 7567, 7568, 6627, 947]", constructRow(2).toString());
        assertEquals("[963, 6658, 7568, 7567, 7568, 7567, 7568, 6627, 947]", constructRow(3).toString());
        assertEquals("[5724, 7568, 7567, 7568, 7567, 7568, 7567, 7568, 5710]", constructRow(4).toString());
        assertEquals("[952, 6633, 7568, 7567, 7568, 7567, 7568, 6650, 955]", constructRow(5).toString());
        assertEquals("[952, 6633, 7568, 7567, 7568, 6650, 955]", constructRow(6).toString());
        assertEquals("[952, 6633, 7568, 6650, 955]", constructRow(7).toString());
        assertEquals("[952, 5716, 955]", constructRow(8).toString());
    }

    @Test
    void testCalculatePlotsWithConstructedRows() {
        //assertEquals(34253, calculatePlotsWithConstructedRows(3));
        assertEquals(94909, calculatePlotsWithConstructedRows(5));
        assertEquals(185835, calculatePlotsWithConstructedRows(7));
        assertEquals(307031, calculatePlotsWithConstructedRows(9));
        assertEquals(458497, calculatePlotsWithConstructedRows(11));
        assertEquals(619407349431167L, calculatePlotsWithConstructedRows(404601));
//        assertEquals(307031, calculatePlotsWithConstructedRows(404601));
    }

    private long calculatePlotsWithConstructedRows(int sle) {
        scale = sle;
        long plots = (963 + 5718 + 947) + (952 + 5716 + 955); // fixed first and last row
        long rowSum = 963 + 6658 + 7568 + 6627 + 947; // start with second from top
        plots += rowSum;
        final long additionalPerRow = 7567 + 7568;
        for (int i = 2; i < scale/2; i++) {
            rowSum += additionalPerRow;
            plots += rowSum;
        }

        // same from bottom
        rowSum = 952 + 6633 + 7568 + 6650 + 955;
        plots += rowSum;
        for (int i = 2; i < scale/2; i++) {
            rowSum += additionalPerRow;
            plots += rowSum;
        }

        // and middle row
        rowSum = 5724 + 7568 + 5710;
        rowSum += (scale/2 - 1) * additionalPerRow;
        plots += rowSum;

        return plots;
    }

    private List<Integer> constructRow(int row) {
        if(row == 0) return List.of(963, 5718, 947);
        if(row == scale-1) return List.of(952, 5716, 955);

        List<Integer> result = new ArrayList<>();
        if (row < scale / 2) {
            var first = (row%2 == 0) ? 7567 : 7568;
            result.add(first);
            var other = first == 7567 ? 7568 : 7567;
            for (int i = 0; i < row-1; i++) {
                result.add(0, other);
                result.add(other);
                other = other == 7567 ? 7568 : 7567;
            }
            result.add(0, 6658);
            result.add(0, 963);
            result.add(6627);
            result.add(947);
        }
        if (row == scale / 2) {
            var first = (row%2 == 0) ? 7567 : 7568;
            result.add(first);
            var other = first == 7567 ? 7568 : 7567;
            for (int i = 0; i < row-1; i++) {
                result.add(0, other);
                result.add(other);
                other = other == 7567 ? 7568 : 7567;
            }
            result.add(0, 5724);
            result.add(5710);
        }
        if (row > scale / 2) {
            var first = (row%2 == 0) ? 7567 : 7568;
            result.add(first);
            var other = first == 7567 ? 7568 : 7567;
            for (int i = 0; i < scale-row-2; i++) {
                result.add(0, other);
                result.add(other);
                other = other == 7567 ? 7568 : 7567;
            }
            result.add(0, 6633);
            result.add(0, 952);
            result.add(6650);
            result.add(955);
        }

        return result;
    }

    @Test
    void part2() throws IOException {
        Path path = Paths.get("src/test/resources/day21input.txt");
        var lines = Files.readAllLines(path);
        width = lines.get(0).length();
        height = lines.size();
        originalWidth = width;
        originalHeight = height;
        System.out.println("width: "+width+" height: "+height);

        scale = 11;
        int steps = scale/2 * 131 + 65;
        field = new char[width*scale][height*scale];
        toField(lines);

        for (int i = 1; i < scale; i++) {
            copyTo(width * i, 0);
        }

        for(int y = 1; y < scale; y++) {
            for (int x = 0; x < scale; x++) {
                copyTo(width * x, height*y);
            }
        }

        width *= scale;
        height *= scale;
        removeStartingPoints(scale);

        int[] lastFour = new int[4];
        int index = 0;
        for (int i = 0; i < steps; i++) {
            stepCount++;
            step();
            //final var plots = countPlots();
            //System.out.printf("%4d %5d\n", stepCount, plots);

/*
            index %= 4;
            lastFour[index] = plots;
            if (lastFour[0] == lastFour[2] && lastFour[1] == lastFour[3]) {
                System.out.println("Period found at "+i+": "+ Arrays.toString(lastFour));
                break;
            }
            index++;
*/
        }

        final var plots = countPlots();
        System.out.printf("scale %2d: %4d steps %5d plots\n", scale, stepCount, plots);

        countPlotsCopyWise();

    }

    private void removeStartingPoints(int scale) {
        var keepS = (scale)/2*scale + scale/2+1;
        System.out.println("keepS = "+keepS);
        var count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y] == 'S') {
                    count++;
                    if (count != keepS) {
                        field[x][y] = '.';
                    }
                }
            }
        }
    }

    private void copyTo(int newX, int newY) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                field[newX + x][newY+y] = field[x][y];
            }
        }
    }

    private int countPlots() {
        var count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y] == 'O') {
                    count++;
                }
            }
        }
        return count;
    }

    private int countPlotsCopyWise() {
        var copy = 0;
        for (int copyY = 0; copyY < scale; copyY++) {
            for (int copyX = 0; copyX < scale; copyX++) {
                copy++;
                int fromX = originalWidth * copyX;
                int fromY = originalHeight * copyY;
                int toX = originalWidth * (copyX+1);
                int toY = originalHeight * (copyY+1);
                var count = 0;
                for (int y = fromY; y < toY; y++) {
                    for (int x = fromX; x < toX; x++) {
                        if (field[x][y] == 'O') {
                            count++;
                        }
                    }
                }
                System.out.printf("  %5d  ", count);
            }
            System.out.println();
        }
        
        var count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y] == 'O') {
                    count++;
                }
            }
        }
        return count;
    }

    private void step() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y] == 'O' || field[x][y] == 'S') {
                    field[x][y] = '.';
                    north(x,y);
                    south(x,y);
                    east(x,y);
                    west(x,y);
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y] == 'X') {
                    field[x][y] = 'O';
                }
            }
        }
    }

    // leave north 65 0
    private void north(int x, int y) {
        if(y == 0) {
            System.out.println("leave north "+x+" "+y+" "+stepCount);
            throw new RuntimeException();
            //return;
        }
        if (field[x][y - 1] != '#') {
            field[x][y - 1] = 'X';
        }
    }

    // leave south 65 130 66 steps
    private void south(int x, int y) {
        if(y == height - 1) {
            System.out.println("leave south "+x+" "+y+" "+stepCount);
            throw new RuntimeException();
            //return;
        }
        if (field[x][y + 1] != '#') {
            field[x][y + 1] = 'X';
        }
    }

    // leave east 130 65, 66 steps
    private void east(int x, int y) {
        if(x == width - 1) {
            System.out.println("leave east "+x+" "+y+" "+stepCount);
            throw new RuntimeException();
            //return;
        }
        if (field[x + 1][y] != '#') {
            field[x + 1][y] = 'X';
        }
    }

    // leave west 0 65
    private void west(int x, int y) {
        if(x == 0) {
            System.out.println("leave west "+x+" "+y+" "+stepCount);
            return;
        }
        if (field[x - 1][y] != '#') {
            field[x - 1][y] = 'X';
        }
    }

    private void printField() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y] == 0) {
                    System.out.print('_');
                } else {
                    System.out.print(field[x][y]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private void toField(List<String> lines) {
        for (int y = 0; y < height; y++) {
            var line = lines.get(y);
            for (int x = 0; x < width; x++) {
                field[x][y] = line.charAt(x);
            }
        }
    }
}
