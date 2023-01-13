package com.gjjy.speechsdk;

import android.Manifest;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

public class PermManage {

    private PermManage() {}
    public static Perm create(FragmentActivity activity) {
        return HANDLER.I.createRxPerm( activity );
    }
    public static Perm create(Fragment fragment) {
        return HANDLER.I.createRxPerm( fragment );
    }
    private static final class HANDLER { private static final PermManage I = new PermManage(); }

    private Perm createRxPerm(FragmentActivity activity) { return new Perm( activity ); }
    private Perm createRxPerm(Fragment fragment) { return new Perm( fragment ); }

    public class Perm {
        private RxPermissions mRx;
        private int mRecordAudioPermCount;
        public Perm(Fragment fragment) { mRx = new RxPermissions( fragment ); }
        public Perm(FragmentActivity activity) { mRx = new RxPermissions( activity ); }

        /**
         * 请求语音权限
         * @return  是否被处理
         */
        public boolean reqRecordAudioPerm(Call call) {
            String pRA = Manifest.permission.RECORD_AUDIO;
            String pWES = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            String pRES = Manifest.permission.READ_EXTERNAL_STORAGE;
            if( isGranted( pRA, call ) && isGranted( pWES, call ) && isGranted( pRES, call ) ) {
                return true;
            }
            return requestEach( call, pRA, pWES, pRES );
        }
        public boolean reqRecordAudioPerm(Consumer<Boolean> call) {
            String pRA = Manifest.permission.RECORD_AUDIO;
            String pWES = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            String pRES = Manifest.permission.READ_EXTERNAL_STORAGE;
            return request( call, pRA, pWES, pRES );
        }
        public boolean reqRecordAudioPerm() { return reqRecordAudioPerm( (Call) null ); }

        public boolean reqExternalStoragePerm(Call call) {
            String pWES = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            String pRES = Manifest.permission.READ_EXTERNAL_STORAGE;
            if( isGranted( pWES, call ) && isGranted( pRES, call ) ) {
                return true;
            }
            return requestEach( call, pWES, pRES );
        }
        public boolean reqExternalStoragePerm() { return reqExternalStoragePerm( null ); }

//        public boolean reqIOPerm(Call call) {
//            String permW = Manifest.permission.WRITE_EXTERNAL_STORAGE;
//            String permR = Manifest.permission.READ_EXTERNAL_STORAGE;
//            if( isGranted( permW, call ) && isGranted( permR, call ) ) return true;
//            return requestEach( call, permW, permR );
//        }

        private boolean isGranted(String name, Call call) {
            if( mRx.isGranted( name ) ) {
                if( call == null ) return true;
                call.onCall( true, name, true );
                return true;
            }
            return false;
        }

        private boolean requestEach(Call call, String... name) {
            return mRx.requestEach( name ).subscribe(p -> {
                if( call == null ) return;
                call.onCall( p.granted, p.name, p.shouldShowRequestPermissionRationale );
            }).isDisposed();
        }

        private boolean request(Consumer<Boolean> call, String... name) {
            return mRx.request( name ).subscribe(p -> {
                if( call != null ) call.accept( p );
            }).isDisposed();
        }
    }

    public interface Call {
        void onCall(boolean isGranted, String name, boolean shouldShowRequestPermissionRationale);
    }
}
