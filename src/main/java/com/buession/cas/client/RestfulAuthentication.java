/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 
 * (the "License"); you may not use this file except in compliance with the License. You may obtain 
 * a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * =================================================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation. For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * +------------------------------------------------------------------------------------------------+
 * | License: http://cas-client-support.buession.com.cn/LICENSE 									|
 * | Author: Yong.Teng <webmaster@buession.com> 													|
 * | Copyright @ 2013-2014 Buession.com Inc.														|
 * +------------------------------------------------------------------------------------------------+
 */
package com.buession.cas.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CAS Restful 认证
 * 
 * @author Yong.Teng <webmaster@buession.com>
 */
public class RestfulAuthentication {

	/**
	 * CAS Server 配置
	 */
	private Configuration configuration;

	/**
	 * HttpClient
	 */
	private final static HttpClient httpClient = HttpClients.createDefault();

	/**
	 * HttpClient Request Configuration
	 */
	private static RequestConfig requestConfig;

	/**
	 * Represents a response to a validation request
	 */
	private Assertion assertion;

	private final static Logger logger = LoggerFactory.getLogger(RestfulAuthentication.class);

	/**
	 * @param configuration
	 *        CAS Server 配置
	 */
	public RestfulAuthentication(Configuration configuration) {
		setConfiguration(configuration);
	}

	public RestfulAuthentication() {

	}

	/**
	 * 返回 CAS Server 配置
	 * 
	 * @return CAS Server 配置
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * 设置 CAS Server 配置
	 * 
	 * @param configuration
	 *        CAS Server 配置
	 */
	public void setConfiguration(Configuration configuration) {
		Validate.notNull(configuration, "Configuration could not be null");

		this.configuration = configuration;

		requestConfig = RequestConfig.custom().setSocketTimeout(configuration.getSocketTimeout())
				.setConnectTimeout(configuration.getConnectTimeout())
				.setConnectionRequestTimeout(configuration.getConnectionRequestTimeout()).build(); // 设置请求和传输超时时间
	}

	/**
	 * 获取 TicketGrantingTicket
	 * 
	 * @param username
	 *        用户名
	 * @param password
	 *        密码
	 * @return TicketGrantingTicket
	 */
	public String getTicketGrantingTicket(final String username, final String password) {
		return getTicketGrantingTicket(username, password, null);
	}

	/**
	 * 获取 Ticket Granting Ticket
	 * 
	 * @param username
	 *        用户名
	 * @param password
	 *        密码
	 * @param parameters
	 *        附加请求参数
	 * @return Ticket Granting Ticket
	 */
	@SuppressWarnings("deprecation")
	public String getTicketGrantingTicket(final String username, final String password,
			Map<String, Object> parameters) {
		Validate.notBlank(username, "Username could not be null or empty");

		final String url = configuration.getServer() + "/v1/tickets";
		final HttpPost httpPost = new HttpPost(url);

		httpPost.setConfig(requestConfig);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		if (parameters != null) {
			for (Map.Entry<String, Object> e : parameters.entrySet()) {
				if (e.getValue() != null) {
					params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
				}
			}
		}

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			try {
				HttpResponse response = httpClient.execute(httpPost);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode >= 200 && statusCode < 300) {
					final String content = EntityUtils.toString(response.getEntity());
					final Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(
							content);

					if (matcher.matches()) {
						String ticketGrantingTicket = matcher.group(1);
						logger.debug("get Ticket Granting Ticket: {}", ticketGrantingTicket);
						return ticketGrantingTicket;
					} else {
						logger.warn("Could not find TicketGrantingTicket in response: {}", content);
					}
				} else {
					logger.warn("Invalid response code \"{}\" from CAS　Server", statusCode);
				}
			} catch (ClientProtocolException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			} finally {
				httpPost.releaseConnection();
			}
		} catch (UnsupportedEncodingException e) {
			logger.warn("Encoding {} could noe support", HTTP.UTF_8);
		} finally {
			httpPost.releaseConnection();
		}

		return null;
	}

	/**
	 * 获取 Service Ticket
	 * 
	 * @param ticketGrantingTicket
	 *        Ticket Granting Ticket
	 * @param service
	 * @return Service Ticket
	 */
	@SuppressWarnings("deprecation")
	public String getServiceTicket(final String ticketGrantingTicket, final String service) {
		Validate.notBlank(ticketGrantingTicket, "TicketGrantingTicket could not be null or empty");

		final String url = configuration.getServer() + "/v1/tickets/" + ticketGrantingTicket;
		final HttpPost httpPost = new HttpPost(url);

		httpPost.setConfig(requestConfig);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("service", service));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			try {
				HttpResponse response = httpClient.execute(httpPost);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode >= 200 && statusCode < 300) {
					final String content = EntityUtils.toString(response.getEntity());

					logger.debug("get Service Ticket: {}", content);
					return content;
				} else {
					logger.warn("Invalid response code \"{}\" from CAS　Server", statusCode);
				}
			} catch (ClientProtocolException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			} finally {
				httpPost.releaseConnection();
			}
		} catch (UnsupportedEncodingException e) {
			logger.warn("Encoding {} could noe support", HTTP.UTF_8);
		} finally {
			httpPost.releaseConnection();
		}

		return null;
	}

	/**
	 * 用户验证
	 * 
	 * @param serviceTicket
	 *        Service Ticket
	 * @param service
	 * @return 是否登录成功
	 */
	public boolean validate(final String serviceTicket, final String service) {
		Validate.notBlank(serviceTicket, "ServiceTicket could not be null or empty");

		Cas20ProxyTicketValidator proxyTicketValidator = new Cas20ProxyTicketValidator(
				configuration.getServer());
		try {
			assertion = proxyTicketValidator.validate(serviceTicket, service);
			return assertion == null ? false : true;
		} catch (TicketValidationException e) {
			logger.warn("Ticket <{}> validate failure: {}", serviceTicket, e.getMessage());
		}

		return false;
	}

	/**
	 * 用户退出登录
	 * 
	 * @param ticketGrantingTicket
	 *        Ticket Granting Ticket
	 * @return boolean
	 */
	public boolean logout(final String ticketGrantingTicket) {
		Validate.notBlank(ticketGrantingTicket, "TicketGrantingTicket could not be null or empty");

		final String url = configuration.getServer() + "/v1/tickets/" + ticketGrantingTicket;
		final HttpDelete httpDelete = new HttpDelete(url);

		httpDelete.setConfig(requestConfig);

		try {
			HttpResponse response = httpClient.execute(httpDelete);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 200 && statusCode < 300) {
				return true;
			} else {
				logger.warn("Invalid response code \"{}\" from CAS　Server", statusCode);
			}
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			httpDelete.releaseConnection();
		}

		return false;
	}

	/**
	 * return Represents a response to a validation request
	 * 
	 * @return Represents a response to a validation request
	 */
	public Assertion getAssertion() {
		return assertion;
	}

}