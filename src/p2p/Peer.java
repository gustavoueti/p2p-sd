package p2p;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
		public void run() {
			
		}
	}
	
	public static class MenuThread extends Thread {
		private MonitorThread fileMonitor;
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
			    } else if(anwser == ConsoleOption.SEARCH.getValue()) {
			    	
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
	}
	
	public static class MonitorThread extends Thread {
		private String pathName, peerId;
		private String[] files;
		
		public MonitorThread(String peerId,String pathName) {
			this.peerId = peerId;
			this.pathName = pathName;
		}
		
		public void run() {
			File dir = new File(this.pathName);
			String concatNames;
			while(true) {
				this.files = dir.list();
				concatNames = "";
				for(String fileName: this.files) {
					concatNames = concatNames + " " + fileName;
				}
				System.out.println("Sou peer "+  this.peerId + " com arquivos" + concatNames);
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static class SearchThread extends Thread {
		public void run() {
			
		}
	}

}
