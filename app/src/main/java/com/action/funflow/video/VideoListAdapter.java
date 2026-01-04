package com.action.funflow.video;

import android.content.Context;

import com.action.funflow.R;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private final Context context;
    private final List<VideoFile> videos;

    public VideoListAdapter(Context context, List<VideoFile> videos) {
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_list, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoFile video = videos.get(position);
        holder.videoNameTextView.setText(video.getFileName());
        holder.videoSizeTextView.setText(video.getFormattedFileSize());
        holder.durationBadgeTextView.setText(video.getFormattedDuration());

        // Load video thumbnail
        loadVideoThumbnail(video.getFilePath(), holder.videoThumbnailImageView);

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putParcelableArrayListExtra("videos", new ArrayList<>(videos));
                intent.putExtra("currentPosition", currentPosition);
                context.startActivity(intent);
            }
        });
    }

    private void loadVideoThumbnail(String videoPath, ImageView imageView) {
        try {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Video.Media._ID},
                    MediaStore.Video.Media.DATA + "=?",
                    new String[]{videoPath},
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));

                Glide.with(context)
                        .load(uri)
                        .frame(1000000) // Extract frame at 1 second
                        .centerCrop()
                        .placeholder(android.R.color.darker_gray)
                        .into(imageView);

                cursor.close();
            } else {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnailImageView;
        TextView videoNameTextView;
        TextView videoSizeTextView;
        TextView durationBadgeTextView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnailImageView = itemView.findViewById(R.id.videoThumbnailImageView);
            videoNameTextView = itemView.findViewById(R.id.videoNameTextView);
            videoSizeTextView = itemView.findViewById(R.id.videoSizeTextView);
            durationBadgeTextView = itemView.findViewById(R.id.durationBadgeTextView);
        }
    }
}
