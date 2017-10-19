import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.util.*;

/**
 * A class the defines the attributes and behavior of a canvas.
 * A canvas represents a blank rectangular area of the screen
 * used for drawing.
 *
 * @author    Pat Paternostro
 * @version   v1.0
 */
class GameCanvas extends Canvas implements Runnable
{
  private static final int FPS = 80;            /* Frames per second */
  private static final int PERIOD = 1000/FPS;   /* 1000 ms divided by FPS */

  private Thread thread;

  private Graphics offscreenG;

  private Image offscreenImage;

  private boolean paused;
  private boolean over;

  private int width;
  private int height;

  private ArrayList aliens = new ArrayList();

  /**
   * Constructs a canvas object with the specified
   * dimensions.
   *
   * @param   width the canvas width
   * @param   height the canvas height
   */
  public GameCanvas(int width, int height)
  {
    this.width = width;
    this.height = height;

    /* Set the canvas size */
    setBounds(0,0,width,height);

    addMouseListener(new MouseHandler());
  }

  /**
   * Disposes of the <code>Graphics</code> object
   * associated with this canvas.
   */
  public void disposeGraphics()
  {
    if (offscreenG != null)
      offscreenG.dispose();
  }

  public void addNotify()
  {
    super.addNotify();

    /*
     * Obtain an offscreen image and an offscreen graphics
     * context to reduce screen flicker via double buffering.
     */
    offscreenImage = createImage(width,height);

    /* Get offscreen image graphics context */
    offscreenG = offscreenImage.getGraphics();

    /* Request focus on the canvas */
    requestFocus();
  }

  public void update(Graphics g)
  {
    /* Clear the offscreen graphics context */
    offscreenG.setColor(Color.BLACK);
    offscreenG.fillRect(0,0,width,height);

    /* Draw the aliens */
    Iterator iter = aliens.iterator();

    while (iter.hasNext())
    {
      Entity entity = (Entity) iter.next();

      entity.draw(offscreenG);
    }

    paint(g);

    synchronized(this)
    {
      notifyAll();
    }
  }

  public int getAlienCount()
  {
    return aliens.size();
  }

  public void pause()
  {
    paused = true;
  }

  public void resume()
  {
    paused = false;
  }

  public void paint(Graphics g)
  {
    g.drawImage(offscreenImage,0,0,width,height,this);
  }

  public void start(int alienNumber)
  {
    /* Load the aliens */
    for (int i = 0; i < alienNumber; i++)
    {
      try
      {
        Entity entity =
          new Entity(
            "alien.gif",
            width,
            height);

        aliens.add(entity);
      }
      catch(IOException ioe)
      {
        System.out.println(ioe.getMessage());
      }
    }

    over = false;

    if (thread == null)
    {
      thread = new Thread(this);
      thread.start();
    }
  }

  public void stop()
  {
    thread = null;

    paused = false;

    over = true;

    aliens.clear();
  }

  public synchronized void run()
  {
    long timeDiff = 0L;
    long sleepTime = 0L;
    long beforeTime = 0L;

    while (thread != null)
    {
      beforeTime = System.currentTimeMillis();

      moveAliens();

      repaint();

      timeDiff = System.currentTimeMillis() - beforeTime;
      sleepTime = PERIOD - timeDiff;

      if (sleepTime <= 0)
        sleepTime = 5;

      try
      {
        /* Synchronize threads */
        wait();

        /* Don't hog CPU (Can't we all just get along?) */
        Thread.sleep(sleepTime);
      }
      catch(InterruptedException ie)
      {
      }
    }
  }

  private void moveAliens()
  {
    /* Randomize alien location */
    Iterator iter = aliens.iterator();

    while (iter.hasNext())
    {
      Entity entity = (Entity) iter.next();

      entity.move();
    }
  }

  private class MouseHandler extends MouseAdapter
  {
    public void mousePressed(MouseEvent evt)
    {
      if (!paused && !over)
      {
        /* Remove alien if hit */
        Iterator iter = aliens.iterator();

        while (iter.hasNext())
        {
          Entity entity = (Entity) iter.next();

          if (entity.isHit(evt.getX(),evt.getY()))
          {
            iter.remove();
            break;
          }
        }
      }
    }
  }
}
