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

import java.util.Map;

import com.buession.mcrypt.MD5Mcrypt;

/**
 * @author Yong.Teng <webmaster@buession.com>
 */
public class RestfulAuthenticationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		configuration.setServer("http://login.zhubao.com/");

		RestfulAuthentication restfulAuthentication = new RestfulAuthentication(configuration);

		MD5Mcrypt mcrypt = new MD5Mcrypt("UTF-8");
		String ticketGrantingTicket = restfulAuthentication.getTicketGrantingTicket("development",
				mcrypt.encode("123456"));

		if (ticketGrantingTicket != null) {
			String serviceTicket = restfulAuthentication.getServiceTicket(ticketGrantingTicket,
					"http://www.zhubao.com/");

			if (serviceTicket != null) {
				if (restfulAuthentication.validate(serviceTicket, "http://www.zhubao.com/") == true) {
					Map<String, Object> attributes = restfulAuthentication.getAssertion()
							.getAttributes();

					System.out.println("validate return true");
					for (Map.Entry<String, Object> e : attributes.entrySet()) {
						System.out.println(e.getKey() + ": " + e.getValue());
					}
				} else {
					System.out.println("validate return false");
				}
			} else {
				System.out.println("getServiceTicket return null");
			}
		} else {
			System.out.println("getTicketGrantingTicket return null");
		}
	}

}