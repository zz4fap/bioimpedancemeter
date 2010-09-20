/**--------------------------------------------------------------------
  * Project:                     Program 080055-I  RLC Meter (Elektor)
  * SWV-Number:                  1.0
  * Compiler:                    Java 1.6
  * Host:                        PC-XP, PC-Vista
  * Filename:                    Application.java
  +----------------------------------------
  * Date:                        07-MAI-2008
  * Authors:                     Martin Klaper, Heinz Mathis, e.a.
  *                              martin.klaper@hslu.ch
  *                              hb9ark@arrl.net
  +---------------------------------------------------------------------
  * Description:                 Main module
  * ------------
  *
  +---------------------------------------------------------------------
  * Changes:  					first release    08-MAI-08 MK/Klm
  * --------
  * 
  +---------------------------------------------------------------------
  * Package list:                dsp, view, io
  * -------------
  +--------------------------------------------------------------------*)
*/

package dsp;
//import java.io.FileOutputStream;
//import java.io.IOException;

import io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import view.RlcBridgeGui;


/**
 * This is the Main / Start Application
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class Application 
{
	//	audioformat definieren
	private AudioFormat audioFormat;
	private float fSampleRate = 44100.0F;
	private int sampleSizeInBits = 16;
	private int channels = 2;
	private int frameSize = 4;
	private float frameRate = fSampleRate;
	private boolean bigEndian = false;
	private DeviceChoice dc;
	private AudioFormat[] formats;
	
	//frequency sweep definition
	private FreqSweep freqsweep;
	
	//Defines algorithm type
	private AlgTyp algTyp;
	
	//input samples
    private double[] lSamplesRLC ;
    private double[] rSamplesRLC ;
    
    //calibration flag
    private boolean calibration = false;
    
    //sinusgenerator definieren
	private int frequency = 1250;
	
	//allgemeine Settings
	int byteBufferSize;
	private int samplesNumber = 20000;
	private double micro = 0.05;
	private Display display;
	
	//Threads und Runnable
	private Output output;
	private Input input;
	private Sinusgenerator singen;
	private Alg alg;
	private AlgController algController;
	private Thread outputThread;
	private Thread inputThread;
	private Object lock = new Object();
	private RlcBridgeGui gui;
	
	private SignalEventBuffer signalEventBuffer = null;
	
	//FFT
	private int N = 1024; // FFT size
	
	/**
	 * 	Application Constructor
	 *	Creates the audioformat
	 */
	public Application()
	{
		 //create audioformat
		audioFormat = new AudioFormat
		(
				AudioFormat.Encoding.PCM_SIGNED,
				fSampleRate,
				sampleSizeInBits,
				channels,
				frameSize,
				frameRate,
				bigEndian
		);
		dc = new DeviceChoice(audioFormat);
		
		/*formats = dc.testAllThePossibleAudioFormats();
		for(AudioFormat af : formats)
		{
			if(af != null)
			{
				System.out.println(af.toString());	
			}
		}*/
		
		algTyp = AlgTyp.Linearkombiner;
		
		//use standard device
		dc.setDevice();
		
		//define bytebuffersize
		byteBufferSize = 2816;
		int samplesPerSamplesNumber = Math.round(samplesNumber/(byteBufferSize/4));
		samplesNumber = (byteBufferSize/4)*samplesPerSamplesNumber;
		//create Objects
		output = new Output(dc, algTyp);
		input = new Input(lock, dc, audioFormat, byteBufferSize, algTyp,this);
		//Sinusgenerator creates sinussignal
		singen = new Sinusgenerator(output, audioFormat);
		singen.setFrequency(frequency);
		//input thread
		inputThread = new Thread(input);
		inputThread.start();
		//output thread
		outputThread = new Thread(output);
		outputThread.start();
	}
	/**
	 * Mainclass
	 */
	public static void main(String[] args)
	{
		Application mainApp = new Application(); 
		mainApp.drawGui(mainApp);
	}
	
	public Input getInputObj()
	{
		return input;
	}
	
	/**
	 * Draws the GUI
	 * @param mainApp
	 */
	public void drawGui(Application mainApp)
	{
		display = Display.getDefault();
		signalEventBuffer = new SignalEventBuffer();
		gui = new RlcBridgeGui(mainApp);
		gui.createSShell();
		gui.sShell.setImage(new Image(gui.sShell.getDisplay(), Application.class.getClassLoader().getResourceAsStream("images/icon.png")));
		gui.sShell.open();
		while (!gui.sShell.isDisposed()) 
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		display.dispose();
		//Terminate Threads
		input.terminate();
		output.terminate();
	}

	/**
	 * Returns the DeviceChoice Object
	 * @return DeviceChoice dc
	 */
	public DeviceChoice getDeviceChoice()
	{
		return dc;
	}
	
	/**
	 * Calculates RefVector or RLC Value
	 * 
	 * @param pCalibration Indicates measure or calibration mode.
	 */
	public void calculateRLCValueorRefVecforOhmmeter(boolean pCalibration)
	{
		calibration = pCalibration;	
		//terminate output thread
		this.output.terminate();
		while(this.outputThread.isAlive()){};
				
		if(freqsweep==null){freqsweep = new FreqSweep();}
		
		//Frequenzsweep erzeugen (start output thread)
		this.output = new Output(dc,algTyp);
		this.output.setByteBuffer(freqsweep.getByteData());
		this.outputThread = new Thread(this.output);
		this.outputThread.start();
		
		//Daten einlesen (start input thread)
		input = new Input(lock, dc, audioFormat, byteBufferSize,algTyp,this);
		inputThread = new Thread(input);
		inputThread.start();
				
		try 
		{
			Thread.sleep(10);
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//terminate output and input thread
		this.output.terminate();
		while(this.outputThread.isAlive()){};
		this.input.terminate();
		while(this.inputThread.isAlive()){};
		
		//calculate RLC values or set reference vector
		if(calibration){
			freqsweep.getAlgRLC().setCalibration(true);
			freqsweep.getAlgRLC().determine(this.lSamplesRLC,this.rSamplesRLC, this.lSamplesRLC.length);
			freqsweep.getAlgRLC().setRefVec();}
		else{
			freqsweep.getAlgRLC().setCalibration(false);
			freqsweep.getAlgRLC().determine(this.lSamplesRLC,this.rSamplesRLC, this.lSamplesRLC.length);
		}
						
		//starts output signal for linearekombiner
		this.output = new Output(dc, AlgTyp.Linearkombiner);
		this.singen = new Sinusgenerator(output, audioFormat);
		this.singen.setFrequency(frequency);
		this.outputThread = new Thread(this.output);
		this.outputThread.start();
	}
		
	/**
	 * Starts calibration
	 */
	public void startCalibration()
	{
		algTyp = AlgTyp.Ohmmeter;
		calculateRLCValueorRefVecforOhmmeter(true);		
	}
	
	/**
	 * Starts measurement
	 * 
	 * @param algo Type of algorithm
	 * @param loop Loop flag
	 */
	public void startMeasurement(AlgorithmSettings algo, boolean loop)
	{
		System.out.println("algoname: " + algo.algoName());
		// select algo
		// @Developer: To add a new Algo-> add new case statement and create your Algorithm
		if(algo != null)
		{
			switch (algo) 
			{
				case Linearkombiner:
					algTyp = AlgTyp.Linearkombiner;
					alg = new AlgLinearkombiner(micro, frequency, audioFormat);
					break;
				case Ohmmeter:
					algTyp = AlgTyp.Ohmmeter;
					calculateRLCValueorRefVecforOhmmeter(false);
					alg = freqsweep.getAlgRLC();
					break;
				default:
					break;
			}
		}
		else
		{
			alg = new AlgLinearkombiner(micro, frequency, audioFormat);
		}
		//Start the AlgController
		algController = new AlgController(lock, byteBufferSize, samplesNumber, loop);
		display.asyncExec(algController); // execute runnable in the display thread
		algController.setAlgo(alg);
		algController.setAlgTyp(algTyp);
		addInputSignalListener(algController);
		algController.addInfoListener(gui);
	}

	/**
	 * stop measurement
	 * terminate all threads
	 */
	public void stopMeasurement()
	{
		removeInputSignalListener(algController);
		System.out.println("listener removed");
		if(algController != null)
		{
			algController.terminate();
			System.out.println("alg controller terminated");
		}
	}
	
	/**
	 * Sets the frequency of the sinusgenerator and adapt the hilbert
	 * @param frequency
	 */
	public void setFrequency(int frequency)
	{
		this.frequency = frequency;
		singen = new Sinusgenerator(output, audioFormat);
		singen.setFrequency(frequency);
	}
	
	/**
	 * Add a Signallistener to the inputsignal
	 * @param sl
	 */
	public void addInputSignalListener(SignalListener sl)
	{
		input.addSignalListener(sl);
	}
	
	/**
	 * Remove a Signallistener from the inputsignal
	 * @param s1
	 */
	public void removeInputSignalListener(SignalListener s1)
	{
		input.removeSignalListener(s1);
	}
	
	
	/**
	 * Set Input Device (!!!! NOT WORKING)
	 * @param id
	 * @return boolean setInputDevice OK
	 */
	public boolean setInputDevice(int id)
	{
		System.out.println("DEVICE-ID: " + id);
		//Terminate Input Thread
		input.terminate(); 
		if(dc.setInputDevice(id))
		{
			//Create Input
			input = new Input(lock, dc, audioFormat, byteBufferSize,algTyp,this);
			//input thread
			inputThread = new Thread(input);
			inputThread.start();
			//TODO: add algcontroller and TraceCanvas as listeners
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void setRLCDataSamples(double[] plSamples, double[] prSamples)
	{
		this.lSamplesRLC = plSamples;
		this.rSamplesRLC = prSamples;
	}
	
	public int getFFTSize()
	{
		return N;
	}
	
	public float getSamplingFrequency()
	{
		return fSampleRate;
	}
	
	public void setNewSamplingFrequency(float sampFreq)
	{
		this.fSampleRate = sampFreq;
		restartDevice();
		addInputSignalListener(signalEventBuffer);
	}
	
	private void restartDevice()
	{
		this.stopMeasurement();
		input.terminate();
		output.terminate();
		dc.closeLine();
		
		audioFormat = new AudioFormat
		(
				AudioFormat.Encoding.PCM_SIGNED,
				fSampleRate,
				sampleSizeInBits,
				channels,
				frameSize,
				frameRate,
				bigEndian
		);
		dc = new DeviceChoice(audioFormat);
		
		//use standard device
		dc.setDevice();
		
		//create Objects
		output = new Output(dc, algTyp);
		input = new Input(lock, dc, audioFormat, byteBufferSize, algTyp,this);
		
		//Sinusoidal generator creates sinusoidal signal
		singen = new Sinusgenerator(output, audioFormat);
		singen.setFrequency(frequency);
		
		//input thread
		inputThread = new Thread(input);
		inputThread.start();
		
		//output thread
		outputThread = new Thread(output);
		outputThread.start();
	}
	
	public SignalEventBuffer getSignalEventBuffer()
	{
		return signalEventBuffer;
	}
}
