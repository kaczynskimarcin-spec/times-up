package com.marcin.timesup;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardBank {

    private final List<Card> cards;

    public CardBank(ObjectMapper objectMapper) {
        this.cards = loadCards(objectMapper);

        System.out.println();
        System.out.println("=================================");
        System.out.println("TIME'S UP");
        System.out.println("Załadowano haseł: " + cards.size());
        System.out.println("=================================");
    }

    private List<Card> loadCards(
            ObjectMapper objectMapper
    ) {
        List<Card> loadedCards = new ArrayList<>();

        for (CardCategory category : CardCategory.values()) {

            String path =
                    "data/cards/"
                            + category.name()
                            + ".json";

            ClassPathResource resource =
                    new ClassPathResource(path);

            if (!resource.exists()) {
                throw new IllegalStateException(
                        "Brakuje pliku: " + path
                );
            }

            try (
                    InputStream inputStream =
                            resource.getInputStream()
            ) {
                List<String> phrases =
                        objectMapper.readValue(
                                inputStream,
                                new TypeReference<List<String>>() {
                                }
                        );

                for (String phrase : phrases) {
                    loadedCards.add(
                            new Card(
                                    phrase,
                                    category
                            )
                    );
                }

                System.out.println(
                        category.name()
                                + ".json -> "
                                + phrases.size()
                                + " haseł"
                );

            } catch (IOException exception) {
                throw new RuntimeException(
                        "Nie udało się wczytać pliku: "
                                + path,
                        exception
                );
            }
        }

        return loadedCards;
    }

    public List<Card> drawCards(int amount) {

        Map<CardCategory, List<Card>> cardsByCategory =
                new EnumMap<>(CardCategory.class);

        for (Card card : cards) {
            cardsByCategory
                    .computeIfAbsent(
                            card.getCategory(),
                            category -> new ArrayList<>()
                    )
                    .add(card);
        }

        List<CardCategory> categories =
                new ArrayList<>(
                        List.of(CardCategory.values())
                );

        Collections.shuffle(categories);

        int categoryCount = categories.size();

        int cardsPerCategory =
                amount / categoryCount;

        int extraCards =
                amount % categoryCount;

        List<Card> result =
                new ArrayList<>();

        for (int i = 0; i < categories.size(); i++) {

            CardCategory category =
                    categories.get(i);

            int cardsToTake =
                    cardsPerCategory;

            if (i < extraCards) {
                cardsToTake++;
            }

            List<Card> categoryCards =
                    cardsByCategory.get(category);

            if (
                    categoryCards == null
                            || categoryCards.size() < cardsToTake
            ) {
                throw new IllegalStateException(
                        "Za mało haseł w kategorii "
                                + category
                );
            }

            List<Card> shuffled =
                    new ArrayList<>(categoryCards);

            Collections.shuffle(shuffled);

            result.addAll(
                    shuffled.subList(
                            0,
                            cardsToTake
                    )
            );
        }

        Collections.shuffle(result);

        return result;
    }

    public int getCardCount() {
        return cards.size();
    }

    public List<Card> getAllCards() {
        return List.copyOf(cards);
    }

    public Map<CardCategory, Long>
    getCategoryStats() {

        return cards
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Card::getCategory,
                                () ->
                                        new EnumMap<>(
                                                CardCategory.class
                                        ),
                                Collectors.counting()
                        )
                );
    }
}