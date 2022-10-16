package p2p;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Mensagem implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	public static enum MessageType {
		SEARCH(1), RESPONSE(2);	
		private final int value;
		
		MessageType(int selectedValue){
			value = selectedValue;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	public static enum Responses {
		NOT_FOUND(1),FOUND(2);
		
		private final int value;
		
		Responses(int selectedValue){
			value = selectedValue;
		}
		
		public int getValue(){
			return value;
		}
		
	}
	
	private String messageContent;
	private String reqId;
	private MessageType reqType;
	private Responses res;
	
	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public MessageType getReqType() {
		return reqType;
	}

	public void setReqType(MessageType reqType) {
		this.reqType = reqType;
	}

	public Responses getRes() {
		return res;
	}

	public void setRes(Responses res) {
		this.res = res;
	}
	
	public byte[] serialize() throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream); 
		oo.writeObject(this);
		oo.close();
		return bStream.toByteArray();
	}
	
	public Mensagem(MessageType type, String content, String reqId) {
		this.messageContent = content;
		this.reqId = reqId;
		this.reqType = type;
		
	}
	
	public Mensagem() {
		
	}
}
