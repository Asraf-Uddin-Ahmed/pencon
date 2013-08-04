/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import java.io.File;

/**
 *
 * @author RATUL
 */
public class Password {
    
    String passwordFolderPath = null;

    public Password() {
        SystemInformation systemInformation = new SystemInformation();
        //if judge or contestant without pen drive means without contest. just for check.
        if( systemInformation.isJudge==true || systemInformation.penDrive==null ){
            passwordFolderPath = systemInformation.mainPath + "\\Password";
        }
        //if contestant
        else{
            passwordFolderPath = systemInformation.penDrive.toString() + "\\Password";
        }
    }
    
    /*make password file. need file name with extension*/
    void makeFile(String passwordFileName, String password){
        System.out.println("Password > makeFile => "+passwordFileName+" "+password);
        String passwordFilePath = passwordFolderPath + "\\" + passwordFileName;
        BatchFileExecution executeBatchFile = new BatchFileExecution();
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        encryptionDecryption.writeFileEncrypt(passwordFilePath, password, encryptionDecryption.getEncryptValue(passwordFilePath));
        executeBatchFile.executeCommand("attrib +S +H +R \""+passwordFilePath+"\"");
    }
    
    /*make password folder for judge & contestant*/
    void makeFolder(){
        FileDirectory fileDirectory = new FileDirectory();
        SystemInformation systemInformation = new SystemInformation();
        
        //if user is a contestant & does not want to use pen drive
        if( systemInformation.isJudge==false && systemInformation.penDrive!=null )
            return ;
        //if folder exists
        if( fileDirectory.isExist(passwordFolderPath)==true )
            return ;
        
        //if user is a judge & folder does not exist
        fileDirectory.makeDirs(passwordFolderPath);
        BatchFileExecution executeBatchFile = new BatchFileExecution();
        executeBatchFile.executeCommand("attrib +S +H +R \""+passwordFolderPath+"\"");
        //make password files
        makeFile("ratul.jud", "ratul");
        makeFile("ratul.con", "ratul");
    }
    
    /*is user exist then assign filepath into static variable of SystemInformation*/
    boolean isUserExist(String userName){
        FileDirectory fileDirectory = new FileDirectory();
        SystemInformation userCategory = new SystemInformation();
        String filePath = passwordFolderPath + "\\" + userName + "." + userCategory.getExtension();
        
        if( fileDirectory.isExist(filePath)==false )
            return false;
        
        userCategory.passwordFilePath = filePath;
        return true;
    }
    
    /**/
    boolean isPasswordRight(String userPassword){
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        String filePath = new SystemInformation().passwordFilePath;
        String realPassword = encryptionDecryption.readFileDecrypt(filePath, encryptionDecryption.getEncryptValue(filePath));
        return userPassword.equals(realPassword);
    }
    
   /**/
    String getContestantPasswordFilePath(String folderPath){
        FileDirectory fileDirectory = new FileDirectory();
        String fileList[] = fileDirectory.getDirectoryChildName(folderPath);
        for(String file : fileList){
            //finding a file which has .con extension
            if( file.regionMatches(file.length()-4, ".con", 0, 4)==true ){
                return passwordFolderPath+"\\"+file;
            }
        }
        return null;
    }
    
    /**/
   boolean deleteContestantPasswordFile(){
        FileDirectory fileDirectory = new FileDirectory();
        File contestantPasswordFile = new File(getContestantPasswordFilePath(passwordFolderPath));
        
        //System.out.println("Password => "+contestantPasswordFile+" -> "+contestantPasswordFile.delete());
        return fileDirectory.deleteDir(contestantPasswordFile);
    }
}
