package com.kgzr.rhetid;

import com.kgzr.rhetid.pos.util.BankLoader;

/**
 * Created by Kevin and Zack on 11/30/14.
 * The Main file for the RhetID Application. This is used to incorporate the logic of all packages.
 */
public class RhetIdApp {

    // Just to keep logic out the the main function, keep it clean.
    public static void runApp(String[] args){
        //First load the part of speech tree.
        BankLoader.readFiles();

        System.out.println(BankLoader.getObservationProbs().toString());
    }

    public static void main(String[] args) {
        runApp(args);
    }
}
