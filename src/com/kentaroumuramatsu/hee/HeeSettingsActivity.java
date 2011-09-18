package com.kentaroumuramatsu.hee;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class HeeSettingsActivity extends Hee implements OnClickListener {

	private CommonsHttpOAuthConsumer consumer;
	private ImageButton twitterSettingButton;
	private Resources resource;
	private boolean twitterOAuthFlg;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        resource = getResources();
        twitterSettingButton = (ImageButton) findViewById(R.id.twitter_setting_button);
        twitterSettingButton.setOnClickListener(this);
        consumer = new CommonsHttpOAuthConsumer(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET);
        if(isTwitterOAuthStatus(this, consumer)) {
        	twitterOAuthFlg = true;
        	twitterSettingButton.setBackgroundDrawable(resource.getDrawable(R.drawable.twitter_button_on));
        } else {
        	twitterOAuthFlg = false;
        	twitterSettingButton.setBackgroundDrawable(resource.getDrawable(R.drawable.twitter_button_off));
        }
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
		}
    }
}