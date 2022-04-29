package UI;


import FileUpload.TCPClient;
import FileUpload.TCPServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

public class MainUI
{
    private Window MainWindow;
    private JPanel panel1;
    private JLabel header_label;
    private JLabel statusLabel;
    private JLabel img_label;
    private ImageIcon icon;
    private JButton select_file_btn;
    private JButton ok_btn;
    private String img_path = "img/home01.jpg";
    private TextField IP_edit;
    private TextField PORT_edit;
    private JLabel IP_label;
    private JLabel PORT_label;

    private String send_file_name;
    private String send_file_path;
    private String IP;
    private int PORT;

    public MainUI()
    {
        initUI();
    }

    private void initUI()
    {
        icon = new ImageIcon(img_path);

        header_label = new JLabel("Welcome to remote file secure transfer!", JLabel.CENTER);
        img_label = new JLabel("", icon, JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.CENTER);
        IP_label = new JLabel("Server IP:", JLabel.RIGHT);
        PORT_label = new JLabel("Server PORT:", JLabel.CENTER);

        IP_edit = new TextField();
        PORT_edit = new TextField();

        IP_edit.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                IP = IP_edit.getText();
                System.out.println("IP:" + IP);
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                IP = IP_edit.getText();
                System.out.println("IP:" + IP);
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                IP = IP_edit.getText();
                System.out.println("IP:" + IP);
            }
        });

        PORT_edit.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
//                PORT = Integer.parseInt(PORT_edit.getText());
                System.out.println("PORT:" + PORT_edit.getText());
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
//                PORT = Integer.parseInt(PORT_edit.getText());
                System.out.println("PORT:" + PORT_edit.getText());
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
//                PORT = Integer.parseInt(PORT_edit.getText());
                System.out.println("PORT:" + PORT_edit.getText());
            }
        });


        final JFileChooser fileDialog = new JFileChooser();
        select_file_btn = new JButton("选择上传文件");
        ok_btn = new JButton("确定上传");
        select_file_btn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int returnVal = fileDialog.showOpenDialog(MainWindow);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fileDialog.getSelectedFile();
                    send_file_name = file.getName();
                    statusLabel.setText("选择上传文件:" + send_file_name);
                    send_file_path = file.getAbsolutePath();
                }
                else
                {
                    statusLabel.setText("取消打开文件");
                }
            }
        });

        ok_btn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
//                            System.out.println(send_file_name);
                            TCPServer server = new TCPServer();
                        }
                        catch (IOException ioException)
                        {
                            ioException.printStackTrace();
                        }
                    }
                }).start();

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            PORT = Integer.parseInt(PORT_edit.getText());
                            TCPClient client = new TCPClient(IP, PORT, send_file_path, send_file_name);
                            statusLabel.setText("上传成功");
                        }
                        catch (IOException ioException)
                        {
                            statusLabel.setText("上传失败");
                            ioException.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        panel1 = new JPanel();

        panel1.setLayout(new GridLayout(0, 4));

        header_label.setFont(new Font("Arial", Font.BOLD, 17));
        IP_label.setFont(new Font("Arial", Font.BOLD, 15));
        PORT_label.setFont(new Font("Arial", Font.BOLD, 15));

        // 布局
        MainWindow = new Window("远程文件安全传输");
        MainWindow.resize(640, 520);
        MainWindow.setLayout(new FlowLayout());

        panel1.add(IP_label, BorderLayout.WEST);
        panel1.add(IP_edit, BorderLayout.EAST);
        panel1.add(PORT_label, BorderLayout.EAST);
        panel1.add(PORT_edit, BorderLayout.WEST);
        panel1.add(new JLabel(""));
        panel1.add(select_file_btn, BorderLayout.WEST);
        panel1.add(new JLabel(""));
        panel1.add(ok_btn, BorderLayout.EAST);

        IP = IP_edit.getText();
        System.out.println(IP);

        MainWindow.add(header_label);
        MainWindow.add(img_label);
        MainWindow.add(panel1);
        MainWindow.add(statusLabel);

        MainWindow.setVisible(true);
    }

    public static void main(String[] args)
    {
        MainUI mainUI = new MainUI();
    }

}
