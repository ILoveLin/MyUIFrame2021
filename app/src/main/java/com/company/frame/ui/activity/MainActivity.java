package com.company.frame.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.company.frame.R;
import com.company.frame.app.AppActivity;
import com.company.frame.app.AppFragment;
import com.company.frame.manager.ActivityManager;
import com.company.frame.other.DoubleClickHelper;
import com.company.frame.other.IntentKey;
import com.company.frame.other.KeyboardWatcher;
import com.company.frame.ui.fragment.Fragment01;
import com.company.frame.ui.fragment.Fragment02;
import com.company.frame.ui.fragment.Fragment03;
import com.company.frame.ui.fragment.Fragment04;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.hjq.xtoast.XToast;
import com.hjq.xtoast.draggable.SpringDraggable;

import java.util.List;


public final class MainActivity extends AppActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private XToast<XToast<?>> xToastXToast;
    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    public static void start(Context context) {
        start(context, Fragment01.class);
    }

    public static void start(Context context, Class<? extends AppFragment<?>> fragmentClass) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(IntentKey.INDEX, fragmentClass);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_home_pager);

        //??????tab????????????   https://juejin.cn/post/6867895624025997320
        mBottomNavigationView = findViewById(R.id.bv_home_navigation);

        // ???????????????????????????
        mBottomNavigationView.setItemIconTintList(null);
        // ?????????????????????????????????
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        // ???????????????????????????????????????
        Menu menu = mBottomNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            mBottomNavigationView.findViewById(menu.getItem(i).getItemId()).setOnLongClickListener(v -> true);
        }
    }

    @Override
    protected void initData() {
        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(Fragment01.newInstance());
        mPagerAdapter.addFragment(Fragment02.newInstance());
        mPagerAdapter.addFragment(Fragment03.newInstance());
        mPagerAdapter.addFragment(Fragment04.newInstance());
        mViewPager.setAdapter(mPagerAdapter);

        onNewIntent(getIntent());
        postDelayed(new Runnable() {
            @Override
            public void run() {
                getGlobalToastBall();
            }
        }, 1000);

    }


    private void getGlobalToastBall() {

        // ?????? Application ?????????????????????????????? Toast
        xToastXToast = new XToast<>(MainActivity.this);  //getApplication()  ??????App???????????????????????????activity????????????????????????
        xToastXToast.setView(R.layout.toast_phone)
                .setGravity(Gravity.END | Gravity.BOTTOM)
                .setYOffset(200)
                // ???????????????????????????
                .setDraggable(new SpringDraggable())
                .setOnClickListener(android.R.id.icon, new XToast.OnClickListener<ImageView>() {

                    @Override
                    public void onClick(XToast<?> toast, ImageView view) {
                        ToastUtils.show("???????????????");
                        // ????????????????????????????????????
                        // Intent intent = new Intent(Intent.ACTION_DIAL);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // toast.startActivity(intent);
                        // ?????? 10 ??????????????? Activity ??????????????????
                        // https://developer.android.google.cn/about/versions/10/privacy/changes#background-activity-starts
                    }
                })
                .show();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int fragmentIndex = mPagerAdapter.getFragmentIndex(getSerializable(IntentKey.INDEX));
        if (fragmentIndex == -1) {
            return;
        }

        mViewPager.setCurrentItem(fragmentIndex);
        switch (fragmentIndex) {
            case 0:
                mBottomNavigationView.setSelectedItemId(R.id.menu_home);
                break;
            case 1:
                mBottomNavigationView.setSelectedItemId(R.id.home_found);
                break;
            case 2:
                mBottomNavigationView.setSelectedItemId(R.id.home_message);
                break;
            case 3:
                mBottomNavigationView.setSelectedItemId(R.id.home_me);
                break;
            default:
                break;
        }
    }

    /**
     * {@link BottomNavigationView.OnNavigationItemSelectedListener}
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_home) {
            mViewPager.setCurrentItem(0);
            return true;
        } else if (itemId == R.id.home_found) {
            mViewPager.setCurrentItem(1);
            return true;
        } else if (itemId == R.id.home_message) {
            mViewPager.setCurrentItem(2);
            return true;
        } else if (itemId == R.id.home_me) {
            mViewPager.setCurrentItem(3);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            toast(R.string.home_exit_hint);
            return;
        }

        // ???????????????????????????????????????????????????????????????
        moveTaskToBack(false);
        postDelayed(() -> {
            // ?????????????????????????????????????????????
            ActivityManager.getInstance().finishAllActivities();
            // ????????????????????????????????? API ?????????????????? Activity onDestroy ???????????????????????????
            // System.exit(0);
        }, 300);
    }

    @Override
    public boolean isStatusBarEnabled() {
        // ????????????????????????
        return !super.isStatusBarEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mBottomNavigationView.setOnNavigationItemSelectedListener(null);
    }

}