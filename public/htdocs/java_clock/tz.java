import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * The tz class is an applet that uses the Clock class to create an
 * analogue clock for a specific time zone.
 *
 * Copyright © 1999 World Wide Web Consortium, (Massachusetts
 * Institute of Technology, Institut National de Recherche en
 * Informatique et en Automatique, Keio University). All rights
 * reserved.
 *
 * For full license statement:
 * @see http://www.w3.org/Consortium/Legal/copyright-software.html
 *
 * @author Bert Bos <bert@w3.org>
 * @version 19 March 1999
 *
 **/
public class tz extends java.applet.Applet
//public class tz extends java.applet.Applet implements ComponentListener
{
    // Netscape's Java doesn't have ComponentListener :-(

  // Add UTC, since Java uses GMT to mean BST.
  // Add BST (British Standard Time), since GMT is too confusing.
  protected final static TimeZone UTC = new SimpleTimeZone(0, "UTC");
  protected final static TimeZone BST = new SimpleTimeZone
  (0, "BST",
   Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60*60*1000,
   Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60*60*1000);

  private Clock clock;



  public void init()
  {
    String tz, language, country;
    TimeZone zone;
    Locale locale;

    setLayout(new GridLayout(1, 1));

    if ((tz = getParameter("tz")) == null)
      zone = TimeZone.getDefault();
    else if (tz.equals("UTC") || tz.equals("GMT"))
      zone = UTC;
    else if (tz.equals("BST"))
      zone = BST;
    else
      zone = TimeZone.getTimeZone(tz);

    if ((country = getParameter("country")) == null)
      country = "";
    if ((language = getParameter("language")) != null)
      locale = new Locale(language, country);
    else
      locale = Locale.getDefault();
    clock = new Clock(zone, locale);
    try {
      clock.setClockColor
	(new Color(Integer.parseInt(getParameter("color"), 16)));
    } catch (Exception e) {}
    try {
      clock.setTextColor
	(new Color(Integer.parseInt(getParameter("textcolor"), 16)));
    } catch (Exception e) {}
    try {
      setBackground
	(new Color(Integer.parseInt(getParameter("bgcolor"), 16)));
    } catch (Exception e) {}
    try {
      clock.setDayColor
	(new Color(Integer.parseInt(getParameter("daycolor"), 16)));
    } catch (Exception e) {}
    try {
      clock.setNightColor
	(new Color(Integer.parseInt(getParameter("nightcolor"), 16)));
    } catch (Exception e) {}
    try {
      clock.setSecondsDisplay(getParameter("seconds").equals("on"));
    } catch (Exception e) {}
    try {
      clock.setDateStyle(Integer.parseInt(getParameter("style")));
    } catch (Exception e) {}
    add(clock);
  }

  public void start()
  {
    clock.start();
  }

  public void stop()
  {
    clock.stop();
  }

}
