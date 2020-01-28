package com.tixhi.bean;

public interface DataSaverLifeCycle {

	 /**
	  * @return the locker to avoid any conflict between the saving process and all updates on the projects collection
	  */
	 Object getLocker();

	 /**
	  * @return {@code true} if the collection has been updated, {@code false} otherwise<br/>
	  * This boolean is checked by the dataSaver bean to proceed, or not, the collection saving. 
	  */
	 boolean isDataUpdated();

	 /**
	  * Inform the handler that the collection has been saved.
	  */
	 void dataAreSaved();

}
