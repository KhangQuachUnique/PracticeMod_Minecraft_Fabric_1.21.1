package net.khangquach.practicemod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.khangquach.practicemod.entity.ModEntities;
import net.khangquach.practicemod.entity.client.DragonRenderer;
import net.khangquach.practicemod.entity.client.HedgehogRenderer;
import net.khangquach.practicemod.entity.custom.GeckoLibEvents;
import net.khangquach.practicemod.entity.internal.HitboxDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PracticeModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.HEDGEHOG, HedgehogRenderer::new);
        EntityRendererRegistry.register(ModEntities.DRAGON, DragonRenderer::new);
        Identifier location = Identifier.of(PracticeMod.MOD_ID, HitboxDataLoader.HITBOX_DATA.getName().toLowerCase());
        ClientPlayNetworking.registerGlobalReceiver(location, (client, handler, buf, responseSender) -> {
            var map = buf.readMap(HashMap::new, PacketByteBuf::readString, HitboxDataLoader::readBuf);
            client.execute(() -> HitboxDataLoader.HITBOX_DATA.replaceData(map));
        });
        if (FabricLoader.getInstance().isModLoaded("geckolib")) {
            GeckoLibEvents.init();
        }
    }
}
