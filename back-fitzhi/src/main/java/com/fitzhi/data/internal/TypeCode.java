package com.fitzhi.data.internal;

public enum TypeCode {
    Annotation ("Annotation"),
    NumberOfLines ("NumberOfLines");
  
    private String name = "";

    /**
     * @param name
     */
    TypeCode(String name){
      this.name = name;
    }

    public String toString(){
      return name;
    }
 
}
