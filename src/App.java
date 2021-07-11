import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin implements Listener {
    private Sack sack;
    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getOnlinePlayers().forEach(player -> player.getInventory().addItem(new Sack()));
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onInvetoryClose(InventoryCloseEvent event) {
        System.out.println(event.getInventory().getType() + " was closed");
        if(event.getInventory().getHolder() instanceof Sack) {
            System.out.println("Inventory is a Sack");
            sack = (Sack)event.getInventory().getHolder();
            //System.out.println(sack.parentInventory.getHolder().getInventory());
            ItemStack[] contents = sack.parentInventory.getContents();
            for (int i = 0; i < contents.length; i++){
                if(sack.isSimilar(contents[i])) {
                    sack.saveContents(event.getInventory().getContents());
                    contents[i] = sack;
                    sack.parentInventory.getHolder().getInventory().setContents(contents);
                    System.out.println("Sack contents saved");
                }
            }                
        }
    }
    @EventHandler void onInventoryClick(InventoryClickEvent event) {
        if (event.isRightClick()) {
            System.out.println("Right clicked");
            if(event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
                System.out.println("Item has meta");
                if(event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(Sack.key, PersistentDataType.BYTE_ARRAY)) {
                    System.out.println("Item is a sack");
                    event.setResult(Result.DENY);
                    System.out.println("Clicked a sack while sack was open: " + (event.getView().getTopInventory().getHolder() instanceof Sack));
                    if(event.getView().getTopInventory().getHolder() instanceof Sack) {
                        sack = (Sack)event.getInventory().getHolder();
                        if(sack.isSimilar(event.getCurrentItem())) {
                            return;
                        }
                    }                                       
                    getServer().getScheduler().runTask(this, new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Opening Sack Inventory");        
                            sack = new Sack(event.getCurrentItem().getItemMeta());
                            sack.parentInventory = event.getClickedInventory();
                            event.getWhoClicked().openInventory(sack.getInventory());
                        }
                    });
                }
            }                        
        }
    }    
}
