import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

public class JavaCodeEditor extends JFrame {

    private JTextArea textArea;
    private JFileChooser fileChooser;

    private JTextArea lineNumberArea;

    public JavaCodeEditor() {
        // Set the title and layout of the frame
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\ctfua\\Desktop\\icon.png");
        setIconImage(icon);
        setTitle("Java Code Editor");
        setLayout(new BorderLayout());

        // Create the text area and add it to the frame
        textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        lineNumberArea = new JTextArea("1");
        lineNumberArea.setBackground(Color.LIGHT_GRAY);
        lineNumberArea.setEditable(false);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public String getText(){
                int caretPosition = textArea.getDocument().getLength();
                Element root = textArea.getDocument().getDefaultRootElement();
                String text = "1" + System.getProperty("line.separator");
                for(int i = 2; i < root.getElementCount() + 1; i++){
                    text += i + System.getProperty("line.separator");
                }
                return text;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lineNumberArea.setText(getText());
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                lineNumberArea.setText(getText());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                lineNumberArea.setText(getText());
            }
        });
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberArea);
        add(scrollPane);

        // Create the menu bar and add it to the frame
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Create the File menu and add it to the menu bar
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Create the Open and Save menu items and add them to the File menu
        JMenuItem openItem = new JMenuItem("Open");
        fileMenu.add(openItem);
        JMenuItem saveItem = new JMenuItem("Save");
        fileMenu.add(saveItem);

        // Add an ActionListener to the Open menu item
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        // Add an ActionListener to the Save menu item
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        // Create the Edit menu and add it to the menu bar
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        // Create the Find and Replace menu item and add it to the Edit menu
        JMenuItem findAndReplaceItem = new JMenuItem("Find and Replace");
        editMenu.add(findAndReplaceItem);

        // Add an ActionListener to the Find and Replace menu item
        findAndReplaceItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findAndReplace();
            }
        });

        // Create the About menu and add it to the menu bar
        JMenu aboutMenu = new JMenu("More");
        menuBar.add(aboutMenu);

        // Create the About menu item and add it to the About menu
        JMenuItem aboutItem = new JMenuItem("About");
        aboutMenu.add(aboutItem);

        JMenuItem switchTheme = new JMenuItem("Switch Theme");
        aboutMenu.add(switchTheme);

        final boolean[] isDark = {false};
        switchTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isDark[0]) {
                    textArea.setBackground(Color.WHITE);
                    textArea.setForeground(Color.BLACK);
                    isDark[0] = false;
                } else {
                    textArea.setBackground(Color.DARK_GRAY);
                    textArea.setForeground(Color.WHITE);
                    isDark[0] = true;
                }
            }
        });

        // Add an ActionListener to the About menu item
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                about();
            }
        });

        // set the default close operation and size of the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);


    }

    private void openFile() {
        // Create a file chooser
        fileChooser = new JFileChooser();

        // Set the file filter to only show .java files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".java") || f.isDirectory();
            }

            public String getDescription() {
                return "Java source files (.java)";
            }
        });

        // Show the file chooser and check the user's selection
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File file = fileChooser.getSelectedFile();

            try {
                // Read the contents of the file into the text area
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                textArea.setText(sb.toString());
                reader.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void saveFile() {
        // Create a file chooser
        fileChooser = new JFileChooser();

        // Set the file filter to only show .java files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".java") || f.isDirectory();
            }

            public String getDescription() {
                return "Java source files (.java)";
            }
        });

        // Show the file chooser and check the user's selection
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File file = fileChooser.getSelectedFile();

            // Make sure the file has the .java extension
            if (!file.getName().toLowerCase().endsWith(".java")) {
                file = new File(file.getAbsolutePath() + ".java");
            }

            try {
                // Write the contents of the text area to the file
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void findAndReplace() {
        String findText = JOptionPane.showInputDialog(this, "Enter the text to find:");
        if (findText == null) {
            return;
        }
        String replaceText = JOptionPane.showInputDialog(this, "Enter the replacement text:");
        if (replaceText == null) {
            return;
        }

        int count = 0;
        int index = textArea.getText().indexOf(findText);
        while (index != -1) {
            count++;
            textArea.replaceRange(replaceText, index, index + findText.length());
            index = textArea.getText().indexOf(findText, index + replaceText.length());
        }

        JOptionPane.showOptionDialog(this, "We made " + count + " replacements!", "Results", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[] {}, null);
    }
    
    private void about() {
        JOptionPane.showMessageDialog(this, "THIS IS PRISTINE!!!", "About", JOptionPane.INFORMATION_MESSAGE);

    }

    public static void main(String[] args) {
        JavaCodeEditor editor = new JavaCodeEditor();
        editor.setVisible(true);
    }
}