package net.javaplugg.minecraft.game.fireballfight.util;

import net.kyori.adventure.text.Component;

public class LanguageUtils {

    public static Component secondsAccusative(int seconds) {
        int lastDigit = seconds % 10;
        int lastTwoDigits = seconds % 100;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
            return Component.text("секунд");
        } else if (lastDigit == 1) {
            return Component.text("секунду");
        } else if (lastDigit >= 2 && lastDigit <= 4) {
            return Component.text("секунды");
        } else {
            return Component.text("секунд");
        }
    }
}
