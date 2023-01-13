package com.gjjy.steerlib;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.utils.Constant;

public class StartUtil {
    public static void startLoginActivity(Activity activity,
                                          @PageName String pageName) {
        Postcard p = ARouter.getInstance().build("/login/loginActivity");
        LogisticsCenter.completion( p );
        Intent intent = new Intent( activity, p.getDestination() );
        intent.putExtra( Constant.PAGE_NAME, pageName );
        com.gjjy.basiclib.utils.StartUtil.startActivityForResult(
                activity,
                p,
                com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_LOGIN
        );
    }
}
