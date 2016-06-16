package com.royole.yogu.mymediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.royole.yogu.videoplayerlibrary.VideoPlayerActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra("path", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        startActivity(intent);
    }
}
