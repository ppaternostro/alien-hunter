package com.pasquasoft.games.hunter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class AlienHunterFrame extends JFrame implements ActionListener
{
  /**
   * Generated serial version UID.
   */
  private static final long serialVersionUID = 454288561925308849L;

  private GameCanvas canvas;

  private JMenuBar mb;

  private boolean timedOut;

  private Timer gameTimer;
  private Timer statusTimer;

  private long gameTimeLimit;
  private long gameDuration;
  private long timeLeft;

  private JMenu game = new JMenu("Game");
  private JMenu help = new JMenu("Help");

  private JMenuItem gameStart = new JMenuItem("Start");
  private JMenuItem gameStop = new JMenuItem("Stop");
  private JMenuItem gamePause = new JMenuItem("Pause");
  private JMenuItem gameResume = new JMenuItem("Resume");
  private JMenuItem gameOptions = new JMenuItem("Options...");
  private JMenuItem gameExit = new JMenuItem("Exit");
  private JMenuItem helpAbout = new JMenuItem("About...");

  private StatusPanel statusBar = new StatusPanel(new String[] { "Time Limit: 00:00", "Aliens: 0" });

  private Preferences preferences = Preferences.systemRoot();

  private JPanel center = new JPanel();

  public AlienHunterFrame()
  {
    super("Alien Hunter");

    /* Components should be added to the container's content pane */
    Container cp = getContentPane();

    /* Add the window listener */
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt)
      {
        dispose();

        canvas.disposeGraphics();

        System.exit(0);
      }

      public void windowIconified(WindowEvent evt)
      {
        gamePause.doClick();
      }

      public void windowDeiconified(WindowEvent evt)
      {
        gameResume.doClick();
      }

      public void windowActivated(WindowEvent evt)
      {
        gameResume.doClick();
      }

      public void windowDeactivated(WindowEvent evt)
      {
        gamePause.doClick();
      }
    });

    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    mb = new JMenuBar();

    game.add(gameStart);
    game.add(gameResume);
    game.addSeparator();
    game.add(gameStop);
    game.add(gamePause);
    game.addSeparator();
    game.add(gameOptions);
    game.addSeparator();
    game.add(gameExit);

    help.add(helpAbout);

    mb.add(game);
    mb.add(help);

    setJMenuBar(mb);

    gameStart.addActionListener(this);
    gameStop.addActionListener(this);
    gamePause.addActionListener(this);
    gameResume.addActionListener(this);
    gameOptions.addActionListener(this);
    gameExit.addActionListener(this);
    helpAbout.addActionListener(this);

    gamePause.setEnabled(false);
    gameResume.setEnabled(false);
    gameStop.setEnabled(false);

    /* Create the game's canvas */
    canvas = new GameCanvas(800, 600);

    /* Set the center panel's resolution and add the canvas */
    center.setPreferredSize(new Dimension(800, 600));
    center.setLayout(null);
    center.add(canvas);

    cp.add(BorderLayout.CENTER, center);
    cp.add(BorderLayout.SOUTH, statusBar);

    /* Size the frame */
    pack();

    /* Don't allow resizing */
    setResizable(false);

    /* Center the frame */
    setLocationRelativeTo(null);

    /* Show the frame */
    setVisible(true);
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object obj = evt.getSource();

    if (obj == gameStart)
    {
      timedOut = false;

      gameStart.setEnabled(false);
      gameStop.setEnabled(true);
      gamePause.setEnabled(true);

      canvas.start(gameAliens());

      gameTimer = new java.util.Timer();
      gameTimer.schedule(new GameTask(),
          new Date(gameDuration = System.currentTimeMillis() + (gameTimeLimit = gameTimeLimit())));

      statusTimer = new java.util.Timer();
      statusTimer.scheduleAtFixedRate(new StatusTask(), 0, 1000);
    }
    else if (obj == gameStop)
    {
      gameStop.setEnabled(false);
      gamePause.setEnabled(false);
      gameResume.setEnabled(false);
      gameStart.setEnabled(true);

      canvas.stop();

      statusTimer.cancel();

      if (!timedOut)
      {
        /* Game was won or stopped before time expired */
        gameTimer.cancel();

        /* Reset the status bar */
        statusBar.setStatusSection(0, "Time Limit: 00:00");
        statusBar.setStatusSection(1, "Aliens: 0");
      }
    }
    else if (obj == gamePause)
    {
      gamePause.setEnabled(false);
      gameResume.setEnabled(true);

      /* Cancel timers */
      gameTimer.cancel();
      statusTimer.cancel();

      /* Determine time left on current game */
      timeLeft = gameDuration - System.currentTimeMillis();

      /* Pause the game */
      canvas.pause();
    }
    else if (obj == gameResume)
    {
      gameResume.setEnabled(false);
      gamePause.setEnabled(true);

      /* Resume the game */
      canvas.resume();

      /* Canceled timers cannot schedule new tasks */
      gameTimer = new java.util.Timer();
      gameTimer.schedule(new GameTask(), new Date(gameDuration = System.currentTimeMillis() + timeLeft));

      statusTimer = new java.util.Timer();
      statusTimer.scheduleAtFixedRate(new StatusTask(), 0, 1000);
    }
    else if (obj == gameOptions)
    {
      new GameOptionsDialog(this);
    }
    else if (obj == gameExit)
    {
      dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    else if (obj == helpAbout)
    {
      JOptionPane.showMessageDialog(this,
          "<html><center>Alien Hunter<br>Pat Paternostro<br>Copyright &copy; 2004</center></html>", "About",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  private int gameAliens()
  {
    return Integer.parseInt(preferences.get("AlienHunter.aliens", "10"));
  }

  private long gameTimeLimit()
  {
    String timeLimit = preferences.get("AlienHunter.timeLimit", "01:00");

    /* Parse the string */
    String parts[] = timeLimit.split(":");

    /* Convert parts to milliseconds */
    int minutes = Integer.parseInt(parts[0]) * 60 * 1000;
    int seconds = Integer.parseInt(parts[1]) * 1000;

    return minutes + seconds;
  }

  private class GameTask extends TimerTask
  {
    public void run()
    {
      timedOut = true;

      AlienHunterFrame.this.gameStop.doClick();

      JOptionPane.showMessageDialog(AlienHunterFrame.this, "Better luck next time!", "Alien Hunter",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  private class StatusTask extends TimerTask
  {
    public void run()
    {
      long minutes = gameTimeLimit / 1000 / 60;
      long seconds = gameTimeLimit / 1000 % 60;

      /* Update the status area */
      statusBar.setStatusSection(0, "Time Limit: " + (minutes < 10 ? "0" + minutes : "" + minutes) + ":"
          + (seconds < 10 ? "0" + seconds : "" + seconds));

      statusBar.setStatusSection(1, "Aliens: " + canvas.getAlienCount());

      gameTimeLimit -= 1000;

      if (AlienHunterFrame.this.canvas.getAlienCount() == 0 && gameTimeLimit > 0)
      {
        AlienHunterFrame.this.gameStop.doClick();

        JOptionPane.showMessageDialog(AlienHunterFrame.this, "Congratulations! You saved the universe!", "Alien Hunter",
            JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }
}
