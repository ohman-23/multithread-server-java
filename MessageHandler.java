package assignment_4;

import java.io.IOException;

/* EE422C Assignment #4 submission by
 * <Ethan Ohman>
 * <ejo527>
 */

/**
Class to solve EE422C Programming Assignment #4
@author Ethan Ohman
@version 1.01 2021-04-13
*/

/**
MessageHandler is a class containing static methods primarily for client and server side message validation, parsing and serializaton, according to the project
defined messaging protocol
*/
public class MessageHandler 
{
	/**
	 * validateRawMessage determines if a client-side message is formatted correctly
	 * @param rawMessage - string containing the client-side message sent to the server 
	 * @return - true if validated correctly, false if not
	 */	
	public static boolean validateRawMessage(String rawMessage) 
	{
		int colonIndex = rawMessage.indexOf(':');
		int atIndex = rawMessage.indexOf('@');
		if(colonIndex == -1) 
		{
			return false;
		}
		if(atIndex == -1) 
		{
			return false;
		}
		return true;
	}
	
	/**
	 * validateSerializedMessage determines if a server-side message is formatted correctly
	 * @param serializedMessage - string containing the client-side serialized message sent to the server 
	 * @param manager - Server's ClientManager object, used to test the existence of users
	 * @return - true if validated correctly, false if not
	 */	
	public static boolean validateSerializedMessage(String serializedMessage, ClientManager manager) 
	{
		String[] messageArr = serializedMessage.split("--");
 		if(messageArr.length != 3) 
 		{
			return false;
		}
 		if(!manager.userExists(messageArr[0].trim())) 
 		{
 			// if recipient of message doesn't exist
 			return false;
 		}
		return true;
	}
	
	
	/**
	 * getRecipientNameSerialized is a server-side message parser which gets the recipients name from the serialized message
	 * @param serializedMessage - string containing the client-side serialized message sent to the server 
	 * @return - String detailing the recipient's name
	 */	
	public static String getRecipientNameSerialized(String serializedMessage) 
	{
		String[] messageArr = serializedMessage.split("--");
		return messageArr[0].trim();
	}
	
	/**
	 * getMessageSerialized is a server-side message parser which gets the actual message from the serialized message
	 * @param serializedMessage - string containing the client-side serialized message sent to the server 
	 * @return - String detailing the message sent by the client
	 */	
	public static String getMessageSerialized(String serializedMessage) 
	{
		String[] messageArr = serializedMessage.split("--");
		return messageArr[1].trim();
	}
	
	/**
	 * getSenderSerialized is a server-side message parser which gets the sender's name from the serialized message
	 * @param serializedMessage - string containing the client-side serialized message sent to the server 
	 * @return - String detailing the sender's name
	 */	
	public static String getSenderSerialized(String serializedMessage)
	{
		String[] messageArr = serializedMessage.split("--");
		return messageArr[2].trim();
	}
	
	/**
	 * getRecipientNameRaw is a client-side message parser which gets the recipient's name from the raw client message
	 * @param rawMessage - string containing the client-side serialized message sent to the server 
	 * @return - String detailing the recipient's name
	 */	
	public static String getRecipientNameRaw(String rawMessage) 
	{
		int index = rawMessage.indexOf(':');
		return rawMessage.substring(1, index).trim();
	}
	
	/**
	 * getRecipientNameRaw is a client-side message parser which gets the actual message from the raw client message
	 * @param rawMessage - string containing the client-side serialized message sent to the server 
	 * @return - String detailing the message sent by the client
	 */	
	public static String getMessageRaw(String rawMessage) 
	{
		int index = rawMessage.indexOf(':');
		return rawMessage.substring(index+1).trim();
	}

	
	/**
	 * serializeMessage is a client-side message serializer which converts a raw message into a protocol defined serialized message
	 * @param sentMessage - raw message input by the client
	 * @param clientUserName - the username of the sender
	 * @return - Serialized string to be sent to the server
	 */	
	public static String serializeMessage(String sentMessage, String clientUserName) 
	{
		String toUser = MessageHandler.getRecipientNameRaw(sentMessage);
		String message = MessageHandler.getMessageRaw(sentMessage);
		String compiledMessage = String.format("%s -- %s -- %s", toUser, message, clientUserName);
		return compiledMessage;
	}
	
	/**
	 * getFinalSentMessage is a server-side message serializer which converts a serialized message from a client into a response to send to the appropriate recipient
	 * @param serializedMessage - serialized message from the client
	 * @return - Serialized server side string to be sent to the appropriate recipient
	 */	
	public static String getFinalSentMessage(String serializedMessage) 
	{
		// parse the message according to the protocol defined:
		// sent message = ToUser -- MESSAGE -- FromUser
		// the message the recipient sees ::
		String[] messageArr = serializedMessage.split("--");
		String message = messageArr[1].trim();
		String fromUser = messageArr[2].trim();
		return String.format("&%s: %s", fromUser, message);
	}
	
	/**
	 * getErrorMessage is called to signify a failed server side processing/sending of client message
	 * @return - String with a simple error message
	 */	
	public static String getErrorMessage() 
	{
		return String.format("(Failed to Send)");
	}
	
	/**
	 * getErrorMessage is called to signify a successful server side processing/sending of client message
	 * @return - String with a simple confirmation message
	 */	
	public static String getConfirmationMessage() 
	{
		return String.format("(Sent)");
	}
}
