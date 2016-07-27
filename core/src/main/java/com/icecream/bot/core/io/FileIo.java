package com.icecream.bot.core.io;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
abstract class FileIo {

    private final String mFolder;
    private final String mFile;

    FileIo(@Nonnull String folder, @Nonnull String file) {
        mFolder = folder;
        mFile = file;
    }

    @Nonnull
    protected final File createFolder() throws FileIoException {
        final File folder = new File(mFolder);

        if (!folder.exists() && !folder.mkdirs()) {
            throw new FileIoException("Cannot create folder: " + mFolder);
        }

        return folder;
    }

    @Nonnull
    protected final File createFile(@Nonnull File folder) throws FileIoException {
        final File file = new File(folder, mFile);

        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new FileIoException("Cannot create file: " + mFile);
            }
        } catch (IOException exception) {
            throw new FileIoException("Cannot create file: " + mFile);
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
