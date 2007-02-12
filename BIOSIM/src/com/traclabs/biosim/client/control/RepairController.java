package com.traclabs.biosim.client.control;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.traclabs.biosim.client.util.BioHolder;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.idl.framework.BioDriver;
import com.traclabs.biosim.idl.sensor.framework.GenericSensor;
import com.traclabs.biosim.idl.simulation.crew.CrewPerson;
import com.traclabs.biosim.idl.simulation.environment.SimEnvironment;
import com.traclabs.biosim.util.CommandLineUtils;
import com.traclabs.biosim.util.OrbUtils;

/**
 * @author Haibei Jiang
 * A controller for stochastic performance and random failure modeling
 */

/*
To compile:
1) build biosim (type "ant" in BIOSIM_HOME directory)
To run: (assuming BIOSIM_HOME/bin is in your path)
1)type "run-nameserver"
2)type "run-server -xml=test/CEVConfig.xml"
3)type "java -classpath $BIOSIM_HOME/lib/xerces/xercesImpl.jar:$BIOSIM_HOME/lib/log4j/log4j.jar:$BIOSIM_HOME/lib/jacorb/jacorb.jar:$BIOSIM_HOME/lib/jacorb/logkit.jar:$BIOSIM_HOME/lib/jacorb/avalon-framework.jar:$BIOSIM_HOME/lib/jacorb:$BIOSIM_HOME/build:$BIOSIM_HOME/resources -Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton -DORBInitRef.NameService=file:$BIOSIM_HOME/tmp/ns/ior.txt com.traclabs.biosim.client.control.RepairController"
*/

public class RepairController implements BiosimController {
	
	private static String CONFIGURATION_FILE = "/BIOSIM/resources/com/traclabs/biosim/server/framework/configuration/reliability/CEVconfig.xml";

	private static final String LOG_FILE = "/home/RepairController/RepairControllerResults.txt";
	
	private BioDriver myBioDriver;

	private BioHolder myBioHolder;

	private Logger myLogger;
	
	private CrewPerson myCrewPerson;
	
	private SimEnvironment crewEnvironment;

	private GenericSensor myO2PressureSensor;

	private GenericSensor myO2ConcentrationSensor;
	
	private GenericSensor myCO2PressureSensor;

	private GenericSensor myNitrogenPressureSensor;

	private GenericSensor myVaporPressureSensor;
	
	private int RepairDelay=0;
	
	private boolean logToFile = false;
	FileOutputStream out;
	private PrintStream myOutput;
	
	
	public RepairController(boolean log) {
		logToFile = log;
		OrbUtils.initializeLog();
		myLogger = Logger.getLogger(this.getClass());

		try{
			out = new FileOutputStream("Configuration.txt", true);		
		}catch (Exception e){
			System.out.println("Can't open Configuration.txt.");
		}

		if (logToFile) {
			try {
				myOutput = new PrintStream(new FileOutputStream(LOG_FILE, true));
			} 
			catch (FileNotFoundException e) {
						e.printStackTrace();
			}
		} else
			myOutput = System.out;
	}

	public static void main(String[] args) {
		boolean logToFile = Boolean.parseBoolean(CommandLineUtils.getOptionValueFromArgs(args, "log"));
		RepairController myController = new RepairController(logToFile);
		myController.collectReferences();
		myController.runSim();
	}
	
	/**
	 * Collects references to BioModules we'll need to run/observer/poke the
	 * sim. The BioHolder is a utility for clients to easily access different
	 * parts of BioSim.
	 * 
	 */
	
