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
ServerThread details Runnables to be created by ClientMain following the establishment of a client connection to the server. ServerThread will receive all
messages from server to the client in parallel with operations within ClientMain
@ADT socket - socket object which holds a connection to the main server
@ADT serverReader - BufferedReader which allows client to recieve messages from the server
@ADT serverWriter - BufferedWriter which allows client to send messages to the server
*/
public class ServerThread implements Runnable 
{
	private Socket server;
	private BufferedReader serverReader;
	private BufferedWriter serverWriter;
	
	/**
	 * ServerThread constructor
	 * @param s - socket object containing the connection to the main server
	 */	
	public ServerThread(Socket s) 
	{
		server = s;
		try 
		{
			InputStreamReader serverInputStream = new InputStreamReader(server.getInputStream());
			serverReader  = new BufferedReader(serverInputStream);
			OutputStreamWriter serverOutputStream = new OutputStreamWriter(server.getOutputStream());
			serverWriter = new BufferedWriter(serverOutputStream);
		}
		catch(IOException e) 
		{
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	/**
	 * details the inner operations of the ServerThread - namely when a message is received from the server, the server message will be output to the client console
	 * when the thread receives the message "disconnect", or there is some other error (such as the client abruptly leaving), the connection and writers will be closed
	 */	
	@Override
	public void run() 
	{
		boolean terminate = false;
		// constantly attempt to receive messages from the server
		try {
			while(!terminate && !Thread.interrupted()) 
			{
				String serverMessage = serverReader.readLine();
				if(serverMessage == null) 
				{
					Thread.sleep(100);;
				}
				// receiving this message implies that the user sent the "exit" message to the console
				else if(serverMessage.equals("disconnect")) 
				{
					terminate = true;
				}
				else 
				{
					//simply print message on user console
					System.out.println(serverMessage);				
				}
			}
		}
		catch(Exception e) 
		{
			System.out.println(e);
		}
		finally 
		{
			// close server socket
			try 
			{
				server.close();
				serverReader.close();
				serverWriter.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return;
	}
 }
