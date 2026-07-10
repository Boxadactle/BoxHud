package dev.boxadactle.boxhud.neoforge.mixin;

import dev.boxadactle.boxhud.BoxWidgets;
import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(Hud.class)
public abstract class GuiMixin {

    @Shadow @Final private GuiLayerManager layerManager;

    @Shadow @Final private Minecraft minecraft;

    @Shadow
    protected abstract void extractCameraOverlays(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractContextualInfoBarBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractSleepOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractDemoOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractTitle(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractChat(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractTabList(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractSubtitleOverlay(GuiGraphicsExtractor graphics, boolean deferRendering);

    @Unique
    private void boxHud$emptyRenderer(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {

    }

    @Inject(
            method = "registerVanillaLayers",
            at = @At("HEAD"),
            cancellable = true
    )
    private void unRegisterVanillaLayers(CallbackInfo ci) {
        BooleanSupplier guiVisible = () -> !this.minecraft.gui.hud.isHidden();
        this.layerManager.add(VanillaGuiLayers.CAMERA_OVERLAYS, this::extractCameraOverlays, guiVisible);
        this.layerManager.add(VanillaGuiLayers.CROSSHAIR, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.AFTER_CAMERA_DECORATIONS, (guiGraphics, deltaTracker) -> guiGraphics.nextStratum(), guiVisible);
        this.layerManager.add(VanillaGuiLayers.HOTBAR, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.PLAYER_HEALTH, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.ARMOR_LEVEL, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.FOOD_LEVEL, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.VEHICLE_HEALTH, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.AIR_LEVEL, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND, this::extractContextualInfoBarBackground, guiVisible);
        this.layerManager.add(VanillaGuiLayers.EXPERIENCE_LEVEL, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.CONTEXTUAL_INFO_BAR, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.SELECTED_ITEM_NAME, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.SPECTATOR_TOOLTIP, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.EFFECTS, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.BOSS_OVERLAY, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.SLEEP_OVERLAY, this::extractSleepOverlay);
        this.layerManager.add(VanillaGuiLayers.DEMO_OVERLAY, this::extractDemoOverlay, guiVisible);
        this.layerManager.add(VanillaGuiLayers.SCOREBOARD_SIDEBAR, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.OVERLAY_MESSAGE, this::boxHud$emptyRenderer, guiVisible);
        this.layerManager.add(VanillaGuiLayers.TITLE, this::extractTitle, guiVisible);
        this.layerManager.add(VanillaGuiLayers.CHAT, this::extractChat, guiVisible);
        this.layerManager.add(VanillaGuiLayers.TAB_LIST, this::extractTabList, guiVisible);
        this.layerManager.add(VanillaGuiLayers.SUBTITLE_OVERLAY, (graphics, deltaTracker) -> {
            if (!this.minecraft.gui.hud.isHidden()) {
                this.extractSubtitleOverlay(graphics, ClientUtils.getCurrentScreen() == null || ClientUtils.getCurrentScreen().isInGameUi());
            } else if (ClientUtils.getCurrentScreen() != null && ClientUtils.getCurrentScreen().isInGameUi()) {
                this.extractSubtitleOverlay(graphics, true);
            }

        });
        this.layerManager.add(Identifier.fromNamespaceAndPath(Boxhud.MOD_ID, "widgets"), (graphics, deltaTracker) -> {
            graphics.nextStratum();
            BoxWidgets.renderAll(graphics);
        });

        ci.cancel();
    }

}
