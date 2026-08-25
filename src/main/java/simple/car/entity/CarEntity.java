package simple.car.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import simple.car.SimpleCar;

public class CarEntity extends PathAwareEntity {
    private static final float MAX_STEER_ANGLE = 0.55F;

    public float steerAngle;
    public float lastSteerAngle;

    public CarEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setStepHeight(1.0F);
        this.setPersistent();
    }

    public static DefaultAttributeContainer.Builder createCarAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0D);
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
    protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
        return (float) (SimpleCar.getCarSpeedBlocksPerSecond() / 43.2);
    }

    @Override
    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
        super.tickControlled(controllingPlayer, movementInput);
        this.prevYaw = this.getYaw();
        this.setYaw(controllingPlayer.getYaw());
        this.setPitch(this.getPitch() * 0.5F);
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
            positionUpdater.accept(passenger, x, this.getY() + 0.2875D, z);
        } else {
            super.updatePassengerPosition(passenger, positionUpdater);
        }
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
