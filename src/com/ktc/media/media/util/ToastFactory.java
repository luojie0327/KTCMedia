package com.ktc.media.media.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ktc.media.R;

public class ToastFactory {

    private static Context context = null;

    private static Toast toast = null;

    public static void showToast(Context context, String message, int duration) {
        if (ToastFactory.context == context && toast != null) {
            toast.cancel();
        } else {
            ToastFactory.context = context;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView toastText = (TextView) view.findViewById(R.id.toast_text);
        toastText.setText(message);
        toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }

}
