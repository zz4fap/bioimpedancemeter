package view;

import io.DeviceChoice;
import io.Input;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import dsp.AlgorithmSettings;
import dsp.Application;
import dsp.InfoEvent;
import dsp.InfoListener;
import dsp.SignalEventBuffer;

/**
 * SWT GUI
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber & Marcel Kluser & Heinz Mathis
 */
public class RlcBridgeGui implements InfoListener 
{
	public Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,52"

	private Application mainApp;

	private DeviceChoice dc;

	private Composite mainComposite = null;

	private Composite titlebar = null;

	private Composite contentComposite = null;

	private Composite statusBar = null;

	private TabFolder tabFolder = null;

	private Composite tabSimpleMode = null;

	private Composite tabExpertMode = null;

	private Composite tabSettings = null;
	
	private Composite tabFFT = null;

	private Group groupSoundcard = null;

	private Label lblLineIn = null;

	private Combo dropdownSelectInput = null;

	private Composite compositeSMResults = null;

	private Label labelResult = null;

	private boolean running = false;
	
	private boolean RLCAlgoSelected = false;

	private boolean runningLoop = false;

	private Group groupSMPic = null;

	private Group groupSMData = null;

	private TraceCanvas traceCanvas = null;
	
	private TraceSinSignals traceSinSignals = null;
	
	private TraceFFT traceFFT = null;

	private SignalEventBuffer signalEventBuffer;

	private Composite compositeExpertModeSettings = null;
	
	private Composite compositeFFTModeSettings = null;

	private Group groupLeftChannel = null;

	private Group groupRightChannel = null;
	
	private Group groupSamplingRate = null;

	private Label labelOSAmplitude = null;

	private Spinner spinnerAmplitude = null;

	private Label labelOSTime = null;

	private Spinner spinnerTime = null;
	
	private Label labelOSTaps = null;
	
	private Spinner spinnerTaps = null;

	private Label labelEMFrequency = null;
	
	private Label labelSamplingrequency = null;

	private Combo comboFrequency = null;
	
	private Combo comboSamplingFrequency = null;

	private Canvas canvasAxes = null;
	
	private Canvas canvasFFTAxes = null;

	private Button buttonFreeze = null;
	
	private Button buttonStartFiltering = null;
	
	private Button buttonStartFilteringOnChannel1 = null;
	
	private Button buttonStartFilteringOnChannel2 = null;

	private Label labelStatusbarFrequency = null;

	private Label labelStatusbarSoundcard = null;

	private Composite compositeButtons = null;

	private Button buttonMeasure = null;

	private Button buttonMeasureLoop = null;

	private Composite tabHelp = null;

	private Group groupSettingsAlgorithm = null;
	
	private Group groupFilterSettings = null;

	private Label labelChooseAlgo = null;

	private Combo comboChooseAlgo = null;

	private Canvas canvasSchema = null;

	private InfoEvent infoEvent = null; // @jve:decl-index=0:

	private Widerstand widerstand = new Widerstand(); // @jve:decl-index=0:

	private Group groupAdditionalInfos = null;

	private Table tableAddiditonalInfos = null;

	private AlgorithmSettings algo = AlgorithmSettings.Linearkombiner; // @jve:decl-index=0:

	private Combo dropdownSelectOutput = null;

	private Label lblLineOut = null;

	private double realValue = 0;

	private double imagValue = 0;

	private Label labelSMTyp = null;

	private boolean startetFirstTime = false;

	private Label labelSettingsAlgName = null;

	private Label labelSettingsAutor = null;

	private Label labelSettingsDescription = null;

	private Label labelSettingsDynName = null;

	private Label labelSettingsDynAutor = null;

	private Label labelSettingsDynDescription = null;
	
	private Button buttonCalibration = null;
	
	private Label labelbuttonCalibration = null;
	
	private FocusListener listener = null;

	public RlcBridgeGui(Application mainApp) 
	{
		this.mainApp = mainApp;
		this.dc = mainApp.getDeviceChoice();
		this.signalEventBuffer = mainApp.getSignalEventBuffer();
	}

	public void setResult(String res) 
	{
		labelResult.setText(res);
	}

	// //////////////////////
	// Gui //
	// //////////////////////

	/**
	 * This method initializes sShell
	 */
	public void createSShell() 
	{
		sShell = new Shell(SWT.MIN | SWT.CLOSE | SWT.MAX);
		sShell.setText("elektor RLC Bridge 1.0");
		sShell.setText("elektor RLC Bridge 1.0");
		sShell.setLayout(new FillLayout());
		createMainComposite();
		sShell.setSize(new Point(600, 630));
	}

