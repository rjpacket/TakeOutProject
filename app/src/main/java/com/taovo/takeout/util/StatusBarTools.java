package com.taovo.takeout.util;

import android.content.Context;

/**
 * Created by Rjp on 2016/6/23.
 */
public class StatusBarTools {

    //Get status bar height
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }else if (resId == 0){
            result = AppUtils.dp2px(context,20);
        }
        return result;
    }

}
