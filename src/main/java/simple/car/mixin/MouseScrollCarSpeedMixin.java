package simple.car.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import simple.car.entity.CarEntity;

@Mixin(Mouse.class)
public class MouseScrollCarSpeedMixin {
    private static final double MIN_SPEED = -20.0;
    private static final double MAX_SPEED = 200.0;
    private static final double STEP = 1.0;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void simplecar$adjustSpeedOnScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (this.client.player == null || this.client.currentScreen != null) {
            return;
        }
        if (!(this.client.player.getVehicle() instanceof CarEntity car)) {
            return;
        }
        boolean ctrl = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        if (!ctrl || vertical == 0) {
            return;
        }

        int steps = vertical > 0 ? (int) Math.ceil(vertical) : (int) Math.floor(vertical);
        double current = car.getCarSpeedBlocksPerSecond();
        double newSpeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, current + steps));
        car.setCarSpeedBlocksPerSecond(newSpeed);

        this.client.player.sendMessage(Text.literal("汽车速度: " + newSpeed), true);
        ci.cancel();
    }
}