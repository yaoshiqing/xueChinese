package com.gjjy.basiclib.mvp.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.gjjy.basiclib.R;
import com.gjjy.basiclib.api.entity.GoogleBuySubEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.entity.BuyVipEvaluationEntity;
import com.gjjy.basiclib.entity.BuyVipOptionEntity;
import com.gjjy.basiclib.entity.VerifyPayReq;
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
    private SkuDetails mCurrentSkuDetails;

    private boolean isHaveSelectTag = false;
    private String mGoogleOrderId = "";
    private long mPurchaseTime = 0L;
    private String mOriginalJson = "";
    private String mSignature = "";

    private final List<GoogleBuySubEntity> mGoogleBuySubList = new ArrayList<GoogleBuySubEntity>();

    public BuyVipPresenter(@NonNull BuyVipView view) {
        super(view);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);

        //????????????
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
        if (mGoogleProductClient != null) {
            mGoogleProductClient.endConnection();
        }
        super.onLifeDestroy();
    }

    public void setCurrentSkuId(String sku) {
        mCurrentSkuId = sku;
        LogUtil.e("BuyVip -> CurrentSkuId -> " + sku);
    }

    public String getCurrentSkuId() {
        return mCurrentSkuId;
    }

    public void setGoogleOrderId(String googleOrderId) {
        mGoogleOrderId = googleOrderId;
        LogUtil.e("BuyVip -> googleOrderId -> " + googleOrderId);
    }

    public String getGoogleOrderId() {
        return mGoogleOrderId;
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


    public void play(String skuId, String googleOrderId) {
        mCurrentSkuDetails = mSkuList.getSkuDetails(skuId);
        if (mCurrentSkuDetails == null) {
            viewCall(v -> v.onCallBuyVipResult(PurchaseState.UNSPECIFIED_STATE, false, "sku is null"));
            return;
        }
        mCurrentSubscriptionPeriod = mCurrentSkuDetails.getSubscriptionPeriod();

        viewCall(v -> v.onCallLoadingDialog(true));

        List<GoogleBuySubEntity> googleBuySubList = new ArrayList<GoogleBuySubEntity>();
        googleBuySubList.addAll(mGoogleBuySubList);

        for (GoogleBuySubEntity sub : googleBuySubList) {
            if (skuId.equals(sub.getTpGoodsId())) {
                // ????????????
                mGoogleProductDao.launchBillingFlow(getActivity(), PurchaseType.ACKNOWLEDGE, mCurrentSkuDetails, googleOrderId, mUserModel.getUid());
                return;
            }
        }

        viewCall(v -> {
            v.onCallLoadingDialog(false);
            v.onCallBuyVipResult(PurchaseState.UNSPECIFIED_STATE, false, "skuId not found");
        });
    }

    public void queryBuySubList() {
        // ??????????????????
        List<GoogleBuySubEntity> googleBuySubList = new ArrayList<GoogleBuySubEntity>();
        googleBuySubList.addAll(mGoogleBuySubList);

        if (googleBuySubList.size() > 0) {
            startGoogleProduct(toSkuList(googleBuySubList));
            return;
        }
        // ????????????
        mOtherModel.queryGoogleBuySubList(googleBuySubEntityList -> {
            mGoogleBuySubList.clear();
            mGoogleBuySubList.addAll(googleBuySubEntityList);
            try {
                googleBuySubList.addAll(mGoogleBuySubList);
                // ????????????
                startGoogleProduct(toSkuList(googleBuySubList));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void startGoogleProduct(List<String> list) {
        if (mGoogleProductClient == null) {
            mGoogleProductClient = GooglePlayProduct.get().create(getContext());
        }
        // ????????????
        mGoogleProductClient.startConnection();
        // ???????????????????????????Dao
        mGoogleProductClient.setOnClientDaoListener(dao -> {
            mGoogleProductDao = dao;
            // ???????????????????????????
            dao.setSkuType(BillingClient.SkuType.SUBS);
            // ????????????????????????????????????
            // dao.setSkuType(BillingClient.SkuType.INAPP);
            // ??????sku??????
            dao.querySkuDetails(new Consumer<SkuList>() {
                @Override
                public void accept(SkuList skuList) {
                    callQuerySkuDetailsOfSubs(skuList);
                }
            }, list);
        });

        // ????????????
        mGoogleProductClient.setOnPurchaseResultListener(new Client.OnPurchaseResultListener() {
            @Override
            public boolean onParam(Purchase purchase) {
                mGoogleOrderId = purchase.getOrderId();
                mPurchaseTime = purchase.getPurchaseTime();
                mOriginalJson = purchase.getOriginalJson();
                mSignature = purchase.getSignature();

                Log.e("GooglePlaySub", "onParam mGoogleProductClient OnPurchaseResultListener -> "
                        + " , signature = " + purchase.getSignature()
                        + " , purchaseState = " + purchase.getPurchaseState()
                        + " , orderId = " + purchase.getOrderId()
                        + " , purchaseTime = " + purchase.getPurchaseTime()
                        + " , originalJson = " + purchase.getOriginalJson()
                        + " , productId = " + purchase.getSkus()
                        + " , developerPayload = " + purchase.getDeveloperPayload()
                        + " , accountIdentifiers = " + purchase.getAccountIdentifiers()
                        + " , quantity = " + purchase.getQuantity()
                        + " , purchaseToken = " + purchase.getPurchaseToken());

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

                viewCall(v -> v.onCallLoadingDialog(true), 250);
                // ??????new_sub
                // sendNewSub(data);

                VerifyPayReq req = new VerifyPayReq();
                req.setGoogleOrderId(mGoogleOrderId);
                // ?????????????????????id
                req.setProductId(mCurrentSkuId);
                req.setOriginalJson(mOriginalJson);
                req.setPurchaseToken(data.getPurchaseToken());
                req.setPackageName(getActivity().getPackageName());
                req.setPurchaseState(data.getPurchaseState() + "");
                req.setSignture(mSignature);
                req.setPurchaseTime(mPurchaseTime);
                // google ??????????????????
                googlePayVerifyPay(data, req);
            }
        });
    }

    private void callQuerySkuDetailsOfSubs(SkuList skuList) {
        mSkuList = skuList;
//        if (mSkuList != null) {
//            mSkuList.sort(list);
//        }
        List<BuyVipOptionEntity> buyVipList = toBuyVipOptionEntityArr(mSkuList);
        List<BuyVipOptionEntity> norList = new ArrayList<>();

        for (BuyVipOptionEntity skuData : buyVipList) {
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
        LogUtil.e("GooglePlaySub", "startGoogleProduct skuList:" + skuList);
    }

    private boolean buySub2SkuData(GoogleBuySubEntity subData, BuyVipOptionEntity skuData) {
        if (!subData.getTpGoodsId().equals(skuData.getSkuId())) {
            return false;
        }
        // ????????????
        skuData.setHotPrice(false);
        // ????????????
        skuData.setDiscount(false);
        // ??????????????????
        skuData.setShowPriceInfo(true);
        // ???????????????id
        if (!isHaveSelectTag && skuData.isHotPrice()) {
            setCurrentSkuId(skuData.getSkuId());
            setGoogleOrderId(skuData.getGoogleOrderId());
            LogUtil.e("GooglePlaySub", "buySub2SkuData skuData.getSkuId()= " + skuData.getSkuId() + ", googleOrderId = " + skuData.getGoogleOrderId());
            isHaveSelectTag = true;
        }
        return true;
    }

    /**
     * ??????new_sub
     *
     * @param data ????????????
     */
    private void sendNewSub(PurchaseResultEntity data) {
        mOtherModel.sendNewSub(data.getPurchaseToken(), isSuccess -> {
            buriedPointBuyVip(isSuccess, mCurrentSubscriptionPeriod);
            if (!isSuccess) {
                data.setPurchaseState(PurchaseState.PENDING);
            }
            mUserModel.setVip(isSuccess);
            if (isSuccess) {
                mUserModel.setVipStatus(2);
            }
            viewCall(v -> v.onCallBuyVipResult(data.getPurchaseState(), mUserModel.isVip(), "newSub result:" + isSuccess));
            Log.e("GooglePlaySub", "onResult -> data:" + data + " | result:" + isSuccess);
        });
    }

    private void googlePayVerifyPay(PurchaseResultEntity data, VerifyPayReq verifyPayReq) {
        mOtherModel.queryGooglePayVerifyPay(verifyPayReq, isSuccess -> {
            buriedPointBuyVip(isSuccess, mCurrentSubscriptionPeriod);
            if (!isSuccess) {
                data.setPurchaseState(PurchaseState.PENDING);
            }
            mUserModel.setVip(isSuccess);
            if (isSuccess) {
                mUserModel.setVipStatus(2);
            }
            viewCall(v -> v.onCallBuyVipResult(data.getPurchaseState(), mUserModel.isVip(), "newSub result:" + isSuccess));
            Log.e("GooglePlaySub", "onResult -> data:" + data + " | result:" + isSuccess);
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
        // ????????????
        String time = SkuUtils.toSubscriptionPeriod(getActivity().getResources(), subscriptionPeriod)
                .toLowerCase()
                .replace("years", "???")
                .replace("year", "???")
                .replace("months", "??????")
                .replace("month", "??????")
                .replace("weeks", "?????????")
                .replace("week", "?????????")
                .replace("days", "???")
                .replace("day", "???");
        // ??????????????????????????????
        BuriedPointEvent.get().onVipPageOfPayButton(
                getContext(),
                result,
                mUserModel.getUid(),
                mUserModel.getUserName(getActivity().getResources()),
                mUserModel.isLoginResult(),
                mUserModel.isVip(),
                time
        );

        if (mCurrentSkuDetails != null && result) {
            StatisticalManage.get().buyVip(
                    mGoogleOrderId,
                    SkuUtils.getName(mCurrentSkuDetails.getTitle()),
                    SkuUtils.getPrice(mCurrentSkuDetails.getPrice()),
                    SkuUtils.getPrice(mCurrentSkuDetails.getIntroductoryPrice()),
                    mCurrentSkuDetails.getPriceCurrencyCode(),
                    mCurrentSkuDetails.getType()
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
        List<SkuDetails> skuDetails = data.getSkuDetailsList();
        for (int i = 0; i < skuDetails.size(); i++) {
            SkuDetails sku = skuDetails.get(i);
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
            //??????
            entity.setInDate(SkuUtils.getName(sku.getTitle()));
            //??????
            entity.setPrice(price);
            //????????????
            entity.setIntroductoryPrice(SkuUtils.getPrice(sku.getIntroductoryPrice()));
            //????????????
            entity.setPriceUnit(priceUnit);
            //????????????
            entity.setSubscriptionDate(dateOfNum);
            //????????????
            entity.setSubscriptionDateSymbol(dateSymbol);
            //???
            if (dateOfNum != 0 && dateSymbol == SkuUtils.DateSymbol.YEAR) {
                dateOfNum *= 12;
            }
            //?????????????????????????????????
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

    private List<String> toSkuList(List<GoogleBuySubEntity> list) {
        List<String> skuList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            GoogleBuySubEntity data = list.get(i);
            String tpGoodsId = data.getTpGoodsId();
            // ???????????? 1????????? 2??????????????????
            if (!tpGoodsId.isEmpty() && data.getBuyType() == 1) {
                skuList.add(tpGoodsId);
            }
        }
        return skuList;
    }
}
