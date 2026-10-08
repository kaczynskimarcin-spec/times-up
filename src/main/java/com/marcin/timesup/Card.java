package com.marcin.timesup;

public class Card {

    private String text;
    private CardCategory category;


    public Card() {
    }


    public Card(
            String text,
            CardCategory category
    ) {

        this.text = text;
        this.category = category;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public CardCategory getCategory() {
        return category;
    }


    public void setCategory(
            CardCategory category
    ) {

        this.category = category;
    }
}