package com.bodytech.reporte.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class Conversacion {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	private String  idConversacion;	
	
	private String  idAgente;
	
	//columna [B]
	private String  nombreAgente;
	
	private Date fechaInicioConversacion;
	
	private Date fechaFinConversacion;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIdConversacion() {
		return idConversacion;
	}

	public void setIdConversacion(String idConversacion) {
		this.idConversacion = idConversacion;
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

	public Date getFechaInicioConversacion() {
		return fechaInicioConversacion;
	}

	public void setFechaInicioConversacion(Date fechaInicioConversacion) {
		this.fechaInicioConversacion = fechaInicioConversacion;
	}

	public Date getFechaFinConversacion() {
		return fechaFinConversacion;
	}

	public void setFechaFinConversacion(Date fechaFinConversacion) {
		this.fechaFinConversacion = fechaFinConversacion;
	}
	
	
	
}
