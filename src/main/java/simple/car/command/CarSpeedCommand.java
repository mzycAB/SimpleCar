package simple.car.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import simple.car.entity.CarEntity;

public class CarSpeedCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("carspeed")
                        .then(CommandManager.argument("speed", DoubleArgumentType.doubleArg(-20.0D, 200.0D))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    PlayerEntity player = source.getPlayer();
                                    if (player == null || !(player.getVehicle() instanceof CarEntity car)) {
                                        source.sendError(Text.translatable("message.simplecar.not_in_car"));
                                        return 0;
                                    }
                                    double speed = DoubleArgumentType.getDouble(context, "speed");
                                    car.setCarSpeedBlocksPerSecond(speed);
                                    source.sendFeedback(
                                            () -> Text.translatable("message.simplecar.speed_set", speed), true);
                                    return (int) Math.round(speed);
                                }))));
    }
}
