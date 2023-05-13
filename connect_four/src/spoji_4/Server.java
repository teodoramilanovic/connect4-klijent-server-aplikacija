package spoji_4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {
	private int SERVER_PORT = 8844;
	private ServerSocket serverSocket=null;
	private ArrayList<ArrayList<Socket>> klijenti=new ArrayList<ArrayList<Socket>>();
	private ArrayList<KlijentNit> niti=new ArrayList<>();
	private ArrayList<ArrayList<Integer>> revansi=new ArrayList<ArrayList<Integer>>();
	
	public Server() throws IOException {
		serverSocket = new ServerSocket(SERVER_PORT);
		System.out.println("Server je pokrenut...");
		execute();
	}
	public void execute(){
		while(true) {
			Socket socket=null;
			try {
				socket = serverSocket.accept();
				KlijentNit klijent=new KlijentNit(socket,this);
				klijent.start();
				niti.add(klijent);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	private void posaljiPocetak(Socket igrac1,Socket igrac2) {
		String poruka="POCETAK ";
		String ime1=getKorisnickoIme(igrac1);
		String ime2=getKorisnickoIme(igrac2);
		
		for(KlijentNit nit : niti) {
			if(nit.getSocket()==igrac1)
				nit.posaljiPoruku(poruka+ime2+" "+0);
			else if(nit.getSocket()==igrac2)
				nit.posaljiPoruku(poruka+ime1+" "+1);
		}
	}
	
	public void posaljiProtivniku(String poruka,Socket klijent) {
		for(ArrayList<Socket> lista : klijenti) {
			if(lista.size()==2 && (klijent==lista.get(0) || klijent==lista.get(1))) {
				Socket protivnik=(klijent==lista.get(0) ? lista.get(1) : lista.get(0));
				for(KlijentNit nit : niti) {
					if(nit.getSocket()==protivnik)
						nit.posaljiPoruku(poruka);
				}
				break;
			}
		}
	}
	
	public synchronized void dodajUListu(Socket socket) {
		if(!klijenti.isEmpty() && klijenti.get(klijenti.size()-1).size()==1) {
			klijenti.get(klijenti.size()-1).add(socket);
			Socket igrac1=klijenti.get(klijenti.size()-1).get(0);
			Socket igrac2=klijenti.get(klijenti.size()-1).get(1);
			posaljiPocetak(igrac1,igrac2);
			
			ArrayList<Integer>lista =new ArrayList<Integer>(Arrays.asList(-1,-1));
			revansi.add(lista);
		}
		else {
			ArrayList<Socket> parKlijenata=new ArrayList<>();
			parKlijenata.add(socket);
			klijenti.add(parKlijenata);
		}
	}
	
	public synchronized void klijentDiskonektovan(KlijentNit klijent,int akcija) {
		
		if(akcija==0)
			niti.remove(klijent);
		int indeks=-1;
		for(int i=0; i<klijenti.size(); i++) {
			if(klijent.getSocket().equals(klijenti.get(i).get(0)) || (klijenti.get(i).size()==2 && klijent.getSocket().equals(klijenti.get(i).get(1)))) {
				indeks=i;
			}
		}
		if(indeks!=-1) {
			klijenti.remove(indeks);
			if(!revansi.isEmpty())
				revansi.remove(indeks);
		}
		
	}
	public synchronized void dodajPotvrdu(Socket klijent, String vrijednost) {
		int indeks=-1;
		for(int i=0; i<klijenti.size(); i++) {
			if(klijent.equals(klijenti.get(i).get(0)) || (klijenti.get(i).size()==2 && klijent.equals(klijenti.get(i).get(1)))) {
				indeks=i;
			}
		}
		if(indeks!=-1) {
			int potvrda=revansi.get(indeks).get(0);
			if(potvrda==-1)
				revansi.get(indeks).set(0, 1);
			else {
				revansi.get(indeks).set(1, 1);
				if(revansi.get(indeks).get(0)==1 && revansi.get(indeks).get(1)==1) {
					posaljiPocetak(klijenti.get(indeks).get(0),klijenti.get(indeks).get(1));
					revansi.get(indeks).set(0,-1);
					revansi.get(indeks).set(1, -1);
				}
			}
		}
	}
	private String getKorisnickoIme(Socket igrac) {
		for(KlijentNit nit : niti) {
			if(nit.getSocket()==igrac)
				return nit.getKorisnickoIme();
		}
		return "";
	}
	
	public static void main(String[]args) throws IOException {
		new Server();
	}
}
