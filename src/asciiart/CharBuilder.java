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
 * Vyváří list znaků daného fontu a počítá průměrnou barvu každého znaku. 
 * @author theodik
 */
public class CharBuilder {
    /**
     * Referenční znak
     */
    public static final char REF_CHAR = 'H';
    /**
     * Tolerance šířky znaku.
     */
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
     * Naplní se pomocí {@link #calcCharacters() calcCharacters}
     * @param fontName Jméno fontu
     * @param size Velikost fontu
     * @param style Styl fontu (0: none, 1: bold, 2: italic)
     */
    public CharBuilder(String fontName, int size, int style) {
        font = new Font(fontName, style, size);
        chars = new HashMap<Character, Integer>();
    }
    
    /**
     * Vytvoří a naplní znaky ze souboru (formát souboru z {@linkplain #calcCharacters() calcCharacters})
     * @param fileName Soubor se znaky
     * @throws FileNotFoundException 
     */
    public CharBuilder(String fileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new FileReader(fileName));
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
     * <br/><br/>
     * Formát souboru (textový formát):
     * <code><br/>
     * (int)Počet Znaků\n<br/>
     * (String)Název fontu\n<br/>
     * (int)Velikost fontu\n<br/>
     * (int)Styl fontu\n<br/>
     * (int)Výška referenčního znaku\n<br/>
     * (int)Šířka referenčního znaku\n<br/>
     * (int)Kód znaku \30 (int)Barva<br/>
     * ...
     * </code>
     * @return Počet vygenerovaných znaků
     * @throws IOException 
     */
    public int calcCharacters() throws IOException {
        // Obrázek v odstínech šedi o velikosti velikost fontu * 2, aby se tam vešli všechny písmena,
        // jelikož zatím není k dispozici FontMetrics...
        final BufferedImage img = new BufferedImage(font.getSize()*2, font.getSize()*2, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics();
        g.setFont(font);
        
        REF_WIDTH = fm.charWidth(REF_CHAR);
        REF_HEIGHT = fm.getHeight();
        
        // Zde se ukládají spočítané znaky
        StringBuilder data = new StringBuilder();
        int count = 0; // Počet znaků
        for (char i = 1; i < Character.MAX_VALUE; i++) {
            int charWidth = fm.charWidth(i);
            // Zjistí jestli je znak validní Unicode znak, je ho možno zobrazit,
            // šířka je větší jak 0, není arabské písmeno (to se píše z prava doleva a to dělá problémy),
            // a jestli to neni speciální znak z bloku 'Combining Diacritical Marks Supplement'
            // ty mají divné chování (např. schovávají jeden znak...)
            if(Character.isValidCodePoint(i) && font.canDisplay(i) && charWidth > 0 &&
                !(i > 0x1DC0 && i < 0x1DFF) &&
                Character.UnicodeBlock.of(i) != Character.UnicodeBlock.ARABIC
              ) {
                // Smaže se plátno a napíše se znak, z něho se spočítá průměrná hodnota a uloží do data
                g.setColor(Color.white);
                g.fillRect(0, 0, img.getWidth(), img.getHeight());
                g.setColor(Color.black);
                g.drawString(Character.toString(i), 0, fm.getMaxAscent());
                img.flush();
                ///saveImgToFile("chars/"+(int)i+".png", img);
                int avgColor = avgColor8bit(img, 0,0, fm.charWidth(i), fm.getHeight());
                chars.put((Character)i,avgColor);
                data.append((int)i).append(" ").append(avgColor & 0xFF).append("\n");
                count++;
            }
        }
        // Výsledek se uloží do souboru
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
        
        return count;
    }
    
    /**
     * Vrací podle barvy nejvhodnější znak.
     * @param avgColor Barva šedi podle které se vybere znak
     * @return Nejvhodnější znak.
     */
    public char getChar(int avgColor) {
        // Grayscale je složení tří stejných bytů, tak nám stačí jen první byte
        int ac = avgColor & 0xFF; 
        int min = Integer.MAX_VALUE;
        char ret = '\0'; // \0 aby mi to tu neremcalo, že hodnota je neinicializovaná %)
        
        // Projedeme celé pole znaků a vybereme znak co má nejbližší hodnotu
        // pro každý znak odečteme |barvu ac od barvy znaku| a hledáme minimum.
        for (Entry<Character, Integer> item : chars.entrySet()) {
            int amin = Math.abs(item.getValue() - ac);
            if (amin < min) {
                min = amin;
                ret = item.getKey();
            }
        }
        return ret;
    }
    
    /**
     * Ukládá obrázek do souboru.
     * @param  fileName Název souboru
     * @param  img Obrázek, který se ukládá
     * @throws IOException
     */
    public static void saveImgToFile(String fileName, BufferedImage img) throws IOException {
        FileOutputStream os = new FileOutputStream(fileName);
        ImageIO.write(img, "png", os);
        os.close();
    }
    
    /**
     * Spočítá průměrnou hodnotu z dané oblasti obrázku.
     * @param img   Obrázek z kterého se čte
     * @param x     x souřadnice
     * @param y     y souřadnice
     * @param w     šířka oblasti, počítáno x+w
     * @param h     výška oblasti, počítáno y+h
     * @return      průměrná barva z oblasti.
     */
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
    
    /**
     * Stejně jak {@link #avgColor(java.awt.image.BufferedImage, int, int, int, int) avgColor},
     * ale počítá jen s prvními 8 bity z pixelu.
     * @param img   Obrázek z kterého se čte
     * @param x     x souřadnice
     * @param y     y souřadnice
     * @param w     šířka oblasti, počítáno x+w
     * @param h     výška oblasti, počítáno y+h
     * @return      průměrná barva z oblasti.
     */
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
