package jiguang.chat.view;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import jiguang.chat.R;
import jiguang.chat.adapter.GroupMemberGridAdapter;

/**
 * 聊天的信息设置布局和组件
 */
public class ChatDetailView extends LinearLayout {


    private LinearLayout mGroupDescLL;//群描述
    private View mSplitLine1;
    private View mSplitLine2;
    private LinearLayout mGroupNameLL;//群名称
    private LinearLayout mMyNameLL;//我在本群的昵称 不显示
    private LinearLayout mGroupNumLL;//群人数上限 不显示
    private LinearLayout mGroupChatRecordLL;//查找聊天内容 不显示
    private LinearLayout mGroupChatDelLL;//清空聊天记录
    private LinearLayout mChatFile;//聊天文件
    private ImageButton mReturnBtn;
    private TextView mTitle;
    private TextView mGroupDesc;
    private ImageButton mMenuBtn;
    private Button mDelGroupBtn;//删除并退群
    private TextView mGroupName;
    private TextView mMyName;
    private GroupGridView mGridView;//群成员显示组件
    public static SlipButton mNoDisturbBtn;
    private SlipButton mBlockBtn;
    private RelativeLayout mBlockRl;
    private View mBlockLine;
    private Context mContext;
    private LinearLayout mTv_moreGroup;//查看更多群成员
    private RelativeLayout mGroupAvatarLL;//群头像
    private Button mAddFriend;//添加好友按钮 不显示
    private LinearLayout mDetailAddFriend;//添加好友 不显示
    private RelativeLayout mClear_rl;//清理缓存
    private ImageView mIv_groupAvatar;

