package com.tttrtclive.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Iverson on 2018/1/16 上午11:24
 * 此类用于：
 */

public class LiveView extends RelativeLayout {
    private boolean isFree = true;
    private long flagUserId;

    public long getFlagUserId() {
        return flagUserId;
    }

    public void setFlagUserId(long flagUserId) {
        this.flagUserId = flagUserId;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
        if(free){
            setVisibility(INVISIBLE);
        }else {
            setVisibility(VISIBLE);
        }
    }

    public LiveView(Context context) {
        super(context);
    }

    public LiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
