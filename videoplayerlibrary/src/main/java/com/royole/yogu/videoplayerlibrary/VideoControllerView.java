package com.royole.yogu.videoplayerlibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * Copyright (C) 2015, Royole Corporation all rights reserved.
 * Author  yogu
 * Since  2016/6/15
 */
public class VideoControllerView extends FrameLayout {
    private static final String Tag = "VideoControllerView";
    private View mRoot;
    private Context mContext;
    private ImageButton mPrevBtn;
    private ImageButton mRewBtn;
    private ImageButton mPauseBtn;
    private ImageButton mfwdBtn;
    private ImageButton mNextBtn;
    public View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.video_controller, null);
        initControllerView(mRoot);
        return mRoot;
    }

    /**
     * 初始化控件
     * @param v
     */
    private void initControllerView(View v){
        mPrevBtn = (ImageButton)v.findViewById(R.id.prevBtn);
        mRewBtn = (ImageButton)v.findViewById(R.id.rewBtn);
        mPauseBtn = (ImageButton)v.findViewById(R.id.pauseBtn);
        mfwdBtn = (ImageButton)v.findViewById(R.id.fwdBtn);
        mNextBtn = (ImageButton)v.findViewById(R.id.nextBtn);
    }


}
