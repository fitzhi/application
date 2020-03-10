/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fitzhi.service.impl.storageservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

/**
 * <p>Tests of the FileSystemStorageService.</p>
 * <i>(based on the spring documentation)</i>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class FileSystemStorageServiceTests {

    private static final String HELLO_WORLD = "Hello World";
	private static final String FOO_TXT = "foo.txt";
	private StorageProperties properties = new ApplicationStorageProperties();
    private FileSystemStorageService service;

    @Before
    public void init() {
    	
        properties.setLocation("target/files/" + new Random().nextInt());
        service = new FileSystemStorageService(properties);
        service.init();
    }

    @Test
    public void loadNonExistent() {
        assertThat(service.load(FOO_TXT)).doesNotExist();
    }

    @Test
    public void saveAndLoad() {
        service.store(new MockMultipartFile("foo", FOO_TXT, MediaType.TEXT_PLAIN_VALUE,
                HELLO_WORLD.getBytes()), FOO_TXT);
        assertThat(service.load(FOO_TXT)).exists();
    }

    @Test(expected = StorageException.class)
    public void saveNotPermitted() {
        service.store(new MockMultipartFile("foo", "../foo.txt",
                MediaType.TEXT_PLAIN_VALUE, HELLO_WORLD.getBytes()), FOO_TXT);
    }

    @Test
    public void savePermitted() {
        service.store(new MockMultipartFile("foo", "bar/../foo.txt",
                MediaType.TEXT_PLAIN_VALUE, HELLO_WORLD.getBytes()), FOO_TXT);
    }

}
