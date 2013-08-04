/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author RATUL
 */
public class SystemInformation {
    /*general info*/
    //address of main folder
    String mainPath = "C:\\PenCon";
    //address of used C++ complier path
    String cppCompilerPath = "C:\\Program Files\\CodeBlocks\\MinGW\\bin";
    //address of used java complier path
    String javaCompilerPath = "C:\\Program Files\\Java\\jdk1.7.0\\bin";
    //address of info folder path which contains judge I/O files & contest duration
    String infoFolderPath = mainPath + "\\Info";
    
    /*initialize form judge's view*/
    public static boolean isJudge = true;
    public static String passwordFilePath = "";
    public static File penDrive = null;
    //without compilation error
    public static String verdict = null;
    //is MainPage jButtonResult clicked
    public static boolean isResult = false;
    
    //storing submission histories
    public static ArrayList< ArrayList<String> > teamSubmissionHistories = new ArrayList< ArrayList<String> >();
    //count total successful submission time
    public static int totalSubmission = 0;
    
    /*get file extension judge or contestant*/
    String getExtension(){
        if( isJudge==true )
            return "jud";
        return "con";
    }
    
    /*remove contest & password folder of main path*/
    void removeContestPasswordFolder(){
        FileDirectory fileDirectory = new FileDirectory();
        fileDirectory.deleteDir(new File(mainPath+"\\Contest"));
        fileDirectory.deleteDir(new File(mainPath+"\\Password"));
        removeResultFileIfNeed();
    }
    
    /**/
    void removeResultFileIfNeed(){
        FileDirectory fileDirectory = new FileDirectory();
        String resultFilePath = penDrive+"\\Info\\Result.txt";
        try{
            if( fileDirectory.getFileVector(resultFilePath).size()!=totalSubmission )
                fileDirectory.deleteDir(new File(resultFilePath));
        }catch(Exception exception){
            System.out.println("SystemInformation => "+exception);
        }
    }
}
