package jiguang.chat.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.jpush.im.android.api.JMessageClient;
import jiguang.chat.R;

/**
 * 关于我们界面
 */
public class AboutJChatActivity extends BaseActivity {

    private TextView mJChat_version;
    private TextView mSDK_version;
    private RelativeLayout mJiguang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_jchat);

        initView();
        initData();
    }

    private void initView() {
        initTitle(true, true, "关于我们", "", false, "");
        mJChat_version = (TextView) findViewById(R.id.jchat_version);
        mSDK_version = (TextView) findViewById(R.id.sdk_version);
        mJiguang = (RelativeLayout) findViewById(R.id.jiguang);
    }

    //跳转官网
    public void initData() {
        mJiguang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://150.109.77.221");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        PackageManager manager = getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
            mJChat_version.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //极光IM版本以及sdk版本
        mSDK_version.setText(JMessageClient.getSdkVersionString());
    }
}
