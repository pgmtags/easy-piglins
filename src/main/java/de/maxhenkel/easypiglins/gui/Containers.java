package de.maxhenkel.easypiglins.gui;

import de.maxhenkel.corelib.ClientRegistry;
import de.maxhenkel.easypiglins.Main;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Containers {

    private static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, Main.MODID);
    public static final RegistryObject<MenuType<BartererContainer>> BREEDER_CONTAINER = MENU_TYPE_REGISTER.register("barterer", () ->
            IForgeMenuType.create((windowId, inv, data) -> new BartererContainer(windowId, inv))
    );

    public static void init() {
        MENU_TYPE_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        ClientRegistry.<BartererContainer, BartererScreen>registerScreen(BREEDER_CONTAINER.get(), BartererScreen::new);
    }

}
