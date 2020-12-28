package com.ktc.media.tv.picture;

import android.content.Context;
import android.provider.Settings;

import com.ktc.media.tv.util.SharePreferenceUtil;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;

import static com.ktc.media.tv.picture.PictureConstant.KEY_AN_PICTURE_BACKLIGHT;
import static com.ktc.media.tv.picture.PictureConstant.KEY_AN_PICTURE_BRIGHTNESS;
import static com.ktc.media.tv.picture.PictureConstant.KEY_AN_PICTURE_CONTRAST;
import static com.ktc.media.tv.picture.PictureConstant.KEY_AN_PICTURE_GAMMA;
import static com.ktc.media.tv.picture.PictureConstant.KEY_AN_PICTURE_HUE;
import static com.ktc.media.tv.picture.PictureConstant.KEY_AN_PICTURE_MODE_DOLBY;
import static com.ktc.media.tv.picture.PictureConstant.KEY_AN_PICTURE_SATURATION;
import static com.ktc.media.tv.picture.PictureConstant.KEY_AN_PICTURE_SHARPNESS;
import static com.ktc.media.tv.picture.PictureConstant.PICTURE_BACKLIGHT;
import static com.ktc.media.tv.picture.PictureConstant.PICTURE_BRIGHTNESS;
import static com.ktc.media.tv.picture.PictureConstant.PICTURE_CONTRAST;
import static com.ktc.media.tv.picture.PictureConstant.PICTURE_GAMMMA;
import static com.ktc.media.tv.picture.PictureConstant.PICTURE_HUE;
import static com.ktc.media.tv.picture.PictureConstant.PICTURE_SATURATION;
import static com.ktc.media.tv.picture.PictureConstant.PICTURE_SHARPNESS;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_BRIGHT_VALUE_BRIGHTNESS;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_BRIGHT_VALUE_CONTRAST;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_BRIGHT_VALUE_HUE;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_BRIGHT_VALUE_SATURATION;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_BRIGHT_VALUE_SHARPNESS;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_SOFT_VALUE_BRIGHTNESS;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_SOFT_VALUE_CONTRAST;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_SOFT_VALUE_HUE;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_SOFT_VALUE_SATURATION;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_SOFT_VALUE_SHARPNESS;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_STANDAER_VALUE_BRIGHTNESS;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_STANDAER_VALUE_CONTRAST;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_STANDAER_VALUE_HUE;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_STANDAER_VALUE_SATURATION;
import static com.ktc.media.tv.picture.PictureDefault.DEFAULT_PICTURE_MODE_STANDAER_VALUE_SHARPNESS;

public class PictureModeManager {

    /**
     * 底层用的 key, TV 的
     */
    private static final String PICTURE_MODE = MtkTvConfigType.CFG_VIDEO_PIC_MODE;

    /**
     * 上层用的 key, AN 的
     */
    private static final String KEY_AN_PICTURE_MODE = "picture_mode";

    private static final String KEY_AN_PIC_MODE_USER_BRIGHTNESS = "user_brightness";
    private static final String KEY_AN_PIC_MODE_USER_CONTRAST = "user_contrast";
    private static final String KEY_AN_PIC_MODE_USER_SATURATION = "user_saturation";
    private static final String KEY_AN_PIC_MODE_USER_HUE = "user_hue";
    private static final String KEY_AN_PIC_MODE_USER_SHARPNESS = "user_sharpness";

    /**
     * User 模式，传入的值为 0 ，
     * Standard 模式，传入的值为 7 ，
     * 以此类推
     */
    public static final int VALUE_PICTURE_MODE_User = 0;
    public static final int VALUE_PICTURE_MODE_Standard = 7;
    public static final int VALUE_PICTURE_MODE_Vivid = 3;
    public static final int VALUE_PICTURE_MODE_Sport = 2;
    public static final int VALUE_PICTURE_MODE_Movie = 9;
    public static final int VALUE_PICTURE_MODE_Game = 10;
    public static final int VALUE_PICTURE_MODE_Energy_Saving = 11;
    public static final int VALUE_PICTURE_MODE_AIPQ = 12;

    public static final int KTC_PICTURE_MODE_STANDARD = VALUE_PICTURE_MODE_Standard;
    public static final int KTC_PICTURE_MODE_SOFT = VALUE_PICTURE_MODE_Movie;
    public static final int KTC_PICTURE_MODE_USER = VALUE_PICTURE_MODE_User;
    public static final int KTC_PICTURE_MODE_BRIGHT = VALUE_PICTURE_MODE_Vivid;
    public static final int KTC_PICTURE_MODE_UNKNOWN = VALUE_PICTURE_MODE_Standard;

