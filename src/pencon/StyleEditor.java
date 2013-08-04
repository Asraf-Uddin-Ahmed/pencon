/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import javax.swing.JTextPane;
import javax.swing.text.*;
import java.awt.Color;

/**
 *
 * @author RATUL
 */
public class StyleEditor {
    StyledDocument sd = null;
    Style s = null;
    String start[] = {"#", "/*", "//", "\"", "'", "import"};
    String stop[] = {"\n", "*/", "\n", "\"", "'", "\n"};
    Color colorsLine[] = {Color.green, Color.gray, Color.gray, Color.orange, Color.orange, Color.green};
    String wordsBlueBold[] = {"void", "int", "long", "char", "float", "double", "bool", "boolean", "byte","unsigned", "signed", 
                              "if", "else", "for", "while", "do", "return", "continue", "break", "switch", "case", "try", "catch", 
                              "finally", "class", "public", "private", "protected", "extends", "implements", "new", "using",
                              "namespace", "final", "const", "true", "false", "static", "auto", "default", "enum", "extern", 
                              "goto", "register", "sizeof", "struct","typedef", "union", "volatile", "__int64"};
    char othersRed[] = {'(', ')', '{', '}', '[', ']', '<', '>',';', ',', '.', '~', '&', '|', '^', '+', '-', '*', '/', '=', '?', ':'};
    //Color colorsWord[] = {Color.blue, Color.blue, Color.blue, Color.blue, Color.blue, Color.blue, Color.blue, Color.blue, Color.blue, Color.blue, Color.blue};
    
    private void coloringWord(String word, Color c, boolean isBold){
        StyleConstants.setForeground(s, c);
        StyleConstants.setBold(s, isBold);
        try { 
            sd.insertString(sd.getLength(), word, s);
        }
        catch (BadLocationException ble){
            System.out.println(ble);
        }
    }
    
    public StyleEditor(JTextPane jtp, String mainText, Color normalColor){
        sd = jtp.getStyledDocument();
        s = jtp.addStyle(null, null);
        
        //filtering carriage return ('\r'=13)
        String text = "";
        for(int I=0; I<mainText.length(); I++)
            if( (int)mainText.charAt(I)!=13 )
                text += mainText.charAt(I);
        
        for(int I=0; I<text.length(); I++){
            //System.out.println("SE -> "+(int)text.charAt(I));
            boolean isColored = false;
            
            //color for start[] & stop[]
            for(int J=0; J<start.length; J++){
                String findStart = start[J];
                String findStop = stop[J];
                if( text.regionMatches(false, I, findStart, 0, findStart.length())==true ){
                    String lines = findStart;
                    int K;
                    for(K=I+findStart.length(); K<text.length()&&text.regionMatches(false, K, findStop, 0, findStop.length())==false; K++)
                        lines += text.charAt(K);
                    if( K<text.length() )
                        lines += findStop;
                    coloringWord(lines, colorsLine[J], false);
                    isColored = true;
                    I += lines.length()-1;
                    //System.out.println("SE -> "+lines.length());
                }
            }
            
            //checking keyword
            char prev = ' ';
            if( I>0 )
                prev = text.charAt(I-1);
            //System.out.println("SE prev => "+prev);
            if( ((prev>='0'&&prev<='9') || (prev>='a'&&prev<='z') || (prev>='A'&&prev<='Z'))==false ){
                //blue bold keyword
                for(int J=0; J<wordsBlueBold.length; J++){
                    String findWord = wordsBlueBold[J];
                    if( text.regionMatches(false, I, findWord, 0, findWord.length())==true ){
                        char next = ' ';
                        if( I+findWord.length()<text.length() )
                            next = text.charAt(I+findWord.length());
                        if( ((next>='0'&&next<='9') || (next>='a'&&next<='z') || (next>='A'&&next<='Z'))==false ){
                            coloringWord(findWord, Color.blue, true);
                            isColored = true;
                            I += findWord.length()-1;
                        }
                    }
                }
            }
            
            //other coloring
            if( isColored==false ){
                for(int J=0; J<othersRed.length; J++)
                    if( othersRed[J]==text.charAt(I) ){
                        coloringWord(String.valueOf(text.charAt(I)), Color.red, false);
                        isColored = true;
                        break;
                    }
            }
            
            //not coloring
            if( isColored==false ){
                coloringWord(String.valueOf(text.charAt(I)), normalColor, false);
            }
        }
        
        
    }
    
}
