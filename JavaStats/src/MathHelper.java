public class MathHelper {
    public static float[] shift(float[] arr, float shift) {
        float[] end = new float[arr.length];
        for (int i = 0; i < arr.length; i++) end[i] = arr[i] + shift;
        return end;
    }
}
