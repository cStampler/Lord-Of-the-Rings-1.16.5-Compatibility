package lotr.client.gui;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.util.*;
import lotr.client.gui.widget.button.RedBookButton;
import lotr.common.data.PlayerMessageType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.*;
import net.minecraft.util.text.*;

public class PlayerMessageScreen extends BasicIngameScreen {
	private static final ResourceLocation MESSAGE_TEXTURE = new ResourceLocation("lotr", "textures/gui/message.png");
	private final PlayerMessageType messageType;
	private final boolean isCommandSent;
	private final String displayText;
	private int guiLeft;
	private int guiTop;
	private Button buttonDismiss;
	private int buttonTimer = 60;
	private final AlignmentRenderer alignmentRenderer = new AlignmentRenderer(AlignmentTextRenderer.newGUIRenderer());

	public PlayerMessageScreen(PlayerMessageType type, boolean command, String custom) {
		super(new StringTextComponent("MESSAGE"));
		messageType = type;
		isCommandSent = command;
		if (custom != null) {
			displayText = custom;
		} else {
			displayText = type.getDisplayMessage().getString();
		}

	}

	@Override
	public void init() {
		super.init();
		guiLeft = (width - 240) / 2;
		guiTop = (height - 160) / 2;
		buttonDismiss = this.addButton(new RedBookButton(guiLeft + 120 - 40, guiTop + 160 + 20, 80, 20, new TranslationTextComponent("gui.lotr.message.dismiss"), b -> {
			minecraft.player.closeContainer();
		}));
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float f) {
		this.renderBackground(matStack);
		minecraft.getTextureManager().bind(MESSAGE_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.blit(matStack, guiLeft, guiTop, 0, 0, 240, 160);
		int pageWidth = 216;
		String[] splitNewline = displayText.split(Pattern.quote("\\n"));
		List messageLines = Stream.of(splitNewline).map(StringTextComponent::new).flatMap(lineComp -> font.split(lineComp, pageWidth).stream()).collect(Collectors.toList());
		int x = guiLeft + 12;
		int y = guiTop + 12;

		for (Iterator var10 = messageLines.iterator(); var10.hasNext(); y += 9) {
			IReorderingProcessor line = (IReorderingProcessor) var10.next();
			font.draw(matStack, line, x, y, 8019267);
			font.getClass();
		}

		if (!isCommandSent) {
			ITextComponent bottomText = new TranslationTextComponent("gui.lotr.message.notDisplayedAgain");
			FontRenderer var10002 = font;
			int var10004 = guiLeft + 120;
			int var10005 = guiTop + 160 - 6;
			font.getClass();
			this.drawCenteredStringNoShadow(matStack, var10002, bottomText, var10004, var10005 - 9, 9666921);
		}

		if (messageType == PlayerMessageType.ALIGN_DRAIN) {
			int numIcons = 3;
			int iconGap = 40;

			for (int l = 0; l < numIcons; ++l) {
				int iconX = guiLeft + 120;
				iconX -= (numIcons - 1) * iconGap / 2;
				iconX += l * iconGap - 8;
				int iconY = guiTop + 12 + 14;
				int numFactions = l + 1;
				alignmentRenderer.renderAlignmentDrain(matStack, minecraft, iconX, iconY, numFactions);
			}
		}

		super.render(matStack, mouseX, mouseY, f);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (buttonTimer > 0) {
			--buttonTimer;
		}

		buttonDismiss.active = buttonTimer == 0;
	}
}
