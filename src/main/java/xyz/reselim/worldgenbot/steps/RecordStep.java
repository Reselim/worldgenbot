package xyz.reselim.worldgenbot.steps;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zakgof.velvetvideo.IMuxer;
import com.zakgof.velvetvideo.IVelvetVideoLib;
import com.zakgof.velvetvideo.IVideoEncoderBuilder;
import com.zakgof.velvetvideo.IVideoEncoderStream;
import com.zakgof.velvetvideo.impl.VelvetVideoLib;

import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Mod;
import xyz.reselim.worldgenbot.Next;
import xyz.reselim.worldgenbot.helpers.ConfigHelper;
import xyz.reselim.worldgenbot.helpers.PathHelper;
import xyz.reselim.worldgenbot.helpers.PreviewHelper;
import xyz.reselim.worldgenbot.helpers.TextHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class RecordStep implements Step {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public void perform(Context context, Next next) {
		Recording recording = new Recording(PathHelper.videoDirectOutput());

		SimpleFramebuffer framebuffer = new SimpleFramebuffer(
			ConfigHelper.CONFIG.video.getScaledWidth(),
			ConfigHelper.CONFIG.video.getScaledHeight(),
			true,
			false
		);

		GameRenderer gameRenderer = CLIENT.gameRenderer;
		WorldRenderer worldRenderer = CLIENT.worldRenderer;
		Window window = CLIENT.getWindow();
		ClientPlayerEntity player = CLIENT.player;

		gameRenderer.setRenderingPanorama(true);
		gameRenderer.setBlockOutlineEnabled(false);
		gameRenderer.setRenderHand(false);
		worldRenderer.reloadTransparencyShader();

		int lastFramebufferWidth = window.getFramebufferWidth();
		int lastFramebufferHeight = window.getFramebufferHeight();
		float lastYaw = player.getYaw();
		float lastPitch = player.getPitch();
		boolean lastHudHidden = CLIENT.options.hudHidden;
		double lastScaleFactor = window.getScaleFactor();

		CLIENT.options.hudHidden = true;
		window.setScaleFactor(ConfigHelper.CONFIG.overlay.scale);

		for (int index = 0; index < ConfigHelper.CONFIG.video.getFrameCount(); index++) {
			Mod.LOGGER.info("Capturing frame {}/{}", index + 1, ConfigHelper.CONFIG.video.getFrameCount());

			window.setFramebufferWidth(ConfigHelper.CONFIG.video.getScaledWidth());
			window.setFramebufferHeight(ConfigHelper.CONFIG.video.getScaledHeight());

			PreviewHelper.step(index);
			
			framebuffer.beginWrite(true);

			// Render world
            gameRenderer.renderWorld(1f, 0l, new MatrixStack());
			RenderSystem.clear(256, false);

			// Render GUI
        	Matrix4f guiMatrix4f = Matrix4f.projectionMatrix(
				0.0f, (float) ((double) window.getFramebufferWidth() / window.getScaleFactor()),
				0.0f, (float) ((double) window.getFramebufferHeight() / window.getScaleFactor()),
				1000.0f, 3000.0f
			);
        	RenderSystem.setProjectionMatrix(guiMatrix4f);
			MatrixStack matrixStack = RenderSystem.getModelViewStack();
			matrixStack.loadIdentity();
			matrixStack.translate(0.0, 0.0, -2000.0);
			RenderSystem.applyModelViewMatrix();
			DiffuseLighting.enableGuiDepthLighting();
			RenderSystem.enableBlend();
			TextHelper.render(new MatrixStack());
			RenderSystem.clear(256, false);

			recording.encode(ScreenshotRecorder.takeScreenshot(framebuffer));

			try {
				Thread.sleep(10);
			} catch(InterruptedException error) {}
		}

		CLIENT.options.hudHidden = lastHudHidden;
		window.setScaleFactor(lastScaleFactor);

		gameRenderer.setRenderingPanorama(false);
		gameRenderer.setBlockOutlineEnabled(true);
		gameRenderer.setRenderHand(true);
		worldRenderer.reloadTransparencyShader();
		CLIENT.getFramebuffer().beginWrite(true);
		framebuffer.delete();

		window.setFramebufferWidth(lastFramebufferWidth);
		window.setFramebufferHeight(lastFramebufferHeight);
		player.setYaw(lastYaw);
		player.setPitch(lastPitch);

		recording.finish();

		next.next();
	}

	public static class Recording {
		private final IMuxer muxer;
		private final IVideoEncoderStream stream;

		public Recording(File file) {
			IVelvetVideoLib lib = VelvetVideoLib.getInstance();

			IVideoEncoderBuilder encoderBuilder = lib.videoEncoder("libx264")
				.framerate(ConfigHelper.CONFIG.video.frameRate)
				.dimensions(ConfigHelper.CONFIG.video.getScaledWidth(), ConfigHelper.CONFIG.video.getScaledHeight())
				.bitrate(1_000_000);
			
			muxer = lib.muxer("mp4").videoEncoder(encoderBuilder).build(file);
			stream = muxer.videoEncoder(0);
		}

		public void encode(BufferedImage image) {
			try {
				stream.encode(image);
			} catch (Exception error) {
				throw new RuntimeException(error);
			}
		}
	
		public void encode(NativeImage nativeImage) {
			int width = nativeImage.getWidth();
			int height = nativeImage.getHeight();
	
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
			int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					data[y * width + x] = nativeImage.getColor(x, y);
				}
			}
	
			encode(image);
		}

		public void finish() {
			muxer.close();
		}
	}
}
