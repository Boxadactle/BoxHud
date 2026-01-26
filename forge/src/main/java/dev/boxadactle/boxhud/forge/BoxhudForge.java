package dev.boxadactle.boxhud.forge;

import dev.boxadactle.boxhud.*;
import dev.boxadactle.boxhud.forge.mixin.GuiInvoker;
import dev.boxadactle.boxhud.util.ModUtil;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Boxhud.MOD_ID)
public final class BoxhudForge {
    public BoxhudForge() {
        Boxhud.init();

        MinecraftForge.registerConfigScreen(WidgetListScreen::new);

        ModUtil.packExclusionFilter = (pack) -> pack.getPackSource().equals(PackSource.BUILT_IN) || pack.getId().equalsIgnoreCase("mod_resources");
        ModUtil.hotbarRenderer = (gui, graphics, delta) -> ((GuiInvoker) gui).invokeRenderHotbar(graphics, delta);
    }

    @Mod.EventBusSubscriber(modid = Boxhud.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent.Pre event) {
            Boxhud.tick();
        }

        @SubscribeEvent
        public static void keyInput(InputEvent.Key e) {
            Bindings.check();
        }

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent e) {
            Bindings.register(e::register);
        }
    }
}
