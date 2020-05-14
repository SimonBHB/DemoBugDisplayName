package fr.simonbhb.demobugdisplayname;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Plugin(id = "demobugdisplayname",name = "DemoBugDisplayName",description = "Demo Bug Display Name",authors = {"SimonBHB"})
public class main {

    @Inject
    private PluginContainer container;

    @Inject
    private Logger logger;

    @Inject
    private Game game;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDirectory;

    final private static String inventairePositionsKey = "inventairePositionsKey";

    @Listener
    public void event(GameRegistryEvent.Register<Key<?>> event) {
        event.register(ToolCore.ISNPCINVENTORYOPEN);
        event.register(ToolCore.MAPINVENTORY);
    }

    @Listener
    public void event(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        World world = player.getWorld();
        Chunk chunk = world.getChunk(player.getLocation().getChunkPosition()).get();
        Location<World> spawnLocation = null;

        Random random = new Random();
            List<Entity> list = chunk.getEntities().stream().filter(entity -> entity.getType().equals(EntityTypes.HUMAN)).collect(Collectors.toList());

//            On crée des Human la première fois:
            if(list.size() == 0) {
//                On en fait 4 autour du joueur:
                for (int j = 0; j < 4; j++) {
                    spawnLocation = Sponge.getGame().getTeleportHelper().getSafeLocation(player.getLocation().add(random.nextInt(4), 0,
                            random.nextInt(4)), 3, 3).orElse(null);
                    if(spawnLocation != null) {
                        Entity entity = world.createEntity(EntityTypes.HUMAN, spawnLocation.getPosition());
                        Human human = (Human) entity;
                        human.offer(Keys.PERSISTS, true);
                        human.offer(Keys.DISPLAY_NAME, Text.builder("NPC").color(TextColors.GREEN).toText());

//                        Les données:
                        Map<String, Map<Integer, ItemStackSnapshot>> mapInventory = new HashMap<>();
                        HashMap<Integer, ItemStackSnapshot> inventairePositions = new HashMap<Integer, ItemStackSnapshot>();

                        inventairePositions.put(0, ItemStack.builder().itemType(ItemTypes.DIAMOND).add(Keys.DISPLAY_NAME, Text.of("Le nom de l'item 1")).build().createSnapshot());
                        inventairePositions.put(1, ItemStack.builder().itemType(ItemTypes.DIAMOND).add(Keys.DISPLAY_NAME, Text.of("Le nom de l'item 2")).build().createSnapshot());

                        mapInventory.put(inventairePositionsKey, inventairePositions);
                        human.offer(new MyMapInventoryDataV2(true, mapInventory));
                        world.spawnEntity(human);
                    }
                }
                ToolCore.getLogger().info("Restart the server now and reconnect you");
                ToolCore.getLogger().info("We must not change Chunk");
                Sponge.getServer().shutdown(Text.of("Redémarré le serveur maintenant et reconnecté vous/Restart the server now and reconnect you"));
            } else {
                ToolCore.getLogger().info("");
                ToolCore.getLogger().info("If the server has been restarted:");
                list.forEach(entity -> {
                    Human human = (Human) entity;

                    ToolCore.getLogger().info(human.getUniqueId().toString() + " " + human.getLocation().getPosition() +":");
                    Map<String, Map<Integer, ItemStackSnapshot>> mapInventoryNew = new HashMap<>();

//                    Pour le prochain test je vais remettre les items dans un inventaire:
                    Inventory inventory = Inventory.builder()
                            .of(InventoryArchetypes.CHEST)
                            .property(InventoryDimension.of(9, 1))
                            .build(ToolCore.getContainer());

                    HashMap<Integer, ItemStackSnapshot> inventairePositions = new HashMap<Integer, ItemStackSnapshot>();

                    ToolCore.getLogger().info("Premier test:");
                    human.get(ToolCore.MAPINVENTORY).ifPresent(stringMapMap -> {
                        if(stringMapMap.containsKey(inventairePositionsKey)) {
                            stringMapMap.get(inventairePositionsKey).forEach((integer, itemStackSnapshot) -> {
//                                J'ajoute dans un inventaire l'item:
                                inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(integer))).set(itemStackSnapshot.createStack());

                                inventairePositions.put(integer, inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(integer))).peek().get().createSnapshot());

                                if(itemStackSnapshot.get(Keys.DISPLAY_NAME).isPresent()) {
                                    ToolCore.getLogger().info("Item name: '" + itemStackSnapshot.get(Keys.DISPLAY_NAME).get().toPlain() + "'");
                                } else {
                                    ToolCore.getLogger().info("Erreur !Keys.DISPLAY_NAME: " + itemStackSnapshot.toContainer().toString());
                                }
                            });
                        }
                    });

                    mapInventoryNew.put(inventairePositionsKey, inventairePositions);
                    human.offer(ToolCore.MAPINVENTORY, mapInventoryNew);

                    ToolCore.getLogger().info("New test that works this time:");
                    human.get(ToolCore.MAPINVENTORY).ifPresent(stringMapMap -> {
                        if(stringMapMap.containsKey(inventairePositionsKey)) {
                            stringMapMap.get(inventairePositionsKey).forEach((integer, itemStackSnapshot) -> {
                                if(itemStackSnapshot.get(Keys.DISPLAY_NAME).isPresent()) {
                                    ToolCore.getLogger().info("Item name: '" + itemStackSnapshot.get(Keys.DISPLAY_NAME).get().toPlain() + "'");
                                } else {
                                    ToolCore.getLogger().info("Erreur !Keys.DISPLAY_NAME: " + itemStackSnapshot.toContainer().toString());
                                }
                            });
                        }
                    });
                    ToolCore.getLogger().info("");
                });
            }
    }

    @Listener
    public void GamePreInitialization(GamePreInitializationEvent event) throws IOException {
        new ToolCore(this, container, game, logger, configDirectory); // Obligatoire

        DataRegistration.builder()
                .name("My MapInventory Key Data DemoBugDisplayName")
                .id("mymapinventory_id_data_demobugdisplayname") // prefix is added for you and you can't add it yourself
                .dataClass(MyMapInventoryDataV2.class)
                .immutableClass(MyMapInventoryDataV2.Immutable.class)
                .builder(new MyMapInventoryDataV2.MyMapInventoryDataBuilder()).build();
        ToolCore.getLogger().info("DemoBugDisplayName Start");
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        ToolCore.getLogger().info("DemoBugDisplayName Start");
    }
}
