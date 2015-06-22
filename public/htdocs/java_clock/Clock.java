/*
 * @(#)Clock2.java	1.8 97/01/24
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

// author: Rachel Gollub, 1995
// modified 96/04/24 Jim Hagen : use getBackground()
// modified 96/05/29 Rachel Gollub : add garbage collecting
// modified 96/10/22 Rachel Gollub : add bgcolor, fgcolor1, fgcolor2 params
// modified 96/12/05 Rachel Gollub : change copyright and Date methods
// Time!

// modified 97/07/22 Bert Bos: made into Component instead of Applet
// modified 11/03/21 Bert Bos: added a package to make it compile with Java6

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.text.*;

public class Clock extends Panel implements Runnable, ComponentListener
{

  private static final int MINDIST = 49000;
  private static final float MIND = 0.4f;
  private static final double TH = 0.18;

  private Thread timer;
  private int lastxs = 0, lastys = 0;		// Seconds hand
  private int lastxm = 0, lastym = 0;		// Minutes hand
  private int lastxd = 0, lastyd = 0;		// Date string
  private int lastxt = 0, lastyt = 0;		// Time string
  private int[] lastxh = {0, 0, 0, 0}, lastyh = {0, 0, 0, 0}; // Hour hand
  private int lastsec = -1;
  private int lasth = -1;
  private GregorianCalendar cal;
  private String lastdate = "", lasttime = "";
  private Font F;		// Will be initialized by componentResized()
  private FontMetrics fmetrics;
  private Color fgcol = Color.blue;
  private Color fgcol2 = Color.darkGray;
  private Color bgcol1 = Color.white;
  private Color bgcol2 = Color.black;
  private DateFormat dfdate, dftime;
  private Color[] dialcolor = new Color[96];
  private Color[] mhandcolor = new Color[96];
  private Color[] shandcolor = new Color[96];
  private boolean doSeconds;
  private int sleeptime;
  private Locale locale;
  private int style;
  private int digitwd;
  private int ascent;
  private int fsize;


  public Clock()
  {
    this(TimeZone.getDefault(), Locale.getDefault());
  }

  public Clock(TimeZone timezone, Locale locale)
  {
    addComponentListener(this);
    cal = new GregorianCalendar(timezone);
    this.locale = locale;
    setDateStyle(DateFormat.MEDIUM);
    setSecondsDisplay(true);
    makeRamp();
    timer = new Thread(this);
    timer.start();
  }

  /**
   * Set style of date display: 0 = full, 1 = long, 2 = medium, 3 = short
   **/
  public void setDateStyle(int style)
  {
    this.style = style;
    dfdate = DateFormat.getDateInstance(style, locale);
    dfdate.setCalendar(cal);
  }

  /**
   * Turn display of seconds and seconds hand on or off.
   * Turning it on may free up some resources, since the
   * clock thread will be sleeping for a larger fraction
   * of the time.
   **/
  public void setSecondsDisplay(boolean onoff)
  {
    doSeconds = onoff;
    sleeptime = doSeconds ? 200 : 12000;
    dftime = DateFormat.getTimeInstance
      (doSeconds ? DateFormat.MEDIUM : DateFormat.SHORT, locale);
    dftime.setCalendar(cal);
  }

  /**
   * Set the clock to a certain time zone. Example call:
   * <pre>
   * clock.setTimeZone(TimeZone.getTimeZone(tz));
   * </pre>
   * @param tz the time zone
   **/
  public void setTimeZone(TimeZone tz)
  {
    cal = new GregorianCalendar(tz);
    dfdate.setCalendar(cal);
    dftime.setCalendar(cal);
  }

  /**
   * Set the font for the 3, 6, 9, 12 markes and the digital time
   * string. There are currently some bugs, because the size of the
   * font is assumed to be close to 12 or 14 pt. This should really
   * be fixed...
   * @param f the font to use
   **/
  public void setFont(Font f) {
    F = f;
    fmetrics = getFontMetrics(F);
    digitwd = fmetrics.charWidth('8');
    ascent = fmetrics.getAscent();
    fsize = ascent + fmetrics.getDescent();
    super.repaint();
  }

  /**
   * Set the color of the minute and hour hand,
   * when the background is the "day color". The color for the
   * nightly background will be automatically derived from this
   * to keep the contrast high enough.
   * @param c the color for the clock, except the second hand
   **/
  public void setClockColor(Color c) {fgcol = c;}

  /**
   * Set the color for the text and the seconds hand. This color
   * is used when the background is the day color. The nightly
   * color will be derived from this to keep the contrast high
   * enough.
   * @param c the color for the text and the seconds hand
   **/
  public void setTextColor(Color c) {fgcol2 = c;}

  /**
   * Set the color for the dial's background during the day.  The
   * color of the dial will be interpolated in hourly steps between
   * the day color (at noon) and the night color (at midnight).
   * @param c the color of the dial at noon
   **/
  public void setDayColor(Color c) {bgcol1 = c; makeRamp();}

  /**
   * Set the color for the dial's background during the night.  The
   * color of the dial will be interpolated in hourly steps between
   * the day color (at noon) and the night color (at midnight).
   * @param c the color of the dial at midnight
   **/
  public void setNightColor(Color c) {bgcol2 = c; makeRamp();}


  /**
   * Create a color ramp of 24 colors from midnight to the next
   * midnight. There are twelve steps from midnight (night color) to
   * noon (day color) and the same twelve steps back again.
   **/
  private void makeRamp()
  {
    int dayr = bgcol1.getRed();
    int dayg = bgcol1.getGreen();
    int dayb = bgcol1.getBlue();
    int nightr = bgcol2.getRed();
    int nightg = bgcol2.getGreen();
    int nightb = bgcol2.getBlue();
    int mhandr = fgcol.getRed();
    int mhandg = fgcol.getGreen();
    int mhandb = fgcol.getBlue();
    int shandr = fgcol2.getRed();
    int shandg = fgcol2.getGreen();
    int shandb = fgcol2.getBlue();

    for (int i = 0; i < 48; i++) {
      // Interpolate between day and night
      int r = nightr + i * (dayr - nightr)/48;
      int g = nightg + i * (dayg - nightg)/48;
      int b = nightb + i * (dayb - nightb)/48;
      dialcolor[i] = dialcolor[95-i] = new Color(r, g, b);
      // Ensure minimum distance between dial color and hands
      if ((mhandr - r) * (mhandr - r) + (mhandg - g) * (mhandg - g) +
	  (mhandb - b) * (mhandb - b) < MINDIST)
	mhandcolor[i] = mhandcolor[95-i] =
	  new Color(255 - mhandr, 255 - mhandg, 255 - mhandb);
      else
	mhandcolor[i] = mhandcolor[95-i] = fgcol;
      // Ensure minimum distance between dial color and seconds hand
      if ((shandr - r) * (shandr - r) + (shandg - g) * (shandg - g) +
	  (shandb - b) * (shandb - b) < MINDIST)
	shandcolor[i] = shandcolor[95-i] =
	  new Color(255 - shandr, 255 - shandg, 255 - shandb);
      else
	shandcolor[i] = shandcolor[95-i] = fgcol2;

//    float[] day = Color.RGBtoHSB(bgcol1.getRed(), bgcol1.getGreen(),
//				 bgcol1.getBlue(), null);
//    float[] night = Color.RGBtoHSB(bgcol2.getRed(), bgcol2.getGreen(),
//				   bgcol2.getBlue(), null);
//    float[] mhand = Color.RGBtoHSB(fgcol.getRed(), fgcol.getGreen(),
//				   fgcol.getBlue(), null);
//    float[] shand = Color.RGBtoHSB(fgcol2.getRed(), fgcol2.getGreen(),
//				   fgcol2.getBlue(), null);
//    for (int i = 0; i < 12; i++) {
//      // Interpolate dial color
//      float h = night[0] + i/12.0f * (day[0] - night[0]);
//      float s = night[1] + i/12.0f * (day[1] - night[1]);
//      float b = night[2] + i/12.0f * (day[2] - night[2]);
//      dialcolor[i] = dialcolor[23-i] = Color.getHSBColor(h, s, b);
//      // Make sure contrast between dial and minute hand is >0.3
//      System.err.print(i + " (" + h + "," + s + "," + b + ") (");
//      System.err.print(mhand[0] + "," + mhand[1] + "," + mhand[2] + ") ");
//      System.err.print((b - mhand[2]) * (b - mhand[2]));
//      System.err.print(" (");
//      System.err.print(shand[0] + "," + shand[1] + "," + shand[2] + ") ");
//      if ((b - mhand[2]) * (b - mhand[2]) > MIND)
//	mhandcolor[i] = mhandcolor[23-i] =
//	  Color.getHSBColor(mhand[0], mhand[1], 1.0f - mhand[2]);
//      else
//	mhandcolor[i] = mhandcolor[23-i] = fgcol;
//      // Make sure contrast between dial and seconds hand is >0.3
//      System.err.print((b - shand[2]) * (b - shand[2]));
//      System.err.println();
//      if ((b - shand[2]) * (b - shand[2]) > MIND)
//	shandcolor[i] = shandcolor[23-i] =
//	  Color.getHSBColor(shand[0], shand[1], 1.0f - shand[2]);
//      else
//	shandcolor[i] = shandcolor[23-i] = fgcol2;
    }
  }

  /**
   * Plotpoints allows calculation to only cover 45 degrees of the circle,
   * and then mirror
   **/
  private void plotpoints(int x0, int y0, int x, int y, Graphics g)
  {
    g.drawLine(x0 - x, y0 + y, x0 + x, y0 + y);
    g.drawLine(x0 - y, y0 + x, x0 + y, y0 + x);
    g.drawLine(x0 - x, y0 - y, x0 + x, y0 - y);
    g.drawLine(x0 - y, y0 - x, x0 + y, y0 - x);

    //g.drawLine(x0 + x, y0 + y, x0 + x, y0 + y);
    //g.drawLine(x0 + y, y0 + x, x0 + y, y0 + x);
    //g.drawLine(x0 + y, y0 - x, x0 + y, y0 - x);
    //g.drawLine(x0 + x, y0 - y, x0 + x, y0 - y);
    //g.drawLine(x0 - x, y0 - y, x0 - x, y0 - y);
    //g.drawLine(x0 - y, y0 - x, x0 - y, y0 - x);
    //g.drawLine(x0 - y, y0 + x, x0 - y, y0 + x);
    //g.drawLine(x0 - x, y0 + y, x0 - x, y0 + y);
  }

  /**
   * Circle is just Bresenham's algorithm for a scan converted circle
   **/
  public void circle(int x0, int y0, int r, Graphics g)
  {
    int x, y;
    float d;

    x = 0;
    y = r;
    d = 5/4 - r;
    plotpoints(x0, y0, x, y, g);

    while (y > x) {
      if (d < 0) {
	d = d + 2 * x + 3;
	x++;
      } else {
	d = d + 2 * (x - y) + 5;
	x++;
	y--;
      }
      plotpoints(x0, y0, x, y, g);
    }
  }

  /**
   * Paint the clock, repainting everything, not just what changed
   * since the last call to paint.
   **/
  public void paint(Graphics g)
  {
    paint(g, false);
  }

  /**
   * Draw the dial, hands and digital time string. If diffonly is true,
   * only the differences between this call and the previous one are
   * painted, otherwise everything is painted. In the former case the
   * dial is not repainted, in the latter case it is.
   **/
  private void paint(Graphics g, boolean diffonly)
  {
    int xm, ym, xs, ys, s, m, h, xcenter, ycenter, r, r2, rs, rm, rh, xd, xt;
    String todaydate, todaytime;
    Dimension size;
    Date now = new Date();
    int[] xh = new int[4], yh = new int[4];
    int colorix;

    cal.setTime(now);
    s = (int)cal.get(Calendar.SECOND);
    m = (int)cal.get(Calendar.MINUTE);
    h = (int)cal.get(Calendar.HOUR_OF_DAY);
    colorix = 4 * h + m/15;
    todaydate = dfdate.format(now);
    todaytime = dftime.format(now);
    size = getSize();
    r = size.width < size.height ? size.width/2 - 2 : size.height/2 - 2;
    xcenter = size.width/2;
    ycenter = r + 1;
    rs = 8 * r/9;
    rm = 8 * rs/9;
    rh = 6 * rm/9;
  
    // a = s* pi/2 - pi/2 (to switch 0,0 from 3:00 to 12:00)
    // x = r(cos a) + xcenter, y = r(sin a) + ycenter

    // seconds hand
    xs = (int)(Math.cos(s * 3.14f/30 - 3.14f/2) * rs + xcenter);
    ys = (int)(Math.sin(s * 3.14f/30 - 3.14f/2) * rs + ycenter);
    // minute hand
    xm = (int)(Math.cos(m * 3.14f/30 - 3.14f/2) * rm + xcenter);
    ym = (int)(Math.sin(m * 3.14f/30 - 3.14f/2) * rm + ycenter);
    // hour hand
    xh[0] = (int)(Math.cos((h*30 + m/2) * 3.14f/180 - 3.14f/2) * rh +
		  xcenter + 0.5);
    yh[0] = (int)(Math.sin((h*30 + m/2) * 3.14f/180 - 3.14f/2) * rh +
		  ycenter + 0.5);
    xh[1] = (int)(Math.cos((h*30 + m/2) * 3.14f/180 - 3.14f/2 + TH) * rh/3 +
		  xcenter + 0.5);
    yh[1] = (int)(Math.sin((h*30 + m/2) * 3.14f/180 - 3.14f/2 + TH) * rh/3 +
		  ycenter + 0.5);
    xh[2] = xcenter;
    yh[2] = ycenter;
    xh[3] = (int)(Math.cos((h*30 + m/2) * 3.14f/180 - 3.14f/2 - TH) * rh/3 +
		  xcenter + 0.5);
    yh[3] = (int)(Math.sin((h*30 + m/2) * 3.14f/180 - 3.14f/2 - TH) * rh/3 +
		  ycenter + 0.5);

    g.setFont(F);

    // If dial changed, force full redraw
    if (colorix != lasth) {
      diffonly = false;
      lasth = colorix;
    }

    // Erase and redraw the digital time
    if (lastsec != s) {
      g.setColor(getBackground());
      g.drawString(lastdate, lastxd, 2 * r + 10 + fsize);
      g.drawString(lasttime, lastxt, 2 * r + 10 + fsize + fsize);
      g.setColor(fgcol2);
      xd = r + 2 - fmetrics.stringWidth(todaydate)/2;
      xt = r + 2 - fmetrics.stringWidth(todaytime)/2;
      g.drawString(todaydate, xd, 2 * r + 10 + fsize);
      g.drawString(todaytime, xt, 2 * r + 10 + fsize + fsize);
      lastdate = todaydate;
      lasttime = todaytime;
      lastsec = s;
      lastxd = xd;
      lastxt = xt;
    }
    if (diffonly) {				// update, rather than paint
      // Erase old seconds hand
      if (doSeconds && (xs != lastxs || ys != lastys)) {
	g.setColor(dialcolor[colorix]);
	g.drawLine(xcenter, ycenter, lastxs, lastys);
      }
      // Erase old minute hand
      if (xm != lastxm || ym != lastym) {
	g.setColor(dialcolor[colorix]);
	g.drawLine(xcenter, ycenter, lastxm, lastym);
	g.drawLine(xcenter + 1, ycenter - 1, lastxm, lastym);
	g.drawLine(xcenter - 1, ycenter + 1, lastxm, lastym);
	g.drawLine(xcenter - 1, ycenter - 1, lastxm, lastym);
	g.drawLine(xcenter + 1, ycenter + 1, lastxm, lastym);
      }
      // Erase old hour hand
      if (xh[0] != lastxh[0] || yh[0] != lastyh[0]) {
	g.setColor(dialcolor[colorix]);
	g.fillPolygon(lastxh, lastyh, 4);
      }
    } else {					// paint, rather than update
      // Redraw circle
      g.setColor(dialcolor[colorix] /*fgcol*/);
      circle(xcenter, ycenter, r, g);
    }
    // Redraw digits
    r2 = r - (ascent/2 > digitwd ? ascent/2 : digitwd);
    g.setColor(shandcolor[colorix] /*fgcol2*/);
    g.drawString("1", xcenter + r2/2 - digitwd/2, ycenter - 87 * r2/100 + ascent/2);
    g.drawString("2", xcenter + 87*r2/100 - digitwd/2, ycenter - r2/2 + ascent/2);
    g.drawString("3", xcenter + r2 - digitwd/2, ycenter + ascent/2);
    g.drawString("4", xcenter + 87*r2/100 - digitwd/2, ycenter + r2/2 + ascent/2);
    g.drawString("5", xcenter + r2/2 - digitwd/2, ycenter + 87 * r2/100 + ascent/2);
    g.drawString("6", xcenter - digitwd/2, ycenter + r2 + ascent/2);
    g.drawString("7", xcenter - r2/2 - digitwd/2, ycenter + 87 * r2/100 + ascent/2);
    g.drawString("8", xcenter - 87 * r2/100 - digitwd/2, ycenter + r2/2 + ascent/2);
    g.drawString("9", xcenter - r2 - digitwd/2, ycenter + ascent/2);
    g.drawString("10", xcenter - 87 * r2/100 - digitwd, ycenter - r2/2 + ascent/2);
    g.drawString("11", xcenter - r2/2 - digitwd, ycenter - 87 * r2/100 + ascent/2);
    g.drawString("12", xcenter - digitwd, ycenter - r2 + ascent/2);
    // Redraw hour hand
    g.setColor(mhandcolor[colorix] /*fgcol*/);
    g.fillPolygon(xh, yh, 4);
    // Redraw minute hand
    g.drawLine(xcenter, ycenter, xm, ym);
    g.drawLine(xcenter + 1, ycenter - 1, xm, ym);
    g.drawLine(xcenter - 1, ycenter + 1, xm, ym);
    g.drawLine(xcenter - 1, ycenter - 1, xm, ym);
    g.drawLine(xcenter + 1, ycenter + 1, xm, ym);
    // Redraw seconds hand 
    if (doSeconds) {
      g.setColor(shandcolor[colorix] /*fgcol2*/);
      g.drawLine(xcenter, ycenter, xs, ys);
    }
    // Remember old values
    lastxs = xs; lastys = ys;
    lastxm = xm; lastym = ym;
    lastxh = xh; lastyh = yh;
  }

  /**
   * Restart the clock
   **/
  public void start()
  {
    if (timer == null) {
      timer = new Thread(this);
      timer.start();
    }
  }

  /**
   * Stop the clock
   **/
  public void stop()
  {
    timer = null;
  }

  /**
   * Main loop of the thread: sleep, repaint, sleep,...
   **/
  public void run()
  {
    while (timer != null) {
      try {Thread.sleep(sleeptime);} catch (InterruptedException e){}
      repaint();
    }
    timer = null;
  }

  /**
   * Paint the screen, without first clearing the area
   **/
  public void update(Graphics g)
  {
    paint(g, true);
  }

  // ComponentListener interface:

  /**
   * React to a change in our size
   */
  public void componentResized(ComponentEvent e)
  {
    // Despite what the javadoc says, font size is in pixels, not points
    setFont(new Font("Helvetica", Font.PLAIN, 11 * getWidth() / 100));
  }

  public void componentHidden(ComponentEvent e) {}

  public void componentMoved(ComponentEvent e) {}

  public void componentShown(ComponentEvent e) {}

}
