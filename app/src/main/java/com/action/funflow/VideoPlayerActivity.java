package com.action.funflow;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private VideoPagerAdapter adapter;
    private ArrayList<VideoFile> videos;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Hide status bar and navigation bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        // Disable overscroll mode to allow smooth wrapping
        viewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Get videos from intent
        videos = getIntent().getParcelableArrayListExtra("videos");
        currentPosition = getIntent().getIntExtra("currentPosition", 0);

        if (videos == null || videos.isEmpty()) {
            finish();
            return;
        }

        adapter = new VideoPagerAdapter(this, videos);
        viewPager.setAdapter(adapter);

        // Set offscreen page limit for smooth scrolling
        viewPager.setOffscreenPageLimit(2);

        // Set initial position
        viewPager.setCurrentItem(currentPosition, false);

        // Register page callback to manage video playback
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                adapter.playVideo(position);
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    adapter.pauseCurrentVideo();
                }
            }
        });

        // Auto play the first video
        viewPager.post(() -> adapter.playVideo(viewPager.getCurrentItem()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.pauseCurrentVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int currentPosition = viewPager.getCurrentItem();
        adapter.playVideo(currentPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.releasePlayers();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        adapter.releasePlayers();
    }
}
