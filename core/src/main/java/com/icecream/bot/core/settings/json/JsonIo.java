package com.icecream.bot.core.settings.json;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
abstract class JsonIo {

    private final String mFolder;
    private final String mFile;

    JsonIo(@Nonnull String folder, @Nonnull String file) {
        mFolder = folder;
        mFile = file;
    }

    @Nonnull
    protected final File createFolder() throws JsonIoException {
        final File folder = new File(mFolder);

        if (!folder.exists() && !folder.mkdirs()) {
            throw new JsonIoException("Cannot create folder: " + mFolder);
        }

        return folder;
    }

    @Nonnull
    protected final File createFile(@Nonnull File folder) throws IOException, JsonIoException {
        final File file = new File(folder, mFile);

        if (!file.exists() && !file.createNewFile()) {
            throw new JsonIoException("Cannot create file: " + mFile);
        }

        return file;
    }

    public String getFolder() {
        return mFolder;
    }

    public String getFile() {
        return mFile;
    }
}
