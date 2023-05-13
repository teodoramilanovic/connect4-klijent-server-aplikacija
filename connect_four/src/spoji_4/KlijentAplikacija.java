package spoji_4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

public class KlijentAplikacija extends Application{
	
    private Stage stage;
    private Scene scene;
    private Klijent klijent;
    private static final int VELICINA_TOKENA = 80;
    private static final int KOLONE = 7;
    private static final int REDOVI = 6;
    private Token[][] matrica = new Token[KOLONE][REDOVI];
    private Pane tokenPane = new Pane();
    private boolean crveniNaRedu=true;
    private boolean povezanProtivnik=false;
    private TextFlow prikaz;
    private Label izvjestaj;
    private String korisnickoIme;
    private String imeProtivnika;
    private boolean tvojRed;
    private boolean krajIgre=false;
    private HBox revans;
    private Paint bojaI=Color.rgb(210, 4, 45);
    private Paint bojaP;
    private boolean nerijeseno=false;
    
	@Override
	public void start(Stage stage){
		klijent=new Klijent(this);
        klijent.start();
        
        this.stage = stage;
        
        VBox pocetna=new VBox(10);
		pocetna.setPrefWidth(500);
		pocetna.setPrefHeight(300);
		pocetna.setAlignment(Pos.CENTER);
		Label unos=new Label("Unesite korisnicko ime:");
		unos.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
		unos.setTextFill(Color.rgb(229,204,255));
		TextField korIme=new TextField();
		korIme.setMaxWidth(200);
		korIme.setMaxHeight(30);
		korIme.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
		korIme.setStyle("-fx-control-inner-background: #2A59A9; -fx-text-box-border: transparent;");
		Button igrajteB=new Button("Igrajte");
		igrajteB.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
		igrajteB.setTextFill(Color.rgb(229,204,225));
		igrajteB.setStyle("-fx-background-color: #2A59A9;");
		
		Label greska=new Label();
		greska.setTextFill(Color.RED);
		greska.setManaged(false);
		greska.setVisible(false);
		pocetna.getChildren().addAll(unos,korIme,igrajteB,greska);
		pocetna.setBackground(new Background(new BackgroundFill(Color.rgb(25,25,112),null,null)));
		
        scene = new Scene(pocetna,500,300);
        stage.setScene(scene);
        stage.setTitle("Connect Four");
        stage.setResizable(false);
        stage.show();
        
        igrajteB.setOnAction(e-> {
        	if(korIme.getText().isEmpty()) {
        		greska.setText("Unesite korisnicko ime!");
        		greska.setManaged(true);
        		greska.setVisible(true);
        		return;
        	}
        	greska.setText("");
        	korisnickoIme=korIme.getText();
        	klijent.setKorisnickoIme(korIme.getText());
        	klijent.posaljiKorisnickoIme();
        	stage.setScene(napraviScenu());
        	stage.show();
        	povezan();
        	
        });
	}
	private void povezan() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				if(!povezanProtivnik) {
					
					izvjestaj.setText("Cekanje na protivnika...");
					
				}
				else {
					if(krajIgre) {
						if(nerijeseno)
							izvjestaj.setText("Nerijeseno!");
						else
							izvjestaj.setText("Pobjednik je " + (tvojRed ? korisnickoIme : imeProtivnika)+"!");
						dodajURevans();
					}
					if(!krajIgre && tvojRed)
						izvjestaj.setText(korisnickoIme+" na redu!");
					else if (!krajIgre && !tvojRed)
						izvjestaj.setText(imeProtivnika+" na redu!");
				}
				
			}
			
		});
	}
	
	private Scene napraviScenu() {
		
		Pane igra = new Pane();
        igra.getChildren().add(tokenPane);

        Shape tabla = new Rectangle(KOLONE * VELICINA_TOKENA, REDOVI * VELICINA_TOKENA);
        
        for (int y = 0; y < REDOVI; y++) {
            for (int x = 0; x < KOLONE; x++) {
                Circle krug = new Circle(VELICINA_TOKENA / 2);
                krug.setCenterX(VELICINA_TOKENA / 2);
                krug.setCenterY(VELICINA_TOKENA / 2);
                krug.setTranslateX(x * VELICINA_TOKENA);
                krug.setTranslateY(y * VELICINA_TOKENA);
                
                tabla = Shape.subtract(tabla, krug);
            }
        }
        tabla.setFill(Color.rgb(42,89,169));
        igra.getChildren().add(tabla);
        
        for (int y = 0; y < REDOVI; y++) {
            for (int x = 0; x < KOLONE; x++) {
                Circle krug = new Circle(VELICINA_TOKENA / 2);
                krug.setCenterX(VELICINA_TOKENA / 2);
                krug.setCenterY(VELICINA_TOKENA / 2);
                krug.setTranslateX(x * VELICINA_TOKENA);
                krug.setTranslateY(y * VELICINA_TOKENA);
                krug.setFill(Color.rgb(25,25,112));
                InnerShadow sjena = new InnerShadow(40,
                        Color.BLACK);
             
                krug.setEffect(sjena);
                tokenPane.getChildren().add(krug);
            }
        } 
        
        List<Rectangle> lista = new ArrayList<>();

        for (int i = 0; i < KOLONE; i++) {
            Rectangle pravougaonik = new Rectangle(VELICINA_TOKENA, REDOVI * VELICINA_TOKENA);
            pravougaonik.setTranslateX(i * VELICINA_TOKENA);
            pravougaonik.setFill(Color.TRANSPARENT);

            int kolona = i;
            pravougaonik.setOnMouseClicked(e -> {
            	if(povezanProtivnik && tvojRed && !krajIgre) {
            		ubaciToken(new Token(crveniNaRedu), kolona);
    			}
            	});

            lista.add(pravougaonik);
        }
        
        igra.getChildren().addAll(lista);
        
        VBox chat=napraviChat();
        
        HBox igraChat = new HBox(50);
		igraChat.getChildren().addAll(igra,chat);
		
		VBox cijeli=new VBox(10);
		cijeli.setPadding(new Insets(40,50,50,50));
		izvjestaj=new Label("");
		izvjestaj.setPrefWidth(400);
		izvjestaj.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		izvjestaj.setTextFill(Color.rgb(229,204,255));
		
		Button napusti = new Button("Napusti");
		napusti.setOnAction(new EventHandler<ActionEvent>() {

			@Override
				public void handle(ActionEvent event) {
					klijent.posaljiNapustioIgru();
					povezanProtivnik=false;
					crveniNaRedu=true;
					nerijeseno=false;
					krajIgre=false;
					imeProtivnika="";
					matrica=new Token[KOLONE][REDOVI];
					stage.setScene(scene);
				}
		});

		Pane dugme = new Pane();
		napusti.setLayoutX(510);
		napusti.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
		napusti.setTextFill(Color.rgb(229,204,225));
		napusti.setStyle("-fx-background-color: #B22222;");
		dugme.getChildren().add(napusti);
		dugme.setMaxHeight(30);
		
		HBox vrh=new HBox(10);
		vrh.getChildren().addAll(izvjestaj,dugme);
		
		revans=new HBox(10);
		revans.setPrefHeight(20);
		
		cijeli.getChildren().addAll(vrh,igraChat,revans);
		
		FileInputStream inputstream=null;
		try {
			inputstream = new FileInputStream("D:\\drugapoz.jpg");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} 
		Image slika = new Image(inputstream); 
        BackgroundImage pozSlika = new BackgroundImage(slika,
                                                   BackgroundRepeat.NO_REPEAT,
                                                   BackgroundRepeat.NO_REPEAT,
                                                   BackgroundPosition.DEFAULT,
                                                   BackgroundSize.DEFAULT);
        Background pozadina = new Background(pozSlika);
        cijeli.setBackground(pozadina);
        
        return new Scene(cijeli);
	}
	
	private VBox napraviChat() {
		prikaz = new TextFlow();
		prikaz.setStyle("-fx-background-color: #191970; -fx-text-box-border: transparent;");
		prikaz.setPrefHeight(450);
		prikaz.setPrefWidth(300);
		prikaz.setPadding(new Insets(10));
		
		TextField upis = new TextField();
		upis.setPrefWidth(300);
		upis.setPrefHeight(31);
		upis.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
		upis.setStyle("-fx-control-inner-background: #2A59A9; -fx-text-box-border: transparent;");
		upis.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(povezanProtivnik)
					klijent.posaljiPoruku(upis.getText());
				
				if(!upis.getText().isEmpty()) {
					Text ime = new Text(klijent.getKorisnickoIme()+": "); 
				    ime.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
				    ime.setFill(bojaI);
				    Text ostatak = new Text(upis.getText() +"\n"); 
				    ostatak.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
				    ostatak.setFill(Color.rgb(229,204,225));
				    ObservableList list = prikaz.getChildren(); 
				    list.addAll(ime,ostatak);       
					upis.clear();
				}
			}
		});
		
		Button posalji=new Button("Posaljite");
		posalji.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
		posalji.setTextFill(Color.rgb(25,25,112));
		posalji.setStyle("-fx-background-color: #2A59A9;");
		posalji.setOnAction(e-> {
			
			if(povezanProtivnik)
				klijent.posaljiPoruku(upis.getText());
			
			if(!upis.getText().isEmpty()) {
				Text ime = new Text(korisnickoIme+": "); 
			    ime.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
			    ime.setFill(bojaI);
			    Text ostatak = new Text(upis.getText() +"\n"); 
			    ostatak.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
			    ostatak.setFill(Color.rgb(229,204,225));
			    ObservableList list = prikaz.getChildren(); 
			    list.addAll(ime,ostatak); 
				upis.clear();
			}
        });
		HBox slanje=new HBox();
		slanje.getChildren().addAll(upis,posalji);
		
		VBox chat=new VBox();
		chat.getChildren().addAll(prikaz,slanje);
		
		return chat;
	}
	public void dodajUChat(String poruka) {

		if(poruka.split(" ").length>=2) {
			Text ime = new Text(imeProtivnika+": "); 
		    ime.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
		    ime.setFill(bojaP);
		    Text ostatak = new Text(poruka.split(" ")[1] +"\n"); 
		    ostatak.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
		    ostatak.setFill(Color.rgb(229,204,225));
			ObservableList list = prikaz.getChildren(); 
		    list.addAll(ime,ostatak);
		}
	}
	
	private void ubaciToken(Token token, int kolona) {
		InnerShadow sjena = new InnerShadow(20,
                Color.BLACK);
		token.setEffect(sjena);
        int red = REDOVI - 1;
        do {
            if (!getToken(kolona, red))
                break;

            red--;
        } while (red >= 0);

        if (red < 0)
            return;

        matrica[kolona][red] = token;
        tokenPane.getChildren().add(token);
        token.setTranslateX(kolona * VELICINA_TOKENA);

        final int trenutniRed = red;

        TranslateTransition animacija = new TranslateTransition(Duration.seconds(0.5), token);
        animacija.setToY(red * VELICINA_TOKENA);
        animacija.setOnFinished(e -> {
            if (jeZavrsena(kolona, trenutniRed)) {
                krajIgre=true;
                povezan();
                if(tvojRed)
                	klijent.posaljiPotez(kolona+"");
                return;
            }
            if(tvojRed)
            	klijent.posaljiPotez(kolona+"");
            tvojRed = !tvojRed;
            crveniNaRedu = !crveniNaRedu;
            povezan();
        });
        
        animacija.play();
    }
	
	private boolean getToken(int kolona, int red) {
        if (kolona < 0 || kolona >= KOLONE
                || red < 0 || red >= REDOVI)
            return false;

        return matrica[kolona][red]!=null;
    }
	
	private boolean jeZavrsena(int kolona,int red) {
		
	    for(int i=0; i<=KOLONE-4; i++) {
	    	int brojacC=0;
	    	int brojacZ=0;
	    	for(int j=i; j<i+4; j++) {
	    		if(matrica[j][red]!=null) {
	    			if(matrica[j][red].crvena)
	    				brojacC++;
	    			else
	    				brojacZ++;
	    		}
	    	}
	    	if(brojacC==4 || brojacZ==4)
	    		return true;
	    }
	    	
	    for(int i=REDOVI-1; i>=3; i--) {
	    	int brojacC=0;
	    	int brojacZ=0;
	    	for(int j=i; j>i-4; j--) {
	    		if(matrica[kolona][j]!=null) {
	    				
	    			if(matrica[kolona][j].crvena)
	    				brojacC++;
	    			else
	    				brojacZ++;
	    		}
	    	}
	    	if(brojacC==4 || brojacZ==4)
	    		return true;
	    }
	    	
	    int pocetnaK1=kolona-(REDOVI-1-red);
    	if(pocetnaK1<0)
    		pocetnaK1=0;
    	
    	int pocetniR1=red+kolona;
    	if(pocetniR1>REDOVI-1)
    		pocetniR1=REDOVI-1;
    	
    	int zavrsnaK1=red+kolona;
    	if(zavrsnaK1>KOLONE-1)
    		zavrsnaK1=KOLONE-1;
    	
    	int zavrsniR1=red-(KOLONE-1-kolona);
    	if(zavrsniR1<0)
    		zavrsniR1=0;
    	
	    for(int i=pocetnaK1; i<=zavrsnaK1-3; i++) {
	    		
	    	int brojacC=0;
	    	int brojacZ=0;
	    		
	    	int redKopija=pocetniR1;
	    	for(int j=i; j<i+4; j++) {
	    			
	    		if(matrica[j][redKopija]!=null) {
	    			if(matrica[j][redKopija].crvena)
	    				brojacC++;
	    			else
	    				brojacZ++;
	    		}
	    		redKopija--;
	    	}
	    	if(brojacC==4 || brojacZ==4)
	    		return true;
	    	pocetniR1--;
	    }
	    
	    int pocetnaK2=kolona+(REDOVI-1-red);
	    if(pocetnaK2>KOLONE-1)
    		pocetnaK2=KOLONE-1;
    	
    	int pocetniR2=red+(KOLONE-1-kolona);
    	if(pocetniR2>REDOVI-1)
    		pocetniR2=REDOVI-1;
    	
    	int zavrsnaK2=kolona-red;
    	if(zavrsnaK2<0)
    		zavrsnaK2=0;
    	
    	int zavrsniR2=red-kolona;
    	if(zavrsniR2<0)
    		zavrsniR2=0;
    	
	    for(int i=pocetnaK2; i>=zavrsnaK2+3; i--) {
	    	
	    	int brojacC=0;
	    	int brojacZ=0;
	    		
	    	int redKopija=pocetniR2;
	    	for(int j=i; j>i-4; j--) {
	
	    		if(matrica[j][redKopija]!=null) {
	    			if(matrica[j][redKopija].crvena)
	    				brojacC++;
	    			else
	    				brojacZ++;
	    		}
	    		redKopija--;
	    	}
	    	if(brojacC==4 || brojacZ==4)
	    		return true;
	    	pocetniR2--;
	    }
	    
	    if(jePopunjeno()) {
			nerijeseno=true;
			return true;
	    }
	    
	    return false;
	}
	private boolean jePopunjeno() {
		int brojac=0;
		for (int i = 0; i < REDOVI; i++) {
			for (int j = 0; j < KOLONE; j++) {
	            	if(matrica[j][i]!=null)
	            		brojac++;
	        }
		}
		if(brojac==REDOVI*KOLONE)
			return true;
		else
			return false;
	}
	private static class Token extends Circle {
        private final boolean crvena;
        public Token(boolean crvena) {
            super(VELICINA_TOKENA / 2, crvena ? Color.rgb(210, 4, 45) : Color.rgb(255, 215, 0));
            this.crvena = crvena;

            setCenterX(VELICINA_TOKENA / 2);
            setCenterY(VELICINA_TOKENA / 2);
        }
    }
	
	public static void main(String[]args) {
	    	launch(args);
	}
	
	public void setPovezanProtivnik() {
		povezanProtivnik=true;
		matrica=new Token[KOLONE][REDOVI];
		if(tvojRed) {
			bojaI=Color.rgb(210, 4, 45);
			bojaP=Color.rgb(255, 215, 0);
		}else {
			bojaP=Color.rgb(210, 4, 45);
			bojaI=Color.rgb(255, 215, 0);
		}
		ukloniIzRevansa();
		povezan();
	}
	public void setImeProtivnika(String ime) {
		imeProtivnika=ime;
	}
	public void dodajPotez(String kolona) {
		try {
			ubaciToken(new Token(crveniNaRedu),Integer.parseInt(kolona));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void setPrviPotez(boolean potez) {
		tvojRed=potez;
	}

	public void diskonektovanProtivnik() {
		izvjestaj.setText(imeProtivnika+" je napustio/la igru!");
		ukloniIzRevansa();
		tvojRed=false;
		krajIgre=false;
		nerijeseno=false;
		povezanProtivnik=false;
	}
	public void dodajURevans() {
		Label ponovo=new Label("Igrajte ponovo?");
		ponovo.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		ponovo.setTextFill(Color.rgb(42,89,169));
		Button da=new Button("Da");
		da.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
		da.setTextFill(Color.rgb(25,25,112));
		da.setStyle("-fx-background-color: #2A59A9;");
		
		da.setOnAction(new EventHandler<ActionEvent>() {
			@Override
				public void handle(ActionEvent event) {
					klijent.posaljiPotvrdu("1");
					resetTabla();
					ukloniIzRevansa();
					izvjestaj.setText("Cekanje na odgovor protivnika..");
					crveniNaRedu=true;
					krajIgre=false;
					nerijeseno=false;
				}
		});
		
		Button ne=new Button("Ne");
		ne.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
		ne.setTextFill(Color.rgb(25,25,112));
		ne.setStyle("-fx-background-color: #2A59A9;");
		
		ne.setOnAction(new EventHandler<ActionEvent>() {
			@Override
				public void handle(ActionEvent event) {
					klijent.posaljiPotvrdu("0");
					klijent.posaljiNapustioIgru();
					ukloniIzRevansa();
					povezanProtivnik=false;
					crveniNaRedu=true;
					krajIgre=false;
					nerijeseno=false;
					imeProtivnika="";
					matrica=new Token[KOLONE][REDOVI];
					stage.setScene(scene);
				}
		});
		
		revans.getChildren().addAll(ponovo,da,ne);
	}
	public void ukloniIzRevansa() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					revans.getChildren().clear();
				}catch(NullPointerException e) {
					return;
				}
			}
		});
	}
	public void resetTabla() {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				for (int y = 0; y < REDOVI; y++) {
			           for (int x = 0; x < KOLONE; x++) {
			               Circle krug = new Circle(VELICINA_TOKENA / 2);
			               krug.setCenterX(VELICINA_TOKENA / 2);
			               krug.setCenterY(VELICINA_TOKENA / 2);
			               krug.setTranslateX(x * VELICINA_TOKENA);
			               krug.setTranslateY(y * VELICINA_TOKENA);
			               krug.setFill(Color.rgb(25,25,112));
			               InnerShadow innerShadow = new InnerShadow(40,
			                        Color.BLACK);
			             
			               krug.setEffect(innerShadow);
			               tokenPane.getChildren().add(krug);
			            }
			        } 
			}
		});
		
	}
	@Override
    public void stop() throws IOException{
		klijent.posaljiDiskonektovan();
        klijent.zatvori();
    }
	

}
