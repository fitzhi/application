package com.fitzhi.data.internal;

/**
 * Enum containing the eligible criteria to lookup for a project.
 * 2 criterias are possible :
 * <ul>
 * <li>
 * <strong>Name</strong> : the name of the project
 * </li>
 * <li>
 * <strong>UrlRepository</strong> : the repository url of the project
 * </li>
 * </ul>
 */
public enum ProjectLookupCriteria {
    Name ("name"),
    UrlRepository ("urlRepostory");

    private String name = "";

    /**
     * @param name
     */
    ProjectLookupCriteria(String name){
      this.name = name;
    }

    public String toString(){
      return name;
    }
}
