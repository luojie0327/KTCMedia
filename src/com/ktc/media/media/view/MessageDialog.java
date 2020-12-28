package com.ktc.media.media.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.media.R;
import com.ktc.media.util.DestinyUtil;

public class MessageDialog extends Dialog {

    private ImageView dialogImage;
    private TextView dialogMessageText;
    private TextView dialogContentText;
    private RelativeLayout btnContainer;
    private DialogButton negativeBtn;
    private DialogButton positiveBtn;
    private OnDialogButtonClickListener mOnDialogButtonClickListener;
    private LinearLayout titleLayout;

    public MessageDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MessageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_message_layout, null);
        setContentView(view);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        titleLayout = (LinearLayout) view.findViewById(R.id.dialog_title_layout);
        dialogImage = (ImageView) view.findViewById(R.id.dialog_image);
        dialogMessageText = (TextView) view.findViewById(R.id.dialog_text);
        dialogContentText = (TextView) view.findViewById(R.id.dialog_content_text);
        btnContainer = (RelativeLayout) view.findViewById(R.id.dialog_btn_container);
        negativeBtn = (DialogButton) view.findViewById(R.id.dialog_negative_btn);
        positiveBtn = (DialogButton) view.findViewById(R.id.dialog_positive_btn);
        addListener();
        setCancelable(false);
        setDialogSize();
    }

    public void setDialogMessageText(String text) {
        dialogMessageText.setText(text);
    }

    public void setDialogContentText(String text) {
        dialogContentText.setText(text);
        setBtnContainerLocation();
    }

    private void setBtnContainerLocation() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnContainer.getLayoutParams();
        layoutParams.topMargin = DestinyUtil.dp2px(getContext(), 30);
        btnContainer.requestLayout();
    }

    public void setIsLoading(boolean isLoading) {
        if (isLoading) {
            dialogImage.setImageResource(R.drawable.dialog_loading);
            dialogContentText.setVisibility(View.GONE);
            negativeBtn.setVisibility(View.GONE);
            positiveBtn.setVisibility(View.GONE);
            relayoutTitle();
            startAnimation();
        } else {
            dialogImage.setImageResource(R.drawable.dialog_error);
        }
    }

    private void relayoutTitle() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleLayout.setLayoutParams(layoutParams);
    }

    private void startAnimation() {
        RotateAnimation rotate = new RotateAnimation(0f, 360f
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(1000);
        rotate.setRepeatCount(-1);
        dialogImage.setAnimation(rotate);
        rotate.start();
    }

    private void addListener() {
        negativeBtn.setOnButtonClickListener(new DialogButton.OnButtonClickListener() {
            @Override
            public void onButtonClick(View view) {
                if (mOnDialogButtonClickListener != null) {
                    mOnDialogButtonClickListener.onNegativeClick();
                }
                dismiss();
            }
        });
        positiveBtn.setOnButtonClickListener(new DialogButton.OnButtonClickListener() {
            @Override
            public void onButtonClick(View view) {
                if (mOnDialogButtonClickListener != null) {
                    mOnDialogButtonClickListener.onPositiveClick();
                }
                dismiss();
            }
        });
    }

    public static class Builder {

        MessageDialog mMessageDialog;

        public Builder(MessageDialog messageDialog) {
            mMessageDialog = messageDialog;
        }

        public Builder setIsLoading(boolean isLoading) {
            mMessageDialog.setIsLoading(isLoading);
            return this;
        }

        public Builder setMessageText(String text) {
            mMessageDialog.setDialogMessageText(text);
            return this;
        }

        public Builder setContentText(String text) {
            mMessageDialog.setDialogContentText(text);
            return this;
        }

        public Builder setButtonClickListener(OnDialogButtonClickListener onDialogButtonClickListener) {
            mMessageDialog.setOnDialogButtonClickListener(onDialogButtonClickListener);
            return this;
        }
    }

    private void setDialogSize() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
    }

    public void setOnDialogButtonClickListener(OnDialogButtonClickListener onDialogButtonClickListener) {
        mOnDialogButtonClickListener = onDialogButtonClickListener;
    }

    public interface OnDialogButtonClickListener {
        void onNegativeClick();

        void onPositiveClick();
    }

}
