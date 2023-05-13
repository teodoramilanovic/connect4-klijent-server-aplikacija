package spoji_4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javafx.application.Platform;

public class Klijent extends Thread {
	
	private static int SERVER_PORT=8844;
	private Socket socket;
	private PrintWriter output;
    	private BufferedReader input;
	private String korisnickoIme;
	private KlijentAplikacija aplikacija;

	public Klijent(KlijentAplikacija aplikacija) {
		this.aplikacija=aplikacija;
		try {
			InetAddress adresa = InetAddress.getByName("localhost");
			socket = new Socket(adresa, SERVER_PORT);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            System.out.println("Klijent se povezao na server!");
            
		} catch (IOException e) {
			try {
				zatvori();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void run() {
		try {
			String porukaOdServera;
			while (true) {
					porukaOdServera = input.readLine();
					if(porukaOdServera==null)
						break;
					String akcija=porukaOdServera.split(" ")[0];
					if(akcija.equals("POCETAK")) {
						String ime=porukaOdServera.split(" ")[1];
						String prviPotez=porukaOdServera.split(" ")[2];
						aplikacija.setImeProtivnika(ime);
						if (prviPotez.equals("0"))
							aplikacija.setPrviPotez(true);
						else
							aplikacija.setPrviPotez(false);
						aplikacija.setPovezanProtivnik();
					}
					else if(akcija.equals("PORUKA")) {
						String poruka=porukaOdServera.substring(7);
						Platform.runLater(new Runnable(){

							@Override
							public void run() {
								aplikacija.dodajUChat(poruka);
							}
						
						});
					}
					else if(akcija.equals("POTEZ")){
						String potez=porukaOdServera.substring(6);
						Platform.runLater(new Runnable(){

							@Override
							public void run() {
								aplikacija.dodajPotez(potez);
							}
						
						});
					}
					else if(akcija.equals("DISKONEKTOVAN")) {
						Platform.runLater(new Runnable(){

							@Override
							public void run() {
								aplikacija.diskonektovanProtivnik();
							}
						
						});
					}
				}
				zatvori();
				} catch (IOException e) {
					try {
						zatvori();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
	}

	public void posaljiPoruku(String poruka) {
		output.println("PORUKA "+korisnickoIme+": "+poruka);
	}
	public void posaljiPotez(String potez) {
		output.println("POTEZ "+potez);
	}
	public void posaljiKorisnickoIme() {
		output.println("KORISNICKO_IME "+korisnickoIme);
	}
	public void posaljiDiskonektovan() {
		output.println("DISKONEKTOVAN");
	}
	public void posaljiNapustioIgru() {
		output.println("NAPUSTIO_IGRU");
	}
	public void posaljiPotvrdu(String vrijednost) {
		output.println("POTVRDA "+vrijednost);
	}
	public void setKorisnickoIme(String ime) {
		korisnickoIme=ime;
	}
	public String getKorisnickoIme() {
		return korisnickoIme;
	}
	public void zatvori() throws IOException {
		input.close();
		output.close();
		socket.close();
		interrupt();
	}
}
