package com.jthemedetecor.consumers;

import java.util.function.Consumer;

public final class DarkModeConsumer implements  ThemingConsumer<Boolean> {


        private final Consumer<Boolean> consumer;

    public DarkModeConsumer(Consumer<Boolean> consumer) {
        this.consumer = consumer;
    }

    @Override
        public void accept(Boolean aBoolean) {
            this.consumer.accept(aBoolean);
        }
    }
