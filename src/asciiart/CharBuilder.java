/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciiart;

import com.sun.swing.internal.plaf.synth.resources.synth;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import javax.imageio.ImageIO;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author theodik
 */
public class CharBuilder {
    public static final char REF_CHAR = 'H';
    public static final int  REF_WIDTH = 27;
    //public final ArrayList<Integer> chars = new ArrayList<Integer>();
    public final HashMap<Character, Integer> chars;
    public final Font font;
    
    public CharBuilder(String fontName, int size, int style) {
        font = new Font(fontName, style, size);
        chars = new HashMap<Character, Integer>();
    }
    
    public CharBuilder(String fileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new FileReader(fileName));
        int mapSize = sc.nextInt();
        chars = new HashMap<Character, Integer>(mapSize);
        String fontName = sc.nextLine();
        int style = sc.nextInt();
        int size = sc.nextInt();
        font = new Font(fontName, style, size);
        while (sc.hasNextLine()) {
            chars.put(sc.next().charAt(0), sc.nextInt());
        }
    }
    
    public void calcCharacters(String dir) throws IOException {
        final BufferedImage img = new BufferedImage(font.getSize()*2, font.getSize()*2, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics();
        g.setFont(font);
        
        StringBuilder data = new StringBuilder();
        int count = 0;
        for (char i = 0; i < Character.MAX_VALUE; i++) {
            if (font.canDisplay(i) && fm.charWidth(i) == fm.charWidth(REF_CHAR)) {
                g.setColor(Color.white);
                g.fillRect(0, 0, img.getWidth(), img.getHeight());
                g.setColor(Color.black);
                g.drawString(Character.toString(i), 0, fm.getMaxAscent());
                img.flush();
                //saveImgToFile("chars/"+(int)i+".png", img);
                int avgColor = avgColor(img, 0,0, fm.charWidth(i), fm.getHeight());
                chars.put((Character)i,avgColor & 0xFF);
                data.append((char)i).append(" ").append(avgColor & 0xFF).append("\n");
                count++;
            }
        }
        BufferedWriter wr = new BufferedWriter(
                new FileWriter(
                dir + this.font.getName() + "_" + this.font.getSize() + "_" + this.font.getStyle() + ".txt"
                ));
        wr.write(count + "\n");
        wr.write(font.getName() + "\n");
        wr.write(font.getStyle() + "\n");
        wr.write(font.getSize() + "\n");
        wr.append(data);
        wr.close();
    }
    
    public char getChar(int avgColor) {
        int ac = avgColor & 0xFF;
        int min = 0;
        char ret = '\0';
        for (Entry<Character, Integer> item : chars.entrySet()) {
            int amin = Math.abs(item.getValue() - ac);
            if (amin < min) {
                min = amin;
                ret = item.getKey();
            }
        }
        return ret;
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
    
    public static int avgColor(BufferedImage img, int x, int y, int w, int h) {
        int R = 0, G = 0, B = 0;
        for (int i = x; i < w; i++) {
            for (int j = y; j < h; j++) {
                int P = img.getRGB(i,j);
                R += P>>16 & 0xFF;
                G += P>>8 & 0xFF;
                B += P & 0xFF;
            }
        }
        R /= (w-x)*(h-y);
        G /= (w-x)*(h-y);
        B /= (w-x)*(h-y);
        return (R<<16)+(G<<8)+B;
    }
}
