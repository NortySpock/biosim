package biosim.server.simulation.food;

import biosim.idl.simulation.food.*;
/**
 * Wheat
 * @author    Scott Bell
 */

public class Wheat extends Erectophile{
	public Wheat(ShelfImpl pShelfImpl){
		super(pShelfImpl);
		canopyClosureConstants[0] = 95488f;
		canopyClosureConstants[1] = 1068.6f;
		canopyClosureConstants[6] = 15.977f;
		canopyClosureConstants[10] = 0.3419f;
		canopyClosureConstants[11] = 0.00019733f;
		canopyClosureConstants[15] = -0.00019076f;

		canopyQYConstants[6] = 0.044793f;
		canopyQYConstants[7] = 0.000051583f;
		canopyQYConstants[8] = -0.000000020724f;
		canopyQYConstants[11] = -0.0000051946f;
		canopyQYConstants[17] = -0.0000000000049303f;
		canopyQYConstants[18] = 0.0000000000000022255f;
	}

	public PlantType getPlantType(){
		return PlantType.WHEAT;
	}

	public float getPPFNeeded(){
		float cropArea = myShelfImpl.getCropAreaUsed();
		float constantPPF = 115f; //in moles per meters squared days
		return (constantPPF * cropArea) / 24f;
	}
	
	protected float getCarbonUseEfficiency24(){
		return 0.64f;
	}

	protected float getBCF(){
		return 0.44f;
	}

	protected float getPhotoperiod(){
		return 20f;
	}
	
	protected float getTimeAtOrganFormation(){
		return 34f;
	}

	protected float getN(){
		return 1.0f;
	}

	protected float getCQYMin(){
		return .001f;
	}

	protected float getTimeAtCanopySenescence(){
		return 33f;
	}

	protected float getTimeAtCropMaturity(){
		return 62f;
	}

	protected float getOPF(){
		return 1.07f;
	}

	protected float getFreshFactor(){
		return 6.4546f;
	}

	public static float getFractionOfEdibleBiomass(){
		return 1.00f;
	}
	
	/**
	 * Returns calories per kilogram
	*/
	public static float getCaloriesPerKilogram(){
		return 3300f;
	}
	
	protected float getCurrentFractionOfEdibleBiomass(){
		if (myAge > getTimeAtOrganFormation())
			return getFractionOfEdibleBiomass();
		else
			return 0f;
	}

	protected float getEdibleFreshBasisWaterContent(){
		return 12f;
	}

	protected float getInedibleFreshBasisWaterContent(){
		return 90f;
	}
}
