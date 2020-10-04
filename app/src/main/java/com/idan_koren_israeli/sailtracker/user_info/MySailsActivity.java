package com.idan_koren_israeli.sailtracker.user_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.home.HomeActivity;

public class MySailsActivity extends AppCompatActivity {

    private FloatingActionButton backButton;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sails);
        findViews();
        setListeners();
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Back to the caller activity, which is not finished
                finish();
            }
        });
    }

    private void findViews(){
        backButton = findViewById(R.id.my_sails_FAB_back);
        recyclerView = findViewById(R.id.my_sails_RYC_recycler);


    }
}