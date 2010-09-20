package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import dsp.Application;
import dsp.SignalEventBuffer;

/**
 * Draw the FFT Signals in the GUI
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Joel Gruber
 */
public class TraceFFT extends Traces
{
	private SignalEventBuffer signalEventBuffer;
	private Display display;
	private Color colorFFTTrace;
	private Color lineColor;
	private Color peakFreqColor;
	private boolean pauseRedraw = false;
	private boolean running = true;
	private Composite parent = null;
	
	public TraceFFT(final SignalEventBuffer signalEventBuffer, final Composite parent, int style, final Application mainApp) 
	{
		super(parent, style);
		this.parent = parent;
		display = this.parent.getDisplay();
		colorFFTTrace = new Color(display, 255, 102, 0);
		lineColor = new Color(display, 255, 255, 255);
		peakFreqColor = new Color(display, 51, 255, 0);
		this.signalEventBuffer = signalEventBuffer;
		this.setBackground(new Color(display, 51, 51, 51));

		this.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e) 
			{	
				int FreqStep = 1000;
				int N = mainApp.getFFTSize();
				float Fs = mainApp.getSamplingFrequency();
				
				int fs = (int)Fs;
		        switch (fs)
		        {
		            case 8000:
		            case 11025:
		            case 16000:
		            case 22050:
		            case 44100:
		            case 48000:
		            	FreqStep = 1000;
		            	break;
		            case 96000:
		            case 192000:
		            	FreqStep = 2000;
		            	break;
		            default: 
		            	System.out.println("Invalid Sampling Frequency.");
		            	FreqStep = 1000;
		            	break;
		        }
				
				//traces
				e.gc.setLineStyle(SWT.LINE_SOLID);
				e.gc.setForeground(colorFFTTrace);
				e.gc.drawPolyline(signalEventBuffer.getFFTSignals());
				e.gc.setForeground(lineColor); // White				
				for(int freq=0; freq <= ((int)Fs/2); freq=freq+FreqStep)
				{
					double point = freq*(((double)N)/((double)Fs));
					int pointInt = (int)point;
					
					double freqDouble = freq/1000.0;
					
					e.gc.setForeground(peakFreqColor);
					int pos = signalEventBuffer.getEventObj().getfftHelperObj().getPeakPosition();
					e.gc.drawLine(pos,150,pos,200); // plot peak marker
					
					e.gc.setForeground(lineColor); // White	
					e.gc.setFont(new Font(display, "Helvetica", 7, SWT.NORMAL)); // set font of the text
					e.gc.drawText(Double.toString(freqDouble), pointInt, 203); // plot frequencies
					
					e.gc.drawLine(pointInt,195,pointInt,200); // plot markers
					
					String peakFreq = "Peak Freq: "+Double.toString(signalEventBuffer.getEventObj().getfftHelperObj().getPeakFrequency()) + " Hz";
					e.gc.setFont(new Font(display, "Helvetica", 8, SWT.NORMAL)); // set font of the text
					e.gc.drawText(peakFreq, 350, 10);
				}
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