	public void collectReferences() {
		BioHolderInitializer.setFile(CONFIGURATION_FILE);
		myBioHolder = BioHolderInitializer.getBioHolder();
		myBioDriver = myBioHolder.theBioDriver;
		crewEnvironment = myBioHolder.theSimEnvironments.get(0);
		SimEnvironment crewEnvironment = myBioHolder.theSimEnvironments.get(0);
		
		//Crew Failure Enabled
		myCrewPerson = myBioHolder.theCrewGroups.get(0).getCrewPerson("Bob Roberts");
		myBioHolder.theCrewGroups.get(0).isFailureEnabled();
		
		//Air Modules Failure Enabled
		myBioHolder.theCO2Stores.get(0).isFailureEnabled();
		myBioHolder.theVCCRModules.get(0).isFailureEnabled();
		myBioHolder.theO2Stores.get(0).isFailureEnabled();
		myBioHolder.theH2Stores.get(0).isFailureEnabled();
		myBioHolder.theOGSModules.get(0).isFailureEnabled();
		
		//Food Store Failure
		myBioHolder.theFoodStores.get(0).isFailureEnabled();
		
		//Crew Suvival Condition Sensors
		myO2ConcentrationSensor = myBioHolder.getSensorAttachedTo(
				myBioHolder.theGasConcentrationSensors, crewEnvironment.getO2Store());
		
		myO2PressureSensor = myBioHolder.getSensorAttachedTo(
				myBioHolder.theGasPressureSensors,crewEnvironment.getO2Store());
		
		myCO2PressureSensor = myBioHolder.getSensorAttachedTo(
				myBioHolder.theGasPressureSensors, crewEnvironment.getCO2Store());
		
		myNitrogenPressureSensor = myBioHolder.getSensorAttachedTo(
				myBioHolder.theGasPressureSensors, crewEnvironment.getNitrogenStore());
		
		myVaporPressureSensor = myBioHolder.getSensorAttachedTo(
				myBioHolder.theGasPressureSensors, crewEnvironment.getVaporStore());
			
	}
	
	/**
	 * Main loop of controller. Pauses the simulation, then ticks it one tick at
	 * a time until end condition is met.
	 */
	public void runSim() {
		myBioDriver.setPauseSimulation(true);
		myBioDriver.startSimulation();
		myLogger.info("Controller starting run");
		do {
			myBioDriver.advanceOneTick();
			if(crewShouldDie())
				myBioHolder.theCrewGroups.get(0).killCrew();
			stepSim();
		} while (!myBioDriver.isDone());	
		printResults();	
		myBioDriver.endSimulation();
		myLogger.info("Controller ended on tick " + myBioDriver.getTicks());
	}

	/**
	 * If the crew is dead, end the simulation.
	 */	
	private boolean crewShouldDie() {
		if (myO2PressureSensor.getValue() < 10.13){
			myLogger.info("killing crew for low oxygen: "+myO2PressureSensor.getValue());
			return true;
		}
		else if(myO2PressureSensor.getValue() > 30.39){
			myLogger.info("killing crew for high oxygen: "+myO2PressureSensor.getValue());
			return true;
		}
		else if(myCO2PressureSensor.getValue() > 1) {
			myLogger.info("killing crew for high CO2: "+myO2PressureSensor.getValue());
			return true;
		}
		else
			return false;
	}


	public void printResults() {
		FileOutputStream out; 
		PrintStream myOutput; 
		try {
			out = new FileOutputStream("ComponentPerformance.txt", true);
			myOutput = new PrintStream(out);
			myOutput.println();
			myOutput.println("Ticks H2ProducerOGS O2ProducerOGS PotableWaterConsumeOGS PowerConsumerOGS PowerConsumerVCCR AirConsumerVCCR AirProducerVCCR CO2ProducerVCCR O2ConsumerInjector O2ProdurerInjector DirtyWaterConsumer GreyWaterConsumer PotableWaterProducer PowerConsumerWaterRS");
			//Ticks
			myOutput.print(myBioDriver.getTicks() + "     ");// Ticks
			//OGS			
			myOutput.print(myBioHolder.theOGSModules.get(0).getH2ProducerDefinition() + "     ");// H2ProducerOGS
			myOutput.print(myBioHolder.theOGSModules.get(0).getPotableWaterConsumerDefinition() + "     ");// O2ProducerOGS
			myOutput.print(	myBioHolder.theOGSModules.get(0).getPotableWaterConsumerDefinition() + "  ");// PotableWaterConsumeOGS
			myOutput.print(myBioHolder.theOGSModules.get(0).getPowerConsumerDefinition() + "  "); // PowerConsumerOGS
			//VCCR
			myOutput.print(myBioHolder.theVCCRModules.get(0).getPowerConsumerDefinition() + "   "); //PowerConsumerVCCR
			myOutput.print(myBioHolder.theVCCRModules.get(0).getAirConsumerDefinition()+ "   ");//AirConsumerVCCR
			myOutput.print(myBioHolder.theVCCRModules.get(0).getAirProducerDefinition()+ "   ");//AirProducerVCCR
			myOutput.print(myBioHolder.theVCCRModules.get(0).getCO2ProducerDefinition()+ "   ");//CO2ProducerVCCR
			// Injector
			myOutput.print(myBioHolder.theInjectors.get(0).getO2ConsumerDefinition() + "   "); // O2ConsumerInjector
			myOutput.print(myBioHolder.theInjectors.get(0).getO2ProducerDefinition() + "   ");//O2ProducerINjector
			//WaterRS
			myOutput.print(myBioHolder.theWaterRSModules.get(0).getDirtyWaterConsumerDefinition() + "   "); //DirtyWaterConsumer
			myOutput.print(myBioHolder.theWaterRSModules.get(0).getGreyWaterConsumerDefinition() + "   "); //GreyWaterConsumer
			myOutput.print(myBioHolder.theWaterRSModules.get(0).getPotableWaterProducerDefinition() + "   "); //PortableWaterProducer
			myOutput.print(myBioHolder.theWaterRSModules.get(0).getPowerConsumerDefinition() + "   "); //PowerConsumer
			
			myOutput.flush();
		} 
		catch (FileNotFoundException e) {
					e.printStackTrace();
		}
	} 
	
