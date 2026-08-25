package simple.car.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import simple.car.SimpleCar;
import simple.car.entity.CarEntity;

public class CarItem extends Item {
    public CarItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos().offset(context.getSide());
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        CarEntity car = new CarEntity(SimpleCar.CAR_ENTITY, world);
        car.refreshPositionAndAngles(
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                context.getHorizontalPlayerFacing().asRotation(),
                0.0F);
        if (!world.isSpaceEmpty(car)) {
            return ActionResult.FAIL;
        }
        world.spawnEntity(car);
        PlayerEntity player = context.getPlayer();
        if (player == null || !player.getAbilities().creativeMode) {
            context.getStack().decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
