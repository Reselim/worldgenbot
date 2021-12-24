package xyz.reselim.worldgenbot.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public final class TextHelper {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	private static final int CORNER_OFFSET_X = 10;
	private static final int CORNER_OFFSET_Y = 10;
	private static final int SPACING = 2;

	public static List<TextField> getTextFields() {
		List<TextField> fields = new ArrayList<>();

		String seed = String.valueOf(CLIENT.getServer().getOverworld().getSeed());
		fields.add(
			new TextField(
				"seed",
				seed
			)
		);
		
		Vec3d position = CLIENT.player.getPos();
		fields.add(
			new TextField(
				"coords",
				String.format(
					"%d / %d / %d",
					(long) Math.floor(position.x),
					(long) Math.floor(position.y),
					(long) Math.floor(position.z)
				)
			)
		);

		String version = SharedConstants.getGameVersion().getName();
		fields.add(
			new TextField(
				"version",
				version
			)
		);

		return fields;
	}

	public static String getRawText() {
		return StringUtils.join(
			getTextFields().stream().map(field -> field.toString()).collect(Collectors.toList()),
			"\n"
		);
	}

	public static List<Text> getFormattedText() {
		return getTextFields().stream().map((field) -> {
			MutableText nameText = new LiteralText(field.name + ": ").setStyle(Style.EMPTY.withColor(0xF0F0F0));
			MutableText valueText = new LiteralText(field.value).setStyle(Style.EMPTY.withBold(true));
			return nameText.append(valueText);
		}).collect(Collectors.toList());
	}

	public static void render(MatrixStack matrixStack) {
		Window window = CLIENT.getWindow();
		TextRenderer textRenderer = CLIENT.textRenderer;

		List<Text> texts = getFormattedText();
		int offset = 0;
		for (Text text : texts) {
			textRenderer.drawWithShadow(matrixStack, text, CORNER_OFFSET_X, CORNER_OFFSET_Y + offset, 0xFFFFFF);
			offset += textRenderer.fontHeight + SPACING;
		}

		textRenderer.drawWithShadow(
			matrixStack,
			new LiteralText(ConfigHelper.CONFIG.overlay.watermark),
			CORNER_OFFSET_X,
			window.getScaledHeight() * 2 - (CORNER_OFFSET_Y + textRenderer.fontHeight),
			0xFFFFFF
		);
	}

	public static class TextField {
		public final String name;
		public final String value;

		public TextField(String initName, String initValue) {
			name = initName;
			value = initValue;
		}

		public String toString() {
			return name + ": " + value;
		}
	}
}
