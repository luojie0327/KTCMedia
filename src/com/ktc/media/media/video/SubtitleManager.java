package com.ktc.media.media.video;

import android.content.Context;
import android.view.SurfaceHolder;

import com.ktc.media.R;
import com.ktc.media.media.business.video.SubtitleTool;
import com.ktc.media.media.business.video.VideoPlayView;
import com.ktc.media.util.EncoderUtil;
import com.mstar.android.media.MMediaPlayer;
import com.mstar.android.media.SubtitleTrackInfo;

import java.util.ArrayList;
import java.util.List;

public class SubtitleManager {

    public static int mVideoSubtitleNo = 0;
    private static SubtitleManager mSubtitleManager = null;
    private static final int KEY_PARAMETER_SUBTITLE_ENCODE_TYPE = 3200;

    private SubtitleManager() {

    }

    public static SubtitleManager getInstance() {
        if (null == mSubtitleManager) {
            mSubtitleManager = new SubtitleManager();
        }
        return mSubtitleManager;
    }

    /**
     * Settings you want to play the subtitles encoding, a video can have
     * multiple subtitles such as English subtitles, Chinese subtitles.
     *
     * @param track
     */
    public void setSubtitleTrack(MMediaPlayer mMMediaPlayer, int track) {
        if (mMMediaPlayer != null) {
            mMMediaPlayer.setSubtitleTrack(track);
        }
    }

    /**
     * Set up additional caption text.
     *
     * @param Uri
     */
    public void setSubtitleDataSource(final MMediaPlayer mMMediaPlayer, final String Uri) {
        if (mMMediaPlayer != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String encode = EncoderUtil.getEncode(Uri);
                    mMMediaPlayer.setSubtitleDataSource(Uri, 0);
                    setSubtitleEncoding(mMMediaPlayer, encode);
                }
            }).start();
        }
    }

    /**
     * Open subtitle.
     */
    public void onSubtitleTrack(MMediaPlayer mMMediaPlayer) {
        if (mMMediaPlayer != null) {
            mMMediaPlayer.onSubtitleTrack();
        }
    }

    /**
     * close subtitle.
     */
    public void offSubtitleTrack(MMediaPlayer mMMediaPlayer) {
        if (mMMediaPlayer != null) {
            mMMediaPlayer.offSubtitleTrack();
        }
    }

    /**
     * Get subtitles data to a string, the string coding unified for utf-8.
     *
     * @return
     */
    public String getSubtitleData(MMediaPlayer mMMediaPlayer) {
        String str = "";
        if (mMMediaPlayer != null) {
            return mMMediaPlayer.getSubtitleData();
        }
        return str;
    }

    /**
     * Set the drawing of subtitles SurfaceHolder.
     *
     * @param sh
     */
    public void setSubtitleDisplay(MMediaPlayer mMMediaPlayer, SurfaceHolder sh) {
        if (mMMediaPlayer != null && sh != null) {
            mMMediaPlayer.setSubtitleDisplay(sh);
        }
    }

    /**
     * synchronize subtitle and video.
     * !need player is in play state
     *
     * @param time
     * @return
     */
    public int setSubtitleSync(MMediaPlayer mMMediaPlayer, int time) {
        if (null != mMMediaPlayer) {
            return mMMediaPlayer.setSubtitleSync(time);
        }
        return 0;
    }

    /**
     * For a concrete SubtitleTrackInfo object of subtitles.
     *
     * @param subtitlePosition !need player is in play state
     * @return
     */
    public SubtitleTrackInfo getSubtitleTrackInfo(MMediaPlayer mMMediaPlayer, int subtitlePosition) {
        if (null != mMMediaPlayer) {
            return mMMediaPlayer.getSubtitleTrackInfo(subtitlePosition);
        }
        return null;
    }

    /**
     * get subtitle info.
     * !need player is in play state
     *
     * @return
     */
    public SubtitleTrackInfo getAllSubtitleTrackInfo(MMediaPlayer mMMediaPlayer) {
        if (null != mMMediaPlayer) {
            return mMMediaPlayer.getAllSubtitleTrackInfo();
        }
        return null;
    }

    public List<SubtitleEntity> getSubtitleList(Context context, VideoPlayView videoPlayView, String currentPath) {
        int innerSubtitleCount = 0;
        SubtitleTrackInfo info = getInnerSubtitleInfo(videoPlayView);
        if (info != null) {
            innerSubtitleCount = info.getAllSubtitleCount();
        }
        String[] subtitleLanguage = new String[innerSubtitleCount];
        info.getSubtitleLanguageType(subtitleLanguage, false);
        List<SubtitleEntity> subtitleEntities = new ArrayList<>();
        SubtitleTool subtitleTool = new SubtitleTool(currentPath);
        List<String> externalSubtitleList = subtitleTool.getSubtitlePathList(0);
        subtitleEntities.add(new SubtitleEntity(SubtitleEntity.CLOSE, context.getString(R.string.video_subtitle_close)
                , null));
        for (int i = 0; i < innerSubtitleCount; i++) {
            SubtitleEntity subtitleEntity = new SubtitleEntity();
            subtitleEntity.setType(SubtitleEntity.INNER);
            subtitleEntity.setName(context.getString(R.string.video_subtitle_inner_title) + (i + 1)
                    + " " + subtitleLanguage[i]);
            subtitleEntities.add(subtitleEntity);
        }
        for (String s : externalSubtitleList) {
            SubtitleEntity subtitleEntity = new SubtitleEntity();
            subtitleEntity.setType(SubtitleEntity.EXTERNAL);
            subtitleEntity.setPath(s);
            subtitleEntity.setName(getSubtitleName(s));
            subtitleEntities.add(subtitleEntity);
        }
        return subtitleEntities;
    }

    private String getSubtitleName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private SubtitleTrackInfo getInnerSubtitleInfo(VideoPlayView videoPlayView) {
        SubtitleTrackInfo info = null;
        if (videoPlayView.isInPlaybackState()) {
            info = getAllSubtitleTrackInfo(
                    videoPlayView.getMMediaPlayer());
        }
        return info;
    }

    private int getInnerSubtitleCount(VideoPlayView videoPlayView) {
        SubtitleTrackInfo info = null;
        if (videoPlayView.isInPlaybackState()) {
            info = getAllSubtitleTrackInfo(
                    videoPlayView.getMMediaPlayer());
        }
        if (info != null) {
            return info.getAllInternalSubtitleCount();
        }
        return 0;
    }

    public static String getSubtitleEncoding(MMediaPlayer mMediaPlayer) {
        return mMediaPlayer.getStringParameter(KEY_PARAMETER_SUBTITLE_ENCODE_TYPE);
    }

    public static void setSubtitleEncoding(MMediaPlayer mMediaPlayer, String encodingType) {
        if (encodingType != null){
            mMediaPlayer.setParameter(KEY_PARAMETER_SUBTITLE_ENCODE_TYPE, encodingType);
        }
    }
}
