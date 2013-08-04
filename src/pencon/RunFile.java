/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import javax.swing.*;
/**
 *
 * @author RATUL
 */
public class RunFile  implements Runnable{
    String mainPath = null;
    String inFile = null;
    String command = null;
    String fileName = null;
    Thread t = null;
    boolean completed = false;
    
    RunFile(String mp, String fn, String ifile, String cmnd){
        mainPath = mp;
        inFile = ifile;
        command = cmnd;
        fileName = fn;
        t = new Thread(this);
        t.start();
    }
    
    public void run(){
        FileDirectory fd = new FileDirectory();
        String batFileName = "";
        batFileName = mainPath+"\\batch\\run.bat";
        BatchFileExecution ebf = new BatchFileExecution();
        try{
            //creating run.bat
            fd.makeFileAppend(batFileName, "@echo off", false);
            fd.makeFileAppend(batFileName, "C:", true);
            fd.makeFileAppend(batFileName, "cd "+mainPath+"\\src", true);
            fd.makeFileAppend(batFileName, command+" "+fileName+"<"+inFile+">\""+mainPath+"\\src\\out.txt\"", true);
            //fd.makeFileAppend(batFileName, "exit\n", true);
            if( ebf.isError(batFileName)==true || fd.getFileString(mainPath+"\\src\\out.txt", "").isEmpty()==true ){
                completed = true;
                if( SystemInformation.verdict==null )
                    SystemInformation.verdict = "RTE";
                
            }
            else{
                completed = true;
                JOptionPane.showMessageDialog(null, "Output file generated :-)", "Successful", JOptionPane.INFORMATION_MESSAGE);
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
