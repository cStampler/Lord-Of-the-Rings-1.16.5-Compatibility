package lotr.client.gui.map;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.LOTRKeyHandler;
import lotr.client.MapImageTextures;
import lotr.client.gui.MiddleEarthFactionsScreen;
import lotr.client.gui.MiddleEarthMenuScreen;
import lotr.client.gui.util.AlignmentTextRenderer;
import lotr.client.util.LOTRClientUtil;
import lotr.common.config.LOTRConfig;
import lotr.common.data.FastTravelDataModule;
import lotr.common.data.FogDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.fac.AreaBorders;
import lotr.common.fac.AreaOfInfluence;
import lotr.common.fac.AreasOfInfluence;
import lotr.common.fac.Faction;
import lotr.common.init.LOTRBiomes;
import lotr.common.init.LOTRDimensions;
import lotr.common.init.LOTRItems;
import lotr.common.network.CPacketCreateMapMarker;
import lotr.common.network.CPacketDeleteMapMarker;
import lotr.common.network.CPacketFastTravel;
import lotr.common.network.CPacketIsOpRequest;
import lotr.common.network.CPacketMapTp;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.util.LOTRUtil;
import lotr.common.world.map.AdoptedCustomWaypoint;
import lotr.common.world.map.CustomWaypoint;
import lotr.common.world.map.MapExplorationTile;
import lotr.common.world.map.MapLabel;
import lotr.common.world.map.MapMarker;
import lotr.common.world.map.MapPlayerLocation;
import lotr.common.world.map.MapSettings;
import lotr.common.world.map.MapSettingsManager;
import lotr.common.world.map.MapWaypoint;
import lotr.common.world.map.Road;
import lotr.common.world.map.RoadPoint;
import lotr.common.world.map.RoadSection;
import lotr.common.world.map.RouteRoadPoint;
import lotr.common.world.map.SelectableMapObject;
import lotr.common.world.map.Waypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class MiddleEarthMapScreen extends MiddleEarthMenuScreen {
   private static final ItemStack QUEST_ICON;
   public static final int BLACK = -16777216;
   public static final int BORDER_COLOR = -6156032;
   private static boolean fullscreen;
   private static float zoomPower;
   private static final DecimalFormat ZOOM_DISPLAY_FORMAT;
   static {
      QUEST_ICON = new ItemStack((IItemProvider) LOTRItems.RED_BOOK.get());
      fullscreen = true;
      zoomPower = 0.0F;
      ZOOM_DISPLAY_FORMAT = new DecimalFormat("0.##");
   }
   private final MapSettings loadedMapSettings;
   private int viewportWidth;
   private int viewportHeight;
   private int viewportXMin;
   private int viewportXMax;
   private int viewportYMin;
   private int viewportYMax;
   private List<MapWidget> mapWidgets = new ArrayList<>();
   private MapWidget widgetZoomIn;
   private MapWidget widgetZoomOut;
   private MapWidget widgetRecentre;
   private MapWidget widgetFullScreen;
   private MapWidget widgetSepia;
   private MapWidget widgetLabels;
   private MapWidget widgetToggleMapWPs;
   private MapWidget widgetToggleCustomWPs;
   private MapWidget widgetToggleShowLocation;
   private MapWidget widgetToggleMarkers;
   private MapWidget widgetNewMarker;
   private double posX;
   private double posY;
   private double prevPosX;
   private double prevPosY;
   private double posXMove;
   private double posYMove;
   private boolean isMouseWithinMap;
   private int mouseXCoord;
   private int mouseZCoord;
   private double recentreToX;
   private double recentreToY;
   private double recentreFromX;
   private double recentreFromY;
   private int recentreTicks;
   private float prevZoomPower;
   private float zoomScale;
   private float zoomScaleStable;
   private float zoomExp;
   private int zoomTicks;
   private int prevZoomTicks;
   public boolean enableZoomOutObjectFading;
   private int zoomingMessageDisplayTicks;
   private boolean zoomingMessageIsZoomIn;
   private SelectableMapObject mouseOverObject;
   private SelectableMapObject selectedObject;
   private int objectSelectTick;
   private int prevObjectSelectTick;
   private final WaypointTooltipRenderer waypointTooltip;
   private final MarkerTooltipRenderer markerTooltip;
   private boolean creatingMarker;
   private boolean hasOverlay;
   private boolean isPlayerOp;
   private boolean sentOpRequestPacket;
   private Faction areaOfInfluenceFaction;
   private boolean mouseInAreaOfInfluence;
   private boolean mouseInReducedAreaOfInfluence;
   private final AlignmentTextRenderer alignmentTextRenderer;

   private boolean isFacScrolling;

   public MiddleEarthMapScreen() {
      super(new StringTextComponent("MAP"));
      prevZoomPower = zoomPower;
      enableZoomOutObjectFading = true;
      waypointTooltip = new WaypointTooltipRenderer();
      markerTooltip = new MarkerTooltipRenderer();
      isPlayerOp = false;
      sentOpRequestPacket = false;
      alignmentTextRenderer = AlignmentTextRenderer.newGUIRenderer();
      isFacScrolling = false;
      loadedMapSettings = MapSettingsManager.clientInstance().getLoadedMapOrLoadDefault(Minecraft.getInstance().getResourceManager());
      loadCurrentMapTextures();
   }

   private int boundCreatingMarkerMouseX(double mouseX) {

      int markerX = (int) mouseX;
      markerX = Math.max(viewportXMin + 5, markerX);
      return Math.min(viewportXMax - 5, markerX);
   }

   private int boundCreatingMarkerMouseZ(double mouseY) {

      int markerZ = (int) mouseY;
      markerZ = Math.max(viewportYMin + 5, markerZ);
      return Math.min(viewportYMax - 5, markerZ);
   }

   private float calcZoomedWaypointAlpha() {
      if (enableZoomOutObjectFading) {
         float alpha = (zoomExp - -3.3F) / 2.2F;
         return Math.min(alpha, 1.0F);
      }
      return 1.0F;
   }

   private boolean canCreateNewMarker() {
      return minecraft.player != null && getClientPlayerData().getMapMarkerData().canCreateNewMarker();
   }

   private boolean canTeleport() {
      if (!isMiddleEarth() || !minecraft.level.getChunkSource().isEntityTickingChunk(minecraft.player)) {
         return false;
      }
      requestIsOp();
      return isPlayerOp;
   }

   private void centreMapOnPlayer() {
      posX = getPlayerMapPosX();
      posY = getPlayerMapPosY();
   }

   @Override
   public boolean charTyped(char c, int modifiers) {
      if (!hasOverlay) {
         if (selectedObject instanceof Waypoint) {
            if (waypointTooltip.charTyped(c, modifiers)) {
               return true;
            }
         } else if (selectedObject instanceof MapMarker && markerTooltip.charTyped(c, modifiers)) {
            return true;
         }
      }

      return super.charTyped(c, modifiers);
   }

   private double[] convertMouseCoordsToMapCoords(int mouseX, int mouseY, float tick) {
      double mapPosX = lerpPosX(tick) + (mouseX - viewportXMin - viewportWidth / 2) / zoomScale;
      double mapPosZ = lerpPosY(tick) + (mouseY - viewportYMin - viewportHeight / 2) / zoomScale;
      return new double[] { mapPosX, mapPosZ };
   }

   private void determineMouseOverObject(int mouseX, int mouseY, float tick) {
      mouseOverObject = null;
      double distanceMouseOverObject = Double.MAX_VALUE;
      List<SelectableMapObject> visibleObjects = new ArrayList<>(getVisibleWaypoints());
      visibleObjects.addAll(getVisibleMarkers());
      for (SelectableMapObject object : visibleObjects) {
          double[] pos = transformWorldCoords(object.getWorldX(), object.getWorldZ(), tick);
          double x = pos[0];
          double y = pos[1];
          if (object != this.selectedObject) {
        	  int halfW = object.getMapIconWidth() / 2;
        	  if (x >= (this.viewportXMin - halfW) && x <= (this.viewportXMax + halfW) && y >= (this.viewportYMin - halfW) && y <= (this.viewportYMax + halfW)) {
        		  double dx = x - mouseX;
        		  double dy = y - mouseY;
        		  double distToObject = Math.sqrt(dx * dx + dy * dy);
        		  if (distToObject <= 5.0D)
        			  if (distToObject <= distanceMouseOverObject) {
        				  this.mouseOverObject = object;
        				  distanceMouseOverObject = distToObject;
        			} 
        	 } 
          }
      }
   }

   public void drawFancyRect(MatrixStack matStack, int x1, int y1, int x2, int y2) {
      fill(matStack, x1, y1, x2, y2, -1073741824);
      hLine(matStack, x1 - 1, x2, y1 - 1, -6156032);
      hLine(matStack, x1 - 1, x2, y2, -6156032);
      vLine(matStack, x1 - 1, y1 - 1, y2, -6156032);
      vLine(matStack, x2, y1 - 1, y2, -6156032);
   }

   private void endMapClipping() {
      GL11.glDisable(3089);
   }

   public LOTRPlayerData getClientPlayerData() {
      return getOptClientPlayerData().get();
   }

   private int[] getCreatingMarkerWorldCoords(int mouseX, int mouseZ) {
      double[] markerMapCoords = convertMouseCoordsToMapCoords(mouseX, mouseZ, 1.0F);
      int worldX = loadedMapSettings.mapToWorldX(markerMapCoords[0]);
      int worldZ = loadedMapSettings.mapToWorldZ(markerMapCoords[1]);
      return new int[] { worldX, worldZ };
   }

   private ITextComponent getMapTitle() {
      return new TranslationTextComponent("gui.lotr.map.title", loadedMapSettings.getTitle());
   }

   private Optional<LOTRPlayerData> getOptClientPlayerData() {
      return Optional.<ClientPlayerEntity>ofNullable(minecraft.player).map(LOTRLevelData.clientInstance()::getData);
   }

   private double getPlayerMapPosX() {
      return loadedMapSettings.worldToMapX_frac(minecraft.player.getX());
   }

   private double getPlayerMapPosY() {
      return loadedMapSettings.worldToMapZ_frac(minecraft.player.getZ());
   }

   private List<MapMarker> getVisibleMarkers() {
      return !showMarkers() ? ImmutableList.of() : getOptClientPlayerData().map(pd -> pd.getMapMarkerData().getMarkers()).orElse(ImmutableList.of());
   }

   private List<Waypoint> getVisibleWaypoints() {
      Stream<MapWaypoint> mapWps = this.loadedMapSettings.getWaypoints().stream();
      Stream<CustomWaypoint> customWps = this.getOptClientPlayerData()
    		  .<Stream<CustomWaypoint>>map(pd -> pd.getFastTravelData().getCustomWaypoints().stream())
              .orElse(Stream.empty());
      Stream<AdoptedCustomWaypoint> adoptedCustomWps = this.getOptClientPlayerData()
              .<Stream<AdoptedCustomWaypoint>>map(pd -> pd.getFastTravelData().getAdoptedCustomWaypoints().stream())
              .orElse(Stream.empty());
      return Streams.concat(mapWps, customWps, adoptedCustomWps)
              .filter(this::isWaypointVisible)
              .collect(Collectors.toList());
   }

   private float getZoomIncrement() {
      return isQuickMapMovement() ? 1.5F : isFineMapMovement() ? 0.25F : 1.0F;
   }

   private ITextComponent getZoomingDisplayMessage() {
      int numLevels = 7;
      float currentRelativeLevel = zoomPower - -3.0F;
      return new TranslationTextComponent(zoomingMessageIsZoomIn ? "gui.lotr.map.zoomingIn" : "gui.lotr.map.zoomingOut", ZOOM_DISPLAY_FORMAT.format(currentRelativeLevel), Integer.valueOf(numLevels));
   }

   private void handleMapKeyboardMovement() {
      posXMove = 0.0D;
      posYMove = 0.0D;
      if (!hasOverlay && !isRecentringOnPlayer()) {
         if (waypointTooltip.isTextFieldFocused() || markerTooltip.isTextFieldFocused()) {
            return;
         }

         float move = 12.0F / (float) Math.pow(zoomScale, 0.800000011920929D);
         if (isQuickMapMovement()) {
            move *= 2.0F;
         } else if (isFineMapMovement()) {
            move *= 0.5F;
         }

         if (isKeyboardKeyDown(minecraft.options.keyLeft.getKey().getValue()) || isKeyboardKeyDown(263)) {
            posXMove -= move;
         }

         if (isKeyboardKeyDown(minecraft.options.keyRight.getKey().getValue()) || isKeyboardKeyDown(262)) {
            posXMove += move;
         }

         if (isKeyboardKeyDown(minecraft.options.keyUp.getKey().getValue()) || isKeyboardKeyDown(265)) {
            posYMove -= move;
         }

         if (isKeyboardKeyDown(minecraft.options.keyDown.getKey().getValue()) || isKeyboardKeyDown(264)) {
            posYMove += move;
         }

         if (posXMove != 0.0D || posYMove != 0.0D) {
            selectObject((SelectableMapObject) null);
         }
      }

   }

   private boolean hasAreasOfInfluence() {
      return areaOfInfluenceFaction != null && AreasOfInfluence.areAreasOfInfluenceEnabled(minecraft.level);
   }

   private boolean hasFogOfWar() {
      return minecraft.level != null && FogDataModule.isFogOfWarEnabledClientside();
   }

   private boolean hasMapLabels() {
      return (Boolean) LOTRConfig.CLIENT.mapLabels.get();
   }

   public boolean hasOverlay() {
      return hasOverlay;
   }

   @Override
   public void init() {
      xSize = 256;
      ySize = 256;
      super.init();
      if (fullscreen) {
         int midX = width / 2;
         int titleWidth = font.width(getMapTitle());
         int idealMargin = 24;
         int minimumGapFromTitle = 4;
         buttonMenuReturn.x = Math.min(0 + idealMargin, midX - titleWidth / 2 - minimumGapFromTitle - buttonMenuReturn.getWidth());
         buttonMenuReturn.y = 4;
      }

      if (hasAreasOfInfluence()) {
         removeButton(buttonMenuReturn);
      }

      setupMapDimensions();
      setupMapWidgets();
      children.add(new MiddleEarthMapScreen.MapDragListener());
      if (hasAreasOfInfluence()) {
         setupMapDimensions();
         AreaBorders zoneBorders = areaOfInfluenceFaction.getAreasOfInfluence().calculateAreaOfInfluenceBordersIncludingReduced();
         posX = loadedMapSettings.worldToMapX_frac(zoneBorders.getXCentre());
         posY = loadedMapSettings.worldToMapZ_frac(zoneBorders.getZCentre());
         double mapZoneWidth = loadedMapSettings.worldToMapDistance(zoneBorders.getWidth());
         double mapZoneHeight = loadedMapSettings.worldToMapDistance(zoneBorders.getHeight());
         int zoomPowerWidth = MathHelper.floor(Math.log(viewportWidth / mapZoneWidth) / Math.log(2.0D));
         int zoomPowerHeight = MathHelper.floor(Math.log(viewportHeight / mapZoneHeight) / Math.log(2.0D));
         prevZoomPower = zoomPower = Math.min(zoomPowerWidth, zoomPowerHeight);
      } else if (minecraft.player != null) {
         centreMapOnPlayer();
      }

      prevPosX = posX;
      prevPosY = posY;
      refreshZoomVariables(1.0F);
      waypointTooltip.init(this, minecraft, font);
      markerTooltip.init(this, minecraft, font);
   }

   private boolean isCreatingMarkerAtValidMousePosition(int mouseX, int mouseZ) {
      int[] worldCoords = getCreatingMarkerWorldCoords(mouseX, mouseZ);
      return MapMarker.isValidMapMarkerPosition(loadedMapSettings, worldCoords[0], worldCoords[1]);
   }

   private boolean isFineMapMovement() {
      return isKeyboardKeyDown(minecraft.options.keyShift.getKey().getValue());
   }

   private boolean isKeyboardKeyDown(int glfwKey) {
      return InputMappings.isKeyDown(minecraft.getWindow().getWindow(), glfwKey);
   }

   private boolean isMiddleEarth() {
      return minecraft.player != null && LOTRDimensions.isDimension(minecraft.player.level, LOTRDimensions.MIDDLE_EARTH_WORLD_KEY);
   }

   private boolean isPositionFogged(double mapX, double mapZ) {
      return !hasFogOfWar() ? false : getClientPlayerData().getFogData().isFogged(MathHelper.floor(mapX), MathHelper.floor(mapZ));
   }

   private boolean isQuickMapMovement() {
      return isKeyboardKeyDown(minecraft.options.keySprint.getKey().getValue());
   }

   private boolean isRecentringOnPlayer() {
      return recentreTicks > 0;
   }

   private boolean isWaypointVisible(Waypoint wp) {
      if (wp.getDisplayState(minecraft.player) == Waypoint.WaypointDisplayState.HIDDEN
              || isPositionFogged(wp.getMapX(), wp.getMapZ()) && !wp.hasPlayerUnlocked(minecraft.player)) {
         return false;
      }
      if (wp.isCustom()) {
         return wp.isSharedCustom() && wp.isSharedHidden() && !showHiddenSharedCustomWaypoints() ? false : showCustomWaypoints();
      }
      return showMapWaypoints();
   }

   private void keepMapPositionWithinBounds() {
      float mapScaleX = viewportWidth / zoomScale;
      float mapScaleY = viewportHeight / zoomScale;
      float minPosY;
      if (loadedMapSettings.isScreenSideLocked(Direction.WEST)) {
         minPosY = mapScaleX / 2.0F;
         posX = Math.max(posX, minPosY);
         prevPosX = Math.max(prevPosX, minPosY);
      }

      if (loadedMapSettings.isScreenSideLocked(Direction.EAST)) {
         minPosY = loadedMapSettings.getWidth() - mapScaleX / 2.0F;
         posX = Math.min(posX, minPosY);
         prevPosX = Math.min(prevPosX, minPosY);
      }

      if (loadedMapSettings.isScreenSideLocked(Direction.NORTH)) {
         minPosY = mapScaleY / 2.0F;
         posY = Math.max(posY, minPosY);
         prevPosY = Math.max(prevPosY, minPosY);
      }

      if (loadedMapSettings.isScreenSideLocked(Direction.SOUTH)) {
         minPosY = loadedMapSettings.getHeight() - mapScaleY / 2.0F;
         posY = Math.min(posY, minPosY);
         prevPosY = Math.min(prevPosY, minPosY);
      }

   }

   @Override
   public boolean keyPressed(int key, int scan, int param3) {
      if (!hasOverlay) {
         if (selectedObject != null && key == 257) {
            selectObject((SelectableMapObject) null);
            return true;
         }

         if (selectedObject instanceof Waypoint) {
            if (waypointTooltip.keyPressed(key, scan, param3)) {
               return true;
            }

            if (waypointTooltip.isTextFieldFocused()) {
               return false;
            }
         } else if (selectedObject instanceof MapMarker) {
            if (markerTooltip.keyPressed(key, scan, param3)) {
               return true;
            }

            if (markerTooltip.isTextFieldFocused()) {
               return false;
            }
         }

         if (!isRecentringOnPlayer() && minecraft.options.keyJump.matches(key, scan)) {
            recentreMapOnPlayer();
            return true;
         }

         Waypoint selectedWp = selectedObject instanceof Waypoint ? (Waypoint) selectedObject : null;
         if (selectedWp != null && LOTRKeyHandler.getFastTravelKey(minecraft).matches(key, scan) && isMiddleEarth()) {
            FastTravelDataModule ftData = getClientPlayerData().getFastTravelData();
            if (selectedWp.hasPlayerUnlocked(minecraft.player) && ftData.getTimeSinceFT() >= ftData.getWaypointFTTime(selectedWp, minecraft.player)) {
               CPacketFastTravel packet = new CPacketFastTravel(selectedWp);
               LOTRPacketHandler.sendToServer(packet);
               minecraft.player.closeContainer();
               return true;
            }
         }

         if (selectedWp == null && LOTRKeyHandler.KEY_BIND_MAP_TELEPORT.matches(key, scan) && isMouseWithinMap && canTeleport()) {
            CPacketMapTp packet = new CPacketMapTp(mouseXCoord, mouseZCoord);
            LOTRPacketHandler.sendToServer(packet);
            minecraft.player.closeContainer();
            return true;
         }

         if (hasAreasOfInfluence() && isEscapeOrInventoryKey(key, scan)) {
            minecraft.setScreen(new MiddleEarthFactionsScreen());
            return true;
         }
      }

      return super.keyPressed(key, scan, param3);
   }

   private double lerpPosX(float tick) {
      return MathHelper.clampedLerp(prevPosX, posX, tick);
   }

   private double lerpPosY(float tick) {
      return MathHelper.clampedLerp(prevPosY, posY, tick);
   }

   public void loadCurrentMapTextures() {
      MapImageTextures.INSTANCE.loadMapTexturesIfNew(loadedMapSettings);
   }

   @Override
   public boolean mouseClicked(double x, double y, int code) {
      if (creatingMarker) {
         if (code == 0) {
            int markerX = boundCreatingMarkerMouseX(x);
            int markerZ = boundCreatingMarkerMouseZ(y);
            if (isCreatingMarkerAtValidMousePosition(markerX, markerZ)) {
               if (canCreateNewMarker()) {
                  int[] worldCoords = getCreatingMarkerWorldCoords(markerX, markerZ);
                  String defaultName = I18n.get("gui.lotr.map.widget.newMarker");
                  CPacketCreateMapMarker packet = new CPacketCreateMapMarker(worldCoords[0], worldCoords[1], defaultName);
                  LOTRPacketHandler.sendToServer(packet);
                  playMarkerUpdateSound();
               }

               creatingMarker = false;
               return true;
            }
         } else if (code == 1) {
            creatingMarker = false;
            return true;
         }
      } else if (mouseOverObject instanceof MapMarker) {
         MapMarker mouseOverMarker = (MapMarker) mouseOverObject;
         if (code == 1) {
            CPacketDeleteMapMarker packet = new CPacketDeleteMapMarker(mouseOverMarker);
            LOTRPacketHandler.sendToServer(packet);
            selectObject((SelectableMapObject) null);
            playMarkerSelectSound();
            return true;
         }
      }

      if (selectedObject instanceof Waypoint) {
         if (waypointTooltip.mouseClicked(x, y, code)) {
            return true;
         }
      } else if (selectedObject instanceof MapMarker && markerTooltip.mouseClicked(x, y, code)) {
         return true;
      }

      return super.mouseClicked(x, y, code);
   }

   @Override
   public boolean mouseScrolled(double x, double y, double scroll) {
      if (super.mouseScrolled(x, y, scroll)) {
         return true;
      }
      if (!hasOverlay && zoomTicks == 0) {
         if (scroll < 0.0D && zoomPower > -3.0F) {
            zoomOut();
            return true;
         }

         if (scroll > 0.0D && zoomPower < 4.0F) {
            zoomIn();
            return true;
         }
      }

      return false;
   }

   public void receiveIsOp(boolean isOp) {
      isPlayerOp = isOp;
   }

   private void recentreMapOnPlayer() {
      recentreToX = getPlayerMapPosX();
      recentreToY = getPlayerMapPosY();
      recentreFromX = posX;
      recentreFromY = posY;
      recentreTicks = 6;
      selectObject((SelectableMapObject) null);
   }

   private void refreshZoomVariables(float tick) {
      float zoomStableExp;
      if (zoomTicks <= 0 && prevZoomTicks <= 0) {
         zoomExp = zoomPower;
         zoomStableExp = zoomExp;
      } else {
         float zoomTickLerp = prevZoomTicks + (zoomTicks - prevZoomTicks) * tick;
         float progress = (6.0F - zoomTickLerp) / 6.0F;
         zoomExp = prevZoomPower + (zoomPower - prevZoomPower) * progress;
         zoomStableExp = Math.min(zoomPower, prevZoomPower);
      }

      zoomScale = (float) Math.pow(2.0D, zoomExp);
      zoomScaleStable = (float) Math.pow(2.0D, zoomStableExp);
      keepMapPositionWithinBounds();
   }

   @Override
   public void render(MatrixStack matStack, int mouseX, int mouseY, float tick) {
      tick = minecraft.getFrameTime();
      World world = minecraft.level;
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buf = tess.getBuilder();
      setBlitOffset(0);
      refreshZoomVariables(tick);
      isMouseWithinMap = mouseX >= viewportXMin && mouseX < viewportXMax && mouseY >= viewportYMin && mouseY < viewportYMax;
      boolean isSepia = (Boolean) LOTRConfig.CLIENT.sepiaMap.get();
      if (fullscreen) {
         minecraft.getTextureManager().bind(MapImageTextures.OVERLAY_TEXTURE);
         RenderSystem.color4f(0.65F, 0.5F, 0.35F, 1.0F);
         float z = getBlitOffset();
         buf.begin(7, DefaultVertexFormats.POSITION_TEX);
         Matrix4f mat = matStack.last().pose();
         buf.vertex(mat, 0.0F, height, z).uv(0.0F, 1.0F).endVertex();
         buf.vertex(mat, width, height, z).uv(1.0F, 1.0F).endVertex();
         buf.vertex(mat, width, 0.0F, z).uv(1.0F, 0.0F).endVertex();
         buf.vertex(mat, 0.0F, 0.0F, z).uv(0.0F, 0.0F).endVertex();
         tess.end();
         int redW = 4;
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         renderGraduatedRects(matStack, viewportXMin - 1, viewportYMin - 1, viewportXMax + 1, viewportYMax + 1, -6156032, -16777216, redW);
      } else {
         this.renderBackground(matStack);
         renderGraduatedRects(matStack, viewportXMin - 1, viewportYMin - 1, viewportXMax + 1, viewportYMax + 1, -6156032, -16777216, 4);
      }

      setupScrollBars();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int seaColor = MapImageTextures.INSTANCE.getMapBackgroundColor(isSepia);
      fill(matStack, viewportXMin, viewportYMin, viewportXMax, viewportYMax, seaColor);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      ITextComponent title = getMapTitle();
      if (fullscreen) {
         this.drawCenteredStringNoShadow(matStack, font, title, width / 2, 10, 16777215);
      } else {
         this.drawCenteredStringNoShadow(matStack, font, title, width / 2, guiTop - 30, 16777215);
      }

      if (hasAreasOfInfluence()) {
         renderMapAndOverlay(matStack, tick, isSepia, 1.0F, false);
         renderAreasOfInfluence(mouseX, mouseY, tick);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         renderMapAndOverlay(matStack, tick, isSepia, 0.5F, true);
         RenderSystem.disableBlend();
      } else {
         renderMapAndOverlay(matStack, tick, isSepia, 1.0F, true);
      }

      this.renderRoads(matStack, tick);
      renderLabels(matStack, tick);
      renderFogOfWar(matStack, tick);
      renderPlayers(matStack, mouseX, mouseY, tick);
      determineMouseOverObject(mouseX, mouseY, tick);
      this.renderWaypoints(matStack, 0, mouseX, mouseY, tick);
      renderMarkers(matStack, 0, mouseX, mouseY, tick);
      RenderSystem.enableAlphaTest();
      MapImageTextures.drawMapCompassBottomLeft(matStack, viewportXMin + 12, viewportYMax - 12, 100.0F, 1.0F);
      this.renderWaypoints(matStack, 1, mouseX, mouseY, tick);
      renderMarkers(matStack, 1, mouseX, mouseY, tick);
      if (creatingMarker) {
         renderCreatingMarker(matStack, mouseX, mouseY);
      }

      if (hasAreasOfInfluence()) {
         ITextComponent tooltip = mouseInAreaOfInfluence ? new TranslationTextComponent("gui.lotr.map.areaOfInfluence.full") : mouseInReducedAreaOfInfluence ? new TranslationTextComponent("gui.lotr.map.areaOfInfluence.reduced") : null;
         if (tooltip != null) {
            renderInFront(() -> {
               int strWidth = minecraft.font.width(tooltip);
               minecraft.font.getClass();
               int strHeight = 9;
               int rectX = mouseX + 12;
               int rectY = mouseY - 12;
               int border = 3;
               int rectWidth = strWidth + border * 2;
               int rectHeight = strHeight + border * 2;
               int mapBorder2 = 2;
               rectX = Math.max(rectX, viewportXMin + mapBorder2);
               rectX = Math.min(rectX, viewportXMax - mapBorder2 - rectWidth);
               rectY = Math.max(rectY, viewportYMin + mapBorder2);
               rectY = Math.min(rectY, viewportYMax - mapBorder2 - rectHeight);
               drawFancyRect(matStack, rectX, rectY, rectX + rectWidth, rectY + rectHeight);
               font.draw(matStack, tooltip, rectX + border, rectY + border, 16777215);
            });
         }
      }

      Waypoint selectedWp;
      if (!hasOverlay && selectedObject != null) {
         if (selectedObject instanceof Waypoint) {
            selectedWp = (Waypoint) selectedObject;
            if (isWaypointVisible(selectedWp)) {
               renderWaypointTooltip(matStack, selectedWp, true, mouseX, mouseY, tick);
            } else {
               selectObject((SelectableMapObject) null);
            }
         } else if (selectedObject instanceof MapMarker) {
            MapMarker selectedMarker = (MapMarker) selectedObject;
            if (showMarkers()) {
               renderMarkerTooltip(matStack, selectedMarker, true, mouseX, mouseY, tick);
            } else {
               selectObject((SelectableMapObject) null);
            }
         }
      }

      setBlitOffset(100);
      if (!hasOverlay) {
         int biomePosX_int;
         int strX;
         if (isMiddleEarth() && selectedObject instanceof Waypoint) {
            selectedWp = (Waypoint) selectedObject;
            int blit1 = getBlitOffset();
            setBlitOffset(500);
            LOTRPlayerData pd = getClientPlayerData();
            FastTravelDataModule ftData = pd.getFastTravelData();
            boolean hasUnlocked = selectedWp.hasPlayerUnlocked(minecraft.player);
            int ftSince = ftData.getTimeSinceFT();
            int wpTimeThreshold = ftData.getWaypointFTTime(selectedWp, minecraft.player);
            biomePosX_int = wpTimeThreshold - ftSince;
            boolean canFastTravel = hasUnlocked && biomePosX_int <= 0;
            ITextComponent notUnlockedMessage = selectedWp.getNotUnlockedMessage(minecraft.player);
            ITextComponent ftPrompt = new TranslationTextComponent("gui.lotr.map.fastTravel.prompt", LOTRKeyHandler.getFastTravelKey(minecraft).getTranslatedKeyMessage());
            ITextComponent ftMoreTime = new TranslationTextComponent("gui.lotr.map.fastTravel.moreTime", LOTRUtil.getHMSTime_Ticks(biomePosX_int));
            ITextComponent ftWaitTime = new TranslationTextComponent("gui.lotr.map.fastTravel.waitTime", LOTRUtil.getHMSTime_Ticks(wpTimeThreshold));
            if (fullscreen) {
               if (!hasUnlocked) {
                  renderFullscreenSubtitles(matStack, notUnlockedMessage);
               } else if (canFastTravel) {
                  renderFullscreenSubtitles(matStack, ftPrompt, ftWaitTime);
               } else {
                  renderFullscreenSubtitles(matStack, ftMoreTime, ftWaitTime);
               }
            } else {
               List lines = new ArrayList();
               if (!hasUnlocked) {
                  lines.add(notUnlockedMessage);
               } else {
                  if (canFastTravel) {
                     lines.add(ftPrompt);
                  } else {
                     lines.add(ftMoreTime);
                  }
                  lines.add(ftWaitTime);
               }

               font.getClass();
               int stringHeight = 9;
               strX = viewportWidth;
               int border = 3;
               int rectHeight = border + (stringHeight + border) * lines.size();
               int x = viewportXMin + viewportWidth / 2 - strX / 2;
               int y = viewportYMax + 10;
               int strX1 = viewportXMin + viewportWidth / 2;
               int strY = y + border;
               drawFancyRect(matStack, x, y, x + strX1, y + rectHeight);

               for (Iterator var33 = lines.iterator(); var33.hasNext(); strY += stringHeight + border) {
                  ITextComponent s = (ITextComponent) var33.next();
                  this.drawCenteredStringNoShadow(matStack, font, s, strX1, strY, 16777215);
               }
            }

            setBlitOffset(blit1);
         } else if (isMouseWithinMap) {
            int blit1 = getBlitOffset();
            setBlitOffset(500);
            double[] biomeMapCoords = convertMouseCoordsToMapCoords(mouseX, mouseY, tick);
            double biomeMapX = biomeMapCoords[0];
            double biomeMapZ = biomeMapCoords[1];
            mouseXCoord = loadedMapSettings.mapToWorldX(biomeMapX);
            mouseZCoord = loadedMapSettings.mapToWorldZ(biomeMapZ);
            Object biomeName;
            if (isPositionFogged(biomeMapX, biomeMapZ)) {
               biomeName = new TranslationTextComponent("gui.lotr.map.unexplored");
            } else {
               biomePosX_int = MathHelper.floor(biomeMapX);
               int biomePosZ_int = MathHelper.floor(biomeMapZ);
               Biome biome = LOTRBiomes.getBiomeByID(loadedMapSettings.getBiomeIdAt(biomePosX_int, biomePosZ_int, world), world);
               biomeName = LOTRBiomes.getBiomeDisplayName(biome, world);
            }

            ITextComponent coords = new TranslationTextComponent("gui.lotr.map.coords", mouseXCoord, mouseZCoord);
            ITextComponent teleport = new TranslationTextComponent("gui.lotr.map.tp", LOTRKeyHandler.KEY_BIND_MAP_TELEPORT.getTranslatedKeyMessage());
            font.getClass();
            int stringHeight = 9;
            if (fullscreen) {
               renderFullscreenSubtitles(matStack, (ITextComponent) biomeName, coords);
               if (canTeleport()) {
                  matStack.pushPose();
                  matStack.translate(width / 2 - 30 - font.width(teleport) / 2, 0.0D, 0.0D);
                  renderFullscreenSubtitles(matStack, teleport);
                  matStack.popPose();
               }
            } else {
               int rectWidth = viewportWidth;
               int border = 3;
               int rectHeight = border * 3 + stringHeight * 2;
               if (canTeleport()) {
                  rectHeight += (stringHeight + border) * 2;
               }

               int x = viewportXMin + viewportWidth / 2 - rectWidth / 2;
               int y = viewportYMax + 10;
               drawFancyRect(matStack, x, y, x + rectWidth, y + rectHeight);
               strX = viewportXMin + viewportWidth / 2;
               int strY = y + border;
               this.drawCenteredStringNoShadow(matStack, font, (ITextComponent) biomeName, strX, strY, 16777215);
               strY += stringHeight + border;
               this.drawCenteredStringNoShadow(matStack, font, coords, strX, strY, 16777215);
               if (canTeleport()) {
                  this.drawCenteredStringNoShadow(matStack, font, teleport, strX, strY + (stringHeight + border) * 2, 16777215);
               }
            }

            setBlitOffset(blit1);
         }
      }

      if (!hasOverlay && hasAreasOfInfluence()) {
         renderInFront(() -> {
            ITextComponent s = new TranslationTextComponent("gui.lotr.map.areaOfInfluence.title", areaOfInfluenceFaction.getDisplayName());
            int x = viewportXMin + viewportWidth / 2;
            int y = viewportYMin + 20;
            alignmentTextRenderer.drawAlignmentText(matStack, font, x - font.width(s) / 2, y, s, 1.0F);
            if (!AreasOfInfluence.areAreasOfInfluenceEnabled(world)) {
               s = new TranslationTextComponent("gui.lotr.map.areaOfInfluence.disabled");
               alignmentTextRenderer.drawAlignmentText(matStack, font, x - font.width(s) / 2, viewportYMin + viewportHeight / 2, s, 1.0F);
            }

         });
      }

      if (zoomingMessageDisplayTicks > 0) {
         renderInFront(() -> {
            ITextComponent s = getZoomingDisplayMessage();
            int x = viewportXMin + viewportWidth / 2;
            int y = viewportYMax - 30;
            float alpha = LOTRUtil.trapezoidalIntensitySinglePulse(30 - zoomingMessageDisplayTicks, 30.0F, 0.16F, 0.0F, 1.0F);
            this.drawCenteredStringNoShadow(matStack, font, s, x, y, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
         });
      }

      super.render(matStack, mouseX, mouseY, tick);
      renderMapWidgets(matStack, mouseX, mouseY);
   }

   private void renderAreasOfInfluence(int mouseX, int mouseY, float tick) {
      mouseInAreaOfInfluence = false;
      mouseInReducedAreaOfInfluence = false;
      Faction faction = areaOfInfluenceFaction;
      if (LOTRDimensions.isDimension(faction, LOTRDimensions.MIDDLE_EARTH_WORLD_KEY)) {
         List areasOfInfluence = faction.getAreasOfInfluence().getAreas();
         if (!areasOfInfluence.isEmpty()) {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            setupMapClipping();
            RenderSystem.disableTexture();

            label64: for (int pass = 0; pass <= 2; ++pass) {
               int areaRgb = 16711680;
               if (pass == 1) {
                  areaRgb = 5570560;
               }

               if (pass == 0) {
                  areaRgb = 16733525;
               }

               Color areaColor = new Color(areaRgb);
               Iterator var12 = areasOfInfluence.iterator();

               while (true) {
                  AreaOfInfluence area;
                  float radius;
                  do {
                     if (!var12.hasNext()) {
                        continue label64;
                     }

                     area = (AreaOfInfluence) var12.next();
                     radius = area.getMapRadius();
                     if (pass == 2) {
                        --radius;
                     }

                     if (pass == 0) {
                        radius = area.getMapRadius() + faction.getAreasOfInfluence().getReducedInfluenceRange();
                     }

                     float radiusWorld = loadedMapSettings.mapToWorldDistance(radius);
                     buf.begin(9, DefaultVertexFormats.POSITION_COLOR);
                     int sides = 100;

                     for (int l = sides - 1; l >= 0; --l) {
                        float angle = (float) l / (float) sides * 2.0F * 3.1415927F;
                        double x = area.getWorldX();
                        double z = area.getWorldZ();
                        x += MathHelper.cos(angle) * radiusWorld;
                        z += MathHelper.sin(angle) * radiusWorld;
                        double[] coords = transformWorldCoords(x, z, tick);
                        buf.vertex(coords[0], coords[1], getBlitOffset()).color(areaColor.getRed(), areaColor.getGreen(), areaColor.getBlue(), areaColor.getAlpha()).endVertex();
                     }

                     tess.end();
                  } while (mouseInAreaOfInfluence && mouseInReducedAreaOfInfluence);

                  double[] coords = transformWorldCoords(area.getWorldX(), area.getWorldZ(), tick);
                  double dx = mouseX - coords[0];
                  double dy = mouseY - coords[1];
                  float rScaled = radius * zoomScale;
                  if (dx * dx + dy * dy <= rScaled * rScaled) {
                     if (pass >= 1) {
                        mouseInAreaOfInfluence = true;
                     } else if (pass == 0) {
                        mouseInReducedAreaOfInfluence = true;
                     }
                  }
               }
            }

            RenderSystem.enableTexture();
            endMapClipping();
         }
      }

   }

   private void renderCreatingMarker(MatrixStack matStack, int mouseX, int mouseY) {

      int u = 0;
      int v = 236;
      int markerX = boundCreatingMarkerMouseX(mouseX);
      int markerZ = boundCreatingMarkerMouseZ(mouseY);
      if (!isCreatingMarkerAtValidMousePosition(markerX, markerZ)) {
         v -= 10;
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      minecraft.getTextureManager().bind(MapImageTextures.MAP_ICONS);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.6F);
      LOTRClientUtil.blitFloat(this, matStack, markerX - 5, markerZ - 5, u, v, 10.0F, 10.0F);
      RenderSystem.disableBlend();
   }

   public void renderFogOfWar(MatrixStack matStack, float tick) {
      if (hasFogOfWar()) {
         getOptClientPlayerData().ifPresent(playerData -> {
            IProfiler profiler = minecraft.getProfiler();
            profiler.push("renderFogOfWar");
            double[] minMapCoords = convertMouseCoordsToMapCoords(viewportXMin, viewportYMin, tick);
            double[] maxMapCoords = convertMouseCoordsToMapCoords(viewportXMax, viewportYMax, tick);
            double fogMapXMin = minMapCoords[0];
            double fogMapXMax = maxMapCoords[0];
            double fogMapZMin = minMapCoords[1];
            double fogMapZMax = maxMapCoords[1];
            minecraft.getTextureManager().bind(MapImageTextures.FOG_OF_WAR_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            profiler.push("iterateTiles");
            playerData.getFogData().streamTilesForRendering(fogMapXMin, fogMapXMax, fogMapZMin, fogMapZMax, profiler).forEach(tile -> {
               profiler.push("transformCoords");
               double[] leftTop = transformMapCoords(((MapExplorationTile) tile).getMapLeft(), ((MapExplorationTile) tile).getMapTop(), tick);
               double[] rightBottom = transformMapCoords(((MapExplorationTile) tile).getMapRight(), ((MapExplorationTile) tile).getMapBottom(), tick);
               profiler.pop();
               float initX0 = (float) leftTop[0];
               float initX1 = (float) rightBottom[0];
               float initY0 = (float) leftTop[1];
               float initY1 = (float) rightBottom[1];
               float x0 = Math.max(initX0, viewportXMin);
               float x1 = Math.min(initX1, viewportXMax);
               float y0 = Math.max(initY0, viewportYMin);
               float y1 = Math.min(initY1, viewportYMax);
               int texSize = 256;
               int tileSize = ((MapExplorationTile) tile).getSize();
               int tileU;
               int tileV;
               int randTexture;
               if (((MapExplorationTile) tile).isThickFog()) {
                  randTexture = ((MapExplorationTile) tile).getPositionalHash() % 4;
                  tileU = randTexture % 4 * tileSize;
                  tileV = (randTexture / 4 + 2) * tileSize;
               } else {
                  randTexture = ((MapExplorationTile) tile).getPositionalHash() % 7;
                  tileU = randTexture % 4 * tileSize;
                  tileV = randTexture / 4 * tileSize;
               }

               float initWidth = initX1 - initX0;
               float initHeight = initY1 - initY0;
               float u0 = (tileU + (x0 - initX0) / initWidth * tileSize) / texSize;
               float u1 = (tileU + (x1 - initX0) / initWidth * tileSize) / texSize;
               float v0 = (tileV + (y0 - initY0) / initHeight * tileSize) / texSize;
               float v1 = (tileV + (y1 - initY0) / initHeight * tileSize) / texSize;
               profiler.push("drawFogTile");
               renderFogOfWarTile(matStack, x0, x1, y0, y1, u0, u1, v0, v1);
               profiler.pop();
            });
            profiler.pop();
            RenderSystem.disableBlend();
            profiler.pop();
         });
      }
   }

   private void renderFogOfWarTile(MatrixStack matStack, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1) {
      float z = getBlitOffset();
      Matrix4f mat = matStack.last().pose();
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buf = tess.getBuilder();
      buf.begin(7, DefaultVertexFormats.POSITION_TEX);
      buf.vertex(mat, x0, y1, z).uv(u0, v1).endVertex();
      buf.vertex(mat, x1, y1, z).uv(u1, v1).endVertex();
      buf.vertex(mat, x1, y0, z).uv(u1, v0).endVertex();
      buf.vertex(mat, x0, y0, z).uv(u0, v0).endVertex();
      tess.end();
   }

   private void renderFullscreenSubtitles(MatrixStack matStack, ITextComponent... lines) {
      int strX = viewportXMin + viewportWidth / 2;
      int strY = viewportYMax + 7;
      font.getClass();
      int border = 9 + 3;
      if (lines.length == 1) {
         strY += border / 2;
      }

      ITextComponent[] var6 = lines;
      int var7 = lines.length;

      for (int var8 = 0; var8 < var7; ++var8) {
         ITextComponent s = var6[var8];
         this.drawCenteredStringNoShadow(matStack, font, s, strX, strY, 16777215);
         strY += border;
      }

   }

   private void renderGraduatedRects(MatrixStack matStack, int x1, int y1, int x2, int y2, int c1, int c2, int w) {
      float[] rgb1 = new Color(c1).getColorComponents((float[]) null);
      float[] rgb2 = new Color(c2).getColorComponents((float[]) null);

      for (int l = w - 1; l >= 0; --l) {
         float f = (float) l / (float) (w - 1);
         float r = rgb1[0] + (rgb2[0] - rgb1[0]) * f;
         float g = rgb1[1] + (rgb2[1] - rgb1[1]) * f;
         float b = rgb1[2] + (rgb2[2] - rgb1[2]) * f;
         int color = LOTRClientUtil.getRGBA(new Color(r, g, b).getRGB(), 1.0F);
         fill(matStack, x1 - l, y1 - l, x2 + l, y2 + l, color);
      }

   }

   private void renderInFront(Runnable renderer) {
      RenderSystem.pushMatrix();
      RenderSystem.translatef(0.0F, 0.0F, 300.0F);
      renderer.run();
      RenderSystem.popMatrix();
   }

   private void renderLabels(MatrixStack matStack, float tick) {
      if (hasMapLabels()) {
         setupMapClipping();
         List labels = loadedMapSettings.getLabels();
         Iterator var4 = labels.iterator();

         while (true) {
            MapLabel label;
            double x;
            double y;
            float alpha;
            while (true) {
               float zoomlerp;
               do {
                  do {
                     if (!var4.hasNext()) {
                        endMapClipping();
                        return;
                     }

                     label = (MapLabel) var4.next();
                     double[] pos = transformMapCoords(label.getMapX(), label.getMapZ(), tick);
                     x = pos[0];
                     y = pos[1];
                     zoomlerp = (zoomExp - label.getMinZoom()) / (label.getMaxZoom() - label.getMinZoom());
                  } while (zoomlerp <= 0.0F);
               } while (zoomlerp >= 1.0F);

               alpha = (0.5F - Math.abs(zoomlerp - 0.5F)) / 0.5F;
               alpha *= 0.7F;
               if (!isOSRS()) {
                  break;
               }

               if (alpha >= 0.3F) {
                  alpha = 1.0F;
                  break;
               }
            }

            matStack.pushPose();
            matStack.translate(x, y, 0.0D);
            float labelScale = zoomScale * label.getScale();
            matStack.scale(labelScale, labelScale, labelScale);
            if (!isOSRS()) {
               matStack.mulPose(Vector3f.ZP.rotationDegrees(label.getAngle()));
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.alphaFunc(516, 0.01F);
            ITextComponent labelName = label.getDisplayName(minecraft.level);
            int strX = -font.width(labelName) / 2;
            font.getClass();
            int strY = -9 / 2;
            if (isOSRS()) {
               if (label.getScale() > 2.5F) {
                  font.draw(matStack, labelName, strX + 1, strY + 1, LOTRClientUtil.getRGBAForFontRendering(0, alpha));
                  font.draw(matStack, labelName, strX, strY, LOTRClientUtil.getRGBAForFontRendering(16755200, alpha));
               } else {
                  font.draw(matStack, labelName, strX + 1, strY + 1, LOTRClientUtil.getRGBAForFontRendering(0, alpha));
                  font.draw(matStack, labelName, strX, strY, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
               }
            } else {
               font.draw(matStack, labelName, strX, strY, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
            }

            RenderSystem.defaultAlphaFunc();
            RenderSystem.disableBlend();
            matStack.popPose();
         }
      }
   }

   public void renderMapAndOverlay(MatrixStack matStack, float tick, boolean sepia, float alpha, boolean overlay) {
      double lerpPosX = lerpPosX(tick);
      double lerpPosY = lerpPosY(tick);
      int mapXMin_W = viewportXMin;
      int mapXMax_W = viewportXMax;
      int mapYMin_W = viewportYMin;
      int mapYMax_W = viewportYMax;
      float mapScaleX = viewportWidth / zoomScale;
      float mapScaleY = viewportHeight / zoomScale;
      float minU = (float) (lerpPosX - mapScaleX / 2.0F) / loadedMapSettings.getWidth();
      float maxU = (float) (lerpPosX + mapScaleX / 2.0F) / loadedMapSettings.getWidth();
      float minV = (float) (lerpPosY - mapScaleY / 2.0F) / loadedMapSettings.getHeight();
      float maxV = (float) (lerpPosY + mapScaleY / 2.0F) / loadedMapSettings.getHeight();
      if (minU < 0.0F) {
         mapXMin_W = viewportXMin + Math.round((0.0F - minU) * loadedMapSettings.getWidth() * zoomScale);
         minU = 0.0F;
         if (maxU < 0.0F) {
            maxU = 0.0F;
            mapXMax_W = mapXMin_W;
         }
      }

      if (maxU > 1.0F) {
         mapXMax_W = viewportXMax - Math.round((maxU - 1.0F) * loadedMapSettings.getWidth() * zoomScale);
         maxU = 1.0F;
         if (minU > 1.0F) {
            minU = 1.0F;
            mapXMin_W = mapXMax_W;
         }
      }

      if (minV < 0.0F) {
         mapYMin_W = viewportYMin + Math.round((0.0F - minV) * loadedMapSettings.getHeight() * zoomScale);
         minV = 0.0F;
         if (maxV < 0.0F) {
            maxV = 0.0F;
            mapYMax_W = mapYMin_W;
         }
      }

      if (maxV > 1.0F) {
         mapYMax_W = viewportYMax - Math.round((maxV - 1.0F) * loadedMapSettings.getHeight() * zoomScale);
         maxV = 1.0F;
         if (minV > 1.0F) {
            minV = 1.0F;
            mapYMin_W = mapYMax_W;
         }
      }

      MapImageTextures.drawMap(matStack, minecraft.player, sepia, mapXMin_W, mapXMax_W, mapYMin_W, mapYMax_W, getBlitOffset(), minU, maxU, minV, maxV, alpha);
      if (overlay) {
         MapImageTextures.drawMapOverlay(matStack, minecraft.player, viewportXMin, viewportXMax, viewportYMin, viewportYMax, getBlitOffset(), minU, maxU, minV, maxV);
      }

   }

   private void renderMapWidgets(MatrixStack matStack, int mouseX, int mouseY) {
      boolean fineZoom = isFineMapMovement();
      boolean quickZoom = isQuickMapMovement();
      widgetZoomIn.visible = !hasOverlay;
      widgetZoomIn.setTexVOffset(zoomPower < 4.0F ? 0 : 1);
      widgetZoomIn.setTooltip(quickZoom ? "zoomIn.quick" : fineZoom ? "zoomIn.fine" : "zoomIn");
      widgetZoomOut.visible = !hasOverlay;
      widgetZoomOut.setTexVOffset(zoomPower > -3.0F ? 0 : 1);
      widgetZoomOut.setTooltip(quickZoom ? "zoomOut.quick" : fineZoom ? "zoomOut.fine" : "zoomOut");
      widgetFullScreen.visible = !hasOverlay;
      widgetSepia.visible = !hasOverlay;
      widgetLabels.visible = !hasOverlay;
      widgetToggleMapWPs.visible = !hasOverlay;
      widgetToggleMapWPs.setTexVOffset(showMapWaypoints() ? 0 : 1);
      widgetToggleCustomWPs.visible = !hasOverlay;
      widgetToggleCustomWPs.setTexVOffset(showCustomWaypoints() ? 0 : 1);
      widgetToggleMarkers.visible = !hasOverlay;
      widgetToggleMarkers.setTexVOffset(showMarkers() ? 0 : 1);
      widgetNewMarker.visible = !hasOverlay;
      widgetNewMarker.setTexVOffset(canCreateNewMarker() ? 0 : 1);
      int numMarkers = getOptClientPlayerData().map(pd -> pd.getMapMarkerData().getMarkers().size()).orElse(0);
      if (numMarkers > 0) {
         widgetNewMarker.setTooltip("newMarker.count", numMarkers, 64);
      } else {
         widgetNewMarker.setTooltip("newMarker");
      }

      widgetToggleShowLocation.visible = !hasOverlay;
      widgetToggleShowLocation.setTexUOffset(showLocationToOthers() ? 0 : 1);
      widgetToggleShowLocation.setTooltip(showLocationToOthers() ? "toggleShowLocation.shown" : "toggleShowLocation.hidden");
      MapWidget mouseOverWidget = null;
      Iterator var8 = mapWidgets.iterator();

      while (var8.hasNext()) {
         MapWidget widget = (MapWidget) var8.next();
         if (widget.visible) {
            minecraft.getTextureManager().bind(MapImageTextures.MAP_ICONS);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(matStack, widget.getXPos(), widget.getYPos(), widget.getTexU(), widget.getTexV(), widget.width, widget.width);
            if (widget.isMouseOver(mouseX, mouseY)) {
               mouseOverWidget = widget;
            }
         }
      }

      if (mouseOverWidget != null) {
         int stringWidth = 200;
         List desc = font.split(mouseOverWidget.getTooltip(), stringWidth);
         this.renderTooltip(matStack, desc, mouseX, mouseY);
      }

   }

   private void renderMarker(MatrixStack matStack, MapMarker marker, float markerX, float markerY, boolean mouseOver, float alpha) {

      int u = marker.getIcon().getU(mouseOver);
      int v = marker.getIcon().getV(mouseOver);
      minecraft.getTextureManager().bind(MapImageTextures.MAP_ICONS);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
      LOTRClientUtil.blitFloat(this, matStack, markerX - 5.0F, markerY - 5.0F, u, v, 10.0F, 10.0F);
   }

   private void renderMarkers(MatrixStack matStack, int pass, int mouseX, int mouseY, float tick) {
      if (showMarkers()) {
         float markerAlpha = calcZoomedWaypointAlpha();
         if (markerAlpha > 0.0F) {
            setupMapClipping();
            List markers = getVisibleMarkers();
            Iterator var8 = markers.iterator();

            label59: while (true) {
               MapMarker marker;
               float x;
               float y;
               byte clip;
               do {
                  do {
                     do {
                        do {
                           do {
                              if (!var8.hasNext()) {
                                 endMapClipping();
                                 break label59;
                              }

                              marker = (MapMarker) var8.next();
                              double[] pos = transformWorldCoords(marker.getWorldX(), marker.getWorldZ(), tick);
                              x = (float) pos[0];
                              y = (float) pos[1];
                              clip = 100;
                           } while (x < viewportXMin - clip);
                        } while (x > viewportXMax + clip);
                     } while (y < viewportYMin - clip);
                  } while (y > viewportYMax + clip);
               } while (pass != 0);

               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               boolean highlight = marker == mouseOverObject || marker == selectedObject;
               renderMarker(matStack, marker, x, y, highlight, markerAlpha);
               RenderSystem.disableBlend();
            }
         }

         if (pass == 1 && !hasOverlay && mouseOverObject instanceof MapMarker) {
            renderMarkerTooltip(matStack, (MapMarker) mouseOverObject, false, mouseX, mouseY, 1.0F);
         }

      }
   }

   private void renderMarkerTooltip(MatrixStack matStack, MapMarker marker, boolean selected, int mouseX, int mouseY, float tick) {
      markerTooltip.setMapDimensions(viewportXMin, viewportXMax, viewportYMin, viewportYMax);
      markerTooltip.setSelectionProgress(prevObjectSelectTick, objectSelectTick, 6, tick);
      markerTooltip.render(matStack, marker, selected, mouseX, mouseY, tick);
   }

   private float renderPlayerIconAndReturnDistance(MatrixStack matStack, GameProfile profile, float playerX, float playerY, int mouseX, int mouseY) {
      NetworkPlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(profile.getId());
      if (playerInfo != null && playerInfo.isSkinLoaded()) {
         ResourceLocation skin = playerInfo.getSkinLocation();
         Matrix4f mat = matStack.last().pose();
         Tessellator tess = Tessellator.getInstance();
         BufferBuilder buf = tess.getBuilder();
         int iconWidthHalf = 4;
         int iconBorder = iconWidthHalf + 1;
         playerX = Math.max(viewportXMin + iconBorder, playerX);
         playerX = Math.min(viewportXMax - iconBorder - 1, playerX);
         playerY = Math.max(viewportYMin + iconBorder, playerY);
         playerY = Math.min(viewportYMax - iconBorder - 1, playerY);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableAlphaTest();
         minecraft.getTextureManager().bind(skin);
         float skinWidth = 64.0F;
         float skinHeight = 64.0F;
         float iconMinU = 8.0F / skinWidth;
         float iconMaxU = 16.0F / skinWidth;
         float iconMinV = 8.0F / skinHeight;
         float iconMaxV = 16.0F / skinHeight;
         float playerX_f = playerX + 0.5F;
         float playerY_f = playerY + 0.5F;
         int z = getBlitOffset();
         buf.begin(7, DefaultVertexFormats.POSITION_TEX);
         buf.vertex(mat, playerX_f - iconWidthHalf, playerY_f + iconWidthHalf, z).uv(iconMinU, iconMaxV).endVertex();
         buf.vertex(mat, playerX_f + iconWidthHalf, playerY_f + iconWidthHalf, z).uv(iconMaxU, iconMaxV).endVertex();
         buf.vertex(mat, playerX_f + iconWidthHalf, playerY_f - iconWidthHalf, z).uv(iconMaxU, iconMinV).endVertex();
         buf.vertex(mat, playerX_f - iconWidthHalf, playerY_f - iconWidthHalf, z).uv(iconMinU, iconMinV).endVertex();
         tess.end();
         iconMinU = 40.0F / skinWidth;
         iconMaxU = 48.0F / skinWidth;
         iconMinV = 8.0F / skinHeight;
         iconMaxV = 16.0F / skinHeight;
         buf.begin(7, DefaultVertexFormats.POSITION_TEX);
         buf.vertex(mat, playerX_f - iconWidthHalf - 0.5F, playerY_f + iconWidthHalf + 0.5F, z).uv(iconMinU, iconMaxV).endVertex();
         buf.vertex(mat, playerX_f + iconWidthHalf + 0.5F, playerY_f + iconWidthHalf + 0.5F, z).uv(iconMaxU, iconMaxV).endVertex();
         buf.vertex(mat, playerX_f + iconWidthHalf + 0.5F, playerY_f - iconWidthHalf - 0.5F, z).uv(iconMaxU, iconMinV).endVertex();
         buf.vertex(mat, playerX_f - iconWidthHalf - 0.5F, playerY_f - iconWidthHalf - 0.5F, z).uv(iconMinU, iconMinV).endVertex();
         tess.end();
         RenderSystem.disableAlphaTest();
         float dx = playerX - mouseX;
         float dy = playerY - mouseY;
         return MathHelper.sqrt(dx * dx + dy * dy);
      }
      return Float.MAX_VALUE;
   }

   private void renderPlayers(MatrixStack matStack, int mouseX, int mouseY, float tick) {
      String mouseOverPlayerName = null;
      double mouseOverPlayerX = 0.0D;
      double mouseOverPlayerY = 0.0D;
      double distanceMouseOverPlayer = Double.MAX_VALUE;
      int iconWidthHalf = 4;
      Map playersToRender = new HashMap(MapPlayerLocationHolder.getPlayerLocations());
      if (isMiddleEarth()) {
         playersToRender.put(minecraft.player.getUUID(), MapPlayerLocation.ofPlayer(minecraft.player));
      }

      Iterator var14 = playersToRender.entrySet().iterator();

      while (var14.hasNext()) {
         Entry entry = (Entry) var14.next();
         MapPlayerLocation info = (MapPlayerLocation) entry.getValue();
         GameProfile profile = info.profile;
         String playerName = profile.getName();
         double playerPosX = info.posX;
         double playerPosZ = info.posZ;
         double[] pos = transformWorldCoords(playerPosX, playerPosZ, tick);
         float playerX = (float) pos[0];
         float playerY = (float) pos[1];
         float distToPlayer = renderPlayerIconAndReturnDistance(matStack, profile, playerX, playerY, mouseX, mouseY);
         if (distToPlayer <= iconWidthHalf + 3 && distToPlayer <= distanceMouseOverPlayer) {
            mouseOverPlayerName = playerName;
            mouseOverPlayerX = playerX;
            mouseOverPlayerY = playerY;
            distanceMouseOverPlayer = distToPlayer;
         }
      }

      if (mouseOverPlayerName != null && !hasOverlay) {
         final String sus = mouseOverPlayerName;
         final double sus1 = mouseOverPlayerX;
         final double sus2 = mouseOverPlayerY;
         renderInFront(() -> {
            int strWidth = font.width(sus);
            font.getClass();
            int strHeight = 9;
            int rectX = (int) Math.round(sus1);
            int rectY = (int) Math.round(sus2);
            rectY += iconWidthHalf + 3;
            int border = 3;
            int rectWidth = strWidth + border * 2;
            rectX -= rectWidth / 2;
            int rectHeight = strHeight + border * 2;
            int mapBorder2 = 2;
            rectX = Math.max(rectX, viewportXMin + mapBorder2);
            rectX = Math.min(rectX, viewportXMax - mapBorder2 - rectWidth);
            rectY = Math.max(rectY, viewportYMin + mapBorder2);
            rectY = Math.min(rectY, viewportYMax - mapBorder2 - rectHeight);
            drawFancyRect(matStack, rectX, rectY, rectX + rectWidth, rectY + rectHeight);
            font.draw(matStack, new StringTextComponent(sus), rectX + border, rectY + border, 16777215);
         });
      }

   }

   private void renderRoads(MatrixStack matStack, float tick) {
      if (showMapWaypoints() || showCustomWaypoints()) {
         this.renderRoads(matStack, tick, hasMapLabels());
      }
   }

   public void renderRoads(MatrixStack matStack, float tick, boolean labels) {
      float roadAlpha = calcZoomedWaypointAlpha();
      if (roadAlpha > 0.0F) {
         float mapScale = loadedMapSettings.getScaleFactor();
         int interval = Math.round(mapScale * 3.125F / zoomScaleStable);
         interval = Math.max(interval, 1);
         Iterator var7 = loadedMapSettings.getRoads().iterator();

         while (var7.hasNext()) {
            Road road = (Road) var7.next();
            Iterator var9 = road.getSections().iterator();

            while (var9.hasNext()) {
               RoadSection section = (RoadSection) var9.next();
               RoadPoint[] sectionPoints = section.getRoutePoints();

               for (int pointIndex = 0; pointIndex < sectionPoints.length; pointIndex += interval) {
                  RoadPoint point = sectionPoints[pointIndex];
                  double[] pos = transformMapCoords(point.getMapX(), point.getMapZ(), tick);
                  float x = (float) pos[0];
                  float y = (float) pos[1];
                  float roadWidth = 1.0F;
                  float roadWidthLess1 = roadWidth - 1.0F;
                  float x0 = x - roadWidthLess1;
                  float x1 = x + roadWidth;
                  float y0 = y - roadWidthLess1;
                  float y1 = y + roadWidth;
                  float zoomlerp;
                  float roadNameScale;
                  if (x0 >= viewportXMin && x1 <= viewportXMax && y0 >= viewportYMin && y1 <= viewportYMax) {
                     float roadR = 0.0F;
                     zoomlerp = 0.0F;
                     roadNameScale = 0.0F;
                     float z = getBlitOffset();
                     RenderSystem.disableTexture();
                     RenderSystem.enableBlend();
                     RenderSystem.defaultBlendFunc();
                     Matrix4f mat = matStack.last().pose();
                     Tessellator tess = Tessellator.getInstance();
                     BufferBuilder buf = tess.getBuilder();
                     buf.begin(7, DefaultVertexFormats.POSITION_COLOR);
                     buf.vertex(mat, x0, y1, z).color(roadR, zoomlerp, roadNameScale, roadAlpha).endVertex();
                     buf.vertex(mat, x1, y1, z).color(roadR, zoomlerp, roadNameScale, roadAlpha).endVertex();
                     buf.vertex(mat, x1, y0, z).color(roadR, zoomlerp, roadNameScale, roadAlpha).endVertex();
                     buf.vertex(mat, x0, y0, z).color(roadR, zoomlerp, roadNameScale, roadAlpha).endVertex();
                     tess.end();
                     RenderSystem.disableBlend();
                     RenderSystem.enableTexture();
                  }

                  if (labels) {
                     int clip = 100;
                     if (x >= viewportXMin - clip && x <= viewportXMax + clip && y >= viewportYMin - clip && y <= viewportYMax + clip) {
                        zoomlerp = (zoomExp - -1.0F) / 4.0F;
                        zoomlerp = Math.min(zoomlerp, 1.0F);
                        roadNameScale = zoomlerp * 0.75F;
                        ITextComponent name = road.getDisplayName();
                        int roadNameWidth = font.width(name);
                        int nameInterval = (int) ((roadNameWidth * 2 + 200) * mapScale * 0.78125F / zoomScaleStable);
                        if (pointIndex % nameInterval < interval) {
                           boolean endNear = false;
                           RouteRoadPoint[] var30 = section.getStartAndEndPoints();
                           int var31 = var30.length;

                           for (int var32 = 0; var32 < var31; ++var32) {
                              RouteRoadPoint end = var30[var32];
                              int endpointOverlapDistance = 10;
                              MapWaypoint endWp = end.getCorrespondingWaypoint();
                              if (endWp != null) {
                                 ITextComponent endWpName = endWp.getDisplayName();
                                 int endWpNameWidth = font.width(endWpName);
                                 endpointOverlapDistance = endWpNameWidth / 2 + 10;
                              }

                              double overlapWidth = roadNameWidth / 2.0D * roadNameScale + endpointOverlapDistance;
                              double overlapWidthSq = overlapWidth * overlapWidth;
                              double[] endPos = transformMapCoords(end.getMapX(), end.getMapZ(), tick);
                              double endX = endPos[0];
                              double endY = endPos[1];
                              double dx = x - endX;
                              double dy = y - endY;
                              double dSq = dx * dx + dy * dy;
                              if (dSq < overlapWidthSq) {
                                 endNear = true;
                              }
                           }

                           if (!endNear && zoomlerp > 0.0F) {
                              setupMapClipping();
                              matStack.pushPose();
                              matStack.translate(x, y, 0.0D);
                              matStack.scale(roadNameScale, roadNameScale, roadNameScale);
                              RoadPoint nextPoint = sectionPoints[Math.min(pointIndex + 1, sectionPoints.length - 1)];
                              RoadPoint prevPoint = sectionPoints[Math.max(pointIndex - 1, 0)];
                              double grad = (nextPoint.getMapZ() - prevPoint.getMapZ()) / (nextPoint.getMapX() - prevPoint.getMapX());
                              float angle = (float) Math.atan(grad);
                              angle = (float) Math.toDegrees(angle);
                              if (Math.abs(angle) > 90.0F) {
                                 angle += 180.0F;
                              }

                              matStack.mulPose(Vector3f.ZP.rotationDegrees(angle));
                              float alpha = zoomlerp * 0.8F;
                              RenderSystem.enableBlend();
                              RenderSystem.defaultBlendFunc();
                              int strX = -roadNameWidth / 2;
                              int strY = -15;
                              font.draw(matStack, name, strX + 1, strY + 1, LOTRClientUtil.getRGBAForFontRendering(0, alpha));
                              font.draw(matStack, name, strX, strY, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
                              RenderSystem.disableBlend();
                              matStack.popPose();
                              endMapClipping();
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void renderWaypoints(MatrixStack matStack, int pass, int mouseX, int mouseY, float tick) {
      if (showMapWaypoints() || showCustomWaypoints() || showHiddenSharedCustomWaypoints()) {
         this.renderWaypoints(matStack, pass, mouseX, mouseY, tick, hasMapLabels());
      }
   }

   public void renderWaypoints(MatrixStack matStack, int pass, int mouseX, int mouseY, float tick, boolean mapLabels) {
      this.renderWaypoints(matStack, getVisibleWaypoints(), pass, mouseX, mouseY, tick, mapLabels);
   }

   private void renderWaypoints(MatrixStack matStack, List waypoints, int pass, int mouseX, int mouseY, float tick, boolean mapLabels) {
      setupMapClipping();
      float wpAlpha = calcZoomedWaypointAlpha();
      if (wpAlpha > 0.0F) {
         Iterator var9 = waypoints.iterator();

         label76: while (true) {
            Waypoint waypoint;
            float x;
            float y;
            byte clip;
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var9.hasNext()) {
                              break label76;
                           }

                           waypoint = (Waypoint) var9.next();
                           double[] pos = transformWorldCoords(waypoint.getWorldX(), waypoint.getWorldZ(), tick);
                           x = (float) pos[0];
                           y = (float) pos[1];
                           clip = 100;
                        } while (x < viewportXMin - clip);
                     } while (x > viewportXMax + clip);
                  } while (y < viewportYMin - clip);
               } while (y > viewportYMax + clip);
            } while (pass != 0);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float zoomlerp;
            if (isOSRS()) {
               zoomlerp = 0.33F;
               matStack.pushPose();
               matStack.scale(0.33F, 0.33F, 1.0F);
               minecraft.getTextureManager().bind(MapImageTextures.OSRS_ICONS);
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               LOTRClientUtil.blitFloat(this, matStack, x / 0.33F - 8.0F, y / 0.33F - 8.0F, 0.0F, 0.0F, 15.0F, 15.0F);
               matStack.popPose();
            } else {
               Waypoint.WaypointDisplayState state = waypoint.getDisplayState(minecraft.player);
               minecraft.getTextureManager().bind(MapImageTextures.MAP_ICONS);
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, wpAlpha);
               boolean highlight = waypoint == mouseOverObject || waypoint == selectedObject;
               LOTRClientUtil.blitFloat(this, matStack, x - 3.0F, y - 3.0F, highlight ? (float) state.highlightIconU : (float) state.iconU, highlight ? (float) state.highlightIconV : (float) state.iconV, 6.0F, 6.0F);
            }

            RenderSystem.disableBlend();
            if (mapLabels) {
               zoomlerp = (zoomExp - -1.0F) / 4.0F;
               zoomlerp = Math.min(zoomlerp, 1.0F);
               if (zoomlerp > 0.0F) {
                  matStack.pushPose();
                  matStack.translate(x, y, 0.0D);
                  matStack.scale(zoomlerp, zoomlerp, 1.0F);
                  float alpha = zoomlerp * 0.8F;
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
                  ITextComponent s = waypoint.getDisplayName();
                  int strX = -font.width(s) / 2;
                  int strY = -15;
                  font.draw(matStack, s, strX + 1, strY + 1, LOTRClientUtil.getRGBAForFontRendering(0, alpha));
                  font.draw(matStack, s, strX, strY, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
                  RenderSystem.disableBlend();
                  matStack.popPose();
               }
            }
         }
      }

      if (pass == 1 && !hasOverlay && mouseOverObject instanceof Waypoint) {
         renderWaypointTooltip(matStack, (Waypoint) mouseOverObject, false, mouseX, mouseY, 1.0F);
      }

      endMapClipping();
   }

   private void renderWaypointTooltip(MatrixStack matStack, Waypoint waypoint, boolean selected, int mouseX, int mouseY, float tick) {
      waypointTooltip.setMapDimensions(viewportXMin, viewportXMax, viewportYMin, viewportYMax);
      waypointTooltip.setSelectionProgress(prevObjectSelectTick, objectSelectTick, 6, tick);
      waypointTooltip.render(matStack, waypoint, selected, mouseX, mouseY, tick);
   }

   private void requestIsOp() {
      if (!sentOpRequestPacket) {
         CPacketIsOpRequest packet = new CPacketIsOpRequest();
         LOTRPacketHandler.sendToServer(packet);
         sentOpRequestPacket = true;
      }

   }

   private void selectObject(SelectableMapObject object) {
      selectedObject = object;
      if (selectedObject != null) {
         objectSelectTick = 6;
      } else {
         objectSelectTick = 0;
      }

      prevObjectSelectTick = objectSelectTick;
      waypointTooltip.onSelect(selectedObject instanceof Waypoint ? (Waypoint) selectedObject : null);
      markerTooltip.onSelect(selectedObject instanceof MapMarker ? (MapMarker) selectedObject : null);
      if (selectedObject instanceof Waypoint) {
         playWaypointSelectSound();
      } else if (selectedObject instanceof MapMarker) {
         playMarkerSelectSound();
      }

   }

   public void setAreasOfInfluence(Faction faction) {
      areaOfInfluenceFaction = faction;
   }

   public void setMapViewportAndPositionAndScale(int xMin, int xMax, int yMin, int yMax, double x, double y, float scale, float scaleExp, float scaleStable) {
      viewportXMin = xMin;
      viewportXMax = xMax;
      viewportYMin = yMin;
      viewportYMax = yMax;
      viewportWidth = viewportXMax - viewportXMin;
      viewportHeight = viewportYMax - viewportYMin;
      prevPosX = posX = x;
      prevPosY = posY = y;
      zoomScale = scale;
      zoomExp = scaleExp;
      zoomScaleStable = scaleStable;
      keepMapPositionWithinBounds();
   }

   private void setupMapClipping() {
      double scale = minecraft.getWindow().getGuiScale();
      GL11.glEnable(3089);
      GL11.glScissor((int) (viewportXMin * scale), (int) ((height - viewportYMax) * scale), (int) (viewportWidth * scale), (int) (viewportHeight * scale));
   }

   private void setupMapDimensions() {
      if (fullscreen) {
         viewportXMin = 30;
         viewportXMax = width - 30;
         viewportYMin = 30;
         viewportYMax = height - 30;
      } else {
         int windowWidth = 312;
         viewportXMin = width / 2 - windowWidth / 2;
         viewportXMax = width / 2 + windowWidth / 2;
         viewportYMin = guiTop;
         viewportYMax = guiTop + 200;
      }

      viewportWidth = viewportXMax - viewportXMin;
      viewportHeight = viewportYMax - viewportYMin;
   }

   private void setupMapWidgets() {
      mapWidgets.clear();
      mapWidgets.add(widgetZoomIn = new MapWidget(this, viewportXMin + 6, viewportYMin + 6, 10, "zoomIn", 30, 0, () -> {
         if (zoomTicks == 0 && zoomPower < 4.0F) {
            zoomIn();
            return true;
         }
         return false;
      }));
      mapWidgets.add(widgetZoomOut = new MapWidget(this, viewportXMin + 6, viewportYMin + 20, 10, "zoomOut", 40, 0, () -> {
         if (zoomTicks == 0 && zoomPower > -3.0F) {
            zoomOut();
            return true;
         }
         return false;
      }));
      mapWidgets.add(widgetRecentre = new MapWidget(this, viewportXMin + 20, viewportYMin + 6, 10, "recentre", 50, 10, () -> {
         if (!isRecentringOnPlayer()) {
            recentreMapOnPlayer();
            return true;
         }
         return false;
      }));
      mapWidgets.add(widgetFullScreen = new MapWidget(this, viewportXMin + 34, viewportYMin + 6, 10, "fullScreen", 50, 0, () -> {
         fullscreen = !fullscreen;
         this.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
         return true;
      }));
      mapWidgets.add(widgetSepia = new MapWidget(this, viewportXMin + 48, viewportYMin + 6, 10, "sepia", 60, 0, () -> {
         LOTRConfig.CLIENT.toggleSepia();
         return true;
      }));
      mapWidgets.add(widgetLabels = new MapWidget(this, viewportXMax - 16, viewportYMin + 6, 10, "labels", 70, 0, () -> {
         toggleMapLabels();
         return true;
      }));
      mapWidgets.add(widgetToggleMapWPs = new MapWidget(this, viewportXMax - 72, viewportYMin + 6, 10, "toggleMapWPs", 80, 0, () -> {
         getOptClientPlayerData().ifPresent(pd -> {
            pd.getFastTravelData().toggleShowMapWaypointsAndSendToServer();
         });
         return true;
      }));
      mapWidgets.add(widgetToggleCustomWPs = new MapWidget(this, viewportXMax - 58, viewportYMin + 6, 10, "toggleCustomWPs", 90, 0, () -> {
         getOptClientPlayerData().ifPresent(pd -> {
            pd.getFastTravelData().toggleShowCustomWaypointsAndSendToServer();
         });
         return true;
      }));
      mapWidgets.add(widgetToggleShowLocation = new MapWidget(this, viewportXMax - 16, viewportYMin + 20, 10, "toggleShowLocation", 60, 10, () -> {
         getOptClientPlayerData().ifPresent(pd -> {
            pd.getMiscData().toggleShowMapLocationAndSendToServer();
         });
         return true;
      }));
      mapWidgets.add(widgetToggleMarkers = new MapWidget(this, viewportXMax - 44, viewportYMin + 6, 10, "toggleMarkers", 110, 0, () -> {
         getOptClientPlayerData().ifPresent(pd -> {
            pd.getMapMarkerData().toggleShowMarkersAndSendToServer();
         });
         return true;
      }));
      mapWidgets.add(widgetNewMarker = new MapWidget(this, viewportXMax - 30, viewportYMin + 6, 10, "newMarker", 120, 0, () -> {
         if (!creatingMarker && canCreateNewMarker()) {
            creatingMarker = true;
            return true;
         }
         return false;
      }));
      children.addAll(mapWidgets);
   }

   private void setupScrollBars() {
   }

   private void setZoom(float newZoomPower) {
      prevZoomPower = zoomPower;
      zoomPower = MathHelper.clamp(newZoomPower, -3.0F, 4.0F);
      prevZoomTicks = zoomTicks = 6;
      zoomingMessageDisplayTicks = 30;
      zoomingMessageIsZoomIn = zoomPower > prevZoomPower;
      selectObject((SelectableMapObject) null);
   }

   private boolean showCustomWaypoints() {
      return getOptClientPlayerData().map(pd -> pd.getFastTravelData().getShowCustomWaypoints()).orElse(false);
   }

   private boolean showHiddenSharedCustomWaypoints() {
      return true;
   }

   private boolean showLocationToOthers() {
      return getOptClientPlayerData().map(pd -> pd.getMiscData().getShowMapLocation()).orElse(true);
   }

   private boolean showMapWaypoints() {
      return getOptClientPlayerData().map(pd -> pd.getFastTravelData().getShowMapWaypoints()).orElse(true);
   }

   private boolean showMarkers() {
      return getOptClientPlayerData().map(pd -> pd.getMapMarkerData().getShowMarkers()).orElse(false);
   }

   @Override
   public void tick() {
      super.tick();
      prevPosX = posX;
      prevPosY = posY;
      handleMapKeyboardMovement();
      posX += posXMove;
      posY += posYMove;
      if (recentreTicks > 0) {
         --recentreTicks;
         float lerp = (6 - recentreTicks) / 6.0F;
         posX = MathHelper.lerp(lerp, recentreFromX, recentreToX);
         posY = MathHelper.lerp(lerp, recentreFromY, recentreToY);
      }

      keepMapPositionWithinBounds();
      if (zoomTicks <= 0 && prevZoomTicks > 0) {
         prevZoomPower = zoomPower;
      }

      prevZoomTicks = zoomTicks;
      if (zoomTicks > 0) {
         --zoomTicks;
      }

      if (zoomingMessageDisplayTicks > 0) {
         --zoomingMessageDisplayTicks;
      }

      prevObjectSelectTick = objectSelectTick;
      if (objectSelectTick > 0) {
         --objectSelectTick;
      }

      waypointTooltip.tick();
      markerTooltip.tick();
   }

   private void toggleMapLabels() {
      LOTRConfig.CLIENT.toggleMapLabels();
   }

   public double[] transformMapCoords(double x, double z, float tick) {
      x -= lerpPosX(tick);
      z -= lerpPosY(tick);
      x *= zoomScale;
      z *= zoomScale;
      x += viewportXMin + viewportWidth / 2;
      z += viewportYMin + viewportHeight / 2;
      return new double[] { x, z };
   }

   public double[] transformWorldCoords(double x, double z, float tick) {
      x = loadedMapSettings.worldToMapX_frac(x);
      z = loadedMapSettings.worldToMapZ_frac(z);
      return transformMapCoords(x, z, tick);
   }

   private void zoomIn() {
      setZoom(zoomPower + getZoomIncrement());
   }

   private void zoomOut() {
      setZoom(zoomPower - getZoomIncrement());
   }

   private static boolean isOSRS() {
      return false;
   }

   public static void playMarkerSelectSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.WOOL_BREAK, 1.0F));
   }

   public static void playMarkerUpdateSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0F));
   }

   public static void playWaypointSelectSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F));
   }

   public class MapDragListener implements IGuiEventListener {
      private boolean mouseDown;

      @Override
      public boolean isMouseOver(double x, double y) {
         if (!isMouseWithinMap) {
            return false;
         }
         return !hasOverlay && !isFacScrolling && !isRecentringOnPlayer();
      }

      @Override
      public boolean mouseClicked(double x, double y, int code) {
         if (code == 0 && isMouseOver(x, y)) {
            mouseDown = true;
            selectObject(mouseOverObject);
            return true;
         }
         return false;
      }

      @Override
      public boolean mouseDragged(double x, double y, int code, double dx, double dy) {
         if (!mouseDown || code != 0) {
            return false;
         }
         posX = posX - dx / zoomScale;
         posY = posY - dy / zoomScale;
         keepMapPositionWithinBounds();
         if (dx != 0.0D || dy != 0.0D) {
            selectObject((SelectableMapObject) null);
         }

         return true;
      }

      @Override
      public boolean mouseReleased(double x, double y, int code) {
         if (code == 0) {
            mouseDown = false;
            return true;
         }
         return false;
      }
   }
}