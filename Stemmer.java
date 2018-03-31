package Stemmer;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import stopWords.StopWords;


public class Stemmer {
    //important variables
//    private static final String INPUT_FILE="input.txt";
//    private static final String OUTPUT_FILE="output.txt";
    private static String INPUT_FILE = "input.txt";
    private static String OUTPUT_FILE = "output.txt";

    private ArrayList<String> words;

    private static int m = 0;
    private static String temp;
    //read dataset from file

    public Stemmer() {

    }

    public static void read(ArrayList arr) {
        FileReader fr = null;
        BufferedReader br = null;
        String line;
        StopWords stop = new StopWords();
        //initialize buffer and open file
        try {
            fr = new FileReader(INPUT_FILE);
            br = new BufferedReader(fr);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        //reading from file untill the end of file
        try {

            while ((line = br.readLine()) != null) {
                line = stop.remove(line);
                arr.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        }

    }

    //write final results to a file
    public static void write(ArrayList arr) {
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(OUTPUT_FILE);
            bw = new BufferedWriter(fw);
        } catch (IOException ex) {
            Logger.getLogger(Stemmer.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Object s : arr) {
            try {
                bw.write((String) s);
                bw.newLine();
            } catch (IOException ex) {
                Logger.getLogger(Stemmer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            //always close the buffered writer
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Stemmer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //check cvc pattern (*o)
    public static boolean cvc(String str) {
        int i = str.length()-1 ;

        while (!vowel(str, i)) {
            if (str.charAt(i) == 'W' || str.charAt(i) == 'X' || str.charAt(i) == 'Y') return false;
            i--;
            if (i < 0) return false;
        }
        while (vowel(str, i)) {
            i--;
            if (i < 0) return false;
        }
        while (!vowel(str, i)) {
            i--;
            if (i < 0) break;
        }
        return true;
    }

    //check if char is a vowel or not
    public static boolean vowel(String str, int i) {
        if (str.charAt(i) == 'A' || str.charAt(i) == 'E' || str.charAt(i) == 'O' || str.charAt(i) == 'U' || str.charAt(i) == 'I')
            return true;
        else if (i > 0 && str.charAt(i) == 'Y' && !(str.charAt(i-1) == 'A' || str.charAt(i-1) == 'E' || str.charAt(i-1) == 'O' || str.charAt(i-1) == 'U' || str.charAt(i-1) == 'I'))
            return true;
        return false;
    }

    //check if the string contatins a vowel or not
    public static boolean vowelIn(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (vowel(str, i)) return true;

        }
        return false;
    }
    //returns vowel pos
    public static int vowelPos(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (vowel(str, i)) return i;

        }
        return -1;
    }

    //this function returns the number of M which [c](vc){M}[v]
    public static int count(String str) {
        m = 0;
        for (int i = 0; i < str.length(); i++) {
            if ((i < str.length() - 1 && vowel(str, i)) || (i != 0 && str.charAt(i) == 'Y' && !vowel(str, i - 1))) {
                i++;
                while (i < str.length() && vowel(str, i)) {
                    if (str.charAt(i) == 'Y' && !vowel(str, i - 1))
                        break;
                    i++;
                }
                i--;
                m++;
            }

        }
        //  System.out.println(m);
        return m;
    }

    //this step do the following
// SSES -> SS
// IES -> I
// SS -> S
// S ->
    public static String step1A(String str)

    {
        StringBuilder builder = new StringBuilder(str);
        int len = str.length();

        if (len > 4 && str.charAt(len - 1) == 'S' && str.charAt(len - 2) == 'E' && str.charAt(len - 3) == 'S' && str.charAt(len - 4) == 'S')
            builder.replace(builder.length() - 4, builder.length(), "SS");
        else if (len > 3 && str.charAt(len - 1) == 'S' && str.charAt(len - 2) == 'E' && str.charAt(len - 3) == 'I')
            builder.replace(builder.length() - 3, builder.length(), "I");
        else if (len > 2 && str.charAt(len - 1) == 'S' && str.charAt(len - 2) == 'S')
            builder.replace(builder.length() - 2, builder.length(), "SS");
        else if (len > 0 && str.charAt(len - 1) == 'S')
            builder.replace(builder.length() - 1, builder.length(), "");

        temp = builder.toString();
        return temp;
    }

    //this step do the following
    //AT ->ATE
    //BL -> BLE
    //IZ -> IZE
    //(*d) ->SINGLE LETTER
    //(m=1 & *o) ->E
    public static String step12B(String str) {
        StringBuilder builder = new StringBuilder(str);
        int len = str.length();

        if (len > 2 && str.charAt(len - 1) == 'T' && str.charAt(len - 2) == 'A')
            builder.replace(builder.length() - 2, builder.length(), "ATE");  //check this line (i add 3 letters instead of 2 )
        else if (len > 2 && str.charAt(len - 1) == 'L' && str.charAt(len - 2) == 'B')
            builder.replace(builder.length() - 2, builder.length(), "BLE");
        else if (len > 2 && str.charAt(len - 1) == 'Z' && str.charAt(len - 2) == 'I')
            builder.replace(builder.length() - 2, builder.length(), "IZE");
        else if (len > 2 && str.charAt(len - 1) == str.charAt(len - 2) && str.charAt(len - 1) != 'L' && str.charAt(len - 1) != 'S' && str.charAt(len - 1) != 'Z')
            builder.replace(builder.length() - 2, builder.length(), String.valueOf(str.charAt(len - 1)));
       /* modfication on porter algorithm*/
        else if (count(str) == 1 && cvc(str) && !(str.charAt(len - 1) == 'L' || str.charAt(len - 1) == 'S' || str.charAt(len - 1) == 'Z'))
            builder.append('E');
        temp = builder.toString();
        return temp;
    }


    //this step do the following
    //(m>0)EED -> EE
    //(*v*) ED ->
    //(*v*) ING ->
    public static String step1B(String str) {
        StringBuilder builder = new StringBuilder(str);
        int len = str.length();
        String s1 = "", s2 = "";
        if (len > 3)
            s1 = new String(str.substring(0, len - 3));
        if (len > 2)
            s2 = new String(str.substring(0, len - 2));
        boolean flag = false;
        if (len > 3 && str.charAt(len - 1) == 'D' && str.charAt(len - 2) == 'E' && str.charAt(len - 3) == 'E' && count(s1) > 0)
            builder.replace(builder.length() - 3, builder.length(), "EE");
        else if (len > 3 && str.charAt(len - 1) == 'G' && str.charAt(len - 2) == 'N' && str.charAt(len - 3) == 'I' && vowelIn(s1)) {
            builder.replace(builder.length() - 3, builder.length(), "");
            flag = true; //to remove another letters
        } else if (len > 2 && str.charAt(len - 1) == 'D' && str.charAt(len - 2) == 'E' && str.charAt(len - 3) != 'E' && vowelIn(s2)) {
            builder.replace(builder.length() - 2, builder.length(), "");
            flag = true;
        }

        temp = builder.toString();
        if (flag) {
            temp = step12B(temp);
        }
        return temp;
    }

    // this step do the following :
    //(*v*)Y ->I
    public static String step1C(String str) {
        StringBuilder builder = new StringBuilder(str);
        int len = str.length();
        String s1 = "";
        if (len > 2)
            s1 = new String(str.substring(0, len - 1));
        /*This has been modified from the original Porter algorithm so
            that y->i is only done when y is preceded by a consonant,
            but not if the stem is only a single consonant, i.e.

               (*c and not c) Y -> I

            So 'happy' -> 'happi', but
               'enjoy' -> 'enjoy'  etc

            This is a much better rule. Formerly 'enjoy'->'enjoi' and
            'enjoyment'->'enjoy'. Step 1c is perhaps done too soon; but
            with this modification that no longer really matters.

            Also, the removal of the contains_vowel(z) condition means
            that 'spy', 'fly', 'try' ... stem to 'spi', 'fli', 'tri' and
            conflate with 'spied', 'tried', 'flies' ...*/
        else if (len > 1 && str.charAt(len - 1) == 'Y' && vowelIn(s1) )
        {
            if(!vowel(str,str.length()-2))
            builder.replace(builder.length() - 1, builder.length(), "I");
        }
        temp = builder.toString();
        return temp;
    }

    //this step deals with the subsequents
    public static String step2(String str) {
        StringBuilder builder = new StringBuilder(str);
        int len = str.length();
        String s1 = "", s2 = "", s3 = "", s4 = "", s5 = "";
        if (len > 3)
            s1 = new String(str.substring(0, len - 3));
        if (len > 4)
            s2 = new String(str.substring(0, len - 4));
        if (len > 5)
            s3 = new String(str.substring(0, len - 5));
        if (len > 6)
            s4 = new String(str.substring(0, len - 6));
        if (len > 7)
            s5 = new String(str.substring(0, len - 7));
        if (len > 3 && count(s1) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'L' && str.charAt(len - 3) == 'E')
            builder.replace(builder.length() - 3, builder.length(), "E");

        else if (len > 4 && count(s2) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'C' && str.charAt(len - 3) == 'N' && str.charAt(len - 4) == 'E')
            builder.replace(builder.length() - 4, builder.length(), "ENCE");
        else if (len > 4 && count(s2) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'C' && str.charAt(len - 3) == 'N' && str.charAt(len - 4) == 'A')
            builder.replace(builder.length() - 4, builder.length(), "ANCE");
        else if (len > 4 && count(s2) > 0 && str.charAt(len - 1) == 'R' && str.charAt(len - 2) == 'E' && str.charAt(len - 3) == 'Z' && str.charAt(len - 4) == 'I')
            builder.replace(builder.length() - 4, builder.length(), "IZE");
        else if (len > 4 && count(s2) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'L' && str.charAt(len - 3) == 'B' && str.charAt(len - 4) == 'A')
            builder.replace(builder.length() - 4, builder.length(), "ABLE");
        else if (len > 4 && count(s2) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'L' && str.charAt(len - 3) == 'L' && str.charAt(len - 4) == 'A')
            builder.replace(builder.length() - 4, builder.length(), "AL");
        else if (len > 4 && count(s2) > 0 && str.charAt(len - 1) == 'R' && str.charAt(len - 2) == 'O' && str.charAt(len - 3) == 'T' && str.charAt(len - 4) == 'A')
            builder.replace(builder.length() - 4, builder.length(), "ATE");

        else if (len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'L' && str.charAt(len - 3) == 'T' && str.charAt(len - 4) == 'N' && str.charAt(len - 5) == 'E')
            builder.replace(builder.length() - 5, builder.length(), "ENT");
        else if (len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'L' && str.charAt(len - 3) == 'S' && str.charAt(len - 4) == 'U' && str.charAt(len - 5) == 'O')
            builder.replace(builder.length() - 5, builder.length(), "OUS");
        else if (len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'N' && str.charAt(len - 2) == 'O' && str.charAt(len - 3) == 'I' && str.charAt(len - 4) == 'T' && str.charAt(len - 5) == 'A' && str.charAt(len - 6) != 'Z')
            builder.replace(builder.length() - 5, builder.length(), "ATE");
        else if (len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'M' && str.charAt(len - 2) == 'S' && str.charAt(len - 3) == 'I' && str.charAt(len - 4) == 'L' && str.charAt(len - 5) == 'A')
            builder.replace(builder.length() - 5, builder.length(), "AL");
        else if (len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'T' && str.charAt(len - 3) == 'I' && str.charAt(len - 4) == 'L' && str.charAt(len - 5) == 'A')
            builder.replace(builder.length() - 5, builder.length(), "AL");
        else if (len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'T' && str.charAt(len - 3) == 'I' && str.charAt(len - 4) == 'V' && str.charAt(len - 5) == 'I')
            builder.replace(builder.length() - 5, builder.length(), "IVE");


        else if (len > 6 && count(s4) > 0 && str.charAt(len - 1) == 'L' && str.charAt(len - 2) == 'A' && str.charAt(len - 3) == 'N' && str.charAt(len - 4) == 'O' && str.charAt(len - 5) == 'I' && str.charAt(len - 6) == 'T' && str.charAt(len - 7) != 'A')
            builder.replace(builder.length() - 6, builder.length(), "TION");
        else if (len > 6 && count(s4) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'T' && str.charAt(len - 3) == 'I' && str.charAt(len - 4) == 'L' && str.charAt(len - 5) == 'I' && str.charAt(len - 6) == 'B')
            builder.replace(builder.length() - 6, builder.length(), "BLE");

        else if (len > 7 && count(s5) > 0 && str.charAt(len - 1) == 'L' && str.charAt(len - 2) == 'A' && str.charAt(len - 3) == 'N' && str.charAt(len - 4) == 'O' && str.charAt(len - 5) == 'I' && str.charAt(len - 6) == 'T' && str.charAt(len - 7) == 'A')
            builder.replace(builder.length() - 7, builder.length(), "ATE");
        else if (len > 7 && count(s5) > 0 && str.charAt(len - 1) == 'N' && str.charAt(len - 2) == 'O' && str.charAt(len - 3) == 'I' && str.charAt(len - 4) == 'T' && str.charAt(len - 5) == 'A' && str.charAt(len - 6) == 'Z' && str.charAt(len - 7) == 'I')
            builder.replace(builder.length() - 7, builder.length(), "IZE");
        else if (len > 7 && count(s5) > 0 && str.charAt(len - 1) == 'S' && str.charAt(len - 2) == 'S' && str.charAt(len - 3) == 'E' && str.charAt(len - 4) == 'N' && str.charAt(len - 5) == 'E' && str.charAt(len - 6) == 'V' && str.charAt(len - 7) == 'I')
            builder.replace(builder.length() - 7, builder.length(), "IVE");
        else if (len > 7 && count(s5) > 0 && str.charAt(len - 1) == 'S' && str.charAt(len - 2) == 'S' && str.charAt(len - 3) == 'E' && str.charAt(len - 4) == 'N' && str.charAt(len - 5) == 'L' && str.charAt(len - 6) == 'U' && str.charAt(len - 7) == 'F')
            builder.replace(builder.length() - 7, builder.length(), "FUL");
        else if (len > 7 && count(s5) > 0 && str.charAt(len - 1) == 'S' && str.charAt(len - 2) == 'S' && str.charAt(len - 3) == 'E' && str.charAt(len - 4) == 'N' && str.charAt(len - 5) == 'S' && str.charAt(len - 6) == 'U' && str.charAt(len - 7) == 'O')
            builder.replace(builder.length() - 7, builder.length(), "OUS");


        temp = builder.toString();

        return temp;
    }

    //THIS STEP APPLIES THE SAME TECHNIQUE AS STEP2
    public static String step3(String str) {
        StringBuilder builder = new StringBuilder(str);
        int len = str.length();
        String s1 = "", s2 = "", s3 = "";
        if (len > 3)
            s1 = new String(str.substring(0, len - 3));
        if (len > 4)
            s2 = new String(str.substring(0, len - 4));
        if (len > 5)
            s3 = new String(str.substring(0, len - 5));

        if (len > 3 && count(s1) > 0 && len > 3 & str.charAt(len - 1) == 'L' && str.charAt(len - 2) == 'U' && str.charAt(len - 3) == 'F')
            builder.replace(builder.length() - 3, builder.length(), "");

        else if (len > 4 && count(s2) > 0 && str.charAt(len - 1) == 'L' && str.charAt(len - 2) == 'A' && str.charAt(len - 3) == 'C' && str.charAt(len - 4) == 'I')
            builder.replace(builder.length() - 4, builder.length(), "IC");
        else if (len > 4 && count(s2) > 0 && str.charAt(len - 1) == 'S' && str.charAt(len - 2) == 'S' && str.charAt(len - 3) == 'E' && str.charAt(len - 4) == 'N')
            builder.replace(builder.length() - 4, builder.length(), "");

        else if (len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'T' && str.charAt(len - 3) == 'A' && str.charAt(len - 4) == 'C' && str.charAt(len - 5) == 'I')
            builder.replace(builder.length() - 5, builder.length(), "IC");
        else if (len > 5 && len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'V' && str.charAt(len - 3) == 'I' && str.charAt(len - 4) == 'T' && str.charAt(len - 5) == 'A')
            builder.replace(builder.length() - 5, builder.length(), "");
        else if (len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'Z' && str.charAt(len - 3) == 'I' && str.charAt(len - 4) == 'L' && str.charAt(len - 5) == 'A')
            builder.replace(builder.length() - 5, builder.length(), "AL");
        else if (len > 5 && count(s3) > 0 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'T' && str.charAt(len - 3) == 'I' && str.charAt(len - 4) == 'C' && str.charAt(len - 5) == 'I')
            builder.replace(builder.length() - 5, builder.length(), "IC");


        temp = builder.toString();
        return temp;
    }

    // THIS STEP REMOVES SUFFX
    public static String step4(String str) {
        StringBuilder builder = new StringBuilder(str);
        int len = str.length();
        String s1 = "", s2 = "", s3 = "", s4 = "";
        if (len > 3)
            s1 = new String(str.substring(0, len - 3));
        if (len > 4)
            s2 = new String(str.substring(0, len - 4));
        if (len > 5)
            s3 = new String(str.substring(0, len - 5));
        if (len > 2)
            s4 = new String(str.substring(0, len - 2));

        if (len > 3 && count(s1) > 1 && str.charAt(len - 1) == 'T' && str.charAt(len - 2) == 'N' && str.charAt(len - 3) == 'A')
            builder.replace(builder.length() - 3, builder.length(), "");
        else if (len > 3 && count(s1) > 1 && str.charAt(len - 1) == 'T' && str.charAt(len - 2) == 'N' && str.charAt(len - 3) == 'E' && str.charAt(len - 4) != 'M')
            builder.replace(builder.length() - 3, builder.length(), "");
        else if (len > 3 && count(s1) > 1 && str.charAt(len - 1) == 'M' && str.charAt(len - 2) == 'S' && str.charAt(len - 3) == 'I')
            builder.replace(builder.length() - 3, builder.length(), "");
        else if (len > 3 && count(s1) > 1 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'T' && str.charAt(len - 3) == 'A')
            builder.replace(builder.length() - 3, builder.length(), "");
        else if (len > 3 && count(s1) > 1 && str.charAt(len - 1) == 'I' && str.charAt(len - 2) == 'T' && str.charAt(len - 3) == 'I')
            builder.replace(builder.length() - 3, builder.length(), "");
        else if (len > 3 && count(s1) > 1 && str.charAt(len - 1) == 'S' && str.charAt(len - 2) == 'U' && str.charAt(len - 3) == 'O')
            builder.replace(builder.length() - 3, builder.length(), "");
        else if (len > 3 && count(s1) > 1 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'V' && str.charAt(len - 3) == 'I')
            builder.replace(builder.length() - 3, builder.length(), "");
        else if (len > 3 && count(s1) > 1 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'Z' && str.charAt(len - 3) == 'I')
            builder.replace(builder.length() - 3, builder.length(), "");
        else if (len > 3 && count(s1) > 1 && (str.charAt(len - 4) == 'T' || str.charAt(len - 4) == 'S') && str.charAt(len - 1) == 'N' && str.charAt(len - 2) == 'O' && str.charAt(len - 3) == 'I')
            builder.replace(builder.length() - 3, builder.length(), "");

        else if (len > 4 && count(s2) > 1 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'C' && str.charAt(len - 3) == 'N' && str.charAt(len - 4) == 'A')
            builder.replace(builder.length() - 4, builder.length(), "");
        else if (len > 4 && count(s2) > 1 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'C' && str.charAt(len - 3) == 'N' && str.charAt(len - 4) == 'E')
            builder.replace(builder.length() - 4, builder.length(), "");
        else if (len > 4 && count(s2) > 1 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'L' && str.charAt(len - 3) == 'B' && str.charAt(len - 4) == 'A')
            builder.replace(builder.length() - 4, builder.length(), "");
        else if (len > 4 && count(s2) > 1 && str.charAt(len - 1) == 'E' && str.charAt(len - 2) == 'L' && str.charAt(len - 3) == 'B' && str.charAt(len - 4) == 'I')
            builder.replace(builder.length() - 4, builder.length(), "");
        else if (len > 4 && (count(s2) > 1 && str.charAt(len - 1) == 'T' && str.charAt(len - 2) == 'N' && str.charAt(len - 3) == 'E' && str.charAt(len - 4) == 'M' && str.charAt(len - 5) != 'E'))
            builder.replace(builder.length() - 4, builder.length(), "");

        else if (len > 5 && count(s3) > 1 && str.charAt(len - 1) == 'T' && str.charAt(len - 2) == 'N' && str.charAt(len - 3) == 'E' && str.charAt(len - 4) == 'M' && str.charAt(len - 5) == 'E')
            builder.replace(builder.length() - 5, builder.length(), "");

        else if (len > 2 && count(s4) > 1 && str.charAt(len - 1) == 'L' && str.charAt(len - 2) == 'A')
            builder.replace(builder.length() - 2, builder.length(), "");
        else if (len > 2 && count(s4) > 1 && str.charAt(len - 1) == 'R' && str.charAt(len - 2) == 'E')
            builder.replace(builder.length() - 2, builder.length(), "");
        else if (len > 2 && count(s4) > 1 && str.charAt(len - 1) == 'C' && str.charAt(len - 2) == 'I')
            builder.replace(builder.length() - 2, builder.length(), "");
        else if (len > 2 && count(s4) > 1 && str.charAt(len - 1) == 'U' && str.charAt(len - 2) == 'O')
            builder.replace(builder.length() - 2, builder.length(), "");


        temp = builder.toString();
        return temp;
    }

    public static String step5A(String str) {
        StringBuilder builder = new StringBuilder(str);
        int len = str.length();
        String s1;
        if (len > 1) {
            s1 = new String(str.substring(0, len - 1));
            if (count(s1) > 1) {
                if (str.charAt(len - 1) == 'E')
                    builder.replace(builder.length() - 1, builder.length(), "");


            } else if (count(s1) == 1) {
                if (!cvc(s1) && str.charAt(len - 1) == 'E')
                    builder.replace(builder.length() - 1, builder.length(), "");
            }
        }
        temp = builder.toString();
        return temp;
    }

    public static String step5B(String str) {
        StringBuilder builder = new StringBuilder(str);
        int len = str.length();
        String s1;
        if (len > 1) {
            s1 = new String(str.substring(0, len - 1));
            if (count(s1) > 1) {
                if (str.charAt(len - 1) == 'L' && str.charAt(len - 2) == 'L')
                    builder.replace(builder.length() - 2, builder.length(), "L");


            }
        }
        temp = builder.toString();
        return temp;
    }
    //this function takes input string 
    //removes stop words then do stemming 
    //it returns array of string
    public static ArrayList<String> stem(String word) {
        ArrayList<String> words = new ArrayList<String>();
        ArrayList<String> results = new ArrayList<String>();
        String result = "";
        StopWords stop = new StopWords();
        
        //remove special characters
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(word);
        word = matcher.replaceAll("");
        
        word=word.toUpperCase();
        word = stop.remove(word);
        String[] splited = word.split("\\s+");
        for (int i = 0; i < splited.length; i++) {

            words.add(splited[i]);
        }
        for (int i = 0; i < words.size(); i++) {
            result = step1A(words.get(i));
            result = step1B(result);
            result = step1C(result);
            result = step2(result);
            result = step3(result);
            result = step4(result);
            result = step5A(result);
            result = step5B(result);
            result=result.toLowerCase();
            results.add(result);
        }
        return results;

    }
    //this function do stemming only 
    //it takes string variable and returen string 
    //if the string has only word then the results array will have only one stemmed word 
    //else it will has the length of the input string and the returned string  has the same length
    public static String stem2(String word) {
        ArrayList<String> words = new ArrayList<String>();
        ArrayList<String> results = new ArrayList<String>();
        String result = "";
        word=word.toUpperCase();
        StringBuilder builder = new StringBuilder(result);
        String[] splited = word.split("\\s+");
        for (int i = 0; i < splited.length; i++) {
            words.add(splited[i]);
        }
        for (int i = 0; i < words.size(); i++) {
            result = step1A(words.get(i));
            result = step1B(result);
            result = step1C(result);
            result = step2(result);
            result = step3(result);
            result = step4(result);
            result = step5A(result);
            result = step5B(result);
            result=result.toLowerCase();
            results.add(result);
        }
        for(int i=0;i<results.size();i++)
        {
            builder.append(results.get(i));
        }
        return builder.toString();
    }

}


