package com.tttrtclive.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tttrtclive.LocalConfig;
import com.tttrtclive.R;
import com.tttrtclive.bean.EnterUserInfo;
import com.tttrtclive.dialog.ExitRoomDialog;
import com.tttrtclive.utils.DensityUtils;
import com.tttrtclive.utils.LiveView;
import com.tttrtclive.utils.MyLog;
import com.tttrtclive.utils.RoomLiveHelp;
import com.tttrtclive.utils.RoomLiveInterface;
import com.wushuangtech.bean.LocalAudioStats;
import com.wushuangtech.bean.LocalVideoStats;
import com.wushuangtech.bean.RemoteAudioStats;
import com.wushuangtech.bean.RemoteVideoStats;
import com.wushuangtech.library.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/19/0019.
 */

public class VideoActivity extends BaseActivity implements RoomLiveInterface , View.OnClickListener{

    private static final String TAG_CLASS = VideoActivity.class.getSimpleName();
    private Context mContext;
    private List<String> mUserIdList = new ArrayList<>();

    private ArrayList<LiveView> mLiveViewList = new ArrayList();

    private RoomLiveHelp mRoomLiveHelp;
    private ExitRoomDialog mExitRoomDialog;
    private AlertDialog.Builder mErrorExitDialog;
    private long hostUserid;
    private long selfUserid;
    private long threeUserid;
    private int mWidth;
    private int mHigh;
    private boolean rightTopVideo = false;
    private boolean localAudio = false;
    private LiveView video_one;
    private LiveView video_two;
    private LiveView video_three;
    private LiveView video_four;
    private ImageView iv_video_view_exit;
    private ImageView iv_video_view_audio_channel;
    private TextView tv_video_view_number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_land);
        mContext = this;
        initView();
        initDialog();
        initErrorExitDialog();
    }
    /**
     * 初始化登录
     */
    private void initView() {

        mRoomLiveHelp = new RoomLiveHelp(this , this);

        video_one = findViewById(R.id.rl_live_video_one);
        video_two = findViewById(R.id.rl_live_video_two);
        video_three = findViewById(R.id.rl_live_video_three);
        video_four = findViewById(R.id.rl_live_video_four);
        iv_video_view_exit = findViewById(R.id.iv_video_view_exit);
        iv_video_view_audio_channel = findViewById(R.id.iv_video_view_audio_channel);
        tv_video_view_number = findViewById(R.id.tv_video_view_number);
        iv_video_view_exit.setFocusable(true);
        iv_video_view_audio_channel.setFocusable(true);

        iv_video_view_audio_channel.requestFocus();
        iv_video_view_audio_channel.setBackgroundResource(R.drawable.auto_transparent_textview);
        iv_video_view_exit.setBackgroundResource(R.drawable.auto_transparent_textview);
        mLiveViewList.add(video_one);
        mLiveViewList.add(video_two);
        mLiveViewList.add(video_three);
        mLiveViewList.add(video_four);

        iv_video_view_exit.setOnClickListener(this);
        iv_video_view_audio_channel.setOnClickListener(this);
        setTextViewContent(tv_video_view_number, R.string.main_title, String.valueOf(LocalConfig.mLoginRoomID));

        //屏幕的宽高
        int[] screenData = DensityUtils.getScreenData(this);
        mWidth = screenData[0];
        mHigh = screenData[1];
        Log.e(TAG_CLASS , " 屏幕总宽 : " + mWidth + " 屏幕总高 : " + mHigh);

        if (LocalConfig.mRole == 1){
            Log.e(TAG_CLASS , "角色主持人 ID " + LocalConfig.mLoginUserID);
            hostUserid = LocalConfig.mLoginUserID;
            selfUserid = LocalConfig.mLoginUserID;

        }else {
            Log.e(TAG_CLASS , "角色参会人员 ID " + LocalConfig.mLoginUserID);
            selfUserid = LocalConfig.mLoginUserID;
        }

        showLocalView(String.valueOf(LocalConfig.mLoginUserID));



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_video_view_exit:
                mExitRoomDialog.show();
                break;
            case R.id.iv_video_view_audio_channel:
                if (!localAudio){
                    //关闭麦克风
                    mRoomLiveHelp.controlLocalAudio(true);
                    localAudio = true;
                    iv_video_view_audio_channel.setImageResource(R.drawable.mainly_btn_mute_speaker_selector);
                }else {
                    mRoomLiveHelp.controlLocalAudio(false);
                    localAudio = false;
                    iv_video_view_audio_channel.setImageResource(R.drawable.mainly_btn_speaker_selector);
                }

                break;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出直播
        if (mRoomLiveHelp != null) {
            mRoomLiveHelp.exitHelp();
            mRoomLiveHelp = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mExitRoomDialog.show();

    }

    @Override
    public void enterRoomSuccess() {
        Log.e(TAG_CLASS , "进入房间成功" + LocalConfig.mLoginUserID);


    }

    @Override
    public void enterRoomFailue(int error) {

    }

    @Override
    public void onDisconnected(int errorCode) {
        //直播中断线
        Log.e(TAG_CLASS , "会议中断线 " + errorCode);
        String message = "";
        if (LocalConfig.mRole == 2){
            if (errorCode == Constants.ERROR_KICK_BY_MASTER_EXIT){
                message = "主持人已退出";
            }else if (errorCode == Constants.ERROR_KICK_BY_HOST){
                message = "被主持人踢出";
            }else if (errorCode == 100) {
                message = "网络连接断开，请检查网络";
            }else {
                message = "连接中断";
            }

        }else {
            if (errorCode == Constants.ERROR_KICK_BY_RELOGIN) {
                message = "重复登录";
            }else if (errorCode == Constants.ERROR_KICK_BY_NEWCHAIRENTER) {
                message = "其他人以主持人身份进入";
            }else if (errorCode == 100) {
                message = "网络连接断开，请检查网络";
            }else {
                message = "连接中断";
            }
        }

        showErrorExitDialog(message);
    }

    @Override
    public void onMemberExit(long userId) {
        Log.e(TAG_CLASS ,  "成员退出 " + userId );
        for (int i = 0; i < mUserIdList.size(); i++) {
            mUserIdList.remove(String.valueOf(userId));
        }

        releaseLiveView(userId);
    }

    @Override
    public void onMemberEnter(long userId, EnterUserInfo userInfo) {
        Log.e(TAG_CLASS ,  "成员进入 " + userId );
        threeUserid = userId;
        showRemoteView(String.valueOf(userId));

    }

    @Override
    public void onHostEnter(long userId, EnterUserInfo userInfo) {
        Log.e(TAG_CLASS ,  "主持人进入 " + userId );
        //        mRoomLiveHelp.openIdRemoteVideo(video_one, userId, true);
        hostUserid = userId;
        showRemoteView(String.valueOf(userId));
    }

    @Override
    public void onUpdateLiveView(List<EnterUserInfo> userInfos) {

    }

    @Override
    public void dispatchMessage(long srcUserID, int type, String sSeqID, String data) {

    }

    @Override
    public void sendMessageResult(int resultType, String data) {

    }

    @Override
    public void localVideoStatus(LocalVideoStats localVideoStats) {

    }

    @Override
    public void remoteVideoStatus(RemoteVideoStats mRemoteVideoStats) {

    }

    @Override
    public void LocalAudioStatus(LocalAudioStats localAudioStats) {

    }

    @Override
    public void remoteAudioStatus(RemoteAudioStats mRemoteAudioStats) {

    }

    @Override
    public void OnupdateUserBaseInfo(Long roomId, long uid, String sCustom) {

    }

    @Override
    public void OnConnectSuccess(String ip, int port) {

    }

    /**
     * 退出弹窗
     */
    private void initDialog() {
        mExitRoomDialog = new ExitRoomDialog(mContext, R.style.NoBackGroundDialog);
        mExitRoomDialog.setCanceledOnTouchOutside(false);
        mExitRoomDialog.mConfirmBT.setOnClickListener(v -> {
            mTTTEngine.stopScreenRecorder();
            exitRoom();
            mExitRoomDialog.dismiss();
        });
        mExitRoomDialog.mDenyBT.setOnClickListener(v -> mExitRoomDialog.dismiss());

    }

    /**
     * 退出房间
     */
    public void exitRoom() {
        MyLog.d("exitRoom was called!... leave room , id : " + LocalConfig.mLoginUserID);
        mTTTEngine.stopAudioMixing();
        mTTTEngine.leaveChannel();
        LocalConfig.mLocalMuteAuido = false;
        finish();
    }

    /**
     * 提示弹窗
     * @param message
     */
    public void showErrorExitDialog(String message) {
        Log.w("wzg", "showErrorExitDialog message " + message);
        if (!TextUtils.isEmpty(message)) {
            mErrorExitDialog.setMessage("退出原因: " + message);//设置显示的内容
            mErrorExitDialog.show();
        }
    }

    private void initErrorExitDialog() {
        if (mErrorExitDialog == null) {
            //添加确定按钮
            mErrorExitDialog = new AlertDialog.Builder(mContext)
                    .setTitle("退出房间提示")//设置对话框标题
                    .setCancelable(false)
                    .setPositiveButton("确定", (dialog, which) -> {//确定按钮的响应事件
                        exitRoom();
                    });
        }
    }


    public void setTextViewContent(TextView textView, int resourceID, String value) {
        String string = getResources().getString(resourceID);
        String result = String.format(string, value);
        textView.setText(result);
    }


    /**
     * 显示视频
     *
     * @param
     */
    private void showRemoteView(String userid) {
        LiveView freeViewLive = getFreeViewLive();
        freeViewLive.setFlagUserId(Long.valueOf(userid));
        freeViewLive.setFree(false);
        mUserIdList.add(userid);
        mRoomLiveHelp.openIdRemoteVideo(freeViewLive, Long.valueOf(userid), true);
        videoTypographyView();
    }

    /**
     * 显示本地视频
     *
     * @param
     */
    private void showLocalView(String userid) {
        LiveView freeViewLive = getFreeViewLive();
        freeViewLive.setFlagUserId(Long.valueOf(userid));
        freeViewLive.setFree(false);
        mUserIdList.add(userid);
        mRoomLiveHelp.openLocalVideo(freeViewLive, false, Constants.CLIENT_ROLE_ANCHOR, 30);
        videoTypographyView();
    }


    /**
     * 获取空闲的View用于播放或者发布.
     */
    protected LiveView getFreeViewLive() {
        LiveView lvFreeView = null;
        for (int i = 0, size = mLiveViewList.size(); i < size; i++) {
            LiveView viewLive = mLiveViewList.get(i);
            if (viewLive.isFree()) {
                lvFreeView = viewLive;
                break;
            }
        }
        return lvFreeView;
    }


    /**
     * 释放直播控件
     * 不补位
     *
     * @param userId
     */
    private void releaseLiveView(long userId) {
        if (userId == 0)
            return;
        for (int i = 0, size = mLiveViewList.size(); i < size; i++) {
            LiveView currentViewLive = mLiveViewList.get(i);
            if (userId == currentViewLive.getFlagUserId()) {
                currentViewLive.removeAllViews();
                SurfaceView mChildSurfaceView = (SurfaceView) currentViewLive.getChildAt(0);
                currentViewLive.removeView(mChildSurfaceView);
                currentViewLive.setFree(true);
                currentViewLive.setFlagUserId(0);
                // 标记最后一个View可用
                mLiveViewList.get(i).setFree(true);
                mLiveViewList.get(i).removeAllViews();
                break;
            }
        }

        videoTypographyView();
    }



    /**
     * 视频布局
     */
    private void videoTypographyView(){
        if (mUserIdList.size() == 1) {
            videoViewOne();
        }else if (mUserIdList.size() == 2){
            videoViewTwo();
        }else if (mUserIdList.size() ==  3){
            videoViewThree();
        }else if (mUserIdList.size() == 4){
            videoViewFour();
        }


    }

    /**
     * 四路视频布局
     */
    private void videoViewFour() {
        LiveView lvFreeView = null;
        for (int i = 0, size = mLiveViewList.size(); i < size; i++) {
            LiveView viewLive = mLiveViewList.get(i);
            if (!viewLive.isFree()) {
                lvFreeView = viewLive;
                if (i ==0){
                    LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                    lvParams.width = mWidth/2;
                    lvParams.height = mWidth/2 * 9/16;
                    if (mHigh/2 <  mWidth/2 * 9/16){
                        lvParams.setMargins(0 , 0  , mWidth/2 ,mHigh/2);
                    }else {
                        lvParams.setMargins(0 , 0  , mWidth/2 ,mHigh/2 - (mHigh/2 - mWidth/2 * 9/16));

                    }
                    lvFreeView.setLayoutParams(lvParams);
                }else if (i == 1){
                    LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                    lvParams.width = mWidth/2;
                    lvParams.height = mWidth/2 * 9/16;
                    if (mHigh/2 <  mWidth/2 * 9/16){
                        lvParams.setMargins(0 , mHigh/2  , mWidth/2 ,0);
                    }else {
                        lvParams.setMargins(0 , mHigh/2 - (mHigh/2 - mWidth/2 * 9/16)  , mWidth/2 ,0);

                    }
                    lvFreeView.setLayoutParams(lvParams);

                }else if (i==2){
                    LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                    lvParams.width = mWidth/2;
                    lvParams.height = mWidth/2 * 9/16;
                    if (mHigh/2 <  mWidth/2 * 9/16){
                        lvParams.setMargins(mWidth/2 , 0  , 0 ,mHigh/2);
                    }else {
                        lvParams.setMargins(mWidth/2 , 0  , 0 ,mHigh/2 - (mHigh/2 - mWidth/2 * 9/16));

                    }
                    lvFreeView.setLayoutParams(lvParams);

                }else if (i==3){
                    LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                    lvParams.width = mWidth/2;
                    lvParams.height = mWidth/2 * 9/16;
                    if (mHigh/2 <  mWidth/2 * 9/16){
                        lvParams.setMargins(mWidth/2 , mHigh/2  , 0 ,0);
                    }else {
                        lvParams.setMargins(mWidth/2 , mHigh/2 - (mHigh/2 - mWidth/2 * 9/16)  , 0 ,0);

                    }
                    lvFreeView.setLayoutParams(lvParams);


                }


            }
        }
    }

    /**
     * 三路视频
     */
    private void videoViewThree() {
        LiveView lvFreeView = null;
        for (int i = 0, size = mLiveViewList.size(); i < size; i++) {
            LiveView viewLive = mLiveViewList.get(i);
            if (!viewLive.isFree()) {
                lvFreeView = viewLive;
                for (int i1 = 0; i1 < mUserIdList.size(); i1++) {
                    if (String.valueOf(viewLive.getFlagUserId()).equals(mUserIdList.get(i1))){
                        if (LocalConfig.mRole == 1){
                            if (mUserIdList.get(i1).equals(String.valueOf(LocalConfig.mLoginUserID))){
                                LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                lvParams.width = mWidth/2;
                                lvParams.height = mWidth/2 * 9/16;
                                lvParams.addRule(RelativeLayout.CENTER_VERTICAL);
                                lvFreeView.setLayoutParams(lvParams);
                            }else {
                                if (!rightTopVideo){
                                    LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                    lvParams.width = mWidth/2;
                                    lvParams.height = mWidth/2 * 9/16;
                                    if (mHigh/2 <  mWidth/2 * 9/16){
                                        lvParams.setMargins(mWidth/2 , 0  , 0 ,mHigh/2);
                                    }else {
                                        lvParams.setMargins(mWidth/2 , 0  , 0 ,mHigh/2 - (mHigh/2 - mWidth/2 * 9/16));

                                    }
                                    lvFreeView.setLayoutParams(lvParams);
                                    rightTopVideo = true;
                                }else {
                                    LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                    lvParams.width = mWidth/2;
                                    lvParams.height = mWidth/2 * 9/16;
                                    if (mHigh/2 <  mWidth/2 * 9/16){
                                        lvParams.setMargins(mWidth/2 , mHigh/2 , 0 , 0);
                                    }else {
                                        lvParams.setMargins(mWidth/2 , mHigh/2 - (mHigh/2 - mWidth/2 * 9/16)  , 0 , 0);
                                    }
                                    lvFreeView.setLayoutParams(lvParams);
                                    rightTopVideo = false;

                                }

                            }
                        }else {
                            if (mUserIdList.get(i1).equals(String.valueOf(hostUserid))){
                                LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                lvParams.width = mWidth/2;
                                lvParams.height = mWidth/2 * 9/16;
                                lvParams.addRule(RelativeLayout.CENTER_VERTICAL);
                                lvFreeView.setLayoutParams(lvParams);
                            }else {
                                if (mUserIdList.get(i1).equals(String.valueOf(selfUserid))){
                                    LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                    lvParams.width = mWidth/2;
                                    lvParams.height = mWidth/2 * 9/16;
                                    if (mHigh/2 <  mWidth/2 * 9/16){
                                        lvParams.setMargins(mWidth/2 , 0  , 0 ,mHigh/2);
                                    }else {
                                        lvParams.setMargins(mWidth/2 , 0  , 0 ,mHigh/2 - (mHigh/2 - mWidth/2 * 9/16));

                                    }
                                    lvFreeView.setLayoutParams(lvParams);
                                }else {
                                    LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                    lvParams.width = mWidth/2;
                                    lvParams.height = mWidth/2 * 9/16;
                                    if (mHigh/2 <  mWidth/2 * 9/16){
                                        lvParams.setMargins(mWidth/2 , mHigh/2 , 0 , 0);
                                    }else {
                                        lvParams.setMargins(mWidth/2 , mHigh/2 - (mHigh/2 - mWidth/2 * 9/16)  , 0 , 0);
                                    }
                                    lvFreeView.setLayoutParams(lvParams);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 两路视频
     */
    private void videoViewTwo() {
        LiveView lvFreeView = null;
        for (int i = 0, size = mLiveViewList.size(); i < size; i++) {
            LiveView viewLive = mLiveViewList.get(i);
            if (!viewLive.isFree()) {
                lvFreeView = viewLive;
                for (int i1 = 0; i1 < mUserIdList.size(); i1++) {
                    if (String.valueOf(viewLive.getFlagUserId()).equals( mUserIdList.get(i1))){
                        if (LocalConfig.mRole == 1){
                            if (mUserIdList.get(i1).equals(String.valueOf(LocalConfig.mLoginUserID))){
                                LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                lvParams.width = mWidth/2;
                                lvParams.height = mWidth/2 * 9/16;
                                lvParams.addRule(RelativeLayout.CENTER_VERTICAL);
                                lvParams.setMargins(0 , 0 , mWidth/2 , 0);
                                lvFreeView.setLayoutParams(lvParams);
                            }else {
                                LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                lvParams.width = mWidth/2;
                                lvParams.height = mWidth/2 * 9/16;
                                lvParams.addRule(RelativeLayout.CENTER_VERTICAL);
                                lvParams.setMargins(mWidth/2  , 0 , 0, 0);
                                lvFreeView.setLayoutParams(lvParams);
                            }

                        }else {
                            if (mUserIdList.get(i1).equals(String.valueOf(LocalConfig.mLoginUserID))){
                                LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                lvParams.width = mWidth/2;
                                lvParams.height = mWidth/2 * 9/16;
                                lvParams.addRule(RelativeLayout.CENTER_VERTICAL);
                                lvParams.setMargins(mWidth/2  , 0 , 0, 0);
                                lvFreeView.setLayoutParams(lvParams);
                            }else {
                                LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
                                lvParams.width = mWidth/2;
                                lvParams.height = mWidth/2 * 9/16;
                                lvParams.addRule(RelativeLayout.CENTER_VERTICAL);
                                lvParams.setMargins(0 , 0 , mWidth/2 , 0);
                                lvFreeView.setLayoutParams(lvParams);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 一路视频布局
     */
    private void videoViewOne() {
        LiveView lvFreeView = null;
        for (int i = 0, size = mLiveViewList.size(); i < size; i++) {
            LiveView viewLive = mLiveViewList.get(i);
            if (!viewLive.isFree()) {
                lvFreeView = viewLive;
                break;
            }
        }

        LiveView.LayoutParams  lvParams = new LiveView.LayoutParams(LiveView.LayoutParams.WRAP_CONTENT , LiveView.LayoutParams.WRAP_CONTENT);
        lvParams.width = mWidth;
        lvParams.height = mWidth * 9/16;
        lvParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        lvFreeView.setLayoutParams(lvParams);
    }


}
