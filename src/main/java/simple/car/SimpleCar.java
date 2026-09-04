package simple.car;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import simple.car.command.CarSpeedCommand;
import simple.car.entity.CarEntity;
import simple.car.item.CarItem;
import simple.car.item.CarRemoverItem;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleCar implements ModInitializer {
    public static final String MOD_ID = "simplecar";

    public static final EntityType<CarEntity> CAR_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "car"),
            EntityType.Builder.create(CarEntity::new, SpawnGroup.MISC)
                    .setDimensions(2.56F, 2.1F)
                    .maxTrackingRange(10)
                    .build("car"));

    public static final EntityType<CarEntity> CAR_WHITE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "car_white"),
            EntityType.Builder.create(CarEntity::new, SpawnGroup.MISC)
                    .setDimensions(2.56F, 2.1F)
                    .maxTrackingRange(10)
                    .build("car_white"));

    public static final EntityType<CarEntity> CAR_GRAY_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "car_gray"),
            EntityType.Builder.create(CarEntity::new, SpawnGroup.MISC)
                    .setDimensions(2.56F, 2.1F)
                    .maxTrackingRange(10)
                    .build("car_gray"));

    public static final Item CAR_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "car"),
            new CarItem(CAR_ENTITY, new Item.Settings()));

    public static final Item CAR_WHITE_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "car_white"),
            new CarItem(CAR_WHITE_ENTITY, new Item.Settings()));

    public static final Item CAR_GRAY_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "car_gray"),
            new CarItem(CAR_GRAY_ENTITY, new Item.Settings()));

    public static final Item CAR_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "car_spawn_egg"),
            new SpawnEggItem(CAR_ENTITY, 0xB71C1C, 0x212121, new Item.Settings()));

    public static final Item CAR_WHITE_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "car_white_spawn_egg"),
            new SpawnEggItem(CAR_WHITE_ENTITY, 0xF5F5F5, 0x212121, new Item.Settings()));

    public static final Item CAR_GRAY_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "car_gray_spawn_egg"),
            new SpawnEggItem(CAR_GRAY_ENTITY, 0x9E9E9E, 0x212121, new Item.Settings()));

    // Remaining 13 concrete colors. Key = color name, value = concrete primary color (for spawn egg).
    public static final Map<String, EntityType<CarEntity>> CAR_COLOR_ENTITIES = new LinkedHashMap<>();
    public static final Map<String, Item> CAR_COLOR_ITEMS = new LinkedHashMap<>();
    public static final Map<String, Item> CAR_COLOR_SPAWN_EGGS = new LinkedHashMap<>();

    private static final String[] CONCRETE_COLORS = {
            "black", "blue", "brown", "cyan", "green", "light_blue", "light_gray",
            "lime", "magenta", "orange", "pink", "purple", "yellow"
    };

    private static final Map<String, Integer> CONCRETE_COLOR_RGB = Map.ofEntries(
            Map.entry("black", 0x080A0F),
            Map.entry("blue", 0x2D2F8F),
            Map.entry("brown", 0x603C20),
            Map.entry("cyan", 0x157788),
            Map.entry("green", 0x495B24),
            Map.entry("light_blue", 0x2489C7),
            Map.entry("light_gray", 0x7D7D73),
            Map.entry("lime", 0x5EA918),
            Map.entry("magenta", 0xA9309F),
            Map.entry("orange", 0xE06101),
            Map.entry("pink", 0xD6658F),
            Map.entry("purple", 0x64209C),
            Map.entry("yellow", 0xF1AF15)
    );

    // Standard Minecraft concrete/wool ordering, "red" maps to the default car.
    private static final String[] ALL_CONCRETE_ORDER = {
            "white", "light_gray", "gray", "black", "brown", "red", "orange", "yellow",
            "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink"
    };

    private static Item carItemForColor(String color) {
        return switch (color) {
            case "red" -> CAR_ITEM;
            case "white" -> CAR_WHITE_ITEM;
            case "gray" -> CAR_GRAY_ITEM;
            default -> CAR_COLOR_ITEMS.get(color);
        };
    }

    public static Item carItemForEntity(EntityType<?> type) {
        if (type == CAR_ENTITY) return CAR_ITEM;
        if (type == CAR_WHITE_ENTITY) return CAR_WHITE_ITEM;
        if (type == CAR_GRAY_ENTITY) return CAR_GRAY_ITEM;
        for (java.util.Map.Entry<String, EntityType<CarEntity>> entry : CAR_COLOR_ENTITIES.entrySet()) {
            if (entry.getValue() == type) return CAR_COLOR_ITEMS.get(entry.getKey());
        }
        return null;
    }

    public static final Item CAR_REMOVER_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "car_remover"),
            new CarRemoverItem(new Item.Settings().maxCount(1)));

    public static final RegistryKey<ItemGroup> CAR_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, "car"));

    public static final ItemGroup CAR_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CAR_ITEM))
            .displayName(Text.translatable("itemGroup.simplecar"))
            .build();

    private static void registerColorCars() {
        for (String color : CONCRETE_COLORS) {
            EntityType<CarEntity> type = Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(MOD_ID, "car_" + color),
                    EntityType.Builder.create(CarEntity::new, SpawnGroup.MISC)
                            .setDimensions(2.56F, 2.1F)
                            .maxTrackingRange(10)
                            .build("car_" + color));
            CAR_COLOR_ENTITIES.put(color, type);

            Item item = Registry.register(
                    Registries.ITEM,
                    new Identifier(MOD_ID, "car_" + color),
                    new CarItem(type, new Item.Settings()));
            CAR_COLOR_ITEMS.put(color, item);

            Item egg = Registry.register(
                    Registries.ITEM,
                    new Identifier(MOD_ID, "car_" + color + "_spawn_egg"),
                    new SpawnEggItem(type, CONCRETE_COLOR_RGB.get(color), 0x212121, new Item.Settings()));
            CAR_COLOR_SPAWN_EGGS.put(color, egg);
        }
    }

    @Override
    public void onInitialize() {
        registerColorCars();
        FabricDefaultAttributeRegistry.register(CAR_ENTITY, CarEntity.createCarAttributes());
        FabricDefaultAttributeRegistry.register(CAR_WHITE_ENTITY, CarEntity.createCarAttributes());
        FabricDefaultAttributeRegistry.register(CAR_GRAY_ENTITY, CarEntity.createCarAttributes());
        CAR_COLOR_ENTITIES.forEach((color, type) ->
                FabricDefaultAttributeRegistry.register(type, CarEntity.createCarAttributes()));
        CarSpeedCommand.register();

        Registry.register(Registries.ITEM_GROUP, CAR_GROUP_KEY, CAR_GROUP);
        ItemGroupEvents.modifyEntriesEvent(CAR_GROUP_KEY).register(entries -> {
            for (String color : ALL_CONCRETE_ORDER) {
                entries.add(carItemForColor(color));
            }
            entries.add(CAR_REMOVER_ITEM);
        });
    }
}
