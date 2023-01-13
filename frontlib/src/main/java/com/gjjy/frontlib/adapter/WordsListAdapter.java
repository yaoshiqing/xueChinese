package com.gjjy.frontlib.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.adapter.holder.WordsListHolder;
import com.gjjy.frontlib.entity.BaseEntity;
import com.gjjy.frontlib.widget.WordsListRecyclerView;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;

import java.util.List;

/**
 * 单词表列表适配器
 */
public class WordsListAdapter extends BaseRecyclerViewAdapter<WordsListAdapter.ItemData, WordsListHolder> {
    private final RequestManager mGlide;
    private boolean isHideItemAll = false;
    private boolean isEnablePinYin = true;
    private boolean isEnableExplain = true;
    private int mCurrentPosition;

    public WordsListAdapter(@NonNull RequestManager glide, @NonNull List<ItemData> list) {
        super(list);
        mGlide = glide;
        setEnableTouchStyle( false );
    }

    @NonNull
    @Override
    public WordsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WordsListHolder( parent, R.layout.item_words_list );
    }

    @Override
    public void onBindViewHolder(@NonNull WordsListHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        holder.getTitle().setText( data.getTitle() );

        if( data.getIconUrl() != null ) {
            mGlide.load( data.getIconUrl() ).into( holder.getImg() );
        }else {
            holder.getImg().setImageResource( data.getLocalIcon() );
        }

        WordsListRecyclerView rvChild = holder.getChildList();
        WordsListChildAdapter childAdapter = new WordsListChildAdapter(
                getContext( holder ), data.getChildList()
        );
        childAdapter.setEnablePinYin( isEnablePinYin );
        childAdapter.setEnableExplain( isEnableExplain );

        rvChild.setLayoutManager( new LinearLayoutManager( holder.getContext() ) );
        rvChild.setAdapter( childAdapter );

        rvChild.setVisibility( data.isShow() ? View.VISIBLE : View.GONE );
        holder.getDiv().setVisibility( data.isShow() ? View.VISIBLE : View.GONE );
        if( data.isShow() ) {
            holder.switchCloseIcon();
        }else {
            holder.switchOpenIcon();
        }
        if( isHideItemAll ) {
            holder.getChildList().setVisibility( View.GONE );
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter<WordsListHolder> adapter,
                            View v, ItemData data, int position) {
        showItem( position );
        isHideItemAll = false;
        mCurrentPosition = position;
        super.onItemClick(adapter, v, data, position);
    }

    private boolean isItemShowing;

    public void showItem(int position) {
        WordsListHolder holder = getHolder( position );
        if( holder == null ) return;
        int showType = 0;
        for( int i = 0; i < getItemCount(); i++ ) {
            ItemData data = getItemData( i );
            if( data == null ) continue;
            if( position == i ) {
                showType = data.isShow() ? 1 : 0;
                isItemShowing = showType == 0;
            }
            data.setShow( position == i && showType == 0 );
        }
        notifyItemRangeChanged( 0, getItemCount() );
//        notifyDataSetChanged();
//        return showItem( holder, showType );
    }

//    private boolean showItem(WordsListHolder holder, int showType) {
//        if( holder == null ) return false;
//        RecyclerView rv = holder.getChildList();
//        switch ( showType ) {
//            case 0:
//                holder.switchCloseIcon();
//                AnimUtil.loadAnimForToBottom( holder.getContext(), rv, AnimUtil.SHOW_STATUS.VISIBLE );
//                holder.getDiv().setVisibility( View.VISIBLE );
//                break;
//            case 1:
//                holder.switchOpenIcon();
//                AnimUtil.loadAnimForToTop( holder.getContext(), rv, AnimUtil.SHOW_STATUS.GONE );
//                holder.getDiv().setVisibility( View.GONE );
//                break;
//        }
//        isItemShowing = showType == 0;
//        notifyDataSetChanged();
//        return showType == 1;
//    }

    public boolean isItemShowing() {
        return isItemShowing;
    }

    public void hideItemAll() {
        isHideItemAll = true;
        notifyDataSetChanged();
    }

    public WordsListChildAdapter getCurrentChildAdapter() {
        return getChildAdapter( mCurrentPosition );
    }

    public WordsListChildAdapter getChildAdapter(int position) {
        WordsListHolder h = getHolder( position );
        if( h == null ) return null;
        return (WordsListChildAdapter) h.getChildList().getAdapter();
    }

    public void setEnablePinYin(boolean enable) {
        isEnablePinYin = enable;
//        notifyItemRangeChanged( 0, getItemCount() );
        notifyDataSetChanged();
//        setEnableChildItem( itemData -> itemData.setEnablePinYin( enable ) );
    }

    public void setEnableExplain(boolean enable) {
        isEnableExplain = enable;
//        notifyItemRangeChanged( 0, getItemCount() );
        notifyDataSetChanged();
//        setEnableChildItem( itemData -> itemData.setEnableExplain( enable ) );
    }

//    private void setEnableChildItem(Consumer<WordsListChildAdapter.ItemData> call) {
//        for( ItemData item : getDataList() ) {
//            for( WordsListChildAdapter.ItemData cItem : item.getChildList() ) {
//                if( call != null ) call.accept( cItem );
//            }
//        }
//        notifyDataSetChanged();
////        WordsListChildAdapter adapter = getCurrentChildAdapter();
////        if( adapter != null ) adapter.notifyDataSetChanged();
//    }

    public static class ItemData extends BaseEntity implements IItemData {
        private String title;
        private String iconUrl;
        private int localIcon;
        private List<WordsListChildAdapter.ItemData> childList;
        private boolean isShow;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "title='" + title + '\'' +
                    ", iconUrl='" + iconUrl + '\'' +
                    ", localIcon=" + localIcon +
                    ", childList=" + childList +
                    ", isShow=" + isShow +
                    '}';
        }

        public String getTitle() { return title; }
        public ItemData setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getIconUrl() { return iconUrl; }
        public ItemData setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }

        public int getLocalIcon() { return localIcon; }
        public ItemData setLocalIcon(int localIcon) {
            this.localIcon = localIcon;
            return this;
        }

        public List<WordsListChildAdapter.ItemData> getChildList() { return childList; }
        public ItemData setChildList(List<WordsListChildAdapter.ItemData> childList) {
            this.childList = childList;
            return this;
        }

        public boolean isShow() {
            return isShow;
        }
        public void setShow(boolean show) {
            isShow = show;
        }

        public boolean isHaveChildData() { return childList != null && childList.size() > 0; }
    }
}
