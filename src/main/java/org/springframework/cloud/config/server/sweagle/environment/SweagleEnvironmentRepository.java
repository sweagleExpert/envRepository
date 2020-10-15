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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * Implementation of {@link EnvironmentRepository} that is backed by Sweagle.
 *
 * @author Kyriakos Mandalas
 * @maintainer Dimitris Finas
 *
 */
@ConfigurationProperties("spring.cloud.config.server.sweagle")
@Validated
public class SweagleEnvironmentRepository implements EnvironmentRepository, Ordered {

	private int order = Ordered.LOWEST_PRECEDENCE;

	/** Properties formatting style. Defaults to JSON */
	private String exportFormat = "JSON";

	/** Protocol scheme. Defaults to https. */
	private String scheme = "https";

	/** Sweagle host. Defaults to testing.sweagle.com. */
	@NotEmpty
	private String host = "testing.sweagle.com";

	/** Sweagle port. Defaults to 443. */
	@Range(min = 1, max = 65535)
	private int port = 443;

	/** Authorization credentials */
	@NotEmpty
	private String token;

	/** Sweagle configdata-set */
	@NotEmpty
	private String configdataset;

	/** Sweagle parser (exporter) */
	@NotEmpty
	private String parser;

	/** The REST template */
	final private RestTemplate restTemplate;

	@Autowired
	public SweagleEnvironmentRepository(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setExportFormat(String exportFormat) {
		this.exportFormat = exportFormat;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setconfigdataset(String configdataset) {
		this.configdataset = configdataset;
	}

	public void setParser(String parser) {
		this.parser = parser;
	}

	@Override
	public Environment findOne(String application, String profile, String label) {
		//final String token = getAccessToken();
		final String config = application;

		// TODO: example handing. Demo only:
		if (StringUtils.isEmpty(label)) {
			label = "master";
		}
		if (StringUtils.isEmpty(profile)) {
			profile = "default";
		}
		if (!profile.startsWith("default")) {
			profile = "default," + profile;
		}
		String[] profiles = StringUtils.commaDelimitedListToStringArray(profile);
		Environment environment = new Environment(application, profiles, label, null, null);
		List<String> applications = new ArrayList<String>(new LinkedHashSet<>(Arrays.asList(StringUtils.commaDelimitedListToStringArray(config))));
		List<String> envs = new ArrayList<String>(new LinkedHashSet<>(Arrays.asList(profiles)));
		Collections.reverse(applications);
		Collections.reverse(envs);
		for (String app : applications) {
			for (String env : envs) {
				String data = sweagle(app, env, label, token);
				if (data != null) {
					// data is in json format of which, yaml is a superset, so parse
					final YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
					yaml.setResources(new ByteArrayResource(data.getBytes()));
					Properties properties = yaml.getObject();
					if (!properties.isEmpty()) {
						environment.add(new PropertySource(app + "-" + env, properties));
					}
				}
			}
		}

		return environment;
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * Performs REST cal towards Sweagle to retrieve app settings
	 *
	 * @param app the app name
	 * @param env the environment name (UAT-PROD etc)
	 * @param label optional additional indicator
	 *
	 * @return a JSON string holding the app's properties
	 */
	private String sweagle(String app, String env, String label, String token) {

		final String url = String.format("%s://%s:%s/api/v1/tenant/metadata-parser/parse?mds=%s&parser=%s&args=%s&format=%s",
						this.scheme, this.host, this.port, configdataset, parser, env + "," + app, exportFormat);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "bearer " + token);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), String.class);

			HttpStatus status = response.getStatusCode();
			if (HttpStatus.OK == status) {
				return response.getBody();
			}
		} catch (HttpStatusCodeException e) {
			if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
				return null;
			}
			throw e;
		}

		return null;
	}

	// no more used, but kept in case customer prefer auth. instead of token strategy
	private String getAccessToken() {
		final String url = String.format("http://%s:%s/oauth/token", this.host, this.port);
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", token);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "password");
		body.add("username", username);
		body.add("password", password);

		HttpEntity<Object> httpEntity = new HttpEntity<>(body, httpHeaders);
		// TODO: example handing. Demo only:
		ResponseEntity<JsonNode> accessTokenNode = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JsonNode.class);

		return accessTokenNode.getBody().get("access_token").asText();
	}

}
