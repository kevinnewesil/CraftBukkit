package org.bukkit.craftbukkit.event;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityBlaze;
import net.minecraft.server.EntityCaveSpider;
import net.minecraft.server.EntityChicken;
import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityCow;
import net.minecraft.server.EntityCreeper;
import net.minecraft.server.EntityEnderCrystal;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.EntityEnderman;
import net.minecraft.server.EntityGhast;
import net.minecraft.server.EntityGiantZombie;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityMagmaCube;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMushroomCow;
import net.minecraft.server.EntityPig;
import net.minecraft.server.EntityPigZombie;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityPotion;
import net.minecraft.server.EntitySheep;
import net.minecraft.server.EntitySilverfish;
import net.minecraft.server.EntitySkeleton;
import net.minecraft.server.EntitySlime;
import net.minecraft.server.EntitySnowman;
import net.minecraft.server.EntitySpider;
import net.minecraft.server.EntitySquid;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.EntityZombie;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;

public class CraftEventFactory {
    // helper methods
    private static boolean canBuild(CraftWorld world, Player player, int x, int z) {
        WorldServer worldServer = world.getHandle();
        int spawnSize = Bukkit.getServer().getSpawnRadius();

        if (spawnSize <= 0) return true;
        if (player.isOp()) return true;

        ChunkCoordinates chunkcoordinates = worldServer.getSpawn();

        int distanceFromSpawn = (int) Math.max(Math.abs(x - chunkcoordinates.x), Math.abs(z - chunkcoordinates.z));
        return distanceFromSpawn > spawnSize;
    }

    /**
     * Block place methods
     */
    public static BlockPlaceEvent callBlockPlaceEvent(World world, EntityHuman who, BlockState replacedBlockState, int clickedX, int clickedY, int clickedZ, int type) {
        return callBlockPlaceEvent(world, who, replacedBlockState, clickedX, clickedY, clickedZ, net.minecraft.server.Block.byId[type]);
    }

    public static BlockPlaceEvent callBlockPlaceEvent(World world, EntityHuman who, BlockState replacedBlockState, int clickedX, int clickedY, int clickedZ, net.minecraft.server.Block block) {
        return callBlockPlaceEvent(world, who, replacedBlockState, clickedX, clickedY, clickedZ, new ItemStack(block));
    }

