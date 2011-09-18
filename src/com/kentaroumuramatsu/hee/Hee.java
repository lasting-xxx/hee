package com.kentaroumuramatsu.hee;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.kentaroumuramatsu.hee.Constants;

abstract class Hee extends Activity {
	
	public static boolean isTwitterOAuthStatus (Activity activity, CommonsHttpOAuthConsumer consumer) {
		//トークンの読み込み
        SharedPreferences pref = activity.getSharedPreferences(Constants.TWITTER_TOKEN, MODE_PRIVATE);
        String token      =pref.getString(Constants.TWITTER_TOKEN_PUBLIC,"");
        String tokenSecret=pref.getString(Constants.TWITTER_TOKEN_SECRET,"");
        //認証済み
        if (token.length()>0 && tokenSecret.length()>0) {
            return true;
        } else {
            return false;
        }
	}
	
	public String getTwitterToken () {
		SharedPreferences pref = getSharedPreferences(Constants.TWITTER_TOKEN, MODE_PRIVATE);
        return pref.getString(Constants.TWITTER_TOKEN_PUBLIC,"");
	}
	
	public String getTwitterTokenSecret () {
		SharedPreferences pref = getSharedPreferences(Constants.TWITTER_TOKEN, MODE_PRIVATE);
        return pref.getString(Constants.TWITTER_TOKEN_SECRET,"");
	}
	
	public boolean getIsTwitterPostUserAuthorize() {
    	SharedPreferences sp = getSharedPreferences(Constants.TWITTER_POST_USER_AUTHORIZE, MODE_PRIVATE);
    	return sp.getBoolean(Constants.TWITTER_POST_USER_AUTHORIZE, false);
	}
	
	public void setIsTwitterPostUserAuthorize(boolean auth) {
		SharedPreferences sp = getSharedPreferences(Constants.TWITTER_POST_USER_AUTHORIZE, MODE_PRIVATE);
     	Editor editor = sp.edit();
     	editor.putBoolean(Constants.TWITTER_POST_USER_AUTHORIZE, auth);
     	editor.commit();
	}
}
