package com.fitzhi.data.internal;

public enum TypeCodePattern {
    SpringAnnotation ("Spring annotation");
  
    private String name = "";

    /**
     * @param name
     */
    TypeCodePattern(String name){
      this.name = name;
    }

    public String toString(){
      return name;
    }
 
}
