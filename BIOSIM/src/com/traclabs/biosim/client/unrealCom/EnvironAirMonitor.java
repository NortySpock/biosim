/**
 * Used for the unrealComm protocol. Routinely checks the water level(s) of the
 * BioSim simulation and then sends that data through a socket to the appropriate
 * UnrealScript NetTrigger call.
 *
 * WaterMonitor will communicate with the JavaGateway using the following text cmd:
 *
 *		netEnv <eventName> <O2Moles> <CO2Moles> <H2OMoles> <NitrogenMoles> <OtherMoles> <TotalMoles>
 *
 * This text string will trigger the appropriate netTrigger and activate <eventName>
 * with the given water amounts.
 *
 * @author: Travis R. Fischer
 */
 
package biosim.client.unrealCom;

import java.net.*;
import java.io.*;
import biosim.client.util.*;
import biosim.idl.simulation.environment.*;
import biosim.idl.framework.*;

public class EnvironAirMonitor extends Thread {
	
	/**
	 * @param mySocket - WaterMonitor instance of the open socket to the Unreal JavaGateway
	 */
	private Socket mySocket;
	
	/**
	 * @param cmdPrefix - String instructions sent to Unreal JavaGateway
	 */
	private String cmdPrefix = "netEnv ";
	
	/**
	 * @param myBioHolder - BioHold for the simulation information.
	 */
	private BioHolder myBioHolder;
	
	/**
	 * @param unrealStream - The output stream used to send commands to the Unreal JavaGateway
	 */
	private PrintWriter unrealStream;
	
	/**
	 * @param mySimEnvironment - All information associated with a simulated environment.
	 */
	private SimEnvironment mySimEnvironment;
	
	
	/**
	 * Constructor to create a new EnvironMonitor object.
	 * @param unrealSocket - open socket to the BioSim3D server JavaGateway
	 * @param eventName - used to communicate with the BioSim3D server based on pre-established BioSim3D Protocol
	 * @param bioHolder - BioHolder used to get the simulation's water information.
	 */
	EnvironAirMonitor(Socket unrealSocket, String eventName, BioHolder bioHolder,boolean isCrewEnv) {
		
		mySocket = unrealSocket;
		cmdPrefix = cmdPrefix.concat(eventName);
		cmdPrefix = cmdPrefix.concat(" ");		
		myBioHolder = bioHolder;
		
		try {
		
			unrealStream = new PrintWriter(mySocket.getOutputStream(),true);

		} catch(IOException e) {
		
			System.err.println("EnvironMonitor: Failed to establish socket I/O.");
			System.exit(1);
		}
		
		if(isCrewEnv) {
			
			mySimEnvironment = (SimEnvironment)(myBioHolder.theSimEnvironments.get(0));
			
		} else {
			
			mySimEnvironment = (SimEnvironment)(myBioHolder.theSimEnvironments.get(1));
		}
		
	}
	
	 
	public void run() {
		
		float co2Level;
		float o2Level;
		float nLevel;
		float h2oLevel;
		float otherLevel;
		float totalLevel;
		
		System.out.println("Starting EnvironMonitor thread");
		
		while(true) {
			
			co2Level = mySimEnvironment.getCO2Moles();
			o2Level = mySimEnvironment.getO2Moles();
			nLevel = mySimEnvironment.getNitrogenMoles();
			h2oLevel = mySimEnvironment.getWaterMoles();
			otherLevel = mySimEnvironment.getCO2Moles();
			totalLevel = mySimEnvironment.getTotalMoles();
			
			unrealStream.println(cmdPrefix+o2Level+" "+co2Level+" "+h2oLevel + " " +nLevel+" "+otherLevel + " " + totalLevel);
			try {
				sleep(1000);
			} catch(InterruptedException e) { }
		}
	}
}
			
		
		
	
	
	