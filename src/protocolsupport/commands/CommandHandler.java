package protocolsupport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import protocolsupport.ProtocolSupport;
import protocolsupport.api.Connection;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.zplatform.ServerPlatform;

public class CommandHandler implements CommandExecutor, TabCompleter {

	private final ProtocolSupport plugin;
	public CommandHandler(ProtocolSupport plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("protocolsupport.admin")) {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
			return true;
		}
		else if(args.length == 1 || args.length == 2) {
			if (args[0].equalsIgnoreCase("buildinfo")) {
				sender.sendMessage(ChatColor.GOLD.toString() + plugin.getBuildInfo());
				return true;
			}
			else if (args[0].equalsIgnoreCase("list")) {
				sender.sendMessage(ChatColor.GREEN + "ProtocolSupport Players:");
				for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
						String res = getPlayersStringForProtocol(version);
						if(res.length() > 0 || (args.length == 2 && (args[1].equalsIgnoreCase("v") || args[1].equalsIgnoreCase("verbose"))))
							sender.sendMessage(ChatColor.GOLD+"["+version.getName()+"]: "+ChatColor.GREEN+res);
				}
				if(args.length == 1 || !(args[1].equalsIgnoreCase("v") || args[1].equalsIgnoreCase("verbose")))
					sender.sendMessage(ChatColor.GOLD +"List all compatible versions using " + ChatColor.GREEN +"/ps list verbose");
				return true;
			}
			else if (args[0].equalsIgnoreCase("debug")) {
				if (ServerPlatform.get().getMiscUtils().isDebugging()) {
					ServerPlatform.get().getMiscUtils().disableDebug();
					sender.sendMessage(ChatColor.GOLD + "Disabled debug");
				} else {
					ServerPlatform.get().getMiscUtils().enableDebug();
					sender.sendMessage(ChatColor.GOLD + "Enabled debug");
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("leakdetector")) {
				if (ResourceLeakDetector.isEnabled()) {
					ResourceLeakDetector.setLevel(Level.DISABLED);
					sender.sendMessage(ChatColor.GOLD + "Disabled leak detector");
				} else {
					ResourceLeakDetector.setLevel(Level.PARANOID);
					sender.sendMessage(ChatColor.GOLD + "Enabled leak detector");
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("connections")) {
				for (Connection connection : ProtocolSupportAPI.getConnections()) {
					sender.sendMessage(ChatColor.GREEN + connection.toString());
				}
				return true;
			}
		}
		return false;
	}

	private String getPlayersStringForProtocol(ProtocolVersion version) {
		StringBuilder sb = new StringBuilder();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (ProtocolSupportAPI.getProtocolVersion(player) == version) {
				sb.append(player.getName());
				sb.append(", ");
			}
		}
		if (sb.length() > 2) {
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> completions = new ArrayList<>();
		if ("list".startsWith(args[0])) {
			completions.add("list");
		}
		if ("debug".startsWith(args[0])) {
			completions.add("debug");
		}
		if ("leakdetector".startsWith(args[0])) {
			completions.add("leakdetector");
		}
		return completions;
	}

}