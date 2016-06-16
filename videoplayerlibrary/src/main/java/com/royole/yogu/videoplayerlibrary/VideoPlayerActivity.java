package com.royole.yogu.videoplayerlibrary;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;

public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl {

    private SurfaceView mVideoSurface;
    private MediaPlayer mPlayer;
    private VideoControllerView mController;
    private String mPath;
    private int mCurrentPosition;

    //lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.acitivity_video_player);
        mPath = getIntent().getStringExtra("path");

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt("videoPosition", 0);
        }

        mVideoSurface = (SurfaceView) findViewById(R.id.videoSurfaceView);
        SurfaceHolder videoHolder = mVideoSurface.getHolder();
        videoHolder.addCallback(this);

        mController = new VideoControllerView(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("videoPosition", mCurrentPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentPosition = this.getCurrentPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
    // End lifecycle

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mController.show();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // 创建一个MediaPlayer对象
            mPlayer = new MediaPlayer();
            // 设置播放的视频数据源
            mPlayer.setDataSource(this, Uri.parse(mPath));
            // 设置AudioStreamType
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 将视频输出到SurfaceView
            mPlayer.setDisplay(holder);
            // 播放准备，使用异步方式，配合OnPreparedListener
            mPlayer.prepareAsync();
            // 设置相关的监听器
            mPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        mController.setMediaPlayer(this);
        mController.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        mController.show();
        mPlayer.start();
        this.seekTo(mCurrentPosition);
        mController.updatePausePlayer();
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (mPlayer != null){
            return mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    public void seekTo(int i) {
        if (mPlayer != null) {
            mPlayer.seekTo(i);
        }
    }

    @Override
    public void start() {
        if (mPlayer != null) {
            mPlayer.start();
        }
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }
    // End VideoMediaController.MediaPlayerControl

}
