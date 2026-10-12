package dev.kiradiamond.miniwynn;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public final class InventoryPreview {
    private InventoryPreview() {}

    public static void normalize(LivingEntityRenderState state, float scale) {
        state.scale = scale;
        if (state instanceof AvatarRenderState) {
            state.scale *= EmoteMath.BODY;
            // Vanilla divided the bounds by our smaller scale; undo that before shrinking them.
            float boundsScale = EmoteMath.BODY * EmoteMath.BODY;
            state.boundingBoxWidth *= boundsScale;
            state.boundingBoxHeight *= boundsScale;
            state.nameTag = null;
            state.nameTagAttachment = null;
            state.passengerOffset = null;
        }
    }
}
