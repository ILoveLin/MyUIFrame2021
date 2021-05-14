package com.company.frame.ui.fragment;


import android.view.View;

import com.company.frame.R;
import com.company.frame.action.StatusAction;
import com.company.frame.app.AppFragment;
import com.company.frame.app.TitleBarFragment;
import com.company.frame.ui.activity.MainActivity;
import com.company.frame.ui.activity.vlc.VlcPlayerActivity;
import com.company.frame.widget.StatusLayout;


/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 可进行拷贝的副本
 */
public final class Fragment04 extends TitleBarFragment<MainActivity> implements StatusAction {
    private StatusLayout mStatusLayout;

    public static Fragment04 newInstance() {
        return new Fragment04();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_04;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.fragment_04_statuslayout);
        /**
         * 跳转vlc播放界面,支持直播或者点播,rtmp,rtsp,http,https,smb等等功能协议的--------录像,截图功能
         */
        findViewById(R.id.tv_022).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(VlcPlayerActivity.class);
            }
        });
    }

    @Override
    protected void initData() {
        showError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("我被点击了~");
            }
        });
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

}