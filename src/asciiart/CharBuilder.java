/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciiart;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author theodik
 */
public class CharBuilder {
    public final CharPart[] chars;
    public final Font font;
    
    public CharBuilder(String fontName, int size, int style) {
        font = new Font(fontName, style, size);
        chars = new CharPart[Character.MAX_VALUE];
    }
    
    public CharBuilder(String fileName, String fontName, int size, int style) {
        this(fontName, style, size);
        // Load from file
    }
    
    public void calcCharacters() {
        final BufferedImage img = new BufferedImage(font.getSize(), font.getSize(), BufferedImage.TYPE_BYTE_GRAY);
        final Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics();
        g.setFont(font);
        for (char i = 0; i < 1000; i++) {
            g.setColor(Color.white);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
            g.setColor(Color.black);
            g.drawString(Character.toString(i), 0, fm.getMaxAscent());
            img.flush();
            saveImgToFile("chars/"+(int)i+".png", img);
        }
    }
    
    public static void saveImgToFile(String fileName, BufferedImage img) {
        try {
            FileOutputStream os = new FileOutputStream(fileName);
            ImageIO.write(img, "png", os);
            os.close();
        } catch (Exception e) {
            System.out.println("e = " + e.getLocalizedMessage());
        }
    }
}
