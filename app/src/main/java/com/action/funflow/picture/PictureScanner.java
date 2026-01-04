package com.action.funflow.picture;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureScanner {

    public static class FolderInfo {
        public final String folderPath;
        public final String folderName;
        public final List<PictureFile> pictures;
        public int pictureCount;

        public FolderInfo(String folderPath, String folderName) {
            this.folderPath = folderPath;
            this.folderName = folderName;
            this.pictures = new ArrayList<>();
            this.pictureCount = 0;
        }

        public void addPicture(PictureFile picture) {
            pictures.add(picture);
            pictureCount++;
        }
    }

    public static List<FolderInfo> scanPictureFolders(Context context) {
        Map<String, FolderInfo> folderMap = new HashMap<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                do {
                    long id = cursor.getLong(idColumn);
                    String fileName = cursor.getString(nameColumn);
                    String filePath = cursor.getString(dataColumn);
                    long fileSize = cursor.getLong(sizeColumn);
                    long dateAdded = cursor.getLong(dateColumn);
                    String bucketId = cursor.getString(bucketIdColumn);
                    String bucketName = cursor.getString(bucketNameColumn);

                    // Get folder path from file path
                    String folderPath = filePath.substring(0, filePath.lastIndexOf('/'));

                    // Create PictureFile object
                    PictureFile pictureFile = new PictureFile(
                            filePath,
                            fileName,
                            folderPath,
                            bucketName != null ? bucketName : "Unknown Folder",
                            fileSize,
                            dateAdded
                    );

                    // Group by folder
                    if (!folderMap.containsKey(folderPath)) {
                        folderMap.put(folderPath, new FolderInfo(folderPath, bucketName != null ? bucketName : "Unknown Folder"));
                    }
                    folderMap.get(folderPath).addPicture(pictureFile);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(folderMap.values());
    }

    public static List<PictureFile> getAllPictures(Context context) {
        List<PictureFile> pictureList = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                do {
                    String fileName = cursor.getString(nameColumn);
                    String filePath = cursor.getString(dataColumn);
                    long fileSize = cursor.getLong(sizeColumn);
                    long dateAdded = cursor.getLong(dateColumn);
                    String bucketName = cursor.getString(bucketNameColumn);

                    String folderPath = filePath.substring(0, filePath.lastIndexOf('/'));

                    PictureFile pictureFile = new PictureFile(
                            filePath,
                            fileName,
                            folderPath,
                            bucketName != null ? bucketName : "Unknown Folder",
                            fileSize,
                            dateAdded
                    );

                    pictureList.add(pictureFile);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pictureList;
    }
}
