package li.lingfeng.globaldanmakudroid.util;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.security.MessageDigest;

public class HashUtils {

    public static String hashFileHead(Context context, Uri uri, int headSize) {
        InputStream input = null;
        try {
            input = context.getContentResolver().openInputStream(uri);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            int count = 0;
            int n;
            byte[] buffer = new byte[4096];
            int toRead = Math.min(headSize, buffer.length);
            while (-1 != (n = input.read(buffer, 0, toRead))) {
                md5.update(buffer, 0, n);
                count += n;
                toRead = Math.min(headSize - count, buffer.length);
                if (toRead == 0) {
                    break;
                }
            }

            byte[] bytes = md5.digest();
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (Throwable e) {
            Logger.e("hashFileHead exception " + uri, e);
            return null;
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static void hashFileHeadAsync(Context context, Uri uri, int headSize, Callback.C1<String> callback) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return hashFileHead(context, uri, headSize);
            }

            @Override
            protected void onPostExecute(String result) {
                callback.onResult(result);
            }
        }.execute();
    }
}
