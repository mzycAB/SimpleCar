package simple.car.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import simple.car.SimpleCar;
import simple.car.client.SimpleCarClient;
import simple.car.client.model.CarEntityModel;
import simple.car.entity.CarEntity;

import java.util.HashMap;
import java.util.Map;

public class CarEntityRenderer extends MobEntityRenderer<CarEntity, CarEntityModel> {
    private static final Identifier RED_TEXTURE = new Identifier(SimpleCar.MOD_ID, "textures/entity/car.png");
    private static final Map<EntityType<?>, Identifier> TEXTURES = new HashMap<>();

    static {
        TEXTURES.put(SimpleCar.CAR_ENTITY, RED_TEXTURE);
        TEXTURES.put(SimpleCar.CAR_WHITE_ENTITY, new Identifier(SimpleCar.MOD_ID, "textures/entity/car_white.png"));
        TEXTURES.put(SimpleCar.CAR_GRAY_ENTITY, new Identifier(SimpleCar.MOD_ID, "textures/entity/car_gray.png"));
        SimpleCar.CAR_COLOR_ENTITIES.forEach((color, type) ->
                TEXTURES.put(type, new Identifier(SimpleCar.MOD_ID, "textures/entity/car_" + color + ".png")));
    }

    public CarEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CarEntityModel(context.getPart(SimpleCarClient.CAR_MODEL_LAYER)), 1.4F);
    }

    @Override
    protected void scale(CarEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(1.6F, 1.5F, 2.0F);
    }

    @Override
    public Identifier getTexture(CarEntity entity) {
        return TEXTURES.getOrDefault(entity.getType(), RED_TEXTURE);
    }
}
