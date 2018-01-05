package com.azilen.chat.conatant;

import com.liferay.portal.kernel.util.PropsUtil;

public class ChatConstant {

	public static final String CREATE_GROUP_COMMAND = "creategroup";

	public static final String XMPP_HOST_NAME = PropsUtil.get("xmpp.hostname");
	public static final String XMPP_DOMAIN_NAME = PropsUtil.get("xmpp.domain.name");
	public static final String XMPP_HTTP_BIND_PORT = PropsUtil.get("xmpp.http.bind.port");
	public static final String XMPP_REST_URL = PropsUtil.get("xmpp.openfire.rest.url");
	public static final String XMPP_REST_PORT = PropsUtil.get("xmpp.openfire.rest.port");
	public static final String XMPP_REST_AUTH_TOKEN = PropsUtil.get("xmpp.openfire.rest.auth.token");
}