    public static BlockPlaceEvent callBlockPlaceEvent(World world, EntityHuman who, BlockState replacedBlockState, int clickedX, int clickedY, int clickedZ, ItemStack itemstack) {
        CraftWorld craftWorld = ((WorldServer) world).getWorld();
        CraftServer craftServer = ((WorldServer) world).getServer();

        Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        CraftItemStack itemInHand = new CraftItemStack(itemstack);

        Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        Block placedBlock = replacedBlockState.getBlock();

        boolean canBuild = canBuild(craftWorld, player, placedBlock.getX(), placedBlock.getZ());

        BlockPlaceEvent event = new BlockPlaceEvent(placedBlock, replacedBlockState, blockClicked, itemInHand, player, canBuild);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Bucket methods
     */
    public static PlayerBucketEmptyEvent callPlayerBucketEmptyEvent(EntityHuman who, int clickedX, int clickedY, int clickedZ, int clickedFace, ItemStack itemInHand) {
        return (PlayerBucketEmptyEvent) getPlayerBucketEvent(false, who, clickedX, clickedY, clickedZ, clickedFace, itemInHand, Item.BUCKET);
    }

    public static PlayerBucketFillEvent callPlayerBucketFillEvent(EntityHuman who, int clickedX, int clickedY, int clickedZ, int clickedFace, ItemStack itemInHand, net.minecraft.server.Item bucket) {
        return (PlayerBucketFillEvent) getPlayerBucketEvent(true, who, clickedX, clickedY, clickedZ, clickedFace, itemInHand, bucket);
    }

    private static PlayerEvent getPlayerBucketEvent(boolean isFilling, EntityHuman who, int clickedX, int clickedY, int clickedZ, int clickedFace, ItemStack itemstack, net.minecraft.server.Item item) {
        Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        CraftItemStack itemInHand = new CraftItemStack(new ItemStack(item));
        Material bucket = Material.getMaterial(itemstack.id);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);

        PlayerEvent event = null;
        if (isFilling) {
            event = new PlayerBucketFillEvent(player, blockClicked, blockFace, bucket, itemInHand);
            ((PlayerBucketFillEvent) event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
        } else {
            event = new PlayerBucketEmptyEvent(player, blockClicked, blockFace, bucket, itemInHand);
            ((PlayerBucketEmptyEvent) event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
        }

        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Player Interact event
     */
    public static PlayerInteractEvent callPlayerInteractEvent(EntityHuman who, Action action, ItemStack itemstack) {
        if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) {
            throw new IllegalArgumentException();
        }
        return callPlayerInteractEvent(who, action, 0, 255, 0, 0, itemstack);
    }

    public static PlayerInteractEvent callPlayerInteractEvent(EntityHuman who, Action action, int clickedX, int clickedY, int clickedZ, int clickedFace, ItemStack itemstack) {
        Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        CraftItemStack itemInHand = new CraftItemStack(itemstack);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);

        if (clickedY == 255) {
            blockClicked = null;
            switch (action) {
            case LEFT_CLICK_BLOCK:
                action = Action.LEFT_CLICK_AIR;
                break;
            case RIGHT_CLICK_BLOCK:
                action = Action.RIGHT_CLICK_AIR;
                break;
            }
        }

        if (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0) {
            itemInHand = null;
        }

        PlayerInteractEvent event = new PlayerInteractEvent(player, action, itemInHand, blockClicked, blockFace);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * EntityShootBowEvent
     */
    public static EntityShootBowEvent callEntityShootBowEvent(EntityLiving who, ItemStack itemstack, EntityArrow entityArrow, float force) {
        LivingEntity shooter = (LivingEntity) who.getBukkitEntity();
        CraftItemStack itemInHand = new CraftItemStack(itemstack);
        Arrow arrow = (Arrow) entityArrow.getBukkitEntity();

        if (itemInHand != null && (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0)) {
            itemInHand = null;
        }

        EntityShootBowEvent event = new EntityShootBowEvent(shooter, itemInHand, arrow, force);
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * BlockDamageEvent
     */
    public static BlockDamageEvent callBlockDamageEvent(EntityHuman who, int x, int y, int z, ItemStack itemstack, boolean instaBreak) {
        Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        CraftItemStack itemInHand = new CraftItemStack(itemstack);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = craftWorld.getBlockAt(x, y, z);

        BlockDamageEvent event = new BlockDamageEvent(player, blockClicked, itemInHand, instaBreak);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * CreatureSpawnEvent
     */
    public static CreatureSpawnEvent callCreatureSpawnEvent(EntityLiving entityliving, SpawnReason spawnReason) {
        LivingEntity entity = (LivingEntity) entityliving.getBukkitEntity();
        CraftServer craftServer = (CraftServer) entity.getServer();

        CreatureType type = null;

        if (entityliving instanceof EntityChicken) {
            type = CreatureType.CHICKEN;
        } else if (entityliving instanceof EntityCow) {
            if (entityliving instanceof EntityMushroomCow) type = CreatureType.MUSHROOM_COW;
            else type = CreatureType.COW;
        } else if (entityliving instanceof EntityCreeper) {
            type = CreatureType.CREEPER;
        } else if (entityliving instanceof EntityGhast) {
            type = CreatureType.GHAST;
        } else if (entityliving instanceof EntityGiantZombie) {
            type = CreatureType.GIANT;
        } else if (entityliving instanceof EntityWolf) {
            type = CreatureType.WOLF;
        } else if (entityliving instanceof EntityPig) {
            type = CreatureType.PIG;
        } else if (entityliving instanceof EntityPigZombie) {
            type = CreatureType.PIG_ZOMBIE;
        } else if (entityliving instanceof EntitySheep) {
            type = CreatureType.SHEEP;
        } else if (entityliving instanceof EntitySkeleton) {
            type = CreatureType.SKELETON;
        } else if (entityliving instanceof EntitySlime) {
            if (entityliving instanceof EntityMagmaCube) type = CreatureType.MAGMA_CUBE;
            else type = CreatureType.SLIME;
        } else if (entityliving instanceof EntitySpider) {
            if (entityliving instanceof EntityCaveSpider) type = CreatureType.CAVE_SPIDER;
            else type = CreatureType.SPIDER;
        } else if (entityliving instanceof EntitySquid) {
            type = CreatureType.SQUID;
        } else if (entityliving instanceof EntityZombie) {
            type = CreatureType.ZOMBIE;
        } else if (entityliving instanceof EntityEnderman) {
            type = CreatureType.ENDERMAN;
        } else if (entityliving instanceof EntitySnowman) {
            type = CreatureType.SNOWMAN;
        } else if (entityliving instanceof EntityEnderDragon) {
            type = CreatureType.ENDER_DRAGON;
        } else if (entityliving instanceof EntityVillager) {
            type = CreatureType.VILLAGER;
        } else if (entityliving instanceof EntityBlaze) {
            type = CreatureType.BLAZE;
        } else if (entityliving instanceof EntitySilverfish) {
            type = CreatureType.SILVERFISH;
        }

        CreatureSpawnEvent event = new CreatureSpawnEvent(entity, type, entity.getLocation(), spawnReason);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * EntityTameEvent
     */
    public static EntityTameEvent callEntityTameEvent(EntityLiving entity, EntityHuman tamer) {
        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
        org.bukkit.entity.AnimalTamer bukkitTamer = (tamer != null ? (AnimalTamer) tamer.getBukkitEntity() : null);
        CraftServer craftServer = (CraftServer) bukkitEntity.getServer();

        EntityTameEvent event = new EntityTameEvent(bukkitEntity, bukkitTamer);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * ItemSpawnEvent
     */
    public static ItemSpawnEvent callItemSpawnEvent(EntityItem entityitem) {
        org.bukkit.entity.Item entity = (org.bukkit.entity.Item) entityitem.getBukkitEntity();
        CraftServer craftServer = (CraftServer) entity.getServer();

        ItemSpawnEvent event = new ItemSpawnEvent(entity, entity.getLocation());

        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * ItemDespawnEvent
     */
    public static ItemDespawnEvent callItemDespawnEvent(EntityItem entityitem) {
        org.bukkit.entity.Item entity = (org.bukkit.entity.Item) entityitem.getBukkitEntity();

        ItemDespawnEvent event = new ItemDespawnEvent(entity, entity.getLocation());

        ((CraftServer) entity.getServer()).getPluginManager().callEvent(event);
        return event;
    }

    /**
     * PotionSplashEvent
     */
    public static PotionSplashEvent callPotionSplashEvent(EntityPotion potion, Map<LivingEntity, Double> affectedEntities) {
        ThrownPotion thrownPotion = (ThrownPotion) potion.getBukkitEntity();

        PotionSplashEvent event = new PotionSplashEvent(thrownPotion, affectedEntities);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * BlockFadeEvent
     */
    public static BlockFadeEvent callBlockFadeEvent(Block block, int type) {
        BlockState state = block.getState();
        state.setTypeId(type);

        BlockFadeEvent event = new BlockFadeEvent(block, state);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static EntityDeathEvent callEntityDeathEvent(EntityLiving victim, List<org.bukkit.inventory.ItemStack> drops) {
        CraftLivingEntity entity = (CraftLivingEntity) victim.getBukkitEntity();
        EntityDeathEvent event = new EntityDeathEvent(entity, drops, victim.getExpReward());
        org.bukkit.World world = entity.getWorld();
        Bukkit.getServer().getPluginManager().callEvent(event);

        victim.expToDrop = event.getDroppedExp();

        for (org.bukkit.inventory.ItemStack stack : event.getDrops()) {
            world.dropItemNaturally(entity.getLocation(), stack);
        }

        return event;
    }

    public static PlayerDeathEvent callPlayerDeathEvent(EntityPlayer victim, List<org.bukkit.inventory.ItemStack> drops, String deathMessage) {
        CraftPlayer entity = (CraftPlayer) victim.getBukkitEntity();
        PlayerDeathEvent event = new PlayerDeathEvent(entity, drops, victim.getExpReward(), 0, deathMessage);
        org.bukkit.World world = entity.getWorld();
        Bukkit.getServer().getPluginManager().callEvent(event);

        // TODO: Possibly a way to persist this incase of disconnect
        victim.keepLevel = event.getKeepLevel();
        victim.newLevel = event.getNewLevel();
        victim.newTotalExp = event.getNewTotalExp();
        victim.expToDrop = event.getDroppedExp();
        victim.newExp = event.getNewExp();

        for (org.bukkit.inventory.ItemStack stack : event.getDrops()) {
            if (stack == null || stack.getType() == Material.AIR) continue;

            world.dropItemNaturally(entity.getLocation(), stack);
        }

        return event;
    }

    /**
     * Server methods
     */
    public static ServerListPingEvent callServerListPingEvent(Server craftServer, InetAddress address, String motd, int numPlayers, int maxPlayers) {
        ServerListPingEvent event = new ServerListPingEvent(address, motd, numPlayers, maxPlayers);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * EntityDamage(ByEntityEvent)
     */
    public static EntityDamageEvent callEntityDamageEvent(Entity damager, EntityLiving damagee, DamageCause cause, int damage) {
        EntityDamageEvent event;
        if (damager != null) {
            event = new EntityDamageByEntityEvent(damager.getBukkitEntity(), damagee.getBukkitEntity(), cause, damage);
        } else {
            event = new EntityDamageEvent(damagee.getBukkitEntity(), cause, damage);
        }
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }

    public static PlayerLevelChangeEvent callPlayerLevelChangeEvent(Player player, int oldLevel, int newLevel) {
        PlayerLevelChangeEvent event = new PlayerLevelChangeEvent(player, oldLevel, newLevel);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static PlayerExpChangeEvent callPlayerExpChangeEvent(EntityHuman entity, int expAmount) {
        Player player = (Player) entity.getBukkitEntity();
        PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, expAmount);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static boolean handleProjectileEvent(Projectile projectile, Entity target, DamageSource damagesource, int damage) {
        if (target instanceof EntityLiving || target instanceof EntityComplexPart || target instanceof EntityEnderCrystal) {
            org.bukkit.entity.Entity damagee = target.getBukkitEntity();
            
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(projectile, damagee, EntityDamageEvent.DamageCause.PROJECTILE, damage);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                return target.damageEntity(damagesource, damage);
            }
        } else {
            // Other entities have their events (if any) handled in damageEntity
            return target.damageEntity(damagesource, damage);
        }

        return !projectile.doesBounce();
    }
}
