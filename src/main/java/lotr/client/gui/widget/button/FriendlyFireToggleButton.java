package lotr.client.gui.widget.button;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.MiddleEarthFactionsScreen;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FriendlyFireToggleButton extends Button {
	private static final List TOOLTIP_ENABLED = generateTooltip("enabled");
	private static final List TOOLTIP_DISABLED = generateTooltip("disabled");

	public FriendlyFireToggleButton(int xIn, int yIn, IPressable onPress) {
		super(xIn, yIn, 16, 16, StringTextComponent.EMPTY, onPress);
	}

	@Override
	protected int getYImage(boolean hovered) {
		return isFriendlyFireEnabled() ? 1 : 0;
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bind(MiddleEarthFactionsScreen.FACTIONS_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		int yOffset = getYImage(isHovered());
		this.blit(matStack, x, y, 84, 142 + yOffset * height, width, height);
		renderBg(matStack, mc, mouseX, mouseY);
	}

	private static List generateTooltip(String state) {
		return IntStream.of(1, 2).mapToObj(i -> String.format("gui.lotr.factions.friendlyFire.%s.%d", state, i)).map(TranslationTextComponent::new).collect(Collectors.toList());
	}

	private static LOTRPlayerData getClientPlayerData() {
		Minecraft mc = Minecraft.getInstance();
		return LOTRLevelData.clientInstance().getData(mc.player);
	}

	public static List getTooltipLines() {
		return isFriendlyFireEnabled() ? TOOLTIP_ENABLED : TOOLTIP_DISABLED;
	}

	public static boolean isFriendlyFireEnabled() {
		return getClientPlayerData().getAlignmentData().isFriendlyFireEnabled();
	}

	public static void sendToggleToServer(Button button) {
		getClientPlayerData().getAlignmentData().toggleFriendlyFireEnabledAndSendToServer();
	}
}
