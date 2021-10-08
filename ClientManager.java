package assignment_4;
import java.io.*;
import java.net.*;
import java.util.*;
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
ClientHandler is a serverside manager of all current client connections to the server. It supports adding clients, deleting clients, checking if clients exist, 
and also sending messages to all current clients	
@ADT clientMap - Map which keys are client usernames and values are the actual sockets of the respective clients
@ADT serverLock - Lock used to prevent race conditions for deleting clients
@ADT server - SeverSocket ojbect containing the connection to the server
*/

public class ClientManager 
{
	private Map<String, Socket> clientMap;
	private ReentrantLock serverLock;
	private ServerSocket server;
	
	/**
	 * constructor of the ClientManager object
	 * @param socket - ServerSocket of main server
	 * @param lock - a normal lock
	 */	
	public ClientManager(ServerSocket socket, ReentrantLock lock) 
	{
		clientMap = new HashMap<>();
		serverLock = lock;
		server = socket;
	} 
	
	/**
	 * gets the socket connection of a specific user connected to the server
	 * @param username - username of the client who's socket you are trying to access
	 */	
	public Socket getUserSocket(String username) 
	{
		return clientMap.get(username.trim());
	}
	
	/**
	 * inserts the user and corresponding socket into the clientManager map
	 * @param clientUserName - username of the client who's socket you are trying to access
	 * @param conn - socket connection from aforementioned client to the main server
	 */	
	public void addUser(String clientUserName, Socket conn) 
	{
		// add user to the map
		clientMap.put(clientUserName, conn);
	}
	
	/**
	 * deletes the user and corresponding socket from the clientManager map, and closes the socket of the deleted client
	 * @param clientUserName - username of the client who's socket you are trying to delete
	 */	
	public void deleteUser(String clientUserName) 
	{
		// delete user from the map
		Socket clientConn = clientMap.get(clientUserName);
		if(clientConn != null) 
		{
			// means the user exists within the chat
			try 
			{
				serverLock.lock();
				clientConn.close();
				clientMap.remove(clientUserName);
			}
			catch(Exception e) 
			{
				System.out.println("--- clientmanager ERROR ---");
				System.out.println(e);
			}
			finally 
			{
				serverLock.unlock();
			}
		}
	}
	
	/**
	 * checks if a certain user exists in the clientManager by username
	 * @param clientUserName - username of the client who's socket you are trying to access
	 * returns true if user exists within the client manager, false if not
	 */	
	public boolean userExists(String clientUserName) 
	{
		Socket clientConn = clientMap.get(clientUserName);
		if(clientConn != null) 
		{
			return true;
		}
		return false;
	}
	
	/**
	 * sends a message to every client connected to the server except one
	 * @param message - message = to send to connected clients
	 * @param exemptUserName - the username of the client exempt from receiving the message
	 */	
	public void messageAllClientsExcept(String message, String exemptUserName) 
	{
		// broadcast message from server to everyone
		for(String username : clientMap.keySet()) 
		{
			if(username != exemptUserName) 
			{
				try 
				{
					//write messages to every member in the chat
					Socket clientSocket = clientMap.get(username);
					OutputStreamWriter clientWriter = new OutputStreamWriter(clientSocket.getOutputStream());
					BufferedWriter clientOut = new BufferedWriter(clientWriter);
					clientOut.write(message);
					clientOut.newLine();
					clientOut.flush();
				}
				catch(Exception e) 
				{
					System.out.println("--- clientmanager ERROR ---");
					System.out.println(e);
				}
			}
		}
	}
	
	/**
	 * sends a message to every client connected to the server
	 * @param message - message = to send to connected clients
	 */	
	public void messageAllClients(String message) 
	{
		// broadcast message from server to everyone
		for(String username : clientMap.keySet()) 
		{
			try 
			{
				//write messages to every member in the chat
				Socket clientSocket = clientMap.get(username);
				OutputStreamWriter clientWriter = new OutputStreamWriter(clientSocket.getOutputStream());
				BufferedWriter clientOut = new BufferedWriter(clientWriter);
				clientOut.write(message);
				clientOut.newLine();
				clientOut.flush();
			}
			catch(Exception e)
			{
				System.out.println("--- clientmanager ERROR ---");
				System.out.println(e);
			}
		}
	}
	
	/**
	 * Returns a string representing all current client connections to the server
	 * @returns - a string detailing every user currently connected to the server
	 */	
	public String toString() 
	{
		// return a list of all the current clients
		String allClients = "CONNECTED USERS::\n";
		for(String username : clientMap.keySet()) 
		{
			allClients += username +"\n";
		}
		return allClients;
	}
}
