/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import java.awt.Frame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;

/**
 *
 * @author RATUL
 */
public class LogIn {
    
    JTextField jTextFieldName = null;
    JPasswordField jPasswordFieldPassword = null;
    Frame frameCurrent = null;
    
    public LogIn(JTextField jTextField, JPasswordField jPasswordField, Frame frame) {
        jTextFieldName = jTextField;
        jPasswordFieldPassword = jPasswordField;
        frameCurrent = frame;
    }
    
    
    boolean checkLogIn(){
        //taking input from user name & password
        String userName = jTextFieldName.getText();
        String userPassword = new String(jPasswordFieldPassword.getPassword());
        
        Password password = new Password();
        
        //make password folder if does not exist
        password.makeFolder();
        
        //if userName is not valid
        if( password.isUserExist(userName) ==false )
            return false;
        
        //if userName is valid
        return password.isPasswordRight(userPassword);
    }
    
    void logging(){
        SystemInformation systemInformation = new SystemInformation();
        if( systemInformation.isJudge==false ){
            WaitForPenDrivePage waitForPenDrivePage = new WaitForPenDrivePage(null, true);
            waitForPenDrivePage.setVisible(true);
        }
        
        if( checkLogIn()==false ){
            JOptionPane.showMessageDialog(frameCurrent, "Your given name or password is not valid.\nPlease try again (make sure your caps lock is off).", "Error", JOptionPane.ERROR_MESSAGE);
            jPasswordFieldPassword.setText(null);
        }
        else{
            new MainPage().setVisible(true);
            frameCurrent.dispose();
        }
    }
    
}
