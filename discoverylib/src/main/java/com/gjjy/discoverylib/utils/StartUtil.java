package com.gjjy.discoverylib.utils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gjjy.basiclib.utils.Constant;

public class StartUtil {

    public static void startMyCourseActivity() {
        ARouter.getInstance().build( "/discovery/myCourseActivity" ).navigation();
    }

    public static void startListenDailyMoreListActivity() {
        ARouter.getInstance().build( "/discovery/listenDailyMoreListActivity" ).navigation();
    }

    public static void startTargetedLearningVoiceMoreListActivity() {
        ARouter.getInstance().build( "/discovery/TargetedLearningMoreListActivity" ).navigation();
    }

    public static void startListenDailyDetailsActivity(long id,String videoId, String name) {
        ARouter.getInstance()
                .build( "/discovery/listenDailyDetailsActivity" )
                .withLong( Constant.ID_FOR_INT, id )
                .withString( Constant.ID_FOR_VIDEO, videoId )
                .withString( Constant.NAME, name )
                .navigation();
    }

    public static void startPopularVideosDetailsActivity(long id, String videoId,String name) {
        ARouter.getInstance()
                .build( "/discovery/hotVideoDetailsActivity" )
                .withLong( Constant.ID_FOR_INT, id )
                .withString( Constant.ID_FOR_VIDEO, videoId )
                .withString( Constant.NAME, name )
                .navigation();
    }

    public static void startPopularVideosMoreListActivity() {
        ARouter.getInstance().build( "/discovery/hotVideoMoreListActivity" ).navigation();
    }

    public static void startTargetedLearningDetailsActivity(long id,String videoId) {
        ARouter.getInstance()
                .build( "/discovery/TargetedLearningDetailsActivity" )
                .withLong( Constant.ID_FOR_INT, id )
                .withString( Constant.ID_FOR_VIDEO, videoId )
                .navigation();
    }
}
