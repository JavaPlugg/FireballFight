package net.javaplugg.minecraft.game.fireballfight.localization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.List;

@Getter
@AllArgsConstructor
public class Animation {
    private final int period;
    private final List<Component> frames;

    public Component getCurrentFrame() {
        return frames.get((int) ((System.currentTimeMillis() / 50 / period) % frames.size()));
    }
}
