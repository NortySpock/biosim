package biosim.server.sensor.water;

import biosim.server.sensor.framework.*;
import biosim.idl.sensor.water.*;
import biosim.idl.framework.*;
import biosim.idl.simulation.water.*;

public abstract class WaterRSSensorImpl extends GenericSensorImpl implements WaterRSSensorOperations{
	protected WaterRS myWaterRS;
	
	public WaterRSSensorImpl(int pID, String pName){
		super(pID, pName);
	}

	protected abstract void gatherData();
	protected abstract void notifyListeners();

	public void setInput(WaterRS source){
		myWaterRS = source;
	}
	
	public WaterRS getInput(){
		return myWaterRS;
	}
	
	protected BioModule getInputModule(){
		return (BioModule)(getInput());
	}
}
