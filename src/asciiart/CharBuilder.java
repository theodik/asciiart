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
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 *
 * @author theodik
 */
public class CharBuilder {
    public static final char REF_CHAR = 'H';
    public static final int  REF_WIDTH = 27;
    public final ArrayList<CharPart> chars = new ArrayList<CharPart>();
    public final Font font;
    
    public CharBuilder(String fontName, int size, int style) {
        font = new Font(fontName, style, size);
    }
    
    public CharBuilder(String fileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new FileReader(fileName));
        String fontName = sc.nextLine();
        int style = sc.nextInt();
        int size = sc.nextInt();
        font = new Font(fontName, style, size);
        while (sc.hasNextLine()) {
            CharPart cp = new CharPart(sc.next().charAt(0), sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt());
            chars.add(cp);
        }
    }
    
    public void calcCharacters() throws IOException{
        final BufferedImage img = new BufferedImage(font.getSize()*2, font.getSize()*2, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics();
        g.setFont(font);
        
        BufferedWriter wr = new BufferedWriter(new FileWriter(this.font.getName()+".txt"));
        wr.write(font.getName() + "\n");
        wr.write(font.getStyle() + "\n");
        wr.write(font.getSize() + "\n");
        
        for (char i = 0; i < Character.MAX_VALUE; i++) {
            if (font.canDisplay(i) && fm.charWidth(i) == fm.charWidth(REF_CHAR)) {
                g.setColor(Color.white);
                g.fillRect(0, 0, img.getWidth(), img.getHeight());
                g.setColor(Color.black);
                g.drawString(Character.toString(i), 0, fm.getMaxAscent());
                img.flush();
                //saveImgToFile("chars/"+(int)i+".png", img);
                CharPart cp = new CharPart((char)i, img, 0,0, fm.charWidth(i), fm.getHeight());
                wr.write(cp.toString() + "\n");
            }
        }
        wr.close();
    }
    
    public char getChar(CharPart cp) {
        
        return '0';
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
