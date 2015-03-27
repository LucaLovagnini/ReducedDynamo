import java.net.*;

public class scannerporte 
{
	public DatagramSocket UDPSocket()
	{ for (int i=1024; i<10000; i++)
		{try 
			{DatagramSocket s =new DatagramSocket(i);
			return s;
			}
		catch (BindException e) 
			{}
		catch (Exception e) {}
		}
	return null; 
	}
	public ServerSocket TCPSocket()
	{
		for (int i=1; i<1024; i++)
		{try 
			{ServerSocket s =new ServerSocket(i);
			return s;
			}
		catch (BindException e) 
			{}
		catch (Exception e) {}
		}
		return null;
	}
	
}