    /**
     * 如果是 KTC 规格外的图像模式，定义为标准
     */
    private static final int INDEX_KTC_PICTURE_MODE_UNKNOWN = 0;
    /**
     * 顺序写死, 和 arrays.xml 里 array_picture_mode 的顺序保持一致
     * index - zoomMode
     * 0     - 标准
     * 1     - 柔和
     * 2     - 用户
     * 3     - 亮丽
     */
    private static int[] KtcPicModeArray = new int[]{
            KTC_PICTURE_MODE_STANDARD,
            KTC_PICTURE_MODE_SOFT,
            KTC_PICTURE_MODE_USER,
            KTC_PICTURE_MODE_BRIGHT,
    };

    private MtkTvConfig mtkTvConfig;

    private Context mContext;
    private static PictureModeManager pictureModeManager;

    private PictureModeManager() {
        super();
    }

    private PictureModeManager(Context context) {
        super();
        mContext = context.getApplicationContext();
        mtkTvConfig = MtkTvConfig.getInstance();
    }

    public static PictureModeManager getInstance(Context context) {
        if (pictureModeManager == null) {
            synchronized (PictureModeManager.class) {
                if (pictureModeManager == null) {
                    pictureModeManager = new PictureModeManager(context);
                }
            }
        }
        return pictureModeManager;
    }

    /**
     * 直接返回下标
     */
    public int getKtcPictureModeValueInt() {
        int picV = getPictureModeValueInt();
        for (int p = 0; p < KtcPicModeArray.length; p++) {
            if (picV == KtcPicModeArray[p]) {
                return p;
            }
        }
        return INDEX_KTC_PICTURE_MODE_UNKNOWN;
    }

    public void setKtcPictureMode(int ktcPictureModeIndex) {
        switch (ktcPictureModeIndex) {
            case 0:
                setPictureMode(KTC_PICTURE_MODE_STANDARD);
                break;
            case 1:
                setPictureMode(KTC_PICTURE_MODE_SOFT);
                break;
            case 2:
                setPictureMode(KTC_PICTURE_MODE_USER);
                break;
            case 3:
                setPictureMode(KTC_PICTURE_MODE_BRIGHT);
                break;
            default:
                break;
        }
    }

    private void setPictureMode(int pictureModeValue) {
        mtkTvConfig.setConfigValue(PICTURE_MODE, pictureModeValue);
    }

    private int getPictureModeValueInt() {
        return mtkTvConfig.getConfigValue(PICTURE_MODE);
    }

    public void setBackLight(int backlight) {
        int real_backLight = 100 - backlight;
        mtkTvConfig.setConfigValue(PICTURE_BACKLIGHT, real_backLight);
    }

    public int getBackLight() {
        return 100 - mtkTvConfig.getConfigValue(PICTURE_BACKLIGHT);
    }

    private void setBrightness(int brightness) {
        mtkTvConfig.setConfigValue(PICTURE_BRIGHTNESS, brightness);
    }

    public int getBrightness() {
        return mtkTvConfig.getConfigValue(PICTURE_BRIGHTNESS);
    }

    private void setContrast(int contrast) {
        mtkTvConfig.setConfigValue(PICTURE_CONTRAST, contrast);
    }

    public int getContrast() {
        return mtkTvConfig.getConfigValue(PICTURE_CONTRAST);
    }

    private void setSaturation(int saturation) {
        mtkTvConfig.setConfigValue(PICTURE_SATURATION, saturation);
    }

    public int getSaturation() {
        return mtkTvConfig.getConfigValue(PICTURE_SATURATION);
    }

    private void setHue(int hue) {
        hue = hue - 50;
        mtkTvConfig.setConfigValue(PICTURE_HUE, hue);
    }

    public int getHue() {
        return mtkTvConfig.getConfigValue(PICTURE_HUE) + 50;
    }

    private void setSharpness(int sharpness) {
        mtkTvConfig.setConfigValue(PICTURE_SHARPNESS, sharpness);
    }

    public int getSharpness() {
        return mtkTvConfig.getConfigValue(PICTURE_SHARPNESS) ;
    }

    private void setGamma(int gamma) {
        mtkTvConfig.setConfigValue(PICTURE_GAMMMA, gamma);
    }

    public int getGamma() {
        return mtkTvConfig.getConfigValue(PICTURE_GAMMMA);
    }

    public void setUserBrightness(int userBrightness) {
        setBrightness(userBrightness);
    }


    public void setUserContrast(int userContrast) {
        setContrast(userContrast);
    }

    public void setUserHue(int userHue) {
        setHue(userHue);
    }

    public void setUserSaturation(int userSaturation) {
        setSaturation(userSaturation);
    }

    public void setUserSharpness(int userSharpness) {
        setSharpness(userSharpness);
    }

}
