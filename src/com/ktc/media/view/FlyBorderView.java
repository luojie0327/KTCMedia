package com.ktc.media.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.ktc.media.R;


public class FlyBorderView extends View {

    private int borderWidth;//焦点移动飞框的边框
    private int duration;//动画持续时间

    public FlyBorderView(Context context) {
        this(context, null);
        init();
    }

    public FlyBorderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public FlyBorderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        borderWidth = getContext().getResources().getDimensionPixelSize(R.dimen.borderWidth);
        setElevation(-1);
        duration = getResources().getInteger(R.integer.fly_border_duration);
    }

    /**
     * @param newFocus 下一个选中项视图
     * @param xScale   选中项视图的X轴伸缩大小
     * @param yScale   选中项视图的Y轴伸缩大小
     */
    public void attachToView(View newFocus, float xScale, float yScale) {
        setVisibility(VISIBLE);
        View v = newFocus;
        if (v instanceof RecyclerView) {
            return;
        }
        int left = v.getLeft();
        int top = v.getTop();
        while (!(v.getParent() instanceof FileListContainer)) {
            try {
                v = (ViewGroup) v.getParent();
            } catch (ClassCastException e) {
                e.printStackTrace();
                setVisibility(GONE);
                return;
            }
            if (left >= v.getWidth()) {
                left = v.getWidth();
            } else if (left < 0) {
                left = 0;
            } else {
                left += v.getLeft();
            }
            int topMax = 0;
            if (v instanceof ViewGroup && ((ViewGroup) v).getChildCount() > 0) {
                topMax = v.getHeight() - ((ViewGroup) v).getChildAt(0).getHeight();
            }
            if (top >= topMax) {
                top = topMax;
            } else if (top < 0) {
                top = 0;
            } else {
                top += v.getTop();
            }
        }
        final int widthInc = (int) ((newFocus.getWidth() * xScale + 2 * borderWidth - getWidth()));//当前选中项与下一个选中项的宽度偏移量
        final int heightInc = (int) ((newFocus.getHeight() * yScale - getHeight()));//当前选中项与下一个选中项的高度偏移量
        float translateX = left - borderWidth
                - (newFocus.getWidth() * xScale - newFocus.getWidth()) / 2;//飞框到达下一个选中项的X轴偏移量
        if (translateX < 0) {
            translateX = left - borderWidth
                    - (newFocus.getWidth() * xScale - newFocus.getWidth()) / 2
            /*+ getResources().getDimension(R.dimen.list_left_margin)*/;
        }
        float translateY = top
                - (newFocus.getHeight() * yScale - newFocus.getHeight()) / 2;//飞框到达下一个选中项的Y轴偏移量
        startTotalAnim(widthInc, heightInc, translateX, translateY);//调用飞框 自适应和移动 动画效果
    }

    /**
     * 飞框 自适应和移动 动画效果
     *
     * @param widthInc   宽度偏移量
     * @param heightInc  高度偏移量
     * @param translateX X轴偏移量
     * @param translateY Y轴偏移量
     */
    private void startTotalAnim(final int widthInc, final int heightInc, float translateX, float translateY) {
        final int width = getWidth();//当前飞框的宽度
        final int height = getHeight();//当前飞框的高度
        ValueAnimator widthAndHeightChangeAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);//数值变化动画器，能获取平均变化的值
        widthAndHeightChangeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setFlyBorderLayoutParams((int) (width + widthInc * Float.parseFloat(valueAnimator.getAnimatedValue().toString())),
                        (int) (height + heightInc * Float.parseFloat(valueAnimator.getAnimatedValue().toString())));//设置当前飞框的宽度和高度的自适应变化
            }
        });

        ObjectAnimator translationX = ObjectAnimator.ofFloat(this, "translationX", translateX);//X轴移动的属性动画
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this, "translationY", translateY);//y轴移动的属性动画

        AnimatorSet set = new AnimatorSet();//动画集合
        set.play(widthAndHeightChangeAnimator).with(translationX).with(translationY);//动画一起实现
        set.setDuration(duration);
        set.setInterpolator(new LinearInterpolator());//设置动画插值器
        set.start();//开始动画
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private void setFlyBorderLayoutParams(int width, int height) {//设置焦点移动飞框的宽度和高度
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }
}
