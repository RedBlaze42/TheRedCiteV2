package fr.redblaze.theredcite;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.redblaze.theredcite.tools.Outils;


@SuppressWarnings("unused")
public class PlayerBankEvent implements Listener{
		private Plugin plugin;
		EmeraldMenu menu = new EmeraldMenu(plugin);
		File fichierBalance;
		FileConfiguration balance;
		
		PlayerBankEvent(Plugin plugins, File file){
			this.plugin = plugins;
			fichierBalance  = new File(file + "/balance.yml");
			balance = YamlConfiguration.loadConfiguration(fichierBalance);
		}

		
		public int getBalance(Player p){
			return balance.getInt(p.getUniqueId().toString());
		}
		public void addEmerald(Player p,int amount){
			int previous = 0;
			if(balance.contains(p.getUniqueId().toString())){
				previous = balance.getInt(p.getUniqueId().toString());
			}
			previous = previous + amount;
			balance.set(p.getUniqueId().toString(), previous);
			try {
				balance.save(fichierBalance);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void takeEmerald(Player p,int amount){
			int previous = getBalance(p) - amount;
			balance.set(p.getUniqueId().toString(), previous);
			try {
				balance.save(fichierBalance);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@EventHandler
		public void onIventoryClick(InventoryClickEvent event){
		
			ItemStack current = event.getCurrentItem();
			Inventory inv = event.getInventory();
			Player player = (Player) event.getWhoClicked();
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != null && event.getCurrentItem().getType() != Material.AIR){
			if(event.getInventory().getTitle().equals(menu.getVirementInv(player,getBalance(player)).getTitle())){
				if(event.getCurrentItem().getItemMeta().hasLore()){
				if(event.getCurrentItem().getItemMeta().getLore().equals(menu.getBlock(event.getCurrentItem().getAmount()).getItemMeta().getLore())){
					int amount = event.getCurrentItem().getAmount();
					int eminv = Outils.getAmount(player, new ItemStack(Material.EMERALD));
					if(amount<=eminv){
						addEmerald(player, amount);
						Outils.clearAll(player,new ItemStack(Material.EMERALD),amount);
						player.sendMessage(getBalance(player) + ""); // TODO Debug
					}else{
						player.sendMessage(ChatColor.DARK_GREEN + "[Bank]" + ChatColor.GREEN + "Vous n'avez pas assez d'emeraude");
					}
					player.updateInventory();
					menu.setItemRet(player, event.getInventory(), getBalance(player));
					menu.setItem(player, event.getInventory());
					event.setCancelled(true);
				}
				}
				if(event.getCurrentItem().getItemMeta().hasLore()){
				if(event.getCurrentItem().getItemMeta().getLore().equals(menu.getBlock1(event.getCurrentItem().getAmount()).getItemMeta().getLore())){
					int embank = getBalance(player);
					int amount = event.getCurrentItem().getAmount();
					if(amount<=embank){
						for(int i = 1;i<=amount;i++){
						player.getInventory().addItem(new ItemStack(Material.EMERALD));
						}
						takeEmerald(player, amount);
					}else{
						player.sendMessage(ChatColor.DARK_GREEN + "[Bank]" + ChatColor.GREEN + "Vous n'avez pas assez d'emeraude");
					}
					player.updateInventory();
				}
				menu.setItemRet(player, event.getInventory(), getBalance(player));
				menu.setItem(player, event.getInventory());
				event.setCancelled(true);
			}
			
			}
			}
		}
		
		
		

}
		


