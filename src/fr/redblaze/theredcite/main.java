package fr.redblaze.theredcite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import fr.redblaze.theredcite.tools.Outils;

public class main extends JavaPlugin implements Listener {
	public Logger log = Logger.getLogger("Minecraft");
	File fichierAVendre = new File(this.getDataFolder() + "/maisonAVendre.yml");
	File fichierMaison = new File(this.getDataFolder() + "/maison.yml");
	File fichierBalance = new File(this.getDataFolder() + "/balance.yml");
	File folder = new File("plugins/TheRedCite/Factions");
	FileConfiguration vendre = YamlConfiguration.loadConfiguration(fichierAVendre);
	FileConfiguration maison = YamlConfiguration.loadConfiguration(fichierMaison);
	FileConfiguration config = getConfig();
	FileConfiguration balance = YamlConfiguration.loadConfiguration(fichierBalance);
	Listener event = new PlayerBankEvent(this,this.getDataFolder());
	World world;
	EmeraldMenu menu = new EmeraldMenu(this);
	String debutMessage = ChatColor.GREEN + "[CitéDesBois]";
	boolean isNonInit = false;
	HashMap<Player,Integer> playerMaison = new HashMap<Player, Integer>();
	HashMap<Player, List<Block>> blockBuild = new HashMap<Player, List<Block>>();
	List<Player> griefs = new ArrayList<Player>();
	List<LivingEntity> vil = new ArrayList<LivingEntity>();
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
	} catch (IOException e) {
		e.printStackTrace();
	}
	if(config.getString("Monde") != null){
		world = Bukkit.getServer().getWorld(config.getString("Monde"));
		log.info("[TheRedCite]Cité crée dans le monde: " + world.getName());
	}else{
		log.info("[TheRedCite]Monde non trouvé");
	}
	
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
		for(int i = 0;i<vil.size();i++){
			vil.get(i).remove();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if(label.equals("citesetup")){
	    if(!(sender instanceof Player)){
		sender.sendMessage(debutMessage + "Commande utilisable que par un joueur");
		return true;
	    }
		Player player = (Player) sender;
		if(player.hasPermission("theredcube.staff")){                      // TODO permission
		if(args.length>0){
		if(args[0].equalsIgnoreCase("createHouse")){
			playerMaison.put(player, Integer.parseInt(args[1]));
			blockBuild.put(player, new ArrayList<Block>());
			vendre.set(playerMaison.get(player).toString() + ".i", 0);
			player.sendMessage(debutMessage + "Vous Commencez le build de la maison: " + playerMaison.get(player).toString() + ".");
		}else if(args[0].equalsIgnoreCase("endHouse")){
			for(int i = 0;i<blockBuild.get(player).size();i++){
				blockBuild.get(player).get(i).setType(Material.AIR);
			}
			blockBuild.remove(player);
			player.sendMessage(debutMessage + "Vous avez terminer de paramétrer la maison: " + playerMaison.get(player).toString() + ".");
			playerMaison.remove(player);
		}else if(args[0].equalsIgnoreCase("setworld")){
			config.set("Monde", player.getLocation().getWorld().getName());
			world = Bukkit.getServer().getWorld(player.getLocation().getWorld().getName());
			player.sendMessage(debutMessage + "Cité dans " + player.getLocation().getWorld().getName());
		/*}else if(args[0].equalsIgnoreCase("spawn")){
			if(args.length>=3){
				// /citesetup spawn <Nom>
				player.sendMessage(debutMessage + "Villageois:");
			}else{
				player.sendMessage(debutMessage + "Utilisation: /citesetup spawn <Nom> <NomAffiché>");
			}
/*			int i = villageois.getInt("i") + 1;
			villageois.set("i", i);
			villageois.set(i + ".type", Integer.parseInt(args[1]));
			villageois.set(i + ".vector", player.getLocation().toVector());
			spawnVillager(world, player.getLocation().toVector(),);
			player.sendMessage(debutMessage + "Villageois spawné de type: " + Villager.Profession.getProfession(Integer.parseInt(args[1])).toString().toLowerCase());
			try {
				villageois.save(fichierVillageois);
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}else if (args[0].equalsIgnoreCase("addTrade")){
			if(args.length>=3){
				if(args.length>=4){
					addTrade(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
				}else{
					addTrade(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				}
			}else{
				player.sendMessage(debutMessage+"Utilisation: /citesetup addTrade <Nom> <Item1> [Item2] <Item a vendre>");
			}*/
		
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
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getBlock().getLocation().getWorld().equals(world)){
			if(playerMaison.containsKey(event.getPlayer())){
				event.getPlayer().sendMessage(debutMessage + "Block ajouté");
				int i = vendre.getInt(playerMaison.get(event.getPlayer()).toString() + ".i") + 1;
				vendre.set(playerMaison.get(event.getPlayer()).toString() + "." + i, event.getBlock().getLocation().toVector());
				blockBuild.get(event.getPlayer()).add(blockBuild.size()-1,  event.getBlock());
				vendre.set(playerMaison.get(event.getPlayer()).toString() + ".i", i);
			}else{
				if(!Outils.isOk(event.getBlock().getX(), event.getBlock().getZ(), config.getInt("x1"), config.getInt("z2"), config.getInt("x1"), config.getInt("z1"))){
				if(!griefs.contains(event.getPlayer())){
				if(event.getBlock().getLocation().getBlockX()<config.getInt("minx") && event.getBlock().getLocation().getBlockX()<config.getInt("maxx")){
				if(event.getBlock().getLocation().getBlockZ()<config.getInt("minz") && event.getBlock().getLocation().getBlockZ()<config.getInt("maxz")){
				Player player = event.getPlayer();
				boolean isOK  = true;
				for(int i = 1;i<=maison.getInt(player.getUniqueId() + ".i");i++){
					for(int v = 1;v <=maison.getInt(player.getUniqueId() + "." + i + ".i");v++){
						if(maison.getVector(player.getUniqueId() + "." + i + "." + v).equals(event.getBlock().getLocation().toVector())){
							isOK = false;
							v=1000;
							i=1000;
						}
					}
				}
				if(isOK){
					event.setCancelled(true);
				}
			}
			}
			}
			}
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event){
		if(event.getBlock().getWorld().equals(world)){
			if(!griefs.contains(event.getPlayer())){
			if(event.getBlock().getLocation().getBlockX()<config.getInt("minx") && event.getBlock().getLocation().getBlockX()<config.getInt("maxx")){
			if(event.getBlock().getLocation().getBlockZ()<config.getInt("minz") && event.getBlock().getLocation().getBlockZ()<config.getInt("maxz")){
			Player player = event.getPlayer();
			boolean isOK  = true;
			for(int i = 1;i<=maison.getInt(player.getUniqueId() + ".i");i++){
				for(int v = 1;v <=maison.getInt(player.getUniqueId() + "." + i + ".i");v++){
					if(maison.getVector(player.getUniqueId() + "." + i + "." + v).equals(event.getBlock().getLocation().toVector())){
						isOK = false;
						v=1000;
						i=1000;
					}
				}
			}
			
			if(isOK){
				event.setCancelled(true);
			}
			}
			}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		if(event.getEntity().getLocation().getBlockX()<config.getInt("minx") && event.getEntity().getLocation().getBlockX()<config.getInt("maxx")){
		if(event.getEntity().getLocation().getBlockZ()<config.getInt("minz") && event.getEntity().getLocation().getBlockZ()<config.getInt("maxz")){
			event.setCancelled(true);
		}}
	}

	public void clearAll(Player player,Material m,int montant){
		 for(int slot = 0;slot<=player.getInventory().getSize();slot++){
			 if(player.getInventory().getItem(slot) != null){
			 if(player.getInventory().getItem(slot).getType().equals(m)){
				
				 if(montant<player.getInventory().getItem(slot).getAmount()){
					 ItemStack item = player.getInventory().getItem(slot);
					 item.setAmount(item.getAmount() - montant);
					 player.getInventory().setItem(slot,item);
					 montant = 0;
				 }
				 else{
			     montant = montant - player.getInventory().getItem(slot).getAmount();
				 player.getInventory().setItem(slot,new ItemStack(Material.AIR));
				 }
			 }
			 }
			 if(montant == 0){
				 slot = 1000;
			 }
			 player.updateInventory();
		 }
	}
	

	
	/*
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		for(int i = 0;i<vil.size();i++){
			if(event.getEntity().equals(vil.get(i))){
				event.setCancelled(true);
			}
		}
		*/
	
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
		if(event.getPlayer().getWorld().equals(world)){
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				Player player = event.getPlayer();
				if(event.getClickedBlock().getState() instanceof Sign){
					Sign sign = (Sign) event.getClickedBlock().getState();
					if(sign.getLine(0).equals("§a[§2A vendre§a]")){
					int amount = getAmount(event.getPlayer(),Material.EMERALD);
					if(amount>=Integer.parseInt(sign.getLine(2).replaceAll(" §aEmeraude", "").replaceAll("§2§l", ""))){
					int n = Integer.parseInt(sign.getLine(1));
					int nb = maison.getInt(player.getUniqueId() + ".i") + 1;
					for(int i = 1;i<=vendre.getInt(n + ".i");i++){
						maison.set(player.getUniqueId() + "." + nb + "." + i, vendre.getVector(n + "." + i));
					}
					maison.set(player.getUniqueId() + "." + nb + ".i", vendre.getInt(n + ".i"));
					maison.set(player.getUniqueId() + ".i", nb);
					clearAll(player, Material.EMERALD, Integer.parseInt(sign.getLine(2).replaceAll(" §aEmeraude", "").replaceAll("§2§l", "")));
					player.sendMessage(debutMessage + "Vous avez acheté la maison " + n + " pour " + sign.getLine(2).replaceAll(" §aEmeraude", "").replaceAll("§2§l", ""));
					sign.setLine(0, ChatColor.GREEN + "Maison de");
					sign.setLine(1, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + player.getName());
					sign.setLine(2, Integer.toString(n));// TODO Custom
					sign.setLine(3, "");
					sign.update();
					try {
						maison.save(fichierMaison);
					} catch (IOException e) {
						e.printStackTrace();
					}
					}else{
						event.getPlayer().sendMessage(debutMessage + "Vous n'avez pas assez d'argent; il vous manque " + Integer.toString(Integer.parseInt(sign.getLine(2).replaceAll(" §aEmeraude", "").replaceAll("§2§l", "")) - amount) + " emeraudes");
					}
					}else if(sign.getLine(0).equals("Teleportation")){
						teleport(player);
					}else if(!griefs.contains(event.getPlayer())){
					
					boolean isOK  = true;
					
					for(int i = 1;i<=maison.getInt(player.getUniqueId() + ".i");i++){
						for(int v = 1;v <=maison.getInt(player.getUniqueId() + "." + i + ".i");v++){
							if(maison.getVector(player.getUniqueId() + "." + i + "." + v).equals(event.getClickedBlock().getLocation().toVector())){
								isOK = false;
								v=1000;
								i=1000;
							}
						}
					}
					if(isOK){
						event.setCancelled(true);
					}
				}
				}
			}
		}
		}

	public int getAmount(Inventory inv,Material m){
		int amount = 0;
		for(int slot = 0;slot <= inv.getSize();slot++){
			if(inv.getItem(slot) != null){
			if(inv.getItem(slot).getType().equals(m)){
				amount = amount + inv.getItem(slot).getAmount();
			}
			}
		}
		
		return amount;
	}

	public int getAmount(Player player,Material m){
		ItemStack[] item = player.getInventory().getContents();
		int amount = 0;
		for(int slot = 0;slot < item.length;slot++){
			if(item[slot] != null){
			if(item[slot].getType().equals(m)){
				amount = amount + item[slot].getAmount();
		}
		}
	}
		return amount;
	}

	void teleport(Player player){
		if(maison.getInt(player.getUniqueId() + ".i")>0){
			player.teleport(new Location(world, maison.getVector(player.getUniqueId() + ".1.1").getX(), maison.getVector(player.getUniqueId() + ".1.1").getY(), maison.getVector(player.getUniqueId() + ".1.1").getZ()));
			player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 100, 100);
			player.sendMessage(debutMessage + "Vous avez été téléporté vers votre maison");
		}else{
			player.sendMessage(debutMessage + "Vous n'avez aucune maison");
		}
	}
}


