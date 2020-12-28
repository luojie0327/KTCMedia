package com.ktc.media.menu.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.menu.base.BaseMenuFragment;
import com.ktc.media.menu.entity.MinorMenuEntity;
import com.ktc.media.menu.entity.MinorType;
import com.ktc.media.menu.view.MajorMenuView;
import com.ktc.media.menu.view.MenuViewContainer;
import com.ktc.media.menu.view.MinorMenuView;
import com.ktc.media.menu.view.MinorRecyclerView;
import com.ktc.media.util.DestinyUtil;
import com.ktc.media.util.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MinorMenuFragment extends BaseMenuFragment {

    private RelativeLayout minorMenuLayout;
    private MenuViewContainer minorMenuContainer;
    private List<MinorMenuEntity> mEntities;
    private MajorMenuView mMajorMenuView = null;
    private int mMajorIndex = 0;
    private List<MinorMenuView> mMenuViews;

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimatorInflater.loadAnimator(getContext(), R.animator.minor_menu_left_in);
        } else {
            return AnimatorInflater.loadAnimator(getContext(), R.animator.minor_menu_left_out);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_minor_menu_layout;
    }

    @Override
    public void initView(View view) {
        minorMenuLayout = (RelativeLayout) view.findViewById(R.id.minor_menu_layout);
    }

    @Override
    public void initData() {
        if (mEntities == null) {
            throw new RuntimeException("Entities must prepared before load fragment!");
        }
        mMenuViews = new ArrayList<>();
        initContainerLocation();
    }

    @Override
    public void addListener() {

    }

    @Override
    public void initFocus() {

    }

    @Override
    public ViewGroup getContainerView() {
        return minorMenuContainer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMajorMenuView.requestFocus();
    }

    //初始化Container位置
    private void initContainerLocation() {
        if (mMajorMenuView == null) return;
        minorMenuContainer = new MenuViewContainer(getContext());
        minorMenuLayout.addView(minorMenuContainer);
        boolean isNeedList = false;
        if (mMajorIndex < 2) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                    , DestinyUtil.dp2px(getContext(), 600));
            if (mMajorMenuView.getParent() instanceof View) {
                layoutParams.topMargin = mMajorMenuView.getTop()
                        + ((View) mMajorMenuView.getParent()).getTop();
            } else {
                layoutParams.topMargin = mMajorMenuView.getTop();
            }
            minorMenuContainer.setLayoutParams(layoutParams);
        } else {
            int height = getContainerHeight();
            int containerHeight = DestinyUtil.dp2px(getContext(), 317);
            if (height > containerHeight) {
                height = containerHeight;
                isNeedList = true;
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                    , containerHeight);
            if (mMajorMenuView.getParent() instanceof View) {
                layoutParams.topMargin = mMajorMenuView.getTop() + ((View) mMajorMenuView.getParent()).getTop()
                        + mMajorMenuView.getHeight() / 2 - height / 2;
            } else {
                layoutParams.topMargin = mMajorMenuView.getTop() + mMajorMenuView.getHeight() / 2 - height / 2;
            }
            minorMenuContainer.setLayoutParams(layoutParams);
        }
        startAnim(minorMenuContainer);
        initMinorView(isNeedList);
    }

    private void initMinorView(boolean isNeedList) {
        mMenuViews.clear();
        if (isNeedList) {
            final RecyclerView recyclerView = new MinorRecyclerView(getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            recyclerView.setLayoutParams(layoutParams);
            recyclerView.setLayoutManager(new MinorLayoutManager(getContext()));
            MinorListAdapter adapter = new MinorListAdapter(mEntities, getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setFocusable(false);
            recyclerView.setFocusableInTouchMode(false);
            recyclerView.setAnimation(null);
            recyclerView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(getContext(), 6.7f)));
            minorMenuContainer.addView(recyclerView);
            recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(@NonNull View view) {
                    mMajorMenuView.setNextFocusRightId(view.getId());
                    recyclerView.removeOnChildAttachStateChangeListener(this);
                }

                @Override
                public void onChildViewDetachedFromWindow(@NonNull View view) {

                }
            });
        } else {
            for (MinorMenuEntity entity : mEntities) {
                if (entity == null) continue;
                MinorMenuView minorMenuView = new MinorMenuView(getContext());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        DestinyUtil.dp2px(getContext(), 38.7f));
                if (mEntities.indexOf(entity) != (mEntities.size() - 1)) {
                    layoutParams.bottomMargin = DestinyUtil.dp2px(getContext(), 6.7f);
                }
                if (mMenuViews.size() > 0) {
                    layoutParams.addRule(RelativeLayout.BELOW, mMenuViews.get(mMenuViews.size() - 1).getId());
                }
                minorMenuView.setLayoutParams(layoutParams);
                minorMenuView.setTextView(entity.getTextString());
                minorMenuView.setIsPoint(entity.getType() == MinorType.TYPE_POINT);
                if (entity.isSelected()) {
                    minorMenuView.setSelectStatus(true);
                } else {
                    minorMenuView.setSelectStatus(false);
                }
                minorMenuView.setId(minorMenuView.hashCode());
                minorMenuView.setOnItemClickListener(entity.getListener());
                minorMenuContainer.addView(minorMenuView);
                mMenuViews.add(minorMenuView);
                minorMenuView.setNextFocusLeftId(mMajorMenuView.getId());
                minorMenuView.setNextFocusRightId(minorMenuView.getId());
            }
            if (mMenuViews.size() > 0) {
                mMajorMenuView.setNextFocusRightId(mMenuViews.get(0).getId());
            }
        }
    }

    private int getContainerHeight() {
        if (mMajorMenuView == null) return 0;
        return DestinyUtil.dp2px(getContext(), 38.7f) * mEntities.size()
                + DestinyUtil.dp2px(getContext(), 6.7f) * (mEntities.size() - 1);
    }

    public void setMajor(MajorMenuView majorMenuView, int index, List<MinorMenuEntity> minorMenuEntities) {
        mMajorMenuView = majorMenuView;
        mMajorIndex = index;
        mEntities = minorMenuEntities;
        if (minorMenuContainer != null) {
            clearOtherView();
            initContainerLocation();
        }
    }

    private void clearOtherView() {
        minorMenuLayout.removeAllViews();
    }

    private void startAnim(View view) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        alphaAnim.setDuration(400);
        alphaAnim.start();
    }

    class MinorListAdapter extends RecyclerView.Adapter<MinorListAdapter.ViewHolder> {

        private List<MinorMenuEntity> mList;
        private Context mContext;

        private MinorListAdapter(List<MinorMenuEntity> list, Context context) {
            mList = list;
            mContext = context;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public MinorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_minor_menu
                    , viewGroup, false);
            return new MinorListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MinorListAdapter.ViewHolder viewHolder, int i) {
            viewHolder.mMinorMenuView.setTextView(mList.get(i).getTextString());
            viewHolder.mMinorMenuView.setIsPoint(mList.get(i).getType() == MinorType.TYPE_POINT);
            viewHolder.mMinorMenuView.setOnItemClickListener(mList.get(i).getListener());
            viewHolder.mMinorMenuView.setNextFocusLeftId(mMajorMenuView.getId());
            viewHolder.mMinorMenuView.setNextFocusRightId(viewHolder.mMinorMenuView.getId());
            if (mList.get(i).isSelected()) {
                viewHolder.mMinorMenuView.setSelectStatus(true);
            } else {
                viewHolder.mMinorMenuView.setSelectStatus(false);
            }
        }

        @Override
        public int getItemCount() {
            if (mList != null) {
                return mList.size();
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            MinorMenuView mMinorMenuView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                mMinorMenuView = (MinorMenuView) itemView.findViewById(R.id.item_minor_view);
            }
        }
    }

    class MinorLayoutManager extends LinearLayoutManager {

        MinorLayoutManager(Context context) {
            super(context);
        }

        @Override
        public View onInterceptFocusSearch(final View focused, int direction) {
            int count = getItemCount();
            int fromPos = getPosition(focused);
            int lastVisibleItemPos = findLastVisibleItemPosition();
            switch (direction) {
                case View.FOCUS_DOWN:
                    fromPos++;
                    break;
                case View.FOCUS_UP:
                    fromPos--;
                    break;
            }
            if (fromPos < 0 || fromPos >= count) {
                return focused;
            } else {
                if (fromPos > lastVisibleItemPos) {
                    scrollToPosition(fromPos);
                }
            }
            return super.onInterceptFocusSearch(focused, direction);
        }
    }
}
