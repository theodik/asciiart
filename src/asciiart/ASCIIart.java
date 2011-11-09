/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciiart;

/**
 *
 * @author theodik
 */
public class ASCIIart {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CharBuilder cb = new CharBuilder("Monospace", 12, 0);
        cb.calcCharacters();
    }
}
