package com.gjjy.frontlib.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.adapter.WordsListChildAdapter;
import com.gjjy.frontlib.mvp.presenter.SearchWordsPresenter;
import com.gjjy.frontlib.mvp.view.SearchWordsView;
import com.ybear.mvp.annotations.Presenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索页面
 */
@Route(path = "/front/searchWordsActivity")
public class SearchWordsActivity extends BaseActivity implements SearchWordsView {
    @Presenter
    private SearchWordsPresenter mPresenter;

    private EditText etSearch;
    private TextView tvCancelBtn;
    private TextView tvNotKwTips;
    private RecyclerView rvList;
    private WordsListChildAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_words);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        etSearch = findViewById( R.id.search_words_et_search );
        tvCancelBtn = findViewById( R.id.search_words_tv_cancel_btn );
        tvNotKwTips = findViewById( R.id.search_words_tv_not_kw_tips );
        rvList = findViewById( R.id.search_words_rv_list );
    }

    private void initData() {
        setStatusBarHeightForSpace( findViewById( R.id.toolbar_height_space ) );
        mAdapter = new WordsListChildAdapter( this, new ArrayList<>() );
        rvList.setLayoutManager( new LinearLayoutManager( this ) );
//        rvList.addItemDecoration(
//                new DividerItemDecoration( this, DividerItemDecoration.VERTICAL )
//        );
        rvList.setAdapter( mAdapter );
    }

    private boolean isTextChanging = false;

    private void initListener() {
        tvCancelBtn.setOnClickListener(v -> finish());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( isTextChanging ) return;
                isTextChanging = true;
                postDelayed( () -> {
                    if( s == null || TextUtils.isEmpty( s.toString().trim() ) ) {
                        onCallWordsChildList( null );
                        return;
                    }
                    mPresenter.search( s.toString() );
                    isTextChanging = false;
                }, 1000);

            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onCallWordsChildList(List<WordsListChildAdapter.ItemData> list) {
        mAdapter.clearItemData();
        if( tvNotKwTips.getVisibility() != View.VISIBLE ) tvNotKwTips.setVisibility( View.VISIBLE );
        if( list == null || list.size() == 0 ) {
            mAdapter.notifyDataSetChanged();
            rvList.setVisibility( View.GONE );
            return;
        }
        mAdapter.addItemData( list );
        mAdapter.notifyDataSetChanged();
        rvList.setVisibility( View.VISIBLE );
    }
}
