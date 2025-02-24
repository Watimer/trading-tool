package com.wizard.component;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.MultiTweetLookupResponse;
import com.twitter.clientlib.model.ResourceUnauthorizedProblem;
import com.twitter.clientlib.model.SingleTweetLookupResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 岳耀栎
 * @date 2024-11-18
 * @desc
 */
public class TwitterMonitor {

	public static void main(String[] args) {
		String bearerToken = "AAAAAAAAAAAAAAAAAAAAAPoqxAEAAAAAIn1XEesmqFb1OaJGAmJAAGFnNsc%3DCGPyLHWEDPJGkYHdXSnyMfCubQg1iTx4MjRTE4FQ1lZMxQHYL9"; // 替换为你的 Bearer Token

		// Instantiate library client
		TwitterApi apiInstance = new TwitterApi();

		// Instantiate auth credentials (App-only example)
		TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(bearerToken);

		// Pass credentials to library client
		apiInstance.setTwitterCredentials(credentials);

		List<String> ids = Arrays.asList("20","1519781379172495360", "1519781381693353984");
		Set<String> tweetFields = new HashSet<>();
		tweetFields.add("author_id");
		tweetFields.add("id");
		tweetFields.add("created_at");


		try {
			MultiTweetLookupResponse result = apiInstance.tweets().findTweetsById(ids,new HashSet<>(),tweetFields,new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
			System.out.println(result);
		} catch (ApiException e) {
			System.err.println("Exception when calling TweetsApi#findTweetsById");
			System.err.println("Status code: " + e.getCode());
			System.err.println("Reason: " + e.getResponseBody());
			System.err.println("Response headers: " + e.getResponseHeaders());
			e.printStackTrace();
		}
	}


	//@PostConstruct
	//public void getInstance(){
	//	// 配置 Twitter API
	//	ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
	//	configurationBuilder.setDebugEnabled(true)
	//			.setOAuthConsumerKey("your-consumer-key")
	//			.setOAuthConsumerSecret("your-consumer-secret")
	//			.setOAuthAccessToken("your-access-token")
	//			.setOAuthAccessTokenSecret("your-access-token-secret");
	//	// 2. 初始化 Twitter Stream
	//	TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
	//
	//	Status
	//
	//	// 3. 监听器定义
	//	StatusListener listener = new StatusListener() {
	//		@Override
	//		public void onStatus(Status status) {
	//			System.out.println("New tweet from @" + status.getUser().getScreenName() + ": " + status.getText());
	//		}
	//
	//		@Override
	//		public void onException(Exception ex) {
	//			ex.printStackTrace();
	//		}
	//
	//		@Override
	//		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	//			System.out.println("Tweet deleted: " + statusDeletionNotice.getStatusId());
	//		}
	//
	//		@Override
	//		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	//			System.out.println("Track limitation notice: " + numberOfLimitedStatuses);
	//		}
	//
	//		@Override
	//		public void onScrubGeo(long userId, long upToStatusId) {
	//			System.out.println("Scrub geo event: UserId=" + userId + ", UpToStatusId=" + upToStatusId);
	//		}
	//
	//		@Override
	//		public void onStallWarning(StallWarning warning) {
	//			System.out.println("Stall warning: " + warning.getMessage());
	//		}
	//	};
	//
	//	// 4. 添加监听器
	//	twitterStream.addListener(listener);
	//
	//	// 5. 过滤特定用户
	//	FilterQuery filterQuery = new FilterQuery();
	//	filterQuery.follow(new long[]{123456789L}); // 替换为目标用户的 Twitter 用户 ID
	//	twitterStream.filter(filterQuery);
	//}
}
