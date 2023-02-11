package com.gjjy.basiclib.mvp.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.gjjy.basiclib.R;
import com.gjjy.basiclib.api.entity.GoogleBuySubEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.entity.BuyVipEvaluationEntity;
import com.gjjy.basiclib.entity.BuyVipOptionEntity;
import com.gjjy.basiclib.mvp.model.OtherModel;
import com.gjjy.basiclib.mvp.model.ReqConfigModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.mvp.view.BuyVipView;
import com.gjjy.basiclib.statistical.StatisticalManage;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.googlebillinglib.Client;
import com.gjjy.googlebillinglib.ClientDao;
import com.gjjy.googlebillinglib.GooglePlayProduct;
import com.gjjy.googlebillinglib.SkuUtils;
import com.gjjy.googlebillinglib.annotation.PurchaseState;
import com.gjjy.googlebillinglib.annotation.PurchaseType;
import com.gjjy.googlebillinglib.entity.PurchaseResultEntity;
import com.gjjy.googlebillinglib.entity.SkuList;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.toast.ToastManage;

import java.util.ArrayList;
import java.util.List;

public class BuyVipPresenter extends MvpPresenter<BuyVipView> {
    @Model
    private UserModel mUserModel;
    @Model
    private OtherModel mOtherModel;
    @Model
    private ReqConfigModel mReqConfigModel;

    private Client mGoogleProductClient;
    private ClientDao mGoogleProductDao;
    private SkuList mSkuList;
    private String mCurrentSkuId;
    private String mCurrentSubscriptionPeriod;
    private SkuDetails mCurrentSku;

    private boolean isHaveSelectTag = false;

    private final List<GoogleBuySubEntity> mGoogleBuySubList = new ArrayList<GoogleBuySubEntity>();

    public BuyVipPresenter(@NonNull BuyVipView view) {
        super(view);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);

