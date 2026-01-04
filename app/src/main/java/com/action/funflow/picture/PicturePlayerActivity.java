package com.action.funflow.picture;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.action.funflow.R;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class PicturePlayerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private PicturePagerAdapter adapter;
    private ArrayList<PictureFile> pictures;
    private int currentPosition;
    private LinearLayout dotsContainer;
    private ImageView[] dots;
    private TextView pageNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_player);

        // Hide status bar and navigation bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        viewPager = findViewById(R.id.viewPager);
        dotsContainer = findViewById(R.id.dotsContainer);
        pageNumberTextView = findViewById(R.id.pageNumberTextView);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL); // Horizontal swipe for pictures

        // Disable overscroll mode
        viewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Get pictures from intent
        pictures = getIntent().getParcelableArrayListExtra("pictures");
        currentPosition = getIntent().getIntExtra("currentPosition", 0);

        if (pictures == null || pictures.isEmpty()) {
            finish();
            return;
        }

        adapter = new PicturePagerAdapter(this, pictures);
        viewPager.setAdapter(adapter);

        // Set offscreen page limit for smooth scrolling
        viewPager.setOffscreenPageLimit(2);

        // Create dots indicator
        createDotsIndicator();

        // Set initial position
        viewPager.setCurrentItem(currentPosition, false);
        updateDotsIndicator(currentPosition);
        updatePageNumber(currentPosition);

        // Register page callback to track position
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                updateDotsIndicator(position);
                updatePageNumber(position);
            }
        });
    }

    private void createDotsIndicator() {
        dots = new ImageView[pictures.size()];
        for (int i = 0; i < pictures.size(); i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.dot_unselected);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dots[i].setLayoutParams(params);

            dotsContainer.addView(dots[i]);
        }
    }

    private void updateDotsIndicator(int position) {
        for (int i = 0; i < pictures.size(); i++) {
            if (i == position) {
                dots[i].setImageResource(R.drawable.dot_selected);
            } else {
                dots[i].setImageResource(R.drawable.dot_unselected);
            }
        }
    }

    private void updatePageNumber(int position) {
        String pageNumber = (position + 1) + "/" + pictures.size();
        pageNumberTextView.setText(pageNumber);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

