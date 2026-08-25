package simple.car.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import simple.car.SimpleCar;

public class CarSpeedCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("carspeed")
                        .then(CommandManager.argument("speed", DoubleArgumentType.doubleArg(0.1D, 50.0D))
                                .executes(context -> {
                                    double speed = DoubleArgumentType.getDouble(context, "speed");
                                    SimpleCar.setCarSpeedBlocksPerSecond(speed);
                                    context.getSource().sendFeedback(
                                            () -> Text.translatable("message.simplecar.speed_set", speed), true);
                                    return (int) Math.round(speed);
                                }))));
    }
}
