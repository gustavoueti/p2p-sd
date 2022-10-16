package p2p;

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
		public ListenThread(String threadId) {
			
		}
		
		public void run() {
			
		}
	}
	
	public static class MenuThread extends Thread {
		public void run() {
			printMenu();
			Scanner in = new Scanner(System.in);
			while(true) {
			    int anwser = in.nextInt();
			    if(anwser == ConsoleOption.INICIALIZA.getValue()) {
			    	
			    } else if(anwser == ConsoleOption.SEARCH.getValue()) {
			    	
			    } else {
			    	System.out.println("Opção Inválida");
			    	printMenu();
			    }
			}
		}
		public static void printMenu() {
			System.out.println("Peer Menu - Pressione:");
			System.out.println(ConsoleOption.INICIALIZA.getValue() + "-" + ConsoleOption.INICIALIZA);
			System.out.println(ConsoleOption.SEARCH.getValue() + "-" + ConsoleOption.SEARCH);
		}
	}
	
	public static class MonitorThread extends Thread {
		public MonitorThread(String peerId,String pathName) {
			
		}
		
		public void run() {
			
		}
	}
	
	public static class SearchThread extends Thread {
		public void run() {
			
		}
	}

}
