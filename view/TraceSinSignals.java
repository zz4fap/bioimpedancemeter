package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import dsp.SignalEventBuffer;

/**
 * Draw the Sin Signals on the GUI
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Joel Gruber
 */
public class TraceSinSignals extends Traces
{
	private SignalEventBuffer signalEventBuffer;
	private Display display;
	private Color colorAxes;
	private Color colorLeftTrace;
	private Color colorRightTrace;
	private boolean pauseRedraw = false;
	private boolean running = true;
	private int count = 0;
	private Composite parent = null;
	
	public TraceSinSignals(final SignalEventBuffer signalEventBuffer, final Composite parent, int style) 
	{
		super(parent, style);
		this.parent = parent;
		display = this.parent.getDisplay();
		colorAxes = new Color(display, 102, 102, 102);
		colorLeftTrace = new Color(display, 255, 102, 0);
		colorRightTrace = new Color(display, 51, 255, 0);
		this.signalEventBuffer = signalEventBuffer;
		this.setBackground(new Color(display, 51, 51, 51));

		this.addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent e) 
			{
				e.gc.setForeground(colorAxes);
				//centerline
				e.gc.drawLine(0, 100, 570, 100);
				//vertical lines
				e.gc.setLineStyle(SWT.LINE_DOT);
				int abstand = 30;
				for(int i=1;i<570/abstand;i++)
				{
					e.gc.drawLine(i*abstand, 0, i*abstand, 200);
				}
				//traces
				e.gc.setLineStyle(SWT.LINE_SOLID);
				e.gc.setForeground(colorLeftTrace);
				e.gc.drawPolyline(signalEventBuffer.getLeftSignals());
				e.gc.setForeground(colorRightTrace);
				e.gc.drawPolyline(signalEventBuffer.getRightSignals());
			}
		});
	}
	
	/**
	 * Redraws the Traces
	 *
	 */
	public void drawTrace() 
	{
		if(!pauseRedraw)
		{
			this.redraw();
		}
	}
	
	/**
	 * Freezes the redraw
	 * @return boolean pauseRedraw
	 */
	public boolean pauseRedraw()
	{
		if(pauseRedraw)
		{
			pauseRedraw = false;
		}
		else
		{
			pauseRedraw = true;
		}
		return pauseRedraw;
	}
}
