package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.R;
import jiguang.chat.application.JGApplication;
import jiguang.chat.database.FriendEntry;
import jiguang.chat.database.FriendRecommendEntry;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.dialog.LoadDialog;
import jiguang.chat.view.SlipButton;

/**
 * 好友的设置界面 备注名、是否黑名单、发送名片
 */

public class FriendSettingActivity extends BaseActivity implements SlipButton.OnChangedListener, View.OnClickListener {

    private RelativeLayout mSetNoteName;
    private SlipButton mBtn_addBlackList;//加入黑名单
    private SlipButton btn_addGroupKeeper;//设为群管理
    private SlipButton btn_setGroupMemSilence;//设为禁言
    private Button mBtn_deleteFriend;
    private TextView mTv_noteName;//备注名
    private Dialog mDialog;
    private UserInfo mFriendInfo;//用户对象
    private String mUserName;//用户名
    private RelativeLayout mRl_business;//发送名片
    private long mGroupId;//群组ID
    private boolean isGroupAdmin = false;//是否具有群管理权
    private GroupInfo group;//群组信息


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_setting);
        initView();
        initData();

    }

    private void initData() {
        //设置黑名单
        mBtn_addBlackList.setOnChangedListener(R.id.btn_addBlackList, this);
        btn_addGroupKeeper.setOnChangedListener(R.id.btn_addGroupKeeper, this);
        btn_setGroupMemSilence.setOnChangedListener(R.id.btn_setGroupMemSilence, this);
        mBtn_deleteFriend.setOnClickListener(this);
        mSetNoteName.setOnClickListener(this);
        mRl_business.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setNoteName://设置备注名
                Intent intent = new Intent(FriendSettingActivity.this, SetNoteNameActivity.class);
                intent.putExtra("user", getIntent().getStringExtra("userName"));
                intent.putExtra("note", getIntent().getStringExtra("noteName"));
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_deleteFriend://删除好友
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.jmui_cancel_btn:
                                mDialog.dismiss();
                                break;
                            case R.id.jmui_commit_btn:
                                final Dialog dialog = DialogCreator.createLoadingDialog(FriendSettingActivity.this, getString(R.string.processing));
                                dialog.show();
                                mFriendInfo.removeFromFriendList(new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        dialog.dismiss();
                                        if (responseCode == 0) {
                                            //将好友删除时候还原黑名单设置
                                            List<String> name = new ArrayList<>();
                                            name.add(mFriendInfo.getUserName());
                                            JMessageClient.delUsersFromBlacklist(name, null);

                                            FriendEntry friend = FriendEntry.getFriend(JGApplication.getUserEntry(),
                                                    mFriendInfo.getUserName(), mFriendInfo.getAppKey());
                                            if (friend != null) {
                                                friend.delete();
                                            }
                                            FriendRecommendEntry entry = FriendRecommendEntry
                                                    .getEntry(JGApplication.getUserEntry(),
                                                            mFriendInfo.getUserName(), mFriendInfo.getAppKey());
                                            if (entry != null) {
                                                entry.delete();
                                            }
                                            ToastUtil.shortToast(FriendSettingActivity.this, "移除好友");
                                            mDialog.dismiss();
                                            delConvAndReturnMainActivity();
                                        } else {
                                            mDialog.dismiss();
                                            ToastUtil.shortToast(FriendSettingActivity.this, "移除失败");
                                        }
                                    }
                                });
                                break;
                        }
                    }
                };
                mDialog = DialogCreator.createBaseDialogWithTitle(this,
                        getString(R.string.delete_friend_dialog_title), listener);
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                mDialog.show();
                break;
            case R.id.rl_business://发送好友名片
                Intent businessIntent = new Intent(FriendSettingActivity.this, ForwardMsgActivity.class);
                businessIntent.setFlags(1);
                businessIntent.putExtra("userName", mFriendInfo.getUserName());
                businessIntent.putExtra("appKey", mFriendInfo.getAppKey());
                if (mFriendInfo.getAvatarFile() != null) {
                    businessIntent.putExtra("avatar", mFriendInfo.getAvatarFile().getAbsolutePath());
                }
                startActivity(businessIntent);
                break;
            default:
                break;
        }
    }

    public void delConvAndReturnMainActivity() {
        Conversation conversation = JMessageClient.getSingleConversation(mFriendInfo.getUserName(), mFriendInfo.getAppKey());
        EventBus.getDefault().post(new Event.Builder().setType(EventType.deleteConversation)
                .setConversation(conversation)
                .build());
        JMessageClient.deleteSingleConversation(mFriendInfo.getUserName(), mFriendInfo.getAppKey());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        initTitle(true, true, "设置", "", false, "");
        //
        mGroupId = getIntent().getLongExtra("mGroupId", 0);
        isGroupAdmin = getIntent().getBooleanExtra("isGroupAdmin",false);
        mUserName = getIntent().getStringExtra("userName");
        //
        mSetNoteName = (RelativeLayout) findViewById(R.id.setNoteName);
        mBtn_addBlackList = (SlipButton) findViewById(R.id.btn_addBlackList);
        btn_addGroupKeeper = (SlipButton) findViewById(R.id.btn_addGroupKeeper);
        btn_setGroupMemSilence = (SlipButton) findViewById(R.id.btn_setGroupMemSilence);
        mBtn_deleteFriend = (Button) findViewById(R.id.btn_deleteFriend);
        mTv_noteName = (TextView) findViewById(R.id.tv_noteName);
        mRl_business = (RelativeLayout) findViewById(R.id.rl_business);
        //newchange
        mRl_business.setVisibility(View.GONE);
        //
        final Dialog dialog = DialogCreator.createLoadingDialog(FriendSettingActivity.this,
                FriendSettingActivity.this.getString(R.string.jmui_loading));
        dialog.show();
        if (!TextUtils.isEmpty(getIntent().getStringExtra("noteName"))) {
            mTv_noteName.setText(getIntent().getStringExtra("noteName"));
        }
        JMessageClient.getUserInfo(mUserName, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                dialog.dismiss();
                if (responseCode == 0) {
                    mFriendInfo = info;
                    mBtn_addBlackList.setChecked(info.getBlacklist() == 1);
                    if (info.isFriend()) {
                        mBtn_deleteFriend.setVisibility(View.VISIBLE);
                        mSetNoteName.setVisibility(View.VISIBLE);
                    }else {
                        mBtn_deleteFriend.setVisibility(View.GONE);
                        mSetNoteName.setVisibility(View.GONE);
                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            mTv_noteName.setText(data.getStringExtra("updateName"));
        }
    }

    /**
     * switch事件
     * @param id
     * @param checkState
     */
    @Override
    public void onChanged(int id, boolean checkState) {
        final LoadDialog dialog = new LoadDialog(FriendSettingActivity.this, false, "正在设置");
        dialog.show();
        switch (id) {
            case R.id.btn_addBlackList://加入黑名单按钮
                String userName = getIntent().getStringExtra("userName");
                List<String> name = new ArrayList<>();
                name.add(userName);
                if (checkState) {//加入
                    JMessageClient.addUsersToBlacklist(name, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(FriendSettingActivity.this, "添加成功");
                            } else {
                                mBtn_addBlackList.setChecked(false);
                                ToastUtil.shortToast(FriendSettingActivity.this, "添加失败" + responseMessage);
                            }
                        }
                    });
                } else {//移除
                    JMessageClient.delUsersFromBlacklist(name, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(FriendSettingActivity.this, "移除成功");
                            } else {
                                mBtn_addBlackList.setChecked(true);
                                ToastUtil.shortToast(FriendSettingActivity.this, "移除失败" + responseMessage);
                            }
                        }
                    });
                }
                break;
            case R.id.btn_addGroupKeeper://设为群管理
                List<UserInfo> users = new ArrayList<UserInfo>();
                users.add(mFriendInfo);
                if (checkState) {
                    group.addGroupKeeper(users, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(FriendSettingActivity.this, "设置成功");
                            } else {
                                btn_addGroupKeeper.setChecked(false);
                                ToastUtil.shortToast(FriendSettingActivity.this, "设置失败" + responseMessage);
                            }
                        }
                    });
                }else{
                    group.removeGroupKeeper(users, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(FriendSettingActivity.this, "设置成功");
                            } else {
                                btn_addGroupKeeper.setChecked(true);
                                ToastUtil.shortToast(FriendSettingActivity.this, "设置失败" + responseMessage);
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
                                ToastUtil.shortToast(FriendSettingActivity.this, "设置成功");
                            } else {
                                btn_setGroupMemSilence.setChecked(false);
                                ToastUtil.shortToast(FriendSettingActivity.this, "设置失败" + responseMessage);
                            }
                        }
                    });
                }else{
                    group.setGroupMemSilence(mUserName, null, false, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(FriendSettingActivity.this, "设置成功");
                            } else {
                                btn_setGroupMemSilence.setChecked(true);
                                ToastUtil.shortToast(FriendSettingActivity.this, "设置失败" + responseMessage);
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
