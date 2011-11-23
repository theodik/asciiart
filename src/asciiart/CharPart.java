/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciiart;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 *
 * @author theodik
 */
public class CharPart {
    public final char C;
    public final int[] part;

    public CharPart(char C) {
        this.C = C;
        this.part = new int[4];
    }
    
    public CharPart(char C, int[] part) {
        this.C = C;
        this.part = part;
    }

    public CharPart(char C, int part1, int part2, int part3, int part4) {
        this(C);
        this.part[0] = part1;
        this.part[1] = part1;
        this.part[2] = part1;
        this.part[3] = part1;
    }
    
    public CharPart(char C, BufferedImage img, int x, int y, int w, int h) {
        this(C);
        this.part[0] = avgColor(img, x, y, w/2, h/2);
        this.part[1] = avgColor(img, w/2, y, w, h/2);
        this.part[2] = avgColor(img, x, h/2, w/2, h);
        this.part[3] = avgColor(img, w/2, h/2, w, h);
    }

    public char getC() {
        return C;
    }
    
    public int[] getPart() {
        return part;
    }
    
    public int getPart(int num){
        return this.part[num];
    }

    @Override
    public String toString() {
        return C + " " + part[0] + " " + part[1] + " " + part[2] + " " + part[3]; 
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof CharPart)
            return Arrays.equals(this.part, ((CharPart)o).part);
        else
            return false;
    }
    
    public static int avgColor(BufferedImage img, int x0, int y0, int w, int h) {
        int R = 0, G = 0, B = 0;
        for (int x = x0; x < w; x++) {
            for (int y = y0; y < h; y++) {
                int col = img.getRGB(x, y);
                R += (col >> 16) & 0xFF;
                G += (col >> 8) & 0xFF;
                B += col & 0xFF;
            }
        }
        R /= w*h;
        G /= w*h;
        B /= w*h;
        return (R<<16)+(G<<8)+B;
    }
}
