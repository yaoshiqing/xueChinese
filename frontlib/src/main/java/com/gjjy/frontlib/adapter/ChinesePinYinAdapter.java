package com.gjjy.frontlib.adapter;

import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.adapter.holder.VoiceCenterHolder;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybutils.utils.ObjUtils;
import com.gjjy.frontlib.R;
import com.gjjy.speechsdk.evaluator.parser.entity.Syll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 汉字拼音列表适配器
 */
public class ChinesePinYinAdapter extends BaseRecyclerViewAdapter<ChinesePinYinAdapter.ItemData, VoiceCenterHolder> {
    private String mPinYin, mChinese;
    private int mTextGravity;

    public ChinesePinYinAdapter(@NonNull List<ItemData> list) {
        super(list);
        switchTextGravityOfDefault();
    }

    @NonNull
    @Override
    public VoiceCenterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VoiceCenterHolder( parent, R.layout.item_chinese_pinyin );
    }

    @Override
    public void onBindViewHolder(@NonNull VoiceCenterHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        TextView tvPinYin = holder.getPinYin();
        TextView tvText = holder.getText();

        String pinyin = data.getPinyin();
        String text = data.getText();

        int[] colors = new int[ data.getStatus().length ];
        Resources res = holder.getItemView().getResources();

        for (int i = 0; i < data.getStatus().length; i++) {
            switch ( data.getStatus()[ i ] ) {
                case Status.CORRECT:case Status.TOUCH:    //正确，按下
                    colors[ i ] = res.getColor( R.color.colorMain );
                    break;
                case Status.ERROR:      //错误
                    colors[ i ] = res.getColor( R.color.colorError );
                    break;
                default:
                    colors[ i ] = res.getColor( R.color.color66 );
                    break;
            }
        }
        if( colors.length > 0 ) {
            tvPinYin.setText( Html.fromHtml( getPinYinText( pinyin, colors ) ) );
            tvText.setText( Html.fromHtml( getText( text, colors ) ) );
        }

        if( mEnablePinYinZoom && data.isEnablePinYin() && !data.isEnableText() ) {
            tvPinYin.setTextSize(
                    isPinYinLetter(  data.getPinyin() ) ? 34 : 17
            );
        }

        tvPinYin.setGravity( mTextGravity );
        tvText.setGravity( mTextGravity );

        tvPinYin.setVisibility( data.isEnablePinYin() && pinyin != null ? View.VISIBLE : View.GONE );
        tvText.setVisibility( data.isEnableText() && text != null ? View.VISIBLE : View.GONE );
    }

    private int getColor(int[] colors, int index) {
        index = index >= colors.length ? colors.length -1 : index;
        return colors.length == 1 ? colors[ 0 ] : colors[ index ];
    }

    private boolean isPinYinLetter(String s) {
        String letter =
                "ā á ǎ à ō ó ǒ ò ê ē é ě è ī í ǐ ì ū ú ǔ ù ǖ ǘ ǚ ǜ ü ê ɑ \uE7C7 ń ň \uE7C8 ɡ " +
                "b p m f d t n l g k h j q x zh ch sh r z c s y w " +
                "a o e i u ü " +
                "ai ei ui ao ou iu ie üe er an en in un ang eng ing ong " +
                "zhi chi shi ri zi ci si yi wu yu ye yue yuan yin yun ying";
        for( String let : letter.split(" ") ) {
            if( s != null && s.toLowerCase().contains( let ) ) return true;
        }
        return false;
    }

    private final String mFontHtml = "<font color='%s'>%s</font>";

    private String getPinYinText(String s, int[] colors) {
        if( TextUtils.isEmpty( s ) ) return "";
        StringBuilder sb = new StringBuilder();
        String[] pys = s.split(" ");
        for (int i = 0; i < pys.length; i++) {
            sb.append(String.format(mFontHtml, getColor( colors, i ), pys[ i ]));
            sb.append(" ");
        }
        return sb.toString();
    }
    private String getText(String s, int[] colors) {
        if( TextUtils.isEmpty( s ) ) return "";
        StringBuilder sb = new StringBuilder();
        int index = -1;
        int colorIndex = 0;
        for (int i = 0; i < s.length(); i++) {
            char chr = s.charAt( i );
            boolean isCC = ObjUtils.isChineseChar( Character.toString( chr ) );
            if( isCC ) {
                if( index != -1 ) {
                    //字母
                    sb.append(String.format(mFontHtml,
                            getColor( colors, colorIndex++ ),
                            s.substring( index, i )
                    ));
                    index = -1;
                }
                //汉字
                sb.append(String.format(
                        mFontHtml,
                        getColor( colors, colorIndex++ ),
                        Character.toString( chr )
                ));
            }else {
                if( index == -1 ) index = i;

                if( i == s.length() - 1 ) {
                    sb.append(String.format(
                            mFontHtml,
                            getColor( colors, colorIndex ),
                            s.substring( index, i + 1 )
                    ));
                }
            }
        }
        return sb.toString();
    }

    public void setChinesePinYin(String[] pinyin, boolean enablePinyin,
                                 String[] text, boolean enableText) {
        StringBuilder sbPinyin = new StringBuilder();
        StringBuilder sbText = new StringBuilder();

        clearItemData();

        if( pinyin == null || pinyin.length == 0 && text != null && text.length > 0 ) {
            for( String s : text ) {
                ChinesePinYinAdapter.ItemData itemData = new ChinesePinYinAdapter.ItemData();
                itemData.setText( s );
                addItemData( itemData );
            }
        }else {
            for (int i = 0; i < pinyin.length; i++) {
                ChinesePinYinAdapter.ItemData itemData = new ChinesePinYinAdapter.ItemData();
                //拼音
                if( enablePinyin ) itemData.setPinyin( pinyin[ i ] );
                sbPinyin.append( itemData.getPinyin() ).append(" ");
                //汉字
                if( enableText && i < text.length ) {
                    itemData.setText( text[ i ] );
                    sbText.append( itemData.getText() );
                }
                addItemData( itemData );
            }
        }

        mPinYin = sbPinyin.toString();
        mChinese = sbText.toString();
        notifyDataSetChanged();
    }

    public void setChinesePinYin(String[] pinyin, String[] text) {
        setChinesePinYin( pinyin, true, text, true );
    }

    public String getPinYin() { return mPinYin; }

    public String getChinese() { return mChinese; }

    public void switchStatus(List<Syll> statusList) {
        int j = 0;
        for( ItemData data : getDataList() ) {
            List<Integer> status = new ArrayList<>();
            String s = data.getText();

            for( int i = 0; i < s.length(); i++ ) {
                String chrAt = String.valueOf( s.charAt( i ) );
                Syll syll = j >= statusList.size() ? null : statusList.get( j );
                if( syll == null ) continue;
                if( !ObjUtils.isChineseChar( chrAt ) ) continue;
                if( !syll.getContent().equals( chrAt ) ) continue;
                status.add( syll.getDpMessage() == 0 ? Status.CORRECT : Status.ERROR );

                j++;
            }

            data.setStatus( status.toArray( new Integer[ 0 ] ) );
        }
        notifyDataSetChanged();

    }

    public void switchNormalStatus() {
        for (ItemData data : getDataList()) data.setStatus( Status.NORMAL );
        notifyDataSetChanged();
    }

    public void switchTouchStatus() {
        for( ItemData data : getDataList() ) {
            data.setStatus( Status.TOUCH );
        }
        notifyDataSetChanged();
    }

    public void switchCorrectStatus() {
        for (ItemData data : getDataList()) data.setStatus( Status.CORRECT );
        notifyDataSetChanged();
    }

    public void switchErrorStatus() {
        for (ItemData data : getDataList()) data.setStatus( Status.ERROR );
        notifyDataSetChanged();
//        notifyDataSetChangedSuper();
    }

    public void switchTextGravityOfDefault() {
        mTextGravity = Gravity.CENTER_HORIZONTAL;
//        for (ItemData data : getDataList()) data.setTextGravityOfDefault();
//        notifyDataSetChangedSuper();
    }

    public void switchTextGravityOfLeft() {
        mTextGravity = Gravity.CENTER_VERTICAL | Gravity.START;
//        for (ItemData data : getDataList()) data.setTextGravityOfLeft();
//        notifyDataSetChangedSuper();
    }

    private boolean mEnablePinYinZoom = false;
    public void setEnablePinYinZoom(boolean enable) {
        mEnablePinYinZoom = enable;
    }

