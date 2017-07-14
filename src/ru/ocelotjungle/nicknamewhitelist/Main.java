package ru.ocelotjungle.nicknamewhitelist;

import java.io.File;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener,CommandExecutor {
	
	private FileConfiguration whitelistConfig;
	private HashSet<String> allowedPlayers;
	
	public Main() {
		getLogger().setLevel(Level.OFF);
	}
	
	private void log(Object Log) {
		getServer().getLogger().info(Log == null ? "null" : Log.toString());
	}
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		initWhitelist();
		
		log("[NicknameWhitelist] Plugin successfully loaded and enabled!");
	}
	
	public void onDisable() {
		log("[NicknameWhitelist] Plugin disabled!");
	}
	
	private void initWhitelist() {
		File file = new File(getDataFolder(), "whitelist.yml");
		if(!file.exists()) { saveResource("whitelist.yml", false); }
		whitelistConfig = YamlConfiguration.loadConfiguration(file);
		
		allowedPlayers = new HashSet<>();
		for(String player : whitelistConfig.getStringList("players")) {
			allowedPlayers.add(player);
		}
	}
	
	@EventHandler
	public void onPlayerJoined(PlayerLoginEvent evt) {
		if(evt.getPlayer().isOp() || allowedPlayers.contains(evt.getPlayer().getName())) {
			evt.setResult(Result.ALLOWED);
		} else {
			evt.setKickMessage("You ain't whitelisted!");
			evt.setResult(Result.KICK_WHITELIST);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("nwhitelist")) {
			initWhitelist();
		}
		return true;
	}
}
