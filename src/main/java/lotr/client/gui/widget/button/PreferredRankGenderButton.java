package lotr.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.MiddleEarthFactionsScreen;
import lotr.common.data.*;
import lotr.common.fac.RankGender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class PreferredRankGenderButton extends Button {
	private final RankGender rankGender;

	public PreferredRankGenderButton(int xIn, int yIn, RankGender gender, IPressable onPress) {
		super(xIn, yIn, 12, 12, StringTextComponent.EMPTY, onPress);
		rankGender = gender;
	}

	@Override
	protected int getYImage(boolean hovered) {
		return hovered ? 2 : isCurrentlyPreferredGender() ? 1 : 0;
	}

	private boolean isCurrentlyPreferredGender() {
		return getClientPlayerData().getMiscData().getPreferredRankGender() == rankGender;
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bind(MiddleEarthFactionsScreen.FACTIONS_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		int uMin = 60 + rankGender.ordinal() * width;
		int yOffset = getYImage(isHovered());
		this.blit(matStack, x, y, uMin, 142 + yOffset * height, width, height);
		renderBg(matStack, mc, mouseX, mouseY);
	}

	private static LOTRPlayerData getClientPlayerData() {
		Minecraft mc = Minecraft.getInstance();
		return LOTRLevelData.clientInstance().getData(mc.player);
	}

	public static void sendPreferenceToServer(Button button) {
		PreferredRankGenderButton thisButton = (PreferredRankGenderButton) button;
		getClientPlayerData().getMiscData().setPreferredRankGenderAndSendToServer(thisButton.rankGender);
	}
}
