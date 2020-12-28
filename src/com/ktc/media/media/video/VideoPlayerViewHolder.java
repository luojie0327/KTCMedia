package com.ktc.media.media.video;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.text.Html;
import android.text.Spanned;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.media.business.video.VideoPlayView;
import com.ktc.media.media.view.BorderTextViews;
import com.ktc.media.media.view.MediaControllerView;


public class VideoPlayerViewHolder {

    private static final String TAG = VideoPlayerViewHolder.class.getSimpleName();
    protected RelativeLayout mVideoPlayLayout;
    public VideoPlayView mVideoPlayView;
    protected MediaControllerView mMediaControllerView;
    private VideoPlayerActivity mVideoPlayerActivity;
    protected BorderTextViews videoPlayerTextView;
    private SurfaceView mImageSubtitleSurfaceView;

    public VideoPlayerViewHolder(VideoPlayerActivity videoPlayerActivity) {
        mVideoPlayerActivity = videoPlayerActivity;
        findView();
    }

    private void findView() {
        mVideoPlayLayout = (RelativeLayout) mVideoPlayerActivity.findViewById(R.id.video_play_layout);
        mVideoPlayView = (VideoPlayView) mVideoPlayerActivity.findViewById(R.id.video_play_view);
        mMediaControllerView = (MediaControllerView) mVideoPlayerActivity.findViewById(R.id.video_play_controller);
        mMediaControllerView.setTranslationZ(2);
        initSubtitleView();
    }

    private void initSubtitleView() {
        mImageSubtitleSurfaceView = (SurfaceView) mVideoPlayerActivity.findViewById(R.id.videoPlayerImageSurfaceView);
        mImageSubtitleSurfaceView.getHolder().setFormat(
                PixelFormat.RGBA_8888);
        mImageSubtitleSurfaceView.setBackgroundColor(Color.TRANSPARENT);
        //mImageSubtitleSurfaceView.setZOrderMediaOverlay(true);
        videoPlayerTextView = (BorderTextViews) mVideoPlayerActivity.findViewById(R.id.firstBorderTextView);
        videoPlayerTextView.setVisibility(View.GONE);
    }

    public SurfaceView getImageSubtitleSurfaceView() {
        return mImageSubtitleSurfaceView;
    }

    public void setSubTitleText(String str) {
        if (str.length() != 0) {
            str = "\u200E " + str;
        }
        // For RTL language, the BIDI algorithm determines the order of punctuation,
        // and we need to add \u200E character after the line break.
        str = str.replace("\n", "\n\u200E");
        //Spanned spanned = Html.fromHtml(str);
        if (videoPlayerTextView != null) {
            videoPlayerTextView.setText(str);
        }
    }

    public void setSubtitleSize() {
        // left: large, medium (35 sp), small, big
        float size = 35.0f;
        float zoomRate = 1.0f;
        videoPlayerTextView.setTextSize(size * zoomRate);
    }

    public BorderTextViews getSubtitleTextView() {
        return videoPlayerTextView;
    }

    public SurfaceView getSubtitleImageView() {
        return mImageSubtitleSurfaceView;
    }

    public void setSubtitleTextViewVisible(boolean bvisible) {
        if (getSubtitleTextView() != null) {
            getSubtitleTextView().setVisibility(bvisible ? View.VISIBLE : View.GONE);
        }
    }

    public void resetSubtitleTextView() {
        //TODO by dingyl for reset
        videoPlayerTextView.setVisibility(View.GONE);
    }
}

