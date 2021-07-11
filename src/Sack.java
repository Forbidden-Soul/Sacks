import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class Sack extends ItemStack implements InventoryHolder {
    public static NamespacedKey key = new NamespacedKey(JavaPlugin.getPlugin(App.class), "Sack");
    private Inventory inventory = Bukkit.getServer().createInventory(this, 9, "Sack");
    public Inventory parentInventory;

    public Sack() {
        super(Material.LEATHER);
        ItemMeta itemMeta = getItemMeta();
        itemMeta.setDisplayName("Sack");
        setItemMeta(itemMeta);        
        //inventory.addItem(new ItemStack(Material.APPLE));
        saveContents(new ItemStack[]{new ItemStack(Material.APPLE), null, null, null, null, null, null, null, null});
        //System.out.println("Custom Model Data: " + itemMeta.getCustomModelData());                
    }
    public Sack(ItemMeta itemMeta) {
        super(Material.LEATHER);
        setItemMeta(itemMeta);
    }

    public void saveContents(ItemStack[] contents) {                
        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(bOut));
            out.writeObject(contents);
            out.close();
            ItemMeta itemMeta = getItemMeta();
            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE_ARRAY, bOut.toByteArray());
            setItemMeta(itemMeta);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSack(ItemStack itemStack) {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE_ARRAY);
    }

    @Override
    public Inventory getInventory() {        
        BukkitObjectInputStream in;
        try {            
            in = new BukkitObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BYTE_ARRAY))));
            inventory.setContents((ItemStack[])in.readObject());
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return inventory;
    }
}
