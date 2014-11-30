package com.kgzr.rhetid;

import com.kgzr.rhetid.pos.util.BankLoader;
import com.kgzr.rhetid.util.StopWords;

/**
 * Created by Kevin and Zach on 11/30/14.
 * The Main file for the RhetID Application. This is used to incorporate the logic of all packages.
 */
public class RhetIdApp {
    public static final boolean DEBUG = true;
    /**
     * Load the static assets.
     * Current files includes:
     * <ul>
     *     <li>POS Tagger tree bank</li>
     *     <li>StopWords lookup map</li>
     * </ul>
     */
    public static void loadStaticAssets(){
        BankLoader.readFiles();
        if (RhetIdApp.DEBUG){ System.out.println("Loaded TreeBank"); }

        StopWords.loadStopWords();
        if (RhetIdApp.DEBUG) {
            System.out.println("Loaded StopWords.");
            System.out.println("StopWords: " + StopWords.getStopWords().toString());
        }
    }

    /**
     * runApp put here to keep the main function clean for development.
     * Will be moved later.
     * @param args main functions args array.
     */
    public static void runApp(String[] args){
        //First load the part of speech tree.
        loadStaticAssets();
    }

    public static void main(String[] args) {
        runApp(args);
    }
}
