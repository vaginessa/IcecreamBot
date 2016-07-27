package com.icecream.bot.core.settings.json;

import com.google.gson.JsonParser;
import rx.Observable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class JsonReader extends JsonIo {

    public JsonReader(@Nonnull String folder, @Nonnull String file) {
        super(folder, file);
    }

    @Nonnull
    private String fileToJson() throws IOException, JsonIoException {
        return new JsonParser().parse(new FileReader(new File(createFolder(), getFile()))).getAsJsonObject().toString();
    }

    public Observable<String> read() {
        return Observable.fromCallable(this::fileToJson);
    }
}
