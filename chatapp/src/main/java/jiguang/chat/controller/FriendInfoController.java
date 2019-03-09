package jiguang.chat.controller;

import android.content.Intent;
import android.view.View;

import cn.jpush.im.android.api.model.UserInfo;
import jiguang.chat.R;
import jiguang.chat.activity.FriendInfoActivity;
import jiguang.chat.activity.FriendSettingActivity;
import jiguang.chat.view.FriendInfoView;

/**
 * 好友信息查看控制器
 */

public class FriendInfoController implements View.OnClickListener {
    private FriendInfoActivity mContext;
    private UserInfo friendInfo;
    private long mGroupId;//群组ID
    private boolean isGroupAdmin = false;//是否具有群管理权

    public FriendInfoController(FriendInfoView friendInfoView, FriendInfoActivity context) {
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goToChat:
                mContext.startChatActivity();
                break;
            case R.id.iv_friendPhoto:
                mContext.startBrowserAvatar();
                break;
            case R.id.jmui_commit_btn://进入好友设置、右上键
                Intent intent = new Intent(mContext, FriendSettingActivity.class);
                intent.putExtra("userName", friendInfo.getUserName());
                intent.putExtra("noteName", friendInfo.getNotename());
                intent.putExtra("mGroupId", mGroupId);
                intent.putExtra("isGroupAdmin", isGroupAdmin);
                mContext.startActivity(intent);
                break;
            case R.id.return_btn:
                mContext.finish();
                break;
            default:
                break;
        }
    }

    public void setFriendInfo(UserInfo info) {
        friendInfo = info;
    }

    public long getmGroupId() {
        return mGroupId;
    }

    public void setmGroupId(long mGroupId) {
        this.mGroupId = mGroupId;
    }

    public boolean isGroupAdmin() {
        return isGroupAdmin;
    }

    public void setGroupAdmin(boolean groupAdmin) {
        isGroupAdmin = groupAdmin;
    }
}
