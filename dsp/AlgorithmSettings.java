package dsp;

/**
 * Defines all Algorithm
 * add an entry if you wrote your own algorithm and edit the 
 * startMeasurement-Method of the Application class
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public enum AlgorithmSettings 
{
    // defines algorithm
	// if you wrote your own algorithm you have to add an entry in this file. e.g. Linearkombiner
	// IMPORTANT: each algorithm is separated by a comma. The last algorithm ends with a semikolon
	Linearkombiner (0, 
			"linearcombiner LMS", 
			"Martin Klaper ", 
			"uses the least mean square method (LMS)."),
    
	Ohmmeter (1, 
			"RLC Algorithm", 
			"Heinz Mathis, Marcel Kluser  ", 
			"Hypothesis test for potential elements (use specific h/w)") ;
    
    //variables
    private final int id;
    private final String algoName;
    private final String autor;
	private final String description; 
    
	//construktor
    AlgorithmSettings(int id, String algoName, String autor, String description) {
    	this.id  = id;
    	this.algoName = algoName;
    	this.autor = autor;
    	this.description = description;
    }
    
    //methods
    public int id()   { return id; }
    public String algoName()   { return algoName; }
    public String autor()   { return autor; }
    public String description()   { return description; }
}