package com.gjjy.usercenterlib.mvp.presenter;

import androidx.annotation.NonNull;

import com.gjjy.usercenterlib.mvp.view.IntegralDetailsView;
import com.gjjy.usercenterlib.adapter.IntegralAdapter;
import com.gjjy.usercenterlib.adapter.IntegralDetailAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.time.DateTimeType;
import com.gjjy.basiclib.api.entity.IntegralDataChildEntity;
import com.gjjy.basiclib.mvp.model.ReqIntegralModel;
import com.gjjy.basiclib.mvp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class IntegralDetailsPresenter extends MvpPresenter<IntegralDetailsView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqIntegralModel mReqIntegralModel;
    private int mPage = 1;
    //积分类型，1获取，2消耗，默认为全部
    private int mIntegralType = 0;

    public IntegralDetailsPresenter(@NonNull IntegralDetailsView view) {
        super(view);
    }

    public void switchIntegralType(int position) {
        mIntegralType = position;
        mPage = 1;
    }

    public void updateIntegralType() { switchIntegralType( mIntegralType ); }

    public void queryIntegralList() {
        IntegralDetailsView call = getView();
        if( call != null ) call.onCallLoadingDialog( true );

        List<IntegralAdapter.ItemData> retList = new ArrayList<>();
        mReqIntegralModel.reqIntegralList( mPage, mIntegralType, data -> {
            long ts = System.currentTimeMillis();
            int curYear = getTimeInt( ts, DateTimeType.YEAR );
            int curMonth = getTimeInt( ts, DateTimeType.MONTH );
            int tmpCurYear = curYear;
            int beforeYear = 0, beforeMonth = 0;
            List<IntegralDetailAdapter.ItemData> detailList = new ArrayList<>();
            for( int i = 0; i < data.getData().size(); i++ ) {
                IntegralDataChildEntity entity = data.getData().get( i );
                int year = 0, month = 0;
                try {
                    year = getTimeInt( entity.getCreateTime(), DateTimeType.YEAR );
                    month = getTimeInt( entity.getCreateTime(), DateTimeType.MONTH );
                }catch(Exception e) {
                    e.printStackTrace();
                }

                if( beforeYear == 0 ) beforeYear = year;
                if( beforeMonth == 0 ) beforeMonth = month;

                if( detailList.size() > 0 && year != beforeYear && month != beforeMonth ) {
                    retList.add(
                            toIntegralItem( beforeYear, beforeMonth, new ArrayList<>( detailList ) )
                    );
                    detailList.clear();
                }

                /* 生成其他年度以及本年度未支出/收入的订单 */
                while( year - tmpCurYear <= -1 ) {
                    //因为只生成没有数据的年度，所以不包括有数据的月份，这里 - 1
                    int fromMonth = tmpCurYear == curYear ? curMonth - 1 : 12;
                    int toMonth = curYear == year ? month : 1;
                    for( int j = fromMonth; j >= toMonth; j-- ) {
                        retList.add( toIntegralItem( tmpCurYear, j, new ArrayList<>() ) );
                    }
                    tmpCurYear--;
                }

                /* 本年度有支出/收入的订单 */
                detailList.add( toIntegralDetailItem( entity ) );
                beforeYear = year;
                beforeMonth = month;
            }
            //当前月份的数据只会在下一个月份才会添加，所以这里填充最后一次数据
            if( detailList.size() > 0 ) {
                retList.add( toIntegralItem( beforeYear, beforeMonth, detailList ) );
            }
            viewCall( v -> {
                v.onCallIntegralList( retList, mPage > 1 );
                v.onCallLoadingDialog( false );
            } );
        });
    }

    public void nextIntegralList() {
        mPage++;
        queryIntegralList();
    }

    public void queryMonthTotalList(int year, int month, int pos) {
        mReqIntegralModel.reqMonthTotal( year, month, data -> viewCall( v ->
                v.onCallMonthTotal( data.getAddTotal(), data.getSubTotal(), pos ) )
        );

    }

    public void queryTotalCount() {
        mReqIntegralModel.reqTotal( false, count ->
                viewCall( v -> v.onCallTotalCount( count ) )
        );
    }

    private int getTimeInt(long ts, @DateTimeType String type) {
        return ObjUtils.parseInt( DateTime.parse( ts, type ) );
    }
    private int getTimeInt(String s, @DateTimeType String type) {
        return getTimeInt( DateTime.parse( s ), type );
    }

    private IntegralDetailAdapter.ItemData toIntegralDetailItem(IntegralDataChildEntity data) {
        IntegralDetailAdapter.ItemData item = new IntegralDetailAdapter.ItemData();
        item.setId( data.getId() );
        item.setUserId( data.getUserId() );
        item.setTitle( data.getTitle() );
        item.setType( data.getType() );
        item.setCount( data.getNum() );
        item.setTimestamp( DateTime.parse( data.getCreateTime() ) );
        return item;
    }

    private long getTime(int year, int month) {
        return DateTime.parse( String.format(
                "%s-%s-01 00:00:00",
                year,
                month < 10 ? ( "0" + month ) : month
        ));
    }

    private IntegralAdapter.ItemData toIntegralItem(int year, int month,
                                                    List<IntegralDetailAdapter.ItemData> list) {
        IntegralAdapter.ItemData item = new IntegralAdapter.ItemData();
        try {
            item.setTimestamp( getTime( year,month ) );
        }catch(Exception e) {
            e.printStackTrace();
        }
        item.setYear( year );
        item.setMonth( month );
        item.setList( list );
        return item;
    }
}