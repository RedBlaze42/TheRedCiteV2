package fr.redblaze.theredcite.tools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle.EnumTitleAction;

public class Outils {
	public static void clearAll(Player player,Material m,int montant){
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
				 break;
			 }
			 player.updateInventory();
		 }
	}
	public static int getAmount(Player player,ItemStack m){
		return getAmount(player.getInventory(), m);
	}
	public static int getAmount(Player player,Material m){
		return getAmount(player.getInventory(), new ItemStack(m));
	}
	public static int getAmount(Inventory inv,Material m){
		return getAmount(inv, new ItemStack(m));
	}
	public static int getAmount(Inventory inv,ItemStack m){
		ItemStack[] item = inv.getContents();
		int amount = 0;
		int montant;
		for(int slot = 0;slot < item.length;slot++){
			if(item[slot] != null){
			montant = item[slot].getAmount();
			item[slot].setAmount(1);
			m.setAmount(1);
			if(item[slot].equals(m)){
				item[slot].setAmount(montant);
				amount = amount + item[slot].getAmount();
		}
		}
	}		
		return amount;
	}

	public static boolean toBoolean(String string){
		if(string.equalsIgnoreCase("true")){
			return true;
		}else {
			return false;
		}
	}
	
	public static boolean vector_block_equals(Vector vec1, Vector vec2) {
		boolean x= vec1.getBlockX()==vec2.getBlockX();
		boolean y= vec1.getBlockY()==vec2.getBlockY();
		boolean z= vec1.getBlockZ()==vec2.getBlockZ();
		return x && y && z;
	}
	
	public static boolean is_in_coords(int x,int z,int xmin,int zmin,int xmax, int zmax){
	    boolean ifx = x <= xmax && x >= xmin;
	    boolean ifz = z <= zmax && z >= zmin;
	    return ifx && ifz;
	}
	
	public static void sendTitle(Player player, String msgTitle, String msgSubTitle){
		IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + msgTitle + "\"}");
		IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + msgSubTitle + "\"}");
		PacketPlayOutTitle p = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle p2 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(p);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(p2);
	}
	public static ItemStack getItem(int nb,Material mat,String name, String lore1){
		List<String> itemLore = new ArrayList<>();
		itemLore.add(lore1);
		ItemStack item = new ItemStack(mat);
		ItemMeta itemm = item.getItemMeta();
		itemm.setLore(itemLore);
		itemm.setDisplayName(name);
		item.setItemMeta(itemm);
		item.setAmount(nb);
		return item;
	}
	public static ItemStack getItem(int nb,Material mat,String name, String lore1,String lore2){
		List<String> itemLore = new ArrayList<>();
		itemLore.add(lore1);
		itemLore.add(lore2);
		ItemStack item = new ItemStack(mat);
		ItemMeta itemm = item.getItemMeta();
		itemm.setLore(itemLore);
		itemm.setDisplayName(name);
		item.setItemMeta(itemm);
		item.setAmount(nb);
		return item;
	}
	public static ItemStack getItem(int nb,Material mat,String name, String lore1,String lore2,String lore3){
		List<String> itemLore = new ArrayList<>();
		itemLore.add(lore1);
		itemLore.add(lore2);
		itemLore.add(lore3);
		ItemStack item = new ItemStack(mat);
		ItemMeta itemm = item.getItemMeta();
		itemm.setLore(itemLore);
		itemm.setDisplayName(name);
		item.setItemMeta(itemm);
		item.setAmount(nb);
		return item;
	}
	public static ItemStack getItem(int nb,Material mat,String name, String lore1,String lore2,String lore3,String lore4){
		List<String> itemLore = new ArrayList<>();
		itemLore.add(lore1);
		itemLore.add(lore2);
		itemLore.add(lore3);
		itemLore.add(lore4);
		ItemStack item = new ItemStack(mat);
		ItemMeta itemm = item.getItemMeta();
		itemm.setLore(itemLore);
		itemm.setDisplayName(name);
		item.setItemMeta(itemm);
		item.setAmount(nb);
		return item;
	}
	public static ItemStack getItem(int nb,Material mat,String name, List<String> itemLore){
		ItemStack item = new ItemStack(mat);
		ItemMeta itemm = item.getItemMeta();
		itemm.setLore(itemLore);
		itemm.setDisplayName(name);
		item.setItemMeta(itemm);
		item.setAmount(nb);
		return item;
	}
}
