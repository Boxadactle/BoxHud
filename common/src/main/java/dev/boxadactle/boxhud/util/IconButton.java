package dev.boxadactle.boxhud.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class IconButton extends Button.Plain {
    int spriteWidth;
    int spriteHeight;

    Identifier sprite;

    public IconButton(int x, int y, int width, int height, int spriteWidth, int spriteHeight, Identifier icon, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);

        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        sprite = icon;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderContents(guiGraphics, mouseX, mouseY, partialTick);
        int i = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
        int j = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.sprite, i, j, 0, 0, this.spriteWidth, this.spriteHeight, spriteWidth, spriteHeight);
    }
}
