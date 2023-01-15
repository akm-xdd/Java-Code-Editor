import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;


public class JavaCodeEditor extends JFrame {

    private final JTextArea textArea;
    private final JTextArea lineNumberArea;
    private final UndoManager undo = new UndoManager();
    private final UndoAction undoAction = new UndoAction();
    private final RedoAction redoAction = new RedoAction();
    private JFileChooser fileChooser;

    public JavaCodeEditor() {
        // Set the title and layout of the frame
        JFrame frame = new JFrame();

        Image icon = Toolkit.getDefaultToolkit().getImage("icon.png");
        setIconImage(icon);
        setTitle("PRISTINE TEXT");
        setLayout(new BorderLayout());

        // Create the text area and add it to the frame
        textArea = new JTextArea("//THIS IS PRISTINE \nDelete this text or start writing  below this line\n");
        textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
        textArea.setFont(new Font("Comic Sans MS", Font.BOLD, 12));

        add(new JScrollPane(textArea), BorderLayout.CENTER);
        lineNumberArea = new JTextArea("1");
        lineNumberArea.setBackground(Color.LIGHT_GRAY);
        lineNumberArea.setEditable(false);

        // Create a new document filter


        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public String getText() {
                Element root = textArea.getDocument().getDefaultRootElement();
                StringBuilder text = new StringBuilder("1" + System.getProperty("line.separator"));
                for (int i = 2; i < root.getElementCount() + 1; i++) {
                    text.append(i).append(System.getProperty("line.separator"));
                }
                return text.toString();
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
        openItem.addActionListener(e -> openFile());

        // Add an ActionListener to the Save menu item
        saveItem.addActionListener(e -> saveFile());

        // Create the Edit menu and add it to the menu bar
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);


        // Create the Find and Replace menu item and add it to the Edit menu
        JMenuItem findAndReplaceItem = new JMenuItem("Find and Replace");
        editMenu.add(findAndReplaceItem);


        // Add an ActionListener to the Find and Replace menu item
        findAndReplaceItem.addActionListener(e -> findAndReplace());

        // Create the About menu and add it to the menu bar
        JMenu aboutMenu = new JMenu("More");
        menuBar.add(aboutMenu);

        // Create the About menu item and add it to the About menu
        JMenuItem aboutItem = new JMenuItem("About");
        aboutMenu.add(aboutItem);

        JMenuItem visitUsItem = new JMenuItem("Visit Us");
        aboutMenu.add(visitUsItem);
        visitUsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showOptionDialog(frame,
                        "You can learn more about the project at my Github Repo!\nPlease visit us for more information.",
                        "Visit Us",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"OK", "Take me there"},
                        "OK");
            }
        });

        visitUsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showOptionDialog(frame,
                        "You can learn more about the project at my Github Repo!\nPlease visit us for more information.",
                        "Visit Us",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"OK", "Take me there"},
                        "OK");

                if (choice == 1) {
                    // "Take me there" button clicked, open the website in the default browser
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/akm-xdd/Java-Code-Editor"));
                    } catch (URISyntaxException | IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });




        JMenuItem switchTheme = new JMenuItem("Switch Theme");
        aboutMenu.add(switchTheme);

        final boolean[] isDark = {false};
        switchTheme.addActionListener(e -> {
            if (isDark[0]) {
                textArea.setBackground(Color.WHITE);
                textArea.setForeground(Color.BLACK);
                isDark[0] = false;
            } else {
                textArea.setBackground(Color.DARK_GRAY);
                textArea.setForeground(Color.WHITE);
                isDark[0] = true;
            }
        });

        // Add an ActionListener to the About menu item
        aboutItem.addActionListener(e -> about());

        // set the default close operation and size of the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);

        textArea.getDocument().addUndoableEditListener(e -> undo.addEdit(e.getEdit()));
        InputMap im = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = textArea.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        am.put("undo", undoAction);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        am.put("redo", redoAction);
        textArea.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                int zoom = e.getWheelRotation();
                if (zoom > 0) {
                    textArea.setFont(textArea.getFont().deriveFont(textArea.getFont().getSize() - 2f));
                    lineNumberArea.setFont(lineNumberArea.getFont().deriveFont(lineNumberArea.getFont().getSize() - 2f));
                } else {
                    textArea.setFont(textArea.getFont().deriveFont(textArea.getFont().getSize() + 2f));
                    lineNumberArea.setFont(lineNumberArea.getFont().deriveFont(lineNumberArea.getFont().getSize() + 2f));
                }
            }
        });

    }

    public static void main(String[] args) {
        JavaCodeEditor editor = new JavaCodeEditor();
        editor.setVisible(true);
    }

    private void openFile() {
        // Create a file chooser
        fileChooser = new JFileChooser();

        // Set the file filter to only show .java files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
            }

            public String getDescription() {
                return "Text Files (.txt)";
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
                return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
            }

            public String getDescription() {
                return "Text Files (.txt)";
            }
        });

        // Show the file chooser and check the user's selection
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File file = fileChooser.getSelectedFile();

            // Make sure the file has the .java extension
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
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

        JOptionPane.showOptionDialog(this, "We made " + count + " replacements!", "Results", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
    }

    private void about() {
        JOptionPane.showMessageDialog(this, "This is Pristine, a simple text editor.\nLearn more on Github at @ax ", "About", JOptionPane.INFORMATION_MESSAGE);

    }

    protected void update() {
        undoAction.setEnabled(undo.canUndo());
        redoAction.setEnabled(undo.canRedo());
    }

    private class UndoAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                // Handle exception
            }
            update();
        }
    }

    private class RedoAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                // Handle exception
            }
            update();
        }

    }
}

