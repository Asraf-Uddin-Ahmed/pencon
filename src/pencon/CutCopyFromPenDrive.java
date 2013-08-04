/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author RATUL
 */
public class CutCopyFromPenDrive implements Runnable{

    public CutCopyFromPenDrive() {
        new Thread(this).start();
    }
    
    /**removing Contest, Password & Info folder of main path
     * copying Contest, Password & Info folder in main path
     * make them undetectable for windows
     * removing Contest folder of pen drive
     */
    public void run(){
        //create & show please wait window
        PleaseWaitAutoClosePage pleaseWaitAutoClosePage = new PleaseWaitAutoClosePage();
        pleaseWaitAutoClosePage.setVisible(true);
        
        //initialization
        SystemInformation systemInformation = new SystemInformation();
        File penDrive = systemInformation.penDrive;
        String mainPath = systemInformation.mainPath;
        
        //creating contest folders & files
        FileDirectory fileDirectory = new FileDirectory();
        File mainPathContest = new File(mainPath+"\\Contest");
        File mainPathPassword = new File(mainPath+"\\Password");
        File mainPathInfo = new File(mainPath+"\\Info");
        try{
            //deleting folders & files from mainPath C: drive
            fileDirectory.deleteDir(mainPathContest);
            fileDirectory.deleteDir(mainPathPassword);
            fileDirectory.deleteDir(mainPathInfo);
            
            //copying folders & files
            fileDirectory.copyFolder(new File(penDrive+"\\Contest"), mainPathContest);
            fileDirectory.copyFolder(new File(penDrive+"\\Password"), mainPathPassword);
            fileDirectory.copyFolder(new File(penDrive+"\\Info"), mainPathInfo);
            
            //deleting contest folders & files from pen drive
            fileDirectory.deleteDir( new File(penDrive+"\\Contest") );
            
        }catch(Exception exception){
            pleaseWaitAutoClosePage.dispose();
            System.out.println("CutCopyPenDrive => "+exception);
            JOptionPane.showMessageDialog(null, "The system cannot find the file specified", "Error", JOptionPane.ERROR_MESSAGE);
        }

        //make them undecteable for windows
        BatchFileExecution executeBatchFile = new BatchFileExecution();
        executeBatchFile.executeCommand("attrib +S +H +R \""+mainPathContest.toString()+"\"");
        executeBatchFile.executeCommand("attrib +S +H +R \""+mainPathPassword.toString()+"\"");
        executeBatchFile.executeCommand("attrib +S +H +R \""+mainPathInfo.toString()+"\"");
        
        
        pleaseWaitAutoClosePage.dispose();
    }
}
