package com.bongbong.mineage.duels.velocity.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class Config {
    @JsonProperty
    private String DUELS_SERVER_NAME, DUELS_GRPC_IP;
    @JsonProperty
    private int GRPC_PORT, DUELS_GRPC_PORT;
    @JsonProperty
    private List<ServerObject> SERVERS;
}
