package com.gjjy.frontlib.ui.activity;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.adapter.FrontMenuAdapter;
import com.gjjy.frontlib.adapter.WordsListAdapter;
import com.gjjy.frontlib.adapter.WordsListChildAdapter;
import com.gjjy.frontlib.adapter.holder.WordsListHolder;
import com.gjjy.frontlib.mvp.presenter.WordsListPresenter;
import com.gjjy.frontlib.mvp.view.WordsListView;
import com.gjjy.frontlib.widget.FrontMenuView;
import com.gjjy.frontlib.widget.WordsListRecyclerView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.AnimUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;

import java.util.ArrayList;
import java.util.List;

/**
 * 单词表页面
 */
@Route(path = "/front/wordListActivity")
public class WordsListActivity extends BaseActivity implements WordsListView {
    @Presenter
    private WordsListPresenter mPresenter;

    private Toolbar tbToolbar;
    private ImageView ivRvTopItem;
    private LinearLayout llMenuLayout;
    private WordsListRecyclerView rvList;
    private TextView tvMenuBtn;
    private FrontMenuView hmvMenu;
    private PopupWindow pwPopup;
    private DialogOption mLoadingDialog;
    private CheckBox cbPinYinStatus;
    private CheckBox cbExplainStatus;

    private WordsListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.words_list_tb_toolbar );
        llMenuLayout = findViewById( R.id.words_list_ll_menu_Layout );
        tvMenuBtn = findViewById( R.id.words_bar_tv_menu_btn );
        cbPinYinStatus = findViewById( R.id.words_list_cb_py_status );
        cbExplainStatus = findViewById( R.id.words_list_cb_en_status );
        ivRvTopItem = findViewById( R.id.words_list_iv_rv_top_item );
        rvList = findViewById( R.id.words_list_rv_list );

        hmvMenu = new FrontMenuView( getContext() );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        mLoadingDialog = createLoadingDialog();

        tbToolbar.setBackBtnOfImg( R.drawable.ic_answer_bar_close );

        mAdapter = new WordsListAdapter(
                Glide.with( this ),
                new ArrayList<>()
        );

        rvList.setLayoutManager( new LinearLayoutManager( this ) );
        rvList.setAdapter( mAdapter );
    }

    private int mFirstViewPositionOfOfRecyclerView = -1;
    private View mFirstViewOfRecyclerView;
    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        tbToolbar.setOnClickOtherBtnListener(v -> StartUtil.startSearchWordsActivity());

        tvMenuBtn.setOnClickListener(v -> showMenu());

        cbPinYinStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPresenter.setEnablePinYin( isChecked );
            mAdapter.setEnablePinYin( isChecked );
        });

        cbExplainStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPresenter.setEnableExplain( isChecked );
            mAdapter.setEnableExplain( isChecked );
        });

        //题型分类
        hmvMenu.setOnItemClickListener((adapter, view, data, i) -> {
            int id = data.getId();
            if( !mPresenter.eqSelectCategoryId( id ) ) {
                mAdapter.hideItemAll();
            }
            onCallAllCategoryTitle( id );
            mPresenter.setSelectCategoryId( id );
            ivRvTopItem.setVisibility( View.GONE );
            hideMenu();
        });

        mAdapter.setOnItemClickListener((adapter, view, data, pos) -> {
            rvList.setEnabled( false );
            post(() -> rvList.setEnabled( true ), 200);
            if( data.isShow() && !data.isHaveChildData() ) mPresenter.getUnitWordsList( data.getId() );
        });

        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if( dy > 0 ) {
                    ViewGroup vg = getCreateViewGroupOfTopItem();
                    if( vg != null ) createTopItem( vg );
                }
                changedTopItemVis();
            }
        });

        ivRvTopItem.setOnClickListener(v -> {
            WordsListHolder h = mAdapter.getHolder( mFirstViewPositionOfOfRecyclerView );
            mAdapter.onItemClick(
                    mAdapter,
                    h != null ? h.itemView : null,
                    mAdapter.getItemData( mFirstViewPositionOfOfRecyclerView ),
                    mFirstViewPositionOfOfRecyclerView
            );

            if( !mAdapter.isItemShowing() ) {
                ivRvTopItem.setVisibility( View.GONE );
            }
        });
    }

    @Nullable
    private ViewGroup getCreateViewGroupOfTopItem() {
        LinearLayoutManager llm = (LinearLayoutManager) rvList.getLayoutManager();
        if( llm == null ) return null;
        int firstPos = mFirstViewPositionOfOfRecyclerView;
        int findPos = llm.findFirstVisibleItemPosition();
        if( firstPos != findPos ) {
            ViewGroup vg = (ViewGroup) llm.findViewByPosition( findPos );
            if( vg != null ) mFirstViewPositionOfOfRecyclerView = findPos;
            return vg;
        }
        return null;
    }

    private void createTopItem(@NonNull ViewGroup vg) {
        post(() -> {
            View v = vg.getChildAt( 0 );
            v.setBackgroundColor( Color.WHITE );
            ivRvTopItem.setVisibility( View.GONE );
            ivRvTopItem.setImageBitmap( Utils.viewToBitmap( v ) );
            v.setBackgroundColor( Color.TRANSPARENT );
        });
        mFirstViewOfRecyclerView = vg;
    }

    private void changedTopItemVis() {
        if( mFirstViewOfRecyclerView == null ) return;
        ViewGroup vg = ((ViewGroup)mFirstViewOfRecyclerView);
        View vChildTitleBar = vg.getChildAt( 0 );
        int vis = View.VISIBLE;
        int itemHeight = vChildTitleBar.getHeight();

        //到达item总高度的结尾
        if( vg.getHeight() - Math.abs( vg.getY() ) < itemHeight ) {
            vis = View.GONE;
            LogUtil.e("changedTopItemVis -> last");
        }

        //没有显示RecyclerView
        if( vg.getChildAt( 1 ).getVisibility() != View.VISIBLE ) {
            vis = View.GONE;
            LogUtil.e("changedTopItemVis -> not data");
        }

        //超出原始位置
        if( vg.getY() >= 0 ) {
            vis = View.GONE;
            LogUtil.e("changedTopItemVis -> zero");
        }
        ivRvTopItem.setVisibility( vis );
    }


    private void showMenu() {
        if( pwPopup == null ) {
            pwPopup = new PopupWindow( hmvMenu );
            pwPopup.setOutsideTouchable( true );
            pwPopup.setOnDismissListener(() -> setTransparent( false ));
        }
        if( pwPopup.isShowing() ) return;
        hmvMenu.measure( 0, 0 );
        pwPopup.setWidth( hmvMenu.getMeasuredWidth() );
        pwPopup.setHeight( hmvMenu.getMeasuredHeight() );
        pwPopup.showAsDropDown( tvMenuBtn );
        setTransparent( true );
    }
    private void hideMenu() { if( pwPopup != null ) pwPopup.dismiss(); }

    @Override
    public void onCallLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( isShow ) {
            if( !mLoadingDialog.isShowing() ) mLoadingDialog.show();
        }else {
            if( mLoadingDialog.isShowing() )mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onCallAllCategoryTitle(int id) {
        FrontMenuAdapter.ItemData data = hmvMenu.getData( id );
        if( data != null ) tvMenuBtn.setText( data.getTitle() );
    }

    @Override
    public void onCallAllCategory(@NonNull List<FrontMenuAdapter.ItemData> list,
                                  @Nullable FrontMenuAdapter.ItemData currentItem) {
        boolean isExistData = list.size() > 0;
        llMenuLayout.setVisibility( isExistData ? View.VISIBLE : View.GONE );
        if( !isExistData ) {
            mPresenter.setSelectCategoryId( -1 );
            return;
        }
        hmvMenu.setData( list );
        if( currentItem != null ) {
            tvMenuBtn.setText( currentItem.getTitle() );
        }
        tvMenuBtn.setVisibility( View.VISIBLE );
    }

    @Override
    public void onCallCategoryContent(@NonNull List<WordsListAdapter.ItemData> list) {
        mAdapter.clearItemData();
        mAdapter.addItemData( list );
        mAdapter.notifyDataSetChanged();
        rvList.scrollToPosition( 0 );
    }

    @Override
    public void onCallWordsChildList(@NonNull List<WordsListChildAdapter.ItemData> list) {
        WordsListChildAdapter adapter = mAdapter.getCurrentChildAdapter();
        if( adapter != null ) {
            adapter.setItemData( list );
            adapter.notifyDataSetChanged();
        }

        onCallLoadingDialog( false );
        //拼音和英文的显示与隐藏
        if( cbPinYinStatus.getVisibility() == View.VISIBLE ) return;
        AnimUtil.setAlphaAnimator(
                200, (Consumer<ValueAnimator>) null, cbPinYinStatus, cbExplainStatus
        );
    }
}
