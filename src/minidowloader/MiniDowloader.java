/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minidowloader;

import javax.swing.JFrame;

/**
 *
 * @author Aljoša Šljuka
 */
public class MiniDowloader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        NasProzor np = new NasProzor();
        np.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        np.setVisible(true);
        np.setResizable(false);
    }
}
