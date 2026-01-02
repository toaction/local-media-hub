package com.action.funflow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView folderRecyclerView;
    private FolderAdapter folderAdapter;
    private View emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        folderRecyclerView = findViewById(R.id.folderRecyclerView);
        // Use GridLayoutManager with 2 columns for modern look
        folderRecyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_VIDEO},
                        PERMISSION_REQUEST_CODE);
            } else {
                loadVideoFolders();
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                loadVideoFolders();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideoFolders();
            } else {
                Toast.makeText(this, "Permission denied to read videos", Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        }
    }

    private void loadVideoFolders() {
        List<VideoScanner.FolderInfo> folders = VideoScanner.scanVideoFolders(this);

        if (folders.isEmpty()) {
            showEmptyState();
            Toast.makeText(this, "No video folders found", Toast.LENGTH_SHORT).show();
        } else {
            hideEmptyState();
            folderAdapter = new FolderAdapter(this, folders);
            folderRecyclerView.setAdapter(folderAdapter);
        }
    }

    private void showEmptyState() {
        folderRecyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        emptyStateLayout.setVisibility(View.GONE);
        folderRecyclerView.setVisibility(View.VISIBLE);
    }
}