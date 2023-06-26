import java.util.*;

// User model
class User {
    private String name;
    private String mobileNumber;
    
    public User(String name, String mobileNumber) {
        this.name = name;
        this.mobileNumber = mobileNumber;
    }
    
    // Getters and setters
    // ...
}

// Group model
class Group {
    private String name;
    private User admin;
    private List<User> users;
    
    public Group(String name, User admin, List<User> users) {
        this.name = name;
        this.admin = admin;
        this.users = users;
    }
    
    // Getters and setters
    // ...
}

// Message model
class Message {
    private int messageId;
    private String content;
    
    public Message(int messageId, String content) {
        this.messageId = messageId;
        this.content = content;
    }
    
    // Getters and setters
    // ...
}

// UserRepository interface
interface UserRepository {
    void createUser(User user);
    User getUserByMobileNumber(String mobileNumber);
    List<User> getAllUsers();
}

// InMemoryUserRepository implementation using a hashmap
class InMemoryUserRepository implements UserRepository {
    private Map<String, User> users;
    
    public InMemoryUserRepository() {
        this.users = new HashMap<>();
    }
    
    @Override
    public void createUser(User user) {
        if (users.containsKey(user.getMobileNumber())) {
            throw new RuntimeException("User with mobile number already exists");
        }
        users.put(user.getMobileNumber(), user);
    }
    
    @Override
    public User getUserByMobileNumber(String mobileNumber) {
        return users.get(mobileNumber);
    }
    
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}

// GroupRepository interface
interface GroupRepository {
    void createGroup(Group group);
    Group getGroupByName(String groupName);
    void updateGroup(Group group);
    void removeUserFromGroup(Group group, User user);
}

// InMemoryGroupRepository implementation using a hashmap
class InMemoryGroupRepository implements GroupRepository {
    private Map<String, Group> groups;
    
    public InMemoryGroupRepository() {
        this.groups = new HashMap<>();
    }
    
    @Override
    public void createGroup(Group group) {
        groups.put(group.getName(), group);
    }
    
    @Override
    public Group getGroupByName(String groupName) {
        return groups.get(groupName);
    }
    
    @Override
    public void updateGroup(Group group) {
        groups.put(group.getName(), group);
    }
    
    @Override
    public void removeUserFromGroup(Group group, User user) {
        group.getUsers().remove(user);
        // Remove user's messages from the group's database
        // Update relevant attributes accordingly
    }
}

// MessageRepository interface
interface MessageRepository {
    void sendMessage(Group group, User sender, Message message);
}

// InMemoryMessageRepository implementation using a hashmap
class InMemoryMessageRepository implements MessageRepository {
    private Map<String, List<Message>> messagesByGroup;
    private int messageIdCounter;
    
    public InMemoryMessageRepository() {
        this.messagesByGroup = new HashMap<>();
        this.messageIdCounter = 1;
    }
    
    @Override
    public void sendMessage(Group group, User sender, Message message) {
        if (!group.getUsers().contains(sender)) {
            throw new RuntimeException("Sender is not a member of the group");
        }
        
        List<Message> messages = messagesByGroup.getOrDefault(group.getName(), new ArrayList<>());
        message.setMessageId(messageIdCounter++);
        messages.add(message);
        messagesByGroup.put(group.getName(), messages);
    }
}

// UserService implementation
class UserService {
    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private MessageRepository messageRepository;
    
    public UserService() {
        this.userRepository = new InMemoryUserRepository();
        this.groupRepository = new InMemoryGroupRepository();
        this.messageRepository = new InMemoryMessageRepository();
    }
    
    public void createUser(String name, String mobileNumber) {
        User user = new User(name, mobileNumber);
        userRepository.createUser(user);
    }
    
    public void createGroup(String groupName, List<User> users) {
        if (users.size() < 2) {
            throw new RuntimeException("At least 2 users are required to create a group");
        }
        
        User admin = users.get(0);
        Group group;
        
        if (users.size() == 2) {
            group = new Group(users.get(1).getName(), admin, users);
        } else {
            group = new Group(groupName, admin, users);
        }
        
        groupRepository.createGroup(group);
    }
    
    public void sendMessage(String groupName, User sender, String messageContent) {
        Group group = groupRepository.getGroupByName(groupName);
        
        if (group == null) {
            throw new RuntimeException("Group not found");
        }
        
        if (!group.getUsers().contains(sender)) {
            throw new RuntimeException("Sender is not a member of the group");
        }
        
        Message message = new Message(0, messageContent);
        messageRepository.sendMessage(group, sender, message);
    }
    
    // Other methods for changing the admin, removing a user, and finding messages
    // ...
}
