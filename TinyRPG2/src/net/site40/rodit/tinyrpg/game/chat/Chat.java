package net.site40.rodit.tinyrpg.game.chat;

import java.util.ArrayList;

public class Chat {
	
	public static class ChatMessage{
		
		protected IChatSender sender;
		protected String content;
		
		public ChatMessage(IChatSender sender, String content){
			this.sender = sender;
			this.content = content;
		}
		
		public String getFullMessage(){
			return (sender.showName() ? sender.getDisplayName() + ": " : "") + content;
		}
	}
	
	private ArrayList<ChatMessage> messages;
	
	public Chat(){
		this.messages = new ArrayList<ChatMessage>();
	}
	
	public ArrayList<ChatMessage> getMessages(){
		return messages;
	}
	
	public void addChatMessage(IChatSender sender, String message){
		addChatMessage(new ChatMessage(sender, message));
	}
	
	public void addChatMessage(ChatMessage message){
		messages.add(message);
	}
	
	public void clear(){
		messages.clear();
	}
}
