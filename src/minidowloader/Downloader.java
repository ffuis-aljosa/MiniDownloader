package minidowloader;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JOptionPane;

/**
 *
 * Klasa koja se bavi preuzimanjem datoteke putem Neta.
 * 
 * @author      Sljuka  <a.sljuka@ffuis.edu.ba>
 * @version     0.1
 * @since       2014-03-17
 * 
 */
public class Downloader {
    private String urlText;
    private Component rootPane;
    
    /**
     * Jedini konstruktor klase. Inicijalizuje arrgumente.
     *
     * @param  urlText   tekst koji predstavlja URL datoteke koju želimo da preuzmemo
     * @param  rootPane  Komponenta (prozor) koja je odgovorna za prikazivanje MessageDialog-a
     */
    public Downloader(String urlText, Component rootPane) {
        this.urlText = urlText;
        this.rootPane = rootPane;
    }
    
    /**
     * Metoda koja obavlja sav download. Provjerava ispravnost URL-a, pokušava
     * da pristupi resursu na URL-u, koristi bafer da prekopira resurs na računar.
     * Prijavljuje sve greške pozivima na JOptionPane.showMessageDialog
     * 
     * @returns void
     */
    public void download() {
        if (urlText == null || urlText.equals("")) {
            JOptionPane
                .showMessageDialog(rootPane, "Niste unijeli URL!", "Upozorenje", JOptionPane.WARNING_MESSAGE);
            
            return;
        }
        
        URL url;
        
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
            in = new BufferedInputStream(url.openStream());
            
            // Metodu extractFileName smo mi napisali ispod
            fout = new FileOutputStream(extractFileName(url.getFile()));
            
            byte buffer[] = new byte[1024];
            
            int downloaded;
            
            while ((downloaded = in.read(buffer)) != -1) {
                fout.write(buffer, 0, downloaded);
            }
            
            JOptionPane
                .showMessageDialog(rootPane, "Uspješno preuzeta datoteka", "Info", JOptionPane.INFORMATION_MESSAGE);
        } 
        catch (Exception e) {
            JOptionPane
                .showMessageDialog(rootPane, e.getMessage(), "Upozorenje", JOptionPane.ERROR_MESSAGE);
        }
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
     * Za URL = "www.moj-sajt-com/put/do/slike/slika.jpg"
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
