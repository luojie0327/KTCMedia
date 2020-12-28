package com.ktc.media.tv.sound;

public class SoundDefault {

    /**
     * 声音模式相关的五个值的默认值都存储在这里
     * 五个值分别是
     * 120Hz
     * 500Hz
     * 1.5KHz
     * 5KHz
     * 10KHz
     * <p>
     * 取值范围为：
     * MTK : -50 ~ 50
     * KTC : 0 ~ 100`
     */

    public static final int GAP_SOUND_MODE_OF_MTK_AND_KTC = 50;

    /**
     * sound mode - standard
     */
    public static final int DEFAULT_SOUND_MODE_STANDAER_VALUE_120HZ = 0;
    public static final int DEFAULT_SOUND_MODE_STANDAER_VALUE_500HZ = 0;
    public static final int DEFAULT_SOUND_MODE_STANDAER_VALUE_1_5KHZ = 0;
    public static final int DEFAULT_SOUND_MODE_STANDAER_VALUE_5KHZ = 0;
    public static final int DEFAULT_SOUND_MODE_STANDAER_VALUE_10KHZ = 0;

    /**
     * sound mode - music
     */
    public static final int DEFAULT_SOUND_MODE_MUSIC_VALUE_120HZ = 15;
    public static final int DEFAULT_SOUND_MODE_MUSIC_VALUE_500HZ = 15;
    public static final int DEFAULT_SOUND_MODE_MUSIC_VALUE_1_5KHZ = 0;
    public static final int DEFAULT_SOUND_MODE_MUSIC_VALUE_5KHZ = 15;
    public static final int DEFAULT_SOUND_MODE_MUSIC_VALUE_10KHZ = 15;

    /**
     * sound mode - movie
     */
    public static final int DEFAULT_SOUND_MODE_MOVIE_VALUE_120HZ = 20;
    public static final int DEFAULT_SOUND_MODE_MOVIE_VALUE_500HZ = 20;
    public static final int DEFAULT_SOUND_MODE_MOVIE_VALUE_1_5KHZ = 0;
    public static final int DEFAULT_SOUND_MODE_MOVIE_VALUE_5KHZ = 5;
    public static final int DEFAULT_SOUND_MODE_MOVIE_VALUE_10KHZ = 5;

    /**
     * sound mode - user
     */
    public static final int DEFAULT_SOUND_MODE_USER_VALUE_120HZ = 50;
    public static final int DEFAULT_SOUND_MODE_USER_VALUE_500HZ = 50;
    public static final int DEFAULT_SOUND_MODE_USER_VALUE_1_5KHZ = 50;
    public static final int DEFAULT_SOUND_MODE_USER_VALUE_5KHZ = 50;
    public static final int DEFAULT_SOUND_MODE_USER_VALUE_10KHZ = 50;
}
