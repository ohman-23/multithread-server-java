package assignment_4;
import java.io.*;
import java.util.Scanner;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;


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
ServerMain creates socket server to host a "online" one to one chat room between multiple users
*/
public class ServerMain 
{
	/**
	 * Main loop which manages/runs a client connection to some server
	 * @param args - array of command line arguments, not used in the context of this file
	 */	
	public static void main(String[] args)
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(6666);
			ReentrantLock lock = new ReentrantLock();
			ClientManager clientManager = new ClientManager(serverSocket, lock);
			System.out.println("--- SERVER STARTING, AWAITING CONNECTIONS ---");
			
			
			while(true) 
			{
				// serverSocket methods are all blocking commands 
				Socket client = serverSocket.accept();//establishes connection 
				System.out.println("Connection establised");
				
				InputStreamReader clientInputStream = new InputStreamReader(client.getInputStream());
				BufferedReader clientReader  = new BufferedReader(clientInputStream);
				OutputStreamWriter clientOutputStream = new OutputStreamWriter(client.getOutputStream());
				BufferedWriter clientWriter = new BufferedWriter(clientOutputStream);
				
				// get username information
				String clientUserName = clientReader.readLine();
				
				// send confirmation message
				clientWriter.write("SUCCESSFULLY CONNECTED TO SERVER -- type \"exit\" to leave");
				clientWriter.newLine();
				clientWriter.flush();
				
				// add user to client Manager
				clientManager.addUser(clientUserName, client);
				
				// notify all users that new user has joined
				String notification = String.format("%s has joined the chat!", clientUserName);
				clientManager.messageAllClients(notification);
				System.out.println(clientManager);
				// create client Thread
				Runnable clientRunnable = new ClientThread(clientManager, client, clientUserName);
				Thread thread = new Thread(clientRunnable);
				thread.start();
			}
		}
		catch(Exception e) 
		{
			System.out.println(e);
			System.out.println("SERVER MAIN ERROR");
			}
	
		}
	}
