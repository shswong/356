package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import subsystem.Event;

/**
 * 
 *	GUI wrapper for the Event class.
 */
public class EventShape {
	//-- Public data
	public Event event;
	public int weight;
	public boolean selected = false;

	//-- Private data
	private BufferedImage bImage;
	private Rectangle bounds;
	private Color selectColor, normalColor;

	/**
	 * Constructor for an EventShape. Use of this constructor is preferable.
	 * 
	 * @param e Event to use
	 */
	public EventShape(Event e) {
		setupColors();
		event = e;
		weight = 0;
		bounds = new Rectangle(0,0,0,0);
		bImage = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		redrawImage();
	}

	/**
	 * Overrides the other constructor.
	 * 
	 * @param e Event to use
	 * @param bounds initial bounds
	 * @throws IllegalInputException if the bounds are 0
	 */
	public EventShape(Event e, Rectangle bounds) throws IllegalInputException {
		setupColors();
		event = e;
		weight = 0;
		this.bounds = bounds;
		bImage = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		setBounds(bounds);
	}

	/**
	 * Sets up some colors for the EventShape
	 */
	private void setupColors() {
		float[] s = new float[3];
		Color.RGBtoHSB(103, 137, 163, s);
		selectColor = Color.getHSBColor(s[0], s[1], s[2]);
		Color.RGBtoHSB(179, 196, 209, s);
		normalColor = Color.getHSBColor(s[0], s[1], s[2]);
	}

	/**
	 * Sets the bounds of this EventShape
	 * 
	 * @param bounds bounds to set it to
	 */
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;

		int width = Math.abs(bounds.width);
		if( width == 0 ){
			//if there are too many events, the width calculated
			//is less than one and so it would never show.
			//this should ensure that the width is always at least one
			width = 1;
		}
		int height= Math.abs(bounds.height);
		if( height == 0 ){
			height = 1;
		}
		bImage = new BufferedImage(width, height, 
				BufferedImage.TYPE_INT_ARGB);

		redrawImage();
	}

	/**
	 * Returns the bounds of the object
	 * 
	 * @return bounds of the object
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Sets the position of the EventShape
	 * 
	 * @param p xy coordinate to change to
	 */
	public void setPosition(Point p) {
		this.bounds.x = p.x;
		this.bounds.y = p.y;
	}

	/**
	 * Checks whether the point exists in this EventShape
	 * 	
	 * @param p Point to check
	 * @return true if the point exists in this EventShape, false otherwise
	 */
	public boolean contains(Point p) {
		return (p.x >= bounds.x && p.x <= (bounds.width + bounds.x) && p.y >= bounds.y && p.y <= (bounds.height + bounds.y));
	}

	/**
	 * Returns a bufferedImage of the image.
	 * @return bImage the bufferedImage to be returned.
	 */
	public BufferedImage getBImage() {
		return bImage;
	}

	/**
	 * draws the image in EventShape.
	 * @param gp The Graphics object that needs to be used to draw.
	 */
	public void DrawOn(Graphics2D gp) {
		gp.drawImage(bImage, null, bounds.x, bounds.y);
	}

	/**
	 * redraws the image using default settings.
	 */
	public void redrawImage() {
		Graphics2D gp = bImage.createGraphics();
		if (selected) {
			gp.setColor(selectColor);
			gp.fillRoundRect(0, 0, bounds.width, bounds.height, 20, 20);
			gp.setColor(Color.WHITE);
			gp.drawString(event.getTitle(), 6, 15);
			gp.drawString(event.getPeriod().start.time.toString()+" - "+event.getPeriod().end.time.toString(), 6, 27);
		} else {
			gp.setColor(normalColor);
			gp.fillRoundRect(0, 0, bounds.width, bounds.height, 20, 20);
			gp.setColor(Color.BLACK);
			gp.drawString(event.getTitle(), 6, 15);	
			gp.drawString(event.getPeriod().start.time.toString()+" - "+event.getPeriod().end.time.toString(), 6, 27);
		}
		gp.setColor(selectColor);
		gp.drawRoundRect(0, 0, bounds.width-1, bounds.height, 20, 20);
	}

	/**
	 * Returns the String representation of the event.
	 * @returns s the String representation of the event.
	 */
	public String toString() {
		String s = event.getTitle() + ": x - " + bounds.x + ", y - " + bounds.y + ", width - " + bounds.width +
		", height - " + bounds.height + ", weight - " + weight;
		return s;
	}
}
