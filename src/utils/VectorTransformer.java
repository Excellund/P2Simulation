package utils;

import java.util.Arrays;

public class VectorTransformer {
    public static double[][] scalePolygon(double[][] shape, double scalar) {
        double[][] result = Arrays.copyOf(shape, shape.length);

        for (int column = 0; column < shape[0].length; ++column) {
            result[0][column] *= scalar;
            result[1][column] *= scalar;
        }

        return result;
    }

    public static double[][] translatePolygon(double[][] shape, Vector transform) {
        double[][] result = Arrays.copyOf(shape, shape.length);

        for (int column = 0; column < shape[0].length; ++column) {
            result[0][column] += transform.x;
            result[1][column] += transform.y;
        }

        return result;
    }

    public static double[][] rotatePolygon(double[][] shape, Vector pivot, double rotation) {
        double[][] rotationMatrix =
                {
                        {Math.cos(Math.toRadians(rotation)), -Math.sin(Math.toRadians(rotation))},
                        {Math.sin(Math.toRadians(rotation)), Math.cos(Math.toRadians(rotation))}
                };

        double[][] coordinateReset = Arrays.copyOf(shape, shape.length);
        double[][] result = new double[shape.length][shape[0].length];

        translatePolygon(coordinateReset, new Vector(-pivot.x, -pivot.y));

        Arrays.fill(result[0], 0);
        Arrays.fill(result[1], 0);

        for (int row = 0; row < 2; ++row) {
            for (int shapeColumn = 0; shapeColumn < coordinateReset[0].length; ++shapeColumn) {
                for (int rotationColumn = 0; rotationColumn < 2; ++rotationColumn) {
                    result[row][shapeColumn] += rotationMatrix[row][rotationColumn] * coordinateReset[rotationColumn][shapeColumn];
                }
            }
        }

        translatePolygon(result, pivot);

        return result;
    }
}