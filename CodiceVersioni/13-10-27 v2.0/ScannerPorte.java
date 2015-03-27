import java.net.*;

public class ScannerPorte 
{
	public synchronized DatagramSocket UDPSocket()
		{ DatagramSocket s = null;
			for (int i=1024; i<9999; i++)
				{try 
					{s =new DatagramSocket(i);
					return s;
					}
				catch (BindException e) 
					{}
				catch (Exception e) {e.printStackTrace();}
				}
		return s; 
		}
	public synchronized ServerSocket TCPSocket()
		{	ServerSocket s = null;
			for (int i=1; i<1024; i++)
			{try 
				{s =new ServerSocket(i);
				return s;
				}
			catch (BindException e) 
				{}
			catch (Exception e) {e.printStackTrace();}
			}
			return s;
		}
	
}