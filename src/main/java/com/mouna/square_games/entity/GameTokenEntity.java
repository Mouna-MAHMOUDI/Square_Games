package com.mouna.square_games.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class GameTokenEntity {
    @Id
    public Long id;
    public String ownerId;
    public String name;
    public boolean removed;
    public Integer x;
    public Integer y;
}
