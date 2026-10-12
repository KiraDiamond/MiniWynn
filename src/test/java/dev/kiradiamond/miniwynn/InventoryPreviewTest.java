package dev.kiradiamond.miniwynn;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

final class InventoryPreviewTest {
    static void run() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        for (float originalScale : new float[]{0.5f, 1, 2}) {
            for (float height : new float[]{1.5f, 1.8f}) {
                AvatarRenderState player = new AvatarRenderState();
                player.scale = originalScale * EmoteMath.BODY;
                player.boundingBoxWidth = 0.6f * originalScale / player.scale;
                player.boundingBoxHeight = height * originalScale / player.scale;
                player.nameTag = Component.literal("Player");
                player.nameTagAttachment = new Vec3(0, height, 0);
                player.passengerOffset = new Vec3(0, 0.3, 0);

                InventoryPreview.normalize(player, 1);

                close(player.scale, EmoteMath.BODY);
                close(player.boundingBoxWidth, 0.6f * EmoteMath.BODY);
                close(player.boundingBoxHeight, height * EmoteMath.BODY);
                assert player.nameTag == null : "Name tag overlaps the inventory preview";
                assert player.nameTagAttachment == null;
                assert player.passengerOffset == null : "Mount offset shifts the inventory preview";
            }
        }

        LivingEntityRenderState mob = new LivingEntityRenderState();
        mob.scale = 2;
        mob.boundingBoxWidth = 0.6f;
        mob.boundingBoxHeight = 1.8f;
        mob.nameTag = Component.literal("Mob");
        mob.passengerOffset = new Vec3(0, 0.3, 0);
        InventoryPreview.normalize(mob, 1);
        close(mob.scale, 1);
        close(mob.boundingBoxWidth, 0.6f);
        close(mob.boundingBoxHeight, 1.8f);
        assert mob.nameTag != null;
        assert mob.passengerOffset != null;
    }

    private static void close(float actual, float expected) {
        assert Math.abs(actual - expected) < 0.0001f : actual + " != " + expected;
    }
}
