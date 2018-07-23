package com.tttrtclive.utils;

import android.content.Context;

import com.tttrtclive.callback.MyTTTRtcEngineEventHandler;
import com.wushuangtech.wstechapi.TTTRtcEngine;

/**
 * Created by Administrator on 2018/7/19/0019.
 */

public class TTTSDK {
    private static TTTSDK instance;
    private static Context mContext;
    public static MyTTTRtcEngineEventHandler mMyTTTRtcEngineEventHandler;


    private void TTTSDK(){}

    public static void init(Context context,String appId) {
        if(instance == null){
            synchronized (TTTSDK.class){
                if (instance == null) {
                    instance = new TTTSDK();
                }
            }
        }
        mContext = context;
        initRoomLive(appId);
    }

    //初始化直播sdk
    private static void initRoomLive(String appId) {
        //1.设置SDK的回调接收类
        mMyTTTRtcEngineEventHandler = new MyTTTRtcEngineEventHandler(mContext);
        //2.初始化SDK
        TTTRtcEngine mTTTEngine = TTTRtcEngine.create(mContext,appId,mMyTTTRtcEngineEventHandler);
        if (mTTTEngine == null) {
            System.exit(0);
        }
    }
}
