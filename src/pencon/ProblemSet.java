/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author RATUL
 */
public class ProblemSet {
    
    String contestFolderPath = null;

    public ProblemSet() {
        SystemInformation systemInformation = new SystemInformation();
        contestFolderPath = systemInformation.mainPath + "\\Contest";
    }
    
    
    /*return a string array of problem id*/
    String[] getProblemIdList(){
        return new FileDirectory().getDirectoryChildName(contestFolderPath);
    }
    
    /*return an integer of added problem*/
    int getAddedProblemSize(){
        return getProblemIdList().length;
    }
    
    /*return a character which is the problem id of next problem that will be added*/
    char getRecentProblemId(){
        return (char)(getAddedProblemSize()+'A');
    }
    
    /*return a string arraylist full of problem name without prblem id*/
    ArrayList<String> getProblemNameList(){
        FileDirectory fileDirectory = new FileDirectory();
        String idList[] = getProblemIdList();
        ArrayList<String> nameList = new ArrayList<String>();
        
        System.out.println("ProblemSet => idList = "+idList);
        if( idList==null )
            return nameList;
                    
        for(String id : idList){
            String problemFolderPath = contestFolderPath + "\\" + id;
            try{
                nameList.add(fileDirectory.getFileString(problemFolderPath+"\\Name", ""));
            }catch(IOException iOException){
                nameList.add("ProblemSet ERROR");
                System.out.println("ProblemSet => "+iOException);
            }
        }
        return nameList;
    }
    
    /*add & update a problem*/
    boolean addUpdateProblem(char problemId, String problemName, String inFile, String outFile, String timeLimit, String memoryLimit){
        FileDirectory fileDirectory = new FileDirectory();
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        
        //if input or output file does not exist
        if( (isInputFileExists(problemId).equals("Found")==false&&fileDirectory.isExist(inFile)==false) || 
                (isOutputFileExists(problemId).equals("Found")==false&&fileDirectory.isExist(outFile)==false) )
            return false;

        //if timeLimit & memoryLimit does not valid
        try{
            Integer.valueOf(timeLimit);
            Integer.valueOf(memoryLimit);
        }catch(NumberFormatException numberFormatException){
            System.out.println("ProblemSet => "+numberFormatException);
            return false;
        }
        
        //if I/O files exist & inputs are valid
        String problemFolderPath = contestFolderPath + "\\" + String.valueOf(problemId);
        //create folder named problem ID
        fileDirectory.makeDirs(problemFolderPath);
        
        //make file to save name, timeLimit & memoryLimit
        try{
            fileDirectory.makeFileUtf(problemFolderPath+"\\Name", problemName);
            
            String timeLimitFilePath = problemFolderPath+"\\TimeLimit";
            encryptionDecryption.writeFileEncrypt(timeLimitFilePath, timeLimit,encryptionDecryption.getEncryptValue(timeLimitFilePath));
            String memoryLimitFilePath = problemFolderPath+"\\MemoryLimit";
            encryptionDecryption.writeFileEncrypt(memoryLimitFilePath, memoryLimit, encryptionDecryption.getEncryptValue(memoryLimitFilePath));    
            
            if( fileDirectory.isExist(inFile)==true )
                encryptionDecryption.makeFastestEncryptDecryptFile(inFile, problemFolderPath+"\\Input", true);
            if( fileDirectory.isExist(outFile)==true )
                encryptionDecryption.makeFastestEncryptDecryptFile(outFile, problemFolderPath+"\\Output", true);
        }catch(Exception exception){
            System.out.println(exception);
            return false;
        }
        return true;
    }
    
    /*return a problem name according to problem id*/
    String getProblemName(char problemId){
        try{
            return new FileDirectory().getFileString(contestFolderPath+"\\"+String.valueOf(problemId)+"\\Name", "");
        }catch(IOException iOException){
            System.out.println("ProblemSet => "+iOException);
            return "not found";
        }
    }
    
    /*return time limit of problem*/
    String getProblemTimeLimit(char problemId){
        String timeLimitFilePath = contestFolderPath+"\\"+String.valueOf(problemId)+"\\TimeLimit";
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        return encryptionDecryption.readFileDecrypt(timeLimitFilePath, encryptionDecryption.getEncryptValue(timeLimitFilePath));
    }
    
    /*return memory limit of problem*/
    String getProblemMemoryLimit(char problemId){
        String memoryLimitFilePath = contestFolderPath+"\\"+String.valueOf(problemId)+"\\MemoryLimit";
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        return encryptionDecryption.readFileDecrypt(memoryLimitFilePath, encryptionDecryption.getEncryptValue(memoryLimitFilePath));
    }
    
    /**/
    String isInputFileExists(char problemId){
        if( new FileDirectory().isExist(contestFolderPath+"\\"+String.valueOf(problemId)+"\\Input")==true )
            return "Found";
        return "not found";
    }
    
    /**/
    String isOutputFileExists(char problemId){
        if( new FileDirectory().isExist(contestFolderPath+"\\"+String.valueOf(problemId)+"\\Output")==true )
            return "Found";
        return "not found";
    }
    
    /*remove problem & rename other problem ID if needed*/
    boolean removeProblem(char problemId){
        FileDirectory fileDirectory = new FileDirectory();
        
        //delete problem folder
        fileDirectory.deleteDir(new File(contestFolderPath+"\\"+String.valueOf(problemId)));
        
        //get all folder name(problem ID) from contest
        String problemIdList[] = fileDirectory.getDirectoryChildName(contestFolderPath);
        for(int I=problemId-'A'; I<problemIdList.length; I++){
            String oldIdPath = contestFolderPath + "\\" + problemIdList[I];
            //new problem ID
            char newId = (char)(problemIdList[I].charAt(0)-1);
            String newIdPath = contestFolderPath + "\\" + String.valueOf(newId);
            
            if( fileDirectory.rename(oldIdPath, newIdPath)==false ){
                System.out.println("ProblemSet => "+oldIdPath+" <=> "+newIdPath);
                return false;
            }
        }
        return true;
    }
    
    /*print problem list in combo box*/
    void printProblemList(JComboBox jComboBox){
        ArrayList<String> problemList = getProblemNameList();
        jComboBox.removeAllItems();
        jComboBox.addItem("none");
        for(int I=0; I<problemList.size(); I++)
            jComboBox.addItem(String.valueOf((char)(I+'A'))+". "+problemList.get(I));
        jComboBox.setSelectedIndex(0);
    }
    
    /*make decrypted judge input or output file*/
    boolean makeDecryptedJudgeFile(String encryptFilePath, String decryptFilePath){
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        new FileDirectory().makeDirs(new SystemInformation().infoFolderPath);
        try{
            encryptionDecryption.makeFastestEncryptDecryptFile(encryptFilePath, decryptFilePath, false);
            return true;
        }catch(Exception exception){
            System.out.println("ProblemSet => "+exception);
            JOptionPane.showMessageDialog(null, "Unable to create judge input file.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /*get problem id which contestant selected selected */
    char getSelectedProblemId(JComboBox jComboBox){
        return (char)(jComboBox.getSelectedIndex()-1+'A');
    }
}
