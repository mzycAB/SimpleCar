package simple.car.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import simple.car.entity.CarEntity;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "getRiddenEntity", at = @At("HEAD"), cancellable = true)
    private void simplecar$hideCarHealth(CallbackInfoReturnable<LivingEntity> cir) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.getVehicle() instanceof CarEntity) {
            cir.setReturnValue(null);
        }
    }
}