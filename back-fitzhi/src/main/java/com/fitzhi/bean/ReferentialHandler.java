package com.fitzhi.bean;

import java.util.List;

import com.fitzhi.exception.ApplicationException;
import com.google.gson.reflect.TypeToken;

public interface ReferentialHandler<T> {
    
    /**
     * Load and parse a referential JSON file from the file system.
     * The content of the file is returned into a list. 
     * 
     * @param filename the <b>name</b> of the referential file <i>(without path)</i>
     * @param typeToken the type of Generic. 
     * <p>
     * This parameter is linked to java Type erasure. Generics are only known at compile time.<br/>
     * Technical reference :
     * {@link https://stackoverflow.com/questions/14503881/strange-behavior-when-deserializing-nested-generic-classes-with-gson/14506181#14506181}
     * </p>
     * @return the resulting list
     * @throws ApplicationException thrown if any problem occurs, 
     * most probably an {@link java.io.IOException} if the file does not exist, or a {@link ClassCastException} if the content does not match the expected format.
     */
	List<T> loadReferential(String filename, TypeToken<List<T>> typeToken) throws ApplicationException;

}
