package p2p;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Peer {
	public static enum ConsoleOption {
		INICIALIZA(1),SEARCH(2);
		
		private final int value;
		
		ConsoleOption(int selectedValue){
			value = selectedValue;
		}
		
		public int getValue(){
			return value;
		}
		
	}

	public static void main(String[] args) {
		MenuThread menu = new MenuThread();
		menu.start();
	}
	
	public static class ListenThread extends Thread {
		InetAddress peerIP;
		int portNumber;
		File dirReference;
		Map<String,Boolean> processedRequests = new HashMap<String,Boolean>();
		
		public ListenThread(String peerIP, String dir) throws UnknownHostException {
			this.peerIP = InetAddress.getByName(peerIP.split(":")[0]);
			this.portNumber = Integer.valueOf(peerIP.split(":")[1]);
			this.dirReference = new File(dir);
		}
		
		public void run() {
			try {
				DatagramSocket listenSocket = new DatagramSocket(this.portNumber);
				while(true) {
					byte[] messageBuffer = new byte[1024];
					DatagramPacket message = new DatagramPacket(messageBuffer,messageBuffer.length);
					listenSocket.receive(message);
					ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(message.getData()));
					Mensagem messageClass = (Mensagem) iStream.readObject();
					iStream.close();
					System.out.println(messageClass.getMessageContent());
					System.out.println(messageClass.getReqType());
					System.out.println(messageClass.getReqId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class MenuThread extends Thread {
		private MonitorThread fileMonitor;
		private ListenThread listener;
		private SearchThread searcher;
		private List<String> peersIPList = new ArrayList<String>();
		private String peerId, pathName;
		private Scanner input = new Scanner(System.in);
		
		public void run() {
			printMenu();
			while(true) {
			    int anwser = Integer.valueOf(this.input.nextLine());
			    if(anwser == ConsoleOption.INICIALIZA.getValue()) {
			    	this.requestInitVariables();
			    	this.initMonitor();
			    	this.initListen();
			    } else if(anwser == ConsoleOption.SEARCH.getValue()) {
			    	this.initSearch();
			    } else {
			    	System.out.println("Opção Inválida");
			    	printMenu();
			    }
			}
		}
		
		private static void printMenu() {
			System.out.println("Peer Menu - Pressione:");
			System.out.println(ConsoleOption.INICIALIZA.getValue() + "-" + ConsoleOption.INICIALIZA);
			System.out.println(ConsoleOption.SEARCH.getValue() + "-" + ConsoleOption.SEARCH);
		}
		
		private void requestInitVariables() {
			int nPeers;
			System.out.println("Inicializando peer - Digite o IP do peer:");
			this.peerId = this.input.nextLine();
			System.out.println("Digite o número de peers");
			nPeers = Integer.valueOf(this.input.nextLine());
			for(int i=0;i<nPeers;i++) {
				System.out.println("Digite o IP do peer " + i + ":");
				String peerIP = this.input.nextLine();
				this.peersIPList.add(peerIP);
			}
			System.out.println("Qual o diretorio deste peer:");
			this.pathName = this.input.nextLine();
		}
		
		private void initMonitor() {
			this.fileMonitor = new MonitorThread(this.peerId,this.pathName);
			this.fileMonitor.start();
		}
		
		private void initListen() {
			try {
				this.listener = new ListenThread(this.peerId, this.pathName);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			this.listener.start();
		}
		
		private void initSearch() {
			String searchFor;
			System.out.println("Digite o nome do arquivo que deve ser buscado:");
			searchFor = this.input.nextLine();
			this.searcher = new SearchThread(searchFor,this.peerId,this.peersIPList);
			this.searcher.start();
		}
	}
	
	public static class MonitorThread extends Thread {
		private String pathName, peerId;
		
		public MonitorThread(String peerId,String pathName) {
			this.peerId = peerId;
			this.pathName = pathName;
		}
		
		public void run() {
			File dir = new File(this.pathName);
			String concatNames = getFileString(dir);
			System.out.println("arquivos da pasta:" + concatNames);
			while(true) {
				concatNames = getFileString(dir);
				System.out.println("Sou peer "+  this.peerId + " com arquivos" + concatNames);
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private static String getFileString(File dir) {
			String[] files = dir.list();
			String concatNames = "";
			for(String fileName: files) {
				concatNames = concatNames + " " + fileName;
			}
			return concatNames;
		}
	}
	
	public static class SearchThread extends Thread {
		private List<String> peersIPs;
		private String fileName, peerId;
		private int ttl = 2;
		
		public SearchThread(String fileName, String peerId ,List<String> peersIPs) {
			this.peersIPs = peersIPs;
			this.fileName = fileName;
			this.peerId = peerId;
		}
		
		public void run() {
			try {
				DatagramSocket searchReqSocket = new DatagramSocket();
				byte[] payload = new byte[1024];
				for(String peerIP: this.peersIPs) {
					InetAddress ipAddress = InetAddress.getByName(peerIP.split(":")[0]);
					int port = Integer.valueOf(peerIP.split(":")[1]);
					String reqId = MD5.getMd5(this.peerId + new java.util.Date() + this.fileName);
					Mensagem msg = new Mensagem(Mensagem.MessageType.SEARCH,this.fileName,reqId);
					payload = msg.serialize();
					DatagramPacket sendPackage = new DatagramPacket(payload,payload.length,ipAddress,port);
					searchReqSocket.send(sendPackage);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class MD5 {
	    public static String getMd5(String input)
	    {
	        try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            byte[] messageDigest = md.digest(input.getBytes());
	            BigInteger no = new BigInteger(1, messageDigest);
	            String hashtext = no.toString(16);
	            while (hashtext.length() < 32) {
	                hashtext = "0" + hashtext;
	            }
	            return hashtext;
	        }
	        catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException(e);
	        }
	    }
	}
}
