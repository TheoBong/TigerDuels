package com.bongbong.mineage.duels.velocity.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ServerObject {
    @JsonProperty
    private String NAME, IP;
    @JsonProperty
    private int PORT;
}
