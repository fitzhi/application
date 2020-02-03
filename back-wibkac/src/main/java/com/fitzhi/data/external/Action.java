package com.fitzhi.data.external;

/**
 * Enumeration of actions
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public enum Action {
	  N ("no action done"),
	  U ("Updating existing ghost"),
	  A ("adding a new ghost"),
	  D ("Delete an existing gost");
	   
	  private String name = "";

	  /**
	   * @param name
	   */
	  Action(String name){
	    this.name = name;
	  }
	   
	  public String toString(){
	    return name;
	  }
}
