package com.action.funflow.picture;

import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.action.funflow.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class PictureFolderAdapter extends RecyclerView.Adapter<PictureFolderAdapter.FolderViewHolder> {

    private final Context context;
    private final List<PictureScanner.FolderInfo> folders;

    public PictureFolderAdapter(Context context, List<PictureScanner.FolderInfo> folders) {
        this.context = context;
        this.folders = folders;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        PictureScanner.FolderInfo folder = folders.get(position);
        holder.folderNameTextView.setText(folder.folderName);
        holder.videoInfoTextView.setText(folder.pictureCount + " pictures");
        holder.videoCountBadgeTextView.setText(String.valueOf(folder.pictureCount));

        // Reset folder icon visibility
        holder.folderIconImageView.setVisibility(View.VISIBLE);

        // Load first picture thumbnail
        if (!folder.pictures.isEmpty()) {
            PictureFile firstPicture = folder.pictures.get(0);
            loadPictureThumbnail(firstPicture.getFilePath(), holder.thumbnailImageView, holder.folderIconImageView);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PictureListActivity.class);
            intent.putParcelableArrayListExtra(PictureListActivity.EXTRA_PICTURES, new java.util.ArrayList<>(folder.pictures));
            intent.putExtra(PictureListActivity.EXTRA_FOLDER_NAME, folder.folderName);
            context.startActivity(intent);
        });
    }

    private void loadPictureThumbnail(String picturePath, ImageView imageView, ImageView folderIcon) {
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
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // Hide folder icon when thumbnail is loaded
                                folderIcon.setVisibility(View.GONE);
                                return false;
                            }
                        })
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
        return folders.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        ImageView folderIconImageView;
        TextView folderNameTextView;
        TextView videoInfoTextView;
        TextView videoCountBadgeTextView;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            folderIconImageView = itemView.findViewById(R.id.folderIconImageView);
            folderNameTextView = itemView.findViewById(R.id.folderNameTextView);
            videoInfoTextView = itemView.findViewById(R.id.videoInfoTextView);
            videoCountBadgeTextView = itemView.findViewById(R.id.videoCountBadgeTextView);
        }
    }
}
