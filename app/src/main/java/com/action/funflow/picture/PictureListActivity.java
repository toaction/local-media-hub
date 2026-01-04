package com.action.funflow.picture;

import android.os.Bundle;

import com.action.funflow.R;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PictureListActivity extends AppCompatActivity {

    public static final String EXTRA_PICTURES = "pictures";
    public static final String EXTRA_FOLDER_NAME = "folder_name";

    private RecyclerView pictureRecyclerView;
    private PictureListAdapter pictureAdapter;
    private ArrayList<PictureFile> pictures;
    private String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_picture_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from intent
        pictures = getIntent().getParcelableArrayListExtra(EXTRA_PICTURES);
        folderName = getIntent().getStringExtra(EXTRA_FOLDER_NAME);

        if (pictures == null || pictures.isEmpty()) {
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

        pictureRecyclerView = findViewById(R.id.pictureRecyclerView);
        pictureRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        pictureAdapter = new PictureListAdapter(this, pictures);
        pictureRecyclerView.setAdapter(pictureAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
