package com.pasquasoft.games.hunter;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * A class that caches <code>Sprite</code> references.
 *
 * @author Pat Paternostro
 * @version v1.0
 * @see Sprite
 */
public final class SpriteCache
{
  /**
   * A singleton reference to this object.
   */
  private static SpriteCache spriteCache;

  /**
   * The sprite cache.
   */
  private Map<String, Sprite> sprites = new HashMap<String, Sprite>();

  /**
   * Prevent instantiation outside of this class.
   */
  private SpriteCache()
  {
  }

  /**
   * Retrieves a singleton reference to this object.
   *
   * @return a singleton reference to this object
   */
  public static synchronized SpriteCache getInstance()
  {
    if (spriteCache == null)
      spriteCache = new SpriteCache();

    return spriteCache;
  }

  /**
   * Retrieves a sprite (from the cache if available).
   *
   * @param ref the sprite's image reference
   * @return a sprite instance
   * @throws IOException if the sprite could not be loaded
   */
  public Sprite getSprite(String ref) throws IOException
  {
    /* Check cache first */
    Sprite sprite = (Sprite) sprites.get(ref);

    /* Load the sprite */
    if (sprite == null)
    {
      BufferedImage sourceImage = null;

      URL url = this.getClass().getClassLoader().getResource(ref);

      if (url == null)
      {
        throw new IOException(ref + " could not be loaded!");
      }

      sourceImage = ImageIO.read(url);

      /* Create an accelerated image of the right size to store our sprite */
      GraphicsConfiguration gc = GraphicsEnvironment
          .getLocalGraphicsEnvironment().getDefaultScreenDevice()
          .getDefaultConfiguration();
      Image image = gc.createCompatibleImage(sourceImage.getWidth(),
          sourceImage.getHeight(), Transparency.BITMASK);

      /* Draw our source image into the accelerated image */
      image.getGraphics().drawImage(sourceImage, 0, 0, null);

      /* Create the sprite */
      sprite = new Sprite(image);

      /* Add to the cache */
      sprites.put(ref, sprite);
    }

    return sprite;
  }
}
