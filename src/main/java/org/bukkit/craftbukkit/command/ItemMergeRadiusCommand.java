package org.bukkit.craftbukkit.command;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;

public class ItemMergeRadiusCommand extends Command {
    public ItemMergeRadiusCommand(String name) {
        super(name);
        this.description = "Changes the range at which items will merge";
        this.usageMessage = "/merge";
        this.setPermission("bukkit.command.merge");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        double radius = ((CraftServer)Bukkit.getServer()).getItemMergeRadius();
        double amt = -1;
        try {
        	 amt = Double.parseDouble(args[0]);
        	((CraftServer)Bukkit.getServer()).setItemMergeRadius(amt);
        }
        catch(Exception e) {
        	sender.sendMessage(ChatColor.RED + "Invalid command usage. Try /merge x, where x is a number (can be a decimal).");
        	return true;
        }
        sender.sendMessage(ChatColor.GREEN + "Item merge radius was changed from " + radius + " to " + amt);

        return true;
    }
}
