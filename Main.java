/**
 *  第五次-涂文泰-2016214073 on 2016/11/9.
 */
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;

public class Main {

    private JFrame frame;
    private JTextField textField;
    private JTextField textField_1;
    private TextArea textArea;
    private String filePath;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Main() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("文本搜索");                    //窗体的名称
        frame.setResizable(false);
        frame.setBounds(100, 100, 531, 402);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        textField = new JTextField();
        textField.setBounds(10, 10, 368, 21);
        frame.getContentPane().add(textField);
        textField.setColumns(25);

        textField_1 = new JTextField();
        textField_1.setBounds(10, 41, 368, 21);
        frame.getContentPane().add(textField_1);
        textField_1.setColumns(25);

        textArea = new TextArea();
        textArea.setFont(new Font("华文楷体", Font.PLAIN, 14));
        textArea.setBounds(10, 68, 495, 295);
        frame.getContentPane().add(textArea);

        Button button = new Button("Choose File");            //按钮的名称 选择文件
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jf.showDialog(null, null);
                File file = jf.getSelectedFile();
                try {
                    filePath = file.getAbsolutePath();
                } catch (Exception e2) {
                }
                textField.setText(filePath);
            }
        });
        button.setBounds(384, 8, 121, 23);
        frame.getContentPane().add(button);

        Button button_1 = new Button("Begin to find");                          //按钮的名称  开始搜索
        button_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String keyWord = textField_1.getText().trim().toString();
                if (keyWord == null || keyWord.equals("")) {
                    JOptionPane.showMessageDialog(null, "你还未输入搜索内容", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                filePath = textField.getText().trim().toString();
//				textArea.setText("测试"+filePath);
                MySearchThread thread = new MySearchThread(filePath, keyWord);
                thread.start();
            }
        });
        button_1.setBounds(384, 39, 121, 23);
        frame.getContentPane().add(button_1);
    }

    /**
     * 文件搜索线程类
     *
     * @author shiweixian
     *
     */
    class MySearchThread extends Thread {

        String filePath;
        String keyWord;
        List<File> list ;
        public MySearchThread(String filePath, String keyWord) {
            this.filePath = filePath;
            this.keyWord = keyWord;
            list = new ArrayList<>();
        }

        @Override
        public void run() {
            if (filePath == null || filePath.equals("")) {
                JOptionPane.showMessageDialog(null, "无法找到该文件，请重新操作", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            File file = new File(filePath);
            if (file.isFile()) {
                ScannerFile(file); //搜索文件里的内容
            }
            else if(file.isDirectory()){
                try {
                    getFilePath(file); //获得文件夹下的所有文件
                } catch (NullPointerException e) {
                    JOptionPane.showMessageDialog(null, "路径不正确", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
                }
                if(list != null){
                    for(int i = 0;i<list.size();i++){
                        textArea.append(list.get(i).getAbsolutePath()+":\n");
                        ScannerFile(list.get(i));
                    }
                    textArea.append("\r\n");
                }
            }
        }

        /**
         * 扫描文件，搜索出想要的内容
         */
        private void ScannerFile(File file){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String temp;
                int count = 1;
                while ((temp = reader.readLine()) != null) {
                    if (temp.contains(keyWord))
                        textArea.append("\t第" + count + "行： " + temp + "\n");
                    count++;
                }
                reader.close();
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "无法找到该文件，请重新操作", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "很抱歉，搜索过程中出现错误", "错误", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        /**
         * 获取文件夹下所有子文件的路径（递归）
         * @param file
         */
        private void getFilePath(File file){
            if(file.isFile())
                list.add(file);
            else if(file.isDirectory()){
                File[] files = file.listFiles();
                for(int i =0;i<files.length;i++){
                    getFilePath(files[i]);
                }
            }
        }
    }
}

