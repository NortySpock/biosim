package biosim.client.framework;

import biosim.client.framework.gui.*;
import javax.swing.*;

public class TestDriver
{
	public static void main(String args[]) throws java.lang.InterruptedException
	{
		SimDesktop newDesktop = new SimDesktop();
		newDesktop.setSize(1024, 768);
		newDesktop.setVisible(true);
	}
}

