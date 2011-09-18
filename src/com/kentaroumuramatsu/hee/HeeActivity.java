package com.kentaroumuramatsu.hee;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import com.kentaroumuramatsu.hee.R;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import com.kentaroumuramatsu.hee.Constants;

public class HeeActivity extends Hee implements OnClickListener {

    private CommonsHttpOAuthConsumer consumer;
    private OAuthProvider provider;
    private String twitterStrings;
    private ImageView buttonHee;
    private MediaPlayer mpHee = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mpHee = MediaPlayer.create(this, R.raw.hee);
        buttonHee = (ImageView) findViewById(R.id.buttonHee);
        buttonHee.setOnClickListener(this);
        consumer = new CommonsHttpOAuthConsumer(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET);
        provider = new DefaultOAuthProvider(Constants.TWITTER_REQUEST_TOKEN, Constants.TWITTER_ACCESS_TOKEN, Constants.TWITTER_AUTHORIZE);
        
        new AlertDialog.Builder(this)
        .setIcon(R.drawable.icon)
        .setTitle(getString(R.string.twitter_post_auth_title))
        .setMessage(getString(R.string.twitter_post_auth_content))
        .setPositiveButton(getString(R.string.twitter_post_auth_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /* ここにYESの処理 */
            	// ユーザーの投稿認証を保存
            	setIsTwitterPostUserAuthorize(true);
            	//トークンの読み込み
                SharedPreferences pref = getSharedPreferences(Constants.TWITTER_TOKEN, MODE_PRIVATE);
                String token      =pref.getString(Constants.TWITTER_TOKEN_PUBLIC,"");
                String tokenSecret=pref.getString(Constants.TWITTER_TOKEN_SECRET,"");
            	if(token.length() == 0 && tokenSecret.length() == 0) {
            		doOauth();
            	}
            }
        })
        .setNegativeButton(getString(R.string.twitter_post_auth_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /* ここにNOの処理 */
            	// ユーザーの投稿認証を保存
            	setIsTwitterPostUserAuthorize(false);
            }
        })
        .show();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem settingItem = menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, getString(R.string.menu_setting));
        settingItem.setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }
    
    // メニューが選択された時の処理
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == 1) {
    		startActivity(new Intent(this, HeeSettingsActivity.class));
    		return true;
    	}
        return false;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri=intent.getData();
        if (uri != null && uri.toString().startsWith(Constants.TWITTER_CALLBACK_URL)) {
            String verifier=uri.getQueryParameter(
                    oauth.signpost.OAuth.OAUTH_VERIFIER);
            try {
                provider.retrieveAccessToken(consumer,verifier);
                //トークンの書き込み
                SharedPreferences pref=getSharedPreferences(Constants.TWITTER_TOKEN,MODE_PRIVATE);
                SharedPreferences.Editor editor=pref.edit();
                editor.putString(Constants.TWITTER_TOKEN_PUBLIC,consumer.getToken());
                editor.putString(Constants.TWITTER_TOKEN_SECRET,consumer.getTokenSecret());
                editor.commit();

            } catch(Exception e){
                Log.d("",e.getMessage());
            }
        } else {
            //認証済み
            if (isTwitterOAuthStatus(this, consumer)) {
                consumer.setTokenWithSecret(getTwitterToken(), getTwitterTokenSecret());
            } else {
                finish();
            }
        }
    }

    public void onClick(final View v) {
        int id = v.getId();
        if (id == R.id.buttonHee) {
            mpHee.start();
            if(getRandomInt() == 1) {
                twitterStrings = getString(R.string.twitter_strings_kanojo) + new Date().toString();
            } else if(getRandomInt() == 2){
                twitterStrings = getString(R.string.twitter_strings_yoji) + new Date().toString();
            } else if(getRandomInt() == 3){
                twitterStrings = getString(R.string.twitter_strings_nya) + new Date().toString();
            } else {
                twitterStrings = getString(R.string.twitter_strings_hee) + new Date().toString();
            }
            PostTwitterAsync postTwitterAsync = new PostTwitterAsync(this);
//            postTwitterAsync.execute();
        }
    }

    private void doOauth() {
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

    private static int getRandomInt() {
        int res = 0;
        res = (int)Math.floor(Math.random() * (10-1+1))+1;
        return res;
    }
    
    private class PostTwitterAsync extends AsyncTask<String, Integer, List<BindData>> {

        private HeeActivity heeActivity;

        public PostTwitterAsync(HeeActivity heeActivity) {
            this.heeActivity = heeActivity;
        }

        @Override
        protected List<BindData> doInBackground(String... params) {
            try {
                HttpPost post = new HttpPost(Constants.TWITTER_UPDATE); 
                final List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                postParams.add(new BasicNameValuePair("status",twitterStrings));
                post.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
                post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE,false);
                consumer.sign(post);
                DefaultHttpClient http=new DefaultHttpClient();
                http.execute(post);
            } catch (Exception e) {
                Toast.makeText(this.heeActivity, e.getMessage(),Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }

    class BindData {
        String text;
        Bitmap imageResourceId;
        String userId;

        public BindData(String text, Bitmap id, String userId) {
            this.text = text;
            this.imageResourceId = id;
            this.userId = userId;
        }
    }
}