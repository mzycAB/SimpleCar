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

public class SimpleCar implements ModInitializer {
    public static final String MOD_ID = "simplecar";

    private static double carSpeedBlocksPerSecond = 8.0;

    public static double getCarSpeedBlocksPerSecond() {
        return carSpeedBlocksPerSecond;
    }

    public static void setCarSpeedBlocksPerSecond(double speed) {
        carSpeedBlocksPerSecond = speed;
    }

    public static final EntityType<CarEntity> CAR_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "car"),
            EntityType.Builder.create(CarEntity::new, SpawnGroup.MISC)
                    .setDimensions(3.2F, 2.8F)
                    .maxTrackingRange(10)
                    .build("car"));

    public static final Item CAR_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "car"),
            new CarItem(new Item.Settings()));

    public static final Item CAR_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "car_spawn_egg"),
            new SpawnEggItem(CAR_ENTITY, 0xB71C1C, 0x212121, new Item.Settings()));

    public static final RegistryKey<ItemGroup> CAR_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, "car"));

    public static final ItemGroup CAR_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CAR_ITEM))
            .displayName(Text.translatable("itemGroup.simplecar"))
            .build();

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(CAR_ENTITY, CarEntity.createCarAttributes());
        CarSpeedCommand.register();

        Registry.register(Registries.ITEM_GROUP, CAR_GROUP_KEY, CAR_GROUP);
        ItemGroupEvents.modifyEntriesEvent(CAR_GROUP_KEY).register(entries -> {
            entries.add(CAR_ITEM);
            entries.add(CAR_SPAWN_EGG);
        });
    }
}
