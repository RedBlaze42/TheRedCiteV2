package fr.redblaze.theredcite;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import fr.redblaze.theredcite.tools.Outils;

public class EmeraldMenu {
Player player;
@SuppressWarnings("unused")
private Plugin plugin;
	
	
	public EmeraldMenu(Plugin plugins){
		this.plugin = plugins;
	}
	
	public Inventory getVirementInv(Player player,int balance){
		Inventory inv = Bukkit.createInventory(null, 54, "§aVirement");
		inv.setMaxStackSize(100);
		setItem(player,inv);
		setItemRet(player,inv,balance);
		ItemStack desc = new ItemStack(Material.EMERALD);
		List<String> descLore= new ArrayList<String>();
		descLore.add(ChatColor.DARK_GREEN + "Permet de mettre des emeraude");
		descLore.add(ChatColor.DARK_GREEN + "sur son compte en banque");
		ItemMeta menumeta = desc.getItemMeta();
		menumeta.setDisplayName(ChatColor.GREEN + "Virement");
		menumeta.setLore(descLore);
		desc.setItemMeta(menumeta);
		inv.setItem(4, desc);
		inv.setItem(31, Outils.getItem(1, Material.EMERALD, ChatColor.GREEN + "Distributeur d'emeraudes", ChatColor.DARK_GREEN + "Pour retirer des emeraude",ChatColor.DARK_GREEN + "de son compte en banque"));
		return inv;
	}
	public void setItem(Player player,Inventory inv){
		int emeraudeInv = Outils.getAmount(player,new ItemStack(Material.EMERALD));
		
		if (emeraudeInv >=5) {
			inv.setItem(10, getBlock(5));
		}
		if (emeraudeInv >=10) {
			inv.setItem(12, getBlock(10));
		}
		if (emeraudeInv >=20) {
			inv.setItem(14, getBlock(20));
		}
		if (emeraudeInv >=50) {
			inv.setItem(16, getBlock(50));
		}
		inv.setItem(22, getBlock(emeraudeInv));
	}
	
	public void setItemRet(Player player,Inventory inv,int emeraudeBank){
		if (emeraudeBank >=5) {
			inv.setItem(37, getBlock1(5));
		}
		if (emeraudeBank >=10) {
			inv.setItem(39, getBlock1(10));
		}
		if (emeraudeBank >=20) {
			inv.setItem(41, getBlock1(20));
		}
		if (emeraudeBank >=50) {
			inv.setItem(43, getBlock1(50));
		}
		inv.setItem(49, getBlock1(emeraudeBank));
	}
	
	
	public ItemStack getBlock(int nb){
		List<String> itemlore = new ArrayList<String>();
		itemlore.add(0,ChatColor.GREEN + "Virer " + ChatColor.DARK_GREEN + nb + ChatColor.GREEN + " Emeraudes vers la banque.");
		ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
		ItemMeta itemm = item.getItemMeta();
		itemm.setLore(itemlore);
		itemm.setDisplayName(ChatColor.DARK_GREEN + "" + nb + "" + ChatColor.GREEN + " Emeraudes");
		item.setItemMeta(itemm);
		item.setAmount(nb);
		return item;
	}
	public ItemStack getBlock1(int nb){
		List<String> itemlore = new ArrayList<String>();
		itemlore.add(0,ChatColor.GREEN + "Retirer " + ChatColor.DARK_GREEN + nb + ChatColor.GREEN + " Emeraudes de la banque.");
		ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
		ItemMeta itemm = item.getItemMeta();
		itemm.setLore(itemlore);
		itemm.setDisplayName(ChatColor.DARK_GREEN + "" + nb + "" + ChatColor.GREEN + " Emeraudes");
		item.setItemMeta(itemm);
		item.setAmount(nb);
		return item;
	}

	

}
