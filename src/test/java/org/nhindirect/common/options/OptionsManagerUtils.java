package org.nhindirect.common.options;

public class OptionsManagerUtils 
{
	public static synchronized void clearOptionsManagerInstance()
	{
		OptionsManager.INSTANCE = null;
	}
	
	public static synchronized void clearOptionsManagerOptions()
	{
		OptionsManager.getInstance().options.clear();
	}
}
