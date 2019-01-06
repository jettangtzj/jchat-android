package com.jettang.chat.android.view;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jettang.chat.android.R;
import com.jettang.chat.android.adapter.StickyListAdapter;
import com.jettang.chat.android.utils.sidebar.SideBar;
import com.jettang.chat.android.view.listview.StickyListHeadersListView;


public class SelectFriendView extends LinearLayout {

    private Context mContext;
    private ImageButton mCancelBtn;
    private EditText mSearchEt;
    private StickyListHeadersListView mListView;
    private SideBar mSideBar;
    private TextView mLetterHintTv;
    private LinearLayout mFinishBtn;


    public SelectFriendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void initModule(float ratio, float density) {
        mCancelBtn = (ImageButton) findViewById(R.id.jmui_cancel_btn);
        mFinishBtn = (LinearLayout) findViewById(R.id.finish_btn);
        mSearchEt = (EditText) findViewById(R.id.search_et);
        mListView = (StickyListHeadersListView) findViewById(R.id.sticky_list_view);
        mSideBar = (SideBar) findViewById(R.id.sidebar);
        mLetterHintTv = (TextView) findViewById(R.id.letter_hint_tv);
        mSideBar.setTextView(mLetterHintTv);
        mSideBar.bringToFront();

        mListView.setDrawingListUnderStickyHeader(true);
        mListView.setAreHeadersSticky(true);
        mListView.setStickyHeaderTopOffset(0);
    }

    public void setListeners(OnClickListener listener) {
        mCancelBtn.setOnClickListener(listener);
        mFinishBtn.setOnClickListener(listener);
    }

    public void setAdapter(StickyListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void setSideBarTouchListener(SideBar.OnTouchingLetterChangedListener listener) {
        mSideBar.setOnTouchingLetterChangedListener(listener);
    }

    public void setSelection(int position) {
        mListView.setSelection(position);
    }

    public void setTextWatcher(TextWatcher watcher) {
        mSearchEt.addTextChangedListener(watcher);
    }

    public void setGoneSideBar(boolean isGone) {
        if (isGone) {
            mSideBar.setVisibility(GONE);
        }else {
            mSideBar.setVisibility(VISIBLE);
        }
    }

}
