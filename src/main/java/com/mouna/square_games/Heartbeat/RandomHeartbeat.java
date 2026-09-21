package com.mouna.square_games.Heartbeat;

import org.springframework.stereotype.Service;

@Service
public class RandomHeartbeat implements HeartbeatSensor {

    @Override
    public int get() {
        return (int) (Math.random() *191) +40;
    }
}
