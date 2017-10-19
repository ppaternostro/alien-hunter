import java.awt.*;
import java.awt.event.*;

import java.text.*;

import java.util.prefs.*;

import javax.swing.*;
import javax.swing.text.*;

public class GameOptionsDialog extends JDialog implements ActionListener
{
  private JPanel center = new JPanel();
  private JPanel south = new JPanel();

  private JTextField aliens = new JTextField(20);

  private JButton save = new JButton("Save");
  private JButton cancel = new JButton("Cancel");

  private MaskFormatter timeMask;

  private JFormattedTextField timeLimit;

  private Preferences preferences;

  public GameOptionsDialog(JFrame owner)
  {
    super(owner,"Game Options",true);

    try
    {
      timeMask = new MaskFormatter("##:##");
      timeMask.setPlaceholderCharacter('0');
      timeLimit = new JFormattedTextField(timeMask);
    }
    catch(ParseException pe)
    {
    }

    /* Components should be added to the container's content pane */
    Container cp = getContentPane();

    center.setLayout(new GridLayout(3,2));
    south.setLayout(new FlowLayout());

    center.add(new JLabel("Number of aliens: "));
    center.add(aliens);
    center.add(new JLabel("Game time limit: "));
    center.add(timeLimit);

    south.add(save);
    south.add(cancel);

    cp.add(BorderLayout.CENTER,center);
    cp.add(BorderLayout.SOUTH,south);

    /* Add action listeners */
    save.addActionListener(this);
    cancel.addActionListener(this);

    aliens.setDocument(new PlainDocument() {
      public void insertString(int offset, String str, AttributeSet as) throws BadLocationException
      {
        try
        {
          Integer.parseInt(str);

          /* Don't exceed limit */
          int txtLen = aliens.getText().length();

          if (str.length() + txtLen <= 2)
            super.insertString(offset,str,as);
        }
        catch(NumberFormatException nfe)
        {
        }
      }
    });


    preferences = Preferences.systemRoot();

    aliens.setText(preferences.get("AlienHunter.aliens","10"));
    timeLimit.setValue(preferences.get("AlienHunter.timeLimit","01:00"));

    /* Add the window listener */
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent evt)
      {
        dispose();
      }
    });

    /* Size the dialog */
    setSize(250,140);

    /* Don't allow resize */
    setResizable(false);

    /* Center the dialog */
    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
    Rectangle frameDim = getBounds();
    setLocation((screenDim.width - frameDim.width) / 2,(screenDim.height - frameDim.height) / 2);

    /* Show the dialog */
    setVisible(true);
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object obj = evt.getSource();

    if (obj == save)
    {
      /* Save preferences */
      preferences.put("AlienHunter.aliens",aliens.getText());
      preferences.put("AlienHunter.timeLimit",timeLimit.getText());

      try
      {
        preferences.flush();
      }
      catch(BackingStoreException bse)
      {
        JOptionPane.showMessageDialog(
          this,
          "<html><center>" + bse.getMessage()  + "</center></html>",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      }

      dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    }
    else if (obj == cancel)
    {
      dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    }
  }
}
