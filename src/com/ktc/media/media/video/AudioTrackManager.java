package com.ktc.media.media.video;

import android.content.Context;
import android.media.Metadata;
import android.util.Log;

import com.ktc.media.R;
import com.ktc.media.media.business.video.VideoPlayView;
import com.mstar.android.media.AudioTrackInfo;
import com.mstar.android.media.MMediaPlayer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class AudioTrackManager {

    private static final String TAG = "AudioTrackManager";
    private static AudioTrackManager mAudioTrackManager = null;

    private AudioTrackManager() {
    }

    /**
     * get the {@link AudioTrackManager} instances with singleton patteren
     *
     * @return AudioTrackManager
     */
    public static AudioTrackManager getInstance() {
        if (null == mAudioTrackManager) {
            mAudioTrackManager = new AudioTrackManager();
        }
        return mAudioTrackManager;
    }

    /**
     * For track information.
     * !need player is in play state
     *
     * @param typeIsAudio
     * @return
     */

    public AudioTrackInfo getAudioTrackInfo(MMediaPlayer mMMediaPlayer, final boolean typeIsAudio) {
        if (null != mMMediaPlayer) {
            Log.i(TAG, "***getAudioTrackInfo**");
            try {
                return mMMediaPlayer.getAudioTrackInfo(typeIsAudio);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public List<String> getTrackLanguageInfo(MMediaPlayer mMMediaPlayer, VideoPlayView videoPlayView) {
        int count = getAudioTrackCount(mMMediaPlayer);
        List<String> language = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            MMediaPlayer.MsTrackInfo trackInfo = videoPlayView.getMsTrackInfo(i);
            if (trackInfo != null) {
                language.add(trackInfo.getLanguage());
            }
        }
        return language;
    }

    /**
     * Settings you want to play the audio track, data from getAudioTrackInfo
     * return values.
     *
     * @param track
     */

    public void setAudioTrack(MMediaPlayer mMMediaPlayer, int track) {
        if (mMMediaPlayer != null) {
            mMMediaPlayer.setAudioTrack(track);
        }
    }

    public int getAudioTrackCount(MMediaPlayer mMMediaPlayer) {
        Log.i(TAG, "getAudioTrackCount: (mMMediaPlayer == null) = " + (mMMediaPlayer == null));
        if (mMMediaPlayer == null)
            return 1;

        Metadata data = mMMediaPlayer.getMetadata(true, true);
        Log.i(TAG, "getAudioTrackCount: (data != null) = " + (data != null));
        int totalTrackNum = 1;
        if (data != null) {
            if (data.has(Metadata.TOTAL_TRACK_NUM)) {
                totalTrackNum = data.getInt(Metadata.TOTAL_TRACK_NUM);
                Log.i(TAG, "getAudioTrackCount: totalTrackNum = " + totalTrackNum);
            }
            try {
                Class clz = Class.forName("android.media.Metadata");
                Method get = clz.getDeclaredMethod("recycleParcel");
                // http://hcgit:8080/#/q/bac5dbcffd9773356a969254d9c8733a419df8e4
                data.recycleParcel();
                Log.i(TAG, "recycleParcel");
            } catch (Exception e) {
                Log.i(TAG, "Can't find android.media.Metadata API recycleParcel !");
                e.printStackTrace();
            }
        }
        return totalTrackNum;
    }

    public int getCurrentAudioTrackId(MMediaPlayer mMMediaPlayer) {
        Log.i(TAG, "getCurrentAudioTrackId");
        if (mMMediaPlayer == null)
            return 1;
        Metadata data = mMMediaPlayer.getMetadata(true, true);
        int currentAudioTrackId = 0;
        if (data != null) {
            int CurrentTrackId = 0;
            int count = 0;
            if (data.has(Metadata.TOTAL_TRACK_NUM)) {
                count = data.getInt(Metadata.TOTAL_TRACK_NUM);
            }
            if (data.has(Metadata.CURRENT_AUDIO_TRACK_ID)) {
                CurrentTrackId = data.getInt(Metadata.CURRENT_AUDIO_TRACK_ID);
                if (count != 0) {
                    currentAudioTrackId = CurrentTrackId % count;
                }
            }
            try {
                Class clz = Class.forName("android.media.Metadata");
                Method get = clz.getDeclaredMethod("recycleParcel");
                // http://hcgit:8080/#/q/bac5dbcffd9773356a969254d9c8733a419df8e4
                data.recycleParcel();
                Log.i(TAG, "recycleParcel");
            } catch (Exception e) {
                Log.i(TAG, "Can't find android.media.Metadata API recycleParcel !");
                e.printStackTrace();
            }
        }
        return currentAudioTrackId;
    }
}
