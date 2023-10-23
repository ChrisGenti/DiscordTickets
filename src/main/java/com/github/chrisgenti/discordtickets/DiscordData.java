package com.github.chrisgenti.discordtickets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.jetbrains.annotations.NotNull;

public record DiscordData (
        @JsonProperty("discord") @NotNull Discord discord, @JsonProperty("mongo") @NotNull Mongo mongo
) {
    public DiscordData() {
        this(
                new DiscordData.Discord(),
                new DiscordData.Mongo()
        );
    }

    @JsonRootName("discord")
    public record Discord (
            @JsonProperty("token") @NotNull String token
    ) {
        public Discord() {
            this(
                    ""
            );
        }
    }

    @JsonRootName("mongo")
    public record Mongo (
            @JsonProperty("hostname") @NotNull String hostname, @JsonProperty("username") @NotNull String username, @JsonProperty("password") @NotNull String password
    ) {
        public Mongo() {
            this(
                    "",
                    "",
                    ""
            );
        }
    }
}


