/*
 * Copyright 2015-2016 the original author or authors.
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

package org.springframework.cloud.config.server.sweagle.environment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cloud.config.server.sweagle.EnableSweagleConfigServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

public class SweagleEnvironmentRepositoryTests {

	private ConfigurableApplicationContext context;

	@Before
	public void init() {

	}

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void defaultRepo() {
		// Prepare context

		// Prepare test

		// Test
	}

	@Test
	public void nestedPropertySource() {
		// Prepare context

		// Prepare test

		// Test
	}

	@Test
	public void repoWithProfileAndLabelInSource() {
		// Prepare context

		// Prepare test

		// Test
	}

	@Configuration
	@EnableSweagleConfigServer
	protected static class TestConfiguration {

	}

}
