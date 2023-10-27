package lotr.client.gui;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.util.LOTRClientUtil;
import lotr.common.LOTRLog;
import lotr.common.config.LOTRConfig;
import lotr.common.init.LOTRItems;
import lotr.common.init.LOTRWorldTypes;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldOptionsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.client.ForgeWorldTypeScreens;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class WorldTypeHelpScreen extends Screen {
	private final CreateWorldScreen parentScreen;
	private Button buttonNormalWorld;
	private Button buttonMiddleEarthWorld;
	private Button buttonDismiss;
	private BiomeGeneratorTypeScreens selectedWorldType = null;
	private int selectionCloseScreenTimer;

	public WorldTypeHelpScreen(CreateWorldScreen parent) {
		super(new StringTextComponent("WORLD_TYPE_HELP"));
		parentScreen = parent;
	}

	private void deactivateButtonIfNotSelected(Button button, Button selectedButton) {
		if (button != selectedButton) {
			button.visible = button.active = false;
		}

	}

	private float getFadeoutAlpha() {
		if (selectedWorldType == null) {
			return 1.0F;
		}
		float f = selectionCloseScreenTimer / 20.0F;
		return MathHelper.clamp(f, 0.0F, 1.0F);
	}

	private ITextComponent getSelectWorldTypeDisplayText(BiomeGeneratorTypeScreens worldType) {
		ITextComponent baseMsg = new TranslationTextComponent("selectWorld.mapType");
		return baseMsg.copy().append(" ").append(getWorldTypeDisplayName(worldType));
	}

	private ITextComponent getWorldTypeDisplayName(BiomeGeneratorTypeScreens worldType) {
		return worldType.description();
	}

	@Override
	public void init() {
		super.init();
		int xMid = width / 2;
		int yMid = height / 2;
		int yTop = yMid - 100;
		int yBottom = yMid + 100;
		int buttonW = 150;
		int buttonH = 20;
		int buttonY = yTop + 70;
		buttonNormalWorld = this.addButton(new Button(xMid - 155, buttonY, buttonW, buttonH, getSelectWorldTypeDisplayText(BiomeGeneratorTypeScreens.NORMAL), b -> {
			selectWorldType(b, BiomeGeneratorTypeScreens.NORMAL);
		}));
		buttonMiddleEarthWorld = this.addButton(new Button(xMid + 5, buttonY, buttonW, buttonH, getSelectWorldTypeDisplayText(getGeneratorFromForgeWorldType(LOTRWorldTypes.MIDDLE_EARTH)), b -> {
			selectWorldType(b, getGeneratorFromForgeWorldType(LOTRWorldTypes.MIDDLE_EARTH));
		}));
		buttonDismiss = this.addButton(new Button(xMid - 75, yBottom - 10, buttonW, buttonH, new TranslationTextComponent("gui.lotr.worldTypeHelp.dismiss"), b -> {
			onClose();
		}));
	}

	@Override
	public void onClose() {
		LOTRConfig.CLIENT.showWorldTypeHelp.setAndSave(false);
		minecraft.setScreen(parentScreen);
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float f) {
		int xMid = width / 2;
		int yMid = height / 2;
		int x0 = xMid - 160;
		int x1 = xMid + 160;
		int y0 = yMid - 100;
		int y1 = yMid + 100;
		float alpha = getFadeoutAlpha();
		matStack.pushPose();
		matStack.translate(0.0D, 0.0D, -100.0D);
		parentScreen.render(matStack, -1000, -1000, f);
		matStack.popPose();
		fill(matStack, 0, 0, width, height, LOTRClientUtil.getRGBA(1052688, alpha * 0.75F));
		int border = 1;
		fill(matStack, x0, y0, x1, y1, LOTRClientUtil.getRGBA(0, alpha));
		hLine(matStack, x0 - border, x1 + border, y0 - border, LOTRClientUtil.getRGBA(16777215, alpha));
		hLine(matStack, x0 - border, x1 + border, y1 + border, LOTRClientUtil.getRGBA(16777215, alpha));
		vLine(matStack, x0 - border, y0 - border, y1 + border, LOTRClientUtil.getRGBA(16777215, alpha));
		vLine(matStack, x1 + border, y0 - border, y1 + border, LOTRClientUtil.getRGBA(16777215, alpha));
		int fontBorder = 6;
		int maxFontWidth = 320 - fontBorder * 2;
		ITextComponent title = new TranslationTextComponent("gui.lotr.worldTypeHelp.title");
		List titleLines = font.split(title, maxFontWidth);
		int y = y0 + fontBorder;

		for (Iterator var18 = titleLines.iterator(); var18.hasNext(); y += 9) {
			IReorderingProcessor line = (IReorderingProcessor) var18.next();
			font.draw(matStack, line, xMid - font.width(line) / 2, y, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
			font.getClass();
		}

		if (selectedWorldType == null) {
			renderTextBelowButton(matStack, buttonNormalWorld, new TranslationTextComponent("gui.lotr.worldTypeHelp.normal", getWorldTypeDisplayName(BiomeGeneratorTypeScreens.NORMAL)), alpha);
			renderTextBelowButton(matStack, buttonMiddleEarthWorld, new TranslationTextComponent("gui.lotr.worldTypeHelp.me", getWorldTypeDisplayName(getGeneratorFromForgeWorldType(LOTRWorldTypes.MIDDLE_EARTH))), alpha);
		} else {
			int var10000 = y0 + 70 + 20;
			font.getClass();
			y = var10000 + 9;
			ITextComponent line1 = new TranslationTextComponent("gui.lotr.worldTypeHelp.selected.1", getWorldTypeDisplayName(selectedWorldType));
			ITextComponent line2 = new TranslationTextComponent("gui.lotr.worldTypeHelp.selected.2", new TranslationTextComponent("selectWorld.moreWorldOptions"));
			font.draw(matStack, line1, xMid - font.width(line1) / 2, y, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
			FontRenderer var20 = font;
			float var10003 = xMid - font.width(line2) / 2;
			font.getClass();
			var20.draw(matStack, line2, var10003, y + 9 * 2, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
		}

		renderItemIconAboveButton(buttonNormalWorld, new ItemStack((IItemProvider) LOTRItems.GOLD_RING.get()), -10, 0, alpha);
		renderItemIconAboveButton(buttonNormalWorld, new ItemStack(Items.FLINT_AND_STEEL), 10, 0, alpha);
		renderItemIconAboveButton(buttonMiddleEarthWorld, new ItemStack((IItemProvider) LOTRItems.RED_BOOK.get()), 0, 0, alpha);
		buttons.forEach(button -> {
			button.setAlpha(alpha);
		});
		super.render(matStack, mouseX, mouseY, f);
	}

	private void renderItemIconAboveButton(Button button, ItemStack icon, int xOffset, int yOffset, float alpha) {
		if (button.visible && alpha >= 1.0F) {
			int x = button.x + button.getWidth() / 2 + xOffset;
			int y = button.y - 19 + yOffset;
			x -= 8;
			itemRenderer.renderGuiItem(icon, x, y);
		}

	}

	private void renderTextBelowButton(MatrixStack matStack, Button button, ITextComponent text, float alpha) {
		int buttonBorder = 4;
		if (button.visible) {
			List belowLines = font.split(text, button.getWidth());
			int y = button.y + button.getHeight() + buttonBorder;

			for (Iterator var8 = belowLines.iterator(); var8.hasNext(); y += 9) {
				IReorderingProcessor line = (IReorderingProcessor) var8.next();
				font.draw(matStack, line, button.x, y, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
				font.getClass();
			}
		}

	}

	private void selectWorldType(Button selectedButton, BiomeGeneratorTypeScreens worldType) {
		if (selectedWorldType == null) {
			selectedWorldType = worldType;

			try {
				WorldOptionsScreen wos = parentScreen.worldGenSettingsComponent;
				ObfuscationReflectionHelper.setPrivateValue(WorldOptionsScreen.class, wos, Optional.of(worldType), "preset");
				Field f_dimensionGeneratorSettings = ObfuscationReflectionHelper.findField(WorldOptionsScreen.class, "settings");
				DimensionGeneratorSettings prevDimGenSettings = (DimensionGeneratorSettings) f_dimensionGeneratorSettings.get(wos);
				f_dimensionGeneratorSettings.set(wos, worldType.create(wos.registryHolder(), prevDimGenSettings.seed(), prevDimGenSettings.generateFeatures(), prevDimGenSettings.generateBonusChest()));
			} catch (Exception var6) {
				LOTRLog.error("Error setting world type in world creation screen");
				var6.printStackTrace();
			}

			selectionCloseScreenTimer = 100;
			selectedButton.x = width / 2 - selectedButton.getWidth() / 2;
			deactivateButtonIfNotSelected(buttonNormalWorld, selectedButton);
			deactivateButtonIfNotSelected(buttonMiddleEarthWorld, selectedButton);
		}

	}

	@Override
	public void tick() {
		super.tick();
		if (selectionCloseScreenTimer > 0) {
			--selectionCloseScreenTimer;
			if (selectionCloseScreenTimer <= 0) {
				onClose();
			}
		}

	}

	private static BiomeGeneratorTypeScreens getGeneratorFromForgeWorldType(ForgeWorldType worldType) {
		try {
			Map generators = (Map) ObfuscationReflectionHelper.getPrivateValue(ForgeWorldTypeScreens.class, null, "GENERATORS");
			return (BiomeGeneratorTypeScreens) generators.get(worldType);
		} catch (Exception var2) {
			LOTRLog.error("Reflection tricks to lookup the generator for the ForgeWorldType %s failed!", worldType.getRegistryName());
			var2.printStackTrace();
			return null;
		}
	}

	private static BiomeGeneratorTypeScreens getGeneratorFromForgeWorldType(RegistryObject worldType) {
		return getGeneratorFromForgeWorldType((ForgeWorldType) worldType.get());
	}
}
