package org.adrianvictor.livingroom.utils.image;

import org.adrianvictor.livingroom.data.catalog.Item;
import org.adrianvictor.livingroom.data.catalog.Property;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ColorExtractor {
    public static String getDominantColor(File imageFile) throws Exception {
        BufferedImage image = ImageIO.read(imageFile);

        // Downsample
        BufferedImage resized = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(image, 0, 0, 150, 150, null);
        g2d.dispose();

        // Score by saturation, skip neutrals
        Map<Integer, Double> colorScore = new HashMap<>();
        for (int y = 0; y < resized.getHeight(); y++) {
            for (int x = 0; x < resized.getWidth(); x++) {
                int rgb = resized.getRGB(x, y);

//                if (isNeutralColor(rgb)) {
//                    continue;
//                }

                float[] hsb = Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);
                double score = colorScore.getOrDefault(rgb, 0.0) + hsb[1];
                colorScore.put(rgb, score);
            }
        }

        int dominantColor = colorScore.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);

        return String.format("#%06X", dominantColor & 0xFFFFFF);
    }

    public static String getDominantColor(Item game) throws Exception {
        String image = game.properties().get(Property.ARTWORK);
        File imageFile = new File(game.location().getParent() + '/' + image);
        return getDominantColor(imageFile);
    }

}