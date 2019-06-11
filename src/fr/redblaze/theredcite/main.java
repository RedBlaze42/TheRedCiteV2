package fr.redblaze.theredcite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.plugin.java.JavaPlugin;

import fr.redblaze.theredcite.tools.Outils;

public class main extends JavaPlugin implements Listener {
	public Logger log = Logger.getLogger("Minecraft");
	File fichierAVendre = new File(this.getDataFolder() + "/maisonAVendre.yml");
	File fichierMaison = new File(this.getDataFolder() + "/maison.yml");
	File fichierBalance = new File(this.getDataFolder() + "/balance.yml");
	File fichierTeams = new File(this.getDataFolder() + "/teams.yml");
	FileConfiguration vendre = YamlConfiguration.loadConfiguration(fichierAVendre);
	FileConfiguration maison = YamlConfiguration.loadConfiguration(fichierMaison);
	FileConfiguration teams = YamlConfiguration.loadConfiguration(fichierTeams);
	FileConfiguration config = getConfig();
	FileConfiguration balance = YamlConfiguration.loadConfiguration(fichierBalance);
	Listener event = new PlayerBankEvent(this,this.getDataFolder());
	ArrayList<Material> item_banned_rightclick = new ArrayList<>(Arrays.asList(Material.ITEM_FRAME, Material.GLASS_BOTTLE, Material.SPLASH_POTION, Material.LINGERING_POTION));
	String debutMessage="[Cité des sables]";
	World world;
	teams teams_handler = new teams(this, teams);
	houses house_handler = new houses(world, teams_handler, debutMessage, vendre, maison);
	
	EmeraldMenu menu = new EmeraldMenu(this);
	boolean isNonInit = false;
	
	
	List<Player> griefs = new ArrayList<Player>();

	List<Material> autorized_blocks;
	
