package com.bodytech.reporte.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class ConversacionesPorTipo {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	private String  idConversacion;	

	private String  idSession;
	
	private String  idAgente;
	
	//chat, voz, email
	private String  tipo;
	
	//hold
	private String  segmento;
	
	private Date fechaInicioSegmento;
	
	private Date fechaFinSegmento;

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

	public String getIdSession() {
		return idSession;
	}

	public void setIdSession(String idSession) {
		this.idSession = idSession;
	}

	public String getIdAgente() {
		return idAgente;
	}

	public void setIdAgente(String idAgente) {
		this.idAgente = idAgente;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getSegmento() {
		return segmento;
	}

	public void setSegmento(String segmento) {
		this.segmento = segmento;
	}

	public Date getFechaInicioSegmento() {
		return fechaInicioSegmento;
	}

	public void setFechaInicioSegmento(Date fechaInicioSegmento) {
		this.fechaInicioSegmento = fechaInicioSegmento;
	}

	public Date getFechaFinSegmento() {
		return fechaFinSegmento;
	}

	public void setFechaFinSegmento(Date fechaFinSegmento) {
		this.fechaFinSegmento = fechaFinSegmento;
	}
	

	
}
