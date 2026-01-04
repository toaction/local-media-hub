package com.action.funflow.picture;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PictureFile implements Parcelable {
    private final String filePath;
    private final String fileName;
    private final String folderPath;
    private final String folderName;
    private final long fileSize;
    private final long dateAdded;

    public PictureFile(String filePath, String fileName, String folderPath,
                       String folderName, long fileSize, long dateAdded) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.folderPath = folderPath;
        this.folderName = folderName;
        this.fileSize = fileSize;
        this.dateAdded = dateAdded;
    }

    protected PictureFile(Parcel in) {
        filePath = in.readString();
        fileName = in.readString();
        folderPath = in.readString();
        folderName = in.readString();
        fileSize = in.readLong();
        dateAdded = in.readLong();
    }

    public static final Creator<PictureFile> CREATOR = new Creator<PictureFile>() {
        @Override
        public PictureFile createFromParcel(Parcel in) {
            return new PictureFile(in);
        }

        @Override
        public PictureFile[] newArray(int size) {
            return new PictureFile[size];
        }
    };

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public Uri getUri() {
        return Uri.parse(filePath);
    }

    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024 * 1024));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeString(fileName);
        dest.writeString(folderPath);
        dest.writeString(folderName);
        dest.writeLong(fileSize);
        dest.writeLong(dateAdded);
    }
}
