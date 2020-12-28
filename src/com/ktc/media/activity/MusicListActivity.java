package com.ktc.media.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ktc.media.R;
import com.ktc.media.adapter.MusicGridListAdapter;
import com.ktc.media.adapter.MusicLinearListAdapter;
import com.ktc.media.base.BaseActivity;
import com.ktc.media.constant.Constants;
import com.ktc.media.data.FileDataManager;
import com.ktc.media.data.ThreadPoolManager;
import com.ktc.media.db.DatabaseUtil;
import com.ktc.media.media.music.MusicPlayerActivity;
import com.ktc.media.media.util.ToastFactory;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.DiskData;
import com.ktc.media.model.MusicData;
import com.ktc.media.util.DestinyUtil;
import com.ktc.media.util.ExecutorUtil;
import com.ktc.media.util.SpaceItemDecoration;
import com.ktc.media.util.StorageUtil;
import com.ktc.media.view.FileListContainer;
import com.ktc.media.view.KeyboardView;
import com.ktc.media.view.MarqueeTextView;
import com.ktc.media.view.MediaGridItemView;
import com.ktc.media.view.MediaLinearItemView;
import com.ktc.media.view.OnItemClickListener;
import com.ktc.media.view.OnItemFocusListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicListActivity extends BaseActivity implements OnItemFocusListener
        , View.OnClickListener, KeyboardView.OnTextChangeListener, OnItemClickListener {

    private MarqueeTextView subtitleLeftText;
    private TextView subtitleRightText;
    private ImageView refreshImageBtn;
    private ImageView switchImageBtn;
    private ImageView searchImageBtn;
    private ImageView dividerView;
    private FileListContainer mListContainer;
    private KeyboardView keyboardView;
    private RelativeLayout emptyView;
    private ImageView emptyImage;
    private RecyclerView musicListView;
    private MusicLinearListAdapter mMusicLinearListAdapter;
    private MusicGridListAdapter mMusicGridListAdapter;
    private List<MusicData> mDataList;
    private boolean isCurrentLinear = true;
    private boolean isKeyboardVisible = false;
    private static final int spanCount = 4;
    private String currentLeftTitle;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
    private ImageView loadingImage;
    private boolean mRefreshing = false;
    private List<MusicData> fileDataList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_list;
    }

    @Override
    public void initView() {
        keyboardView = (KeyboardView) findViewById(R.id.music_keyboard_view);
        subtitleLeftText = (MarqueeTextView) findViewById(R.id.music_list_sub_title_left);
        subtitleRightText = (TextView) findViewById(R.id.music_list_sub_title_right);
        refreshImageBtn = (ImageView) findViewById(R.id.list_refresh);
        switchImageBtn = (ImageView) findViewById(R.id.music_list_switch_image);
        dividerView = (ImageView) findViewById(R.id.music_list_title_divider);
        searchImageBtn = (ImageView) findViewById(R.id.music_list_search_image);
        mListContainer = (FileListContainer) findViewById(R.id.music_list_container);
        musicListView = (RecyclerView) findViewById(R.id.music_list_view);
        emptyView = (RelativeLayout) findViewById(R.id.media_empty_layout);
        emptyImage = (ImageView) findViewById(R.id.media_empty_image);
        loadingImage = (ImageView) findViewById(R.id.loading);
        keyboardView.setEnabled(false);
        changeLeftFocusable(false);
        musicListView.setHasFixedSize(true);
    }

    @Override
    public void initData() {
        mDataList = getIntent().getParcelableArrayListExtra(Constants.TYPE_MUSIC);
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        } else {
            changeEmptyStatus(mDataList, false);
            subtitleRightText.setText(getString(R.string.str_music_list_count, mDataList.size()));
        }
        mMusicLinearListAdapter = new MusicLinearListAdapter(this, mDataList);
        mMusicGridListAdapter = new MusicGridListAdapter(this, mDataList);
        setLinearAdapter();
        if (mDataList == null || mDataList.size() == 0) {
            updateDataList(null);
        }
    }

    @Override
    public void initFocus() {
        musicListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull final View view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.requestFocus();
                        changeLeftFocusable(true);
                    }
                }, 100);
                mListContainer.setNewFocus(view);
                musicListView.removeOnChildAttachStateChangeListener(this);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
    }

    @Override
    public void addListener() {
        mMusicLinearListAdapter.setOnItemFocusListener(this);
        mMusicGridListAdapter.setOnItemFocusListener(this);
        mMusicLinearListAdapter.setOnItemClickListener(this);
        mMusicGridListAdapter.setOnItemClickListener(this);
        refreshImageBtn.setOnClickListener(this);
        switchImageBtn.setOnClickListener(this);
        searchImageBtn.setOnClickListener(this);
        keyboardView.setOnTextChangeListener(this);
    }

    @Override
    public void handleDiskIntent(Intent intent) {

    }

    @Override
    public void handleUpdate(String type) {
        if (type.equals(Constants.ALL_REFRESH_ACTION)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastFactory.showToast(MusicListActivity.this, getString(R.string.scan_finish), Toast.LENGTH_SHORT);
					
                }
            });
			updateDataList(null);
        } else if (type.equals(Constants.PATH_DELETE_ACTION)) {
            updateDataList(null);
        }
    }

    @Override
    public void blockFocus() {
        changeLeftFocusable(false);
    }

    @Override
    public void releaseFocus() {
        if (!mRefreshing) {
            changeLeftFocusable(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_refresh:
                updateDataList(null);
                break;
            case R.id.music_list_switch_image:
                isCurrentLinear = !isCurrentLinear;
                changeListLayout(isCurrentLinear);
                break;
            case R.id.music_list_search_image:
                isKeyboardVisible = !isKeyboardVisible;
                changeKeyboardStatus(isKeyboardVisible);
                break;
        }
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, BaseData data) {
        currentLeftTitle = getPathDescription(data);
        if (!isKeyboardVisible) subtitleLeftText.setText(currentLeftTitle);
    }

    @Override
    public void onBackPressed() {
        if (isKeyboardVisible) {
            isKeyboardVisible = false;
            changeKeyboardStatus(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExecutorUtil.shutdownAndAwaitTermination(mExecutorService);
        mMusicLinearListAdapter.release();
    }

    private void updateDataList(final String fromSearch) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fromSearch == null) {
                            mRefreshing = true;
                            changeLeftFocusable(false);
                            musicListView.setVisibility(View.GONE);
                            loadingImage.setVisibility(View.VISIBLE);
                            startAnimation();
                        }
                    }
                });

                if (fromSearch == null) {
                    fileDataList = FileDataManager.getInstance(MusicListActivity.this).getAllMusicData();
                    mDataList.clear();
                    mDataList.addAll(fileDataList);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fromSearch == null) {
                            stopAnimation();
                            loadingImage.setVisibility(View.GONE);
                            mRefreshing = false;
                            refreshList();
                            changeLeftFocusable(true);
                            musicListView.setVisibility(View.VISIBLE);
                        } else if (TextUtils.isEmpty(fromSearch)) {
                            mDataList.clear();
                            mDataList.addAll(fileDataList);
                            refreshList();
                        } else {
                            refreshListFromSearch(fromSearch);
                        }
                    }
                });
            }
        });
    }

    private void refreshList() {
        changeEmptyStatus(mDataList, false);
        setAdapterAndNotify();
        if (isKeyboardVisible) {
            subtitleLeftText.setText(getString(R.string.str_music_list_count, mDataList.size()));
        }
        subtitleRightText.setText(getString(R.string.str_music_list_count, mDataList.size()));
    }

    private void setAdapterAndNotify() {
        if (isCurrentLinear) {
            musicListView.setAdapter(mMusicLinearListAdapter);
            mMusicLinearListAdapter.notifyDataSetChanged();
        } else {
            musicListView.setAdapter(mMusicGridListAdapter);
            mMusicGridListAdapter.notifyDataSetChanged();
        }
    }

    private void changeLeftFocusable(boolean canFocus) {
        refreshImageBtn.setFocusable(canFocus);
        refreshImageBtn.setFocusableInTouchMode(canFocus);
        switchImageBtn.setFocusable(canFocus);
        switchImageBtn.setFocusableInTouchMode(canFocus);
        searchImageBtn.setFocusable(canFocus);
        searchImageBtn.setFocusableInTouchMode(canFocus);
    }

    private void changeKeyboardStatus(boolean isVisible) {
        changeTitle(isVisible);
        changeRightMargin(isVisible);
        setLinearContentVisible(!isVisible);
        changeLeftImageBtnVisible(!isVisible);
        adjustFlyBoardView();//重新调整飞框
        startKeyboardAnimation(isVisible);
        if (!isVisible) updateDataList("");
        clearKeyboard();
    }

    private void startKeyboardAnimation(boolean isVisible) {
        ValueAnimator widthAnimator;
        if (isVisible) {
            keyboardView.setEnabled(true);
            widthAnimator = ValueAnimator.ofFloat(1, 0).setDuration(300);
            keyboardView.editTextRequestFocus();
        } else {
            keyboardView.setEnabled(false);
            widthAnimator = ValueAnimator.ofFloat(0, 1).setDuration(300);
            searchImageBtn.requestFocus();
        }
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) keyboardView.getLayoutParams();
                layoutParams.leftMargin = -(int) (DestinyUtil.dp2px(MusicListActivity.this, 413.3f)
                        * Float.parseFloat(valueAnimator.getAnimatedValue().toString()));
                keyboardView.requestLayout();
            }
        });
        widthAnimator.start();
    }

    private void clearKeyboard() {
        keyboardView.clearKeyboard();
    }

    private void changeTitle(boolean isVisible) {
        if (isVisible) {
            subtitleLeftText.setText(getString(R.string.str_music_list_count, mDataList.size()));
            if (isKeyboardVisible || emptyView.getVisibility() == View.VISIBLE) {
                subtitleRightText.setVisibility(View.GONE);
            }
        } else {
            subtitleLeftText.setText(currentLeftTitle);
            if (!isKeyboardVisible && emptyView.getVisibility() == View.GONE) {
                subtitleRightText.setVisibility(View.VISIBLE);
            }
        }
    }

    private void changeRightMargin(boolean isVisible) {
        if (isVisible) {
            RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) mListContainer.getLayoutParams();
            layoutParam.rightMargin = -DestinyUtil.dp2px(this, 22);
            layoutParam.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            mListContainer.requestLayout();
            RelativeLayout.LayoutParams recyclerLayoutParam = (RelativeLayout.LayoutParams) musicListView.getLayoutParams();
            recyclerLayoutParam.rightMargin = 0;
            recyclerLayoutParam.addRule(RelativeLayout.ALIGN_LEFT, dividerView.getId());
            if (!isCurrentLinear) musicListView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) mListContainer.getLayoutParams();
            layoutParam.rightMargin = DestinyUtil.dp2px(this, 148);
            layoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mListContainer.requestLayout();
            RelativeLayout.LayoutParams recyclerLayoutParam = (RelativeLayout.LayoutParams) musicListView.getLayoutParams();
            recyclerLayoutParam.removeRule(RelativeLayout.ALIGN_LEFT);
            recyclerLayoutParam.rightMargin = DestinyUtil.dp2px(this, 13.3f);
            if (!isCurrentLinear) musicListView.setLayoutManager(new GridLayoutManager(this, 4));
        }
    }

    //设置列表文字
    private void setLinearContentVisible(boolean isVisible) {
        if (isCurrentLinear) {
            mMusicLinearListAdapter.setItemContentVisible(isVisible);
        }
    }

    private void changeLeftImageBtnVisible(boolean isVisible) {
        if (isVisible) {
            if (!isKeyboardVisible && emptyView.getVisibility() == View.GONE) {
                refreshImageBtn.setVisibility(View.VISIBLE);
                switchImageBtn.setVisibility(View.VISIBLE);
                searchImageBtn.setVisibility(View.VISIBLE);
            }
        } else {
            if (isKeyboardVisible || emptyView.getVisibility() == View.VISIBLE) {
                refreshImageBtn.setVisibility(View.GONE);
                switchImageBtn.setVisibility(View.GONE);
                searchImageBtn.setVisibility(View.GONE);
            }
        }
    }

    private void changeListLayout(boolean isLinear) {
        if (isLinear) {
            setLinearAdapter();
            switchImageBtn.setImageResource(R.drawable.switch_grid_image);
        } else {
            setGridAdapter();
            switchImageBtn.setImageResource(R.drawable.switch_list_image);
        }
    }

    private void adjustFlyBoardView() {
        mListContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListContainer.reAttachView();
            }
        }, 300);
    }

    private void setLinearAdapter() {
        musicListView.setLayoutManager(new LinearLayoutManager(this));
        if (musicListView.getItemDecorationCount() == 0) {
            musicListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(this, -6.67f)));
        }
        musicListView.setAdapter(mMusicLinearListAdapter);
    }

    private void setGridAdapter() {
        musicListView.setLayoutManager(new GridLayoutManager(this, spanCount));
        if (musicListView.getItemDecorationCount() == 0) {
            musicListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(this, 6.67f)));
        }
        musicListView.setAdapter(mMusicGridListAdapter);
    }

    private void changeEmptyStatus(List<MusicData> dataList, boolean isFromSearch) {
        if (isFromSearch) {
            emptyImage.setImageResource(R.drawable.search_empty);
        } else {
            emptyImage.setImageResource(R.drawable.media_list_empty);
        }
        if (dataList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);
//            changeLeftImageBtnVisible(false);
            subtitleRightText.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
            changeLeftImageBtnVisible(true);
            if (!isKeyboardVisible) {
                subtitleRightText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            mMusicLinearListAdapter.setSpanText(null);
            mMusicGridListAdapter.setSpanText(null);
            updateDataList("");
        } else {
            updateDataList(s);
        }
    }

    private void refreshListFromSearch(String s) {
        Iterator<MusicData> musicDataIterator = mDataList.iterator();
        while (musicDataIterator.hasNext()) {
            MusicData musicData = musicDataIterator.next();
            if (!musicData.getName().toUpperCase().contains(s.toUpperCase())) {
                musicDataIterator.remove();
            }
        }
        changeEmptyStatus(mDataList, true);
        if (isCurrentLinear) {
            musicListView.setAdapter(mMusicLinearListAdapter);
            mMusicLinearListAdapter.setSpanText(s);
            mMusicLinearListAdapter.notifyDataSetChanged();
        } else {
            musicListView.setAdapter(mMusicGridListAdapter);
            mMusicGridListAdapter.setSpanText(s);
            mMusicGridListAdapter.notifyDataSetChanged();
        }
        subtitleLeftText.setText(getString(R.string.str_music_list_count, mDataList.size()));
    }

    @Override
    public void onItemClick(View view) {
        BaseData baseData = null;
        if (view instanceof MediaLinearItemView) {
            baseData = ((MediaLinearItemView) view).getBaseData();
        } else if (view instanceof MediaGridItemView) {
            baseData = ((MediaGridItemView) view).getBaseData();
        }
        if (baseData == null) return;
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        intent.putExtra(Constants.BUNDLE_PATH_KEY, baseData.getPath());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScannerService();
    }

    private void startScannerService() {
        List<String> diskPathList = DatabaseUtil.getInstance(this).getDiskPathList();
        List<DiskData> diskDataList = StorageUtil.getMountedDisksList(this);
        for (DiskData diskData : diskDataList) {
            if (!diskPathList.contains(diskData.getPath())) {
                DatabaseUtil.getInstance(this).insertDisk(diskData.getPath());
                Intent intent = new Intent();
                intent.setAction("com.ktc.FILE_SCAN");
                intent.setPackage("com.ktc.filemanager");
                intent.putExtra("diskPath", diskData.getPath());
                startService(intent);
            }
        }
    }

    private void startAnimation() {
        RotateAnimation rotate = new RotateAnimation(0f, 360f
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(1000);
        rotate.setRepeatCount(-1);
        loadingImage.setAnimation(rotate);
        rotate.start();
    }

    private void stopAnimation() {
        loadingImage.clearAnimation();
    }
}
