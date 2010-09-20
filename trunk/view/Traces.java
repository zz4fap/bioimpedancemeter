package view;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * Draw the Sin Signals on the GUI
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Joel Gruber
 */
public abstract class Traces extends Canvas
{	
	public Traces(final Composite parent, int style) 
	{
		super(parent, style);
	}

	/**
	 * Redraws the Traces
	 *
	 */
	abstract void drawTrace();
	
	/**
	 * Freezes the redraw
	 * @return boolean pauseRedraw
	 */
	abstract boolean pauseRedraw();
}
