package com.github.chrisgenti.discordtickets.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.chrisgenti.discordtickets.DiscordData;

import java.io.IOException;

public class DiscordDataDeserializer extends StdDeserializer<DiscordData> {
    public DiscordDataDeserializer() {
        super(DiscordData.class);
    }

    @Override
    public DiscordData deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        JsonNode discordNode    =   node.get("discord");
        String discordToken     =   discordNode.get("token").asText("");

        JsonNode mongoNode      =   node.get("mongo");
        String mongoHostname    =   mongoNode.get("hostname").asText("");
        String mongoUsername    =   mongoNode.get("username").asText("");
        String mongoPassword    =   mongoNode.get("password").asText("");

        return new DiscordData(new DiscordData.Discord(discordToken), new DiscordData.Mongo(mongoHostname, mongoUsername, mongoPassword));
    }
}
