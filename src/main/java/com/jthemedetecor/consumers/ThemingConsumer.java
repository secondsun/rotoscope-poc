package com.jthemedetecor.consumers;

import java.util.function.Consumer;

public sealed interface  ThemingConsumer<T> extends Consumer<T> permits DarkModeConsumer, PrimaryColorConsumer {

}

