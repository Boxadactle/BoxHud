package dev.boxadactle.boxhud.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconButton extends Button {
    int spriteWidth;
    int spriteHeight;

    ResourceLocation sprite;

    public IconButton(int x, int y, int width, int height, int spriteWidth, int spriteHeight, ResourceLocation icon, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);

        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        sprite = icon;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        int i = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
        int j = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
        guiGraphics.blit(this.sprite, i, j, 0, 0, this.spriteWidth, this.spriteHeight, spriteWidth, spriteHeight);
    }
}
