package simple.car.client.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import simple.car.entity.CarEntity;

public class CarEntityModel extends EntityModel<CarEntity> {
    private final ModelPart root;
    private final ModelPart frontLeftWheel;
    private final ModelPart frontRightWheel;
    private final ModelPart backLeftWheel;
    private final ModelPart backRightWheel;

    public CarEntityModel(ModelPart root) {
        this.root = root;
        this.frontLeftWheel = root.getChild("front_left_wheel");
        this.frontRightWheel = root.getChild("front_right_wheel");
        this.backLeftWheel = root.getChild("back_left_wheel");
        this.backRightWheel = root.getChild("back_right_wheel");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("body", ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-12.0F, -12.0F, -18.0F, 24.0F, 8.0F, 36.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        root.addChild("roof", ModelPartBuilder.create()
                        .uv(0, 76)
                        .cuboid(-9.0F, -20.0F, -8.0F, 18.0F, 2.0F, 16.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        root.addChild("left_wall", ModelPartBuilder.create()
                        .uv(0, 96)
                        .cuboid(-9.0F, -18.0F, -8.0F, 2.0F, 6.0F, 16.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        root.addChild("right_wall", ModelPartBuilder.create()
                        .uv(40, 96)
                        .cuboid(7.0F, -18.0F, -8.0F, 2.0F, 6.0F, 16.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        root.addChild("back_wall", ModelPartBuilder.create()
                        .uv(80, 96)
                        .cuboid(-7.0F, -18.0F, 6.0F, 14.0F, 6.0F, 2.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        root.addChild("front_left_wheel", ModelPartBuilder.create()
                        .uv(96, 48)
                        .cuboid(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F),
                ModelTransform.pivot(-11.0F, 20.0F, -12.0F));

        root.addChild("front_right_wheel", ModelPartBuilder.create()
                        .uv(96, 48)
                        .cuboid(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F),
                ModelTransform.pivot(11.0F, 20.0F, -12.0F));

        root.addChild("back_left_wheel", ModelPartBuilder.create()
                        .uv(96, 48)
                        .cuboid(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F),
                ModelTransform.pivot(-11.0F, 20.0F, 12.0F));

        root.addChild("back_right_wheel", ModelPartBuilder.create()
                        .uv(96, 48)
                        .cuboid(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F),
                ModelTransform.pivot(11.0F, 20.0F, 12.0F));

        root.addChild("left_headlight", ModelPartBuilder.create()
                        .uv(96, 72)
                        .cuboid(-9.0F, -11.0F, -19.0F, 2.0F, 2.0F, 1.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        root.addChild("right_headlight", ModelPartBuilder.create()
                        .uv(96, 72)
                        .cuboid(7.0F, -11.0F, -19.0F, 2.0F, 2.0F, 1.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        root.addChild("left_taillight", ModelPartBuilder.create()
                        .uv(104, 72)
                        .cuboid(-9.0F, -11.0F, 18.0F, 2.0F, 2.0F, 1.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        root.addChild("right_taillight", ModelPartBuilder.create()
                        .uv(104, 72)
                        .cuboid(7.0F, -11.0F, 18.0F, 2.0F, 2.0F, 1.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(CarEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.frontLeftWheel.pitch = limbAngle;
        this.frontRightWheel.pitch = limbAngle;
        this.backLeftWheel.pitch = limbAngle;
        this.backRightWheel.pitch = limbAngle;

        this.frontLeftWheel.yaw = entity.steerAngle;
        this.frontRightWheel.yaw = entity.steerAngle;
    }
}
