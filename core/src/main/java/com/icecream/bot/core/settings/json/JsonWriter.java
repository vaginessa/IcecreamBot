package com.icecream.bot.core.settings.json;

import rx.Observable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class JsonWriter extends JsonIo {

    public JsonWriter(@Nonnull String folder, @Nonnull String file) {
        super(folder, file);
    }

    @Nonnull
    private String jsonToFile(@Nonnull final String object) throws JsonIoException, IOException {
        final File file = createFile(createFolder());

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(object);
        } catch (IOException exception) {
            throw new JsonIoException("Cannot write into file: " + file.getName());
        }

        return object;
    }

    public Observable<Object> write(@Nonnull final String object) {
        return Observable.fromCallable(() -> jsonToFile(object));
    }
}
