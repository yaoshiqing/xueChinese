package com.gjjy.basiclib.widget.drag;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.ybcomponent.OnTouchListener;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.base.adapter.IItemData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.View.GONE;

public class DragViewPool implements OnTouchListener {
    public interface OnItemStatusListener {
        void onChanged(@Nullable Object data, boolean isShowItem);
    }

    private final List<DragRecyclerView> mViewList;
    private DragRecyclerView rvDragAddView = null;
    private ImageView ivFloatingView;
    private View vTouchView;

    private final static int[] DRAW_XY = new int[ 2 ];
    private final static float[] TOUCH_XY = new float[ 2 ];
    private int dragToPosition = -1;
    private int fromPosition = -1;
    private int toPosition = -1;
    private boolean isEnablePoolDrag = true;
    private boolean isEnableViewDrag = true;

    private OnItemStatusListener mOnItemStatusListener;

    private DragViewPool() {
        mViewList = new ArrayList<>();
    }

    public static DragViewPool create() { return new DragViewPool(); }

    private ViewGroup mSuperView;
    public DragViewPool registerView(ViewGroup superView, @NonNull List<DragRecyclerView> list) {
        mSuperView = superView;
        //注册touch监听
        for( DragRecyclerView rv : list ) {
            if( rv == null ) continue;
            Utils.setOnSuperTouchListener( rv, this );
            rv.setEnableDrag( isEnableViewDrag );
        }
        //添加到处理列表
        mViewList.addAll( list );
        return this;
    }

    public DragViewPool registerView(ViewGroup superView, @NonNull DragRecyclerView... rvs) {
        return registerView( superView, Arrays.asList( rvs ) );
    }

    /**
     * 设置Item拖拽。RecyclerView内的Item可以拖拽
     * @param enable        是否启用
     */
    public void setEnableViewDrag(boolean enable) {
        isEnableViewDrag = enable;
        if( mViewList.size() == 0 ) return;
        for (DragRecyclerView rv : mViewList) { rv.setEnableDrag( enable ); }
    }

    /**
     * 设置池拖拽。允许所有RecyclerView之间拖拽Item
     * @param enable        是否启用
     */
    public void setEnablePoolDrag(boolean enable) {
        isEnablePoolDrag = enable;
    }

    public void dispatchTouchEvent(MotionEvent ev) {
        TOUCH_XY[ 0 ] = ev.getX();
        TOUCH_XY[ 1 ] = ev.getY();
    }

    /**
     * 设置Item状态发生改变监听器
     * @param l     监听器
     */
    public void setOnItemStatusListener(OnItemStatusListener l) { mOnItemStatusListener = l; }

    /**
     * 获取Item状态发生改变监听器
     * @return      监听器
     */
    @NonNull
    public OnItemStatusListener getOnItemStatusListener() {
        if( mOnItemStatusListener == null ) throw new NullPointerException("not status listener.");
        return mOnItemStatusListener;
    }

    @Override
    public boolean onTouch(@NonNull View v, @NonNull MotionEvent ev) {
        DragRecyclerView fromView = (DragRecyclerView) v;

        if( fromView.isEnableDrag() ) {
            switch ( ev.getAction() ) {
                case MotionEvent.ACTION_MOVE:
                    //显示浮动控件
                    showFloatingView( fromView );
                    break;
                case MotionEvent.ACTION_UP:
                    //隐藏浮动控件
                    hideFloatingView();
                    break;
            }
        }

        //是否启用的池拖拽
        if( !isEnablePoolDrag ) return false;

        //查找拖动范围内的Rv列表
        DragRecyclerView rvToView = findToView();
        switch ( ev.getAction() ) {
            case MotionEvent.ACTION_MOVE:
//                //处理浮动的控件
//                showFloatingView( fromView );
                //离开放置区域（进入时的Rv列表）时
                if( rvDragAddView != null && dragToPosition >= 0 &&
                        !rvDragAddView.equals( rvToView ) ) {
                    BaseRecyclerViewAdapter adapter = rvDragAddView.getDragAdapter();
                    //移除之前隐藏的Item
                    adapter.removeItemData( dragToPosition );
                    //通知移除
                    adapter.notifyItemRemoved( dragToPosition );
                    rvDragAddView = null;
                    dragToPosition = -1;
                }

                if( rvToView == null ) break;

                getDragXY( rvToView, DRAW_XY );
                //目标Rv列表的Item下标
                toPosition = findChildViewPosition( rvToView, DRAW_XY[ 0 ], DRAW_XY[ 1 ] );

                if( toPosition < 0 || fromView.equals(rvToView) ) break;

                int dragFromPosition = fromView.getDragViewPosition();

                //进入放置区域（其他的Rv列表）时
                if( rvDragAddView == null && dragFromPosition >= 0 ) {
                    rvDragAddView = rvToView;
                    //获取源列表拖动的Item数据
                    IItemData dragFromData = fromView.getDragAdapter()
                            .getItemData( dragFromPosition );
                    //添加源Rv列表的Item
                    rvDragAddView.getDragAdapter().addItemData( toPosition, dragFromData );
                    //通过回调隐藏添加的这个item
                    changedToItemVisibility( toPosition, false, true );
                    dragToPosition = toPosition;
                }
                //目标Rv列表的Item跟随拖动位置
                if( fromPosition != -1 && fromPosition != toPosition ) {
                    if( rvDragAddView == null ) {
                        toPosition = fromPosition;
                    }else {
                        rvToView.move( fromPosition, toPosition );
                    }
                }
                //拖拽时取消
                fromView.cancelClickMove();
                fromPosition = toPosition;
                break;
            case MotionEvent.ACTION_UP:
                if( rvDragAddView != null && rvDragAddView.equals( rvToView ) ) {
                    int removePosition = fromView.getDragViewPosition( true );
                    //通过回调显示这个item
                    changedToItemVisibility( toPosition, true, false );
                    if( removePosition >= 0 ) {
                        fromView.getDragAdapter().removeItemData( removePosition );
                        fromView.getDragAdapter().notifyItemRemoved( removePosition );
                    }
                }
//                //隐藏浮动控件
//                hideFloatingView();
                rvDragAddView = null;
                dragToPosition = -1;
                fromPosition = -1;
                toPosition = -1;
                break;
        }
        return false;
    }

