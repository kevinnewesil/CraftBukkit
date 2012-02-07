package org.bukkit.craftbukkit.command;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

public class SpawnMobRadiusCommand extends Command {
	HashMap<UUID, Boolean> oldMonsters = new HashMap<UUID, Boolean>();
	HashMap<UUID, Boolean> oldAnimals = new HashMap<UUID, Boolean>();
    public SpawnMobRadiusCommand(String name) {
        super(name);
        this.description = "Changes the range at which mobs spawn at";
        this.usageMessage = "/mobspawn";
        this.setPermission("bukkit.command.mobspawn");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        int radius = ((CraftServer)Bukkit.getServer()).getMobSpawnRange();
        int amt = -1;
        try {
        	 amt = Integer.parseInt(args[0]);
        	((CraftServer)Bukkit.getServer()).setMobSpawnRange(amt);
        }
        catch(Exception e) {
        	sender.sendMessage(ChatColor.RED + "Invalid command usage. Try /mobspawn x, where x is a number.");
        	return true;
        }
        sender.sendMessage(ChatColor.GREEN + "Mob spawn radius was changed from " + radius + " to " + amt);
        if (amt <= 0 && radius > 0) {
        	sender.sendMessage(ChatColor.YELLOW + "Mob spawning is now disabled.");
        	for (World w : Bukkit.getServer().getWorlds()) {
        		oldMonsters.put(w.getUID(), w.getAllowMonsters());
        		oldAnimals.put(w.getUID(), w.getAllowAnimals());
        		((CraftWorld)w).getHandle().allowMonsters = false;
        		((CraftWorld)w).getHandle().allowAnimals = false;
        	}
        }
        else if (amt > 0 && radius <= 0) {
        	sender.sendMessage(ChatColor.YELLOW + "Mob spawning is now enabled.");
        	for (World w : Bukkit.getServer().getWorlds()) {
        		if (oldMonsters.containsKey(w.getUID())) {
        			((CraftWorld)w).getHandle().allowMonsters = oldMonsters.get(w.getUID());
        		}
        		if (oldAnimals.containsKey(w.getUID())) {
        			((CraftWorld)w).getHandle().allowAnimals = oldAnimals.get(w.getUID());
        		}
        	}
        }

        return true;
    }
}
