package net.khangquach.practicemod.entity.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.khangquach.practicemod.PracticeMod;
import net.khangquach.practicemod.entity.api.HitboxData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class HitboxDataLoader extends JsonDataLoader {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    public static final HitboxDataLoader HITBOX_DATA = new HitboxDataLoader(GSON);
    private ImmutableMap<Identifier, List<HitboxData>> hitboxData = ImmutableMap.of();

    public HitboxDataLoader(Gson gson) {
        super(gson, "hitboxes");
    }

    /**
     * Returns a list of the hitbox data that was loaded from data/hitboxes
     *
     * @param entityLocation the {@link net.minecraft.util.Identifier} of the mob EntityType
     *  *                       (from {@code Registries.ENTITY_TYPE.getId(EntityType)})
     * @return a list of the mobs hitbox data
     */
    public List<HitboxData> getHitboxes(Identifier entityLocation) {
        return hitboxData.get(entityLocation);
    }

    public Map<Identifier, List<HitboxData>> getHitboxData() {
        return hitboxData;
    }

    /**
     * Replaces all hitbox data with a copy of the given map
     *
     * @param dataMap the new hitbox data
     */
    public void replaceData(Map<Identifier, List<HitboxData>> dataMap) {
        hitboxData = ImmutableMap.copyOf(dataMap);
    }

    public static List<HitboxData> readBuf(PacketByteBuf buf) {
        return buf.readList(HitboxData::readBuf);
    }

    private static void writeBuf(PacketByteBuf buf, List<HitboxData> hitboxes) {
        buf.writeCollection(hitboxes, HitboxData::writeBuf);
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeMap(hitboxData, (buffer, key) -> buf.writeIdentifier(key), HitboxDataLoader::writeBuf);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, List<HitboxData>> builder = ImmutableMap.builder();
        for (Map.Entry<Identifier, JsonElement> fileEntry : prepared.entrySet()) {
            if (!(fileEntry.getValue() instanceof JsonObject root)) {
                continue;
            }
            JsonArray elements = root.getAsJsonArray("elements");
            ImmutableList.Builder<HitboxData> listBuilder = ImmutableList.builder();
            for (JsonElement element : elements) {
                JsonObject elemObject = element.getAsJsonObject();
                PracticeMod.LOGGER.info("get" + element.getAsString());
                double[] pos = new double[3];
                JsonArray posArray = JsonHelper.getArray(elemObject, "pos");
                JsonElement refElement = elemObject.get("ref");
                String ref = refElement == null ? "" : refElement.getAsString();
                for (int i = 0; i < pos.length; ++i) {
                    pos[i] = JsonHelper.asDouble(posArray.get(i), "pos[" + i + "]");
                }
                if (elemObject.has("is_anchor") && JsonHelper.getBoolean(elemObject, "is_anchor")) {
                    listBuilder.add(new HitboxData(elemObject.get("name").getAsString(), new Vec3d(pos[0] / 16, pos[1] / 16, pos[2] / 16), 0, 0, ref, false, true));
                } else {
                    float width = JsonHelper.getFloat(elemObject, "width") / 16;
                    float height = JsonHelper.getFloat(elemObject, "height") / 16;

                    JsonElement attackElement = elemObject.get("is_attack_box");
                    boolean isAttack = attackElement != null && attackElement.getAsBoolean();
                    listBuilder.add(new HitboxData(elemObject.get("name").getAsString(), new Vec3d(pos[0] / 16, pos[1] / 16, pos[2] / 16), width, height, ref, isAttack, false));
                }
            }
            builder.put(fileEntry.getKey(), listBuilder.build());
        }
        hitboxData = builder.build();
    }
}


