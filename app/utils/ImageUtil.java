package utils;

import play.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageUtil {
    public static byte[] convert(File file, String targetType) throws IOException {
        assert targetType.equals("jpg");

        Logger.info("Converting image " + file.toString() + " to " + targetType + ".");

        BufferedImage original = ImageIO.read(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage convertedImage = new BufferedImage(original.getWidth(null), original.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = convertedImage.createGraphics();
        g.drawImage(original, 0, 0, convertedImage.getWidth(), convertedImage.getHeight(), Color.WHITE, null);
        ImageIO.write(convertedImage, targetType, outputStream);

        return outputStream.toByteArray();
    }


    public static byte[] resize(File file, int width, int height) throws IOException {
        BufferedImage original = ImageIO.read(file);
        int x = 0, y = 0, minLength = 0;
        if (original.getHeight() < original.getWidth()) {
            minLength = original.getHeight();
            x = (original.getWidth() - minLength) / 2;
        } else {
            minLength = original.getWidth();
            y = (original.getHeight() - minLength) / 2;
        }
        BufferedImage image = new BufferedImage(width, height, original.getType());
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.drawImage(original.getSubimage(x, y, minLength, minLength), 0, 0, width, height, null);
        graphics2D.dispose();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        return outputStream.toByteArray();
    }
}
