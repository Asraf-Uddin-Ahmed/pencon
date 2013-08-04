/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TeamInformation.java
 *
 * Created on Aug 22, 2012, 11:57:51 PM
 */
package pencon;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author RATUL
 */
public class TeamInformation extends javax.swing.JDialog implements Runnable{

    Frame predecessor = null;
    String timeFilePath = null;
    String mainPath = null;
    
    /**creating contestant name & password
     * removing Contest, Password & Info folder of pen drive
     * copying Contest, Password & Info folder in pen drive
     * make them undetectable for windows
     */
    void addNewContestant(){
        //delete old contestant password file
        Password password = new Password();
        password.deleteContestantPasswordFile();
        
        //create new contestant password file
        String userName = jTextFieldName.getText();
        String userPassword = new String(jPasswordFieldPassword.getPassword());
        password.makeFile(userName+".con", userPassword);
        
        SystemInformation systemInformation = new SystemInformation();
        File penDrive = systemInformation.penDrive;
        
        //creating contest folders & files
        FileDirectory fileDirectory = new FileDirectory();
        File penDriveContest = new File(penDrive+"\\Contest");
        File penDrivePassword = new File(penDrive+"\\Password");
        File penDriveInfo = new File(penDrive+"\\Info");
        try{
            //deleting contest folders & files
            fileDirectory.deleteDir(penDriveContest);
            fileDirectory.deleteDir(penDrivePassword);
            fileDirectory.deleteDir(penDriveInfo);
            
            //copying contest folders & files
            fileDirectory.copyFolder(new File(mainPath+"\\Contest"), penDriveContest);
            fileDirectory.copyFolder(new File(mainPath+"\\Password"), penDrivePassword);
            fileDirectory.copyFolder(new File(mainPath+"\\Info"), penDriveInfo);
        }catch(IOException iOException){
            System.out.println(iOException);
            JOptionPane.showMessageDialog(rootPane, iOException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        //make them undecteable for windows
        BatchFileExecution executeBatchFile = new BatchFileExecution();
        executeBatchFile.executeCommand("attrib +S +H +R \""+penDriveContest.toString()+"\"");
        executeBatchFile.executeCommand("attrib +S +H +R \""+penDrivePassword.toString()+"\"");
        executeBatchFile.executeCommand("attrib +S +H +R \""+penDriveInfo.toString()+"\"");
        
    }
    
    /*checking & parsing validity of duration time*/
    boolean checkTime(){
        try{
            Integer.valueOf(jTextFieldContestDuration.getText());
            return true;
        }catch(NumberFormatException numberFormatException){
            System.out.println("TeamInformation => "+numberFormatException);
            return false;
        }
    }
    
    /** Creates new form TeamInformation */
    public TeamInformation(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        
        //initialization
        SystemInformation systemInformation = new SystemInformation();
        mainPath = systemInformation.mainPath;
        timeFilePath = mainPath+"\\info\\Time";
        predecessor = parent;
        
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        jTextFieldContestDuration.setText(encryptionDecryption.readFileDecrypt(timeFilePath, encryptionDecryption.getEncryptValue(timeFilePath)));
        
    }
    
    public void run(){
        //make encrypted contest duration file
        FileDirectory fileDirectory = new FileDirectory();
        BatchFileExecution executeBatchFile = new BatchFileExecution();
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        fileDirectory.makeDirs(mainPath+"\\Info");
        executeBatchFile.executeCommand("attrib +S +H +R \""+mainPath+"\\Info\"");
        encryptionDecryption.writeFileEncrypt(timeFilePath, jTextFieldContestDuration.getText(), encryptionDecryption.getEncryptValue(timeFilePath));
        
        //create & show please wait window
        PleaseWaitAutoClosePage pleaseWaitAutoClosePage = new PleaseWaitAutoClosePage();
        pleaseWaitAutoClosePage.setVisible(true);
        
        //release all winodow components
        dispose();
        
        //disabling mainPage
        predecessor.disable();
        
        addNewContestant();
        
        //dispose please wait
        pleaseWaitAutoClosePage.dispose();
        
        //enabling mainPage
        predecessor.enable();
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOk = new javax.swing.JButton();
        jLabelPassword = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jLabelName = new javax.swing.JLabel();
        jButtonCancel = new javax.swing.JButton();
        jPasswordFieldPassword = new javax.swing.JPasswordField();
        jLabelContestDuration = new javax.swing.JLabel();
        jLabelMinuets = new javax.swing.JLabel();
        jTextFieldContestDuration = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Team Information");
        setResizable(false);

        jButtonOk.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButtonOk.setText("Activate");
        jButtonOk.setFocusPainted(false);
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jLabelPassword.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabelPassword.setText("Password             :");

        jTextFieldName.setFont(new java.awt.Font("Tahoma", 0, 16));

        jLabelName.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabelName.setText("Team Name         :");

        jButtonCancel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButtonCancel.setText("Cancel");
        jButtonCancel.setFocusPainted(false);
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jPasswordFieldPassword.setFont(new java.awt.Font("Tahoma", 0, 16));

        jLabelContestDuration.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabelContestDuration.setText("Contest Duration :");

        jLabelMinuets.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabelMinuets.setText("Minutes");

        jTextFieldContestDuration.setFont(new java.awt.Font("Tahoma", 0, 16));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelPassword)
                            .addComponent(jLabelContestDuration))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPasswordFieldPassword)
                                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldContestDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelMinuets))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonOk)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelName, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPasswordFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelContestDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMinuets, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldContestDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOk)
                    .addComponent(jButtonCancel))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
    //addNewContestant();
    if( checkTime()==true )
        new Thread(this).start();
    else
        JOptionPane.showMessageDialog(rootPane, "You did not give correct information.", "Error", JOptionPane.ERROR_MESSAGE);
}//GEN-LAST:event_jButtonOkActionPerformed

private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
    dispose();
}//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TeamInformation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TeamInformation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TeamInformation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TeamInformation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                TeamInformation dialog = new TeamInformation(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelContestDuration;
    private javax.swing.JLabel jLabelMinuets;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JPasswordField jPasswordFieldPassword;
    private javax.swing.JTextField jTextFieldContestDuration;
    private javax.swing.JTextField jTextFieldName;
    // End of variables declaration//GEN-END:variables
}
