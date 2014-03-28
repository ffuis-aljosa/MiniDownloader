package minidowloader;

import java.awt.Component;
import java.awt.Dialog;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        
        InputStream in = null;
        ByteArrayOutputStream out = null;
        
        try {            
            // Otvorimo konekciju prema URL-u
            // Izbacuje grešku ako je resurs nedostupan (npr. nema konekcije na Internet)
            URLConnection conn = url.openConnection();
            
            // Otvorimo stream za čitanje
            in = new BufferedInputStream(conn.getInputStream());
            
            // Ovaj tip output stream-a piše direktno u RAM (u jedan niz bajtova)
            // Mi ćemo našu datoteku preuzeti prvo u RAM, pa onda sačuvati u
            // fajl na našem sistemu
            out = new ByteArrayOutputStream();
            
            // Bafer koji ćemo puniti podacima sa inputStream-a i iz kog ćemo
            // kopirati podatke u outputStream
            byte buffer[] = new byte[1024];
            
            // Ukupna veličina datoteke
            int totalSize = conn.getContentLength();
            // Koliko smo do sad preuzeli
            int totalDownloaded = 0;
            int downloaded;
            
            // Osiguravamo da je progressBar postavljen na 0
            progress.setValue(0);
            
            // in.read(buffer) čita maksimalan mogući broj bajtova sa inputStream-a
            // u bafer.
            // Broj pročitanih bajtova se spašava u downloaded.
            // downloaded će biti -1 ukoliko više nema šta da se pročita.
            while ((downloaded = in.read(buffer)) != -1) {
                // Na outputStream ispisujemo iz bafera
                // od počekta do broja pročitanih bajtova
                out.write(buffer, 0, downloaded);
                
                // Progres prikazujemo na progressBar-u
                totalDownloaded += downloaded;
                progress.setValue((int)((double)totalDownloaded / totalSize * 100));
            }
            
            // Kada je preuzimanje gotovo, korisniku se nudi opcija da sačuva
            // datoteku na svoj sistem
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            
            // Uzmemo originalni naziv datoteke
            String filename = extractFileName(urlText);
            // I njegovu ekstenziju
            String extension = extractExtension(filename);
            
            // Predložimo korisniku originalni naziv
            chooser.setSelectedFile(new File(filename));
            int result = chooser.showDialog(rootPane, "OK");
            
            // Ukoliko je korisnik kliknuo "OK"...
            if (result == JFileChooser.APPROVE_OPTION) {
                // Uzmemo datoteku u koju korisnik hoće da sačuva
                File fileToSave = chooser.getSelectedFile();
                
                // Ne damo korisniku da sačuva datoteku pod drugom ekstenzijom
                // Ukoliko je sačuvao pod drugom ekstenzijom, na nju dodamo originalnu
                if (!extractExtension(fileToSave.getName()).equals(extension))
                    fileToSave = new File(fileToSave.getPath() + "." + extension);
                
                // Ispišemo iz našeg niza bajtova u datoteku
                out.writeTo(new FileOutputStream(fileToSave));
            }
            
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
                
                if (out != null)
                    out.close();
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
    
    /**
     * Izvlači ekstenziju iz naziva datoteke.
     * <p>
     * Primjer:
     * Za naziv datoteke "slika.jpg"
     * rezultat je "jpg"
     * 
     * @param url   Naziv datoteke iz kojeg treba da se izvuče ekstenzija
     * @return Podstring proslijeđenog stringa od poslijednjeg ponavljanja '.' do kraja. Ukoliko tačka ne postoji, vraća se prazan string.
     */
    private String extractExtension(String filename) {
        int lastIndexOfDot = filename.lastIndexOf('.');
        
        if (lastIndexOfDot < 0)
            return "";
        
        else
            return filename.substring(lastIndexOfDot + 1);
    }
}
