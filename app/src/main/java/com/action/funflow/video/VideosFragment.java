package com.action.funflow.video;

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

import com.action.funflow.video.FolderAdapter;
import com.action.funflow.video.VideoScanner;

import java.util.List;

public class VideosFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 102;
    private RecyclerView videoRecyclerView;
    private FolderAdapter folderAdapter;
    private View emptyStateLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        videoRecyclerView = view.findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2));
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);

        checkAndRequestPermissions();

        return view;
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_VIDEO},
                        PERMISSION_REQUEST_CODE);
            } else {
                loadVideoFolders();
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                loadVideoFolders();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideoFolders();
            } else {
                Toast.makeText(requireContext(), "Permission denied to read videos", Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        }
    }

    private void loadVideoFolders() {
        List<VideoScanner.FolderInfo> folders = VideoScanner.scanVideoFolders(requireContext());

        if (folders.isEmpty()) {
            showEmptyState();
            Toast.makeText(requireContext(), "No video folders found", Toast.LENGTH_SHORT).show();
        } else {
            hideEmptyState();
            folderAdapter = new FolderAdapter(requireContext(), folders);
            videoRecyclerView.setAdapter(folderAdapter);
        }
    }

    private void showEmptyState() {
        videoRecyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        emptyStateLayout.setVisibility(View.GONE);
        videoRecyclerView.setVisibility(View.VISIBLE);
    }
}
