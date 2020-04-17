package com.tannerbernth.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tannerbernth.model.Group;
import com.tannerbernth.model.Message;
import com.tannerbernth.model.User;
import com.tannerbernth.model.UserGroup;
import com.tannerbernth.repository.UserGroupRepository;
import com.tannerbernth.repository.GroupRepository;
import com.tannerbernth.repository.MessageRepository;
import com.tannerbernth.service.UserDetailService;

import com.tannerbernth.web.dto.MessageDto;
import com.tannerbernth.web.error.GroupDoesNotExistException;
import com.tannerbernth.web.error.UsernameDoesNotExistException;
import com.tannerbernth.web.error.EmptyUserException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserGroupService {

	private final Logger LOGGER = LoggerFactory.getLogger(UserGroupService.class);
	
	@Autowired
    private UserDetailService userDetailService;

	@Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    public Group findGroupById(Long id) {
    	return groupRepository.findOneById(id);
    }

    public UserGroup findUserGroupByUserAndGroup(User user, Group group) {
        return userGroupRepository.findOneByUserAndGroup(user, group);
    }

    public void updateLastReadDate(UserGroup userGroup) {
        userGroup.setLastReadDate(LocalDateTime.now());
    }

    public Collection<Group> findGroupsByUser(User user) {
    	return user.getGroups();
    }

    public Collection<Group> findAllGroupsByUser(User user) {
    	return groupRepository.findByUsersIdOrderByLastModifiedDateDesc(user.getId());
    }

    public Collection<Message> findMessagesByGroupId(Long id) {
    	return messageRepository.findByGroupIdOrderByCreatedDateAsc(id);
    }

    public Collection<Message> findBottom100MessagesByGroupId(Long id) {
        return messageRepository.findBottom100ByGroupIdOrderByCreatedDateAsc(id);
    }

    public Message findMostRecentMessageByGroupId(Long id) {
    	return messageRepository.findTopByGroupIdOrderByCreatedDateDesc(id);
    }

    public Group createNewGroup(User leader, Collection<User> users, String name) throws EmptyUserException {
    	if (users.size() < 1) {
            LOGGER.info("No users for group");
    		throw new EmptyUserException("No users for group");
    	}

        LocalDateTime currentTime = LocalDateTime.now();
    	Group group = new Group();
    	group.setName(name);
    	group.setLeaderId(leader.getId());
    	if (!users.contains(leader)) {
            users.add(leader);
        }
    	group.setUsers(users);
    	group.setLastModifiedDate(currentTime);
    	LOGGER.info("Saving group!");
    	group = groupRepository.save(group);

    	Collection<User> newUsers = new ArrayList<User>(users);
        
    	for (User user : newUsers) {
    		userDetailService.updateUserGroups(user,group,currentTime);
    	}
    	return group;
    }

    public Message sendNewGroupAndMessage(MessageDto messageDto) throws UsernameDoesNotExistException {
    	Group group = null;
    	Collection<User> users = new ArrayList<User>();
    	for (String member : messageDto.getMembers()) {
			if(!userDetailService.usernameExists(member)) {
                LOGGER.info("User with username {} does not exist",member);
				throw new UsernameDoesNotExistException("User with username (" + member + ") does not exist");
			}
            User next = userDetailService.findUserByUsername(member);
			if (!users.contains(next)) {
                users.add(next);
            }
		}
		group = createNewGroup(userDetailService.findUserByUsername(messageDto.getLeaderName()),users,messageDto.getGroupName());

		return sendMessage(group, messageDto);
    }

    public Message sendNewMessage(MessageDto messageDto) throws GroupDoesNotExistException {
    	Group group = findGroupById(messageDto.getGroupId());
		if (group == null) {
			throw new GroupDoesNotExistException("Group does not exist");
		}

    	return sendMessage(group, messageDto);
    }

    public boolean userInGroup(User user, Group group) {
    	if (group != null && user.getGroups().contains(group)) {
    		return true;
    	}
    	return false;
    }

    /*public void leaveGroup(User user, Group group) {

    }*/

    private Message sendMessage(Group group, MessageDto messageDto) {
    	Message message = new Message();

    	message.setGroup(group);
    	message.setMessage(messageDto.getMessage());
    	message.setSenderUsername(messageDto.getSenderUsername());
    	message.setSenderId(messageDto.getSenderId());
    	message.setCreatedDate(LocalDateTime.now());
    	message.setLastModifiedDate(LocalDateTime.now());
    	group.setLastModifiedDate(LocalDateTime.now());

    	Collection<User> users = group.getUsers();
    	for (User user : users) {
    		if (user.getId() != messageDto.getSenderId()) {
    			user.setUnreadCount(user.getUnreadCount()+1);
    		}
    	}

    	return messageRepository.save(message);
    }
}