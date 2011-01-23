package org.bukkit.craftbukkit.block;

import net.minecraft.server.TileEntityMobSpawner;
import org.bukkit.block.Block;
import org.bukkit.block.MobSpawner;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.MobType;

public class CraftMobSpawner extends CraftBlockState implements MobSpawner {
    private final CraftWorld world;
    private final TileEntityMobSpawner spawner;

    public CraftMobSpawner(final Block block) {
        super(block);

        world = (CraftWorld)block.getWorld();
        spawner = (TileEntityMobSpawner)world.getTileEntityAt(getX(), getY(), getZ());
    }

    public MobType getMobType() {
        return MobType.fromName(spawner.h);
    }

    public void setMobType(MobType mobType) {
        spawner.h = mobType.getName();
    }

    public String getMobTypeId() {
        return spawner.h;
    }

    public void setMobTypeId(String mobType) {
        // Verify input
        MobType type = MobType.fromName(mobType);
        if (type == null) {
            return;
        }
        spawner.h = type.getName();
        
    }
    
    public int getDelay() {
        return spawner.e;
    }

    public void setDelay(int delay) {
        spawner.e = delay;
    }
}