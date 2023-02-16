package com.gjjy.googlebillinglib;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.gjjy.googlebillinglib.annotation.PurchaseState;
import com.gjjy.googlebillinglib.annotation.PurchaseType;
import com.gjjy.googlebillinglib.entity.PurchaseResultEntity;
import com.gjjy.googlebillinglib.entity.ResultEntity;

public class Client implements BillingClientStateListener {
    public interface OnPurchaseResultListener {
        boolean onParam(Purchase purchase);
        void onResult(PurchaseResultEntity result);
    }

    @Nullable
    private BillingClient mBillingClient;
    private ClientDao mClientDao;
    @PurchaseType
    private int mPurchaseType;

    private Consumer<ClientDao> mOnClientDaoListener;
    private Consumer<ResultEntity> mOnConnectionResultListener;
    private OnPurchaseResultListener mOnPurchaseResultListener;

    public Client(@Nullable Context context) {
        if (context == null) {
            return;
        }
        mBillingClient = BillingClient.newBuilder(context)
                .setListener(createPurchasesUpdatedListener())
                .enablePendingPurchases()
                .build();
    }

    /**
     * 服务器连接成功
     *
     * @param result 响应结果
     */
    @Override
    public void onBillingSetupFinished(@NonNull BillingResult result) {
        ResultEntity data = new ResultEntity(result);

        Log.e("GooglePlaySub", "startConnection -> " +
                "onBillingSetupFinished -> " + data.toString()
        );

        if (mBillingClient != null) {
            mClientDao = data.isSuccess() ? new ClientDao(this, mBillingClient) : null;
        }
        // 返回Dao
        if (mOnClientDaoListener != null && mClientDao != null) {
            mOnClientDaoListener.accept(mClientDao);
        }
        // 返回连接结果
        if (mOnConnectionResultListener != null) {
            mOnConnectionResultListener.accept(data);
        }
    }

    /**
     * 服务器断开连接
     */
    @Override
    public void onBillingServiceDisconnected() {
        // 尝试在下一个请求上重新启动连接 通过调用startConnection()方法重新连接
        ResultEntity data = new ResultEntity();
        data.setCode(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED);
        data.setDebugMessage("Billing service disconnected");

        Log.e("GooglePlaySub", "startConnection -> " + "onBillingServiceDisconnected");

        mClientDao = null;
        if (mOnConnectionResultListener != null) {
            mOnConnectionResultListener.accept(data);
        }
    }

