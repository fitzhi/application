package com.fitzhi.data.internal;

public enum TypeCode {
    SpringAnnotation ("Spring annotation");
  
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
