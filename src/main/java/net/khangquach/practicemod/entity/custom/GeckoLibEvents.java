package net.khangquach.practicemod.entity.custom;

import net.khangquach.practicemod.entity.internal.GeckoLibMultiPartMob;
import software.bernie.geckolib.event.GeoRenderEvent;

public class GeckoLibEvents {

    public static void init() {
        GeoRenderEvent.Entity.Post.EVENT.register(event -> {
            if (event.getEntity() instanceof GeckoLibMultiPartMob multiPartMob) {
                multiPartMob.practicemod$updateRenderTick();
            }
        });
    }
}