		public boolean CheckFailure(){
			if (myBioHolder.theCO2Stores.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by CO2Store " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theVCCRModules.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by VCCR " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theO2Stores.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by O2Store " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theOGSModules.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by OGS " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theH2Stores.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by H2Store " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theCrewGroups.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by Crew " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theFoodStores.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by FoodStore " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theInjectors.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by Injector " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.thePowerStores.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by PowerStore " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theDryWasteStores.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by DryWasteStore " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.thePotableWaterStores.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by PortableWaterStore " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theDirtyWaterStores.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by DirtyWaterStore " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theWaterRSModules.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by WaterRS " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			if (myBioHolder.theDirtyWaterStores.get(0).isMalfunctioning()){
				myLogger.info("Component failure caused by DirtyWaterStore " + " " + " at Tick " + myBioDriver.getTicks());
				return true;
			}
			else
				return false;
		}
			
		public void componentRepair(){
			if (myBioHolder.theCO2Stores.get(0).isMalfunctioning()){
				myBioHolder.theCO2Stores.get(0).reset();
				myLogger.info("Component CO2Store is repaired" + " " + " at Tick " + myBioDriver.getTicks());		
			}
			if (myBioHolder.theVCCRModules.get(0).isMalfunctioning()){
				myBioHolder.theVCCRModules.get(0).reset();
				myLogger.info("Component VCCR is repaired " + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theO2Stores.get(0).isMalfunctioning()){
				myBioHolder.theO2Stores.get(0).reset();
				myLogger.info("Component O2Store is repaired" + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theOGSModules.get(0).isMalfunctioning()){
				myBioHolder.theOGSModules.get(0).reset();
				myLogger.info("Component OGS is repaired " + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theH2Stores.get(0).isMalfunctioning()){
				myBioHolder.theH2Stores.get(0).reset();
				myLogger.info("Component H2Store is repaired" + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theCrewGroups.get(0).isMalfunctioning()){
				myBioHolder.theCrewGroups.get(0).reset();
				myLogger.info("Component Crew has recovered" + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theFoodStores.get(0).isMalfunctioning()){
				myBioHolder.theFoodStores.get(0).reset();
				myLogger.info("Component FoodStore is repaired " + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theInjectors.get(0).isMalfunctioning()){
				myBioHolder.theInjectors.get(0).reset();
				myLogger.info("Component Injector is repaired" + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.thePowerStores.get(0).isMalfunctioning()){
				myBioHolder.thePowerStores.get(0).reset();
				myLogger.info("Component PowerStore is repaired " + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theDryWasteStores.get(0).isMalfunctioning()){
				myBioHolder.theDryWasteStores.get(0).reset();
				myLogger.info("Component DryWasteStore is repaired" + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.thePotableWaterStores.get(0).isMalfunctioning()){
				myBioHolder.thePotableWaterStores.get(0).reset();
				myLogger.info("Component PortableWaterStore is repaired" + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theDirtyWaterStores.get(0).isMalfunctioning()){
				myBioHolder.theDirtyWaterStores.get(0).reset();
				myLogger.info("Component DirtyWaterStore is repaired" + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theWaterRSModules.get(0).isMalfunctioning()){
				myBioHolder.theWaterRSModules.get(0).reset();
				myLogger.info("Component WaterRS is repaired " + " " + " at Tick " + myBioDriver.getTicks());
			}
			if (myBioHolder.theDirtyWaterStores.get(0).isMalfunctioning()){
				myBioHolder.theDirtyWaterStores.get(0).reset();
				myLogger.info("Component failure caused by DirtyWaterStore " + " " + " at Tick " + myBioDriver.getTicks());
			}		
		}
		
		public void RecordSensor(){ //is necessary? or just output directly
			
//			OGS
			myBioHolder.theOGSModules.get(0).getH2ProducerDefinition();
			myBioHolder.theOGSModules.get(0).getO2ProducerDefinition();
			myBioHolder.theOGSModules.get(0).getPotableWaterConsumerDefinition();
			myBioHolder.theOGSModules.get(0).getPowerConsumerDefinition();

//			VCCR
			myBioHolder.theVCCRModules.get(0).getPowerConsumerDefinition();
			myBioHolder.theVCCRModules.get(0).getAirConsumerDefinition();
			myBioHolder.theVCCRModules.get(0).getAirProducerDefinition();
			myBioHolder.theVCCRModules.get(0).getCO2ProducerDefinition();

//			Injector
			myBioHolder.theInjectors.get(0).getO2ConsumerDefinition();
			myBioHolder.theInjectors.get(0).getO2ProducerDefinition();

//			WaterRS
			myBioHolder.theWaterRSModules.get(0).getDirtyWaterConsumerDefinition();
			myBioHolder.theWaterRSModules.get(0).getGreyWaterConsumerDefinition();
			myBioHolder.theWaterRSModules.get(0).getPotableWaterProducerDefinition();
			myBioHolder.theWaterRSModules.get(0).getPowerConsumerDefinition();	
		}
		
		/**
		 * Executed every tick.  Looks at a sensor, looks at an actuator,
		 * then increments the actuator.
		 */	

		public void stepSim() {
//			Check sensor to monitor stochastic performance	
//			Only monitor OGS, VCCR, Injector, WaterRS	
				RecordSensor();
//			Check failure to monitor component malfunction using a Boolean "CheckFailure"
//	        Report Failure and fix the failed component	using a function "componentRepair"
				if(CheckFailure()){
					if (RepairDelay!=0){   // Repair Delay is the time needed for repair activities 
						componentRepair();
						RepairDelay=0;
					}
					else {
					RepairDelay=1;	
					}
				}
			}
}

	


/*
//OGS 
	myBioHolder.theOGSModules.get(0).getH2ProducerDefinition();
	myBioHolder.theOGSModules.get(0).getO2ProducerDefinition();
	myBioHolder.theOGSModules.get(0).getPotableWaterConsumerDefinition();
	myBioHolder.theOGSModules.get(0).getPowerConsumerDefinition();

//VCCR
	myBioHolder.theVCCRModules.get(0).getPowerConsumerDefinition();
	myBioHolder.theVCCRModules.get(0).getAirConsumerDefinition();
	myBioHolder.theVCCRModules.get(0).getAirProducerDefinition();
	myBioHolder.theVCCRModules.get(0).getCO2ProducerDefinition();
	
// Injector
	myBioHolder.theInjectors.get(0).getO2ConsumerDefinition();
	myOutput.print(myBioHolder.theInjectors.get(0).getO2ProducerDefinition();

//WaterRS
	myBioHolder.theWaterRSModules.get(0).getDirtyWaterConsumerDefinition();
	myBioHolder.theWaterRSModules.get(0).getGreyWaterConsumerDefinition();
	myBioHolder.theWaterRSModules.get(0).getPotableWaterProducerDefinition();
	myBioHolder.theWaterRSModules.get(0).getPowerConsumerDefinition();
*/