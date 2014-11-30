package com.kgzr.rhetid.pos.tokenizer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EDITED: For the sake of Zipf's law
 * TODO: Make this work for Paragraph counts. Reliable to track just the new lines?
  This program is a very simple string tokenizer.
  It recognizes two types of token, a string of digits and
  a string of letters.

  It reads from either a file specified on the command line or,
  if no file name is present, from System.in.
  
  @author: John Donaldson
*/
public class Tokenizer {
    private boolean zipf;

    public Tokenizer(){
        this.zipf=false;
    }

    public Tokenizer(boolean zipf){
        this.zipf=zipf;
    }
   
   public List<String> tokenize(String rawInput){
      // Write a regular expression for each token type
      String ordinal = "(\\d+(?:st|nd|rd|th))";// ordinal number e.g 1st
      String decimal = "([\\+\\-]?\\d+\\.\\d+|[\\+\\-]?\\d+(?:,\\d+)+)"; // decimal numbers and negative numbers and numbers with comma (ignore 1,000.0 form here)
      String number = "(\\d+)"; // int numbers
      String dollar = "(\\$\\d+(?:\\.\\d\\d)?)";
      
      String title =  "(?:(Dr\\.|Mr\\.|Mrs\\.|Sr\\.|Ms\\.|Jr\\.|Mx\\.|Fr\\.|Prof\\.|St\\.|U\\.S\\.)\\s)";// titles list and Abbreviations (with period at the end) list. It's a finite list but not comprehensive in this case. e.g. U.S. Dr.
      String contractionsOrPossessives = "([a-zA-Z]+)(n't|'s|'ve|'d|'ll|'m|'re)"; // Contractions and Possessives e.g. mom's>> [mom, 's]    I've >> [I, 've]
      String word = "([a-zA-Z']+(?:-[a-zA-Z']+)*)"; // words
      
      String specialPunct= "(--)"; // special case when -- should be tokenized to one -- instead of two -
      String punctuation = "(\\p{Punct})"; // e.g. !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~

      // Combine them into a single string
      String r;
      if(!zipf) {
          r = ordinal + "|" + dollar + "|" + decimal + "|" + number + "|" + title + "|" + contractionsOrPossessives + "|" + word + "|" + specialPunct + "|" + punctuation;
      }
      else {
          r = ordinal + "|" + dollar + "|" + decimal + "|" + number + "|" + title + "|" + contractionsOrPossessives + "|" + word;
      }

      // Compile it to a Pattern
      Pattern p = Pattern.compile(r);
      
      // Create a list for the tokens
      List<String> tokenList = new ArrayList<String>();
      
      // Use a Matcher to loop through the input
      Matcher m = p.matcher(rawInput);
      
      int groupCount =  m.groupCount();
      
      while(m.find()){     
         for(int k = 1; k <= groupCount; k++){
            if(m.group(k)!=null){ // modified sample code from m.group(1)!=null
               //System.out.println("Group " + k +": " + m.group(k)); // test purpose
               tokenList.add(m.group(k));
            }
         }
      }
      return tokenList;
      
   }
   
   public static void main(String[] args) throws IOException {
      InputStreamReader reader;
      // Get the file name from the command line; if not present,
      // read from System.in.
      if(args.length>0){
         reader = new FileReader(new File(args[0]));
      }
      else {
         reader = new InputStreamReader(System.in);
      }
      // Create a StringBuffer containing the entire contents
      // of the file
      StringBuffer input = new StringBuffer(50000);
      int x = reader.read();
      while(x!=-1){
         input.append((char)x);
         x = reader.read();
      }
      
      Tokenizer tokenizer = new Tokenizer();
      List<String> tokenList = tokenizer.tokenize(new String(input));
      
      // Print the list of tokens
      System.out.println(tokenList);
   }
}
