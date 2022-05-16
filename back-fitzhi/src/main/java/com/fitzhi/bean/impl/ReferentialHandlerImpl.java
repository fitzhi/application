package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;

import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.List;

import com.fitzhi.bean.ReferentialHandler;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReferentialHandlerImpl<T> implements ReferentialHandler<T> {
    
	/**
	 * Directory where the referential data are stored.
	 */
	@Value("${referential.dir}")
	private String referentialDir;

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

    @Override
	public List<T> loadReferential(String filename, TypeToken<List<T>> typeToken) throws ApplicationException {

		final File file = new File (referentialDir + filename); 
		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading the file %s", file.getAbsolutePath()));
		}
		
		try (FileReader fr = new FileReader(file)) {
			List<T> list = gson.fromJson(fr, typeToken.getType());
			return list;
		} catch (final Exception e) {
			log.error(MessageFormat.format(MESSAGE_IO_ERROR, file.getAbsolutePath()), e); 
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, file.getAbsolutePath()), e);
		}
	}
}
