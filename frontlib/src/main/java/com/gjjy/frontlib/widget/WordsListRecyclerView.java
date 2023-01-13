package com.gjjy.frontlib.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class WordsListRecyclerView extends RecyclerView {
//    private int mMaxHeight = 0;
//    private boolean mDisallowIntercept = true;

    public WordsListRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public WordsListRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordsListRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
    }

//    private void init() {
//        addOnItemTouchListener(new OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
////                LayoutManager manager = rv.getLayoutManager();
////                if( manager == null ) return false;
////                //当前按下位置的View。
////                View v = rv.findChildViewUnder( e.getX(), e.getY() );
////                if( v == null ) return false;
////                ViewHolder childHolder = rv.getChildViewHolder( v );
////                View lastView = manager.getChildAt( manager.getChildCount() - 1 );
////
////                Log.e("TAG", "lastView -> " + childHolder + "  " + lastView);
////                if( childHolder instanceof WordsListHolder ) {
////
////
////                    ((ViewGroup)v).requestDisallowInterceptTouchEvent( true );
////
////                    ((WordsListHolder) childHolder).getChildList().requestDisallowInterceptTouchEvent( false );
////                }
//
//
////                LayoutManager lmMain = rv.getLayoutManager();
////                if( lmMain == null ) return false;
////                //当前按下位置的View。
////                View v = rv.findChildViewUnder( e.getX(), e.getY() );
////                View topView = findTopView( rv );
////                if( v == null || topView == null ) return false;
////                ViewHolder childHolder = rv.getChildViewHolder( v );
////
////                float topViewY = topView.getTop();
////
////                boolean isChildRv = findRv( rv ) instanceof RecyclerView;
////
////                Log.e("TAG", "vvv -> " + topViewY + " | " + " = " + isChildRv);
////
////                if( childHolder instanceof WordsListHolder && v instanceof ViewGroup ) {
////                    WordsListHolder h = (WordsListHolder) childHolder;
////                    WordsListRecyclerView rvChild = h.getChildList();
////                    LayoutManager lmChild = rv.getLayoutManager();
////                    if( lmChild == null ) return false;
////                    View lastView = lmChild.getChildAt( lmChild.getChildCount() - 1 );
////
////                    if( lastView == null ) return false;
////
////                    //最后一个View是否到达最底部
////                    boolean isBottom = lastView.getBottom() == rvChild.getHeight();
////                    //最后一个View的下标是否与数据源数量一致
////                    boolean isLastPosition = lmChild.getPosition( lastView ) == lmChild.getItemCount() - 1;
////
////                    Log.e("TAG", "isBottom -> " + v + " | " + ( lmChild.getItemCount() - 1 ) + " | isLastPosition -> " + isLastPosition);
////                    //滑动到底部时
//////                    mDisallowIntercept = !( isBottom && isLastPosition );
////
////                    boolean disallowIntercept = ( isBottom && isLastPosition );
////
////                    if( disallowIntercept ) {
////                        findRv( rv ).requestDisallowInterceptTouchEvent( true );
////                    }else {
////                        //请求不允许拦截触摸事件
////                        ((ViewGroup)v).requestDisallowInterceptTouchEvent( topViewY > 0 );
////                    }
////
////
////                }
//                return false;
//            }
//            @Override
//            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
//        });
//    }

//    @Nullable
//    private View findTopView(RecyclerView rv) {
//        LayoutManager lm = rv.getLayoutManager();
//        if( lm == null ) return null;
//        View v;
//        int index = 0;
//        while ( index < lm.getItemCount() - 1 ) {
//            v = lm.getChildAt( index++ );
//            if( v != null && v.getTop() >= rv.getHeight() / 2 ) return v;
//        }
//        return null;
//    }
//
//    private ViewGroup findRv(View v) {
//        return (ViewGroup) v.getParent().getParent();
//    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
//        if( mMaxHeight > 0 ) {
//            heightSpec = MeasureSpec.makeMeasureSpec( mMaxHeight, MeasureSpec.AT_MOST );
//        }
        super.onMeasure(widthSpec, heightSpec);
    }

//    public void setMaxHeight(int height) { this.mMaxHeight = height; }
}
