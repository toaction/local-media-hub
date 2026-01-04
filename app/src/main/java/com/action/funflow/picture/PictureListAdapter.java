package com.action.funflow.picture;

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

public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.PictureViewHolder> {

    private final Context context;
    private final List<PictureFile> pictures;

    public PictureListAdapter(Context context, List<PictureFile> pictures) {
        this.context = context;
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_picture_list, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        PictureFile picture = pictures.get(position);
        holder.pictureNameTextView.setText(picture.getFileName());
        holder.pictureSizeTextView.setText(picture.getFormattedFileSize());

        // Load picture thumbnail
        loadPictureThumbnail(picture.getFilePath(), holder.pictureThumbnailImageView);

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, PicturePlayerActivity.class);
                intent.putParcelableArrayListExtra("pictures", new ArrayList<>(pictures));
                intent.putExtra("currentPosition", currentPosition);
                context.startActivity(intent);
            }
        });
    }

    private void loadPictureThumbnail(String picturePath, ImageView imageView) {
        try {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=?",
                    new String[]{picturePath},
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));

                Glide.with(context)
                        .load(uri)
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
        return pictures.size();
    }

    static class PictureViewHolder extends RecyclerView.ViewHolder {
        ImageView pictureThumbnailImageView;
        TextView pictureNameTextView;
        TextView pictureSizeTextView;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureThumbnailImageView = itemView.findViewById(R.id.pictureThumbnailImageView);
            pictureNameTextView = itemView.findViewById(R.id.pictureNameTextView);
            pictureSizeTextView = itemView.findViewById(R.id.pictureSizeTextView);
        }
    }
}
