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
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 * Vytvoří a udržuje seznam písmen a jejich průměrných barev.
 * @author theodik
 */
public class CharBuilder {
    /**
     * Referenční znak
     */
    public static final char REF_CHAR = 'H';
    public static final int REF_VAR = 3;
    /**
     * Šířka referenčního znaku.
     */
    public static int  REF_WIDTH = 27;
    /**
     * Výška referenčního znaku
     */
    public static int REF_HEIGHT;
    private final HashMap<Character, Integer> chars;
    private final Font font;
    
    /**
     * Vytvoří prázdný seznam písmen.
     * Naplní se pomocí {@link #calcCharacters(java.lang.String) calcCharacters}
     * @param fontName Jméno fontu
     * @param size Velikost fontu
     * @param style Styl fontu (0: none, 1: bold, 2: italic)
     */
    public CharBuilder(String fontName, int size, int style) {
        font = new Font(fontName, style, size);
        chars = new HashMap<Character, Integer>();
    }
    
    /**
     * Vytvoří a naplní znaky ze souboru.
     * @param fileName Soubor se znaky
     * @throws FileNotFoundException 
     */
    public CharBuilder(String fileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new FileReader(fileName));
        //sc.useDelimiter("\n *");
        int mapSize = sc.nextInt();
        sc.nextLine();
        String fontName = sc.nextLine();
        int style = sc.nextInt();
        int size = sc.nextInt();
        REF_HEIGHT = sc.nextInt();
        REF_WIDTH = sc.nextInt();
        
        chars = new HashMap<Character, Integer>(mapSize);
        font = new Font(fontName, style, size);
        while (sc.hasNext()) {
            chars.put((char)sc.nextInt(), sc.nextInt());
        }
    }
    
    /**
     * Vytvoří seznam s písmeny, spočítá jejich průměrnou barvu a uloží do souboru
     * v aktuální složce ve formátu <code>fontName_fontSize_fontStyle.txt</code>.
     * @throws IOException 
     */
    public void calcCharacters() throws IOException {
        final BufferedImage img = new BufferedImage(font.getSize()*2, font.getSize()*2, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics();
        g.setFont(font);
        
        REF_WIDTH = fm.charWidth(REF_CHAR);
        REF_HEIGHT = fm.getHeight();
        
        StringBuilder data = new StringBuilder();
        int count = 0;
        for (char i = 1; i < Character.MAX_VALUE; i++) {
            if (i > 0x1DC0 && i < 0x1DFF)
                continue;
            int charWidth = fm.charWidth(i);
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(i);
            //if (font.canDisplay(i)  && (charWidth > REF_WIDTH && charWidth < REF_WIDTH+REF_VAR)) {
            if(Character.isValidCodePoint(i) && font.canDisplay(i) && 
                    !Character.isSpaceChar(i) &&
                    ub != Character.UnicodeBlock.ARABIC &&
                    //charWidth > REF_WIDTH-REF_VAR && charWidth < REF_WIDTH+REF_VAR
                    charWidth > 0
                    ) {
                g.setColor(Color.white);
                g.fillRect(0, 0, img.getWidth(), img.getHeight());
                g.setColor(Color.black);
                g.drawString(Character.toString(i), 0, fm.getMaxAscent());
                img.flush();
                //saveImgToFile("chars/"+(int)i+".png", img);
                int avgColor = avgColor8bit(img, 0,0, fm.charWidth(i), fm.getHeight());
                chars.put((Character)i,avgColor);
                data.append((int)i).append(" ").append(avgColor & 0xFF).append("\n");
                count++;
            }
        }
        BufferedWriter wr = new BufferedWriter(
                new FileWriter(
                    this.font.getName() + "_" + this.font.getSize() + "_" + this.font.getStyle() + ".txt"
                ));
        wr.write(count + "\n");
        wr.write(font.getName() + "\n");
        wr.write(font.getStyle() + "\n");
        wr.write(font.getSize() + "\n");
        wr.write(REF_HEIGHT + "\n");
        wr.write(REF_WIDTH + "\n");
        wr.append(data);
        wr.close();
    }
    
    /**
     * Vrací podle barvy nejvhodnější znak.
     * @param avgColor Barva šedi podle které se vybere znak
     * @return Nejvhodnější znak.
     */
    public char getChar(int avgColor) {
        int ac = avgColor & 0xFF;
        int min = Integer.MAX_VALUE;
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
    
    public static int avgColor8bit(BufferedImage img, int x, int y, int w, int h) {
        int B = 0;
        for (int i = x; i < w; i++) {
            for (int j = y; j < h; j++) {
                int P = img.getRGB(i,j);
                B += P & 0xFF;
            }
        }
        B /= (w-x)*(h-y);
        return B;
    }
}
