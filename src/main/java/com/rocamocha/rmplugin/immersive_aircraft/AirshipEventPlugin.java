package com.rocamocha.rmplugin.immersive_aircraft;

import circuitlord.reactivemusic.SongpackEventType;
import circuitlord.reactivemusic.api.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class AirshipEventPlugin implements SongpackEventPlugin {

    private static final boolean IA_LOADED = FabricLoader.getInstance().isModLoaded("immersive_aircraft");

    private static SongpackEventType AIRSHIP; 

    @Override public void init() {
        AIRSHIP = SongpackEventType.register("AIRSHIP");
    }

    @Override public void gameTick(PlayerEntity player, World world, Map<SongpackEventType, Boolean> eventMap) {
        // Keep this safe even if IA isn't installed
        boolean active = IA_LOADED && player != null && isInImmersiveAircraftAirship(player);
        if (player == null || world == null) return;
        eventMap.put(AIRSHIP, active);

        Entity v = player.getRootVehicle();
        if (v != null && v != player) {
            Identifier id = Registries.ENTITY_TYPE.getId(v.getType());
            if (id != null) {
                // Replace with your logger if you have one; this is fine for quick checks
                System.out.println("[RM:SpEP-IA] Root vehicle: " + id);
            }
        }
    }

    /** Detect IA airship by entity type registry id: immersive_aircraft:<something_with_airship> */
    private static boolean isInImmersiveAircraftAirship(PlayerEntity player) {
        // climb to the root vehicle (the actual aircraft)
        Entity vehicle = player.getRootVehicle();
        if (vehicle == null || vehicle == player) return false;

        Identifier typeId = Registries.ENTITY_TYPE.getId(vehicle.getType());
        if (typeId == null) return false;

        // strict namespace match
        if (!"immersive_aircraft".equals(typeId.getNamespace())) return false;

        // be flexible on the path; tighten when you know exact IDs
        String path = typeId.getPath().toLowerCase(java.util.Locale.ROOT);
        return path.contains("airship"); // e.g., "airship", "cargo_airship", etc.
    }

    @Override
    public String getId() {
        return "Immersive Aircraft";
    }
}
