package dev.kiradiamond.miniwynn;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import org.joml.Matrix4f;

public final class EmoteDisplays {
    private EmoteDisplays() {}

    public static void resize(Display display, DisplayEntityRenderState state, float partialTick) {
        if (state.renderState == null || !(display instanceof Display.ItemDisplay)) return;
        Entity root = display.getRootVehicle();
        if (!(root instanceof Display.ItemDisplay) || root.getPassengers().size() < 6
                || root.getPassengers().size() > 12) return;
        int limbs = 0;
        int partCount = 0;
        Display.ItemDisplay head = null;
        for (Entity passenger : root.getPassengers()) {
            if (!(passenger instanceof Display.ItemDisplay part)) continue;
            int limb = playerLimb(part, partialTick);
            if (limb < 0) continue;
            limbs |= 1 << limb;
            partCount++;
            if (limb == 0) head = part;
        }
        if (partCount != 6 || limbs != 63 || head == null) return;

        Display.RenderState original = state.renderState;
        Display.ItemDisplay.ItemRenderState item = ((Display.ItemDisplay) display).itemRenderState();
        int ownLimb = playerLimb((Display.ItemDisplay) display, partialTick);
        boolean hat = ownLimb < 0 && item != null && item.itemTransform() == ItemDisplayContext.HEAD;
        Matrix4f headPose = EmoteMath.decode(head.renderState().transformation()
                .get(head.calculateInterpolationProgress(partialTick)).getMatrix());
        state.renderState = new Display.RenderState(
                progress -> new Transformation(EmoteMath.shrink(
                        original.transformation().get(progress).getMatrix(),
                        ownLimb == 0, hat || ownLimb == 0 ? headPose : null)),
                original.billboardConstraints(), original.brightnessOverride(),
                progress -> original.shadowRadius().get(progress) * EmoteMath.BODY,
                original.shadowStrength(), original.glowColorOverride());
    }

    private static int playerLimb(Display.ItemDisplay display, float partialTick) {
        Display.ItemDisplay.ItemRenderState item = display.itemRenderState();
        Display.RenderState render = display.renderState();
        if (item == null || render == null || !item.itemStack().is(Items.PLAYER_HEAD)) return -1;
        CustomModelData data = item.itemStack().get(DataComponents.CUSTOM_MODEL_DATA);
        if (data == null || data.getFloat(0) == null || data.getFloat(0) != 100.0f) return -1;
        return EmoteMath.limb(render.transformation().get(
                display.calculateInterpolationProgress(partialTick)).getTranslation().y());
    }
}
