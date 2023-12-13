package de.kontext_e.aoc2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day07Test {

    @Test
    void test() throws IOException {
        Path path = Paths.get("src/test/resources/day07test.txt");
        var lines = Files.readAllLines(path);
        final var linesArray = lines.toArray(new String[]{});
        sort(linesArray);

        long totalWinnings = 0;
        for (int i = 0; i < linesArray.length; i++) {
            long rank = i + 1;
            long bid = getBid(linesArray[i]);
            totalWinnings += bid * rank;
        }

        assertEquals(5905, totalWinnings);
    }

    @Test
    void testWithInput() throws IOException {
        Path path = Paths.get("src/test/resources/day07input.txt");
        var lines = Files.readAllLines(path);
        final var linesArray = lines.toArray(new String[]{});
        sort(linesArray);

        long totalWinnings = 0;
        for (int i = 0; i < linesArray.length; i++) {
            long rank = i + 1;
            long bid = getBid(linesArray[i]);
            totalWinnings += bid * rank;
        }

        assertEquals(251003917, totalWinnings);
    }

    private long getBid(String line) {
        var bidString = line.substring(line.indexOf(" ")).trim();
        return Long.parseLong(bidString);
    }

    private void sort(String... lines) {
        for (int i = 0; i < lines.length - 1; i++) {
            for (int k = 0; k < lines.length - 1; k++) {
                if (isStronger(lines[k], lines[k + 1])) {
                    var tmp = lines[k+1];
                    lines[k + 1] = lines[k];
                    lines[k] = tmp;
                }
            }
        }
    }

    private boolean isStronger(String line1, String line2) {
        var hand1 = hand(line1);
        var hand2 = hand(line2);

        var type1 = type(hand1);
        var type2 = type(hand2);

        if(type1 > type2) return true;
        if (type1 == type2) {
            for (int i = 0; i < hand1.length(); i++) {
                var card1 = hand1.charAt(i);
                var card2 = hand2.charAt(i);
                if(card1 == card2) continue;
                var strength1 = getStrength(card1);
                var strength2 = getStrength(card2);
                return strength1 > strength2;
            }
        }

        return false;
    }

    private int getStrength(char card) {
        if(card == 'J') return 1;
        if(card == '2') return 2;
        if(card == '3') return 3;
        if(card == '4') return 4;
        if(card == '5') return 5;
        if(card == '6') return 6;
        if(card == '7') return 7;
        if(card == '8') return 8;
        if(card == '9') return 9;
        if(card == 'T') return 10;
        if(card == 'Q') return 11;
        if(card == 'K') return 12;
        if(card == 'A') return 13;
        return 0;
    }

    private static final int HIGH_CARD = 1;
    private static final int ONE_PAIR = 2;
    private static final int TWO_PAIR = 3;
    private static final int THREE_OF_A_KIND = 4;
    private static final int FULL_HOUSE = 5;
    private static final int FOUR_OF_A_KIND = 6;
    private static final int FIVE_OF_A_KIND = 7;
    private long type(String hand) {
        final var counts = countCharacters(hand);
        if(allCardsHaveSameLabel(counts, true)) return FIVE_OF_A_KIND;
        if(fourCardsHaveSameLabel(counts, true)) return FOUR_OF_A_KIND;
        if(threeAndTwoCardsHaveSameLabel(counts, true)) return FULL_HOUSE;
        if(threeCardsHaveSameLabel(counts, true)) return THREE_OF_A_KIND;
        if(twoAndTwoCardsHaveSameLabel(counts, true)) return TWO_PAIR;
        if(twoCardsHaveSameLabel(counts, true)) return ONE_PAIR;
        return HIGH_CARD;
    }

    private boolean allCardsHaveSameLabel(int[] counts, boolean withJokers) {
        for (int i = 0; i < 255; i++) {
            if (counts[i] == 5) {
                return true;
            }
        }

        if(withJokers) {
            if (fourCardsHaveSameLabel(counts, false) && countJokers(counts) == 1) {
                return true;
            }

            if (threeCardsHaveSameLabel(counts, false) && countJokers(counts) == 2) {
                return true;
            }

            if (twoCardsHaveSameLabel(counts, false) && countJokers(counts) == 3) {
                return true;
            }

            if (countJokers(counts) == 4) {
                return true;
            }
        }

        return false;
    }

    private boolean fourCardsHaveSameLabel(int[] counts, boolean withJokers) {
        for (int i = 0; i < 255; i++) {
            if (counts[i] == 4) {
                return true;
            }
        }

        if(withJokers) {
            if (threeCardsHaveSameLabel(counts, false) && countJokers(counts) == 1) {
                return true;
            }

            for (int i = 0; i < 255; i++) {
                if (counts[i] == 2 && i != 'J' && countJokers(counts) == 2) {
                    return true;
                }
            }

            if (countJokers(counts) == 3) {
                return true;
            }
        }

        return false;
    }

    private boolean threeCardsHaveSameLabel(int[] counts, boolean withJokers) {
        for (int i = 0; i < 255; i++) {
            if (counts[i] == 3) {
                return true;
            }
        }

        if(withJokers) {
            if (twoCardsHaveSameLabel(counts, false) && countJokers(counts) == 1) {
                return true;
            }

            if (countJokers(counts) == 2) {
                return true;
            }
        }

        return false;
    }

    private boolean twoCardsHaveSameLabel(int[] counts, boolean withJokers) {
        for (int i = 0; i < 255; i++) {
            if (counts[i] == 2) {
                return true;
            }
        }

        if(withJokers) {
            if (countJokers(counts) == 1) {
                return true;
            }
        }

        return false;
    }

    private boolean threeAndTwoCardsHaveSameLabel(int[] counts, boolean withJokers) {
        boolean threeFound = false;
        boolean twoFound = false;
        for (int i = 0; i < 255; i++) {
            if (counts[i] == 3) {
                threeFound = true;
            }
            if (counts[i] == 2) {
                twoFound = true;
            }
        }

        if(withJokers) {
            if (twoAndTwoCardsHaveSameLabel(counts, false) && countJokers(counts) == 1) {
                return true;
            }
        }

        return threeFound && twoFound;
    }

    private boolean twoAndTwoCardsHaveSameLabel(int[] counts, boolean withJokers) {
        int twoFound = 0;
        for (int i = 0; i < 255; i++) {
            if (counts[i] == 2) {
                twoFound++;
            }
        }

        return twoFound == 2;
    }

    private static int[] countCharacters(String hand) {
        int[] counts = new int[255];
        for (int i = 0; i < hand.length(); i++) {
            var c = hand.charAt(i);
            counts[c]++;
        }
        return counts;
    }

    private String hand(String input) {
        return input.substring(0, input.indexOf(" "));
    }

    private int countJokers(int[] counts) {
        return counts['J'];
    }
}
