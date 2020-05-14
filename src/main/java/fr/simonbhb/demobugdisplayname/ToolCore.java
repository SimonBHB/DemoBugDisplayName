package fr.simonbhb.demobugdisplayname;

import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.util.Map;

public class ToolCore {
    private static PluginContainer container;

    /** The game. */
    private static Game game;

    /** The logger. */
    private static Logger logger;

    /** The main. */
    private static main main;

    private static File configDirectory;

    public ToolCore(main main, PluginContainer container, Game game, Logger logger, File configDirectory) {
        ToolCore.main = main;
        ToolCore.container = container;
        ToolCore.game = game;
        ToolCore.logger = logger;
        ToolCore.configDirectory = configDirectory;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static final Key<Value<Boolean>> ISNPCINVENTORYOPEN = Key.builder()
            .type(new TypeToken<Value<Boolean>>() {})
            .id("ishoppersorter_id")
            .name("isHopperSorter ID")
            .query(DataQuery.of('.', "ishoppersorter_id"))
            .build();

    public static final Key<MapValue<String, Map<Integer, ItemStackSnapshot>>> MAPINVENTORY = Key.builder()
            .type(new TypeToken<MapValue<String, Map<Integer, ItemStackSnapshot>>>() {})
            .id("mapinventory_id")
            .name("MapInventory ID")
            .query(DataQuery.of('.', "mapinventory_id"))
            .build();

    public static PluginContainer getContainer() {
        return container;
    }
}
