package com.company.frame.ui.activity.vlc;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.company.frame.R;
import com.company.frame.app.AppActivity;
import com.company.frame.utils.FileUtil;
import com.company.frame.utils.VlcUtils;
import com.company.frame.widget.vlc.ENDownloadView;
import com.company.frame.widget.vlc.ENPlayView;
import com.company.frame.widget.vlc.MyVlcVideoView;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.vlc.lib.RecordEvent;
import com.vlc.lib.VlcVideoView;
import com.vlc.lib.listener.MediaListenerEvent;
import com.vlc.lib.listener.util.LogUtils;

import org.videolan.libvlc.Media;

import java.io.File;
import java.util.List;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 可进行拷贝的副本
 */

public final class VlcPlayerActivity extends AppActivity {
    private File recordFile = new File(Environment.getExternalStorageDirectory(), "CME");
    private String directory = recordFile.getAbsolutePath();    //  录像地址
    //    public static final String path = "rtmp://58.200.131.2:1935/livetv/jxhd";
    public static final String path = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u81";

    private MyVlcVideoView player;
    private TextView mTitle;
    private TextView tv_current_time;
    private VlcVideoView vlcVideoView;
    private TextView mChangeFull;
    private TextView snapShot;
    private LinearLayout layout_top, linear_contral;
    private ImageView lock_screen;
    private TextView error_text;
    private ENPlayView startView;
    private ENDownloadView mENDownloadView;
    private TextView recordStart;
    private ImageView back;
    private boolean isPlayering = false;   //视频是否播放的标识符

    private static final int Show_Lock = 111;
    private static final int Show_Unlock = 112;
    private static final int Type_Loading_Visible = 108;
    private static final int Type_Loading_InVisible = 109;
    private static final int Show_Control_InVisible = 113;
    private static final int Show_Control_Visible = 114;
    private static final int Record_Start = 103;
    private static final int Record_Stop = 104;
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    tv_current_time.setText("" + VlcUtils.stringForTime(Integer.parseInt(currentTime)));
                case Type_Loading_Visible:  //加载框 可见
                    mENDownloadView.setVisibility(View.VISIBLE);
                    break;
                case Type_Loading_InVisible:
                    //隐藏 加载框
                    mENDownloadView.setVisibility(View.INVISIBLE);
                    break;
                case Show_Lock: //设置锁屏显示
                    lock_screen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_close_ic));
                    lock_screen.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    layout_top.setVisibility(View.INVISIBLE);
                    mChangeFull.setVisibility(View.INVISIBLE);
                    linear_contral.setVisibility(View.INVISIBLE);
                    tv_current_time.setVisibility(View.INVISIBLE);
                    break;
                case Show_Unlock: //设置锁屏隐藏
                    lock_screen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_open_ic));
                    lock_screen.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    layout_top.setVisibility(View.VISIBLE);
                    mChangeFull.setVisibility(View.VISIBLE);
                    linear_contral.setVisibility(View.VISIBLE);
                    tv_current_time.setVisibility(View.VISIBLE);
                    break;
