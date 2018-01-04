/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.azilen.chat.portlet.xmpp.util;

import com.azilen.chat.conatant.ChatConstant;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.igniterealtime.restclient.RestApiClient;
import org.igniterealtime.restclient.RestClient;
import org.igniterealtime.restclient.entity.AuthenticationToken;
import org.igniterealtime.restclient.entity.MUCRoomEntities;
import org.igniterealtime.restclient.entity.MUCRoomEntity;

/**
 * The implementation of the chat local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link com.azilen.chat.service.ChatLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 */
public class ChatUtil {

	private static final Log _log = LogFactoryUtil.getLog(ChatUtil.class);
	private static final String XMPP_DOMAIN_NAME = ChatConstant.XMPP_DOMAIN_NAME;
	private static final String SERVICE_NAME = "conference";
	public static final int UNLIMITED_USERS = 0;
	private static final String CHATROOMS = "chatrooms";

	/**
	 * createChatGroup: API to create new group in Open  fire
	 * @param groupName
	 * @param memberList
	 * @return
	 */
	public static boolean createChatGroup(String groupName, List<String> memberList) {
		_log.info("Entered in createChatGroup: "+ groupName);
		
		MUCRoomEntity chatRoom = new MUCRoomEntity();
		chatRoom.setRoomName(groupName);
		chatRoom.setNaturalName(groupName);
		chatRoom.setDescription(StringPool.BLANK);

		chatRoom.setSubject(StringPool.BLANK);
		chatRoom.setMaxUsers(UNLIMITED_USERS);
		chatRoom.setPersistent(true);
		chatRoom.setPublicRoom(false);
		chatRoom.setCanAnyoneDiscoverJID(false);
		chatRoom.setCanOccupantsChangeSubject(false);
		chatRoom.setCanOccupantsInvite(false);
		chatRoom.setCanChangeNickname(false);

		chatRoom.setLogEnabled(true);
		chatRoom.setMembersOnly(false);
		chatRoom.setModerated(false);

		// Setting member list in chatroom
		_log.info("memberList: "+ memberList.size());
		List<String> groupMemberList = new ArrayList<>();

		for (String member : memberList) {
			groupMemberList.add(member + StringPool.AT + XMPP_DOMAIN_NAME);
		}
		chatRoom.setMembers(groupMemberList);
		Response response = getRestClient().post(CHATROOMS, chatRoom, null);
		_log.info("response: "+ response);
		
		if (response.getStatus() == HttpStatus.SC_CREATED) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add member in Chat Group.
	 * 
	 * @param groupName
	 * @return
	 * @return
	 */
	public MUCRoomEntity getChatGroupRoom(String groupName) {
		return getRestClient().get(CHATROOMS + StringPool.SLASH + groupName, MUCRoomEntity.class,
				getServiceQueryParam());
	}

	/**
	 * Add member in Chat Group.
	 * 
	 * @param groupName
	 * @param screnName
	 * @return
	 */
	public boolean addMemberInChatGroup(String groupName, String userScreenName) {

		MUCRoomEntity chatRoom = getChatGroupRoom(groupName);

		Response response = getRestClient().post(CHATROOMS + StringPool.SLASH + chatRoom.getRoomName() + "/members/"
				+ userScreenName + StringPool.AT + XMPP_DOMAIN_NAME, null, getServiceQueryParam());

		if (response.getStatus() == HttpStatus.SC_CREATED) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Remove member from Chat Group.
	 * 
	 * @param groupName
	 * @param screnName
	 * @return
	 */
	public boolean removeMemberFromChatGroup(String groupName, String userScreenName) {

		MUCRoomEntity chatRoom = getChatGroupRoom(groupName);

		Response response = getRestClient().delete(CHATROOMS + StringPool.SLASH + StringPool.SLASH
				+ chatRoom.getRoomName() + "/members/" + userScreenName + StringPool.AT + XMPP_DOMAIN_NAME,
				getServiceQueryParam());

		if (response.getStatus() == HttpStatus.SC_CREATED) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Get Rest Query Param
	 * 
	 * @return
	 */
	private static Map<String, String> getServiceQueryParam() {

		String chatRoomServiceName = ChatConstant.XMPP_DOMAIN_NAME;

		return Collections.singletonMap(SERVICE_NAME, chatRoomServiceName);
	}

	/**
	 * Get Rest Client
	 * 
	 * @return
	 */
	private static RestClient getRestClient() {

		String serverUrl = ChatConstant.XMPP_REST_URL;
		int serverPort = GetterUtil.getInteger(ChatConstant.XMPP_REST_PORT);
		String authTkn = ChatConstant.XMPP_REST_AUTH_TOKEN;

		AuthenticationToken authenticationToken = new AuthenticationToken(authTkn);

		RestApiClient restClient = new RestApiClient(serverUrl, serverPort, authenticationToken);

		return restClient.getRestClient();

	}
	
	/**
	 * getUserChatrooms: returns chatrooms in which specified user in a member.
	 * @param userName
	 * @return
	 */
	public static List<String> getUserChatrooms(String userName) {
		_log.info("Entered in getUserChatrooms: " + userName);
		List<String> groups = new ArrayList<>();
		try {
			//  Gets chatroom entity
			MUCRoomEntities response = getRestClient().get(CHATROOMS + StringPool.QUESTION + "type=all", MUCRoomEntities.class, null);
			
			// Checks each group for members
			response.getMucRooms().forEach(group -> {
				_log.debug("group menbers: " + group.getMembers());
				
				// getting members of group and check if current user is part of it.
				group.getMembers().forEach(member -> {
					String memberName = member.substring(0, member.indexOf(StringPool.AT));
					_log.info("memberName :" + memberName );
					
					// Add group in user's group list if user is member of the group
					if(memberName.trim().equalsIgnoreCase(userName.trim())){
						_log.debug("found group: " + group.getRoomName());
						groups.add(group.getRoomName());
					}
				});
			});
			_log.info("groups: " + groups);
		} catch (Exception e) {
			_log.error(e);
		}

		// Returns list og groups for user
		return groups;

	}
}