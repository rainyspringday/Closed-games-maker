package com.example.TestApp.pageViews;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class GameLobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String team1;
    private String team2;
    private Integer gameResult;

    public GameLobby( String team1, String team2, Integer gameResult) {
        this.team1 = team1;
        this.team2 = team2;
        this.gameResult = gameResult;
    }
}
