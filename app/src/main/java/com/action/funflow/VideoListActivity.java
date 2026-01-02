package com.action.funflow;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VideoListActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEOS = "videos";
    public static final String EXTRA_FOLDER_NAME = "folder_name";

    private RecyclerView videoRecyclerView;
    private VideoListAdapter videoAdapter;
    private ArrayList<VideoFile> videos;
    private String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from intent
        videos = getIntent().getParcelableArrayListExtra(EXTRA_VIDEOS);
        folderName = getIntent().getStringExtra(EXTRA_FOLDER_NAME);

        if (videos == null || videos.isEmpty()) {
            finish();
            return;
        }

        // Set title
        if (getActionBar() != null) {
            getActionBar().setTitle(folderName);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(folderName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        videoAdapter = new VideoListAdapter(this, videos);
        videoRecyclerView.setAdapter(videoAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
