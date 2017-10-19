import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;

import java.util.*;

import javax.swing.*;

public class SimpleGame
{
  public static void main(String args[])
  {
    new SimpleGameFrame();
  }
}

class SimpleGameFrame extends JFrame implements ActionListener
{
  private GameCanvas canvas;

  private JMenuBar mb;

  private JMenu file = new JMenu("File");
  private JMenu help = new JMenu("Help");

  private JMenuItem fileStart = new JMenuItem("Start");
  private JMenuItem fileExit = new JMenuItem("Exit");
  private JMenuItem helpAbout = new JMenuItem("About...");

  SimpleGameFrame()
  {
    super();

    /* Components should be added to the container's content pane */
    Container cp = getContentPane();

    /* Add the window listener */
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent evt)
      {
        dispose();
        System.exit(0);
      }
    });

    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    mb = new JMenuBar();

    file.add(fileStart);
    file.addSeparator();
    file.add(fileExit);

    help.add(helpAbout);

    mb.add(file);
    mb.add(help);

    setJMenuBar(mb);

    fileStart.addActionListener(this);
    fileExit.addActionListener(this);
    helpAbout.addActionListener(this);

    /* Set game's resolution */
    ((JPanel) cp).setPreferredSize(new Dimension(800,600));
    ((JPanel) cp).setLayout(null);

    /* Create the game's canvas and add to the frame */
    canvas = new GameCanvas(this);
    ((JPanel) cp).add(canvas);

    /* Size the frame */
    pack();

    /* Don't allow resizing */
    setResizable(false);

    /* Center the frame */
    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
    Rectangle frameDim = getBounds();
    setLocation((screenDim.width - frameDim.width) / 2,(screenDim.height - frameDim.height) / 2);

    /* Show the frame */
    setVisible(true);
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object obj = evt.getSource();

    if (obj == fileStart)
    {
      String command = ((AbstractButton) obj).getText();

      if (command.equals("Start"))
      {
        ((AbstractButton) obj).setText("Stop");
        canvas.start();
      }
      else
      {
        ((AbstractButton) obj).setText("Start");
        canvas.stop();
      }
    }
    else if (obj == fileExit)
    {
      dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    }
    else if (obj == helpAbout)
    {
      JOptionPane.showMessageDialog(this,"<html><center>Pat Paternostro<br>Copyright © 2004</center></html>","About",JOptionPane.INFORMATION_MESSAGE);
    }
  }
}

class GameCanvas extends Canvas implements Runnable
{
  private static final int WIDTH = 20;
  private static final int HEIGHT = 20;
  private static final int INCREMENT = 5;

  private JFrame owner;

  private BufferStrategy strategy;

  private Thread thread;

  private boolean leftPressed;
  private boolean rightPressed;
  private boolean firePressed;

  private int x = 400;
  private int y = 300;

  private ArrayList aliens = new ArrayList(10);

  public GameCanvas(JFrame owner)
  {
    this.owner = owner;

    /* Set the canvas size */
    setBounds(0,0,800,600);

    /* We will handle repainting */
    setIgnoreRepaint(true);

    //addKeyListener(new KeyHandler());

    addMouseListener(new MouseHandler());
  }

  public void addNotify()
  {
    super.addNotify();

    /*
     * To create a buffer strategy a component must have
     * a valid peer. A component's peer is created when
     * its addNotify() method is called.
     */
    createBufferStrategy(2);
    strategy = getBufferStrategy();

    /* Request focus on the canvas */
    requestFocus();
  }

  public void start()
  {
    Graphics g = strategy.getDrawGraphics();

    /* Clear the screen */
    g.setColor(Color.BLACK);
    g.fillRect(0,0,800,600);

    /* Draw the ball */
    //g.setColor(Color.BLUE);
    //g.drawOval(x,y,WIDTH,HEIGHT);

    /* Clear graphics and flip buffer */
    g.dispose();
    strategy.show();

    if (thread == null)
    {
      thread = new Thread(this);
      thread.start();
    }
  }

  public void stop()
  {
    thread = null;

    x = 400;
    y = 300;
  }

  public void run()
  {
    /* Load the aliens */
    for (int i = 0; i < 10; i++)
    {
      try
      {
        Entity entity = new Entity("alien.gif",(int) (Math.random() * 779), (int) (Math.random() * 579));

        aliens.add(entity);
      }
      catch(IOException ioe)
      {
        System.out.println(ioe.getMessage());
      }
    }

    while (thread != null)
    {
      Graphics g = strategy.getDrawGraphics();

      /* Clear the screen */
      g.setColor(Color.BLACK);
      g.fillRect(0,0,800,600);

      /* Draw the aliens */
      Iterator iter = aliens.iterator();

      while (iter.hasNext())
      {
        Entity entity = (Entity) iter.next();
        entity.setX((int) (Math.random() * 779));
        entity.setY((int) (Math.random() * 579));
        entity.draw(g);
      }

      /* Draw the ball */
      //g.setColor(Color.BLUE);
      //g.fillOval(x,y,WIDTH,HEIGHT);

      /* Clear graphics and flip buffer */
      g.dispose();
      strategy.show();

      try
      {
        Thread.sleep(500);
      }
      catch(InterruptedException ie)
      {
      }
    }
  }

  private class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent evt)
    {
      /* If mouse click hit alien remove it */
      Iterator iter = aliens.iterator();

      while (iter.hasNext())
      {
        Entity entity = (Entity) iter.next();

        if (entity.isHit(evt.getX(),evt.getY()))
        {
          System.out.println("HIT!");
          iter.remove();
          break;
        }
      }

    }
  }

  private class KeyHandler extends KeyAdapter
  {
    public void keyPressed(KeyEvent evt)
    {
      int keyCode = evt.getKeyCode();
      int temp = 0;

      switch(keyCode)
      {
        case KeyEvent.VK_LEFT:
          x = (temp = x - INCREMENT) < 1 ? x : temp;
          leftPressed = true;
          break;
        case KeyEvent.VK_RIGHT:
          x = (temp = x + INCREMENT) > 779 ? x : temp;
          rightPressed = true;
          break;
        case KeyEvent.VK_UP:
          y = (temp = y - INCREMENT) < 1 ? y : temp;
          break;
        case KeyEvent.VK_DOWN:
          y = (temp = y + INCREMENT) > 579 ? y : temp;
          break;
        case KeyEvent.VK_SPACE:
          firePressed = true;
          break;
      }
    }

    public void keyReleased(KeyEvent evt)
    {
      int keyCode = evt.getKeyCode();

      switch(keyCode)
      {
        case KeyEvent.VK_LEFT:
          leftPressed = false;
          break;
        case KeyEvent.VK_RIGHT:
          rightPressed = false;
          break;
        case KeyEvent.VK_UP:
          break;
        case KeyEvent.VK_DOWN:
          break;
        case KeyEvent.VK_SPACE:
          firePressed = false;
          break;
      }
    }

    public void keyTyped(KeyEvent evt)
    {
      int keyChar = evt.getKeyChar();

      if (keyChar == 27)
      {
        owner.dispatchEvent(new WindowEvent(owner,WindowEvent.WINDOW_CLOSING));
      }
    }
  }
}
