package com.marcin.timesup;

import java.util.List;


public record StartGameRequest(

        List<String> teamNames

) {
}