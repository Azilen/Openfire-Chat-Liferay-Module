package com.azilen.chat.conatant;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;

/**
 * ChatConstant: Constans to be used in Chat APIs, Also includes properties from portlet.properties
 * 
 * @author vijay kareliya
 *
 */
public class ChatConstant {

	/*
	 * configuration: To get properties from portlet.properties file
	 */
	public static final Configuration configuration = ConfigurationFactoryUtil
			.getConfiguration(ChatConstant.class.getClassLoader(), "portlet");

	/* Used for REST API call */
	public static final String CREATE_GROUP_COMMAND = "creategroup";

	/* Properties to be used for connecting Openfire server */
	
	//XMPP server host name (Openfire server host name)
	public static final String XMPP_HOST_NAME = configuration.get("xmpp.hostname");
	public static final String XMPP_DOMAIN_NAME = configuration.get("xmpp.domain.name");
	public static final String XMPP_HTTP_BIND_PORT = configuration.get("xmpp.http.bind.port");
	public static final String XMPP_REST_URL = configuration.get("xmpp.openfire.rest.url");
	public static final String XMPP_REST_PORT = configuration.get("xmpp.openfire.rest.port");
	public static final String XMPP_REST_AUTH_TOKEN = configuration.get("xmpp.openfire.rest.auth.token");
}
