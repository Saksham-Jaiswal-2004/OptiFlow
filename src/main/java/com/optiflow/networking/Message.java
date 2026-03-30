package com.optiflow.networking;

import java.io.Serializable;

public class Message implements Serializable
{
    private MessageType type;
    private String content;
    private int sender_id;
    private int entity_id;
    private String entityType;

    public Message(MessageType type, String content, int sender_id, int entity_id, String entityType)
    {
        this.type = type;
        this.content = content;
        this.sender_id = sender_id;
        this.entity_id = entity_id;
        this.entityType = entityType;
    }

    public MessageType getMessageType()
    {
        return type;
    }

    public String getContent()
    {
        return content;
    }

    public int getSender_id()
    {
        return sender_id;
    }

    public int getEntity_id()
    {
        return entity_id;
    }

    public String getEntityType()
    {
        return entityType;
    }

    public void setMessageType(MessageType type)
    {
        this.type = type;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void setSender_id(int sender_id)
    {
        this.sender_id = sender_id;
    }

    public void setEntity_id(int entity_id)
    {
        this.entity_id = entity_id;
    }

    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }
}
