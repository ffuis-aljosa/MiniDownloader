package minidowloader;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * Klasa koja se bavi preuzimanjem datoteke putem Neta.
 * 
 * @author      Aljoša Šljuka  <a.sljuka@ffuis.edu.ba>
 * @version     0.1
 * @since       2014-03-17
 * 
 */
public class Downloader {
    private String urlText;
    private Component rootPane;
    private JProgressBar progress;
    
    /**
     * Jedini konstruktor klase. Inicijalizuje arrgumente.
     *
     * @param  urlText   tekst koji predstavlja URL datoteke koju želimo da preuzmemo
     * @param  rootPane  Komponenta (prozor) koja je odgovorna za prikazivanje MessageDialog-a
     */
    public Downloader(String urlText, Component rootPane, JProgressBar progress) {
        this.urlText = urlText;
        this.rootPane = rootPane;
        this.progress = progress;
    }
    
    /**
     * Metoda koja obavlja sav download. Provjerava ispravnost URL-a, pokušava
     * da pristupi resursu na URL-u, koristi bafer da prekopira resurs na računar.
     * Prijavljuje sve greške pozivima na JOptionPane.showMessageDialog
     * 
     * @returns void
     */
    public void download() {
        // Ukoliko je proslijeđen null ili prazan URL, prijaviti grešku
        // i izaći
        if (urlText == null || urlText.equals("")) {
            JOptionPane
                .showMessageDialog(rootPane, "Niste unijeli URL!", "Upozorenje", JOptionPane.WARNING_MESSAGE);
            
            return;
        }
        
        URL url;
        
        // Pokušamo napraviti objekat klase URL od proslijeđenog teksta
        // Ukoliko proslijeđeni tekst nije valida URL, prijavljujemo grešku
        // i izlazimo
        try {
            url = new URL(urlText);
        } 
        catch (MalformedURLException ex) {
            JOptionPane
                .showMessageDialog(rootPane, "Uneseni URL nije validan!", "Upozorenje", JOptionPane.ERROR_MESSAGE);
            
            return;
        }
        
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        
        try {
            JFileChooser chooser = new JFileChooser();
            
            URLConnection conn = url.openConnection();
            
            // Pokušamo se "zakačiti" na URL
            in = new BufferedInputStream(conn.getInputStream());
            
            // Izvučemo naziv datoteke iz URL-a i pokušamo
            // napraviti datoteku na našem sistemu sa tim imenom.
            // Po default-u će se datoteka napraviti u direktorijumu gdje
            // je program.
            // Metodu extractFileName smo mi napisali ispod
            
            
            int result = chooser.showDialog(rootPane, "OK");
            
            if (result == JFileChooser.APPROVE_OPTION) {
                fout = new FileOutputStream(chooser.getName(chooser.getSelectedFile()));
            }
            
            // Bafer koji ćemo puniti podacima sa inputStream-a i iz kog ćemo
            // kopirati podatke u outputStream
            byte buffer[] = new byte[1024];
            
            int totalSize = conn.getContentLength();
            int totalDownloaded = 0;
            int downloaded;
            
            // in.read(buffer) čita maksimalan mogući broj bajtova sa inputStream-a
            // u bafer.
            // Broj pročitanih bajtova se spašava u downloaded.
            // downloaded će biti -1 ukoliko više nema šta da se pročita.
            while ((downloaded = in.read(buffer)) != -1) {
                // Na outputStream ispisujemo iz bafera
                // od počekta do broja pročitanih bajtova
                fout.write(buffer, 0, downloaded);
                
                totalDownloaded += downloaded;
                
                progress.setValue((int)((double)totalDownloaded / totalSize * 100));
            }
            
            progress.setValue(0);
            
            // Ukoliko smo došli do ovde sve je prošlo kako treba (nadamo se)
            JOptionPane
                .showMessageDialog(rootPane, "Uspješno preuzeta datoteka", "Info", JOptionPane.INFORMATION_MESSAGE);
        } 
        catch (IOException e) {
            JOptionPane
                .showMessageDialog(rootPane, e.toString(), "Upozorenje", JOptionPane.ERROR_MESSAGE);
        }
        // finally blok se uvijek izvršava, bez obzira da li je try prošao
        // bez grešaka ili je greška uhvaćena u catch.
        // To možemo iskoristiti za operacije "čišćenja", kao recimo
        // zatvaranje stream-ova
        finally {
            try {
                if (in != null)
                    in.close();
                
                if (fout != null)
                    fout.close();
            } 
            catch (IOException ex) {
                JOptionPane
                    .showMessageDialog(rootPane, ex.getMessage(), "Upozorenje", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Izvlači naziv datoteke iz URL-a.
     * <p>
     * Primjer:
     * Za URL = "www.moj-sajt.com/put/do/slike/slika.jpg"
     * rezultat je "slika.jpg"
     * 
     * @param url   Čitav URL iz kojeg treba da se izvuče naziv datoteke.
     * @return Podstring proslijeđenog stringa od poslijednjeg ponavljanja '/' do kraja
     */
    private String extractFileName(String url) {
        String result = url;
        
        // Nađemo zadnje pojavljivanje karaktera '/' u URL-u
        int lastIndexOfSlash = url.lastIndexOf('/');
        
        // Ukoliko nije -1 (to znači da '/' ne postoji u string-u)
        // odsječemo sve lijevo
        if (lastIndexOfSlash > -1) {
            result = result.substring(lastIndexOfSlash + 1);
        }
        
        return result;
    }
}
