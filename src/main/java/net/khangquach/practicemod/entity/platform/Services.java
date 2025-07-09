package net.khangquach.practicemod.entity.platform;

import net.khangquach.practicemod.PracticeMod;
import net.khangquach.practicemod.entity.api.MultiPart;
import net.khangquach.practicemod.entity.custom.FabricMultiPart;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

@ApiStatus.Internal
public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final MultiPart.Factory MULTI_PART = new FabricMultiPart.FabricMultiPartFactory();

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        PracticeMod.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}

