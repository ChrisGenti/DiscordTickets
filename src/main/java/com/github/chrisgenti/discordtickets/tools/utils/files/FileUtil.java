package com.github.chrisgenti.discordtickets.tools.utils.files;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.chrisgenti.discordtickets.DiscordData;
import com.github.chrisgenti.discordtickets.io.DiscordDataDeserializer;
import com.github.chrisgenti.discordtickets.io.DiscordDataSerializer;
import com.github.chrisgenti.discordtickets.tools.ObjectTriple;
import com.github.chrisgenti.discordtickets.tools.enums.files.FileResult;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileUtil {
    private FileUtil() {}

    public static @NotNull ObjectTriple<FileResult, DiscordData, Exception> loadData(@NotNull Path path) {
        File var = path.toFile();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("DiscordDataSerializer");

        module.addDeserializer(DiscordData.class, new DiscordDataDeserializer());
        module.addSerializer(DiscordData.class, new DiscordDataSerializer());
        mapper.registerModule(module);

        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        ObjectReader reader = mapper.reader();

        if (createFileIfNeeded(var)) {
            try {
                DiscordData discordData = new DiscordData(); writer.writeValue(var, new DiscordData());
                return ObjectTriple.of(FileResult.CREATED, discordData, null);
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }

        try {
            DiscordData discordData = reader.readValue(var, DiscordData.class);
            return ObjectTriple.of(FileResult.EXISTING, discordData, null);
        } catch (Exception exc) {
            return ObjectTriple.of(FileResult.MALFORMED, null, exc);
        }
    }

    private static boolean createFileIfNeeded(@NotNull File file) {
        try {
            return file.createNewFile();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}
