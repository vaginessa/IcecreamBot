package com.icecream.bot.core.io;

import java.io.IOException;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class FileWriteException extends IOException {

    FileWriteException(String message) {
        super(message);
    }
}
