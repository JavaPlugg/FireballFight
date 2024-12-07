package net.javaplugg.minecraft.game.fireballfight.resources;

import lombok.SneakyThrows;
import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Resources {

    @SneakyThrows
    public static String readStringResourceFile(String resourcePath) {
        InputStream inputStream = FireballFight.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        try {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