	/**
	 * This method initializes mainComposite
	 * 
	 */
	private void createMainComposite() 
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 2;
		mainComposite = new Composite(sShell, SWT.NONE);
		createTitlebar();
		mainComposite.setLayout(gridLayout);
		createContentComposite();
		createCompositeButtons();
		createCalibrationButton();
		createStatusBar();
	}

	/**
	 * This method initializes title bar
	 * 
	 */
	private void createTitlebar() 
	{
		GridData gridData12 = new GridData();
		gridData12.grabExcessVerticalSpace = true;
		gridData12.horizontalAlignment = GridData.FILL;
		gridData12.verticalAlignment = GridData.CENTER;
		gridData12.grabExcessHorizontalSpace = true;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 80;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;
		titlebar = new Composite(mainComposite, SWT.NONE);
		titlebar.setLayout(new GridLayout());
		titlebar.setLayoutData(gridData);
		titlebar.setBackground(new Color(sShell.getDisplay(), 255, 255, 255));
		labelResult = new Label(titlebar, SWT.NONE);
		labelResult.setBackground(new Color(sShell.getDisplay(), 255, 255, 255));
		labelResult.setFont(new Font(Display.getDefault(), "Sans", 36, SWT.BOLD));
		labelResult.setLayoutData(gridData12);
		labelResult.setAlignment(SWT.CENTER);
		labelResult.setText("-.-");
	}

	/**
	 * This method initializes contentComposite
	 * 
	 */
	private void createContentComposite() 
	{
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		contentComposite = new Composite(mainComposite, SWT.NONE);
		contentComposite.setLayout(new FillLayout());
		createTabFolder();
		contentComposite.setLayoutData(gridData2);
	}

	/**
	 * This method initializes statusBar
	 * 
	 */
	private void createStatusBar() 
	{
		RowLayout rowLayout = new RowLayout();
		rowLayout.fill = false;
		rowLayout.wrap = true;
		rowLayout.pack = true;
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.justify = true;
		GridData gridData3 = new GridData();
		gridData3.grabExcessHorizontalSpace = false;
		gridData3.verticalAlignment = GridData.CENTER;
		gridData3.heightHint = 20;
		gridData3.horizontalAlignment = GridData.FILL;
		statusBar = new Composite(mainComposite, SWT.BORDER);
		statusBar.setLayoutData(gridData3);
		statusBar.setLayout(rowLayout);
		labelStatusbarFrequency = new Label(statusBar, SWT.NONE);
		labelStatusbarFrequency.setForeground(new Color(sShell.getDisplay(),102, 102, 102));
		labelStatusbarFrequency.setText("Frequency: 1250 Hz");
		labelStatusbarSoundcard = new Label(statusBar, SWT.NONE);
		labelStatusbarSoundcard.setForeground(new Color(sShell.getDisplay(),102, 102, 102));
		labelStatusbarSoundcard.setText("Soundcard: ");
	}

	/**
	 * This method initializes tabFolder
	 * 
	 */
	private void createTabFolder() 
	{
		tabFolder = new TabFolder(contentComposite, SWT.NONE);
	
		/*tabFolder.addSelectionListener(new SelectionAdapter() {
			  public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
			    //tabFolder.getSelection()[0]; // This should be your TabItem/CTabItem
				  System.out.println("1 - I'm on It!");
				  System.out.println("2 - "+tabFolder.toString());
				  TabItem[] ti = tabFolder.getSelection();
				  System.out.println("3 - "+ti[0].toString());
				  System.out.println("4 - "+ti[0].getText());
				  tabFolder.getSelectionIndex();
			  }
			});*/
		
		createTabSimpleMode();
		mainApp.addInputSignalListener(signalEventBuffer);
		createTabFFT();
		createTabExpertMode();
		createTabSettings();
		createTabHelp();
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Simple Mode");
		tabItem.setToolTipText("measure device under test");
		tabItem.setControl(tabSimpleMode);
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText("Expert Mode");
		tabItem1.setToolTipText("oscilloscope view to check both input signals, change measurement frequency, etc.");
		tabItem1.setControl(tabExpertMode);
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText("Settings");
		tabItem2.setToolTipText("select algorithm 1 or 2, use appropriate hardware please");
		tabItem2.setControl(tabSettings);
		TabItem tabItem21 = new TabItem(tabFolder, SWT.NONE);
		tabItem21.setText("Help");
		tabItem21.setToolTipText("unfortunately not yet available, use README file instead");
		tabItem21.setControl(tabHelp);
		TabItem tabItem22 = new TabItem(tabFolder, SWT.NONE);
		tabItem22.setText("FFT");
		tabItem22.setToolTipText("It plots the FFT of the input signals");
		tabItem22.setControl(tabFFT);
		traceCanvas = new TraceCanvas(traceSinSignals,traceFFT,signalEventBuffer, sShell);
		sShell.getDisplay().asyncExec(traceCanvas);
	}

	/**
	 * This method initializes tabSimpleMode
	 * 
	 */
	private void createTabSimpleMode() 
	{
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		gridLayout2.makeColumnsEqualWidth = true;
		tabSimpleMode = new Composite(tabFolder, SWT.NONE);
		tabSimpleMode.setLayout(gridLayout2);
		createCompositeSMResults();
	}

	/**
	 * This method initializes tabExpertMode
	 * 
	 */
	private void createTabExpertMode() 
	{
		GridLayout gridLayout8 = new GridLayout();
		gridLayout8.numColumns = 1;
		tabExpertMode = new Composite(tabFolder, SWT.NONE);
		createCanvasAxes();
		tabExpertMode.setLayout(gridLayout8);
		new Label(tabExpertMode, SWT.NONE);
		createCompositeExpertModeSettings();
	}

	/**
	 * This method initializes tabSettings
	 * 
	 */
	private void createTabSettings() 
	{
		tabSettings = new Composite(tabFolder, SWT.NONE);
		tabSettings.setLayout(new GridLayout());
		createGroupSoundcard();
		createGroupSettingsAlgorithm();
	}
	
	/**
	 * This method initializes tabFFT
	 * 
	 */
	private void createTabFFT()
	{
		GridLayout gridLayout8 = new GridLayout();
		gridLayout8.numColumns = 1;
		tabFFT = new Composite(tabFolder, SWT.NONE);
		createFFTCanvasAxes();
		tabFFT.setLayout(gridLayout8);
		new Label(tabFFT, SWT.NONE);
		createCompositeFFTModeSettings();
	}
	
	/**
	 * This method initializes compositeExpertModeSettings
	 * 
	 */
	private void createCompositeFFTModeSettings() 
	{
		GridLayout gridLayout5 = new GridLayout();
		gridLayout5.numColumns = 3;
		GridData gridData16 = new GridData();
		
		gridData16.grabExcessHorizontalSpace = true;
		gridData16.verticalAlignment = GridData.FILL;
		gridData16.grabExcessVerticalSpace = true;
		gridData16.horizontalAlignment = GridData.FILL;
		
		compositeFFTModeSettings = new Composite(tabFFT, SWT.NONE);
		compositeFFTModeSettings.setLayout(gridLayout5);
		compositeFFTModeSettings.setLayoutData(gridData16);
		createFFTSettings();
	}
	
	/**
	 * This method initializes Sampling rate.
	 * 
	 */
	private void createFFTSettings() 
	{
		GridLayout gridLayout7 = new GridLayout();
		gridLayout7.numColumns = 3;
		GridData gridData17 = new GridData();
		gridData17.horizontalAlignment = GridData.BEGINNING;
		gridData17.verticalAlignment = GridData.BEGINNING;
		groupSamplingRate = new Group(compositeFFTModeSettings, SWT.NONE);
		groupSamplingRate.setLayoutData(gridData17);
		groupSamplingRate.setLayout(gridLayout7);
		groupSamplingRate.setText("Sampling Frequency");
		labelSamplingrequency = new Label(groupSamplingRate, SWT.NONE);
		labelSamplingrequency.setText("Frequency: ");
		new Label(groupSamplingRate, SWT.NONE);
		createComboSamplingFrequency();
	}
	
	/**
	 * This method initializes createComboSamplingFrequency
	 * 
	 */
	private void createComboSamplingFrequency() 
	{
		comboSamplingFrequency = new Combo(groupSamplingRate, SWT.READ_ONLY);
		comboSamplingFrequency.setItems(new String[] {"8000 Hz", "11025 Hz", "16000 Hz", "22050 Hz", "44100 Hz", "48000 Hz", "96000 Hz", "192000 Hz" });
		comboSamplingFrequency.select(4);
		comboSamplingFrequency.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e){
						switch (comboSamplingFrequency.getSelectionIndex()) 
						{
							case 0:
								mainApp.setNewSamplingFrequency(8000.0F);
								System.out.println("8000hz");
								break;
							case 1:
								mainApp.setNewSamplingFrequency(11025.0F);
								System.out.println("11025hz");
								break;
							case 2:
								mainApp.setNewSamplingFrequency(16000.0F);
								System.out.println("16000hz");
								break;
							case 3:
								mainApp.setNewSamplingFrequency(22050.0F);
								System.out.println("22050hz");
								break;
							case 4:
								mainApp.setNewSamplingFrequency(44100.0F);
								System.out.println("44100hz");
								break;
							case 5:
								mainApp.setNewSamplingFrequency(48000.0F);
								System.out.println("48000hz");
								break;
							case 6:
								mainApp.setNewSamplingFrequency(96000.0F);
								System.out.println("96000hz");
								break;
							case 7:
								mainApp.setNewSamplingFrequency(192000.0F);
								System.out.println("192000hz");
								break;
							default:
								mainApp.setNewSamplingFrequency(44100.0F);
						}
					}
				});
	}

	/**
	 * This method initializes groupSoundcard
	 * 
	 */
	private void createGroupSoundcard() 
	{
		GridData gridData7 = new GridData();
		gridData7.widthHint = 120;
		gridData7.grabExcessHorizontalSpace = true;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		gridLayout1.makeColumnsEqualWidth = true;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = GridData.CENTER;
		groupSoundcard = new Group(tabSettings, SWT.NONE);
		groupSoundcard.setText("Soundcard");
		groupSoundcard.setLayout(gridLayout1);
		groupSoundcard.setLayoutData(gridData4);
		lblLineIn = new Label(groupSoundcard, SWT.NONE);
		lblLineIn.setText("Select Input");
		lblLineIn.setLayoutData(gridData7);
		lblLineOut = new Label(groupSoundcard, SWT.NONE);
		lblLineOut.setText("Select Output");
		createDropdownLineIn();
		createDropdownSelectOutput();
	}

	/**
	 * This method initializes dropdownLineIn
	 * 
	 */
	private void createDropdownLineIn() 
	{
		GridData gridData5 = new GridData();
		gridData5.widthHint = 100;
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.verticalAlignment = GridData.CENTER;
		gridData5.grabExcessHorizontalSpace = false;
		dropdownSelectInput = new Combo(groupSoundcard, SWT.DROP_DOWN | SWT.READ_ONLY);
		dropdownSelectInput.setLayoutData(gridData5);
		// fill
		dropdownSelectInput.addSelectionListener(new SelectionListener(){
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) 
					{
						System.out.println("widgetSelected()" + dropdownSelectInput.getSelectionIndex()); 	// TODO
																											// Auto-generated
																											// Event
																											// stub
																											// widgetSelected()
						String id = dropdownSelectInput.getItem(dropdownSelectInput.getSelectionIndex()).substring(3, 5);
						int posSpace = id.indexOf(" ");
						int idint = Integer.valueOf(id.substring(0, posSpace));
						mainApp.setInputDevice(idint);
					}

					public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {}
				});
		List<String> line = dc.getInputDevice();
		for (Iterator<String> it = line.iterator(); it.hasNext();) 
		{
			String mixer = (String) it.next();
			dropdownSelectInput.add(mixer);
		}
		dropdownSelectInput.select(0);
	}

	/**
	 * This method initializes compositeSMResults
	 * 
	 */
	private void createCompositeSMResults() 
	{
		GridData gridData9 = new GridData();
		gridData9.widthHint = 10;
		gridData9.heightHint = 10;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 1;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalSpan = 2;
		gridData1.heightHint = 100;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		compositeSMResults = new Composite(tabSimpleMode, SWT.NONE);
		compositeSMResults.setLayoutData(gridData1);
		compositeSMResults.setLayout(gridLayout3);
		compositeSMResults.setBackground(new Color(sShell.getDisplay(), 255,255, 255));
		createGroupSMPic();
		createGroupSMData();
	}

	public void infoReady(InfoEvent event) 
	{
		this.infoEvent = event;
		DecimalFormat df = new DecimalFormat("#0.00");
		
		// set result string
		if(event.getUnit().toString().equals("unendlich"))
		{
			setResult(String.valueOf(event.getUnit().toString()));
		}
		else
		{
			setResult(String.valueOf(df.format(event.getValue())) + " " + event.getUnit().toString());
		}
		
		// update additional infos
		tableAddiditonalInfos.removeAll();
		if (event.getAdditonalInfos() != null) 
		{
			for (Map.Entry<String, Double> e : event.getAdditonalInfos().entrySet()) 
			{
				System.out.println(e.getKey() + "=" + e.getValue());
				new TableItem(tableAddiditonalInfos, 0).setText(new String[] {
						e.getKey(), e.getValue().toString() });
			}

			// set real and imag values (if alg ist the linear combiner)
			if (event.getAdditonalInfos().containsKey("real") && event.getAdditonalInfos().containsKey("imag")) 
			{
				realValue = event.getAdditonalInfos().get("real");
				imagValue = event.getAdditonalInfos().get("imag");
			} 
			else 
			{
				realValue = 0;
				imagValue = 0;
			}
		}

		// redraw canvas
		canvasSchema.redraw();

		// running
		if (running) 
		{
			mainApp.stopMeasurement();
			buttonMeasure.setText("Measure");
			if(!RLCAlgoSelected)
			{
				buttonMeasureLoop.setEnabled(true);
			}
			running = false;
		}
	}

	/**
	 * This method initializes groupSMPic
	 * 
	 */
	private void createGroupSMPic() 
	{
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.grabExcessHorizontalSpace = true;
		gridData8.heightHint = 240;
		gridData8.verticalAlignment = GridData.CENTER;
		groupSMPic = new Group(compositeSMResults, SWT.NONE);
		groupSMPic.setLayout(new GridLayout());
		groupSMPic.setText("diagram");
		groupSMPic.setToolTipText("equivalent circuit diagram");
		groupSMPic.setLayoutData(gridData8);
		createCanvasSchema();
		groupSMPic.setBackground(new Color(sShell.getDisplay(), 255, 255, 255));
	}

	/**
	 * This method initializes groupSMData
	 * 
	 */
	private void createGroupSMData() 
	{
		GridData gridData25 = new GridData();
		gridData25.horizontalAlignment = GridData.FILL;
		gridData25.grabExcessHorizontalSpace = true;
		gridData25.grabExcessVerticalSpace = true;
		gridData25.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout4 = new GridLayout();
		GridData gridData14 = new GridData();
		gridData14.horizontalAlignment = GridData.FILL;
		gridData14.grabExcessHorizontalSpace = true;
		gridData14.grabExcessVerticalSpace = true;
		gridData14.verticalAlignment = GridData.FILL;
		groupSMData = new Group(compositeSMResults, SWT.NONE);
		groupSMData.setText("info");
		groupSMData.setToolTipText("type of measured device");
		groupSMData.setBackground(new Color(sShell.getDisplay(), 255, 255, 255));
		groupSMData.setLayout(gridLayout4);
		groupSMData.setLayoutData(gridData14);
		labelSMTyp = new Label(groupSMData, SWT.NONE);
		labelSMTyp.setBackground(new Color(sShell.getDisplay(), 255, 255, 255));
		labelSMTyp.setLayoutData(gridData25);
		labelSMTyp.setFont(new Font(Display.getDefault(), "Sans", 22,org.eclipse.swt.SWT.BOLD));
		labelSMTyp.setAlignment(SWT.CENTER);
	}

	/**
	 * This method initializes traceCanvas
	 * 
	 */
	private void createTraceSinSignalsCanvas() 
	{
		traceSinSignals = new TraceSinSignals(signalEventBuffer, canvasAxes, SWT.NONE);
	}
	
	/**
	 * This method initializes FFTtraceCanvas
	 * 
	 */
	private void createFFTTraceCanvas() 
	{
		traceFFT = new TraceFFT(signalEventBuffer, canvasFFTAxes, SWT.NONE, mainApp);
	}
	
	/**
	 * This method initializes traceCanvas and FFTtraceCanvas
	 * 
	 */	
	private void createTraceCanvas()
	{
		traceSinSignals = new TraceSinSignals(signalEventBuffer, canvasAxes, SWT.NONE);
		traceFFT = new TraceFFT(signalEventBuffer, canvasFFTAxes, SWT.NONE, mainApp);
	}

	/**
	 * This method initializes compositeExpertModeSettings
	 * 
	 */
	private void createCompositeExpertModeSettings() 
	{
		GridLayout gridLayout5 = new GridLayout();
		gridLayout5.numColumns = 3;
		GridData gridData16 = new GridData();
		
		gridData16.grabExcessHorizontalSpace = true;
		gridData16.verticalAlignment = GridData.FILL;
		gridData16.grabExcessVerticalSpace = true;
		gridData16.horizontalAlignment = GridData.FILL;
		
		compositeExpertModeSettings = new Composite(tabExpertMode, SWT.NONE);
		compositeExpertModeSettings.setLayout(gridLayout5);
		compositeExpertModeSettings.setLayoutData(gridData16);
		createGroupAdditionalInfos();
		createGroupOscSettings();
		createGroupFilterSettings();
		createGroupSettings();
	}
	
	private void createGroupFilterSettings()
	{
		GridLayout gridLayout6 = new GridLayout();
		gridLayout6.numColumns = 2;
		
		GridData gridData22 = new GridData();
		gridData22.horizontalAlignment = GridData.FILL;
		gridData22.verticalAlignment = GridData.FILL;

		groupFilterSettings = new Group(compositeExpertModeSettings, SWT.NONE);
		groupFilterSettings.setText("Filter Settings");
		groupFilterSettings.setLayoutData(gridData22);
		groupFilterSettings.setLayout(gridLayout6);
		
		GridData gridData18 = new GridData();
		gridData18.grabExcessHorizontalSpace = true;
		gridData18.verticalAlignment = GridData.CENTER;
		gridData18.horizontalSpan = 2;
		gridData18.horizontalAlignment = GridData.FILL;
		
		final Input input = mainApp.getInputObj();
		buttonStartFiltering = new Button(groupFilterSettings, SWT.NONE);
		buttonStartFiltering.setText("Start Filtering");
		buttonStartFiltering.setLayoutData(gridData18);
		buttonStartFiltering.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						if (input.startFiltering()) {
							buttonStartFiltering.setText("Start Filtering");
						} else {
							buttonStartFiltering.setText("Stop Filtering");
						}
					}
				});
		
		labelOSTaps = new Label(groupFilterSettings, SWT.NONE);
		labelOSTaps.setText("Taps:");
		spinnerTaps = new Spinner(groupFilterSettings, SWT.NONE);
		spinnerTaps.setMinimum(1);
		spinnerTaps.setMaximum(100);
		spinnerTaps.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
						input.setTaps(spinnerTaps.getSelection());
					}
				});
		
		buttonStartFilteringOnChannel1 = new Button(groupFilterSettings, SWT.CHECK);
		buttonStartFilteringOnChannel1.setText("Left");
		buttonStartFilteringOnChannel1.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
					input.enableLeftFiltering();
			}
		});
		
		buttonStartFilteringOnChannel2 = new Button(groupFilterSettings, SWT.CHECK);
		buttonStartFilteringOnChannel2.setText("Right");
		buttonStartFilteringOnChannel2.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
					input.enableRightFiltering();
			}
		});
	}

	/**
	 * This method initializes groupLeftChannel
	 * 
	 */
	private void createGroupOscSettings() 
	{
		GridData gridData22 = new GridData();
		gridData22.horizontalAlignment = GridData.CENTER;
		gridData22.grabExcessVerticalSpace = false;
		gridData22.verticalAlignment = GridData.FILL;
		GridData gridData18 = new GridData();
		gridData18.grabExcessHorizontalSpace = true;
		gridData18.verticalAlignment = GridData.CENTER;
		gridData18.horizontalSpan = 2;
		gridData18.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout6 = new GridLayout();
		gridLayout6.numColumns = 2;
		groupLeftChannel = new Group(compositeExpertModeSettings, SWT.NONE);
		groupLeftChannel.setText("Oscilloscope Settings");
		groupLeftChannel.setLayoutData(gridData22);
		groupLeftChannel.setLayout(gridLayout6);
		buttonFreeze = new Button(groupLeftChannel, SWT.NONE);
		buttonFreeze.setText("Freeze Traces");
		buttonFreeze.setLayoutData(gridData18);
		buttonFreeze.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (traceSinSignals.pauseRedraw()) {
							buttonFreeze.setText("Defreeze Traces");
						} else {
							buttonFreeze.setText("Freeze Traces");
						}
					}
				});
		labelOSAmplitude = new Label(groupLeftChannel, SWT.NONE);
		labelOSAmplitude.setText("Amplitude: ");
		spinnerAmplitude = new Spinner(groupLeftChannel, SWT.NONE);
		spinnerAmplitude.setMinimum(1);
		spinnerAmplitude.setMaximum(10);
		labelOSTime = new Label(groupLeftChannel, SWT.NONE);
		labelOSTime.setText("Time:");
		spinnerTime = new Spinner(groupLeftChannel, SWT.NONE);
		spinnerTime.setMinimum(1);
		spinnerTime.setMaximum(10);
		spinnerTime.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
						signalEventBuffer.setZoomX(spinnerTime.getSelection());
					}
				});
		spinnerAmplitude.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
						signalEventBuffer.setZoomY(spinnerAmplitude.getSelection());
					}
				});
	}

	/**
	 * This method initializes groupRightChannel
	 * 
	 */
	private void createGroupSettings() 
	{
		GridLayout gridLayout7 = new GridLayout();
		gridLayout7.numColumns = 3;
		GridData gridData17 = new GridData();
		gridData17.horizontalAlignment = GridData.BEGINNING;
		gridData17.verticalAlignment = GridData.BEGINNING;
		groupRightChannel = new Group(compositeExpertModeSettings, SWT.NONE);
		groupRightChannel.setLayoutData(gridData17);
		groupRightChannel.setLayout(gridLayout7);
		groupRightChannel.setText("sine signal");
		labelEMFrequency = new Label(groupRightChannel, SWT.NONE);
		labelEMFrequency.setText("Frequency: ");
		new Label(groupRightChannel, SWT.NONE);
		createComboFrequency();
	}

	/**
	 * This method initializes comboFrequency
	 * 
	 */
	private void createComboFrequency() 
	{
		comboFrequency = new Combo(groupRightChannel, SWT.READ_ONLY);
		comboFrequency.setItems(new String[] { "1000 Hz", "1250 Hz", "2200 Hz", "4400 Hz", "6600 Hz", "8800 Hz" });
		comboFrequency.select(1);
		comboFrequency.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
						switch (comboFrequency.getSelectionIndex()) 
						{
							case 0:
								mainApp.setFrequency(1000);
								setStatusTextFrequency("1000 Hz");
								System.out.println("1000hz");
								break;
							case 1:
								mainApp.setFrequency(1250);
								setStatusTextFrequency("1250 Hz");
								System.out.println("1250hz");
								break;
							case 2:
								mainApp.setFrequency(2250);
								setStatusTextFrequency("2200 Hz");
								System.out.println("2250hz");
								break;
							case 3:
								mainApp.setFrequency(4400);
								setStatusTextFrequency("4400 Hz");
								System.out.println("4400hz");
								break;
							case 4:
								mainApp.setFrequency(6600);
								setStatusTextFrequency("6600 Hz");
								System.out.println("6600hz");
								break;
							case 5:
								mainApp.setFrequency(8800);
								setStatusTextFrequency("8800 Hz");
								System.out.println("8800hz");
								break;
							default:
								mainApp.setFrequency(1000);
						}
					}
				});
	}

	/**
	 * This method initializes canvasAxes
	 * 
	 */
	private void createCanvasAxes()
	{
		GridData gridData15 = new GridData();
		gridData15.heightHint = 200;
		gridData15.horizontalIndent = 0;
		gridData15.widthHint = 570;
		canvasAxes = new Canvas(tabExpertMode, SWT.NONE);
		canvasAxes.setLayout(new FillLayout());
		canvasAxes.setLayoutData(gridData15);
		canvasAxes.setBackground(new Color(sShell.getDisplay(), 255, 0, 51));
		canvasAxes.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(new Color(sShell.getDisplay(), 102, 102, 102));
				// centerline
				e.gc.drawLine(0, 100, 560, 100);
				// horizontal lines
				e.gc.setLineStyle(SWT.LINE_DOT);
				for (int i = 0; i < 10; i++) 
				{
					e.gc.drawLine(0, 100 - i * 15, 560, 100 - i * 15);
					e.gc.drawLine(0, 100 + i * 15, 560, 100 + i * 15);
				}
				// for(int i=0;i<20;i++){
				// e.gc.drawLine(i*15, 0, i*15, 200);
				// }
			}
		});
		canvasAxes.redraw();
		createTraceSinSignalsCanvas();
	}
	
	/**
	 * This method initializes FFT canvas Axes
	 * 
	 */
	private void createFFTCanvasAxes()
	{
		GridData gridData15 = new GridData();
		gridData15.heightHint = 220;
		gridData15.horizontalIndent = 0;
		gridData15.widthHint = 535;
		canvasFFTAxes = new Canvas(tabFFT, SWT.NONE);
		canvasFFTAxes.setLayout(new FillLayout());
		canvasFFTAxes.setLayoutData(gridData15);
		canvasFFTAxes.setBackground(new Color(sShell.getDisplay(), 255, 0, 51));
		canvasFFTAxes.addPaintListener(new PaintListener() 
		{
			public void paintControl(PaintEvent e) 
			{
				e.gc.setForeground(new Color(sShell.getDisplay(), 102, 102, 102));
				// centerline
				e.gc.drawLine(0, 100, 560, 100);
				// horizontal lines
				e.gc.setLineStyle(SWT.LINE_DOT);
				for (int i = 0; i < 10; i++) 
				{
					e.gc.drawLine(0, 100 - i * 15, 560, 100 - i * 15);
					e.gc.drawLine(0, 100 + i * 15, 560, 100 + i * 15);
				}		
			}
		});
		canvasFFTAxes.redraw();
		createFFTTraceCanvas();
	}

	private void setStatusTextFrequency(String frequency) 
	{
		labelStatusbarFrequency.setText("Frequency: " + frequency);
	}
	
	/**
	 * This method creates Calibration Button
	 * 
	 */
	private void createCalibrationButton()
	{
		buttonCalibration = new Button(groupSettingsAlgorithm, SWT.NONE);		
		buttonCalibration.setText("Calibration");
		buttonCalibration.setEnabled(false);
		
		labelbuttonCalibration = new Label(groupSettingsAlgorithm, SWT.NONE);
		labelbuttonCalibration.setText("Not calibrated");
		labelbuttonCalibration.setEnabled(false);	
		buttonCalibration.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
					labelbuttonCalibration.setText("Calibration is running...");
					buttonCalibration.setEnabled(false);
					mainApp.startCalibration();
					buttonCalibration.setEnabled(true);	
					labelbuttonCalibration.setText("Calibration done");											
			}
		});
	}
	
	/**
	 * This method initializes compositeButtons
	 * 
	 */
	private void createCompositeButtons() 
	{
		GridData gridData21 = new GridData();
		gridData21.heightHint = 32;
		gridData21.horizontalAlignment = GridData.FILL;
		gridData21.verticalAlignment = GridData.CENTER;
		gridData21.grabExcessHorizontalSpace = true;
		GridData gridData20 = new GridData();
		gridData20.heightHint = 32;
		gridData20.horizontalAlignment = GridData.FILL;
		gridData20.verticalAlignment = GridData.CENTER;
		gridData20.grabExcessHorizontalSpace = true;
		GridLayout gridLayout9 = new GridLayout();
		gridLayout9.numColumns = 2;
		GridData gridData19 = new GridData();
		gridData19.grabExcessHorizontalSpace = true;
		gridData19.verticalAlignment = GridData.CENTER;
		gridData19.heightHint = 40;
		gridData19.horizontalAlignment = GridData.FILL;
		compositeButtons = new Composite(mainComposite, SWT.NONE);
		compositeButtons.setLayoutData(gridData19);
		compositeButtons.setLayout(gridLayout9);
		buttonMeasure = new Button(compositeButtons, SWT.NONE);
		buttonMeasure.setText("Measure");
		buttonMeasure.setToolTipText("starts a single measurement");
		buttonMeasure.setLayoutData(gridData20);
		buttonMeasure.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (running) {
							mainApp.stopMeasurement();
							buttonMeasure.setText("Measure");
							switch (comboFrequency.getSelectionIndex()) {
							case 0:
								labelStatusbarFrequency.setText("Frequency: 1000Hz");
								break;
							case 1:
								labelStatusbarFrequency.setText("Frequency: 1250Hz");
								break;
							case 2:
								labelStatusbarFrequency.setText("Frequency: 2250Hz");
								break;
							}
							labelStatusbarFrequency.setToolTipText("set measurement frequency for algortihm 1");
							if(!RLCAlgoSelected){
								buttonMeasureLoop.setEnabled(true);	
							}
							running = false;
						} else {
							buttonMeasureLoop.setEnabled(false);
							buttonMeasure.setText("Stop");
							if(RLCAlgoSelected){
								labelStatusbarFrequency.setText("Frequency Sweep");
							}
							mainApp.startMeasurement(algo, false);
							switch (comboFrequency.getSelectionIndex()) {
							case 0:
								labelStatusbarFrequency.setText("Frequency: 1000Hz");
								break;
							case 1:
								labelStatusbarFrequency.setText("Frequency: 1250Hz");
								break;
							case 2:
								labelStatusbarFrequency.setText("Frequency: 2250Hz");
								break;
							}
							labelStatusbarFrequency.setToolTipText("set measurement frequency for algortihm 1");
							running = true;
						}
					}
				});
		buttonMeasureLoop = new Button(compositeButtons, SWT.NONE);
		buttonMeasureLoop.setText("Measure (loop)");
		buttonMeasureLoop.setToolTipText("start continuous measurement");
		buttonMeasureLoop.setLayoutData(gridData21);
		buttonMeasureLoop.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (runningLoop) {
							mainApp.stopMeasurement();
							buttonMeasureLoop.setText("Measure (loop)");
							buttonMeasure.setEnabled(true);
							runningLoop = false;
						} else {
							buttonMeasure.setEnabled(false);
							buttonMeasureLoop.setText("Stop");
							mainApp.startMeasurement(algo, true);
							runningLoop = true;
						}
					}
				});
	}

	/**
	 * This method initializes tabHelp
	 * 
	 */
	private void createTabHelp() 
	{
		tabHelp = new Composite(tabFolder, SWT.NONE);
		tabHelp.setLayout(new GridLayout());
	}

	/**
	 * This method initializes groupSettingsAlgorithm
	 * 
	 */
	private void createGroupSettingsAlgorithm() 
	{
		GridData gridData29 = new GridData();
		gridData29.horizontalAlignment = GridData.FILL;
		gridData29.verticalAlignment = GridData.CENTER;
		GridData gridData28 = new GridData();
		gridData28.horizontalAlignment = GridData.FILL;
		gridData28.verticalAlignment = GridData.CENTER;
		GridData gridData27 = new GridData();
		gridData27.horizontalAlignment = GridData.FILL;
		gridData27.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout10 = new GridLayout();
		gridLayout10.numColumns = 2;
		GridData gridData10 = new GridData();
		gridData10.grabExcessHorizontalSpace = true;
		gridData10.verticalAlignment = GridData.CENTER;
		gridData10.horizontalAlignment = GridData.FILL;
		groupSettingsAlgorithm = new Group(tabSettings, SWT.NONE);
		groupSettingsAlgorithm.setLayoutData(gridData10);
		groupSettingsAlgorithm.setLayout(gridLayout10);
		groupSettingsAlgorithm.setText("Algorithm");
		labelChooseAlgo = new Label(groupSettingsAlgorithm, SWT.NONE);
		labelChooseAlgo.setText("Choose Algorithm");
		new Label(groupSettingsAlgorithm, SWT.NONE);
		createComboChooseAlgo();
		labelSettingsAlgName = new Label(groupSettingsAlgorithm, SWT.NONE);
		labelSettingsAlgName.setText("Name:");
		labelSettingsDynName = new Label(groupSettingsAlgorithm, SWT.NONE);
		labelSettingsDynName.setText("Label");
		labelSettingsDynName.setLayoutData(gridData27);
		labelSettingsAutor = new Label(groupSettingsAlgorithm, SWT.NONE);
		labelSettingsAutor.setText("Author:");
		labelSettingsDynAutor = new Label(groupSettingsAlgorithm, SWT.NONE);
		labelSettingsDynAutor.setText("Label");
		labelSettingsDynAutor.setLayoutData(gridData28);
		labelSettingsDescription = new Label(groupSettingsAlgorithm, SWT.NONE);
		labelSettingsDescription.setText("Description:");
		labelSettingsDynDescription = new Label(groupSettingsAlgorithm,SWT.NONE);
		labelSettingsDynDescription.setText("Label");
		labelSettingsDynDescription.setLayoutData(gridData29);
		labelSettingsDynName.setText(algo.algoName());
		labelSettingsDynAutor.setText(algo.autor());
		labelSettingsDynDescription.setText(algo.description());
	}

	/**
	 * This method initializes comboChooseAlgo
	 * 
	 */
	private void createComboChooseAlgo() 
	{
		GridData gridData26 = new GridData();
		gridData26.horizontalAlignment = GridData.FILL;
		gridData26.grabExcessHorizontalSpace = true;
		gridData26.horizontalSpan = 2;
		gridData26.verticalAlignment = GridData.CENTER;
		comboChooseAlgo = new Combo(groupSettingsAlgorithm, SWT.READ_ONLY);
		comboChooseAlgo.setLayoutData(gridData26);
		comboChooseAlgo.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						for (AlgorithmSettings d : AlgorithmSettings.values()) {
							if (comboChooseAlgo.getSelectionIndex() == d.id()) {
								stopMeasurementResetButtons();
								algo = d;
								labelSettingsDynName.setText(algo.algoName());
								labelSettingsDynAutor.setText(algo.autor());
								labelSettingsDynDescription.setText(algo.description());
								if(d.id()== 1){
									RLCAlgoSelected = true;
									buttonCalibration.setEnabled(true);
									labelbuttonCalibration.setEnabled(true);
									buttonMeasureLoop.setEnabled(false);
								}
								else{
									RLCAlgoSelected = false;
									buttonCalibration.setEnabled(false);
									labelbuttonCalibration.setEnabled(false);
									buttonMeasureLoop.setEnabled(true);
									
									switch (comboFrequency.getSelectionIndex()) {
									case 0:
										labelStatusbarFrequency.setText("Frequency: " + 1000 + " Hz");
										break;
									case 1:
										labelStatusbarFrequency.setText("Frequency: " + 1250 + " Hz");
										break;
									case 2:
										labelStatusbarFrequency.setText("Frequency: " + 2250 + " Hz");
										break;
									}
												
								}
							}
						}
					}

					public void widgetDefaultSelected(
							org.eclipse.swt.events.SelectionEvent e) {
					}
				});
		for (AlgorithmSettings d : AlgorithmSettings.values()) {
			comboChooseAlgo.add(d.algoName());
		}
		comboChooseAlgo.select(0);
	}

	/**
	 * This method initializes canvasSchema
	 * 
	 */
	private void createCanvasSchema() 
	{
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.grabExcessVerticalSpace = true;
		gridData11.verticalAlignment = GridData.FILL;
		canvasSchema = new Canvas(groupSMPic, SWT.NONE);
		canvasSchema.setLayoutData(gridData11);
		canvasSchema.setBackground(new Color(sShell.getDisplay(), 255, 255, 255));
		canvasSchema.addPaintListener(new org.eclipse.swt.events.PaintListener() {
					public void paintControl(org.eclipse.swt.events.PaintEvent e) {
						if (infoEvent != null) 
						{
							DecimalFormat df = new DecimalFormat("#0.000");
							switch (infoEvent.getTyp())
							{
								case RESISTOR:// resistor
									labelSMTyp.setText("type: resistor");
									// paint resistor
									if(infoEvent.getUnit().toString().equals("kOhm"))
									{
	                                    widerstand.calcColors((infoEvent.getValue() * 1000.0));
									}
									else
									{
										widerstand.calcColors(infoEvent.getValue());
									}
									e.gc.setBackground(new Color(sShell.getDisplay(), 137, 220, 254));
									e.gc.fillRectangle(120, 70, 300, 100);
									e.gc.setBackground(widerstand.getRing1C());
									e.gc.fillRectangle(140, 70, 40, 100);
									e.gc.setBackground(widerstand.getRing2C());
									e.gc.fillRectangle(210, 70, 40, 100);
									e.gc.setBackground(widerstand.getRingMC());
									e.gc.fillRectangle(280, 70, 40, 100);
									e.gc.setLineWidth(2);
									e.gc.drawRectangle(120, 70, 300, 100);
									e.gc.drawLine(40, 120, 120, 120);
									e.gc.drawLine(420, 120, 520, 120);
									break;
								case INDUCTOR:// inductor
									labelSMTyp.setText("type: inductor");
									e.gc.setFont(new Font(sShell.getDisplay(),"Sans", 22, SWT.BOLD));
									e.gc.drawText("R =", 285, 150);
									e.gc.drawText("X =", 70, 150);
									e.gc.setFont(new Font(sShell.getDisplay(),"Sans", 16, SWT.BOLD));
									e.gc.drawText("L", 93, 170);
									e.gc.setFont(new Font(sShell.getDisplay(),"Sans", 14, SWT.NORMAL));
									if (imagValue == 0 && realValue == 0) 
									{
										e.gc.drawText("NaN", 145, 155);
										e.gc.drawText("NaN", 345, 155);
									}
									else 
									{
										e.gc.drawText(String.valueOf(df.format(imagValue) + " Ohm"), 145, 155);
										e.gc.drawText(String.valueOf(df.format(realValue) + " Ohm"), 345, 155);
									}
									e.gc.setLineWidth(2);
									e.gc.drawLine(40, 70, 80, 70);
									e.gc.drawLine(250, 70, 280, 70);
									e.gc.drawLine(450, 70, 500, 70);
									e.gc.drawRectangle(280, 50, 170, 40);
									e.gc.setBackground(new Color(sShell.getDisplay(), 0, 0, 0));
									e.gc.fillRectangle(80, 50, 170, 40);
									break;
								case CAPACITOR:// capacitor
									labelSMTyp.setText("type: capacitor");
									e.gc.setFont(new Font(sShell.getDisplay(),"Sans", 22, SWT.BOLD));
									e.gc.drawText("R =", 285, 150);
									e.gc.drawText("X   =", 70, 150);
									e.gc.setFont(new Font(sShell.getDisplay(),"Sans", 16, SWT.BOLD));
									e.gc.drawText("C", 93, 170);
									e.gc.setFont(new Font(sShell.getDisplay(),"Sans", 14, SWT.NORMAL));
									if (imagValue == 0 && realValue == 0)
									{
										e.gc.drawText("NaN", 145, 155);
										e.gc.drawText("NaN", 345, 155);
									} 
									else 
									{
										e.gc.drawText(String.valueOf(df.format(imagValue) + " Ohm"), 145, 155);
										e.gc.drawText(String.valueOf(df.format(realValue) + " Ohm"), 345, 155);
									}
									e.gc.setLineWidth(2);
									e.gc.drawLine(40, 70, 130, 70);
									e.gc.drawLine(450, 70, 520, 70);
									e.gc.drawLine(130, 30, 130, 110);
									e.gc.drawLine(160, 30, 160, 110);
									e.gc.drawLine(160, 70, 280, 70);
									e.gc.drawRectangle(280, 50, 170, 40);
									break;
								default:
									System.out.println("");
							}
						} 
						else if (!startetFirstTime) 
						{
							startetFirstTime = true;
							e.gc.drawImage(new Image(sShell.getDisplay(),Application.class.getClassLoader().getResourceAsStream("images/titel_sw.gif")), 30, 30);
						}
					}
				});
	}

	/**
	 * This method initializes groupAdditionalInfos
	 * 
	 */
	private void createGroupAdditionalInfos() 
	{
		GridData gridData24 = new GridData();
		gridData24.grabExcessHorizontalSpace = true;
		gridData24.horizontalAlignment = GridData.FILL;
		gridData24.verticalAlignment = GridData.FILL;
		gridData24.grabExcessVerticalSpace = true;
		GridData gridData23 = new GridData();
		gridData23.grabExcessHorizontalSpace = true;
		gridData23.horizontalAlignment = GridData.FILL;
		gridData23.verticalAlignment = GridData.FILL;
		gridData23.grabExcessVerticalSpace = true;
		GridData gridData13 = new GridData();
		gridData13.horizontalAlignment = GridData.FILL;
		gridData13.verticalAlignment = GridData.FILL;
		groupAdditionalInfos = new Group(compositeExpertModeSettings, SWT.NONE);
		groupAdditionalInfos.setLayout(new GridLayout());
		groupAdditionalInfos.setText("Additional Infos");
		groupAdditionalInfos.setLayoutData(gridData13);
		tableAddiditonalInfos = new Table(groupAdditionalInfos, SWT.NONE);
		tableAddiditonalInfos.setHeaderVisible(true);
		tableAddiditonalInfos.setLayoutData(gridData24);
		tableAddiditonalInfos.setLinesVisible(true);
		TableColumn col1 = new TableColumn(tableAddiditonalInfos, SWT.LEFT);
		col1.setResizable(true);
		col1.setText("Name");
		col1.setWidth(120);
		TableColumn col2 = new TableColumn(tableAddiditonalInfos, SWT.LEFT);
		col2.setText("Value");
		col2.setWidth(120);
	}

	/**
	 * This method initializes dropdownSelectOutput
	 * 
	 */
	private void createDropdownSelectOutput() 
	{
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.verticalAlignment = GridData.CENTER;
		dropdownSelectOutput = new Combo(groupSoundcard, SWT.NONE);
		dropdownSelectOutput.setLayoutData(gridData6);
	}

	private void stopMeasurementResetButtons() 
	{
		mainApp.stopMeasurement();
		buttonMeasure.setText("Measure");
		buttonMeasureLoop.setEnabled(true);
		running = false;
		buttonMeasureLoop.setText("Measure (loop)");
		buttonMeasure.setEnabled(true);
		runningLoop = false;
	}
	
}
