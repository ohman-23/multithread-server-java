package assignment_4;
import java.io.*;
import java.net.*; 

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
ClientThread details Runnables to be created by ServerMain following the establishment of a client connection to the server. ClientThread will receive all
messages from clients and delagate them to their proper clients, in parallel with operations within ServerMain
@ADT serverClientManager - ClientManager of server socket
@ADT clientSocket - the client socket for which this thread is responsible for
@ADT username - username of the corresponding client socket
@ADT clientReader - enables messages to be received from the client to the server concurrently 
@ADT clientWriter - enables the thread to write to the proper client socket
*/

public class ClientThread implements Runnable
{
	private ClientManager serverClientManager;
	private Socket clientSocket;
	private String username;
	private BufferedReader clientReader;
	private BufferedWriter clientWriter;
	
	/**
	 * constructor for the ClientThread 
	 * @param manager - clientManager reference to the manager owned by the server
	 * @param client - socket connection of the client
	 * @param user - username of the client
	 */	
	public ClientThread(ClientManager manager, Socket client, String user) 
	{
		serverClientManager = manager; // points to same instance
		clientSocket = client;
		username = user;
		try 
		{
			InputStreamReader clientInputStream = new InputStreamReader(clientSocket.getInputStream());
			clientReader  = new BufferedReader(clientInputStream);
			OutputStreamWriter clientOutputStream = new OutputStreamWriter(clientSocket.getOutputStream());
			clientWriter = new BufferedWriter(clientOutputStream);
		} catch (IOException e) 
		{
			e.printStackTrace();
			System.out.println(e);
		}
	}

	/**
	 * details the inner operations of the ServerThread - namely when a message is received from the client, the message will be validated by the server and
	 * then routed to the intended recipient (if that recipient is connected to the server)
	 */	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		boolean terminate = false;
		try 
		{
			while(!terminate) 
			{
				//wait to receive message from client
				String serializedMessage = clientReader.readLine();
				if(serializedMessage == null) 
				{
					Thread.sleep(100);
				}
				
				else if(serializedMessage.equals("exit"))
				{
					//if the client wants to exit the server, this is the protocol taken
					terminate = true;
					serverClientManager.deleteUser(this.username);
					//send message to all other users that this user quit
					String notification = String.format("%s has left the chat!", this.username);
					serverClientManager.messageAllClients(notification);
					// NOTE SHOULDN'T SEND TO THE USER THAT QUIT
					// send quit message to client server thread
					clientWriter.write("disconnected");
					clientWriter.newLine();
					clientWriter.flush();
				}
				
				else 
				{
					System.out.println(String.format("Client Says:: %s", serializedMessage));
					if(MessageHandler.validateSerializedMessage(serializedMessage, serverClientManager))
					{
						//if message is a valid message
						String recipientName = MessageHandler.getRecipientNameSerialized(serializedMessage);
						String confirmationMessage = MessageHandler.getConfirmationMessage();
						String messageToSend = MessageHandler.getFinalSentMessage(serializedMessage);
						Socket recipient = serverClientManager.getUserSocket(recipientName);
						// define recipient socket writer
						OutputStreamWriter recipientOutputStream = new OutputStreamWriter(recipient.getOutputStream());
						BufferedWriter recipientWriter = new BufferedWriter(recipientOutputStream);
						
						//send conf message to sender (this thread)
						clientWriter.write(confirmationMessage);
						clientWriter.newLine();
						clientWriter.flush();
						
						//send message to recipient
						recipientWriter.write(messageToSend);
						recipientWriter.newLine();
						recipientWriter.flush();
					}
					
					else 
					{
						//if message is not valid
						String errorMessage = MessageHandler.getErrorMessage();
						//send error message to client
						clientWriter.write(errorMessage);
						clientWriter.newLine();
						clientWriter.flush();
					}
				}
			}
		}
		catch(Exception e) 
		{
//			System.out.println("SERVER CLIENT THREAD ERROR");
			System.out.println(e);
		}
		finally 
		{
			//close client connection if it hasn't been closed already (ie - this ensure that the connection will be closed)
			if(!clientSocket.isClosed()) 
			{
				try {
					clientSocket.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
				serverClientManager.deleteUser(username);
				serverClientManager.messageAllClients(String.format("%s has left the chat!", username));
			}
		}
		return;
	}
}
