package com.github.chrisgenti.discordtickets.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.chrisgenti.discordtickets.DiscordData;

import java.io.IOException;

public class DiscordDataSerializer extends StdSerializer<DiscordData> {
    public DiscordDataSerializer() {
        super(DiscordData.class);
    }

    @Override
    public void serialize(DiscordData var, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        DiscordData.Discord discordData = var.discord(); DiscordData.Mongo mongoData = var.mongo();

        generator.writeStartObject();
        generator.writeObjectFieldStart("discord");
        generator.writeStringField("token", "");
        generator.writeEndObject();

        generator.writeObjectFieldStart("mongo");
        generator.writeStringField("hostname", "");
        generator.writeStringField("username", "");
        generator.writeStringField("password", "");

        generator.writeEndObject();
    }
}
