package com.jthemedetecor.consumers;

import java.awt.*;
import java.util.function.Consumer;

public final class PrimaryColorConsumer implements ThemingConsumer<Color> {
    private final Consumer<Color> consumer;

    public PrimaryColorConsumer(Consumer<Color> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void accept(Color color) {
        this.consumer.accept(color);
    }
}
