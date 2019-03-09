package jiguang.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.R;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.dialog.LoadDialog;
import jiguang.chat.view.SlipButton;

/**
 * 非好友关系的信息设置界面 发送名片 加入黑名单
 */

public class NotFriendSettingActivity extends BaseActivity implements SlipButton.OnChangedListener{
    private UserInfo mUserInfo;//查看用户对象
    private SlipButton mBtn_addBlackList;//加入黑名单
    private SlipButton btn_addGroupKeeper;//设为群管理
    private SlipButton btn_setGroupMemSilence;//设为禁言
    private String mUserName;//查看用户对象username
    private long mGroupId;//群组ID
    private boolean isGroupAdmin = false;//是否具有群管理权
    private GroupInfo group;//群组信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_friend_setting);
        //
        mGroupId = getIntent().getLongExtra("mGroupId", 0);
        isGroupAdmin = getIntent().getBooleanExtra("isGroupAdmin",false);

        //
        mBtn_addBlackList = (SlipButton) findViewById(R.id.btn_addBlackList);
        btn_addGroupKeeper = (SlipButton) findViewById(R.id.btn_addGroupKeeper);
        btn_setGroupMemSilence = (SlipButton) findViewById(R.id.btn_setGroupMemSilence);
        mUserName = getIntent().getStringExtra("notFriendUserName");
        mBtn_addBlackList.setOnChangedListener(R.id.btn_addBlackList, this);
        btn_addGroupKeeper.setOnChangedListener(R.id.btn_addGroupKeeper, this);
        btn_setGroupMemSilence.setOnChangedListener(R.id.btn_setGroupMemSilence, this);
        JMessageClient.getUserInfo(mUserName, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0) {
                    mUserInfo = userInfo;
                    mBtn_addBlackList.setChecked(userInfo.getBlacklist() == 1);
                }
            }
        });
        if(isGroupAdmin && mGroupId != 0){//设置可见
            RelativeLayout rl = (RelativeLayout)findViewById(R.id.relativeLayout_addGroupKeeper);
            rl.setVisibility(View.VISIBLE);
            rl = (RelativeLayout)findViewById(R.id.relativeLayout_setGroupMemSilence);
            rl.setVisibility(View.VISIBLE);
            //获取群组信息
            JMessageClient.getGroupInfo(mGroupId, new GetGroupInfoCallback() {
                @Override
                public void gotResult(int i, String s, GroupInfo groupInfo) {
                    if(i == 0){
                        //获取对象值，并判断是否管理员、是否被禁言，设置按钮初始状态
                        group = groupInfo;
                        GroupMemberInfo gmi = groupInfo.getGroupMember(mUserName, null);
                        if(gmi.getType() == GroupMemberInfo.Type.group_keeper){
                            btn_addGroupKeeper.setChecked(true);
                        }else if(gmi.getType() == GroupMemberInfo.Type.group_member){
                            btn_addGroupKeeper.setChecked(false);
                        }

                        btn_setGroupMemSilence.setChecked(groupInfo.isKeepSilence(mUserName, null));
                    }
                }
            });
        }
    }

    public void returnBtn(View view) {
        finish();
    }

    public void sendBusinessCard(View view) {
        //newchange
        ToastUtil.shortToast(NotFriendSettingActivity.this, "您不能发送个人名片");
        return;
        //
        //发送此人的名片
//        Intent businessIntent = new Intent(NotFriendSettingActivity.this, ForwardMsgActivity.class);
//        businessIntent.setFlags(1);
//        businessIntent.putExtra("userName", mUserInfo.getUserName());
//        businessIntent.putExtra("appKey", mUserInfo.getAppKey());
//        if (mUserInfo.getAvatarFile() != null) {
//            businessIntent.putExtra("avatar", mUserInfo.getAvatarFile().getAbsolutePath());
//        }
//        startActivity(businessIntent);
    }

    //switch事件
    @Override
    public void onChanged(int id, boolean checkState) {
        final LoadDialog dialog = new LoadDialog(NotFriendSettingActivity.this, false, "正在设置");
        dialog.show();
        switch (id) {
            case R.id.btn_addBlackList://加入黑名单按钮
                List<String> name = new ArrayList<>();
                name.add(mUserName);
                if (checkState) {//加入
                    JMessageClient.addUsersToBlacklist(name, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "添加成功");
                            } else {
                                mBtn_addBlackList.setChecked(false);
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "添加失败" + responseMessage);
                            }
                        }
                    });
                } else {//移除
                    JMessageClient.delUsersFromBlacklist(name, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "移除成功");
                            } else {
                                mBtn_addBlackList.setChecked(true);
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "移除失败" + responseMessage);
                            }
                        }
                    });
                }
                break;
            case R.id.btn_addGroupKeeper://设为群管理
                List<UserInfo> users = new ArrayList<UserInfo>();
                users.add(mUserInfo);
                if (checkState) {
                    group.addGroupKeeper(users, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "设置成功");
                            } else {
                                btn_addGroupKeeper.setChecked(false);
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "设置失败" + responseMessage);
                            }
                        }
                    });
                }else{
                    group.removeGroupKeeper(users, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "设置成功");
                            } else {
                                btn_addGroupKeeper.setChecked(true);
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "设置失败" + responseMessage);
                            }
                        }
                    });
                }
                break;
            case R.id.btn_setGroupMemSilence://禁言
                if (checkState) {
                    group.setGroupMemSilence(mUserName, null, true, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "设置成功");
                            } else {
                                btn_setGroupMemSilence.setChecked(false);
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "设置失败" + responseMessage);
                            }
                        }
                    });
                }else{
                    group.setGroupMemSilence(mUserName, null, false, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "设置成功");
                            } else {
                                btn_setGroupMemSilence.setChecked(true);
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "设置失败" + responseMessage);
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
}
