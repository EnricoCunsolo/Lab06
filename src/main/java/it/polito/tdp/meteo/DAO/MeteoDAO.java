package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		String sql = "SELECT * "
				+ "FROM situazione "
				+ "WHERE localita = ? AND MONTH(DATA)=?";
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, localita);
			st.setString(2, Integer.toString(mese)); // Integer.toString ritorna una stringa che rappresenta l'Integer
			ResultSet rs = st.executeQuery();
			
			
			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
	
	public double AVGUmiditaLocalitaMese(int mese, String localita) {
		String sql = "SELECT localita, AVG(Umidita) AS AVGUmidita "
				+ "FROM situazione "
				+ "WHERE localita = ? AND MONTH(Data)= ? "
				+ "GROUP BY localita";

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, localita);
			st.setString(2, Integer.toString(mese));
			ResultSet rs = st.executeQuery();
			rs.next();
			double avg = rs.getDouble("AVGUmidita");
			conn.close();
			return avg;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
	
	public List<Citta> getAllCitta(){
		final String sql = "SELECT DISTINCT Localita "
				+ "FROM situazione";

		List<Citta> allCitta = new ArrayList<Citta>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				allCitta.add(new Citta (rs.getString("Localita")));
			}

			conn.close();
			return allCitta;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


}
