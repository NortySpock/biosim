package biosim.server.simulation.air;

import biosim.idl.simulation.air.*;
import biosim.server.simulation.framework.*;
/**
 * The CO2 Store Implementation.  Used by the AirRS to store excess CO2 for plants.
 * Not really used right now.
 *
 * @author    Scott Bell
 */

public class CO2StoreImpl extends StoreImpl implements CO2StoreOperations {
	
	public CO2StoreImpl(int pID, String pName){
		super(pID, pName);
	}
	
}
