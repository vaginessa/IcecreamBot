package com.icecream.bot.core.io;

import com.google.gson.JsonParser;
import rx.Observable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class FileRead extends FileIo {

    public FileRead(@Nonnull String folder, @Nonnull String file) {
        super(folder, file);
    }

    @Nonnull
    private String fileToString() throws FileIoException {
        final File file = createFile(createFolder());

        try {
            return new JsonParser().parse(new FileReader(file)).getAsJsonObject().toString();
        } catch (IOException ioe) {
            throw new FileIoException("Cannot read file: " + file.getName());
        }
    }

    public Observable<String> read() {
        return Observable.fromCallable(this::fileToString);
    }
}
