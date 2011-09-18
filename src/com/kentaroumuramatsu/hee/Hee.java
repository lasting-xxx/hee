package com.kentaroumuramatsu.hee;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.widget.Toast;

import com.kentaroumuramatsu.hee.Constants;

abstract class Hee extends Activity {
	
    protected CommonsHttpOAuthConsumer consumer;
    protected OAuthProvider provider;
	
	public boolean isTwitterOAuthStatus (Activity activity, CommonsHttpOAuthConsumer consumer) {
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
	
	public void setDefalutPostContents() {
		SharedPreferences sp = getSharedPreferences(Constants.TWITTER_POST_CONTENTS, MODE_PRIVATE);
     	Editor editor = sp.edit();
     	editor.putString(Constants.TWITTER_POST_CONTENTS1, getString(R.string.twitter_strings_kanojo));
     	editor.putString(Constants.TWITTER_POST_CONTENTS2, getString(R.string.twitter_strings_hee));
     	editor.putString(Constants.TWITTER_POST_CONTENTS3, getString(R.string.twitter_strings_yoji));
     	editor.putString(Constants.TWITTER_POST_CONTENTS4, getString(R.string.twitter_strings_nya));
     	editor.commit();
    }
	
	public void setPostContent(String key, String content) {
		SharedPreferences sp = getSharedPreferences(Constants.TWITTER_POST_CONTENTS, MODE_PRIVATE);
     	Editor editor = sp.edit();
     	editor.putString(key, content);
     	editor.commit();
	}
	
	public String getPostContents(String key) {
    	SharedPreferences sp = getSharedPreferences(Constants.TWITTER_POST_CONTENTS, MODE_PRIVATE);
    	return sp.getString(key, "");
	}
	
	public void doOauth() {
        try {
            //トークンの読み込み
            SharedPreferences pref=getSharedPreferences(Constants.TWITTER_TOKEN,MODE_PRIVATE);
            String token      =pref.getString(Constants.TWITTER_TOKEN_PUBLIC,"");
            String tokenSecret=pref.getString(Constants.TWITTER_TOKEN_SECRET,"");
            //認証済み
            if (token.length()>0 && tokenSecret.length()>0) {
                consumer.setTokenWithSecret(token,tokenSecret);
            } 
            //認証処理のためブラウザ起動
            else {
                String authUrl = provider.retrieveRequestToken(consumer, Constants.TWITTER_CALLBACK_URL);
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
	
}
