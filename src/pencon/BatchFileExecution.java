/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import java.io.*;

/**
 *
 * @author RATUL
 */
public class BatchFileExecution {
    
    String errorMessage = null;
    
    /* for manipulating standard error*/
    boolean isError(String batFileName)throws Exception{
        //batch file entering src to create exe file
        Process p = Runtime.getRuntime().exec(batFileName);
        p.waitFor();
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        
        //checking Error found. if not found ...
        if( stdError.ready()==false )
            return false;
        
        //error print
        String str = null;
        errorMessage = "";
        while ((str = stdError.readLine()) != null){
            errorMessage += (str+"\n");
            System.out.println("BatchFileExecution => "+str);
        }
        return true;
    }
    
    /* get standard input string from command line */
    String inputString(String batFileName)throws Exception{
        //batch file entering src to create exe file
        Process p = Runtime.getRuntime().exec(batFileName);
        p.waitFor();
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
        //System.out.println("BatchFileExecution => "+"EBF_checked");
                
        //input print
        String str = null;
        String ans = null;
        while ((str = stdIn.readLine()) != null){
            System.out.println("BatchFileExecution => "+str);
            ans = str;
        }
        return ans;
    }
    
    /* for manipulating standard input*/
    boolean isInput(String batFileName)throws Exception{
        return inputString(batFileName).equals("true");
    }
    
    /* creating compile.bat file to compiling file */
    boolean creatingCompileBat(String batFileName, String compilerPath,String compileInstruction){
        FileDirectory fd = new FileDirectory();
        try{
            fd.makeFileAppend(batFileName, "@echo off", false);
            fd.makeFileAppend(batFileName, "path "+compilerPath, true);
            fd.makeFileAppend(batFileName, compileInstruction, true);
            //fd.makeFileAppend(batFileName, "exit", true);
            return true;
        }catch(IOException ioe){
            return false;
        }
        
    }
    
    /**/
    boolean executeCommand(String command){
        try{
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            return true;
        }catch(Exception e){
            System.out.println("BatchFileExecution => "+e);
            return false;
        }
    }
}
