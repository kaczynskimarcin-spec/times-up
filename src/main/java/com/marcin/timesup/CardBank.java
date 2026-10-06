package com.marcin.timesup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardBank {

    private final List<String> phrases = List.of(
            "Albert Einstein",
            "Harry Potter",
            "Pan Tadeusz",
            "Robert Lewandowski",
            "Mikołaj Kopernik",
            "Sherlock Holmes",
            "Adam Mickiewicz",
            "Maria Skłodowska-Curie",
            "Juliusz Cezar",
            "Napoleon Bonaparte",

            "Władca Pierścieni",
            "Titanic",
            "Gwiezdne Wojny",
            "Król Lew",
            "Matrix",
            "Shrek",
            "Minecraft",
            "Monopoly",
            "Wikipedia",
            "YouTube",

            "Wieża Eiffla",
            "Wielki Mur Chiński",
            "Piramidy",
            "Mount Everest",
            "Sahara",
            "Amazonka",
            "Księżyc",
            "Mars",
            "Ocean Spokojny",
            "Antarktyda",

            "Leonardo da Vinci",
            "William Shakespeare",
            "Fryderyk Chopin",
            "Elon Musk",
            "Steve Jobs",
            "Michael Jackson",
            "Cristiano Ronaldo",
            "Usain Bolt",
            "Vincent van Gogh",
            "Pablo Picasso",

            "Dinozaur",
            "Wulkan",
            "Grawitacja",
            "Fotosynteza",
            "Internet",
            "Sztuczna inteligencja",
            "Czarna dziura",
            "DNA",
            "Ewolucja",
            "Układ Słoneczny",

            "Romeo i Julia",
            "Mały Książę",
            "Hobbit",
            "Lalka",
            "Quo Vadis",
            "Dziady",
            "Kamienie na szaniec",
            "Opowieść wigilijna",
            "Akademia Pana Kleksa",
            "Balladyna"
    );

    public List<Card> drawCards(int amount) {

        List<String> shuffled = new ArrayList<>(phrases);

        Collections.shuffle(shuffled);

        int numberOfCards = Math.min(amount, shuffled.size());

        List<Card> cards = new ArrayList<>();

        for (int i = 0; i < numberOfCards; i++) {
            cards.add(new Card(shuffled.get(i)));
        }

        return cards;
    }
}
