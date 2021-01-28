package com.blalp.consolecommandlog;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ConsoleCommandLog extends JavaPlugin implements Listener {
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	private boolean isMalicious(String command){
		if(command.startsWith("/")){
			command=command.substring(1);
		}
		if (command.contains(" ")){
			switch (command.split(" ")[0]) {
				case "lp":
				case "luckperms":
				case "luckperms:lp":
				case "luckperms:luckperms":
				case "pex":
				case "permissionsex":
				case "permissionsex:pex":
				case "permissionsex:permissionsex":
					if (command.split(" ").length>=5){
						switch (command.split(" ")[1]) {
							case "user":
							case "u":
								switch (command.split(" ")[3]) {
									case "set":
									case "permission":
									case "p":
										switch(command.split(" ")[4]){
											case "*":
											case "\"*\"":
											case "'*'":
												return true;
										}
										if (command.split(" ")[4].contains("luckperms")){ 
											return true;
										}
								}
						}
					}
					return true;
			}
		}
		return false;
	}
	private boolean isDangerous(String command){
		if(command.startsWith("/")){
			command=command.substring(1);
		}
		if (command.contains(" ")){
			switch (command.split(" ")[0]) {
				case "lp":
				case "luckperms":
				case "luckperms:lp":
				case "luckperms:luckperms":
				case "pex":
				case "permissionsex":
				case "permissionsex:pex":
				case "permissionsex:permissionsex":
				case "ban":
				case "unban":
				case "ipban":
				case "kick":
				case "op":
				case "deop":
					return true;
			}
		} else {
			switch (command) {
				case "save-all":
				case "stop":
					return true;
			}
		}
		return false;
	}
	private void printInfo(CommandSender sender, String command,Cancellable event,String eventName){
		if(isMalicious(command)) {
			if(event!=null){
				event.setCancelled(true);
			}
			System.err.println("[ConsoleCommandLogger] MALICIOUS COMMAND |>|"+command+"|<| sent by |>|"+sender.getName()+"|<| during event "+eventName);
		} else if (isDangerous(command)){
			System.err.println("[ConsoleCommandLogger] DANGEROUS COMMAND |>|"+command+"|<| sent by |>|"+sender.getName()+"|<| during event "+eventName);
		} else {
			System.out.println("[ConsoleCommandLogger] Command |>|"+command+"|<| sent by |>|"+sender.getName()+"|<| during event "+eventName);
		}
	}
	@EventHandler
	public void onRemoteServer(RemoteServerCommandEvent e){
		e.setCancelled(true);
		printInfo(e.getSender(), e.getCommand(),e,e.getEventName());
		System.err.println("Blocked command "+e.getCommand()+" from being executed over RCON.");
	}
	@EventHandler
	public void onConsoleCommand(ServerCommandEvent e){
		printInfo(e.getSender(), e.getCommand(), e,e.getEventName());
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e){
		if(e.getMessage().startsWith("/")||e.isCancelled()) {
			printInfo(e.getPlayer(), e.getMessage(), e,e.getEventName());
		}
	}
	@EventHandler
	public void onPlayerSendCommand(PlayerCommandSendEvent e) {
		printInfo(e.getPlayer(), "RECEIVING COMMANDS", null, e.getEventName());
	}
}