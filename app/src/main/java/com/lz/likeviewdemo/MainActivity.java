package com.lz.likeviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    LikeView mLikeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLikeView= findViewById(R.id.like_view);
    }

    public void add(View view) {
        mLikeView.add();
    }

    public void subtraction(View view) {
        mLikeView.subtraction();
    }
}
