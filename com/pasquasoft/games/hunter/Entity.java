package com.pasquasoft.games.hunter;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;

public class Entity
{
  private static final int X_INCREMENT_SIZE = 10;
  private static final int Y_INCREMENT_SIZE = 10;

  private int xIncrement = X_INCREMENT_SIZE;
  private int yIncrement = Y_INCREMENT_SIZE;

  private Sprite sprite;

  private Rectangle rectangle;

  private int boundsWidth;
  private int boundsHeight;

  private int spriteWidth;
  private int spriteHeight;

  public Entity(String ref, int boundsWidth, int boundsHeight)
      throws IOException
  {
    int derivedX = (int) (Math.random() * boundsWidth);
    int derivedY = (int) (Math.random() * boundsHeight);

    this.boundsWidth = boundsWidth;
    this.boundsHeight = boundsHeight;

    sprite = SpriteCache.getInstance().getSprite(ref);

    spriteWidth = sprite.getWidth();
    spriteHeight = sprite.getHeight();

    derivedX = derivedX + spriteWidth > boundsWidth
        ? derivedX - spriteWidth
        : derivedX;
    derivedY = derivedY + spriteHeight > boundsHeight
        ? derivedY - spriteHeight
        : derivedY;

    rectangle = new Rectangle(derivedX, derivedY, spriteWidth, spriteHeight);
  }

  public void draw(Graphics g)
  {
    sprite.draw(g, rectangle.x, rectangle.y);
  }

  public void move()
  {
    int derivedX = rectangle.x + xIncrement;
    int derivedY = rectangle.y + yIncrement;

    if (derivedX <= 0)
    {
      rectangle.x = 0;
      xIncrement = X_INCREMENT_SIZE;
    }
    else
    {
      if (derivedX + spriteWidth > boundsWidth)
      {
        rectangle.x = boundsWidth - spriteWidth;
        xIncrement = -X_INCREMENT_SIZE;
      }
      else
      {
        rectangle.x += xIncrement;
      }
    }

    if (derivedY <= 0)
    {
      rectangle.y = 0;
      yIncrement = Y_INCREMENT_SIZE;
    }
    else
    {
      if (derivedY + spriteHeight > boundsHeight)
      {
        rectangle.y = boundsHeight - spriteHeight;
        yIncrement = -Y_INCREMENT_SIZE;
      }
      else
      {
        rectangle.y += yIncrement;
      }
    }
  }

  public boolean isHit(int x, int y)
  {
    return rectangle.contains(x, y);
  }
}
