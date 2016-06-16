package com.royole.yogu.videoplayerlibrary;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * 视频播放器视图
 * Author  yogu
 * Since  2016/6/15
 */
public class VideoControllerView extends FrameLayout {
    private static final String Tag = "VideoControllerView";
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private MediaPlayerControl mPlayer;
    private ViewGroup mAnchor;
    private View mRoot;
    private Context mContext;
    /**
     * 控件
     */
    private ImageButton mPrevBtn;//上一个
    private ImageButton mRewBtn;//快退
    private ImageButton mPauseBtn;//暂停播放按钮
    private ImageButton mfwdBtn;//快进
    private ImageButton mNextBtn;//下一个
    private ImageButton mFullScreenBtn;
    private SeekBar mProgress;//进度条
    private TextView mEndTime;
    private TextView mCurrentTime;

    private boolean mShowing;//控制界面是否显示
    private boolean mDragging;
    private static final int sDefaultTimeout = 3000;//默认3s控制界面消失
    private Handler mHandler = new MessageHandler(this);

    public VideoControllerView(Context context) {
        super(context);
        mContext = context;
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlayer();
        updateFullScreen();
    }

    /**
     * 设置视频播放器视图
     *
     * @param view
     */
    public void setAnchorView(ViewGroup view) {
        mAnchor = view;
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        removeAllViews();
        View v = setControllerView();
        addView(v, frameParams);
    }

    /**
     * 设置视频控制视图
     *
     * @return
     */
    public View setControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.video_controller, null);
        initControllerView(mRoot);
        return mRoot;
    }

    /**
     * 初始化视频控制控件
     *
     * @param v
     */
    private void initControllerView(View v) {
        mPrevBtn = (ImageButton) v.findViewById(R.id.prevBtn);

        mRewBtn = (ImageButton) v.findViewById(R.id.rewBtn);
        mRewBtn.setOnClickListener(mRewListener);

        mPauseBtn = (ImageButton) v.findViewById(R.id.pauseBtn);
        mPauseBtn.setOnClickListener(mPauseListener);

        mfwdBtn = (ImageButton) v.findViewById(R.id.fwdBtn);
        mRewBtn.setOnClickListener(mfwdListener);

        mNextBtn = (ImageButton) v.findViewById(R.id.nextBtn);

        mFullScreenBtn = (ImageButton) v.findViewById(R.id.fullScreen);
        mFullScreenBtn.setOnClickListener(mFullscreenListener);

        mProgress = (SeekBar) v.findViewById(R.id.controllerBar);
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setMax(1000);

        mEndTime = (TextView) v.findViewById(R.id.time);
        mCurrentTime = (TextView) v.findViewById(R.id.curTime);
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            //doPauseResume();
            show(sDefaultTimeout);
        }
    };

    private View.OnClickListener mFullscreenListener = new View.OnClickListener() {
        public void onClick(View v) {
            //doToggleFullscreen();
            show(sDefaultTimeout);
        }
    };
    private View.OnClickListener mRewListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mPlayer == null) {
                return;
            }
            int pos = mPlayer.getCurrentPosition();
            pos -= 5000; // milliseconds
            mPlayer.seekTo(pos);
            setProgress();
            show(sDefaultTimeout);
        }
    };

    private View.OnClickListener mfwdListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mPlayer == null) {
                return;
            }

            int pos = mPlayer.getCurrentPosition();
            pos += 15000; // milliseconds
            mPlayer.seekTo(pos);
            setProgress();

            show(sDefaultTimeout);
        }
    };
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            Log.d(Tag, "onStartTrackingTouch");
            show(3600000);

            mDragging = true;
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            Log.d(Tag, "onProgressChanged");
            if (mPlayer == null) {
                return;
            }

            if (!fromuser) {
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            mPlayer.seekTo((int) newposition);
            if (mCurrentTime != null)
                mCurrentTime.setText(StringUtils.stringForTime((int) newposition));
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlayer();
            show(sDefaultTimeout);

            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    /**
     * 更新播放按钮状态
     */
    public void updatePausePlayer() {
        if (mPlayer == null || mRoot == null || mPauseBtn == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPauseBtn.setImageResource(R.drawable.ic_media_pause);
        } else {
            mPauseBtn.setImageResource(R.drawable.ic_media_play);
        }
    }

    public void updateFullScreen() {
        if (mPlayer == null || mRoot == null || mFullScreenBtn == null) {
            return;
        }
        if (mPlayer.isFullScreen()) {

        }
    }

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();//获取音频总时长
        Log.d(Tag, "position:" + position + ",duration:" + duration);

        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();//获取缓冲百分比
            mProgress.setSecondaryProgress(percent * 10);
        }
        if (mEndTime != null)
            mEndTime.setText(StringUtils.stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(StringUtils.stringForTime(position));
        return position;
    }

    private void disableUnsupportedButtons() {
        if (mPlayer == null) {
            return;
        }
        try {
            if (mPauseBtn != null && !mPlayer.canPause()) {
                mPauseBtn.setEnabled(false);
            }
            if (mRewBtn != null && !mPlayer.canSeekBackward()) {
                mRewBtn.setEnabled(false);
            }
            if (mfwdBtn != null && !mPlayer.canSeekForward()) {
                mfwdBtn.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {

        }
    }

    /**
     * 显示控制器
     */
    public void show() {
        show(sDefaultTimeout);
    }

    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {
            setProgress();
            if (mPauseBtn != null) {
                mPauseBtn.requestFocus();
            }
            disableUnsupportedButtons();

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
            );

            mAnchor.addView(this, tlp);
            mShowing = true;
        }
        updatePausePlayer();
        updateFullScreen();

        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    /**
     * 隐藏控制器
     */
    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    /**
     * 控制器显示隐藏句柄
     */
    private static class MessageHandler extends Handler {
        private WeakReference<VideoControllerView> mView;

        MessageHandler(VideoControllerView view) {
            mView = new WeakReference<VideoControllerView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoControllerView view = mView.get();
            if (view == null || view.mPlayer == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
                case SHOW_PROGRESS:
                    pos = view.setProgress();
                    if (!view.mDragging && view.mShowing && view.mPlayer.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }


    public interface MediaPlayerControl {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int pos);

        boolean isPlaying();

        int getBufferPercentage();

        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();

        boolean isFullScreen();

        void toggleFullScreen();
    }

}

