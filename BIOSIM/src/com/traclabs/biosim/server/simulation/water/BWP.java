package biosim.server.water;

import biosim.server.util.*;
import biosim.idl.power.*;
import biosim.idl.water.*;
/**
 * The Biological Waste Processor is the first stage of water purification.  It takes dirty/grey water, filters it some, and
 * sends the water to the RO
 *
 * @author    Scott Bell
 */

public class BWP extends WaterRSSubSystem{
	//The subsystem to send the water to next
	private RO myRO;
	private AES myAES;
	private DirtyWaterStore myDirtyWaterStore;
	private GreyWaterStore myGreyWaterStore;
	private float currentDirtyWaterConsumed = 0f;
	private float currentGreyWaterConsumed = 0f;
	private float currentROWaterProduced = 0f;
	private float currentAESWaterProduced = 0f;
	private final float NORMAL_WATER_NEEDED = waterNeeded;
	private final float RO_DISABLED_WATER_NEEDED = new Double(waterNeeded * 0.15f).floatValue();
	private final float BOTH_DISABLED_WATER_NEEDED = 0f;
	

	/**
	* Constructor that creates the BWP
	* @param pWaterRSImpl The Water RS system the BWP is contained in
	*/
	public BWP(WaterRSImpl pWaterRSImpl){
		super(pWaterRSImpl);
	}

	/**
	* Collects references to subsystems needed for putting/getting resources
	*/
	private void collectReferences(){
		if (!hasCollectedReferences){
			try{
				myRO = myWaterRS.getRO();
				myAES = myWaterRS.getAES();
				myDirtyWaterStore = DirtyWaterStoreHelper.narrow(OrbUtils.getNCRef().resolve_str("DirtyWaterStore"));
				myGreyWaterStore = GreyWaterStoreHelper.narrow(OrbUtils.getNCRef().resolve_str("GreyWaterStore"));
				myPowerStore = PowerStoreHelper.narrow(OrbUtils.getNCRef().resolve_str("PowerStore"));
				hasCollectedReferences = true;
			}
			catch (org.omg.CORBA.UserException e){
				e.printStackTrace(System.out);
			}
		}
	}
	
	public float getDirtyWaterConsumed(){
		return currentDirtyWaterConsumed;
	}
	
	public float getGreyWaterConsumed(){
		return currentGreyWaterConsumed;
	}

	/**
	* Attempts to collect enough water from the Dirty Water Store to put into the BWP.
	* If the Dirty Water Store can't provide enough, the Water RS supplements from the Grey Water Store.
	*/
	private void gatherWater(){
		if (!myRO.isEnabled()){
			if (myAES.isEnabled())
				waterNeeded = RO_DISABLED_WATER_NEEDED;
			else
				waterNeeded = BOTH_DISABLED_WATER_NEEDED;
		}
		else{
			waterNeeded = NORMAL_WATER_NEEDED;
		}
		//draw as much as we can from dirty water
		if (myDirtyWaterStore.getLevel() >= waterNeeded){
			currentDirtyWaterConsumed = myDirtyWaterStore.take(waterNeeded);
			//currentGreyWaterConsumed = 0;
		}
		//draw from both
		else{
			currentDirtyWaterConsumed = myDirtyWaterStore.take(waterNeeded);
			//currentGreyWaterConsumed = myGreyWaterStore.take(waterNeeded - currentDirtyWaterConsumed);
		}
		currentGreyWaterConsumed = 0;
		addWater(currentDirtyWaterConsumed + currentGreyWaterConsumed);
	}

	/**
	* Flushes the water from this subsystem to the RO
	*/
	private void pushWater(){
		if (myRO.isEnabled()){
			currentROWaterProduced = waterLevel;
			currentAESWaterProduced = 0;
			myRO.addWater(currentROWaterProduced);
		}
		else if (myAES.isEnabled()){
			currentROWaterProduced = 0;
			currentAESWaterProduced = waterLevel;
			myAES.addWater(currentAESWaterProduced);
		}
		else{
			//dump water! no subsystem enabled to send water to
			currentAESWaterProduced = 0f;
			currentROWaterProduced = 0f;
		}
		waterLevel = 0;
	}
	
	public float getROWaterProduced(){
		return currentROWaterProduced;
	}
	
	public float getAESWaterProduced(){
		return currentAESWaterProduced;
	}

	/**
	* In one tick, this subsystem:
	* 1) Collects references (if needed).
	* 2) Flushes the water from this subsystem to the RO.
	*/
	public void tick(){
		collectReferences();
		gatherPower();
		if (hasEnoughPower){
			gatherWater();
			pushWater();
		}
		else{
			currentROWaterProduced = 0f;
			currentAESWaterProduced = 0f;
			currentDirtyWaterConsumed = 0f;
			currentGreyWaterConsumed = 0f;
		}
	}
	
	public void reset(){
		currentDirtyWaterConsumed = 0f;
		currentGreyWaterConsumed = 0f;
		currentPowerConsumed = 0;
		currentROWaterProduced = 0f;
		currentAESWaterProduced = 0f;
		hasEnoughPower = false;
		hasEnoughWater = false;
		waterLevel = 0;
	}
}
