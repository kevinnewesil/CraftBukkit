package org.bukkit.craftbukkit.command;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;

public class AlterGrowthCommand extends Command {
    public AlterGrowthCommand(String name) {
        super(name);
        this.description = "Changes the number of chunks that growth occurs on each tick";
        this.usageMessage = "/growth";
        this.setPermission("bukkit.command.growth");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        int growth = ((CraftServer)Bukkit.getServer()).getOptimalGrowthChunks();
        int amt = -1;
        try {
        	 amt = Integer.parseInt(args[0]);
        	((CraftServer)Bukkit.getServer()).setOptimalGrowthChunks(amt);
        }
        catch(Exception e) {
        	sender.sendMessage(ChatColor.RED + "Invalid command usage. Try /growth x, where x is a number.");
        	return true;
        }
        sender.sendMessage(ChatColor.GREEN + "Growth was changed from " + growth + " to " + amt);
        if (amt > 0 && growth < 0) {
        	sender.sendMessage(ChatColor.GREEN + "You have enabled growth!");
        }
        else if (amt < 0 && growth > 0) {
        	sender.sendMessage(ChatColor.GREEN + "You have disabled growth!");
        }

        return true;
    }
}
