package dev.kiradiamond.miniwynn;

import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;

final class SmallMobVisualsTest {
    private static final class TestAvatar extends Avatar {
        TestAvatar() { super(EntityType.MANNEQUIN, null); }
    }

    static void run() throws Exception {
        for (float scale : new float[] {0.5f, 1, 2}) {
            for (float age : new float[] {0.5f, 1}) {
                var state = new VillagerRenderState();
                state.scale = scale;
                state.ageScale = age;
                state.nameTag = Component.literal("Merchant");
                state.nameTagAttachment = new Vec3(0, 1.95 * scale * age, 0);
                var text = state.nameTag;
                SmallMobVisuals.resizeVillager(state);
                close(state.scale, scale * 0.55);
                close(state.nameTagAttachment.y, 1.95 * scale * age * 0.55
                        + 0.625 * scale * age * 0.55 * 0.5);
                assert state.nameTag == text : "Label text changed";
            }
        }
        var noLabel = new VillagerRenderState();
        noLabel.scale = 1;
        SmallMobVisuals.resizeVillager(noLabel);
        assert noLabel.nameTagAttachment == null;

        var owner = new TestAvatar();
        var label = new Display.TextDisplay(EntityType.TEXT_DISPLAY, null);
        mount(label, owner);
        var state = new EntityRenderState();
        state.x = 8; state.y = 11.95; state.z = -2;
        SmallMobVisuals.lowerAttachedLabel(label, state);
        double offset = owner.getBbHeight() * (0.55 - 1) + 0.5 * 0.9375 * 0.55 * 0.5;
        close(state.y, 11.95 + offset);
        close(state.x, 8); close(state.z, -2);
        assert owner.getScale() == 1 : "Server entity scale was changed";

        var nested = new Display.TextDisplay(EntityType.TEXT_DISPLAY, null);
        mount(nested, label);
        state.y = 12.25;
        SmallMobVisuals.lowerAttachedLabel(nested, state);
        close(state.y, 12.25 + offset);

        var standalone = new Display.TextDisplay(EntityType.TEXT_DISPLAY, null);
        state.y = 12;
        SmallMobVisuals.lowerAttachedLabel(standalone, state);
        close(state.y, 12);

        var unrelated = new ArmorStand(EntityType.ARMOR_STAND, null);
        mount(label, unrelated);
        SmallMobVisuals.lowerAttachedLabel(label, state);
        close(state.y, 12);

        var effect = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, null);
        mount(effect, owner);
        SmallMobVisuals.lowerAttachedLabel(effect, state);
        close(state.y, 12);

        var name = new ArmorStand(EntityType.ARMOR_STAND, null);
        name.setInvisible(true);
        name.setCustomName(Component.literal("NPC"));
        mount(name, owner);
        SmallMobVisuals.lowerAttachedLabel(name, state);
        close(state.y, 12 + offset);
        name.setInvisible(false);
        state.y = 12;
        SmallMobVisuals.lowerAttachedLabel(name, state);
        close(state.y, 12);
    }

    private static void mount(Entity child, Entity parent) throws Exception {
        var field = Entity.class.getDeclaredField("vehicle");
        field.setAccessible(true);
        field.set(child, parent);
    }

    private static void close(double actual, double expected) {
        assert Math.abs(actual - expected) < 0.00001 : actual + " != " + expected;
    }
}
