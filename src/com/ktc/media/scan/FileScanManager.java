package com.ktc.media.scan;

import android.content.ContentValues;
import android.content.Context;

import com.ktc.media.constant.Constants;
import com.ktc.media.db.DBHelper;
import com.ktc.media.db.DatabaseUtil;
import com.ktc.media.model.BaseData;
import com.ktc.media.scan.observe.FileObserverInstance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileScanManager {

    private Context mContext;
    private int videoCount;
    private int pictureCount;
    private int musicCount;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
    public String[] videoTypes;
    public String[] musicTypes;
    public String[] pictureTypes;
    public int videoType = Constants.FILE_TYPE_VIDEO;
    public int musicType = Constants.FILE_TYPE_MUSIC;
    public int pictureType = Constants.FILE_TYPE_PICTURE;
    private FileObserverInstance mFileObserverInstance;

    public FileScanManager(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        videoTypes = FileTypeConstants.videoTypes;
        musicTypes = FileTypeConstants.musicTypes;
        pictureTypes = FileTypeConstants.pictureTypes;
        mFileObserverInstance = FileObserverInstance.getInstance();
    }

    public void insertData(final int fileType, final BaseData baseData) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ContentValues values = new ContentValues();
                    switch (fileType) {
                        case Constants.FILE_TYPE_VIDEO:
                            values.put(DBHelper.PATH_KEY, baseData.getPath());
                            values.put(DBHelper.TYPE_KEY, Constants.FILE_TYPE_VIDEO);
                            values.put(DBHelper.NAME_KEY, baseData.getName());
                            DatabaseUtil.getInstance(mContext).insertData(values, DBHelper.TB_VIDEO);
                            videoCount++;
                            break;
                        case Constants.FILE_TYPE_PICTURE:
                            values.put(DBHelper.PATH_KEY, baseData.getPath());
                            values.put(DBHelper.TYPE_KEY, Constants.FILE_TYPE_PICTURE);
                            values.put(DBHelper.NAME_KEY, baseData.getName());
                            DatabaseUtil.getInstance(mContext).insertData(values, DBHelper.TB_PICTURE);
                            pictureCount++;
                            break;
                        case Constants.FILE_TYPE_MUSIC:
                            values.put(DBHelper.PATH_KEY, baseData.getPath());
                            values.put(DBHelper.TYPE_KEY, Constants.FILE_TYPE_MUSIC);
                            values.put(DBHelper.NAME_KEY, baseData.getName());
                            DatabaseUtil.getInstance(mContext).insertData(values, DBHelper.TB_MUSIC);
                            musicCount++;
                            break;
                    }
                    sendRefreshAction(fileType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendRefreshAction(final int fileType) {
        switch (fileType) {
            case Constants.FILE_TYPE_VIDEO:
                if (videoCount % Constants.REFRESH_COUNT == 0) {
                    mFileObserverInstance.notifyVideoAction();
                }
                break;
            case Constants.FILE_TYPE_PICTURE:
                if (pictureCount % Constants.REFRESH_COUNT == 0) {
                    mFileObserverInstance.notifyPictureAction();
                }
                break;
            case Constants.FILE_TYPE_MUSIC:
                if (musicCount % Constants.REFRESH_COUNT == 0) {
                    mFileObserverInstance.notifyMusicAction();
                }
                break;
        }
    }

    public void sendFinishAction() {
        mFileObserverInstance.notifyAllAction();
    }
}
