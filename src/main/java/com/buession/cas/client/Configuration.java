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
 * | License: http://cas-client-support.buession.com.cn/LICENSE 										|
 * | Author: Yong.Teng <webmaster@buession.com> 													|
 * | Copyright @ 2013-2014 Buession.com Inc.														|
 * +------------------------------------------------------------------------------------------------+
 */
package com.buession.cas.client;

/**
 * CAS Server 配置
 * 
 * @author Yong.Teng <webmaster@buession.com>
 */
public final class Configuration {

	/**
	 * CAS Server 地址
	 */
	private String server;

	/**
	 * CAS Server 端口
	 */
	private int port = 0;

	/**
	 * Connection Request Timeout
	 */
	private int connectionRequestTimeout = 0;

	/**
	 * Connect Timeout
	 */
	private int connectTimeout = 0;

	/**
	 * Socket Timeout
	 */
	private int socketTimeout = 0;

	/**
	 * 返回 CAS Server 地址
	 * 
	 * @return CAS Server 地址
	 */
	public String getServer() {
		return server;
	}

	/**
	 * 设置 CAS Server 地址
	 * 
	 * @param server
	 *        CAS Server 地址
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * 返回 CAS Server 端口
	 * 
	 * @return CAS Server 端口
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 设置 CAS Server 端口
	 * 
	 * @param port
	 *        CAS Server 端口
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the timeout in milliseconds used when requesting a connection
	 * from the connection manager. A timeout value of zero is interpreted
	 * as an infinite timeout.
	 * 
	 * @return connectionRequestTimeout
	 */
	public int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}

	/**
	 * 设置 connectionRequestTimeout
	 * 
	 * @param connectionRequestTimeout
	 */
	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

	/**
	 * Determines the timeout in milliseconds until a connection is established.
	 * A timeout value of zero is interpreted as an infinite timeout.
	 * 
	 * @return connectTimeout
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * 设置 connectTimeout
	 * 
	 * @param connectTimeout
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * Defines the socket timeout (<code>SO_TIMEOUT</code>) in milliseconds,
	 * which is the timeout for waiting for data or, put differently,
	 * a maximum period inactivity between two consecutive data packets).
	 * 
	 * @return socketTimeout
	 */
	public int getSocketTimeout() {
		return socketTimeout;
	}

	/**
	 * 设置 socketTimeout
	 * 
	 * @param socketTimeout
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

}