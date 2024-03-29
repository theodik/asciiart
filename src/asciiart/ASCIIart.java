package asciiart;
/**
 * Vytvoří ze vstupního obrázku podobný pomocí znaků.
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 * Vytvoří ze vstupního obrázku podobný pomocí znaků.
 * @author theodik
 */
public class ASCIIart {
    static final int RATIO = 1;
    static CharBuilder cb;
    static String fontName, ifile, ofile;
    static int size, style;

    /**
     * První parametr - vstupní obrázek
     * Druhý parametr - výstupní soubor
     * Třetí - název fontu
     * Čtvrtý velikost fontu
     * Pátý - styl fontu (0: none, 1: bold, 2: italic)
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Načteme parametry
        readParams(args);
        
        // Jestli už existuje nějaký soubor s vygenerovanými znaky, použije se ten
        // jinak se nejdřív vygenerují znaky
        if (new File(fontName + "_" + size + "_" + style + ".txt").exists()) {
            System.out.println("Načítám data ze souboru: '"+fontName + "_" + size + "_" + style + ".txt'");
            try {
                cb = new CharBuilder(fontName + "_" + size + "_" + style + ".txt");
            } catch(FileNotFoundException e) {
                System.out.println("Soubor nenalezen: "+e);
                return;
            }
        } else {
            cb = new CharBuilder(fontName, size, style);
            System.out.println("Vytvářím soubor se znaky: "+fontName + "_" + size + "_" + style + ".txt");
            try {
                int count = cb.calcCharacters();
                System.out.println("Počet znaků: " + count);
            } catch(IOException e) {
                System.out.println("Nelze zapsat soubor se znaky: "+e);
                (new File(fontName + "_" + size + "_" + style + ".txt")).delete();
                return;
            }
        }
        
        // Načte obrázek a převede na grayscale.
        BufferedImage img;
        FileWriter fw;
        try{
            FileInputStream fs = new FileInputStream(ifile);
            BufferedImage orig = ImageIO.read(fs);
            img = new BufferedImage(orig.getWidth(), orig.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            img.getGraphics().drawImage(orig, 0, 0, null);
            //CharBuilder.saveImgToFile("input-test.jpg", img);
            fs.close();
        } catch(IOException e){
            System.out.println("Nemohl jsem načíst vstupní obrázek: "+e);
            return;
        }
        
        // Vytvoří obrázek pomocí znaků
        try {
            fw = new FileWriter(ofile);
            System.out.println("Vytvářím obrázek...");
            createArt(img, fw);
            fw.close();
            System.out.println("Hotovo.");
        } catch(IOException e) {
            System.out.println(e);
            return;
        }
    }
    
    /**
     * Zpracuje argumenty, pokud nejsou zeptá se.
     * @param args the command line arguments
     */
    public static void readParams(String[] args) {
        if(args.length >= 3) {
            ifile = args[0];
            ofile = args[1];
            fontName = args[2];
            size = Integer.parseInt(args[3]);
            style = Integer.parseInt(args[4]);
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Input image: ");
            ifile = sc.next();
            System.out.print("\nOutput file: ");
            ofile = sc.next();
            System.out.print("Font name: ");
            fontName = sc.next();
            System.out.print("\nFont size: ");
            size = sc.nextInt();
            System.out.print("\nFont style (0: none, 1: bold, 2: italic): ");
            style = sc.nextInt();
        }
    }
    
    /**
     * Vytváří obrázek ze znaků.
     * @param img   Předloha v stupních šedi
     * @param fw    FileWriter kam se budou zapisovat znaky
     * @throws IOException 
     */
    public static void createArt(BufferedImage img, FileWriter fw) throws IOException{
        // Obrázek se rozdělí na jednotlivé oblasti velké jako znak, pro ty se 
        // spočítá průměrná hodnota a podle ní se najde odpovídající znak
        int w,h,dx,dy;
        dx = CharBuilder.REF_WIDTH / RATIO;
        w = (int)Math.floor(img.getWidth() / dx);
        dy = CharBuilder.REF_HEIGHT / RATIO;
        h = (int)Math.floor(img.getHeight() / dy);
        int charCount=0, lineCount=0; // Info kolik se provede řádků a sloupců
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                int avgColor = CharBuilder.avgColor8bit(img, i*dx, j*dy, (i+1)*dx, (j+1)*dy);
                char aChar = cb.getChar(avgColor);
                fw.write(Character.toString(aChar));
                charCount++;
            }
            fw.write("\n");
            lineCount++;
        }
        System.out.println("lineCount = " + lineCount);
        System.out.println("charCount = " + charCount);
    }

}
