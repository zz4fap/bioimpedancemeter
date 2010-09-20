package io;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * Create the Mixers and serve the Lines
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class DeviceChoice 
{
    private AudioFormat audioFormat;
    private SourceDataLine lineSource = null;
    private TargetDataLine lineTarget = null;
    private Mixer mixer = null;
    private Mixer mixerIn;
    private Mixer mixerOut;
    private Mixer.Info[] mixers; 
    private boolean defaultMixer = false;

    public DeviceChoice(AudioFormat audioFormat) 
    {
        this.audioFormat = audioFormat;
        mixers = AudioSystem.getMixerInfo();
        if(mixers.length == 0)
        {
        	System.out.println("No mixers are available on the system.");
        }
        else
        {
        	for(int i=0; i<mixers.length;i++)
        	{
        		System.out.println("Mixer: "+i);
        		System.out.println(mixers[i].getDescription());
        	}
        }
    }
    
    /**
     * Use the default Device
     *
     */
    public void setDevice()
    {
    	defaultMixer = true;
    }
    
    /**
     * Sets the Device
     * Use this method, if you won't use the default device
     * @param id
     */
    public void setDevice(int id) 
    {
    	defaultMixer = false;
    	mixer = AudioSystem.getMixer(mixers[id]);
    	try {
    		mixer.open();
    	} catch (LineUnavailableException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	System.out.println(mixer.getMixerInfo());
    	
    }
    
    /**
     * Sets Input Device
     * Use this method, if you want use two separate device for
     * the input and output
     * @param id
     * @return boolean always true
     */
    public boolean setInputDevice(int id)
    {
    	defaultMixer = false;
    	mixerIn = AudioSystem.getMixer(mixers[id]);
    	return true;
    }

    /**
     * Sets Output Device
     * Use this method, if you want use two seperate device for
     * the input and output
     * @param id
     * @return boolean always true
     */
    public boolean setOutputDevice(int id)
    {
    	defaultMixer = false;
    	mixerOut = AudioSystem.getMixer(mixers[id]);
    	return true;
    }
   
    /**
     * Returns the TargetDataLine which can be used to get samples from the input
     * @return TargetDataLine lineTarget
     */
    public TargetDataLine getTargetDataLine() 
    { 
    	//for input
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
        	if(defaultMixer){
        		lineTarget = (TargetDataLine) AudioSystem.getLine(info);
        	}else if(mixer != null){
        		lineTarget = (TargetDataLine) mixer.getLine(info);
        	}else{
        		lineTarget = (TargetDataLine) mixerOut.getLine(info);
        	}
            lineTarget.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        return lineTarget;
    }
    
    public AudioFormat[] testAllThePossibleAudioFormats() 
    {
    	AudioFormat localAudioFormat;
    	AudioFormat formats[] = new AudioFormat[399360];
    	int i = 0, j = 0;
    	
    	AudioFormat.Encoding encodings[] = {AudioFormat.Encoding.PCM_SIGNED,AudioFormat.Encoding.PCM_UNSIGNED,AudioFormat.Encoding.ALAW,AudioFormat.Encoding.ULAW};
    	float sampleRates[] = {8000.0F,11025.0F,16000.0F,22050.0F,44100.0F,48000.0F,96000.0F,192000.0F};
    	int sampleSize[] = {8,16,20,24,32,64};
    	int channels[] = {1,2,3,4,5,6,7,8,9,10,12,16,24};
    	int frameSize[] = {1,2,3,4,5,6,7,8,9,10};
    	float frameRate[] = sampleRates;
    	boolean endianess[] = {true,false};
    	
    	for(AudioFormat.Encoding enc : encodings)
    	{
    		for(float sr : sampleRates)
    		{
    			for(int ss : sampleSize)
    			{
    				for(int ch : channels)
    				{
    					for(int fs : frameSize)
    					{
    						for(float fr : frameRate)
    						{
    							for(boolean bool : endianess)
    							{
    							   	localAudioFormat = new AudioFormat(enc,sr,ss,ch,fs,fr,bool);
    							   	
    							   	j++;
    							   	
    							   	//for input
    						        DataLine.Info info = new DataLine.Info(TargetDataLine.class, localAudioFormat);
    						        try 
    						        {
    						        	DataLine line = (DataLine) AudioSystem.getLine(info);
    						        	System.out.println("Found one format which works");
    						        	formats[i] = localAudioFormat;
    						        	i++;
    						    
    						        } catch (Exception e) 
    						        {
    						        	System.out.println("Unsupported audio format: "+j);
    						        }
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	return formats;
    }
    
    /**
     * Returns the SourceDataLine which can be used to send samples to the output
     * @return SourceDataLine lineSource
     */
    public SourceDataLine getSourceDataLine() 
    { //for output
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try 
        {
        	if(defaultMixer)
        	{
        		lineSource = (SourceDataLine) AudioSystem.getLine(info);
        	}
        	else if(mixer != null)
        	{
        		lineSource = (SourceDataLine) mixer.getLine(info);
        	}
        	else
        	{
        		lineSource = (SourceDataLine) mixerIn.getLine(info);
        	}
            lineSource.open(audioFormat);
        } 
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
        return lineSource;
    }
    
    /**
     * Returns a list of all available input devices
     * @return List<String> list
     */
    public List<String> getInputDevice() 
    {
    	DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
    	List<String> list = new ArrayList<String>();
    	for (int i = 0; i < mixers.length; i++) {
    		if(AudioSystem.getMixer(mixers[i]).isLineSupported(info)){
    			list.add("ID:" + i + "  " + mixers[i].getName());
    		}
		};
        return list;
    }
    
    public void closeLine()
    {
    	lineSource.close();
    	lineTarget.close();
    }
}
