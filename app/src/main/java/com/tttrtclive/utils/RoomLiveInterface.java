package com.tttrtclive.utils;

import com.tttrtclive.bean.EnterUserInfo;
import com.wushuangtech.bean.LocalAudioStats;
import com.wushuangtech.bean.LocalVideoStats;
import com.wushuangtech.bean.RemoteAudioStats;
import com.wushuangtech.bean.RemoteVideoStats;

import java.util.List;

/**
 * Created by Administrator on 2018/7/19/0019.
 */

public interface RoomLiveInterface {


    //进入房间成功
    void enterRoomSuccess();

    //进入房间失败
    void enterRoomFailue(int error);

    //直播中断线
    void onDisconnected(int errorCode);

    //直播成员退出
    void onMemberExit(long userId);

    //直播成员进入
    void onMemberEnter(long userId, EnterUserInfo userInfo);

    //直播主持人进入
    void onHostEnter(long userId, EnterUserInfo userInfo );

    void onUpdateLiveView(List<EnterUserInfo> userInfos);

    //收到im消息处理
    void dispatchMessage(long srcUserID,int type,String sSeqID,String data);

    //发送的消息的结果
    void sendMessageResult(int resultType,String data);

    //接受视频上行速率
    void localVideoStatus(LocalVideoStats localVideoStats);
    //接受视频下行速率
    void remoteVideoStatus(RemoteVideoStats mRemoteVideoStats);
    //接收音频上行
    void LocalAudioStatus(LocalAudioStats localAudioStats);
    //接收音频下行
    void remoteAudioStatus(RemoteAudioStats mRemoteAudioStats);

    //用户更新数据
    void OnupdateUserBaseInfo(Long roomId , long uid , String sCustom);

    //返回媒体服务器
    void OnConnectSuccess(String ip , int port);
}
