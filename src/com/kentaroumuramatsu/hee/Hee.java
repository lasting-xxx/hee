package com.kentaroumuramatsu.hee;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.Activity;
import android.content.SharedPreferences;
import com.kentaroumuramatsu.hee.Constants;

public class Hee {
	public static boolean isTwitterOAuthStatus (Activity activity, CommonsHttpOAuthConsumer consumer) {
		//トークンの読み込み
        SharedPreferences pref = activity.getSharedPreferences(Constants.TWITTER_TOKEN, activity.MODE_PRIVATE);
        String token      =pref.getString(Constants.TWITTER_TOKEN_PUBLIC,"");
        String tokenSecret=pref.getString(Constants.TWITTER_TOKEN_SECRET,"");
        //認証済み
        if (token.length()>0 && tokenSecret.length()>0) {
            return true;
        } else {
            return false;
        }
	}
	
	public static String getTwitterToken (Activity activity) {
		SharedPreferences pref = activity.getSharedPreferences(Constants.TWITTER_TOKEN, activity.MODE_PRIVATE);
        return pref.getString(Constants.TWITTER_TOKEN_PUBLIC,"");
	}
	
	public static String getTwitterTokenSecret (Activity activity) {
		SharedPreferences pref = activity.getSharedPreferences(Constants.TWITTER_TOKEN, activity.MODE_PRIVATE);
        return pref.getString(Constants.TWITTER_TOKEN_SECRET,"");
	}
	
}