//    private void notifyDataSetChangedSuper() { mHandler.post(this::notifyDataSetChanged); }

    public @interface Status {
        int NONE = 0;       //无状态
        int NORMAL = 1;     //正常
        int CORRECT = 2;    //回答正确
        int TOUCH = 3;      //按下
        int ERROR = 4;      //回答错误
    }

    public static class ItemData implements IItemData {
        private String pinyin;
        private String text;
        private String fullText;
        private boolean enablePinYin = true;
        private boolean enableText = true;
        private int textGravity = Gravity.CENTER_HORIZONTAL;
        @Status
        private Integer[] status = new Integer[] { Status.NORMAL };

        public ItemData() {}

        public ItemData(@NonNull ItemData itemData) {
            pinyin = itemData.pinyin;
            text = itemData.text;
            fullText = itemData.fullText;
            enablePinYin = itemData.enablePinYin;
            enableText = itemData.enableText;
            status = itemData.status;
            textGravity = itemData.textGravity;
        }

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "pinyin='" + pinyin + '\'' +
                    ", text='" + text + '\'' +
                    ", fullText='" + fullText + '\'' +
                    ", enablePinYin=" + enablePinYin +
                    ", enableText=" + enableText +
                    ", status=" + Arrays.toString( status ) +
                    ", textGravity=" + textGravity +
                    '}';
        }


        public String getFullText() { return fullText; }
        void setFullText(String fullText) { this.fullText = fullText; }

        public String getPinyin() { return pinyin; }
        public void setPinyin(String pinyin) { this.pinyin = pinyin; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public void setText(char text) { this.text = String.valueOf( text ); }

        public boolean isEnablePinYin() { return enablePinYin; }
        public void setEnablePinYin(boolean enable) { enablePinYin = enable; }

        public boolean isEnableText() { return enableText; }
        public void setEnableText(boolean enable) { enableText = enable; }

        public Integer[] getStatus() { return status; }
        public void setStatus(Integer[] status) { this.status = status; }
        public void setStatus(Integer status) { this.status = new Integer[] { status }; }

        public int getTextGravity() { return textGravity; }

        public ItemData setTextGravity(int textGravity) {
            this.textGravity = textGravity;
            return this;
        }

        public ItemData setTextGravityOfDefault() {
            this.textGravity = Gravity.CENTER_HORIZONTAL;
            return this;
        }

        public ItemData setTextGravityOfLeft() {
            this.textGravity = Gravity.CENTER_VERTICAL | Gravity.START;
            return this;
        }
    }
}
