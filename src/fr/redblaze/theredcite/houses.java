package fr.redblaze.theredcite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.redblaze.theredcite.tools.Outils;
import net.md_5.bungee.api.ChatColor;

public class houses {
	private teams teams_handler;
	@SuppressWarnings("unused")
	private World cite_world;
	private FileConfiguration player_houses;
	private FileConfiguration house_to_sell;
	private HashMap<Player,Integer> players_in_configuration = new HashMap<Player, Integer>();
	private HashMap<Player, List<Block>> conf_blocks = new HashMap<Player, List<Block>>();
	private String debutMessage;
	
	houses(World world,teams main_team_handler,String debutMessage,FileConfiguration vendre,FileConfiguration maison){
		this.cite_world=world;
		this.teams_handler=main_team_handler;
		this.house_to_sell=vendre;
		this.player_houses=maison;
		this.debutMessage=debutMessage;
	}
	boolean location_in_any_house(Location loc) {
		for(int house_number=1; house_number<=house_to_sell.getInt("i");house_number++) {
			for(int block_number=1;block_number<=house_to_sell.getInt(house_number + ".i");block_number++){
				if(house_to_sell.getVector(house_number + "." + block_number).equals(loc.toVector())) return true;
			}
		}
		return false;
	}
	
	void add_bloc_to_house(Player player,Block block) {
		player.sendMessage(debutMessage + "Block ajouté");
		int i = house_to_sell.getInt(players_in_configuration.get(player).toString() + ".i") + 1;
		house_to_sell.set(players_in_configuration.get(player).toString() + "." + i, block.getLocation().toVector());
		conf_blocks.get(player).add(conf_blocks.size()-1,  block);
		house_to_sell.set(players_in_configuration.get(player).toString() + ".i", i);
	}
	
	void begin_house_conf(Player player) {
		if(!house_to_sell.contains("i")) house_to_sell.set("i", 0);
		
		players_in_configuration.put(player, house_to_sell.getInt("i")+1);
		conf_blocks.put(player, new ArrayList<Block>());
		house_to_sell.set(players_in_configuration.get(player).toString() + ".i", 0);
		house_to_sell.set("i", players_in_configuration.get(player));
		player.sendMessage(debutMessage + "Vous Commencez le build de la maison avec des blocs de barrière: " + players_in_configuration.get(player).toString() + ".");
		player.getInventory().addItem(new ItemStack(Material.BARRIER));
	}
	
	void end_house_conf(Player player) {
		if(players_in_configuration.containsKey(player)) {
			for(int i = 0;i<conf_blocks.get(player).size();i++){
				Block param_block = conf_blocks.get(player).get(i);
				if(param_block.getType().equals(Material.BARRIER)) {
					param_block.setType(Material.AIR);
				}
			}
			conf_blocks.remove(player);
			player.sendMessage(debutMessage + "Vous avez terminer de paramétrer la maison: " + players_in_configuration.get(player).toString() + ".");
			players_in_configuration.remove(player);
		}else {
			player.sendMessage(debutMessage + "Vous n'avez pas de maison en cours de paramétrage");
		}
	}
	
	boolean is_player_in_conf(Player player) {
		return players_in_configuration.containsKey(player);
	}
	
	void event_handle_house_selling(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Sign sign = (Sign) event.getClickedBlock().getState();
		int amount = Outils.getAmount(event.getPlayer(),Material.EMERALD);
		if(amount>=Integer.parseInt(sign.getLine(2).replaceAll(" §aEmeraude", "").replaceAll("§2§l", ""))){
		int n = Integer.parseInt(sign.getLine(1));
		int nb = player_houses.getInt(teams_handler.get_player_team(player) + ".i") + 1;
		for(int i = 1;i<=house_to_sell.getInt(n + ".i");i++){
			player_houses.set(teams_handler.get_player_team(player) + "." + nb + "." + i, house_to_sell.getVector(n + "." + i));
		}
		player_houses.set(teams_handler.get_player_team(player) + "." + nb + ".i", house_to_sell.getInt(n + ".i"));
		player_houses.set(teams_handler.get_player_team(player) + ".i", nb);
		Outils.clearAll(player, Material.EMERALD, Integer.parseInt(sign.getLine(2).replaceAll(" §aEmeraude", "").replaceAll("§2§l", "")));
		player.sendMessage(debutMessage + "Vous avez acheté la maison " + n + " pour " + sign.getLine(2).replaceAll(" §aEmeraude", "").replaceAll("§2§l", ""));
		sign.setLine(0, ChatColor.GREEN + "Maison de");
		sign.setLine(1, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + teams_handler.get_player_team(player));
		sign.setLine(2, Integer.toString(n));
		sign.setLine(3, "");
		sign.update();
		}else{
			event.getPlayer().sendMessage(debutMessage + "Vous n'avez pas assez d'argent; il vous manque " + Integer.toString(Integer.parseInt(sign.getLine(2).replaceAll(" §aEmeraude", "").replaceAll("§2§l", "")) - amount) + " emeraudes");
		}
	}
}
