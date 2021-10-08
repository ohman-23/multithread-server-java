package assignment_4;
import java.io.*;
import java.net.*;
import java.util.Scanner;


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
ClientMain creates a connection to the running server to enable the current client to chat with other users on the server
*/

public class ClientMain 
{
	
/**
 * Main loop which manages/runs a client connection to some server
 * @param args - array of command line arguments, not used in the context of this file
 */	
public static void main(String[] args) 
{
	try 
	{	
		//establish connection and input username for public identity
		Scanner clientInput = new Scanner(System.in);
		System.out.println("Please Enter your username:: ");
		String username = clientInput.nextLine().trim();
		System.out.println(String.format("Attempting to connect %s to the chatroom... ", username));
		
		try 
		{
			Socket server  = new Socket("localhost",6666);
			//define server read and write capabilities
			InputStreamReader serverInput = new InputStreamReader(server.getInputStream());
			BufferedReader serverReader = new BufferedReader(serverInput);
			OutputStreamWriter serverOutput = new OutputStreamWriter(server.getOutputStream());
			BufferedWriter serverWriter = new BufferedWriter(serverOutput);
			//send username information
			serverWriter.write(username);
			serverWriter.newLine();
			serverWriter.flush();	
			// receive confirmation message:
			String confMessage = serverReader.readLine();
			System.out.println(confMessage);
			
			// recieve second confirmation message - (username has joined the chat etc)
			String secondconfMessage = serverReader.readLine();
			System.out.println(secondconfMessage);
			
			// start server thread - will recieve incoming messages from the server
			Runnable serverRunnable = new ServerThread(server);
			Thread thread = new Thread(serverRunnable);
			thread.start();
			
			// while loop controls the flow of message sending
			String message = "";
			while(!message.equals("exit") ) 
			{
				message = clientInput.nextLine();
				// parse and serialize the message before sending it (as per protocol)
				if(message.equals("exit")) 
				{
					serverWriter.write(message);
					serverWriter.newLine();
					serverWriter.flush();	
				}
				// client side validation, if is not in the format of @name: message, the message will not be sent to the server
				else if(MessageHandler.validateRawMessage(message)) 
				{
					// serialize the raw message to a serialized version for server parsing
					String serializedMessage = MessageHandler.serializeMessage(message, username);
					// send the serialized message
					serverWriter.write(serializedMessage);
					serverWriter.newLine();
					serverWriter.flush();		
				}
				else 
				{
					System.out.println("Please input a valid message!");
				}
			}
			
			// after client exits, close all writers and remaining socket connections
			System.out.println("Client Socket Closed");
			serverInput.close();
			serverReader.close();
			serverOutput.close();
			serverWriter.close();
			clientInput.close();
			server.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	catch(Exception e) 
	{
			System.out.println(e);
			System.out.println("ERROR CONNECTING TO CHATROOM - exiting program now");
		}
	}
}
