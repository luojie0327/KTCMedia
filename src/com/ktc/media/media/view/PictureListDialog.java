package com.ktc.media.media.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.ktc.media.R;
import com.ktc.media.media.adapter.PictureListAdapter;
import com.ktc.media.model.FileData;
import com.ktc.media.util.DestinyUtil;

import java.util.List;

public class PictureListDialog extends Dialog implements OnListItemClickListener {

    private RecyclerView mRecyclerView;
    private List<FileData> mPictureData;
    private int currentPosition;
    private int mPosition;
    private OnListItemClickListener mOnListItemClickListener;

    public PictureListDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PictureListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_picture_list, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.dialog_picture_list_view);
        setContentView(view);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void prepareData(List<FileData> pictureData, int position) {
        mPictureData = pictureData;
        currentPosition = position;
        mPosition = position;
        initRecyclerView();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            dismiss();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
            mPosition--;
            if(mPosition <= -1){
                mPosition = mPictureData.size() - 1;
            }
        }else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            mPosition++;
            if(mPosition >= mPictureData.size()){
                mPosition = 0;
            }
		}
        return super.onKeyDown(keyCode, event);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL
                , false));
        PictureListAdapter adapter = new PictureListAdapter(getContext(), mPictureData);
        adapter.setOnListItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(currentPosition);
        mRecyclerView.addItemDecoration(new MediaDecoration(getContext(), DestinyUtil.dp2px(getContext(), 8)));
        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int position = layoutManager.getPosition(view);
                    if (position == currentPosition) {
                        view.requestFocus();
                        mRecyclerView.removeOnChildAttachStateChangeListener(this);
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                View newView = layoutManager.findViewByPosition(mPosition);
                if (newView != null) {
                    newView.requestFocus();
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (mOnListItemClickListener != null) {
            currentPosition = position;
            mPosition = position;
            mOnListItemClickListener.onItemClick(view, position);
        }
    }

    public void skipToNextOrBeforeThumb(int delta) {
        currentPosition += delta;
        if(currentPosition <= -1){
            currentPosition = mPictureData.size() - 1;
        }else if(currentPosition >= mPictureData.size()){
            currentPosition = 0;
        }
        mPosition = currentPosition;
        mRecyclerView.scrollToPosition(currentPosition);
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            View newView = layoutManager.findViewByPosition(currentPosition);
            if (newView != null) {
                newView.requestFocus();
            }
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        mOnListItemClickListener = onListItemClickListener;
    }

}
