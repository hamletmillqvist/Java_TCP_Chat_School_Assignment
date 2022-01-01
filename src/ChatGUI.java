import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

import javax.swing.*;

public class ChatGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
    private final JTextField m_chatInput;
    private final JTextArea m_chatOutput;

    //Create a GUI with a provided action listener and a window title
    //This means ActionPerformed should be defined when creating ChatGUI instance 
    public ChatGUI(WindowListener wl, ActionListener listener, String userName) {

        setSize(700, 500);
        setTitle("Chat client for " + userName);
        m_chatOutput = new JTextArea(10, 15);
        m_chatInput = new JTextField(20);

        m_chatOutput.setWrapStyleWord(true);
        m_chatOutput.setLineWrap(true);
        m_chatOutput.setEditable(false);
        m_chatOutput.setBackground(Color.BLACK);
        m_chatOutput.setForeground(Color.GREEN);

        Container pane = getContentPane();
        pane.add(m_chatOutput, BorderLayout.NORTH);
        pane.add(m_chatInput, BorderLayout.SOUTH);
        pane.add(new JScrollPane(m_chatOutput), BorderLayout.CENTER);

        m_chatInput.addActionListener(listener);
        this.addWindowListener(wl);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // The action listener is associated with the input field
    public void displayMessage(String message) {
        m_chatOutput.append(message + "\n");
    }

    // Returns the typed text in input field
    public String getInput() {
        return m_chatInput.getText();
    }

    public void clearInput() {
        m_chatInput.setText("");
    }

}