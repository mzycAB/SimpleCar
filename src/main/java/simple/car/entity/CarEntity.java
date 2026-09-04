package simple.car.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import simple.car.SimpleCar;
import simple.car.item.CarRemoverItem;

public class CarEntity extends PathAwareEntity {
    private static final float MAX_STEER_ANGLE = 0.55F;

    private double carSpeedBlocksPerSecond = 8.0;

    public float steerAngle;
    public float lastSteerAngle;

    public double getCarSpeedBlocksPerSecond() {
        return this.carSpeedBlocksPerSecond;
    }

    public void setCarSpeedBlocksPerSecond(double carSpeedBlocksPerSecond) {
        this.carSpeedBlocksPerSecond = carSpeedBlocksPerSecond;
    }

    public CarEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setPersistent();
    }

    @Override
    public float getStepHeight() {
        return 2.0F;
    }

    public static DefaultAttributeContainer.Builder createCarAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0D);
    }

    @Override
    protected void initGoals() {
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof PlayerEntity player ? player : null;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        // 蹲下 + 手持汽车移除器 → 删除汽车并掉落对应颜色的车（此方法先于物品 useOnEntity 执行）
        if (player.isSneaking() && player.getStackInHand(hand).getItem() instanceof CarRemoverItem) {
            if (!this.getWorld().isClient) {
                Item carItem = SimpleCar.carItemForEntity(this.getType());
                if (carItem != null) {
                    this.dropStack(new ItemStack(carItem));
                }
                this.discard();
            }
            return ActionResult.success(this.getWorld().isClient);
        }
        if (!this.hasPassengers()) {
            player.startRiding(this);
            return ActionResult.success(this.getWorld().isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
        return new Vec3d(controllingPlayer.sidewaysSpeed * 0.5F, 0.0F, controllingPlayer.forwardSpeed);
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        World world = this.getWorld();
        double carX = this.getX();
        double carZ = this.getZ();
        int baseY = (int) Math.floor(this.getY());
        double[][] offsets = {
                {0.0D, 2.2D}, {0.0D, -2.2D}, {2.2D, 0.0D}, {-2.2D, 0.0D},
                {2.2D, 2.2D}, {2.2D, -2.2D}, {-2.2D, 2.2D}, {-2.2D, -2.2D}
        };
        for (double[] off : offsets) {
            int bx = (int) Math.floor(carX + off[0]);
            int bz = (int) Math.floor(carZ + off[1]);
            for (int up = 0; up < 2; up++) {
                BlockPos feet = new BlockPos(bx, baseY + up, bz);
                BlockPos below = feet.down();
                if (world.getBlockState(below).isFullCube(world, below)
                        && world.getBlockState(feet).isAir()
                        && world.getBlockState(feet.up()).isAir()) {
                    return new Vec3d(bx + 0.5D, baseY + up, bz + 0.5D);
                }
            }
        }
        return super.updatePassengerForDismount(passenger);
    }

    @Override
    protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
        return (float) (this.carSpeedBlocksPerSecond / 43.2);
    }

    @Override
    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
        super.tickControlled(controllingPlayer, movementInput);
        this.setPitch(this.getPitch() * 0.5F);
        // 只在玩家有驾驶输入或汽车仍在滑行时跟随玩家视角转向；
        // 完全停下后保持当前朝向，避免汽车在停止后自行旋转约90°。
        if (this.hasDriveInput(controllingPlayer) || this.getVelocity().horizontalLengthSquared() > 1.0E-6) {
            this.setYaw(controllingPlayer.getYaw());
        }
        // 与原版 PigEntity 保持一致：每个tick都将身体朝向/头部朝向/插值起始角与当前航向对齐，
        // 否则 MobEntity 的 BodyControl 会在停车后把车身慢慢旋转到头部方向。
        this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
    }

    private boolean hasDriveInput(PlayerEntity controllingPlayer) {
        return Math.abs(controllingPlayer.forwardSpeed) > 1.0E-4F
                || Math.abs(controllingPlayer.sidewaysSpeed) > 1.0E-4F;
    }

    @Override
    public void tick() {
        super.tick();
        this.lastSteerAngle = this.steerAngle;
        float target = 0.0F;
        LivingEntity controller = this.getControllingPassenger();
        if (controller != null) {
            target = controller.sidewaysSpeed * MAX_STEER_ANGLE;
        }
        this.steerAngle += (target - this.steerAngle) * 0.35F;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (this.hasPassenger(passenger)) {
            float yawRad = this.bodyYaw * MathHelper.RADIANS_PER_DEGREE;
            double forward = 0.4875D;
            double left = 0.375D;
            double x = this.getX() - Math.sin(yawRad) * forward + Math.cos(yawRad) * left;
            double z = this.getZ() + Math.cos(yawRad) * forward + Math.sin(yawRad) * left;
            positionUpdater.accept(passenger, x, this.getY() - 0.15D, z);
        } else {
            super.updatePassengerPosition(passenger, positionUpdater);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // 汽车免疫一切伤害；仅放行 /kill（GENERIC_KILL）与掉出世界（OUT_OF_WORLD）这类移除型伤害。
        if (source.isOf(DamageTypes.OUT_OF_WORLD) || source.isOf(DamageTypes.GENERIC_KILL)) {
            return super.damage(source, amount);
        }
        return false;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 0.15F, 0.6F);
    }
}
