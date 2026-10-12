package dev.kiradiamond.miniwynn;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class EmoteMathTest {
    public static void main(String[] args) {
        InventoryPreviewTest.run();
        protocolSurvivesScaling();
        hatFollowsRotatedHead();
        feetRemainAnchored();
        unrelatedPositionsAreRejected();
    }

    private static void protocolSurvivesScaling() {
        for (int limb = 0; limb < 6; limb++) {
            for (int skin = 0; skin < 2; skin++) {
                for (int fade = 0; fade < 3; fade++) {
                    float code = 1024 + 512 * (skin + 2 * (fade + 3 * limb)) + 255;
                    Matrix4f original = new Matrix4f().translation(0.4f, code + 0.7f, -0.2f)
                            .rotateXYZ(0.3f, 0.8f, -0.4f).scale(15);
                    Matrix4f result = EmoteMath.shrink(original, false, null);
                    assert EmoteMath.limb(result.m31()) == limb;
                    assert (int) Math.floor(result.m31() / 512) == (int) Math.floor(original.m31() / 512);
                    close(result.m30(), 0.22f);
                    close(result.m31() - code, 0.55f * 0.7f - 0.45f, 0.003f);
                    close(result.getScale(new Vector3f()).x, 8.25f);
                }
            }
        }
    }

    private static void hatFollowsRotatedHead() {
        for (float angle : new float[]{-1.2f, 0, 0.9f, 2.7f}) {
            Matrix4f head = new Matrix4f().translation(0.2f, 1791.4f, -0.1f)
                    .rotateXYZ(angle, 0.7f, -0.3f).scale(15);
            Matrix4f decodedHead = EmoteMath.decode(head);
            Matrix4f hat = new Matrix4f(decodedHead).translate(0.02f, 0.05f, -0.01f).scale(0.0375f);
            Matrix4f newHead = EmoteMath.decode(EmoteMath.shrink(head, true, decodedHead));
            Matrix4f newHat = EmoteMath.shrink(hat, false, decodedHead);
            Matrix4f expectedRelation = new Matrix4f(decodedHead).invert().mul(hat);
            Matrix4f actualRelation = new Matrix4f(newHead).invert().mul(newHat);
            assert expectedRelation.equals(actualRelation, 0.0005f) : "Hat detached at angle " + angle;
        }
    }

    private static void feetRemainAnchored() {
        Matrix4f feet = EmoteMath.shrink(new Matrix4f().translation(0, -1, 0), false, null);
        close(feet.m31(), -1);
    }

    private static void unrelatedPositionsAreRejected() {
        assert EmoteMath.limb(0) == -1;
        assert EmoteMath.limb(Float.NaN) == -1;
        assert EmoteMath.limb(999999) == -1;
    }

    private static void close(float actual, float expected) { close(actual, expected, 0.0001f); }
    private static void close(float actual, float expected, float tolerance) {
        assert Math.abs(actual - expected) < tolerance : actual + " != " + expected;
    }
}
