package com.marcin.timesup;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class CardController {

    private final CardBank cardBank;

    public CardController(
            CardBank cardBank
    ) {
        this.cardBank = cardBank;
    }

    @GetMapping("/api/cards/stats")
    public Map<String, Object> getStats() {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "total",
                cardBank.getCardCount()
        );

        response.put(
                "categories",
                cardBank.getCategoryStats()
        );

        return response;
    }
}
