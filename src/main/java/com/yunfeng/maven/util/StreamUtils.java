package com.yunfeng.maven.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {

    public static void closeOutputStream(OutputStream out) {
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                System.err.println("closeOutputStream: " + e.getMessage());
            }
        }
    }

    public static void closeInputStream(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                System.err.println("closeInputStream: " + e.getMessage());
            }
        }
    }

}
