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
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class HeeActivity extends Activity implements OnClickListener{

    private CommonsHttpOAuthConsumer consumer;
    private OAuthProvider provider;

    // Twitter関連
    public final static String TWITTER_CALLBACK_URL = "hee://hee";
    public final static String TWITTER_CONSUMER_KEY = "6TWCWs1V0Of2C7agwWL4dA";
    public final static String TWITTER_CONSUMER_SECRET = "CmdYYpubN9RhWYflTmmW58TvOIPLraIFAAFRiplrg";
    public final static String TWITTER_REQUEST_TOKEN = "http://twitter.com/oauth/request_token";
    public final static String TWITTER_ACCESS_TOKEN = "http://twitter.com/oauth/access_token";
    public final static String TWITTER_AUTHORIZE = "http://twitter.com/oauth/authorize";
    public final static String TWITTER_USER_STATUS = "http://twitter.com/account/verify_credentials.xml";
    public final static String TWITTER_FOLLOWING = "http://api.twitter.com/1/statuses/friends.xml";
    public final static String TWITTER_FOLLOWER = "https://api.twitter.com/1/statuses/followers.xml";
    public final static String TWITTER_UPDATE = "https://api.twitter.com/1/statuses/update.xml";
    public final static String TWITTER_TOKEN = "twitter_token";
    public final static String TWITTER_TOKEN_PUBLIC = "twitter_token";
    public final static String TWITTER_TOKEN_SECRET = "twitter_token_secret";
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
        doOauth();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri=intent.getData();
        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
            String verifier=uri.getQueryParameter(
                    oauth.signpost.OAuth.OAUTH_VERIFIER);
            try {
                provider.retrieveAccessToken(consumer,verifier);
                //トークンの書き込み
                SharedPreferences pref=getSharedPreferences(TWITTER_TOKEN,MODE_PRIVATE);
                SharedPreferences.Editor editor=pref.edit();
                editor.putString(TWITTER_TOKEN_PUBLIC,consumer.getToken());
                editor.putString(TWITTER_TOKEN_SECRET,consumer.getTokenSecret());
                editor.commit();

            } catch(Exception e){
                Log.d("",e.getMessage());
            }
        } else {
            consumer = new CommonsHttpOAuthConsumer(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);

            //トークンの読み込み
            SharedPreferences pref=getSharedPreferences(TWITTER_TOKEN,MODE_PRIVATE);
            String token      =pref.getString(TWITTER_TOKEN_PUBLIC,"");
            String tokenSecret=pref.getString(TWITTER_TOKEN_SECRET,"");
            //認証済み
            if (token.length()>0 && tokenSecret.length()>0) {
                consumer.setTokenWithSecret(token,tokenSecret);
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
            PostTwitterAsync postMentionAsync = new PostTwitterAsync(this);
            postMentionAsync.execute();
        }
    }

    private void doOauth() {
        try {
            consumer = new CommonsHttpOAuthConsumer(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
            provider = new DefaultOAuthProvider(TWITTER_REQUEST_TOKEN, TWITTER_ACCESS_TOKEN, TWITTER_AUTHORIZE);

            //トークンの読み込み
            SharedPreferences pref=getSharedPreferences(TWITTER_TOKEN,MODE_PRIVATE);
            String token      =pref.getString(TWITTER_TOKEN_PUBLIC,"");
            String tokenSecret=pref.getString(TWITTER_TOKEN_SECRET,"");
            //認証済み
            if (token.length()>0 && tokenSecret.length()>0) {
                consumer.setTokenWithSecret(token,tokenSecret);
            } 
            //認証処理のためブラウザ起動
            else {
                String authUrl = provider.retrieveRequestToken(consumer, TWITTER_CALLBACK_URL);
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
                HttpPost post = new HttpPost(TWITTER_UPDATE); 
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