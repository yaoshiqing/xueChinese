package com.gjjy.login.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class GoogleLogInImpl implements GoogleLogIn {
    private static final String TAG = "GoogleLogIn";
    private static final int RC_SIGN_IN = 800;
    private static final int RC_SCOPE_PERM = 801;

    private Consumer<Boolean> mScopePermCall;

    private WeakReference<Activity> mWRActivity;
    private final GoogleBuild mBuild;
    private GoogleSignInClient mClient = null;
    private GoogleCallback mCallback;


    GoogleLogInImpl(Context context) {
        mBuild = GoogleBuild.create( context );
    }

    @Override
    public void registerCallback(@NonNull Activity activity, GoogleCallback call) {
        mWRActivity = new WeakReference<>( activity );
        mCallback = call;
    }

    @Override
    public GoogleBuild requestIdToken() { return mBuild.requestIdToken(); }

    @Override
    public GoogleBuild requestEmail() { return mBuild.requestEmail(); }

//    @Override
//    public GoogleBuild requestGender() {
//        return mBuild.requestGender();
//    }

    @Override
    public GoogleBuild requestId() { return mBuild.requestId(); }

    @Override
    public GoogleBuild requestProfile() { return mBuild.requestProfile(); }

    @Override
    public GoogleBuild requestScopes(Scope scope, Scope... scopes) {
        return mBuild.requestScopes( scope, scopes );
    }

    @Override
    public GoogleBuild requestServerAuthCode(String s) { return mBuild.requestServerAuthCode( s ); }

    @Override
    public GoogleBuild requestServerAuthCode(String s, boolean b) {
        return mBuild.requestServerAuthCode( s, b );
    }

    private PeopleService getPeopleService(GoogleSignInAccount gsiAccount, @NonNull String... contactsScope) {
        Context context = mWRActivity.get();
        GoogleAccountCredential gac = GoogleAccountCredential
                .usingOAuth2( context, Arrays.asList( contactsScope ) )
                .setSelectedAccount( gsiAccount.getAccount() );
        return new PeopleService.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(), gac
        ).build();
    }

    private void reqScopePerm(@NonNull Consumer<Boolean> call, GoogleSignInAccount gsiAccount,
                              @NonNull String... contactsScope) {
        mScopePermCall = call;
        Scope[] scopes = new Scope[ contactsScope.length ];
        for( int i = 0; i < scopes.length; i++ ) scopes[ i ] = new Scope( contactsScope[ i ] );

        if( GoogleSignIn.hasPermissions( gsiAccount, scopes ) ) {
            mScopePermCall.accept( true );
        }else {
            GoogleSignIn.requestPermissions(
                    mWRActivity.get(), RC_SCOPE_PERM, gsiAccount, scopes
            );
        }
    }

//    public void startRequestEmail(GoogleSignInAccount gsiAccount, Consumer<List<EmailAddress>> call) {
//        new Thread( () -> {
//            reqScopePerm( result -> {
//                PeopleService ps = getPeopleService( gsiAccount, mBuild.getEmail() );
//                Person person = null;
//                try {
//                    person = ps.people()
//                            .get( "people/me" )
//                            .setRequestMaskIncludeField( "person.email" )
//                            .setFields( "email" )
//                            .execute();
//                }catch(IOException e) {
//                    e.printStackTrace();
//                }
//                if( person == null ) {
//                    if( call != null ) call.accept( new ArrayList<>() );
//                    return;
//                }
//                Person finalPerson = person;
//                HandlerManage.create().post( () -> {
//                    if( call != null ) call.accept( finalPerson.getEmailAddresses() );
//                } );
//            }, gsiAccount, mBuild.getEmail() );
//        } ).start();
//    }
//
//    public void startRequestGenders(GoogleSignInAccount gsiAccount, Consumer<List<Gender>> call) {
//        new Thread( () -> {
//            reqScopePerm( result -> {
//                PeopleService ps = getPeopleService( gsiAccount, mBuild.getGender() );
//                Person person = null;
//                try {
//                    person = ps.people()
//                            .get( "people/me" )
//                            .setRequestMaskIncludeField( "person.genders" )
//                            .setFields( "genders" )
//                            .execute();
//                }catch(IOException e) {
//                    e.printStackTrace();
//                }
//                if( person == null ) {
//                    if( call != null ) call.accept( new ArrayList<>() );
//                    return;
//                }
//                Person finalPerson = person;
//                HandlerManage.create().post( () -> {
//                    if( call != null ) call.accept( finalPerson.getGenders() );
//                } );
//            }, gsiAccount, mBuild.getGender() );
//        } ).start();
//    }

    @Override
    public void onClick(View v) {
        if( mClient == null ) mClient = mBuild.getClient();
        if( mWRActivity != null ) {
            Activity activity = mWRActivity.get();
            if( activity == null ) {
                Log.e(TAG, "Activity is null!");
                return;
            }
            activity.startActivityForResult( mClient.getSignInIntent(), RC_SIGN_IN );
        }else {
            Log.e(TAG, "You need call registerCallback()");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /* scope权限 */
        if( requestCode == RC_SCOPE_PERM && mScopePermCall != null ) {
            mScopePermCall.accept( resultCode == Activity.RESULT_OK );
            return;
        }

        /* 登录 */
        if( requestCode != RC_SIGN_IN || mCallback == null ) return;
        GoogleSignInAccount gsiAccount = null;
        Task<GoogleSignInAccount> task = null;
        try {
            //是否已经授权过
            gsiAccount = GoogleSignIn.getLastSignedInAccount( mWRActivity.get() );
            if( gsiAccount != null ) {
                mCallback.onSuccess( gsiAccount, true );
                return;
            }
            task = GoogleSignIn.getSignedInAccountFromIntent( data );
            //获取授权
            gsiAccount = task.getResult( ApiException.class );
        } catch (ApiException e) {
            e.printStackTrace();
        }

        if( task.isCanceled() ) {
            //取消授权
            mCallback.onCancel();
            return;
        }else if( gsiAccount == null ) {
            //授权出错
            mCallback.onFailure();
            return;
        }
        //授权成功
        mCallback.onSuccess( gsiAccount, false );
    }
}
