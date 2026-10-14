package dev.kiradiamond.miniwynn;

import com.google.gson.Gson;
import com.google.common.collect.ImmutableList;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class EmoteDisplaysTest {
    private record Part(String item, float model, ItemDisplayContext context, float[] matrix) {}

    static void run() throws Exception {
        try (var input = EmoteDisplaysTest.class.getResourceAsStream("/crate-animation.json")) {
            if (input == null) throw new AssertionError("Missing crate animation fixture");
            Part[][] frames = new Gson().fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), Part[][].class);
            for (Part[] frame : frames) {
                var root = display(ItemStack.EMPTY, ItemDisplayContext.NONE, new Matrix4f());
                List<Entity> parts = new ArrayList<>();
                for (Part part : frame) {
                    ItemStack stack = switch (part.item) {
                        case "player_head" -> new ItemStack(Items.PLAYER_HEAD);
                        case "oak_boat" -> new ItemStack(Items.OAK_BOAT);
                        default -> ItemStack.EMPTY;
                    };
                    if (!stack.isEmpty()) stack.set(DataComponents.CUSTOM_MODEL_DATA,
                            new CustomModelData(List.of(part.model), List.of(), List.of(), List.of()));
                    var entity = display(stack, part.context, new Matrix4f().set(part.matrix));
                    set(Entity.class, entity, "vehicle", root);
                    parts.add(entity);
                }
                set(Entity.class, root, "passengers", ImmutableList.copyOf(parts));
                for (int i = 0; i < frame.length; i++) {
                    var entity = (Display.ItemDisplay) parts.get(i);
                    var state = new ItemDisplayEntityRenderState();
                    state.renderState = entity.renderState();
                    var original = state.renderState;
                    EmoteDisplays.resize(entity, state, 1);
                    if (frame[i].item.equals("player_head")) {
                        assert state.renderState != original : "Crate animation left a player limb full-sized";
                        var before = original.transformation().get(1).getMatrix();
                        var after = state.renderState.transformation().get(1).getMatrix();
                        int limb = EmoteMath.limb(before.m31());
                        float expected = before.getScale(new Vector3f()).x * EmoteMath.BODY
                                * (limb == 0 ? EmoteMath.HEAD : 1);
                        assert Math.abs(after.getScale(new Vector3f()).x - expected) < 0.001;
                        assert EmoteMath.limb(after.m31()) == limb : "Crate animation lost skin metadata";
                    } else {
                        assert state.renderState == original : "Player scaling changed the crate or its contents";
                    }
                }
                var hat = display(new ItemStack(Items.POTION), ItemDisplayContext.HEAD,
                        new Matrix4f().translation(0, 1, 0).scale(0.7f));
                set(Entity.class, hat, "vehicle", root);
                var hatState = new ItemDisplayEntityRenderState();
                hatState.renderState = hat.renderState();
                EmoteDisplays.resize(hat, hatState, 1);
                assert Math.abs(hatState.renderState.transformation().get(1).getMatrix()
                        .getScale(new Vector3f()).x - 0.7f * EmoteMath.BODY * EmoteMath.HEAD) < 0.001
                        : "Head equipment stopped following a mixed animation";

                parts.remove(0);
                set(Entity.class, root, "passengers", ImmutableList.copyOf(parts));
                var state = new ItemDisplayEntityRenderState();
                state.renderState = ((Display.ItemDisplay)parts.getFirst()).renderState();
                var original = state.renderState;
                EmoteDisplays.resize((Display)parts.getFirst(), state, 1);
                assert state.renderState == original : "Incomplete body was treated as an animation";
            }
        }
    }

    private static Display.ItemDisplay display(ItemStack stack, ItemDisplayContext context, Matrix4f matrix) throws Exception {
        var entity = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, null);
        set(Display.ItemDisplay.class, entity, "itemRenderState", new Display.ItemDisplay.ItemRenderState(stack, context));
        set(Display.class, entity, "renderState", new Display.RenderState(progress -> new Transformation(matrix),
                Display.BillboardConstraints.FIXED, -1, progress -> 0, progress -> 1, -1));
        return entity;
    }

    private static void set(Class<?> type, Object target, String name, Object value) throws Exception {
        var field = type.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
