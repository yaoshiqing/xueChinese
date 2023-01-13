package com.gjjy.basiclib.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybcomponent.base.adapter.listener.OnItemClickListener;
import com.ybear.ybcomponent.widget.shape.ShapeImageView;
import com.ybear.ybutils.utils.Utils;
import com.gjjy.basiclib.R;
import com.gjjy.basiclib.utils.StartUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加图片View
 */
public class CollectImagesView extends RecyclerView {
    private ImageAdapter mAdapter;

    private int mRequestCode = 888;
    private int mMaxCollectCount = 3;
    private boolean isMaxCount;

    private Consumer<Integer> mOnImageChangedListener;

    public CollectImagesView(@NonNull Context context) {
        this(context, null);
    }

    public CollectImagesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectImagesView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayoutManager( new LinearLayoutManager( getContext(), HORIZONTAL, false ) );

        initData();
        initListener();
    }

    private void initData() {
        mAdapter = new ImageAdapter( new ArrayList<>() );
        setAdapter( mAdapter );
        //增加一个图片位置
        addImage();
    }

    private void initListener() {
        //Item点击事件监听器
        mAdapter.setOnItemClickListener((adapter, view, data, position) -> {
            if( data.getImgUri() == null ) {
                //打开相册
                StartUtil.startDCIM( getContext(), mRequestCode );
            }else {
                //打开图片
                StartUtil.startPictureViewerActivity( getContext(), data.getImgUri() );
            }
        });

        //删除按钮点击事件监听器
        mAdapter.setOnDeleteClickListener((adapter, view, data, position) -> {
            //增加一个图片位置
            if( isMaxCount ) {
                isMaxCount = false;
                addImage();
            }

            mAdapter.removeItemData( position );
            mAdapter.notifyItemRemoved( position );
            doImageChangedListener();
        });
    }

    public void setMaxCollectCount(int count) { mMaxCollectCount = count; }
    public int getMaxCollectCount() { return mMaxCollectCount; }


    public void setRequestCode(int requestCode) { mRequestCode = requestCode; }

    public List<Uri> getDataList() {
        List<Uri> retList = new ArrayList<>();
        List<ImageAdapter.ItemData> list = mAdapter.getDataList();
        for( ImageAdapter.ItemData data : list ) retList.add( data.getImgUri() );
        return retList;
    }

    public Consumer<Integer> getOnImageChangedListener() { return mOnImageChangedListener; }
    public void setOnImageChangedListener(Consumer<Integer> l) { mOnImageChangedListener = l; }

    /**
     * 获取所有图片的{@link Uri}
     * @return      返回结果
     */
    public List<Uri> getImageUriAll() {
        List<Uri> list = new ArrayList<>();
        for( ImageAdapter.ItemData data : mAdapter.getDataList() ) {
            list.add( data.getImgUri() );
        }
        return list;
    }

    /**
     * 获取所有图片的{@link File}
     * @return      返回结果
     */
    public List<File> getImageFileAll() {
        List<File> list = new ArrayList<>();
        for( ImageAdapter.ItemData data : mAdapter.getDataList() ) {
            if( data.getImgUri() == null ) continue;
            list.add( Utils.uriToFile( getContext(), data.getImgUri() ) );
        }
        return list;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode != mRequestCode || resultCode != Activity.RESULT_OK ) return;
        if( data == null || data.getData() == null ) return;
        Uri uri = data.getData();
        //设置在相册中选择的图片
        if( uri != null ) addImage( uri );
    }

    private void addImage(Uri uri) {
        int position = mAdapter.getItemCount() - 1;
        position = position < 0 ? 0 : position;
        mAdapter.addItemData( position, new ImageAdapter.ItemData( uri ) );
        mAdapter.notifyItemInserted( position );

        //到达上限时移除添加按钮
        if( mAdapter.getItemCount() >= mMaxCollectCount + 1 ) {
            isMaxCount = true;
            position = mAdapter.getItemCount() - 1;
            mAdapter.removeItemData( position );
            mAdapter.notifyItemRemoved( position );
        }
        doImageChangedListener();
    }

    private void addImage() {
        mAdapter.addItemData( new ImageAdapter.ItemData( R.drawable.ic_collect_images_add ) );
        mAdapter.notifyItemInserted( mAdapter.getItemCount() - 1 );
    }

    private void doImageChangedListener() {
        if( mOnImageChangedListener == null ) return;
        mOnImageChangedListener.accept(
                isMaxCount ? 0 : mMaxCollectCount - mAdapter.getItemCount() + 1
        );
    }

    public static class ImageAdapter
            extends BaseRecyclerViewAdapter<ImageAdapter.ItemData, ImageAdapter.ImageHolder> {

        private OnItemClickListener<ItemData, ImageHolder> mOnDeleteClickListener;

        public ImageAdapter(@NonNull List<ItemData> mDataList) { super(mDataList); }

        @NonNull
        @Override
        public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ImageHolder( parent, R.layout.item_collect_image );
        }

        @Override
        public void onBindViewHolder(@NonNull ImageHolder h, int position) {
            super.onBindViewHolder( h, position );
            ItemData data = getItemData( position );
            if( data == null ) return;

            ShapeImageView sivImg = h.getImg();
            ImageView ivDelBtn = h.getDelBtn();
            if( data.getImgUri() != null ) {
                /* 图片 */
                //缓存图
                Bitmap thumb = Utils.getThumbnail( h.getContext(), data.getImgUri() );
                if( thumb != null ) sivImg.setImageBitmap( thumb );
                sivImg.setScaleType( ImageView.ScaleType.CENTER_CROP );
                sivImg.setBorderSize( 0 );
                ivDelBtn.setVisibility( VISIBLE );
            }else if( data.getImgResId() != -1 ) {
                /* 添加按钮 */
                sivImg.setScaleType( ImageView.ScaleType.CENTER );
                sivImg.setImageResource( data.getImgResId() );
                sivImg.setBorderSize( Utils.dp2Px( h.getContext(), 2 ) );
                sivImg.setBorderColor( h.getContext().getResources().getColor( R.color.colorEF ) );
                ivDelBtn.setVisibility( GONE );
            }
            //删除按钮点击事件监听器
            ivDelBtn.setOnClickListener(v -> {
                if( mOnDeleteClickListener == null ) return;
                mOnDeleteClickListener.onItemClick( this, v, data, h.getAdapterPosition() );
            });
        }

        /**
         * 设置删除按钮点击事件监听器
         * @param l     监听器
         */
        public void setOnDeleteClickListener(OnItemClickListener<ItemData, ImageHolder> l) {
            mOnDeleteClickListener = l;
        }

        /**
         * Holder
         */
        class ImageHolder extends BaseViewHolder {
            private ShapeImageView sivImg;
            private ImageView ivDelBtn;

            ImageHolder(@NonNull ViewGroup parent, int itemRes) {
                super(parent, itemRes);
                View v = getItemView();
                sivImg = v.findViewById( R.id.item_collect_image_siv_img );
                ivDelBtn = v.findViewById( R.id.item_collect_image_iv_delete_btn );
            }

            ShapeImageView getImg() { return sivImg; }

            public ImageView getDelBtn() { return ivDelBtn; }
        }

        /**
         * 数据源
         */
        public static class ItemData implements IItemData {
            private Uri imgUri = null;
            @DrawableRes
            private int resIdImg = -1;

            public ItemData() {}
            public ItemData(Uri uri) { imgUri = uri; }
            public ItemData(@DrawableRes int resId) { resIdImg = resId; }

            public Uri getImgUri() { return imgUri; }
            public int getImgResId() { return resIdImg; }

            public void setImgUri(Uri imgUri) { this.imgUri = imgUri; }
            public void setImg(@DrawableRes int resId) { resIdImg = resId; }
        }
    }
}
