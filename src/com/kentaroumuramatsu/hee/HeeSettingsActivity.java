package com.kentaroumuramatsu.hee;

import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


public class HeeSettingsActivity extends Hee implements OnClickListener {

	private ImageButton twitterSettingButton;
	private Button buttonSettingSave;
	private Resources resource;
	private boolean twitterOAuthFlg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		consumer = new CommonsHttpOAuthConsumer(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET);
		provider = new DefaultOAuthProvider(Constants.TWITTER_REQUEST_TOKEN, Constants.TWITTER_ACCESS_TOKEN, Constants.TWITTER_AUTHORIZE);
		resource = getResources();
		buttonSettingSave = (Button) findViewById(R.id.buttonSettingSave);
		buttonSettingSave.setOnClickListener(this);
		twitterSettingButton = (ImageButton) findViewById(R.id.twitter_setting_button);
		twitterSettingButton.setOnClickListener(this);
		if(getIsTwitterPostUserAuthorize()) {
			twitterOAuthFlg = true;
			twitterSettingButton.setBackgroundDrawable(resource.getDrawable(R.drawable.twitter_button_on));
		} else {
			twitterOAuthFlg = false;
			twitterSettingButton.setBackgroundDrawable(resource.getDrawable(R.drawable.twitter_button_off));
		}
		EditText et1 = (EditText)findViewById(R.id.editTwitterPostOne);
		et1.setText(getPostContents(Constants.TWITTER_POST_CONTENTS1));
		EditText et2 = (EditText)findViewById(R.id.editTwitterPostTwo);
		et2.setText(getPostContents(Constants.TWITTER_POST_CONTENTS2));
		EditText et3 = (EditText)findViewById(R.id.editTwitterPostThree);
		et3.setText(getPostContents(Constants.TWITTER_POST_CONTENTS3));
		EditText et4 = (EditText)findViewById(R.id.editTwitterPostFour);
		et4.setText(getPostContents(Constants.TWITTER_POST_CONTENTS4));
	}

	public void onClick(final View v) {
		int id = v.getId();
		if (id == R.id.twitter_setting_button) {
			if(twitterOAuthFlg) {
				twitterOAuthFlg = false;
				twitterSettingButton.setBackgroundDrawable(resource.getDrawable(R.drawable.twitter_button_off));
			} else {
				twitterOAuthFlg = true;
				twitterSettingButton.setBackgroundDrawable(resource.getDrawable(R.drawable.twitter_button_on));
			}
		} else if (id == R.id.buttonSettingSave){
			EditText et1 = (EditText)findViewById(R.id.editTwitterPostOne);
			EditText et2 = (EditText)findViewById(R.id.editTwitterPostTwo);
			EditText et3 = (EditText)findViewById(R.id.editTwitterPostThree);
			EditText et4 = (EditText)findViewById(R.id.editTwitterPostFour);
			setPostContent(Constants.TWITTER_POST_CONTENTS1, et1.getText().toString());
			setPostContent(Constants.TWITTER_POST_CONTENTS2, et2.getText().toString());
			setPostContent(Constants.TWITTER_POST_CONTENTS3, et3.getText().toString());
			setPostContent(Constants.TWITTER_POST_CONTENTS4, et4.getText().toString());
			if(twitterOAuthFlg) {
				setIsTwitterPostUserAuthorize(true);
				SharedPreferences pref = getSharedPreferences(Constants.TWITTER_TOKEN, MODE_PRIVATE);
				String token      =pref.getString(Constants.TWITTER_TOKEN_PUBLIC,"");
				String tokenSecret=pref.getString(Constants.TWITTER_TOKEN_SECRET,"");
				if(token.length() == 0 && tokenSecret.length() == 0) {
					doOauth();
				}
				finish();
			} else {
				setIsTwitterPostUserAuthorize(false);
				finish();
			}
		}
	}
}