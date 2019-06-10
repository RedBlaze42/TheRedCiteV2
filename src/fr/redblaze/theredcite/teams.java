package fr.redblaze.theredcite;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class teams {
	private Plugin plugin ;
	private FileConfiguration team_file;

	teams(Plugin main_plugin, FileConfiguration main_team_file){
		this.plugin = main_plugin;
		this.team_file = main_team_file;	
	}
	
	public String get_player_team(Player player) {
		String search_name=player.getName();
		for (String team_name : team_file.getStringList("teams")) {
			for (String player_name : team_file.getStringList("teams."+team_name)) {
				if(search_name.equals(player_name)) {
					return team_name;
				}
			}
		}
		return null;
	}
	
	
	
}
