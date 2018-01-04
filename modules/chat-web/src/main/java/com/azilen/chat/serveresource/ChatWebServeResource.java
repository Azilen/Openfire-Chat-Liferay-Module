package com.azilen.chat.serveresource;

import com.azilen.chat.conatant.ChatConstant;
import com.azilen.chat.portlet.xmpp.util.ChatUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

/**
 * ChatWebServeResource: To handle create Chat room rest call from UI
 * 
 * @author vijay kareliya
 *
 */
@Component(
		immediate = true, 
		property = { 
				"javax.portlet.name=com_azilen_chat_portlet_ChatWebModulePortlet",
				"mvc.command.name=/chatAPI" }, 
		service = MVCResourceCommand.class
)
public class ChatWebServeResource implements MVCResourceCommand {

	private static final Log _log = LogFactoryUtil.getLog(ChatWebServeResource.class);
	
	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {
		_log.info("Entered in serveResource");

		String requestCommand = ParamUtil.getString(resourceRequest, "requestCommand");

		/* Create new Group request*/
		if (ChatConstant.CREATE_GROUP_COMMAND.equalsIgnoreCase(requestCommand)) {
			String groupName = ParamUtil.getString(resourceRequest, "groupName");
			_log.debug("groupName : " + groupName);

			// Gets member list given by request
			String[] memberListArr = ParamUtil.getStringValues(resourceRequest, "memberList[]");
			List<String> memberList = ListUtil.fromArray(memberListArr);
			_log.debug("memberList : " + memberList);
			
			// Call to API for creating group with specified member list
			ChatUtil.createChatGroup(groupName, memberList);
		}

		try {
			resourceResponse.getWriter().write("SUCCESS");
		} catch (IOException e) {
			_log.error("Exception:", e);
		}
		return false;
	}

}
