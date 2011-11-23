/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciiart;

import java.io.IOException;

/**
 *
 * @author theodik
 */
public class ASCIIart {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        CharBuilder cb = new CharBuilder("Monospace", 12, 0);
        cb.calcCharacters();
    }
}