//                case Record_Start:
//                    recordStart.setTag("recording");
//                    setTextColor(getResources().getColor(R.color.colorAccent), "录像", false);
//                    Drawable record_start = getResources().getDrawable(R.drawable.icon_record_pre);
//                    recordStart.setCompoundDrawablesWithIntrinsicBounds(null, record_start, null, null);
//                    break;
//                case Record_Stop:
//                    recordStart.setTag("over");
//                    setTextColor(getResources().getColor(R.color.white), "录像", true);
//                    toast("录像成功");
//                    Drawable record_end = getResources().getDrawable(R.drawable.icon_record_nore);
//                    recordStart.setCompoundDrawablesWithIntrinsicBounds(null, record_end, null, null);
//                    break;
                case Show_Control_InVisible: //控制布局，锁屏显示
                    layout_top.setVisibility(View.INVISIBLE);
                    mChangeFull.setVisibility(View.INVISIBLE);
                    linear_contral.setVisibility(View.INVISIBLE);
                    tv_current_time.setVisibility(View.INVISIBLE);
                    break;
                case Show_Control_Visible: //控制布局，锁屏隐藏
                    layout_top.setVisibility(View.VISIBLE);
                    mChangeFull.setVisibility(View.VISIBLE);
                    linear_contral.setVisibility(View.VISIBLE);
                    tv_current_time.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    private ENPlayView start;
    private TextView recordStop;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vlc_palyer;
    }

    @Override
    protected void initView() {
        recordFile.mkdirs();
        player = findViewById(R.id.player);
        mTitle = findViewById(R.id.tv_top_title);
        tv_current_time = findViewById(R.id.tv_current_time);
        vlcVideoView = findViewById(R.id.vlc_video_view);
        mChangeFull = findViewById(R.id.change);
        lock_screen = findViewById(R.id.lock_screen);
        recordStart = findViewById(R.id.recordStart);
        layout_top = findViewById(R.id.layout_top);
        linear_contral = findViewById(R.id.linear_contral);
        error_text = findViewById(R.id.error_text);
        error_text.setVisibility(View.INVISIBLE);
        snapShot = findViewById(R.id.snapShot);
        startView = findViewById(R.id.start);
        mENDownloadView = findViewById(R.id.loading);
        back = findViewById(R.id.back);
        start = findViewById(R.id.start);
        recordStop = findViewById(R.id.recordStop);


        back.setOnClickListener(this);
        start.setOnClickListener(this);
        recordStart.setOnClickListener(this);  //录像
        recordStop.setOnClickListener(this);  //录像
        snapShot.setOnClickListener(this);     //截图

        requestPermission();//获取权限不然那不能录像哦

    }

    private void requestPermission() {
        XXPermissions.with(this)
                // 不适配 Android 11 可以这样写
                //.permission(Permission.Group.STORAGE)
                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .permission(Permission.RECORD_AUDIO)
                .permission(Permission.CAMERA)
//                .permission(Permission.WRITE_EXTERNAL_STORAGE)
//                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.READ_PHONE_STATE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
//                            showToast("获取存储权限成功");
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            toast("被永久拒绝授权，请手动授予存储权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(VlcPlayerActivity.this, permissions);
                        } else {
                            toast("获取存储权限失败");
                        }
                    }
                });
    }

    private RecordEvent recordEvent = new RecordEvent();
    private File takeSnapshotFile = new File(Environment.getExternalStorageDirectory(), "CME");

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.start:
                startLive(path);
                break;
            case R.id.recordStop:
                if (isStarting && vlcVideoView.isPrepare() && mFlag_Record) {
                    vlcRecordOver();
                }
                break;
            case R.id.recordStart:    //开始或者介绍录像
                if (isPlayering) {
                    LogUtils.i("path=====Start:=====" + "我是当前播放的url======recordStart.getTag()======" + recordStart.getTag());
                    if (isStarting && vlcVideoView.isPrepare()) {
                        mFlag_Record = true;
                        vlcVideoView.getMediaPlayer().record(directory);
                        recordEvent.startRecord(vlcVideoView.getMediaPlayer(), directory, "cme.mp4");
                        toast("开始---录像!");

                    }
                } else {
                    toast("只有在播放的时候才能录像!");
                }

                break;
            case R.id.snapShot:     //截图
                if (isPlayering) {

                    if (vlcVideoView.isPrepare()) {
                        Media.VideoTrack videoTrack = vlcVideoView.getVideoTrack();
                        if (videoTrack != null) {
                            //vlcVideoView.getMediaPlayer().updateVideoSurfaces();
                            toast("截图成功");
                            //原图
//                            LogUtils.e("path=====截图地址:=====" + takeSnapshotFile.getAbsolutePath()); //   /storage/emulated/0/1604026573438.mp4
                            File localFile = new File(takeSnapshotFile.getAbsolutePath());
                            if (!localFile.exists()) {
                                localFile.mkdir();
                            }
                            recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), 0, 0);
                            //插入相册 解决了华为截图显示问题
                            MediaStore.Images.Media.insertImage(getContentResolver(), vlcVideoView.getBitmap(), "", "");
                            //原图的一半
                            //recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), videoTrack.width / 2, 0);
                        }
                    }
                    //这个就是截图 保存Bitmap就行了
                    //thumbnail.setImageBitmap(vlcVideoView.getBitmap());
                    //Bitmap bitmap = vlcVideoView.getBitmap();
                    //saveBitmap("", bitmap);
                } else {
                    toast("只有在播放的时候才能截图!");
                }
                break;
        }
    }

    private boolean mFlag_Record = false;
    private boolean isStarting = true;

    private void vlcRecordOver() {
        mFlag_Record = false;
        vlcVideoView.getMediaPlayer().record(null);
        FileUtil.RefreshAlbum(directory, true, this);
        toast("结束--录像");
    }


    public void setTextColor(int color, String message, boolean isStarting) {
        recordStart.setText(message);
        recordStart.setTextColor(color);
        this.isStarting = isStarting;
    }

    @Override
    protected void initData() {
        responseListener();
    }

    private void responseListener() {
        vlcVideoView.setMediaListenerEvent(new MediaListenerEvent() {
            @Override
            public void eventBuffing(int event, float buffing) {
                if (buffing < 100) {
                    mENDownloadView.start();
                    mENDownloadView.setVisibility(View.VISIBLE);
//                        showDialog();
                    LogUtils.i("path=====Start:=====" + "我是当前播放的url======eventBuffing===1buffing < 100===========100=");

//                    mHandler.sendEmptyMessage(Type_Loading_Visible);
                } else {
                    isPlayering = true;
//                    hideDialog();
                    LogUtils.i("path=====Start:=====" + "我是当前播放的url======eventBuffing===100==100===========100=");

//                    mENDownloadView.setVisibility(View.GONE);
                    mENDownloadView.release();
                    mENDownloadView.setVisibility(View.GONE);
//                    mHandler.sendEmptyMessage(Type_Loading_InVisible);
                }
                LogUtils.i("path=====Start:=====" + "我是当前播放的url======eventBuffing======" + buffing);
            }

            @Override
            public void eventStop(boolean isPlayError) {
                if (isPlayError) {
                    if (mFlag_Record) { //如果在录像，断开录像
                        vlcRecordOver();
                    }
                    error_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void eventError(int event, boolean show) {
                if (show) {
                    startView.setVisibility(View.VISIBLE);
                    mENDownloadView.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void eventPlay(boolean isPlaying) {

            }

            @Override
            public void eventSystemEnd(String isStringed) {
                if (mFlag_Record) { //如果在录像，断开录像
                    vlcRecordOver();

                }
            }

            @Override
            public void eventCurrentTime(String time) {
                currentTime = time;
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }

            @Override
            public void eventPlayInit(boolean openClose) {
                startView.setVisibility(View.INVISIBLE);
                error_text.setVisibility(View.INVISIBLE);

            }
        });
    }

    private String currentTime = "0";

    /**
     * 开始直播
     *
     * @param path
     */
    private void startLive(String path) {
        vlcVideoView.setPath(path);
        vlcVideoView.startPlay();
        error_text.setVisibility(View.INVISIBLE);
        mHandler.sendEmptyMessage(Type_Loading_Visible);
        mENDownloadView.setVisibility(View.VISIBLE);
        mENDownloadView.start();

    }

    @Override
    public void onResume() {
        super.onResume();
        startLive(path);
        vlcVideoView.startPlay();
        mENDownloadView.setVisibility(View.VISIBLE);
        mENDownloadView.start();

    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        super.onPause();
        //直接调用stop 不然回ANR
        vlcVideoView.onStop();
        mENDownloadView.setVisibility(View.INVISIBLE);
        mENDownloadView.release();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //手动清空字幕
        vlcVideoView.setAddSlave(null);
        vlcVideoView.onDestroy();
        mENDownloadView.release();

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return false;
    }
}