package biosim.client.food.gui;

import java.awt.*;
import biosim.client.framework.gui.*;

/**
 * This is the JPanel that displays a chart about the Food/Biomass
 *
 * @author    Scott Bell
 */
public class FoodChartPanel extends BioTabPanel
{
	private FoodStorePanel myFoodStorePanel;

	public FoodChartPanel() {
		setLayout(new BorderLayout());
		myFoodStorePanel = new FoodStorePanel();
		add(myFoodStorePanel, BorderLayout.CENTER);
	}
	
	public void visibilityChange(boolean nowVisible){
		myFoodStorePanel.visibilityChange(nowVisible);
	}
}
