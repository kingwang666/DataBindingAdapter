package com.example.jiudeng009.databindingadapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created on 2016/11/12.
 * Author: wang
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onMultiType(View view) {
        Intent intent = new Intent(this, MultiTypeActivity.class);
        startActivity(intent);
    }

    public void onSticky(View view) {
        Intent intent = new Intent(this, StickyHeaderActivity.class);
        startActivity(intent);
    }
}
