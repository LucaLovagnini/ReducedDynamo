import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.net.*;
import java.util.*;


public class StorageNode_UDPWriter extends Thread {
		StorageNode_UDPMonitor UDPM;
		DatagramSocket UDPServiceSocket;
		List<DatagramPacket> list;
		int socketPort;
		public StorageNode_UDPWriter(StorageNode_UDPMonitor UDPM, DatagramSocket UDPServiceSocket, List<DatagramPacket> list, int socketPort) {
			this.UDPM = UDPM;
			this.UDPServiceSocket = UDPServiceSocket;
			this.list = list;
			this.socketPort = socketPort;
		}

		public void run() {
			while (true) {
					try {
							UDPM.StartWrite(); //se supero questa istruzione significa che ho acquisito la lock
							UDPServiceSocket.setSoTimeout(1000); //sto ad ascoltare per 1 secondo

							while (true) {
									byte [] data = new byte[200];
									DatagramPacket dp = new DatagramPacket (data, data.length);
									UDPServiceSocket.receive(dp);
									list.add(dp); //aggiungo alla lista il pacchetto inviato dal client
								}

						} catch (SocketTimeoutException e) //eccezione sollevata quando il tempo è scaduto
						{} catch (Exception e) {
							e.printStackTrace();
						}

					UDPM.EndWrite(); //rilascio la lock
				}
		}
	}
