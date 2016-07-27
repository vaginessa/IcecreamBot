package com.icecream.bot.core.io;

import rx.Observable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class FileWrite extends FileIo {

    public FileWrite(@Nonnull String folder, @Nonnull String file) {
        super(folder, file);
    }

    @Nonnull
    private String stringToFile(@Nonnull final String object) throws FileIoException {
        final File file = createFile(createFolder());

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(object);
        } catch (IOException exception) {
            throw new FileIoException("Cannot write into file: " + file.getName());
        }

        return object;
    }

    public Observable<Object> write(@Nonnull final String object) {
        return Observable.fromCallable(() -> stringToFile(object));
    }
}
