package biosim.server.sensor.air;

import biosim.server.sensor.framework.*;
import biosim.idl.sensor.air.*;
import biosim.idl.framework.*;
import biosim.idl.simulation.air.*;

public abstract class CO2StoreSensorImpl extends GenericSensorImpl implements CO2StoreSensorOperations{
	private CO2Store myCO2Store;
	
	public CO2StoreSensorImpl(int pID){
		super(pID);
	}

	protected abstract void gatherData();
	protected abstract void notifyListeners();

	public void setInput(CO2Store source){
		myCO2Store = source;
	}
	
	public CO2Store getInput(){
		return myCO2Store;
	}
	
	public float getMax(){
		return myCO2Store.getCapacity();
	}
	
	protected BioModule getInputModule(){
		return (BioModule)(getInput());
	}
	
	/**
	* Returns the name of this module (CO2StoreSensorImpl)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "CO2StoreSensor"+getID();
	}
}
