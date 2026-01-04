package com.action.funflow.picture;

import android.Manifest;

import com.action.funflow.R;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.action.funflow.picture.PictureFolderAdapter;
import com.action.funflow.picture.PictureScanner;

import java.util.List;

public class PicturesFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private RecyclerView pictureRecyclerView;
    private PictureFolderAdapter pictureAdapter;
    private View emptyStateLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);

        pictureRecyclerView = view.findViewById(R.id.pictureRecyclerView);
        pictureRecyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2));
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);

        checkAndRequestPermissions();

        return view;
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                loadPictureFolders();
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                loadPictureFolders();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPictureFolders();
            } else {
                Toast.makeText(requireContext(), "Permission denied to read images", Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        }
    }

    private void loadPictureFolders() {
        List<PictureScanner.FolderInfo> folders = PictureScanner.scanPictureFolders(requireContext());

        if (folders.isEmpty()) {
            showEmptyState();
            Toast.makeText(requireContext(), "No picture folders found", Toast.LENGTH_SHORT).show();
        } else {
            hideEmptyState();
            pictureAdapter = new PictureFolderAdapter(requireContext(), folders);
            pictureRecyclerView.setAdapter(pictureAdapter);
        }
    }

    private void showEmptyState() {
        pictureRecyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        emptyStateLayout.setVisibility(View.GONE);
        pictureRecyclerView.setVisibility(View.VISIBLE);
    }
}
