package com.ktc.media.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.ktc.media.R;
import com.ktc.media.constant.Constants;
import com.ktc.media.db.DBHelper;
import com.ktc.media.db.DatabaseUtil;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.DiskData;
import com.ktc.media.model.FileData;
import com.ktc.media.model.MusicData;
import com.ktc.media.model.VideoData;
import com.ktc.media.util.FileSizeUtil;
import com.ktc.media.util.Tools;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FileDataManager {

    private Context mContext;
    private List<DiskData> diskPaths;
    private HashMap<Integer, String> typeMap;

    private volatile static FileDataManager instance;

    private List<String[]> typeList;
    private List<Integer> types;

    public static FileDataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FileDataManager.class) {
                if (instance == null) {
                    instance = new FileDataManager(context);
                }
            }
        }
        return instance;
    }

    private FileDataManager(Context context) {
        mContext = context;
        typeMap = new HashMap<>();
        init();
        initFileTypeResource();
    }

    private void init() {
        typeMap.put(Constants.FILE_TYPE_VIDEO, DBHelper.TB_VIDEO);
        typeMap.put(Constants.FILE_TYPE_MUSIC, DBHelper.TB_MUSIC);
        typeMap.put(Constants.FILE_TYPE_PICTURE, DBHelper.TB_PICTURE);
    }

    private void initFileTypeResource() {
        typeList = new ArrayList<>();
        types = new ArrayList<>();
        String[] videoTypes = mContext.getResources().getStringArray(R.array.video_filter);
        String[] pictureTypes = mContext.getResources().getStringArray(R.array.picture_filter);
        String[] musicTypes = mContext.getResources().getStringArray(R.array.audio_filter);
        String[] docTypes = mContext.getResources().getStringArray(R.array.doc_filter);
        String[] pdfTypes = mContext.getResources().getStringArray(R.array.pdf_filter);
        String[] pptTypes = mContext.getResources().getStringArray(R.array.ppt_filter);
        String[] txtTypes = mContext.getResources().getStringArray(R.array.txt_filter);
        String[] xlsTypes = mContext.getResources().getStringArray(R.array.xls_filter);
        String[] apkTypes = mContext.getResources().getStringArray(R.array.apk_filter);
        typeList.add(videoTypes);
        typeList.add(pictureTypes);
        typeList.add(musicTypes);
        typeList.add(docTypes);
        typeList.add(pdfTypes);
        typeList.add(pptTypes);
        typeList.add(txtTypes);
        typeList.add(xlsTypes);
        typeList.add(apkTypes);
        types.add(Constants.FILE_TYPE_VIDEO);
        types.add(Constants.FILE_TYPE_PICTURE);
        types.add(Constants.FILE_TYPE_MUSIC);
        types.add(Constants.FILE_TYPE_DOC);
        types.add(Constants.FILE_TYPE_PDF);
        types.add(Constants.FILE_TYPE_PPT);
        types.add(Constants.FILE_TYPE_TXT);
        types.add(Constants.FILE_TYPE_XLS);
        types.add(Constants.FILE_TYPE_APK);
    }

    public List<VideoData> getAllVideoData() {
        List<VideoData> dataList = DatabaseUtil.getInstance(mContext).getAllVideoData();
        Collections.sort(dataList, new DataComparator<>());
        return dataList;
    }

    public List<MusicData> getAllMusicData() {
        List<MusicData> dataList = DatabaseUtil.getInstance(mContext).getAllMusicData();
        Collections.sort(dataList, new DataComparator<>());
        return dataList;
    }

    public List<FileData> getAllPictureData() {
        List<FileData> dataList = DatabaseUtil.getInstance(mContext).getAllPictureData();
        Collections.sort(dataList, new DataComparator<>());
        return dataList;
    }

    public List<VideoData> getAllVideoData(String path) {
        File source = new File(path);
        List<FileData> pathFileDataList;
        if (source.isDirectory()) {
            pathFileDataList = getPathFileDataWithOutSize(path);
        } else {
            pathFileDataList = getPathFileDataWithOutSize(path.substring(0, path.lastIndexOf("/")));
        }
        List<VideoData> videoList = new ArrayList<>();
        for (FileData fd : pathFileDataList) {
            if (fd.getType() == Constants.FILE_TYPE_VIDEO) {
                File file = new File(fd.getPath());
                VideoData videoData = new VideoData();
                videoData.setPath(file.getAbsolutePath());
                videoData.setType(Constants.FILE_TYPE_VIDEO);
                videoData.setName(file.getName());
                videoData.setDurationString(Tools.getDurationString(file.getAbsolutePath()));
                videoList.add(videoData);
            }
        }
        Collections.sort(videoList, new DataComparator<>());
        return videoList;
    }

    public List<MusicData> getAllMusicData(String path) {
        List<FileData> pathFileDataList = getPathFileDataWithOutSize(path.substring(0, path.lastIndexOf("/")));
        List<MusicData> musicList = new ArrayList<>();
        for (FileData fd : pathFileDataList) {
            if (fd.getType() == Constants.FILE_TYPE_MUSIC) {
                File file = new File(fd.getPath());
                MusicData musicData = new MusicData();
                musicData.setPath(file.getAbsolutePath());
                musicData.setType(Constants.FILE_TYPE_MUSIC);
                musicData.setName(file.getName());
                musicData.setSongName(getSongName(file.getAbsolutePath()));
                musicData.setDurationString(Tools.getDurationString(file.getAbsolutePath()));
                musicList.add(musicData);
            }
        }
        Collections.sort(musicList, new DataComparator<>());
        return musicList;
    }

    public List<FileData> getAllPictureData(String path) {
        List<FileData> pathFileDataList = getPathFileDataWithOutSize(path.substring(0, path.lastIndexOf("/")));
        List<FileData> pictureList = new ArrayList<>();
        for (FileData fd : pathFileDataList) {
            if (fd.getType() == Constants.FILE_TYPE_PICTURE) {
                File file = new File(fd.getPath());
                FileData fileData = new FileData();
                fileData.setPath(file.getAbsolutePath());
                fileData.setType(Constants.FILE_TYPE_PICTURE);
                fileData.setName(file.getName());
                pictureList.add(fileData);
            }
        }
        Collections.sort(pictureList, new DataComparator<>());
        return pictureList;
    }

    public List<FileData> getPathFileDataWithOutSize(String path) {
        File file = new File(path);
        List<FileData> fileDataList = new ArrayList<>();
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                for (File f : fileList) {
                    FileData fileData = new FileData();
                    fileData.setName(f.getName());
                    fileData.setType(getFileType(f));
                    fileData.setPath(f.getAbsolutePath());
                    fileDataList.add(fileData);
                }
            }
        }
        Collections.sort(fileDataList, new DataComparator<>());
        return fileDataList;
    }

    public List<FileData> getPathFileData(String path) {
        File file = new File(path);
        List<FileData> fileDataList = new ArrayList<>();
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                for (File f : fileList) {
                    FileData fileData = new FileData();
                    fileData.setName(f.getName());
                    fileData.setType(getFileType(f));
                    fileData.setPath(f.getAbsolutePath());
                    if (f.isDirectory()) {
                        fileData.setSizeDescription(FileSizeUtil.getFolderSizeDescription(f.getAbsolutePath()));
                    } else {
                        fileData.setSizeDescription(FileSizeUtil.getFileSizeDescription(f.getAbsolutePath()));
                    }
                    fileDataList.add(fileData);
                }
            }
        }
        Collections.sort(fileDataList, new DataComparator<>());
        return fileDataList;
    }

    private List<BaseData> getCollectList(List<BaseData> src) {
        try {
            Collections.sort(src, new DataComparator<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return src;
    }

    private int getFileType(File file) {
        if (file.isDirectory()) {
            return Constants.FILE_TYPE_DIR;
        }
        for (int i = 0; i < typeList.size(); i++) {
            for (String s : typeList.get(i)) {
                if (file.getName().toLowerCase().endsWith(s)) {
                    return types.get(i);
                }
            }
        }
        if (!file.isDirectory()) {
            return Constants.FILE_TYPE_UNKNOWN;
        }
        return Constants.FILE_TYPE_DIR;
    }

    private String getSongName(String path) {
        Cursor cursor = null;
        String where = MediaStore.MediaColumns.DATA + "=?";
        String result = null;
        try {
            cursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media.TITLE,},
                    where, new String[]{path}, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

}
