package dev.kiradiamond.miniwynn;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public final class EmoteMath {
    public static final float BODY = 0.55f;
    public static final float HEAD = 1.5f;
    private static final float FOOT_Y = -1.0f;

    private EmoteMath() {}

    public static int limb(float y) {
        if (!Float.isFinite(y) || y < 1024 || y >= 19456) return -1;
        return ((int) y - 1024) / 3072;
    }

    private static float code(float y) {
        return limb(y) < 0 ? 0 : (float) Math.floor(y / 512) * 512 + 255;
    }

    public static Matrix4f decode(Matrix4fc source) {
        return new Matrix4f(source).m31(source.m31() - code(source.m31()));
    }

    public static Matrix4f shrink(Matrix4fc source, boolean head, Matrix4fc decodedHead) {
        float metadata = code(source.m31());
        Matrix4f physical = decode(source);
        Matrix4f adjustment = new Matrix4f().translation(0, FOOT_Y * (1 - BODY), 0).scale(BODY);
        if (head || decodedHead != null) {
            Vector3f pivot = (decodedHead == null ? physical : decodedHead).getTranslation(new Vector3f());
            adjustment.translate(pivot).scale(HEAD).translate(pivot.negate());
        }
        Matrix4f result = adjustment.mul(physical);
        // Keep the pack's limb metadata outside the spatial transform.
        return result.m31(result.m31() + metadata);
    }
}
