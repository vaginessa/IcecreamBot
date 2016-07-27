package com.icecream.bot.core.io;

import java.io.IOException;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class FileIoException extends IOException {

    FileIoException(String message) {
        super(message);
    }
}
