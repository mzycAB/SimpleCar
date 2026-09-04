package simple.car.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import simple.car.SimpleCar;
import simple.car.client.model.CarEntityModel;
import simple.car.client.render.CarEntityRenderer;

public class SimpleCarClient implements ClientModInitializer {
    public static final EntityModelLayer CAR_MODEL_LAYER =
            new EntityModelLayer(new Identifier(SimpleCar.MOD_ID, "car"), "main");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(SimpleCar.CAR_ENTITY, CarEntityRenderer::new);
        EntityRendererRegistry.register(SimpleCar.CAR_WHITE_ENTITY, CarEntityRenderer::new);
        EntityRendererRegistry.register(SimpleCar.CAR_GRAY_ENTITY, CarEntityRenderer::new);
        SimpleCar.CAR_COLOR_ENTITIES.values().forEach(type ->
                EntityRendererRegistry.register(type, CarEntityRenderer::new));
        EntityModelLayerRegistry.registerModelLayer(CAR_MODEL_LAYER, CarEntityModel::getTexturedModelData);
    }
}