	public void onEnable(){
		saveDefaultConfig();
	try{
	if(!fichierAVendre.exists()){
		fichierAVendre.createNewFile();
		vendre.save(fichierAVendre);
	}
	if(!fichierMaison.exists()){
		fichierMaison.createNewFile();
		maison.save(fichierMaison);
	}
	if(!fichierBalance.exists()){
		fichierBalance.createNewFile();
		maison.save(fichierBalance);
	}
	if(!fichierTeams.exists()){
		fichierTeams.createNewFile();
		teams.save(fichierTeams);
	}
	} catch (IOException e) {
		e.printStackTrace();
	}
	if(config.getString("Monde") != null){
		world = Bukkit.getServer().getWorld(config.getString("Monde"));
		log.info("[TheRedCite] Cité crée dans le monde: " + world.getName());
	}else{
		log.info("[TheRedCite] Monde non trouvé");
	}
	
	debutMessage = config.getString("debut_messages");
	autorized_blocks=new ArrayList<>(Arrays.asList(Material.LECTERN, Material.CHEST, Material.CHEST_MINECART, Material.BREWING_STAND, Material.BARREL, Material.ENDER_CHEST));
	
	getServer().getPluginManager().registerEvents(this, this);
	getServer().getPluginManager().registerEvents(event, this);

	}
	
	
	public void onDisable(){
		try{
		vendre.save(fichierAVendre);
		maison.save(fichierMaison);
		log.info("[TheRedCite]Fichier sauvegardé");
		}catch(IOException e){
			log.warning("[TheRedCite]Fichier non sauvegardé");
			e.printStackTrace();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if(label.equals("citesetup")){
	    if(!(sender instanceof Player)){
		sender.sendMessage(debutMessage + "Commande utilisable que par un joueur");
		return true;
	    }
		Player player = (Player) sender;
		player.sendMessage("Team:"+teams_handler.get_player_team(player));
		if(player.hasPermission("theredcube.staff")){
		if(args.length>0){
		if(args[0].equalsIgnoreCase("createHouse")){// CREATE HOUSE
			house_handler.begin_house_conf(player);
		}else if(args[0].equalsIgnoreCase("endHouse")){
			house_handler.end_house_conf(player);
		}else if(args[0].equalsIgnoreCase("setworld")){
			config.set("Monde", player.getLocation().getWorld().getName());
			world = Bukkit.getServer().getWorld(player.getLocation().getWorld().getName());
			player.sendMessage(debutMessage + "Cité dans " + player.getLocation().getWorld().getName());		
		}else if(args[0].equalsIgnoreCase("grief")){
			if(griefs.contains(player)){
				griefs.remove(player);
				player.sendMessage(debutMessage + "Vous ne pouvez plus grief");
			}else{
				griefs.add(player);
				player.sendMessage(debutMessage + "Vous pouvez maintenant grief");
			}
		}else{
			player.sendMessage(debutMessage + "Pas compris ^^");
		}
	}else{
		player.sendMessage(debutMessage + "setworld,spawn,addTrade,createHouse,endHouse");
	}
	}
	
	if(label.equals("menu")){
		((Player)sender).openInventory(menu.getVirementInv(((Player)sender),balance.getInt(((Player)sender).getUniqueId().toString())));
	}
	
	
	}
	return false;
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if(event.getBlock().getWorld().equals(world)){
			if(house_handler.is_player_in_conf(event.getPlayer())){// SI en cours de création de maison
				house_handler.add_bloc_to_house(event.getPlayer(), event.getBlock());
			}else {
				if(!griefs.contains(event.getPlayer())){
				if(location_in_cite(event.getBlock().getLocation())){//Dans la cite ?
					Player player = event.getPlayer();
					if(!in_house(player,event.getBlock().getLocation())){
						event.setCancelled(true);
					}
				}
				}
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event){
		if(event.getBlock().getWorld().equals(world)){
			if(house_handler.is_player_in_conf(event.getPlayer())){// SI en cours de création de maison
				house_handler.add_bloc_to_house(event.getPlayer(), event.getBlock());// TODO remove block if block=barrier
				event.setCancelled(true);
			}else {
				if(!griefs.contains(event.getPlayer())){
				if(location_in_cite(event.getBlock().getLocation())){//Dans la cite ?
					Player player = event.getPlayer();
					if(!in_house(player,event.getBlock().getLocation())){
						event.setCancelled(true);
					}
				}
				}
			}
		}
	}
	
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		if(location_in_cite(event.getLocation())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event){
		if(location_in_cite(event.getEntity().getLocation())) {
			Entity damager = event.getDamager();

		    if (damager instanceof Player) {
		    	Player player = (Player) damager;
		    	Location loc = player.getLocation();
		    	if(!griefs.contains(player)){
					if(location_in_cite(loc)){//Dans la cite ?
						if(!in_house(player,loc)){
							event.setCancelled(true);
						}
					}
				}
		    }else {
		    	event.setCancelled(true);
		    }
			
			
		}
	}
	
	@EventHandler
	public void onBlockDamage(EntityDamageByBlockEvent event) {
		if(location_in_cite(event.getEntity().getLocation())) {
			event.setCancelled(true);
		}
	}
	


	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		if(event.getBlock().getWorld().equals(world)){
			if(event.getPlayer().hasPermission("cite.setup")){
				if(event.getLine(0).equals("vendre")){
					event.setLine(0, "§a[§2A vendre§a]");
					event.setLine(2, "§2§l" + vendre.getInt(event.getLine(1) + ".i") + " §aEmeraude");
					event.setLine(3,"§b>Clique ici<");
					event.getPlayer().sendMessage(debutMessage + "Vous avez créer le panneaux de vente pour la maison " + event.getLine(1));
				}
				if(event.getLine(0).equals("teleport")){
					event.setLine(0, "Teleportation");
					event.setLine(1, "vers");
					event.setLine(2, org.bukkit.ChatColor.GOLD + "[Maison 1]");
					event.getPlayer().sendMessage(debutMessage + "Vous avez créer un panneaux de teleportation");
				}
			}
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(is_legit_griefer(player) || in_house(player, player.getLocation())) {
			event.setCancelled(false);
		}else if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND) {
	        event.setCancelled(true);
		}else if(item_banned_rightclick.contains(player.getInventory().getItemInMainHand().getType()) || item_banned_rightclick.contains(player.getInventory().getItemInOffHand().getType())) {
			event.setCancelled(true);
		}else if(location_in_cite(event.getPlayer().getLocation())){
			
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				Location block_location=event.getClickedBlock().getLocation();
				if(location_in_cite(block_location) && !in_house(event.getPlayer(), block_location) && !is_legit_griefer(event.getPlayer()) && house_handler.location_in_any_house(block_location)) {//DAns la cité pas legit griefer pas dans sa maison mais dans une maison
					event.setCancelled(true);
				}
				
				if(event.getClickedBlock().getState() instanceof Sign){//SIGN
					event_handle_sign_click(event);
				}
			}
		}
	}
	
	@EventHandler
	public void onArmorStandClicking(PlayerArmorStandManipulateEvent event) {
		Player player = event.getPlayer();
		Location clicked_location = event.getRightClicked().getLocation();
		if(!is_legit_griefer(player) && !in_house(player, clicked_location) && location_in_cite(clicked_location)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBookTaken(PlayerTakeLecternBookEvent event) {
		Location location_event = event.getLectern().getLocation();
		if(location_in_cite(location_event) && !in_house(event.getPlayer(), location_event) && !is_legit_griefer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	void teleport_to_house(Player player){
		if(maison.getInt(teams_handler.get_player_team(player) + ".i")>0){
			String player_team_name= teams_handler.get_player_team(player);
			player.teleport(new Location(world, maison.getVector(player_team_name + ".1.1").getX(), maison.getVector(player_team_name + ".1.1").getY(), maison.getVector(player_team_name + ".1.1").getZ()));
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 100);
			player.sendMessage(debutMessage + "Vous avez été téléporté vers votre maison");
		}else{
			player.sendMessage(debutMessage + "Vous n'avez aucune maison");
		}
	}
	
	boolean in_house(Player player,Location loc) {
		if(!loc.getWorld().equals(world)){
			return false;
		}
		Block block = loc.getBlock();
		
		for(int i = 1;i<=maison.getInt(teams_handler.get_player_team(player) + ".i");i++){
			for(int v = 1;v <=maison.getInt(teams_handler.get_player_team(player) + "." + i + ".i");v++){
				if(maison.getVector(teams_handler.get_player_team(player) + "." + i + "." + v).equals(block.getLocation().toVector())){
					return true;
				}
			}
		}
		return false;
	}
	
	void event_handle_sign_click(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Sign sign = (Sign) event.getClickedBlock().getState();
		if(sign.getLine(0).equals("§a[§2A vendre§a]")){
			house_handler.event_handle_house_selling(event);
		}else if(sign.getLine(0).equals("Teleportation")){
			teleport_to_house(player);
		}else if(!griefs.contains(event.getPlayer())){//Si il n'est pas en mode grief
			if(!in_house(player,event.getClickedBlock().getLocation())){
				event.setCancelled(true);
			}
		}
	}
	
	boolean location_in_cite(Location loc) {
		return loc.getWorld().equals(world) && Outils.is_in_coords(loc.getBlockX(), loc.getBlockZ(), config.getInt("cite_coords.xmin"), config.getInt("cite_coords.zmin"), config.getInt("cite_coords.xmax"), config.getInt("cite_coords.zmax"));
	}
	
	boolean is_legit_griefer(Player player) {
		return griefs.contains(player);
	}
	
	
}


