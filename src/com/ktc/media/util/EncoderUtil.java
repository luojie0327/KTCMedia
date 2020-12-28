package com.ktc.media.util;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EncoderUtil {

    private static Map<String, String> specialEncode;

    static {
        specialEncode = new HashMap<>();
        specialEncode.put("IBM420_ltr", "ISO8859–11");
        specialEncode.put("UTF-16LE", "windows-1250");
        specialEncode.put("windows-1252", "windows-1256");
    }

    public static String getEncode(String path) {
        if (path == null) return null;
        try {
            byte[] bytes = readFile(path);
            if (bytes == null) return null;
            CharsetDetector detector = new CharsetDetector();
            detector.setText(bytes);
            CharsetMatch match = detector.detect();
            String encode = match.getName();
            if (specialEncode.containsKey(encode)) {
                return specialEncode.get(encode);
            }
            return encode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] readFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) return null;
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = fileInputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        fileInputStream.close();
        return baos.toByteArray();
    }
}
