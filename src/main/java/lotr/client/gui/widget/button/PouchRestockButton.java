package lotr.client.gui.widget.button;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.event.LOTRGuiHandler;
import lotr.common.item.PouchItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

public class PouchRestockButton extends Button {
	private static final ResourceLocation TEXTURE = new ResourceLocation("lotr", "textures/gui/widgets.png");
	private static final ITextComponent TOOLTIP = new TranslationTextComponent("gui.lotr.restock_pouches");
	private final ContainerScreen parentScreen;
	private final LOTRGuiHandler.PouchRestockButtonPositioner positioner;
	private int prevContainerGuiLeft;
	private int prevContainerGuiTop;
	private int prevContainerGuiXSize;
	private int prevContainerGuiYSize;
	private boolean prevCreativeTabWasInventory;

	public PouchRestockButton(ContainerScreen parent, int xIn, int yIn, LOTRGuiHandler.PouchRestockButtonPositioner positioner, IPressable onPressIn) {
		super(xIn, yIn, 10, 10, StringTextComponent.EMPTY, onPressIn);
		parentScreen = parent;
		this.positioner = positioner;
		prevContainerGuiLeft = parentScreen.getGuiLeft();
		prevContainerGuiTop = parentScreen.getGuiTop();
		prevContainerGuiXSize = parentScreen.getXSize();
		prevContainerGuiYSize = parentScreen.getYSize();
		checkIsCreativeTabInventory(parent.getMinecraft());
	}

	private void checkIsCreativeTabInventory(Minecraft minecraft) {
		if (parentScreen instanceof CreativeScreen) {
			int creativeTabIndex = ((CreativeScreen) parentScreen).getSelectedTab();
			boolean creativeTabInventory = creativeTabIndex == ItemGroup.TAB_INVENTORY.getId();
			if (creativeTabInventory != prevCreativeTabWasInventory) {
				repositionButton(minecraft);
				prevCreativeTabWasInventory = creativeTabInventory;
			}

			if (!creativeTabInventory) {
				active = visible = false;
			}
		}

	}

	private void checkPouchRestockPositionAndVisibility(Minecraft minecraft) {
		PlayerInventory inv = minecraft.player.inventory;
		active = visible = inv.hasAnyOf(new HashSet(PouchItem.ALL_POUCH_ITEMS));
		int guiLeft = parentScreen.getGuiLeft();
		int guiTop = parentScreen.getGuiTop();
		int guiXSize = parentScreen.getXSize();
		int guiYSize = parentScreen.getYSize();
		if (guiLeft != prevContainerGuiLeft || guiTop != prevContainerGuiTop || guiXSize != prevContainerGuiXSize || guiYSize != prevContainerGuiYSize) {
			repositionButton(minecraft);
			prevContainerGuiLeft = guiLeft;
			prevContainerGuiTop = guiTop;
			prevContainerGuiXSize = guiXSize;
			prevContainerGuiYSize = guiYSize;
		}

		checkIsCreativeTabInventory(minecraft);
	}

	@Override
	public void render(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft minecraft = Minecraft.getInstance();
		checkPouchRestockPositionAndVisibility(minecraft);
		super.render(matStack, mouseX, mouseY, f);
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		int yOffset = getYImage(isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.blit(matStack, x, y, 0, 128 + yOffset * height, width, height);
		renderBg(matStack, minecraft, mouseX, mouseY);
		if (isHovered()) {
			parentScreen.renderTooltip(matStack, TOOLTIP, mouseX, mouseY);
		}

	}

	private void repositionButton(Minecraft minecraft) {
		Optional optButtonCoords = getRestockButtonPosition(minecraft, parentScreen, positioner);
		if (optButtonCoords.isPresent()) {
			x = (Integer) ((Pair) optButtonCoords.get()).getLeft();
			y = (Integer) ((Pair) optButtonCoords.get()).getRight();
		} else {
			active = visible = false;
		}

	}

	public static Optional getRestockButtonPosition(Minecraft minecraft, ContainerScreen containerScreen, LOTRGuiHandler.PouchRestockButtonPositioner positioner) {
		PlayerEntity thePlayer = minecraft.player;
		PlayerInventory playerInv = thePlayer.inventory;
		boolean containsPlayer = false;
		Slot topRightPlayerSlot = null;
		Slot topLeftPlayerSlot = null;
		Container container = containerScreen.getMenu();
		for (Slot slot : container.slots) {
			if (slot.container == playerInv) {
				int slotIndex = slot.getSlotIndex();
				boolean acceptableSlotIndex = slotIndex < playerInv.items.size();
				if (acceptableSlotIndex) {
					containsPlayer = true;
					boolean isTopRight = false;
					if (topRightPlayerSlot == null || slot.y < topRightPlayerSlot.y || slot.y == topRightPlayerSlot.y && slot.x > topRightPlayerSlot.x) {
						isTopRight = true;
					}

					if (isTopRight) {
						topRightPlayerSlot = slot;
					}

					boolean isTopLeft = false;
					if (topLeftPlayerSlot == null || slot.y < topLeftPlayerSlot.y || slot.y == topLeftPlayerSlot.y && slot.x < topLeftPlayerSlot.x) {
						isTopLeft = true;
					}

					if (isTopLeft) {
						topLeftPlayerSlot = slot;
					}
				}
			}
		}

		if (containsPlayer) {
			int guiLeft = containerScreen.getGuiLeft();
			int guiTop = containerScreen.getGuiTop();
			Pair buttonCoords = positioner.getButtonPosition(topLeftPlayerSlot, topRightPlayerSlot);
			buttonCoords = Pair.of(guiLeft + (Integer) buttonCoords.getLeft(), guiTop + (Integer) buttonCoords.getRight());
			return Optional.of(buttonCoords);
		}
		return Optional.empty();
	}
}
