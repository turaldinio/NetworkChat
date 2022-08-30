package sharedResources;

import java.io.Serializable;

public class Message implements Serializable {
    private String text;
    private MessageType messageType;

    public Message(String text, MessageType messageType) {
        this.text = text;
        this.messageType = messageType;
    }

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getText() {
        return text;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
