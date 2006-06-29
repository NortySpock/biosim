package com.traclabs.biosim.client.control;

import org.apache.log4j.Logger;

import com.traclabs.biosim.client.util.BioHolder;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.idl.actuator.framework.GenericActuator;
import com.traclabs.biosim.idl.framework.BioDriver;
import com.traclabs.biosim.idl.sensor.framework.GenericSensor;
import com.traclabs.biosim.idl.simulation.environment.SimEnvironment;
import com.traclabs.biosim.idl.simulation.framework.Injector;
import com.traclabs.biosim.util.OrbUtils;


public class MurderController {
	private static String CONFIGURATION_FILE = "test/NigilConfigInit.xml";

	private BioDriver myBioDriver;

	private BioHolder myBioHolder;

	private Logger myLogger;

	private GenericSensor myO2ConcentrationSensor;

	private GenericSensor myCO2ConcentrationSensor;

	private GenericActuator myO2InjectorActuator;

	private GenericActuator myCO2InjectorActuator;

	public MurderController() {
		OrbUtils.initializeLog();
		myLogger = Logger.getLogger(this.getClass());
		collectReferences();
	}

	public static void main(String[] args) {
		MurderController myController = new MurderController();
		myController.runSim();
	}

	/**
	 * Collects references to BioModules we'll need
	 * to run/observer/poke the sim.  The BioHolder is 
	 * a utility for clients to easily access different parts 
	 * of BioSim.
	 *
	 */
	private void collectReferences() {
		BioHolderInitializer.setFile(CONFIGURATION_FILE);
		myBioHolder = BioHolderInitializer.getBioHolder();
		myBioDriver = myBioHolder.theBioDriver;
		
		Injector O2Injector = myBioHolder.theInjectors.get(0);
		Injector CO2Injector = myBioHolder.theInjectors.get(0);

		for (Injector currentInjector : myBioHolder.theInjectors)	{
			if (currentInjector.getModuleName().equals("O2Injector"))
				O2Injector = currentInjector;
				break;
		}
		for (Injector currentInjector : myBioHolder.theInjectors)	{
			if (currentInjector.getModuleName().equals("CO2Injector"))
				CO2Injector = currentInjector;
				break;
		}

		myO2InjectorActuator = (myBioHolder.getActuatorAttachedTo(
				myBioHolder.theO2InFlowRateActuators, O2Injector));

		myCO2InjectorActuator = (myBioHolder.getActuatorAttachedTo(myBioHolder.theCO2InFlowRateActuators, CO2Injector));

		SimEnvironment crewEnvironment = myBioHolder.theSimEnvironments.get(0);

		myO2ConcentrationSensor = (myBioHolder.getSensorAttachedTo(
				myBioHolder.theGasConcentrationSensors, crewEnvironment
						.getO2Store()));
		myCO2ConcentrationSensor = (myBioHolder.getSensorAttachedTo(myBioHolder.theGasConcentrationSensors, crewEnvironment.getCO2Store()));	
	}
	
	/**
	 * Main loop of controller.  Pauses the simulation, then
	 * ticks it one tick at a time until end condition is met.
	 */
	public void runSim() {
		myBioDriver.setPauseSimulation(true);
		myBioDriver.startSimulation();
		myLogger.info("Controller starting run");
		while (!endConditionMet())
			stepSim();
		//if we get here, the end condition has been met
		myBioDriver.endSimulation();
		myLogger.info("Controller ended on tick " + myBioDriver.getTicks());
	}
	
	/**
	 * If the oxygen in the cabin drifts below 10%, stop the sim.
	 */
	private boolean endConditionMet() {
		float oxygenPercentage = myO2ConcentrationSensor.getValue();
		float CO2Percentage = myCO2ConcentrationSensor.getValue();
		if ((oxygenPercentage < 0.10) || (oxygenPercentage > 0.30) || (CO2Percentage > .06))	{				
			myBioHolder.theCrewGroups.get(0).getCrewPerson("Nigil").kill();
			return true;
		}
		else	{
			return false;
		}
	}

	/**
	 * Executed every tick.  Looks at a sensor, looks at an actuator,
	 * then increments the actuator.
	 */
	public void stepSim() {
		//check sensor
		float sensorValue = myO2ConcentrationSensor.getValue();
		myLogger.info("sensor reading is " + sensorValue);
		//set actuator
		myLogger.info("incrementing actuator by 1");
		float actuatorValue = myO2InjectorActuator.getValue();
		myLogger.info("actuator set at " + myO2InjectorActuator.getValue());
		myO2InjectorActuator.setValue(actuatorValue + 1);
		// advancing the sim 1 tick
		myBioDriver.advanceOneTick();
	}

}