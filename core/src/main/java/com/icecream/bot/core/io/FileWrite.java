package com.icecream.bot.core.io;

import rx.Observable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class FileWrite {

    private final String mFolder;
    private final String mFile;

    public FileWrite(@Nonnull String folder, @Nonnull String file) {
        mFolder = folder;
        mFile = file;
    }

    @Nonnull
    private File createFolder() throws FileWriteException {
        final File folder = new File(mFolder);

        if (!folder.exists() && !folder.mkdirs()) {
            throw new FileWriteException("Cannot create folder: " + mFolder);
        }

        return folder;
    }

    @Nonnull
    private File createFile(@Nonnull File folder) throws FileWriteException {
        final File file = new File(folder, mFile);

        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new FileWriteException("Cannot create file: " + mFile);
            }
        } catch (IOException exception) {
            throw new FileWriteException("Cannot create file: " + mFile);
        }

        return file;
    }

    private Object objectToFile(@Nonnull final Object object) throws FileWriteException {
        final File file = createFile(createFolder());

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(object.toString());
        } catch (IOException exception) {
            throw new FileWriteException("Cannot write into file: " + mFile);
        }

        return object;
    }

    public Observable<Object> write(@Nonnull final Object object) {
        return Observable.fromCallable(() -> objectToFile(object));
    }
}