    public ChatDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        // TODO Auto-generated constructor stub
    }

    public void initModule() {
        mGroupDescLL = (LinearLayout) findViewById(R.id.group_desc_ll);
        mGroupDesc = (TextView) findViewById(R.id.chat_detail_group_desc);
        mSplitLine1 = findViewById(R.id.all_member_split_line1);
        mSplitLine2 = findViewById(R.id.all_member_split_line2);
        mGroupNameLL = (LinearLayout) findViewById(R.id.group_name_ll);
        mGroupAvatarLL = (RelativeLayout) findViewById(R.id.rl_groupAvatar);
        mIv_groupAvatar = (ImageView) findViewById(R.id.iv_groupAvatar);
        mMyNameLL = (LinearLayout) findViewById(R.id.group_my_name_ll);
        mGroupNumLL = (LinearLayout) findViewById(R.id.group_num_ll);
        mGroupChatRecordLL = (LinearLayout) findViewById(R.id.group_chat_record_ll);
        mGroupChatDelLL = (LinearLayout) findViewById(R.id.group_chat_del_ll);
        mChatFile = (LinearLayout) findViewById(R.id.chat_file);
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mTitle = (TextView) findViewById(R.id.title);
        mMenuBtn = (ImageButton) findViewById(R.id.right_btn);
        mDelGroupBtn = (Button) findViewById(R.id.chat_detail_del_group);
        mGroupName = (TextView) findViewById(R.id.chat_detail_group_name);
        mMyName = (TextView) findViewById(R.id.chat_detail_my_name);
        mGridView = (GroupGridView) findViewById(R.id.chat_detail_group_gv);
        mNoDisturbBtn = (SlipButton) findViewById(R.id.no_disturb_slip_btn);
        mBlockRl = (RelativeLayout) findViewById(R.id.block_rl);
        mBlockBtn = (SlipButton) findViewById(R.id.block_slip_btn);
        mBlockLine = findViewById(R.id.block_split_line);
        mTv_moreGroup = (LinearLayout) findViewById(R.id.tv_moreGroup);
        mAddFriend = (Button) findViewById(R.id.chat_detail_add_friend);
        mDetailAddFriend = (LinearLayout) findViewById(R.id.detail_add_friend);
        mClear_rl = (RelativeLayout) findViewById(R.id.clear_rl);


        mTitle.setText(mContext.getString(R.string.chat_detail_title));
        mMenuBtn.setVisibility(View.GONE);
        //自定义GridView点击背景为透明色
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setListeners(OnClickListener onClickListener) {
        mGroupDescLL.setOnClickListener(onClickListener);
        mGroupNameLL.setOnClickListener(onClickListener);
        mGroupAvatarLL.setOnClickListener(onClickListener);
        mMyNameLL.setOnClickListener(onClickListener);
        mGroupNumLL.setOnClickListener(onClickListener);
        mGroupChatRecordLL.setOnClickListener(onClickListener);
        mGroupChatDelLL.setOnClickListener(onClickListener);
        mReturnBtn.setOnClickListener(onClickListener);
        mDelGroupBtn.setOnClickListener(onClickListener);
        mTv_moreGroup.setOnClickListener(onClickListener);
        mAddFriend.setOnClickListener(onClickListener);
        mClear_rl.setOnClickListener(onClickListener);
        mChatFile.setOnClickListener(onClickListener);
    }

    public void setOnChangeListener(SlipButton.OnChangedListener listener) {
        mNoDisturbBtn.setOnChangedListener(R.id.no_disturb_slip_btn, listener);
        mBlockBtn.setOnChangedListener(R.id.block_slip_btn, listener);
    }

    public void setItemListener(AdapterView.OnItemClickListener listener) {
        mGridView.setOnItemClickListener(listener);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setAdapter(GroupMemberGridAdapter adapter) {
        mGridView.setAdapter(adapter);
    }

    public void setGroupName(String str) {
        mGroupName.setText(str);
    }

    public void setMyName(String str) {
        mMyName.setText(str);
    }

    /**
     * 将聊天信息设置界面变为单聊的显示模式
     * @param friend 当前聊天的对象是否为好友
     */
    public void setSingleView(boolean friend) {
        if (friend) {
            mDelGroupBtn.setVisibility(VISIBLE);
            mDetailAddFriend.setVisibility(GONE);
        } else {
            mDetailAddFriend.setVisibility(VISIBLE);
            mDelGroupBtn.setVisibility(GONE);
        }
        mGroupNameLL.setVisibility(View.GONE);
        mGroupAvatarLL.setVisibility(View.GONE);
        mGroupNumLL.setVisibility(View.GONE);
        mMyNameLL.setVisibility(View.GONE);
        mDelGroupBtn.setText("删除好友");
    }

    public void updateGroupName(String newName) {
        mGroupName.setText(newName);
    }

    public GroupGridView getGridView() {
        return mGridView;
    }


    public void setGroupDesc(String desc) {
        mGroupDesc.setText(desc);
    }

    public void dismissAllMembersBtn() {
        mSplitLine1.setVisibility(View.GONE);
        mSplitLine2.setVisibility(View.GONE);
        mGroupDescLL.setVisibility(View.GONE);
    }

    public void initNoDisturb(int status) {
        mNoDisturbBtn.setChecked(status == 1);
    }

    public void setNoDisturbChecked(boolean flag) {
        mNoDisturbBtn.setChecked(flag);
    }

    public void showBlockView(int status) {
        mBlockRl.setVisibility(VISIBLE);
        mBlockLine.setVisibility(VISIBLE);
        mBlockBtn.setChecked(status == 1);
    }

    public void setBlockChecked(boolean flag) {
        mBlockBtn.setChecked(flag);
    }

    public void isLoadMoreShow(boolean isLoad) {
        if (isLoad) {
            mTv_moreGroup.setVisibility(VISIBLE);
        } else {
            mTv_moreGroup.setVisibility(GONE);
        }
    }

    public void setGroupAvatar(File groupAvatar) {
        mIv_groupAvatar.setImageBitmap(BitmapFactory.decodeFile(groupAvatar.getAbsolutePath()));
    }
}
