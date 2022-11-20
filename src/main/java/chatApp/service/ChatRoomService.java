package chatApp.service;
import chatApp.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

//    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
//        this.chatRoomRepository = chatRoomRepository;
//    }

//    ChatRoom updateChatRoom(Long chatId, User user, Message msg) throws SQLDataException {
//        ChatRoom cr = null;
//        if(chatRoomRepository.findById(chatId).isPresent()){
//            cr = chatRoomRepository.findById(chatId).get();
//            List<User> users = cr.getParticipants();
//            if(users.contains(user)){
//                List<Message> messages = cr.getMessages();
//                messages.add(msg);
//                cr.setMessages(messages);
//            }
//            else{
//                throw new SQLDataException(String.format("User %s doesn't exists in this chatRoom table", chatId + " " + user.getName()));
//            }
//        }
//        else{
//            throw new SQLDataException(String.format("Chat Room %s doesn't exists in chatRoom table", chatId));
//        }
//        return chatRoomRepository.save(cr);
//    }

}
