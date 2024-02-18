import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

public class Server{
    static ArrayList<File> files = new ArrayList<File>();

    public static String geFileExtension(String fileName){
        int i = fileName.lastIndexOf('.');
        if(i>0){
            return fileName.substring(i+1);
        }
        else{
            return "No Extension Found";
        }
    }

    public static MouseListener getMyMouseListener(){
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel jPanel = (JPanel) e.getSource();
                int fileId = Integer.parseInt(jPanel.getName());
                for(File file: files){
                    if(file.getId() == fileId){
                        JFrame jfPreview = createFrame(file.getName(), file.getData(), file.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'mousePressed'");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'mouseReleased'");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'mouseEntered'");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'mouseExited'");
            }
        
        };
    }

    public static JFrame createFrame(String fileName,byte[] fileData, String fileExtension){
        JFrame jfrmae = new JFrame("File Downloader");
        jfrmae.setSize(400,400);

        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));

        JLabel jlTitle = new JLabel("File Downloader");
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));

        JLabel jlPrompt = new JLabel("Do you want to download the file?");
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlPrompt.setBorder(new EmptyBorder(20,0,10,0));

        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150,75));
        
        JButton jbNo = new JButton("No");
        jbNo.setPreferredSize(new Dimension(150,75));

        JLabel jFileContent = new JLabel();
        jFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButtons = new JPanel();
        jpButtons.setBorder(new EmptyBorder(20,0,10,0));
        jpButtons.add(jbYes);
        jpButtons.add(jbNo);

        if(fileExtension.equalsIgnoreCase("txt")){
            jFileContent.setText("<html>"+new String(fileData)+"</html>");
        }else{
            jFileContent.setIcon(new ImageIcon(fileData));
        }

        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File fileDownload = new File(fileName);
                try{
                    FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
                    fileOutputStream.write(fileData);
                    fileOutputStream.close();
                    jfrmae.dispose();
                }
                catch(IOException error){
                    error.printStackTrace();
                }
            }
        });

        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jfrmae.dispose();
            }
        });

        jpanel.add(jlTitle);
        jpanel.add(jlPrompt);
        jpanel.add(jFileContent);
        jpanel.add(jpButtons);

        jfrmae.add(jpanel);
        return jfrmae;
    }

    public static void main(String[] args) throws IOException {
        int fileId = 0 ;

        JFrame jframe = new JFrame("Document Server");
        jframe.setSize(400,400);
        jframe.setLayout(new BoxLayout(jframe.getContentPane(), BoxLayout.Y_AXIS));
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));

        JScrollPane jscrollPane = new JScrollPane(jpanel);
        jscrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jlTitle = new JLabel("File Reciever");
        jlTitle.setFont(jlTitle.getFont().deriveFont(24.0f));
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jframe.add(jlTitle);
        jframe.add(jscrollPane);
        jframe.setVisible(true);

        //Connection
        ServerSocket serverSocket = new ServerSocket(1234);
        while(true){
            try{
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int fileNameLength = dataInputStream.readInt();
                if(fileNameLength>0){

                    byte[] fileNameBytes = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);
                    int fileContentLength = dataInputStream.readInt();
                    if(fileContentLength>0){
                        byte[] fileContent = new byte[fileContentLength];
                        dataInputStream.readFully(fileContent,0,fileContent.length);

                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.X_AXIS));
                        
                        JLabel jlFileName = new JLabel(fileName);
                        //jlFileName.setFont(new Font("Arial", Font.PLAIN, 20));
                        jlFileName.setBorder(new EmptyBorder(10,0,10,0));

                        if(geFileExtension(fileName).equalsIgnoreCase("txt")){
                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jpanel.add(jpFileRow);
                            jframe.validate();
                        }
                        else{
                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jpanel.add(jpFileRow);
                            jframe.validate();
                        
                        }

                        files.add(new File(fileId, fileName, fileNameBytes, geFileExtension(fileName)));
                    }
                }
            }
            catch(IOException error){
                error.printStackTrace();
            }
        }
}
}