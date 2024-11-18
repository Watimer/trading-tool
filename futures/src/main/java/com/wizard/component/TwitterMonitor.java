package com.wizard.component;

import org.apache.logging.log4j.status.StatusListener;
import org.ehcache.config.builders.ConfigurationBuilder;
import org.springframework.context.annotation.Configuration;
import twitter4j.*;
import twitter4j.v1.FilterQuery;
import twitter4j.v1.StallWarning;
import twitter4j.v1.StatusDeletionNotice;
import twitter4j.v1.TwitterStream;

import javax.annotation.PostConstruct;

/**
 * @author 岳耀栎
 * @date 2024-11-18
 * @desc
 */
@Configuration
public class TwitterMonitor {


	@PostConstruct
	public void getInstance(){
		// 配置 Twitter API
		ConfigurationBuilder cb = ConfigurationBuilder.newConfigurationBuilder(ConfigurationBuilder.newConfigurationBuilder().build());
			cb.setDebugEnabled(true)
				.setOAuthConsumerKey("your_consumer_key")
				.setOAuthConsumerSecret("your_consumer_secret")
				.setOAuthAccessToken("your_access_token")
				.setOAuthAccessTokenSecret("your_access_token_secret");
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

		Twitter.getInstance().v1().

		TwitterAPIMonitor

		twitterStream.addListener(new StatusListener() {
			@Override
			public void onStatus(Status status) {
				System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {}

			@Override
			public void onStallWarning(StallWarning warning) {}
		});

		long[] userIds = {123456789L}; // 替换为目标用户的 Twitter 用户 ID
		twitterStream.filter(new FilterQuery().follow(userIds));
	}
}
