/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciiart;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author theodik
 */
public class ASCIIart {
    static String fontName, dir;
    static int size, style;    
    
    public static void readParams(String[] args) {
        if(args.length >= 3) {
            fontName = args[0];
            size = Integer.parseInt(args[1]);
            style = Integer.parseInt(args[2]);
            if(args.length > 3)
                dir = args[3];
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Font name: ");
            fontName = sc.next();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        readParams(args);

        CharBuilder cb;
        if (new File(fontName + "_" + size + "_" + style + ".txt").exists()) {
            cb = new CharBuilder(dir + fontName + "_" + size + "_" + style + ".txt");
        } else {
            cb = new CharBuilder(dir + fontName, size, style);
        }
        cb.calcCharacters("");
    }
}
