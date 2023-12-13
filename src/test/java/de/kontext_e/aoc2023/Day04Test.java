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

import static org.junit.jupiter.api.Assertions.*;

class Day04Test {
    @Test
    void testCardsWithTest() throws IOException {
        ScratchCards scratchCards = ScratchCards.readInputFile("src/test/resources/day04test.txt");

        long worth = scratchCards.calculateWorth();
        scratchCards.createCopies();
        final var count = scratchCards.countScratchCards();

        assertEquals(13, worth);
        assertEquals(30, count);
    }

    @Test
    void testCardsWithInput() throws IOException {
        ScratchCards scratchCards = ScratchCards.readInputFile("src/test/resources/day04input.txt");

        long worth = scratchCards.calculateWorth();
        scratchCards.createCopies();
        final var count = scratchCards.countScratchCards();

        assertEquals(25571, worth);
        assertEquals(8805731, count);
    }

    private static class ScratchCards {
        private final List<Card> cards = new ArrayList<>();
        private int[] instances;

        public static ScratchCards readInputFile(String pathString) throws IOException {
            ScratchCards scratchCards = new ScratchCards();

            Path path = Paths.get(pathString);
            var lines = Files.readAllLines(path);
            for (String line : lines) {
                scratchCards.cards.add(Card.parse(line));
            }
            scratchCards.initializeInstances();

            return scratchCards;
        }

        private void initializeInstances() {
            instances = new int[cards.size()];
            Arrays.fill(instances, 1);
        }

        public long calculateWorth() {
            long worth = 0;

            for (Card card : cards) {
                worth += card.getWorth();
            }

            return worth;
        }

        void createCopies() {
            int cardNumber = 0;
            for (Card card : cards) {
                var matchingNumbers = card.countMatchingNumbers();
                var instancesOfThisCard = instances[cardNumber];

                for(int j = 0; j < instancesOfThisCard; j++) {
                    for (int i = 0; i < matchingNumbers; i++) {
                        final var index = i + cardNumber + 1;
                        if ( index < instances.length) {
                            instances[index]++;
                        }
                    }
                }

                cardNumber++;
            }
        }

        long countScratchCards() {
            long count = 0;

            for (int instance : instances) {
                count += instance;
            }

            return count;
        }
    }


    @Test
    void testFilterNumbers() {
        Card card = new Card();
        card.addWinningNumber(41);
        card.addWinningNumber(48);
        card.addNumberYouHave(83);
        card.addNumberYouHave(48);

        var filtered = card.filterWinningNumbers();

        assertTrue(filtered.contains(48));
        assertFalse(filtered.contains(83));
        assertEquals(1, card.countMatchingNumbers());
    }

    @Test
    void testCardWorth() {
        Card card = new Card();
        card.addWinningNumber(41);
        card.addWinningNumber(48);
        card.addNumberYouHave(41);

        assertEquals(1, card.getWorth());

        card.addNumberYouHave(48);
        assertEquals(2, card.getWorth());
        assertEquals(2, card.countMatchingNumbers());
    }

    @Test
    void testParseCard() {
        Card card = Card.parse("Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53");
        assertEquals(8, card.getWorth());
    }

    private static class Card {
        private List<Number> winningNumbers = new LinkedList<>();
        private List<Number> numbersYouHave = new LinkedList<>();

        public static Card parse(String input) {
            Card card = new Card();
            var winningStartPos = input.indexOf(":") + 1;
            var winningEndPos = input.indexOf("|");
            var winningPart = input.substring(winningStartPos, winningEndPos).trim();
            var youHavePart = input.substring(winningEndPos + 1).trim();

            var splitted = winningPart.split(" ");
            for (String s : splitted) {
                if(s.isEmpty() == false) {
                    try {
                        card.winningNumbers.add(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                    }
                }
            }

            splitted = youHavePart.split(" ");
            for (String s : splitted) {
                if(s.isEmpty() == false) {
                    try {
                        card.numbersYouHave.add(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                    }
                }
            }

            return card;
        }

        void addWinningNumber(Number number) {
            winningNumbers.add(number);
        }

        void addNumberYouHave(Number number) {
            numbersYouHave.add(number);
        }

        public long getWorth() {
            List<Number> filtered = filterWinningNumbers();
            long worth = 0;
            if (filtered.isEmpty() == false) {
                worth = (long) Math.pow(2, filtered.size() - 1);
            }
            return worth;
        }

        List<Number> filterWinningNumbers() {
            List<Number> filtered = new ArrayList<>();

            for (Number number : numbersYouHave) {
                if (winningNumbers.contains(number)) {
                    filtered.add(number);
                }
            }

            return filtered;
        }

        public long countMatchingNumbers() {
            List<Number> filtered = filterWinningNumbers();
            return filtered.size();
        }
    }
}
