package com.gjjy.basiclib.utils;

import android.content.Context;

import com.ybear.ybutils.utils.IOUtil;
import com.ybear.ybutils.utils.ObjUtils;

import java.util.Map;

import static com.gjjy.basiclib.utils.SpKey.SP_CD_NAME_MAIN;
import static com.gjjy.basiclib.utils.SpKey.SP_KEY_ANNOUNCEMENT_STATUS;
import static com.gjjy.basiclib.utils.SpKey.SP_KEY_FEEDBACK_STATUS;
import static com.gjjy.basiclib.utils.SpKey.SP_KEY_FIND_INIT_GUIDE_STATUS;
import static com.gjjy.basiclib.utils.SpKey.SP_KEY_HOME_INIT_GUIDE_STATUS;
import static com.gjjy.basiclib.utils.SpKey.SP_KEY_ME_INVITATION_CODE_INIT_GUIDE_STATUS;
import static com.gjjy.basiclib.utils.SpKey.SP_KEY_TARGETED_LEARNING_DETAILS_INIT_GUIDE_STATUS;

public class SpIO {
    public static void saveFeedbackStatus(Context context, boolean status) {
        if( context == null ) return;
        Map<String, Object> map = IOUtil.readSharedPreferences( context, SP_CD_NAME_MAIN );
        map.put( SP_KEY_FEEDBACK_STATUS, status );
        IOUtil.writeSharedPreferences( context, SP_CD_NAME_MAIN, map );
    }

    public static boolean isShowFeedback(Context context) {
        if( context == null ) return false;
        String key = SP_KEY_FEEDBACK_STATUS;
        Map<String, Object> map = IOUtil.readSharedPreferences( context, SP_CD_NAME_MAIN );
        return !( map.containsKey( key ) && ObjUtils.parseBoolean( map.get( key ) ) );
    }

    public static void saveAnnouncementStatus(Context context, String updateTime) {
        if( context == null ) return;
        Map<String, Object> map = IOUtil.readSharedPreferences( context, SP_CD_NAME_MAIN );
        map.put( SP_KEY_ANNOUNCEMENT_STATUS, updateTime );
        IOUtil.writeSharedPreferences( context, SP_CD_NAME_MAIN, map );
    }

    public static boolean isShowAnnouncement(Context context, String updateTime) {
        if( context == null ) return false;
        String key = SP_KEY_ANNOUNCEMENT_STATUS;
        Map<String, Object> map = IOUtil.readSharedPreferences( context, SP_CD_NAME_MAIN );
        String oldTime = String.valueOf( map.containsKey( key ) ? map.get( key ) : "0" );
        return oldTime == null || "0".equals( oldTime ) || !oldTime.equals( updateTime );
    }

    public static void saveFrontInitGuideStatus(Context context) {
        saveInitGuideStatus( context, SP_KEY_HOME_INIT_GUIDE_STATUS );
    }

    public static boolean isShowFrontInitGuide(Context context) {
        return isShowInitGuide( context, SP_KEY_HOME_INIT_GUIDE_STATUS );
    }

    public static void saveFindInitGuideStatus(Context context) {
        saveInitGuideStatus( context, SP_KEY_FIND_INIT_GUIDE_STATUS );
    }

    public static boolean isShowFindInitGuide(Context context) {
        return isShowInitGuide( context, SP_KEY_FIND_INIT_GUIDE_STATUS );
    }

    public static void saveTargetedLearningDetailsInitGuideStatus(Context context) {
        saveInitGuideStatus( context, SP_KEY_TARGETED_LEARNING_DETAILS_INIT_GUIDE_STATUS );
    }

    public static boolean isShowTargetedLearningDetailsInitGuide(Context context) {
        return isShowInitGuide( context, SP_KEY_TARGETED_LEARNING_DETAILS_INIT_GUIDE_STATUS );
    }

    public static void saveMeInvitationCodeInitGuideStatus(Context context) {
        saveInitGuideStatus( context, SP_KEY_ME_INVITATION_CODE_INIT_GUIDE_STATUS );
    }

    public static boolean isShowMeInvitationCodeInitGuideGuide(Context context) {
        return isShowInitGuide( context, SP_KEY_ME_INVITATION_CODE_INIT_GUIDE_STATUS );
    }

    public static void clearStatusAll(Context context) {
        if( context == null ) return;
        String[] keys = {
                SP_KEY_FEEDBACK_STATUS,
                SP_KEY_ANNOUNCEMENT_STATUS,
                SP_KEY_HOME_INIT_GUIDE_STATUS,
                SP_KEY_FIND_INIT_GUIDE_STATUS,
                SP_KEY_TARGETED_LEARNING_DETAILS_INIT_GUIDE_STATUS,
                SP_KEY_ME_INVITATION_CODE_INIT_GUIDE_STATUS,
        };
        Map<String, Object> map = IOUtil.readSharedPreferences( context, SP_CD_NAME_MAIN );
        for( String key : keys ) { map.put( key, 0 ); }
        IOUtil.writeSharedPreferences( context, SP_CD_NAME_MAIN, map );
    }

    private static void saveInitGuideStatus(Context context, String key) {
        if( context == null ) return;
        Map<String, Object> map = IOUtil.readSharedPreferences( context, SP_CD_NAME_MAIN );
        map.put( key, 1 );
        IOUtil.writeSharedPreferences( context, SP_CD_NAME_MAIN, map );
    }

    private static boolean isShowInitGuide(Context context, String key) {
        if( context == null ) return false;
        Map<String, Object> map = IOUtil.readSharedPreferences(
                context, SP_CD_NAME_MAIN
        );
        Object existKey = map != null && map.containsKey( key ) ? map.get( key ) : null;
        return existKey == null || ObjUtils.parseInt( existKey ) != 1;
    }
}
