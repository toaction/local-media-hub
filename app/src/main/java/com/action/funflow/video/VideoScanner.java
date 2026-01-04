package com.action.funflow.video;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoScanner {

    public static class FolderInfo {
        public final String folderPath;
        public final String folderName;
        public final List<VideoFile> videos;
        public int videoCount;

        public FolderInfo(String folderPath, String folderName) {
            this.folderPath = folderPath;
            this.folderName = folderName;
            this.videos = new ArrayList<>();
            this.videoCount = 0;
        }

        public void addVideo(VideoFile video) {
            videos.add(video);
            videoCount++;
        }
    }

    public static List<FolderInfo> scanVideoFolders(Context context) {
        Map<String, FolderInfo> folderMap = new HashMap<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        };

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);
                int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

                do {
                    long id = cursor.getLong(idColumn);
                    String fileName = cursor.getString(nameColumn);
                    String filePath = cursor.getString(dataColumn);
                    long fileSize = cursor.getLong(sizeColumn);
                    long dateAdded = cursor.getLong(dateColumn);
                    long duration = cursor.getLong(durationColumn);
                    String bucketId = cursor.getString(bucketIdColumn);
                    String bucketName = cursor.getString(bucketNameColumn);

                    // Get folder path from file path
                    String folderPath = filePath.substring(0, filePath.lastIndexOf('/'));

                    // Create VideoFile object
                    VideoFile videoFile = new VideoFile(
                            filePath,
                            fileName,
                            folderPath,
                            bucketName != null ? bucketName : "Unknown Folder",
                            fileSize,
                            dateAdded,
                            duration
                    );

                    // Group by folder
                    if (!folderMap.containsKey(folderPath)) {
                        folderMap.put(folderPath, new FolderInfo(folderPath, bucketName != null ? bucketName : "Unknown Folder"));
                    }
                    folderMap.get(folderPath).addVideo(videoFile);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(folderMap.values());
    }

    public static List<VideoFile> getAllVideos(Context context) {
        List<VideoFile> videoList = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        };

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);
                int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

                do {
                    String fileName = cursor.getString(nameColumn);
                    String filePath = cursor.getString(dataColumn);
                    long fileSize = cursor.getLong(sizeColumn);
                    long dateAdded = cursor.getLong(dateColumn);
                    long duration = cursor.getLong(durationColumn);
                    String bucketName = cursor.getString(bucketNameColumn);

                    String folderPath = filePath.substring(0, filePath.lastIndexOf('/'));

                    VideoFile videoFile = new VideoFile(
                            filePath,
                            fileName,
                            folderPath,
                            bucketName != null ? bucketName : "Unknown Folder",
                            fileSize,
                            dateAdded,
                            duration
                    );

                    videoList.add(videoFile);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videoList;
    }
}
