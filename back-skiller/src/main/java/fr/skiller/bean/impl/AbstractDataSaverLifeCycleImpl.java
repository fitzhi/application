package fr.skiller.bean.impl;

import fr.skiller.bean.DataSaverLifeCycle;

public abstract class AbstractDataSaverLifeCycleImpl implements DataSaverLifeCycle {

	/**
	 * {@code true} if the data have been updated, {@code false} otherwise<br/>
	 * This boolean is checked by the dataSaver to proceed, or not, the save 
	 */
	public Boolean dataUpdated = false;

	/**
	 * To avoid any conflict between the saving process and any update on the collection.
	 */
	public final Object lockDataUpdated = new Object();

	@Override
	public Object getLocker() {
		return lockDataUpdated;
	}

	@Override
	public void dataAreSaved() {
		dataUpdated = false;
	}

	@Override
	public boolean isDataUpdated() {
		return dataUpdated;
	}
	
}
