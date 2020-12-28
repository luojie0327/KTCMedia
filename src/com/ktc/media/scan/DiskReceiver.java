package com.ktc.media.scan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.util.Log;

import com.ktc.media.db.DatabaseUtil;

public class DiskReceiver extends BroadcastReceiver {

    private static final String TAG = DiskReceiver.class.getSimpleName();
    private static final String INTERNAL_STORAGE_PATH = "/storage/emulated/0";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            Log.d(TAG, "ACTION_MEDIA_MOUNTED");
        }else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "ACTION_BOOT_COMPLETED");
        }
    }

    private void scanDisk(Context context, String diskPath, boolean needClearAll) {
        Intent intent = new Intent(context, ScanService.class);
        intent.putExtra("diskPath", diskPath);
        if (needClearAll) {
            intent.putExtra("needClearAll", true);
        }
        context.startService(intent);
    }

    private void deletePathData(Context context, String usbPath) {
        DatabaseUtil.getInstance(context).deletePathData(usbPath, true);
        DatabaseUtil.getInstance(context).removeDisk(usbPath);
    }

}
