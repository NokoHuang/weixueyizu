package com.weixue.myUI;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class MyVideoView extends VideoView {

    private int mVideoWidth;
    private int mVideoHeight;

    public MyVideoView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
    }

    public MyVideoView(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // TODO Auto-generated method stub

           //下面的代码是让视频的播放的长宽是根据你设置的参数来决定

            int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
            int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
            setMeasuredDimension(width, height);
    }
}
