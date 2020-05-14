package fr.simonbhb.demobugdisplayname;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MyMapInventoryDataV2 extends AbstractData<MyMapInventoryDataV2, MyMapInventoryDataV2.Immutable> {
    private Boolean isnpcinventoryopen;
    private Map<String, Map<Integer, ItemStackSnapshot>> mapinventory;

    public MyMapInventoryDataV2(Boolean isnpcinventoryopen, Map<String, Map<Integer, ItemStackSnapshot>> mapinventory) {
        this.isnpcinventoryopen = isnpcinventoryopen;
        this.mapinventory = mapinventory;

        // you must call this!
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(ToolCore.ISNPCINVENTORYOPEN, () -> this.isnpcinventoryopen);
        registerFieldSetter(ToolCore.ISNPCINVENTORYOPEN, isnpcinventoryopen -> this.isnpcinventoryopen = isnpcinventoryopen);
        registerKeyValue(ToolCore.ISNPCINVENTORYOPEN, this::isnpcinventoryopen);

        registerFieldGetter(ToolCore.MAPINVENTORY, () -> this.mapinventory);
        registerFieldSetter(ToolCore.MAPINVENTORY, mapinventory -> this.mapinventory = mapinventory);
        registerKeyValue(ToolCore.MAPINVENTORY, this::mapinventory);
    }

    public Value<Boolean> isnpcinventoryopen() {
        return Sponge.getRegistry().getValueFactory().createValue(ToolCore.ISNPCINVENTORYOPEN, isnpcinventoryopen);
    }

    public MapValue<String, Map<Integer, ItemStackSnapshot>> mapinventory() {
        return Sponge.getRegistry().getValueFactory().createMapValue(ToolCore.MAPINVENTORY, mapinventory);
    }

    @Override
    public Optional<MyMapInventoryDataV2> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<MyMapInventoryDataV2> otherData_ = dataHolder.get(MyMapInventoryDataV2.class);
        if (otherData_.isPresent()) {
            MyMapInventoryDataV2 otherData = otherData_.get();
            MyMapInventoryDataV2 finalData = overlap.merge(this, otherData);
            this.isnpcinventoryopen = finalData.isnpcinventoryopen;
            this.mapinventory = finalData.mapinventory;
        }
        return Optional.of(this);
    }

    // the double method isn't strictly necessary but makes implementing the builder easier
    @Override
    public Optional<MyMapInventoryDataV2> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<MyMapInventoryDataV2> from(DataView view){
        if(!view.contains(ToolCore.ISNPCINVENTORYOPEN) && !view.contains(ToolCore.MAPINVENTORY)){
            return Optional.empty();
        }
        DataView invView = view.getView(ToolCore.MAPINVENTORY.getQuery()).get();
        Map<String, Map<Integer, ItemStackSnapshot>> map = new HashMap<>();
        this.isnpcinventoryopen = view.getBoolean(ToolCore.ISNPCINVENTORYOPEN.getQuery()).get();

        for(DataQuery query : invView.getKeys(false)){
            Map<Integer, ItemStackSnapshot> temp = new HashMap();
            String key = query.toString();
            Optional<DataView> dataViewOptional = invView.getView(DataQuery.of(key));
            dataViewOptional.ifPresent(dataView -> {
                for(DataQuery dataQuery : dataView.getKeys(false)){
                    int keyTemp = Integer.parseInt(dataQuery.toString());
                    ItemStackSnapshot stack = dataView.getSerializable(dataQuery, ItemStackSnapshot.class).get();
                    temp.put(keyTemp, stack);
                    ToolCore.getLogger().info("getView " + keyTemp + " + " + stack);
                }
                map.put(key, temp);
            });
        }
        this.mapinventory = map;
        return Optional.of(this);
    }

    @Override
    public MyMapInventoryDataV2 copy() {
        return new MyMapInventoryDataV2(this.isnpcinventoryopen, this.mapinventory);
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(this.isnpcinventoryopen, this.mapinventory);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    // IMPORTANT this is what causes your data to be written to NBT
    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(ToolCore.ISNPCINVENTORYOPEN.getQuery(), this.isnpcinventoryopen)
                .set(ToolCore.MAPINVENTORY.getQuery(), this.mapinventory);
    }

    public static class Immutable extends AbstractImmutableData<MyMapInventoryDataV2.Immutable, MyMapInventoryDataV2> {
        private Boolean isnpcinventoryopen;
        private Map<String, Map<Integer, ItemStackSnapshot>> mapinventory;

        public Immutable(Boolean isnpcinventoryopen, Map<String, Map<Integer, ItemStackSnapshot>> mapinventory) {
            this.isnpcinventoryopen = isnpcinventoryopen;
            this.mapinventory = mapinventory;
            registerGetters();
        }

        @Override
        protected void registerGetters() {
            registerFieldGetter(ToolCore.ISNPCINVENTORYOPEN, () -> this.isnpcinventoryopen);
            registerKeyValue(ToolCore.ISNPCINVENTORYOPEN, this::isnpcinventoryopen);

            registerFieldGetter(ToolCore.MAPINVENTORY, () -> this.mapinventory);
            registerKeyValue(ToolCore.MAPINVENTORY, this::mapinventory);
        }

        public ImmutableValue<Boolean> isnpcinventoryopen() {
            return Sponge.getRegistry().getValueFactory().createValue(ToolCore.ISNPCINVENTORYOPEN, isnpcinventoryopen).asImmutable();
        }

        public ImmutableMapValue<String, Map<Integer, ItemStackSnapshot>> mapinventory() {
            return Sponge.getRegistry().getValueFactory().createMapValue(ToolCore.MAPINVENTORY, mapinventory).asImmutable();
        }

        @Override
        public MyMapInventoryDataV2 asMutable() {
            return new MyMapInventoryDataV2(isnpcinventoryopen, mapinventory);
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer()
                    .set(ToolCore.ISNPCINVENTORYOPEN.getQuery(), this.isnpcinventoryopen)
                    .set(ToolCore.MAPINVENTORY.getQuery(), this.mapinventory);
        }
    }

    public static class MyMapInventoryDataBuilder extends AbstractDataBuilder<MyMapInventoryDataV2> implements DataManipulatorBuilder<MyMapInventoryDataV2, MyMapInventoryDataV2.Immutable> {
        public MyMapInventoryDataBuilder() {
            super(MyMapInventoryDataV2.class, 1);
        }

        @Override
        public MyMapInventoryDataV2 create() {
            HashMap<String, Map<Integer, ItemStackSnapshot>> mapitemstacks = new HashMap<>();
            mapitemstacks.put("test", new HashMap<Integer, ItemStackSnapshot>());
            return new MyMapInventoryDataV2(false, mapitemstacks);
        }

        @Override
        public Optional<MyMapInventoryDataV2> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<MyMapInventoryDataV2> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}