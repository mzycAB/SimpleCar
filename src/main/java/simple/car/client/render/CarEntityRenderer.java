package simple.car.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import simple.car.SimpleCar;
import simple.car.client.SimpleCarClient;
import simple.car.client.model.CarEntityModel;
import simple.car.entity.CarEntity;

public class CarEntityRenderer extends MobEntityRenderer<CarEntity, CarEntityModel> {
    private static final Identifier TEXTURE = new Identifier(SimpleCar.MOD_ID, "textures/entity/car.png");

    public CarEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CarEntityModel(context.getPart(SimpleCarClient.CAR_MODEL_LAYER)), 1.4F);
    }

    @Override
    protected void scale(CarEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(2.0F, 2.0F, 2.0F);
    }

    @Override
    public Identifier getTexture(CarEntity entity) {
        return TEXTURE;
    }
}
