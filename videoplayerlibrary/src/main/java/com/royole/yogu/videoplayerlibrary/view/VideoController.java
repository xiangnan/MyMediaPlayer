package com.royole.yogu.videoplayerlibrary.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.royole.yogu.videoplayerlibrary.R;
import com.royole.yogu.videoplayerlibrary.utils.StringUtils;

import java.lang.ref.WeakReference;

/**
 * Custom MediaController
 * Author  yogu
 * Since  2016/6/21
 */


public class VideoController extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private String Tag = "VideoController";
    // the video controller state
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private ImageButton mRewindBtn;// rewind
    private ImageButton mPauseBtn;// play or pause
    private ImageButton mForwardBtn;// fast forward
    private ImageButton mExpandBtn; // expand or shrink screen
    private SeekBar mProgress;// progress seekbar
    private TextView mEndTime; // total time
    private TextView mCurrentTime; // current time


    private boolean mShowing;// if controller is showing
    private boolean mDragging;// if seekbar is dragging
    private static final int sDefaultTimeout = 3000;//fade out after 3s on default

    private Handler mHandler = new MessageHandler(this);
    private MediaControlImpl mPlayer;
    private ViewGroup mAnchor;
    private View mRoot;
    private Context mContext;

    public VideoController(Context context) {
        super(context);
        mContext = context;
    }

    public VideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    // Private Method

    /**
     * init widget
     */
    private void initView(View v) {
        mRewindBtn = (ImageButton) v.findViewById(R.id.rewBtn);
        mPauseBtn = (ImageButton) v.findViewById(R.id.pauseBtn);
        mForwardBtn = (ImageButton) v.findViewById(R.id.fwdBtn);
        mExpandBtn = (ImageButton) v.findViewById(R.id.fullScreen);
        mRewindBtn.setOnClickListener(this);
        mPauseBtn.setOnClickListener(this);
        mForwardBtn.setOnClickListener(this);
        mExpandBtn.setOnClickListener(this);
        mProgress = (SeekBar) v.findViewById(R.id.controllerBar);
        mProgress.setOnSeekBarChangeListener(this);
        mProgress.setMax(1000);
        mEndTime = (TextView) v.findViewById(R.id.time);
        mCurrentTime = (TextView) v.findViewById(R.id.curTime);
    }
    private void doPauseResume() {
        if (mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    private void doToggleFullscreen() {
        if (mPlayer == null) {
            return;
        }

        mPlayer.toggleFullScreen();
    }

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        Log.d(Tag, "position:" + position + ",duration:" + duration);

        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                Log.d(Tag,"pos:"+pos);
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            Log.d(Tag,"percent:"+percent);
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
            if (mRewindBtn != null && !mPlayer.canSeekBackward()) {
                mRewindBtn.setEnabled(false);
            }
            if (mForwardBtn != null && !mPlayer.canSeekForward()) {
                mRewindBtn.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {

        }
    }
    // End Private - M
    /**
     * show controller
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
        updatePausePlay();
        updateFullScreen();

        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    /**
     * hide controller
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
     * switch pause/play
     */
    public void updatePausePlay() {
        if (mPlayer == null || mRoot == null || mPauseBtn == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPauseBtn.setImageResource(R.drawable.ic_media_pause);
        } else {
            mPauseBtn.setImageResource(R.drawable.ic_media_play);
        }
    }

    /**
     * switch expand/shrink
     */
    public void updateFullScreen() {
        if (mPlayer == null || mRoot == null || mExpandBtn == null) {
            return;
        }
        if (mPlayer.isFullScreen()) {
            mExpandBtn.setImageResource(R.drawable.ic_media_fullscreen_stretch);
        }else{
            mExpandBtn.setImageResource(R.drawable.ic_media_fullscreen_shrink);
        }
    }

    public void setMediaPlayer(MediaControlImpl player) {
        mPlayer = player;
        updatePausePlay();
        updateFullScreen();
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     * @param view The view to which to anchor the controller when it is visible.
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
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     * @return The controller view.
     */
    public View setControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.video_controller, null);
        initView(mRoot);
        return mRoot;
    }

    // Implement View.OnClickListener
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rewBtn){
            if (mPlayer == null) {
                return;
            }
            int pos = mPlayer.getCurrentPosition();
            pos -= 15000; // milliseconds
            mPlayer.seekTo(pos);
            setProgress();
            show(sDefaultTimeout);
        }else if (v.getId() == R.id.fwdBtn){
            if (mPlayer == null) {
                return;
            }
            int pos = mPlayer.getCurrentPosition();
            pos += 15000; // milliseconds
            mPlayer.seekTo(pos);
            setProgress();
            show(sDefaultTimeout);
        }else if(v.getId() == R.id.pauseBtn){
            doPauseResume();
            show(sDefaultTimeout);
        }else if(v.getId() == R.id.fullScreen){
            doToggleFullscreen();
            show(sDefaultTimeout);
        }
    }
    // End  View.OnClickListener

    // Implement SeekBar.OnSeekBarChangeListener
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mPlayer == null) {
            return;
        }

        if (!fromUser) {
            // We're not interested in programmatically generated changes to
            // the progress bar's position.
            return;
        }

        long duration = mPlayer.getDuration();
        long newposition = (duration * progress) / 1000L;
        mPlayer.seekTo((int) newposition);
        if (mCurrentTime != null)
            mCurrentTime.setText(StringUtils.stringForTime((int) newposition));
    }

    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        show(3600000);

        mDragging = true;

        // By removing these pending progress messages we make sure
        // that a) we won't update the progress while the user adjusts
        // the seekbar and b) once the user is done dragging the thumb
        // we will post one of these messages to the queue again and
        // this ensures that there will be exactly one message queued up.
        mHandler.removeMessages(SHOW_PROGRESS);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mDragging = false;
        setProgress();
        updatePausePlay();
        show(sDefaultTimeout);

        // Ensure that progress is properly updated in the future,
        // the call to show() does not guarantee this because it is a
        // no-op if we are already showing.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }
    // End SeekBar.OnSeekBarChangeListener

    private class MessageHandler extends Handler {
        private WeakReference<VideoController> mView;
        public MessageHandler(VideoController view) {
            mView = new WeakReference<VideoController>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoController view = mView.get();
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
    public interface MediaControlImpl {
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