        //展示评论
        if (mReqConfigModel.isEnableBuyVipEval()) {
            queryBuyVipEvalList();
        }
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        if (mUserModel.getVipStatus() == 2) {
            finish();
            return;
        }
        queryBuySubList();
    }

    @Override
    public void onLifeDestroy() {
        if (mGoogleProductClient != null) mGoogleProductClient.endConnection();
        super.onLifeDestroy();
    }

    public void setCurrentSkuId(String sku) {
        mCurrentSkuId = sku;
        LogUtil.e("BuyVip -> CurrentSkuId -> " + sku);
    }

    public String getCurrentSkuId() {
        return mCurrentSkuId;
    }

    public boolean checkIsLogin() {
        if (getActivity() == null) {
            return false;
        }
        boolean isLoginResult = mUserModel.isLoginResult();
        if (!isLoginResult) {
            StartUtil.startLoginActivity(getActivity(), PageName.BUY_VIP);
        }
        return isLoginResult;
    }

    private String mOrderId;

    public void play(String skuId) {
//        mCurrentSkuId = skuId;
        mCurrentSku = mSkuList.getSkuDetails(skuId);
        if (mCurrentSku == null) {
            viewCall(v -> v.onCallBuyVipResult(PurchaseState.UNSPECIFIED_STATE, false, "sku is null"));
            return;
        }
        mCurrentSubscriptionPeriod = mCurrentSku.getSubscriptionPeriod();

        viewCall(v -> v.onCallLoadingDialog(true));

        List<GoogleBuySubEntity> googleBuySubList = new ArrayList<GoogleBuySubEntity>();
        googleBuySubList.addAll(mGoogleBuySubList);

        for (GoogleBuySubEntity sub : googleBuySubList) {
            if (skuId.equals(sub.getTpGoodsId())) {
                mOtherModel.reqCreateOrder(sub.getId(), orderEntity -> {
                    mOrderId = orderEntity.getOrderId();
                    //发起购买
                    mGoogleProductDao.launchBillingFlow(getActivity(), PurchaseType.ACKNOWLEDGE, mCurrentSku);
                });
                return;
            }
        }

        viewCall(v -> {
            v.onCallLoadingDialog(false);
            v.onCallBuyVipResult(PurchaseState.UNSPECIFIED_STATE, false, "skuId not found");
        });
    }

    public void queryBuySubList() {
        //查询所有订阅
        List<GoogleBuySubEntity> googleBuySubList = new ArrayList<GoogleBuySubEntity>();
        googleBuySubList.addAll(mGoogleBuySubList);

        if (googleBuySubList.size() > 0) {
            startGoogleProduct(toSkuArr(googleBuySubList));
            return;
        }
        //正常列表
        mOtherModel.queryGoogleBuySubList(norList -> {
            mGoogleBuySubList.clear();
            mGoogleBuySubList.addAll(norList);
            try {
                // mGoogleBuySubList.clear();
                googleBuySubList.addAll(mGoogleBuySubList);
                //开始查询
                startGoogleProduct(toSkuArr(googleBuySubList));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void startGoogleProduct(List<String> skuArr) {
        if (mGoogleProductClient == null) {
            mGoogleProductClient = GooglePlayProduct.get().create(getContext());
        }
        //开始连接
        mGoogleProductClient.startConnection();
        //连接成功后才会返回Dao
        mGoogleProductClient.setOnClientDaoListener(dao -> {
            mGoogleProductDao = dao;
            //查询sku列表
            dao.querySkuDetailsOfInApp(data -> callQuerySkuDetailsOfSubs(skuArr, data), skuArr);
        });

        //购买结果
        mGoogleProductClient.setOnPurchaseResultListener(new Client.OnPurchaseResultListener() {
            @Override
            public boolean onParam(Purchase purchase) {
                Log.e("GooglePlaySub", "onParam -> " + purchase.getPurchaseToken());
                return true;
            }

            @Override
            public void onResult(PurchaseResultEntity data) {
                if (data.getPurchaseState() != PurchaseState.PURCHASED) {
                    viewCall(v -> {
                        v.onCallLoadingDialog(false);
                        v.onCallBuyVipResult(PurchaseState.PENDING, false, "state is not purchased");
                    });
                }

                if (TextUtils.isEmpty(mOrderId)) {
                    viewCall(v -> {
                        v.onCallLoadingDialog(false);
                        v.onCallBuyVipResult(PurchaseState.UNSPECIFIED_STATE, false, " orderId:" + mOrderId + " not found");
                    });
                    return;
                }
                viewCall(v -> v.onCallLoadingDialog(true), 250);
                //设置new_sub
                sendNewSub(data);
            }
        });
    }

    private void callQuerySkuDetailsOfSubs(List<String> skuArr, SkuList data) {
        mSkuList = data;
        if (mSkuList != null) {
            mSkuList.sort(skuArr);
        }
        List<BuyVipOptionEntity> skuList = toBuyVipOptionEntityArr(mSkuList);
        List<BuyVipOptionEntity> norList = new ArrayList<>();

        for (BuyVipOptionEntity skuData : skuList) {
            for (GoogleBuySubEntity norData : mGoogleBuySubList) {
                if (buySub2SkuData(norData, skuData)) {
                    norList.add(skuData);
                }
            }
        }

        viewCall(v -> {
            Context context = v.getContext();
            if (context != null && norList.size() == 0) {
                ToastManage.get().showToast(context, R.string.stringError);
                finish();
                return;
            }
            v.onCallNormalVipOptionData(norList);
            v.onCallLoadingDialog(false);
        });
        LogUtil.e("GooglePlaySub", "startGoogleProduct -> data:" + data);
    }

    private boolean buySub2SkuData(GoogleBuySubEntity subData, BuyVipOptionEntity skuData) {
        if (!subData.getTpGoodsId().equals(skuData.getSkuId())) {
            return false;
        }
        //展示热门
        skuData.setHotPrice(false);
        //展示折扣
        skuData.setDiscount(false);
        //是否显示原价
        skuData.setShowPriceInfo(true);
        //默认选中的id
        if (!isHaveSelectTag && skuData.isHotPrice()) {
            setCurrentSkuId(skuData.getSkuId());
            isHaveSelectTag = true;
        }
        return true;
    }

    /**
     * 设置new_sub
     *
     * @param data 处理结果
     */
    private void sendNewSub(PurchaseResultEntity data) {
        mOtherModel.sendNewSub(mOrderId, data.getPurchaseToken(), r -> {
            buriedPointBuyVip(r, mCurrentSubscriptionPeriod);
            if (!r) {
                data.setPurchaseState(PurchaseState.PENDING);
            }
            mUserModel.setVip(r);
            if (r) {
                mUserModel.setVipStatus(2);
            }
            viewCall(v -> v.onCallBuyVipResult(data.getPurchaseState(), mUserModel.isVip(), "newSub result:" + r));
            Log.e("GooglePlaySub", "onResult -> data:" + data + " | result:" + r);
        });
    }

    public void queryBuyVipEvalList() {
        Resources res = getResources();
        if (res == null) {
            return;
        }

        List<BuyVipEvaluationEntity> list = new ArrayList<>();
        int[] userPhotoArr = {
                R.drawable.ic_buy_vip_eval_user_photo_1,
                R.drawable.ic_buy_vip_eval_user_photo_2,
                R.drawable.ic_buy_vip_eval_user_photo_3
        };
        String[] userNameArr = res.getStringArray(R.array.stringBuyVipEvalUserNameArray);
        String[] learnedDaysArr = res.getStringArray(R.array.stringBuyVipEvalLearnedDaysArray);
        String[] evalContentArr = res.getStringArray(R.array.stringBuyVipEvalContentArray);
        for (int i = 0; i < userPhotoArr.length; i++) {
            BuyVipEvaluationEntity data = new BuyVipEvaluationEntity();
            data.setUserPhotoResId(userPhotoArr[i]);
            data.setUserName(userNameArr[i]);
            data.setLearnedDaysContent(learnedDaysArr[i]);
            data.setEvalContent(evalContentArr[i]);
            list.add(data);
        }

        viewCall(v -> v.onCallEvalListData(list));
    }

    public String toDateNum(int time) {
        return time < 10 ? ("0" + time) : ObjUtils.parseString(time);
    }

    private void buriedPointBuyVip(boolean result, String subscriptionPeriod) {
        if (getActivity() == null || TextUtils.isEmpty(subscriptionPeriod)) {
            return;
        }
        //埋点转换
        String time = SkuUtils.toSubscriptionPeriod(getActivity().getResources(), subscriptionPeriod)
                .toLowerCase()
                .replace("years", "年")
                .replace("year", "年")
                .replace("months", "个月")
                .replace("month", "个月")
                .replace("weeks", "个星期")
                .replace("week", "个星期")
                .replace("days", "天")
                .replace("day", "天");
        //非会员的限时弹窗埋点
        BuriedPointEvent.get().onVipPageOfPayButton(
                getContext(),
                result,
                mUserModel.getUid(),
                mUserModel.getUserName(getActivity().getResources()),
                mUserModel.isLoginResult(),
                mUserModel.isVip(),
                time
        );

        if (mCurrentSku != null && result) {
            StatisticalManage.get().buyVip(
                    mOrderId,
                    SkuUtils.getName(mCurrentSku.getTitle()),
                    SkuUtils.getPrice(mCurrentSku.getPrice()),
                    SkuUtils.getPrice(mCurrentSku.getIntroductoryPrice()),
                    mCurrentSku.getPriceCurrencyCode(),
                    mCurrentSku.getType()
            );
        }
    }

    @NonNull
    private List<BuyVipOptionEntity> toBuyVipOptionEntityArr(SkuList data) {
        if (getActivity() == null || data == null) {
            return new ArrayList<>();
        }
        Resources res = getActivity().getResources();
        List<BuyVipOptionEntity> retList = new ArrayList<>();
        List<SkuDetails> list = data.getSkuDetailsList();
        for (int i = 0; i < list.size(); i++) {
            SkuDetails sku = list.get(i);
            String price = sku.getPrice();
            if (TextUtils.isEmpty(price)) {
                continue;
            }

            BuyVipOptionEntity entity = new BuyVipOptionEntity();
            String priceUnit = SkuUtils.getPriceSymbol(price);
            String infoPrice = null;
            String strSymbol = null;
            String subscriptionPeriod = sku.getSubscriptionPeriod();
            int dateOfNum = SkuUtils.toSubscriptionPeriodOfDate(subscriptionPeriod);
            char dateSymbol = SkuUtils.toSubscriptionPeriodOfDateSymbol(subscriptionPeriod);

            price = SkuUtils.getPrice(price);
            entity.setSkuId(sku.getSku());
            //时间
            entity.setInDate(SkuUtils.getName(sku.getTitle()));
            //价格
            entity.setPrice(price);
            //特惠价格
            entity.setIntroductoryPrice(SkuUtils.getPrice(sku.getIntroductoryPrice()));
            //货币符号
            entity.setPriceUnit(priceUnit);
            //周期时长
            entity.setSubscriptionDate(dateOfNum);
            //周期符号
            entity.setSubscriptionDateSymbol(dateSymbol);
            //年
            if (dateOfNum != 0 && dateSymbol == SkuUtils.DateSymbol.YEAR) {
                dateOfNum *= 12;
            }
            //计算每个月或每天的价格
            if (dateOfNum > 1) {
                try {
                    infoPrice = SkuUtils.toPriceAmountMicros(sku.getPriceAmountMicros() / dateOfNum);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (dateSymbol == SkuUtils.DateSymbol.YEAR || dateSymbol == SkuUtils.DateSymbol.MONTH) {
                    strSymbol = res.getString(R.string.stringMonth).toLowerCase();
                }
                if (!TextUtils.isEmpty(infoPrice)) {
                    entity.setPricePerMonth(priceUnit + infoPrice + "/" + strSymbol);
                }
            } else {
                entity.setPricePerMonth(null);
            }

            retList.add(entity);
        }
        return retList;
    }

    private List<String> toSkuArr(List<GoogleBuySubEntity> list) {
        List<String> skuList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            GoogleBuySubEntity data = list.get(i);
            String tpGoodsId = data.getTpGoodsId();
            if (!tpGoodsId.isEmpty()) {
                skuList.add(tpGoodsId);
            }
        }
        return skuList;
    }
}
