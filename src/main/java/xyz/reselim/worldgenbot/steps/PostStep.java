package xyz.reselim.worldgenbot.steps;

import java.nio.file.Files;

import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Next;
import xyz.reselim.worldgenbot.helpers.ConfigHelper;
import xyz.reselim.worldgenbot.helpers.PathHelper;
import xyz.reselim.worldgenbot.helpers.TextHelper;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.conf.ConfigurationBuilder;

public class PostStep implements Step {
	public void perform(Context context, Next next) {
		TwitterFactory twitterFactory = new TwitterFactory(
			new ConfigurationBuilder()
				.setOAuthConsumerKey(ConfigHelper.CONFIG.twitter.apiKey)
				.setOAuthConsumerSecret(ConfigHelper.CONFIG.twitter.apiKeySecret)
				.setOAuthAccessToken(ConfigHelper.CONFIG.twitter.accessToken)
				.setOAuthAccessTokenSecret(ConfigHelper.CONFIG.twitter.accessTokenSecret)
				.build()
		);

		Twitter twitter = twitterFactory.getInstance();

		try {
			UploadedMedia media = twitter.uploadMediaChunked("video.mp4", Files.newInputStream(PathHelper.videoTranscodedOutput().toPath()));

			StatusUpdate status = new StatusUpdate(TextHelper.getRawText());
			status.setMediaIds(media.getMediaId());

			twitter.updateStatus(status);
		} catch(Exception error) {
			throw new RuntimeException(error);
		}

		next.next();
	}
}
