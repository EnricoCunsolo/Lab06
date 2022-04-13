package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = -10;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO meteoDAO;
	private List<Citta> allCitta;
	private List<Citta> bestSequenza;
	private double costoMinimo;
	
	public Model() {
	// prova commit
	this.meteoDAO = new MeteoDAO();
	this.allCitta = new ArrayList<Citta>(meteoDAO.getAllCitta());
	this.bestSequenza = new ArrayList<Citta>();
	this.costoMinimo = 100000;
	}
	//
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		return meteoDAO.getAllRilevamenti();
	}

	public double getUmiditaMedia(int mese, String localita) {
		return meteoDAO.AVGUmiditaLocalitaMese(mese, localita);
	}
	
	public List<Citta> trovaSequenza(int mese) {
		List<Citta> parzialeCitta = new ArrayList<Citta>();
		for(Citta c : this.allCitta) {
			c.setRilevamenti(meteoDAO.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		cerca(parzialeCitta,0);
		return this.bestSequenza;
	}
	
	public void cerca(List<Citta> parzialeCitta, int livello) {
		
		if(livello==NUMERO_GIORNI_TOTALI) {
			double costo = calcolaCosto(parzialeCitta);
			if(costo<this.costoMinimo) {
				this.costoMinimo=costo;
				bestSequenza = new ArrayList<Citta>(parzialeCitta);
				System.out.println("Nuovo costo minimo= "+this.costoMinimo);
			}
		}
			else {
				for(Citta c : this.allCitta) {
					parzialeCitta.add(c);
					if(cittaValida(parzialeCitta)) {
						cerca(parzialeCitta, livello+1);
					}
					parzialeCitta.remove(parzialeCitta.size()-1);
				}	
			}
	}


	private boolean cittaValida(List<Citta> parzialeCitta) {
		
		for(Citta c : this.allCitta) { // controllo che tutte le città ci siano
			if(!parzialeCitta.contains(c) && parzialeCitta.size()==NUMERO_GIORNI_TOTALI)
				return false;
		}
		
		Citta ultima = parzialeCitta.get(parzialeCitta.size()-1); // controllo che una città sia contenuta meno di NGCM volte
		int cont=0;
		for(Citta c :parzialeCitta) {
			if(c.equals(ultima)) {
				cont++;
			}
		}
		if(cont>NUMERO_GIORNI_CITTA_MAX) {
			return false;
		}
		
		
		if(parzialeCitta.size()==1) { // se size = 1 va bene
			return true;
		}
		if(parzialeCitta.size()==2) {
			if(parzialeCitta.get(parzialeCitta.size()-1).equals(parzialeCitta.get(parzialeCitta.size()-2))) { // se size = 2 controllo che 
																								// quella in .get(ind=0) sia la stessa citta
				return true;
			}
			else return false;
		}
		if(parzialeCitta.size()==3) { // se size = 3 controllo che la prima la seconda e la terza sono uguali
			if(parzialeCitta.get(parzialeCitta.size()-1).equals(parzialeCitta.get(parzialeCitta.size()-2))
					&& parzialeCitta.get(parzialeCitta.size()-2).equals(parzialeCitta.get(parzialeCitta.size()-3))) {
				return true;
			}
			else return false;
		}
		
		Citta penultima = parzialeCitta.get(parzialeCitta.size()-2);
		
		if(ultima.equals(penultima)) { // se ultima è uguale a penultima ok
			return true;
		}
		
		if(parzialeCitta.size()>3 && !ultima.equals(penultima)) { // se ultima diversa da penultima, controllo che terzultima penultima
																  // e quartultima siano uguali
			
		Citta terzultima = parzialeCitta.get(parzialeCitta.size()-3);
		Citta quartultima = parzialeCitta.get(parzialeCitta.size()-4);
		if(penultima.equals(terzultima) && terzultima.equals(quartultima))
			return true;
		}

		return false;
	}

	private double calcolaCosto(List<Citta> parzialeCitta) {
		double costo = 0;
		for ( int i = 1; i < NUMERO_GIORNI_TOTALI;i++) {
			if(!parzialeCitta.get(i).equals(parzialeCitta.get(i-1))) {
				costo += COST;
			}
		}
		
		for(int d=1;d<=NUMERO_GIORNI_TOTALI;d++) {
			Citta c = parzialeCitta.get(d-1);
			double umidita = c.getRilevamenti().get(d-1).getUmidita();
			costo += umidita;
		}
		System.out.println("Costo minimo iterato= "+costo);
		return costo;
	}
	
	public List<Citta> getAllCitta(){
		return meteoDAO.getAllCitta();
	}
	

}
