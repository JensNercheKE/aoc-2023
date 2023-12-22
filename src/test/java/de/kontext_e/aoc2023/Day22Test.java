package de.kontext_e.aoc2023;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class Day22Test {
    private List<Brick> bricks = new ArrayList<>();
    private Set<Brick> fallenBricks = new HashSet<>();

    @BeforeEach
    void setUp() {
        bricks.clear();
        fallenBricks.clear();
    }

    @Test
    void intro() throws IOException {
        Path path = Paths.get("src/test/resources/day22test.txt");
        var lines = Files.readAllLines(path);
        bricks.addAll(toBricks(lines));
        bricks = bricks.stream().sorted().toList();
        settleBricks();
        bricks = bricks.stream().sorted().toList();
        var count = countBricksToSafelyDisintegrate();
        assertEquals(5, count);

        long sum = countTotalOfFallenBricks();
        assertEquals(7, sum);
    }

    @Test
    void input() throws IOException {
        Path path = Paths.get("src/test/resources/day22input.txt");
        var lines = Files.readAllLines(path);
        bricks.addAll(toBricks(lines));
        bricks = bricks.stream().sorted().toList();

        System.out.println(bricks.size()+" bricks");
        settleBricks();
        bricks = bricks.stream().sorted().toList();
        var count = countBricksToSafelyDisintegrate();
        assertEquals(432, count);

        long sum = countTotalOfFallenBricks();
        assertEquals(63166, sum);
    }

    private long countTotalOfFallenBricks() {
        long sum = 0;
        for (var brick : bricks) {
            fallenBricks.clear();
            createCheckpoints();
            brick.hide();
            sum += settleBricks();
            brick.appear();
            resetBricks();
        }
        return sum;
    }
    private void createCheckpoints() {
        for (var brick : bricks) {
            brick.createCheckpoint();
        }
    }

    private void resetBricks() {
        for (var brick : bricks) {
            brick.reset();
        }
    }

    @Test
    void testSettle() {
        bricks.add(new Brick("0,4,16~0,4,17"));
        bricks.add(new Brick("0,4,10~0,6,10"));
        bricks.add(new Brick("0,4,11~0,4,13"));
        bricks.add(new Brick("0,4,9~2,4,9"));
        bricks = bricks.stream().sorted().toList();
        settleBricks();
        bricks = bricks.stream().sorted().toList();
        printBricks();
    }

    private int countBricksToSafelyDisintegrate() {
        int counter = 0;

        for (int i = 0; i < bricks.size(); i++) {
            var brick = bricks.get(i);
            //System.out.printf("%04d Try to disintegrate %s\n",i, brick);

            // specification unclear here: bricks count, although they are single cubes
            // if(brick.isSingleCube()) continue;
            brick.hide();
            if (somethingWouldFall(i) == false) {
                counter++;
            }
            brick.appear();
        }

        return counter;
    }

    private boolean somethingWouldFall(int startIndex) {
        int counter = 0;
        for (int i = startIndex; i < bricks.size(); i++) {
            var brick = bricks.get(i);
            if (brick.isOnGround()) continue;
            if (brick.isHidden()) continue;
            brick.oneDown();
            if (collides(brick)) {
                brick.oneUp();
            } else {
                brick.oneUp();
                return true;
            }
        }
        return counter > 0;
    }

    private void printBricks() {
        for (Brick brick : bricks) {
            System.out.println(brick);
        }
        System.out.println();
    }

    private long settleBricks() {
        var settledBricks = -1;
        while (settledBricks != 0) {
            settledBricks = trySettle();
        }

        return fallenBricks.size();
    }

    private int trySettle() {
        int counter = 0;
        for (Brick brick : bricks) {
            if (brick.isOnGround()) continue;
            if (brick.hidden) continue;
            brick.oneDown();
            if (collides(brick)) {
                brick.oneUp();
            } else {
                fallenBricks.add(brick);
                counter++;
            }
        }
        return counter;
    }

    private boolean collides(Brick brick) {
        for (Brick other : bricks) {
            if(other.intersects(brick)) return true;
        }
        return false;
    }

    private boolean stackHasIntersections() {
        for (Brick bricka : bricks) {
            for (Brick brickb : bricks) {
                if(bricka.intersects(brickb)) return true;
            }
        }
        return false;
    }

    private List<Brick> toBricks(List<String> lines) {
        return lines.stream().map(Brick::new).toList();
    }



    @Test
    void testIsSingleCube() {
        assertTrue(new Brick("2,2,2~2,2,2").isSingleCube());
        assertFalse(new Brick("2,2,2~2,2,3").isSingleCube());
    }

    @Test
    void testIntersect() {
        assertFalse(brick("1,0,2~3,0,2").intersects(brick("2,1,1~4,0,3")));
        assertTrue(brick("1,0,1~1,2,1").intersects(brick("0,0,1~2,0,1")));
        assertFalse(brick("1,0,1~1,2,1").intersects(brick("0,0,2~2,0,2")));
        assertFalse(brick("0,0,2~2,0,2").intersects(brick("0,2,2~2,2,2")));
        final var sameBrick = brick("0,0,2~2,0,2");
        assertFalse(sameBrick.intersects(sameBrick));
    }

    private static class Brick implements Comparable<Brick> {
        int minx;
        int miny;
        int minz;
        int maxx;
        int maxy;
        int maxz;
        private boolean hidden = false;
        private String original = "";

        public boolean intersects(Brick other) {
            if(equals(other)) return false;
            if(hidden) return false;
            if(other.hidden) return false;
            if(sameZ(other) == false) return false;
            if(sameX(other) == false) return false;
            if(sameY(other) == false) return false;

            return true;
        }

        private boolean sameX(Brick other) {
            for (int i = minx; i <= maxx; i++) {
                if(other.hasX(i)) return true;
            }
            return false;
        }
        private boolean sameY(Brick other) {
            for (int i = miny; i <= maxy; i++) {
                if(other.hasY(i)) return true;
            }
            return false;
        }
        private boolean sameZ(Brick other) {
            for (int i = minz; i <= maxz; i++) {
                if(other.hasZ(i)) return true;
            }
            return false;
        }

        private boolean hasX(int x) {
            for (int i = minx; i <= maxx; i++) {
                if(x == i) return true;
            }
            return false;
        }
        private boolean hasY(int y) {
            for (int i = miny; i <= maxy; i++) {
                if(y == i) return true;
            }
            return false;
        }
        private boolean hasZ(int z) {
            for (int i = minz; i <= maxz; i++) {
                if(z == i) return true;
            }
            return false;
        }

        public Brick(String desc) {
            this.original = desc;
            fillMinsAndMaxs(original);
        }

        private void fillMinsAndMaxs(String desc) {
            var fromTo = desc.split("~");
            final var froms = fromTo[0].split(",");
            minx = Integer.valueOf(froms[0]);
            miny = Integer.valueOf(froms[1]);
            minz = Integer.valueOf(froms[2]);
            final var tos = fromTo[1].split(",");
            maxx = Integer.valueOf(tos[0]);
            maxy = Integer.valueOf(tos[1]);
            maxz = Integer.valueOf(tos[2]);

            if(minx > maxx) throw new RuntimeException(minx + ":" + maxx);
            if(miny > maxy) throw new RuntimeException(miny + ":" + maxy);
            if(minz > maxz) throw new RuntimeException(minz + ":" + maxz);
        }

        int saveMinX;
        int saveMaxX;
        int saveMinY;
        int saveMaxY;
        int saveMinZ;
        int saveMaxZ;
        public void createCheckpoint() {
            saveMinX = minx;
            saveMaxX = maxx;
            saveMinY = miny;
            saveMaxY = maxy;
            saveMinZ = minz;
            saveMaxZ = maxz;
        }

        public void reset() {
            minx = saveMinX;
            maxx = saveMaxX;
            miny = saveMinY;
            maxy = saveMaxY;
            minz = saveMinZ;
            maxz = saveMaxZ;
        }

        @Override
        public String toString() {
            return minx+","+miny+","+minz+"~"+maxx+","+maxy+","+maxz+" ("+original+")";
        }

        public boolean isSingleCube() {
            return minx == maxx && miny == maxy && minz == maxz;
        }
        public void oneDown() {
            minz -= 1;
            maxz -= 1;
        }

        public void oneUp() {
            minz += 1;
            maxz += 1;
        }

        public boolean isOnGround() {
            return minz == 1;
        }

        public void hide() {
            this.hidden = true;
        }

        public void appear() {
            this.hidden = false;
        }

        public boolean isHidden() {
            return hidden;
        }

        @Override
        public int compareTo(Brick o) {
            return Integer.compare(minz, o.minz);
        }
    }

    private static Brick brick(String desc) {
        return new Brick(desc);
    }
}
