package com.gjjy.basiclib.widget.drag;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WaterfallLayoutManager extends RecyclerView.LayoutManager {
    private int mTotalRowHeight;
    private int mTotalWidth;
    private int mLineCount;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
    }
    
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //移除存在的所有View
        detachAndScrapAttachedViews(recycler);
        //RecyclerView实际宽度
        int totalWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        View view;
        RecyclerView.LayoutParams lp;
        int[] vSize = new int[ 2 ];//0:width, 1:height
        int vLeft, vTop, vRight;
        int margin = 0;
        int maxHeight = 0;
        int lineWidth = 0;
        int rowHeight = 0;
        int rowCount = 1;
        int lineRight = 0;
        boolean isMaxSingleLine;

        mTotalRowHeight = 0;
        mLineCount = 1;

        for (int i = 0; i < getItemCount(); i++) {
            int tbMargin;
            //加入当前View到列表中
            addView( view = recycler.getViewForPosition( i ) );
            lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            measuredViewSize( view, vSize );
            tbMargin = lp.topMargin + lp.bottomMargin;
            //Rect位置
            vLeft = lp.leftMargin;
            vRight = vSize[ 0 ] + vLeft;
            vTop = 0;
            //取当前行的最大高度的View
            if( maxHeight < vSize[ 1 ] ) maxHeight = vSize[ 1 ] + lp.topMargin + lp.bottomMargin;
            //取最大边距
            if( margin < tbMargin ) margin = tbMargin * 2;
            //当前行中，所有View的宽度和当前View宽度，是否超出总宽度
            isMaxSingleLine = mSingleLineMaxCount != -1 && i > 0 && i % mSingleLineMaxCount == 0;

            if( lineRight > totalWidth || isMaxSingleLine ) {
                //换行
                rowHeight += vTop + maxHeight;
                mLineCount++;
                lineWidth = 0;
                rowCount = 1;
                lineRight = 0;
            }else {
                //不换行
                vLeft += lineWidth;
                rowCount++;
            }
            if( lineWidth > 0 ) vLeft += lp.rightMargin * ( rowCount - 1 );
            vTop += lp.topMargin + rowHeight;
            //更新一次右边的位置
            vRight = vSize[ 0 ] + vLeft;
            //设置view的位置
            layoutDecorated( view, vLeft, vTop, vRight, vSize[ 1 ] + vTop );
            //追加每个view的宽度（包括了外边距）
            lineWidth += vRight - vLeft;
            lineRight += vSize [ 0 ] +
                    view.getPaddingLeft() + view.getPaddingRight() +
                    lp.leftMargin + lp.rightMargin;
//            lineWidth += vRight;
            mWidth = Math.max( mWidth, lineRight );
        }
        mTotalRowHeight = rowHeight + maxHeight + margin;
    }

//    @Override
//    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        //移除存在的所有View
//        detachAndScrapAttachedViews(recycler);
//        //RecyclerView实际宽度
//        int totalWidth = getWidth() - getPaddingLeft() - getPaddingRight();
//
//        View view;
//        RecyclerView.LayoutParams lp;
//        int[] vSize = new int[ 2 ];//0:width, 1:height
//        int vLeft, vTop, vRight;
////        int margin = 0;
//        int maxHeight = 0;
//        int rowCount = 1;
//        int lineWidth = 0;
//        boolean isMaxSingleLine;
//
//        vLeft = vTop = vRight = 0;
//        mTotalRowHeight = 0;
//        mLineCount = 1;
//        int previousRightMargin = 0;
//
//        for (int i = 0; i < getItemCount(); i++) {
////            int tbMargin;
//            //加入当前View到列表中
//            addView( view = recycler.getViewForPosition( i ) );
//            lp = (RecyclerView.LayoutParams) view.getLayoutParams();
//            measuredViewSize( view, vSize );
//            previousRightMargin = lp.rightMargin;
////            tbMargin = lp.topMargin + lp.bottomMargin;
//            //Rect位置
//////            vLeft = lp.leftMargin;
////            vRight = vSize[ 0 ];
////            vTop = 0;
//            //取当前行的最大高度的View
//            if( maxHeight < vSize[ 1 ] ) maxHeight = vSize[ 1 ];
//            //取最大边距
////            if( margin < tbMargin ) margin = tbMargin * 2;
//            //当前行中，所有View的宽度和当前View宽度，是否超出总宽度
//            isMaxSingleLine = mSingleLineMaxCount != -1 && i > 0 && i % mSingleLineMaxCount == 0;
//            if( lineWidth + vSize[ 0 ] > totalWidth || isMaxSingleLine ) {
//                //换行
//                mLineCount++;
//                lineWidth = 0;
//                vLeft = 0;
//                vTop += maxHeight;
//                rowCount = 0;
//            }else {
//                //不换行
//                vLeft += lineWidth;
//                rowCount++;
//            }
////            if( lineWidth > 0 ) vLeft += lp.rightMargin * ( rowCount - 1 );
//            //更新一次右边的位置
//            vRight = lineWidth + vSize[ 0 ];
//            //设置view的位置
//            layoutDecorated( view, vLeft, vTop, vRight, vTop + vSize[ 0 ] );
//            //追加每个view的宽度（包括了外边距）
////            lineWidth += vRight - vLeft;
//            lineWidth += vSize [ 0 ];
////            lineWidth += vRight;
//            mWidth = Math.max( mWidth, lineWidth );
//        }
//        mTotalRowHeight = vTop + maxHeight;
//    }
//

    private int mWidth;
    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler,
                          @NonNull RecyclerView.State state,
                          int widthSpec,
                          int heightSpec) {
//        super.onMeasure( recycler, state, widthSpec, heightSpec );
//        int width = RecyclerView.LayoutManager.chooseSize(
//                widthSpec,
//                getPaddingLeft() + getPaddingRight(), getMinimumWidth()
//        );
        int minHeight = getMinimumHeight();
        int width = View.MeasureSpec.getSize( widthSpec );
        //除了RecyclerView.LayoutParams.MATCH_PARENT
        if( View.MeasureSpec.getMode( widthSpec ) != View.MeasureSpec.EXACTLY ) {
            width = mWidth > 0 ? mWidth : width;
        }
        setMeasuredDimension( width, Math.max( mTotalRowHeight, minHeight ) );
    }

    private int mSingleLineMaxCount = -1;
    public void setSingleLineMaxCount(int maxCount) {
        mSingleLineMaxCount = maxCount;
    }
    public int getTotalRowHeight() { return mTotalRowHeight; }

    public int getLineCount() { return mLineCount; }

    private void measuredViewSize(View v, int[] size) {
        if( v == null ) return;
//        measureChildWithMargins( v, 0, 0 );
        v.measure( 0, 0 );
        size[ 0 ] = v.getMeasuredWidth() + v.getPaddingLeft() + v.getPaddingRight();
        size[ 1 ] =v.getMeasuredHeight() + v.getPaddingTop() + v.getPaddingBottom();
    }
}
