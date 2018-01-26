package com.taovo.takeout.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

/**
 * @author Gimpo create on 2018/1/25 19:28
 * @email : jimbo922@163.com
 */

public class AppUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param dpValue 单位dip的数据
     *
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取手机 imei
     * @return
     */
    public static String getIMEI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            if (TextUtils.isEmpty(imei)) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 兼容Android 6.0以上设备获取MAC地址
     * 物理地址、硬件地址，用来定义网络设备的位置
     * 兼容原因：从android 6.0之后，android 移除了通过 WiFi 和蓝牙 API 来在应用程序中可编程的访问本地硬件标示符。现在 WifiInfo.getMacAddress() 和 BluetoothAdapter.getAddress() 方法都将返回 02:00:00:00:00:00
     * add by heliquan at 2017年6月12日
     *
     * @return
     */
    public static String getMobileMAC(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 如果当前设备系统大于等于6.0 使用下面的方法
            String heightMac = getHeightMac();
            return heightMac;
        } else {
            String lowerMac = getLowerMac(context);
            return lowerMac;
        }
    }

    /**
     * 兼容Android 6.0以下设备获取MAC地址 Android API < 23
     *
     * @return
     */
    private static String getLowerMac(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            // 获取MAC地址
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String mac = wifiInfo.getMacAddress();
            if (null == mac) {
                // 未获取到
                mac = "";
            }
            return mac;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取高版本手机的MAC地址 Android API >= 23
     *
     * @return
     */
    private static String getHeightMac() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                return getMacAddr();
            }
        }
        return macSerial;
    }

    private static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * 获取mac地址   7.0以后能不能获取  未知
     * @return
     */
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {

        }
        return "";
    }

    /**
     * 获取网络连接类型
     */
    public static String getNetworkType(Context mContext) {
        try {
            ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                return networkInfo.getTypeName();
            }
        }catch (Exception e){}
        return null;
    }

    /**
     * gzip
     *
     * @param data
     *
     * @return byte[]
     */
    public static byte[] decompress2(byte[] data) {
        byte[] output = new byte[0];


        Inflater decompresser = new Inflater();
        ByteArrayOutputStream o = null;
        try {
            decompresser.reset();
            decompresser.setInput(data);

            o = new ByteArrayOutputStream(data.length);

            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                if (o != null) {
                    o.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }
}
