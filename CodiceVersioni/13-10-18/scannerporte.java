import java.net.*;

public class scannerporte 
{
	public synchronized DatagramSocket UDPSocket()
		{ DatagramSocket s = null;
			/*try
			{System.out.println("UDP: me la dormo");
			Thread.sleep(5000);}
			catch(InterruptedException e){}*/
			for (int i=1024; i<10000; i++)
				{try 
					{s =new DatagramSocket(i);
					return s;
					}
				catch (BindException e) 
					{}
				catch (Exception e) {}
				}
		return s; 
		}
	public synchronized ServerSocket TCPSocket()
		{	ServerSocket s = null;
			/*try
			{System.out.println("TCP: me la dormo");
			Thread.sleep(5000);}
			catch(InterruptedException e){}*/
			for (int i=1; i<1024; i++)
			{try 
				{s =new ServerSocket(i);
				return s;
				}
			catch (BindException e) 
				{}
			catch (Exception e) {}
			}
			return s;
		}
	
}