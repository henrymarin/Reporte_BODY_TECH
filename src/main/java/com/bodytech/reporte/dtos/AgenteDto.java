package com.bodytech.reporte.dtos;

import java.io.Serializable;

public class AgenteDto  implements Serializable{

	private static final long serialVersionUID = -3125999404975347403L;

    private String id;
	
	private String  idAgente;
	
	private String  nombreAgente;

	private String tipoAgente;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdAgente() {
		return idAgente;
	}

	public void setIdAgente(String idAgente) {
		this.idAgente = idAgente;
	}

	public String getNombreAgente() {
		return nombreAgente;
	}

	public void setNombreAgente(String nombreAgente) {
		this.nombreAgente = nombreAgente;
	}

	public String getTipoAgente() {
		return tipoAgente;
	}

	public void setTipoAgente(String tipoAgente) {
		this.tipoAgente = tipoAgente;
	}
	
	
	
}
