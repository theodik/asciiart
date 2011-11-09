/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciiart;

import java.util.Arrays;

/**
 *
 * @author theodik
 */
public class CharPart {
    public final int[] part;

    public CharPart() {
        this.part = new int[3];
    }
    
    public CharPart(int[] part) {
        this.part = part;
    }

    public CharPart(int part1, int part2, int part3, int part4) {
        this();
        this.part[0] = part1;
        this.part[1] = part1;
        this.part[2] = part1;
        this.part[3] = part1;
    }

    public int[] getPart() {
        return part;
    }
    
    public int getPart(int num){
        return this.part[num];
    }

    @Override
    public String toString() {
        return part.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof CharPart)
            return Arrays.equals(this.part, ((CharPart)o).getPart());
        else
            return false;
    }
    
    
}
