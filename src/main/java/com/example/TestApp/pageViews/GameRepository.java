package com.example.TestApp.pageViews;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameLobby, Long> {
}
