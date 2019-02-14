package jiguang.chat.controller;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import jiguang.chat.R;
import jiguang.chat.activity.MainActivity;
import jiguang.chat.activity.fragment.ContactsFragment;
import jiguang.chat.activity.fragment.ConversationListFragment;
import jiguang.chat.activity.fragment.MeFragment;
import jiguang.chat.adapter.ViewPagerAdapter;
import jiguang.chat.view.MainView;

/**
 * 主界面
 */

public class MainController implements View.OnClickListener, ViewPager.OnPageChangeListener {
    //主界面显示
    private MainView mMainView;
    private MainActivity mContext;
    //会话界面
    private ConversationListFragment mConvListFragment;
    //me 我的界面
    private MeFragment mMeFragment;
    //联系人好友界面
    private ContactsFragment mContactsFragment;


    public MainController(MainView mMainView, MainActivity context) {
        this.mMainView = mMainView;
        this.mContext = context;
        setViewPager();
    }

    //设置页签
    private void setViewPager() {
        final List<Fragment> fragments = new ArrayList<>();
        // init Fragment
        mConvListFragment = new ConversationListFragment();
        mContactsFragment = new ContactsFragment();
        mMeFragment = new MeFragment();

        fragments.add(mConvListFragment);
        fragments.add(mContactsFragment);
        fragments.add(mMeFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mContext.getSupportFragmentManger(),
                fragments);
        mMainView.setViewPagerAdapter(viewPagerAdapter);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.actionbar_msg_btn://会话页签点击
                mMainView.setCurrentItem(0, false);
                break;
            case R.id.actionbar_contact_btn://联系人点击
                mMainView.setCurrentItem(1, false);
                break;
            case R.id.actionbar_me_btn://我页签点击
                mMainView.setCurrentItem(2, false);
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //设置选择
        mMainView.setButtonColor(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void sortConvList() {
        //会话界面排序会话列表
        mConvListFragment.sortConvList();
    }


}