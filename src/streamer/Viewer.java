/**
 * iesiyok,
 * Viewer is the panel which is showing the image file in Jframe
 */

package streamer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.swing.JPanel;

public class Viewer extends JPanel
{
  private static final long serialVersionUID = 1L;
  private BufferedImage image;

  private int[] toIntArray(byte[] barr)
  {
    int[] result = new int[barr.length];
    for (int i = 0; i < barr.length; i++) result[i] = barr[i];
    return result;
  }
  
  public Viewer(int width, int height) {
    this.image = new BufferedImage(width, height, 5);
  }

  public void ViewerInput(byte[] image_bytes, int width, int height) {
    WritableRaster raster = this.image.getRaster();
    raster.setPixels(0, 0, width, height, toIntArray(image_bytes));
  }

  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    g.drawImage(this.image, 0, 0, null);
  }
}