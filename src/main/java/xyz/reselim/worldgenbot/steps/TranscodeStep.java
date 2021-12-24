package xyz.reselim.worldgenbot.steps;

import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Next;
import xyz.reselim.worldgenbot.helpers.PathHelper;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.filters.ScaleFilter;
import ws.schild.jave.info.VideoSize;

public class TranscodeStep implements Step {
	public void perform(Context context, Next next) {
		// Foone my beloved
		// http://web.archive.org/web/20211212201710/https://twitter.com/Foone/status/997341145146449920

		VideoAttributes videoAttributes = new VideoAttributes();
		videoAttributes.setCodec("libx264");
		videoAttributes.setBitRate(1_024_000);
		videoAttributes.setPixelFormat("yuv420p");
		videoAttributes.setFrameRate(30);
		videoAttributes.addFilter(new ScaleFilter(new VideoSize(640, -1)));

		AudioAttributes audioAttributes = new AudioAttributes();
		audioAttributes.setCodec("aac");
		audioAttributes.setBitRate(44_100);
		audioAttributes.setChannels(2);

		EncodingAttributes attributes = new EncodingAttributes();
		attributes.setAudioAttributes(null);
		attributes.setVideoAttributes(videoAttributes);
		attributes.setOutputFormat("mp4");

		Encoder encoder = new Encoder();

		try {
			encoder.encode(new MultimediaObject(PathHelper.videoDirectOutput()), PathHelper.videoTranscodedOutput(), attributes);
		} catch (Exception error) {
			throw new RuntimeException(error);
		}

		next.next();
	}
}
