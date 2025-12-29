package com.pasquasoft.games.hunter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;

public class GameOptionsDialog extends JDialog implements ActionListener
{
  /**
   * Generated serial version UID.
   */
  private static final long serialVersionUID = -3906539045865403753L;

  private static final String TIME_REGEX = "^(?:[012345]\\d):(?:[012345]\\d)$";

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
    super(owner, "Game Options", true);

    try
    {
      timeMask = new MaskFormatter("##:##");
      timeMask.setPlaceholderCharacter('0');
      timeLimit = new JFormattedTextField(timeMask);
    }
    catch (ParseException pe)
    {
    }

    /* Components should be added to the container's content pane */
    Container cp = getContentPane();

    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 3, 3, 3);

    center.setLayout(gbl);
    center.add(new JLabel("Number of aliens: "), constraintsHelper(gbc, 0, 0));
    center.add(aliens, constraintsHelper(gbc, 1, 0));
    center.add(new JLabel("Game time limit: "), constraintsHelper(gbc, 0, 1));
    center.add(timeLimit, constraintsHelper(gbc, 1, 1));

    south.add(save);
    south.add(cancel);

    cp.add(BorderLayout.CENTER, center);
    cp.add(BorderLayout.SOUTH, south);

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
            super.insertString(offset, str, as);
        }
        catch (NumberFormatException nfe)
        {
        }
      }
    });

    preferences = Preferences.userNodeForPackage(AlienHunter.class);

    aliens.setText(preferences.get("AlienHunter.aliens", "10"));
    timeLimit.setValue(preferences.get("AlienHunter.timeLimit", "01:00"));

    /* Add the window listener */
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt)
      {
        dispose();
      }
    });

    /* Size the dialog */
    pack();

    /* Don't allow resize */
    setResizable(false);

    /* Center the dialog */
    setLocationRelativeTo(owner);

    /* Show the dialog */
    setVisible(true);
  }

  private Object constraintsHelper(GridBagConstraints gbc, int x, int y)
  {
    gbc.gridx = x;
    gbc.gridy = y;

    return gbc;
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object obj = evt.getSource();

    if (obj == save)
    {
      String aliensText = aliens.getText();
      String timeLimitText = timeLimit.getText();

      if (timeLimitText.matches(TIME_REGEX) && !timeLimitText.equals("00:00") && !aliensText.equals("0")
          && !aliensText.equals("00") && !aliensText.equals(""))
      {
        /* Save preferences */
        preferences.put("AlienHunter.aliens", aliensText);
        preferences.put("AlienHunter.timeLimit", timeLimitText);

        try
        {
          preferences.flush();
        }
        catch (BackingStoreException bse)
        {
          JOptionPane.showMessageDialog(this, "<html><center>" + bse.getMessage() + "</center></html>", "Error",
              JOptionPane.ERROR_MESSAGE);
        }

        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
      }
      else
      {
        JOptionPane.showMessageDialog(this, "Invalid aliens or time limit!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if (obj == cancel)
    {
      dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
  }
}
