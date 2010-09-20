package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import dsp.SignalEventBuffer;

/**
 * Draw the Signals in the GUI
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Joel Gruber
 */
public class TraceCanvas implements Runnable
{
	private SignalEventBuffer signalEventBuffer;
	private Display display;
	private Shell[] shells = null;
	private boolean pauseRedraw = false;
	private boolean running = true;
	private Composite parent = null;
	private Shell shell = null;
	private Traces sinSignalTrace = null;
	private Traces fftTrace = null;
	private Traces trace = null;
	
	public TraceCanvas(Traces sin, Traces fft, final SignalEventBuffer signalEventBuffer, Shell shell) 
	{
		this.signalEventBuffer = signalEventBuffer;
		this.shell = shell;
		this.sinSignalTrace = sin;
		this.fftTrace = fft;
	}

	public void run() 
	{
		while(running)
		{			
			try
			{
				if(!shell.isDisposed())
				{
					shell.getDisplay().readAndDispatch();
					
					if(fftTrace.isVisible())
					{
						trace = fftTrace;
					}
					else if(sinSignalTrace.isVisible())
					{
						trace = sinSignalTrace;
					}
					else
					{
						trace = null;
					}
					
					if(signalEventBuffer.getlock() && trace != null)
					{
						trace.drawTrace();
					}
					
					shell.getDisplay().readAndDispatch();
				}
			}
			catch (Exception e)
			{
				this.terminate();
			}
			try 
			{
				Thread.sleep(20);
			} 
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void terminate() 
	{
    	running = false;
    }
}
