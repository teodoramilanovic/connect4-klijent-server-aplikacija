package spoji_4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class KlijentNit extends Thread {

	private Socket klijent;
	private String korisnickoIme;
	private Server server;
	private BufferedReader input;
	private PrintWriter output;

	public KlijentNit(Socket klijent, Server server) {
		this.klijent=klijent;
		this.server = server;
		try {
			input = new BufferedReader(new InputStreamReader(
					klijent.getInputStream()));
			output = new PrintWriter(
					klijent.getOutputStream(), true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			String poruka;
			while (true) {
				poruka = input.readLine();
				String akcija=poruka.split(" ")[0];
				if(akcija.equals("KORISNICKO_IME")) {
					korisnickoIme=poruka.split(" ")[1];
					server.dodajUListu(klijent);
				}
				else if(poruka.equals("DISKONEKTOVAN")) {
					server.posaljiProtivniku(poruka, klijent);
					zatvoriSve();
				}
				else if(poruka.equals("NAPUSTIO_IGRU")) {
					server.posaljiProtivniku("DISKONEKTOVAN", klijent);
					server.klijentDiskonektovan(this,1);
				}
				else if(akcija.equals("POTVRDA")) {
					server.dodajPotvrdu(klijent,poruka.split(" ")[1]);
				}
				else 
					server.posaljiProtivniku(poruka,this.klijent);
			}
		} catch (IOException e) {
			zatvoriSve();
		}
	}
	public void posaljiPoruku(String poruka) {
		output.println(poruka);
	}
	public Socket getSocket() {
		return klijent;
	}
	public String getKorisnickoIme() {
		return korisnickoIme;
	}
	public void zatvoriSve() {
		server.klijentDiskonektovan(this,0);
		try {
			input.close();
			output.close();
			klijent.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		interrupt();
	}
}
