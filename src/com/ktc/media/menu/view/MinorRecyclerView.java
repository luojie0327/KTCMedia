package com.ktc.media.menu.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.ktc.media.R;

public class MinorRecyclerView extends RecyclerView {

    public MinorRecyclerView(@NonNull Context context) {
        super(context);
        addListener();
    }

    public MinorRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addListener();
    }

    private void addListener() {
        getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if (oldFocus instanceof MinorMenuView
                        && oldFocus.getParent() instanceof RecyclerView) {
                    oldFocus.setBackground(null);
                }
                if (newFocus instanceof MinorMenuView
                        && newFocus.getParent() instanceof RecyclerView) {
                    newFocus.setBackgroundResource(R.drawable.menu_focus_item_image);
                }
            }
        });
    }
}
