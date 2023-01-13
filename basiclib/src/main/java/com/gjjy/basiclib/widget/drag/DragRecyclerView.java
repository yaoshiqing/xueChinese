package com.gjjy.basiclib.widget.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.base.adapter.IItemData;

import java.util.Collections;
import java.util.List;

public class DragRecyclerView extends RecyclerView {
    public interface OnDoBindViewListener {
        boolean doBindView(IItemData data, int position);
    }
    private ItemTouchHelper mItemTouchHelper;
    private BaseRecyclerViewAdapter<? super IItemData, ? extends BaseViewHolder> mAdapter;

    private DragRecyclerView mBindView;
//    private WaterfallLayoutManager mLayoutManager;
    private FlowLayoutManager mLayoutManager;

    private int mMinLineCount = 0;
    private boolean isEnableClickMove = true;
    private boolean isEnableDrag = true;
    private int fromPosition = -1, toPosition = -1, oldToPosition = -1;

    private OnDoBindViewListener mOnDoBindViewListener;

    private Paint mPaint = new Paint();
    
    public DragRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public DragRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int lineCount = getLineCount();
        if( lineCount <= 0 ) return;

        int y = getMinimumHeight() / 2;
        //去掉线的大小
        y -= mPaint.getStrokeWidth();
        for (int i = 0; i < lineCount; i++) {
            float drawY = y + ( y * i );
            canvas.drawLine( 0, drawY, getWidth(), drawY, mPaint );
        }
    }

//    private void drawBg(Canvas canvas) {
//        if( isDraw ) return;
//        isDraw = true;
//        for (int i = 0; i < mLayoutManager.getItemCount(); i++) {
//            View v = mLayoutManager.getChildAt( i );
//            if( v == null ) continue;
//            float vX = v.getX();
//            float vY = v.getY();
//            mBgLocation.set( vX, vY,  vX + v.getWidth(), vY + v.getHeight() );
//            mBgNinePatch.draw( canvas, mBgLocation );
//        }
//    }

    public void setMinLineCount(int count) {
        mMinLineCount = count;
        postInvalidate();
    }

    private int getLineCount() {
//        int count = mLayoutManager.getLineCount();
        int count = 2;
        return mMinLineCount == 0 || mMinLineCount > count ? mMinLineCount : count;
//        if( mMinLineCount >= 0 ) return mMinLineCount;
//        double itemCount = (double) mLayoutManager.getItemCount();
//        double spanCount = (double) mSpanCount;
//        return (int) Math.ceil( itemCount / spanCount );
    }

//    private int mSpanCount = 4;
//    public void setSpanCount(int count) {
//        mSpanCount = count;
//        mLayoutManager.requestLayout();
//        Adapter adapter = getAdapter();
//        if( adapter == null ) return;
//        adapter.notifyDataSetChanged();
//    }

    public void setOnDoBindViewListener(OnDoBindViewListener l) { mOnDoBindViewListener = l; }

    private void init() {
        mLayoutManager = new FlowLayoutManager( );
//        mLayoutManager = new WaterfallLayoutManager( getContext(), mSpanCount );
//        mLayoutManager = new StaggeredGridLayoutManager( mSpanCount, StaggeredGridLayoutManager.VERTICAL );
        setLayoutManager( mLayoutManager );
        setOverScrollMode( OVER_SCROLL_NEVER );

        mPaint.setColor( Color.parseColor("#C7C6CB") );
        mPaint.setStrokeWidth( 2 );

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        mItemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback( dragFlags, 0 ) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView rv, @NonNull ViewHolder holder,
                                          @NonNull ViewHolder target) {
                        if( mAdapter == null || !isEnableDrag ) return true;
                        fromPosition = holder.getLayoutPosition();
                        toPosition = target.getLayoutPosition();
                        move( fromPosition, toPosition );
                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull ViewHolder viewHolder, int direction) { }

                    @Override
                    public boolean isLongPressDragEnabled() { return false; }
                });

        mItemTouchHelper.attachToRecyclerView( this );
    }

    private long mDownTime;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if( mAdapter == null ) return super.dispatchTouchEvent(ev);
        ViewHolder holder;
        switch ( ev.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                holder = mAdapter.getHolder( findChildViewUnder(ev.getX(), ev.getY()) );
                if( holder != null ) {
                    if( isEnableDrag ) mItemTouchHelper.startDrag( holder );
                    if( toPosition == -1 ) toPosition = holder.getLayoutPosition();
                    oldToPosition = toPosition;
                }
                break;
            case MotionEvent.ACTION_UP:
                holder = mAdapter.getHolder( findChildViewUnder(ev.getX(), ev.getY()) );
                boolean isMove = ( System.currentTimeMillis() - mDownTime ) >= 120;
                //点击item时移动到绑定的列表中
                if( isEnableClickMove && !isMove && isBindView( holder ) ) {
                    doClickMove( toPosition );
                }
                toPosition = -1;

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isBindView(ViewHolder holder) {
        if( holder == null ) return false;
        int position = holder.getAdapterPosition();
        return mOnDoBindViewListener == null || mOnDoBindViewListener.doBindView(
                mAdapter.getItemData( position ), position
        );
    }

    public void setEnableClickMove(boolean enable) { isEnableClickMove = enable; }
    public boolean isEnableClickMove() { return isEnableClickMove; }

    public void bindMoveView(DragRecyclerView rv) { mBindView = rv; }

    public void setEnableDrag(boolean enable) {
        isEnableDrag = enable;
    }
    public boolean isEnableDrag() { return isEnableDrag; }

    int getDragViewPosition(boolean isKeep) {
        return isKeep && toPosition == -1 ? oldToPosition : toPosition;
    }

    int getDragViewPosition() { return getDragViewPosition( false ); }

    DragRecyclerView getBindView() { return mBindView; }

    void move(int from, int to) {
        if( from < 0 || to < 0 ) return;
        List<?> list = mAdapter.getDataList();
        //数据排序
        if( from < to ) {
            for(int i = from; i < to; i++) Collections.swap( list, i, i + 1 );
        }else {
            for(int i = from; i > to; i--) Collections.swap( list, i, i - 1 );
        }
        //更新排序
        mAdapter.notifyItemMoved( from, to );
    }

//    public <T extends BaseRecyclerViewAdapter<? super IItemData, ? super BaseViewHolder>>
//    void setAdapter(T adapter) {
//        super.setAdapter( adapter );
//        mAdapter = adapter;
//    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter( adapter );
//        setAdapter( ((BaseRecyclerViewAdapter)adapter) );
        mAdapter = (BaseRecyclerViewAdapter<? super IItemData, ? extends BaseViewHolder>) adapter;
    }

    @Nullable
    @Override
    public Adapter getAdapter() {
        return super.getAdapter();
    }

    @NonNull
    public BaseRecyclerViewAdapter<? super IItemData, ? extends BaseViewHolder> getDragAdapter() {
        return mAdapter;
    }

    private boolean isCancelMove = false;
    void cancelClickMove() { isCancelMove = true; }

    private void doClickMove(int position) {
        if( isCancelMove ) {
            isCancelMove = false;
            return;
        }
        if( mAdapter == null || mBindView == null || position < 0 ) return;

        IItemData data = mAdapter.removeItemData( position );
        mAdapter.notifyItemRemoved( position );
        mAdapter.notifyDataSetChanged();

        mBindView.getDragAdapter().addItemData( data );
        mBindView.getDragAdapter().notifyItemInserted(
                mBindView.getDragAdapter().getItemCount() - 1
        );
    }
}