    // 支付监听回调
    public PurchasesUpdatedListener createPurchasesUpdatedListener() {
        return (result, purchases) -> {
            int code = result.getResponseCode();
            if (code == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase p : purchases) {
                    if (p == null) {
                        continue;
                    }
                    // 参数
                    if (mOnPurchaseResultListener.onParam(p)) {
                        handlePurchaseUnspecified(code, p, mPurchaseType, PurchaseState.PURCHASED);
                        break;
                    }
                    // 购买类型
                    switch (mPurchaseType) {
                        case PurchaseType.CONSUME:              // 一次性消耗商品
                            handlePurchaseOfConsume(p);
                            break;
                        case PurchaseType.ACKNOWLEDGE:          // 非消耗商品
                            handlePurchaseOfAcknowledge(p);
                            break;
                    }
                }
            } else {
                // 未完成购买
                handlePurchaseUnspecified(code, null, PurchaseType.UNSPECIFIED_TYPE, PurchaseState.PENDING);
            }
        };
    }

    void setPurchaseType(@PurchaseType int type) {
        mPurchaseType = type;
    }

    BillingClient getBillingClient() {
        return mBillingClient;
    }

    public ClientDao getClientDao() {
        return mClientDao;
    }

    public void startConnection(Consumer<ClientDao> callClientDao, Consumer<ResultEntity> l) {
        if (mBillingClient == null) {
//            throw new NullPointerException("Call the create(Activity) method.");
            if (callClientDao != null) {
                callClientDao.accept(null);
            }
            return;
        }

        // 设置Dao监听器
        if (callClientDao != null) {
            setOnClientDaoListener(callClientDao);
        }
        // 设置监听器
        if (l != null) {
            setOnConnectionResultListener(l);
        }
        // 重复的连接直接返回
        if (mClientDao != null) {
            if (mOnClientDaoListener != null) {
                mOnClientDaoListener.accept(mClientDao);
            }

            if (mOnConnectionResultListener != null) {
                mOnConnectionResultListener.accept(new ResultEntity().setCode(BillingClient.BillingResponseCode.OK));
            }
            Log.e("GooglePlaySub", "startConnection -> reConnection");
        }
        // 发起连接
        mBillingClient.startConnection(this);
    }

    public void startConnection(Consumer<ClientDao> callClientDao) {
        startConnection(callClientDao, null);
    }

    public void startConnection() {
        startConnection(null, null);
    }

    public void endConnection() {
        if (mBillingClient != null) {
            mBillingClient.endConnection();
        }
    }

    /**
     * 客户端Dao回调监听器
     * 服务器连接成功后才会返回Dao，可以用Dao发起查询，购买等请求
     *
     * @param l 监听器
     * @return this
     */
    public Client setOnClientDaoListener(Consumer<ClientDao> l) {
        this.mOnClientDaoListener = l;
        return this;
    }

    /**
     * 连接Google Play服务器结果
     *
     * @param l 监听器
     * @return this
     */
    public Client setOnConnectionResultListener(Consumer<ResultEntity> l) {
        this.mOnConnectionResultListener = l;
        return this;
    }

    /**
     * 购买结果监听器
     *
     * @param l 监听器
     * @return this
     */
    public Client setOnPurchaseResultListener(OnPurchaseResultListener l) {
        this.mOnPurchaseResultListener = l;
        return this;
    }

    /**
     * 一次性消耗商品确认
     *
     * @param purchase 商品
     */
    private void handlePurchaseOfConsume(Purchase purchase) {
        // 参数
        ConsumeParams params = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        // 消费监听回调处理
        ConsumeResponseListener l = (result, purchaseToken) -> {
            if (mOnPurchaseResultListener == null) return;
            int purchaseState = PurchaseState.UNSPECIFIED_STATE;            // 未知状态
            switch (result.getResponseCode()) {
                case BillingClient.BillingResponseCode.OK:                  // 确认成功
                    purchaseState = PurchaseState.PURCHASED;
                    break;
                case BillingClient.BillingResponseCode.USER_CANCELED:       // 取消确认
                    purchaseState = PurchaseState.PENDING;
                    break;
            }
            mOnPurchaseResultListener.onResult(new PurchaseResultEntity(result, PurchaseType.CONSUME).setPurchaseState(purchaseState).setToken(purchaseToken));
        };
        // 发起确认
        if (mBillingClient != null) {
            mBillingClient.consumeAsync(params, l);
        }
    }

    /**
     * 非消耗性商品确认
     *
     * @param purchase 商品
     */
    private void handlePurchaseOfAcknowledge(Purchase purchase) {
        int state = purchase.getPurchaseState();
        String purchaseToken = purchase.getPurchaseToken();
        // 未订阅
        if (state != Purchase.PurchaseState.PURCHASED) {
            if (mOnPurchaseResultListener == null) {
                return;
            }
            mOnPurchaseResultListener.onResult(new PurchaseResultEntity()
                    .setPurchaseState(state)
                    .setPurchaseToken(purchaseToken)
            );
            return;
        }
        // 订阅是否自动续订 true：则订阅处于活动状态，并将在下一个结算日期自动续订 false：则表示用户已取消订阅。
        if (purchase.isAcknowledged()) {
            // 订阅处于活动状态
            mOnPurchaseResultListener.onResult(new PurchaseResultEntity()
                    .setPurchaseState(PurchaseState.HAVE_ACKNOWLEDGE)
                    .setPurchaseToken(purchaseToken)
            );
            return;
        }
        // 参数
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build();

        // 回调处理
        AcknowledgePurchaseResponseListener l = result -> {
            if (mOnPurchaseResultListener == null) {
                return;
            }
            // 订阅成功
            mOnPurchaseResultListener.onResult(
                    new PurchaseResultEntity(result, PurchaseType.ACKNOWLEDGE)
                            .setPurchaseState(PurchaseState.PURCHASED)
                            .setPurchaseToken(purchaseToken)
            );
        };

        // 发起确认
        if (mBillingClient != null) {
            mBillingClient.acknowledgePurchase(params, l);
        }
    }

    /**
     * 未完成购买
     *
     * @param responseCode 响应码
     */
    private void handlePurchaseUnspecified(@BillingClient.BillingResponseCode int responseCode, @Nullable Purchase purchase, @PurchaseType int type, @PurchaseState int state) {
        if (mOnPurchaseResultListener == null) {
            return;
        }
        PurchaseResultEntity data = new PurchaseResultEntity();
        data.setCode(responseCode);
//        data.setDebugMessage( "Purchase error!" );
        data.setPurchaseType(type);
        data.setPurchaseState(state);
        if (purchase != null) {
            data.setPurchaseToken(purchase.getPurchaseToken());
        }
        mOnPurchaseResultListener.onResult(data);
    }
}