    private DragRecyclerView findToView() {
        float x = TOUCH_XY[ 0 ];
        float y = TOUCH_XY[ 1 ];
        float toX, toY;
        //查找当前移动位置下的View
        for( DragRecyclerView rv : mViewList ) {
            if( rv == null ) continue;
            rv.getLocationOnScreen( DRAW_XY );
            toX = DRAW_XY[ 0 ];
            toY = DRAW_XY[ 1 ];
            if( x >= toX && x <= toX + rv.getWidth() &&
                    y >= toY && y <= toY + rv.getHeight() ) {
                return rv;
            }
        }
        return null;
    }

    private void getDragXY(@NonNull View v, int[] xy) {
        v.getLocationOnScreen( xy );
        xy[ 0 ] = (int) TOUCH_XY[ 0 ] - xy[ 0 ];
        xy[ 1 ] = (int) TOUCH_XY[ 1 ] - xy[ 1 ];
    }

    /**
     * 改变item的显示状态
     */
    private void changedToItemVisibility(int position, boolean isShow, boolean isInsert) {
        getOnItemStatusListener().onChanged(
                rvDragAddView.getDragAdapter().getItemData( position ),
                isShow
        );
        if( isInsert ) {
            rvDragAddView.getDragAdapter().notifyItemInserted( position );
        }else {
            rvDragAddView.getDragAdapter().notifyItemChanged( position );
        }
    }

    /**
     * 显示浮动控件
     * @param fromView  拖拽的Rv列表
     */
    private void showFloatingView(DragRecyclerView fromView) {
        if( vTouchView == null ) {
            getDragXY( fromView, DRAW_XY );
            vTouchView = findChildView( fromView, DRAW_XY[ 0 ], DRAW_XY[ 1 ] );
            if( vTouchView == null ) return;
            vTouchView.setVisibility( GONE );
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    vTouchView.getWidth(),
                    vTouchView.getHeight()
            );
            if( ivFloatingView == null ) {
                ivFloatingView = new ImageView( fromView.getContext() );
                ivFloatingView.setLayoutParams( lp );
                mSuperView.addView( ivFloatingView );
            }
            //绘制按下的ItemView
            ivFloatingView.setImageBitmap( Utils.viewToBitmap( vTouchView ) );
            ivFloatingView.setVisibility( View.VISIBLE );
        }
        ivFloatingView.setX( TOUCH_XY[ 0 ] - vTouchView.getWidth() / 2F );
        ivFloatingView.setY( TOUCH_XY[ 1 ] - vTouchView.getHeight() / 2F );
    }

    /**
     * 隐藏浮动控件
     */
    private void hideFloatingView() {
        if( ivFloatingView != null ) ivFloatingView.setVisibility( GONE );
//        mSuperView.removeView( ivFloatingView );
        if( vTouchView != null ) {
            vTouchView.setVisibility( View.VISIBLE );
            vTouchView = null;
        }
    }

    /**
     * 通过xy坐标查找子View的下标
     * @param rv            查找的DragRecyclerView
     * @param x             x轴
     * @param y             y轴
     * @return              找不到返回-1
     */
    private int findChildViewPosition(@NonNull DragRecyclerView rv, float x, float y) {
        BaseViewHolder h = rv.getDragAdapter().getHolder( rv.findChildViewUnder( x, y ) );
        return h == null ? -1 : h.getAdapterPosition();
    }

    private View findChildView(@NonNull DragRecyclerView rv, float x, float y) {
        BaseViewHolder h = rv.getDragAdapter().getHolder( rv.findChildViewUnder( x, y ) );
        return h == null ? null : h.getItemView();
    }
}
