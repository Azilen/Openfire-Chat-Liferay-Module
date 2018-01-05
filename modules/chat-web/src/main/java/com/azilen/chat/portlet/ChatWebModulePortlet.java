package com.azilen.chat.portlet;

import com.azilen.chat.conatant.ChatConstant;
import com.azilen.chat.portlet.xmpp.XMPPPrebind;
import com.azilen.chat.portlet.xmpp.util.ChatUtil;
import com.azilen.chat.portlet.xmpp.util.SessionInfo;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * ChatWebModulePortlet: Portlet to auth in openfire and render view page
 * 
 * @author vijay kareliya, Sandeep Patel
 *
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.chat", 
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=Chat Web Module", 
		"javax.portlet.init-param.template-path=/",
		"com.liferay.portlet.header-portlet-javascript=/js/libs.bundle.js", 
		"com.liferay.portlet.header-portlet-javascript=/js/candy.bundle.js",
		"com.liferay.portlet.header-portlet-javascript=/js/bootstrap.min.js", 
		"com.liferay.portlet.header-portlet-javascript=/js/main.js",
		"com.liferay.portlet.header-portlet-css=/css/bootstrap.min.css", 
		"com.liferay.portlet.header-portlet-css=/css/style.css",
		"javax.portlet.init-param.view-template=/view.jsp", 
		"javax.portlet.supports.mime-type=image/jpeg",
		"javax.portlet.resource-bundle=content.Language", 
		"javax.portlet.security-role-ref=power-user,user" }, 
		service = Portlet.class)
public class ChatWebModulePortlet extends MVCPortlet {

	private static final Log _log = LogFactoryUtil.getLog(ChatWebModulePortlet.class);

	/**
	 * doView: Authenticates in openfire foe loggedin user and renders view page.
	 */
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

		/* themeDisplay: to get loggedin user's information */
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

		/* Get Stored password from cache for loggedin user */
		PortalCache portalCache = MultiVMPoolUtil.getCache(User.class.getName());
		String xmpp_token = (String) portalCache.get(themeDisplay.getUser().getEmailAddress());

		/* XMPP(Open fire) server properties */
		boolean isXmppConnected = false;
		String xmppHostName = ChatConstant.XMPP_HOST_NAME;
		String xmppDomainName = ChatConstant.XMPP_DOMAIN_NAME;
		String httpBindPort = ChatConstant.XMPP_HTTP_BIND_PORT;

		/* Builds string for Auto join in openfire groups while login */
		String autojoinChatRoom = StringPool.QUOTE + "general" + StringPool.AT + "conference." + xmppDomainName + StringPool.QUOTE;
		
		try {
			/* Connects to Openfire server and authenticates a user */
			XMPPPrebind xmppPrebind = new XMPPPrebind(xmppHostName, xmppDomainName, "/http-bind/", httpBindPort,
					themeDisplay.getUser().getScreenName());
			xmppPrebind.connect(themeDisplay.getUser().getScreenName() + StringPool.AT + xmppDomainName, xmpp_token);
			xmppPrebind.auth();
			
			SessionInfo sessionInfo = xmppPrebind.getSessionInfo();
			
			/* Sets required properties in request for openfire */
			renderRequest.setAttribute("jid", sessionInfo.getJid());
			renderRequest.setAttribute("sid", sessionInfo.getSid());
			renderRequest.setAttribute("rid", sessionInfo.getRid());

			// Sets Xmpp server status as connected after Auth completing in Xmpp server.
			isXmppConnected = true;
			
			/* Getting all groups in which current user is member */
			List<String> userChatRooms = ChatUtil.getUserChatrooms(themeDisplay.getUser().getScreenName());
			for (int groupIndex = 1; groupIndex <= userChatRooms.size(); groupIndex++) {
				autojoinChatRoom = autojoinChatRoom + StringPool.COMMA + StringPool.QUOTE + userChatRooms.get(groupIndex - 1) + StringPool.AT
						+ "conference." + xmppDomainName + StringPool.QUOTE;
			}
		} catch (Exception e) {
			_log.error(e.getMessage());
		}

		/* Setting properties in request to be used by frontent */
		renderRequest.setAttribute("isXmppConnected", isXmppConnected);
		renderRequest.setAttribute("xmppDomainName", xmppDomainName);
		renderRequest.setAttribute("xmpp_HTTP_BIND_URL",
				ChatConstant.XMPP_REST_URL + StringPool.COLON + ChatConstant.XMPP_HTTP_BIND_PORT + StringPool.SLASH + "http-bind/");
		renderRequest.setAttribute("xmpp_HTTP_REST_URL",
				ChatConstant.XMPP_REST_URL + StringPool.COLON + ChatConstant.XMPP_REST_PORT + StringPool.SLASH);
		renderRequest.setAttribute("xmppOpenfireAuthToken", ChatConstant.XMPP_REST_AUTH_TOKEN);
		renderRequest.setAttribute("xmpp_USER_CHATROOMS", autojoinChatRoom);
		include(viewTemplate, renderRequest, renderResponse);
	}
}