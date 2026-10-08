package com.marcin.timesup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


@Component
public class CardDataValidator
        implements CommandLineRunner {

    private final CardBank cardBank;


    public CardDataValidator(
            CardBank cardBank
    ) {

        this.cardBank =
                cardBank;
    }


    @Override
    public void run(
            String... args
    ) {

        List<Card> cards =
                cardBank.getAllCards();


        Set<String> seen =
                new HashSet<>();


        int duplicates = 0;
        int errors = 0;


        System.out.println();

        System.out.println(
                "================================="
        );

        System.out.println(
                "TIME'S UP - WALIDACJA HASEŁ"
        );

        System.out.println(
                "================================="
        );


        for (Card card : cards) {

            if (
                    card.getText() == null
                            || card.getText().isBlank()
            ) {

                System.out.println(
                        "BŁĄD: puste hasło"
                );

                errors++;

                continue;
            }


            if (
                    card.getCategory() == null
            ) {

                System.out.println(
                        "BŁĄD: brak kategorii -> "
                                + card.getText()
                );

                errors++;

                continue;
            }


            String normalized =
                    normalize(
                            card.getText()
                    );


            if (!seen.add(normalized)) {

                System.out.println(
                        "DUPLIKAT: "
                                + card.getText()
                );

                duplicates++;
            }
        }


        Map<CardCategory, Long> stats =
                cardBank.getCategoryStats();


        for (
                CardCategory category
                : CardCategory.values()
        ) {

            long count =
                    stats.getOrDefault(
                            category,
                            0L
                    );


            if (count < 4) {

                System.out.println(
                        "BŁĄD: kategoria "
                                + category
                                + " ma tylko "
                                + count
                                + " haseł."
                );

                errors++;
            }
        }


        System.out.println();

        System.out.println(
                "Łącznie haseł: "
                        + cards.size()
        );

        System.out.println(
                "Duplikaty: "
                        + duplicates
        );

        System.out.println(
                "Błędy: "
                        + errors
        );

        System.out.println();


        for (
                CardCategory category
                : CardCategory.values()
        ) {

            System.out.println(
                    category
                            + ": "
                            + stats.getOrDefault(
                            category,
                            0L
                    )
            );
        }


        System.out.println(
                "================================="
        );

        System.out.println();


        if (errors > 0) {

            throw new IllegalStateException(
                    "Baza haseł zawiera błędy."
            );
        }


        if (duplicates > 0) {

            throw new IllegalStateException(
                    "Baza haseł zawiera duplikaty."
            );
        }
    }


    private String normalize(
            String text
    ) {

        return text
                .trim()
                .toLowerCase(
                        Locale.ROOT
                )
                .replaceAll(
                        "\\s+",
                        " "
                );
    }
}