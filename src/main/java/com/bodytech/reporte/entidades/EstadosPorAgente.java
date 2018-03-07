package com.bodytech.reporte.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class EstadosPorAgente {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	private String  idConversacion;	
	
	private String  idAgente;
	
	private Date fechaInicioEstado;
	
	private Date fechaFinEstado;
	
	//routingStatus y systemPresence
	private String estado;

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

	public Date getFechaInicioEstado() {
		return fechaInicioEstado;
	}

	public void setFechaInicioEstado(Date fechaInicioEstado) {
		this.fechaInicioEstado = fechaInicioEstado;
	}

	public Date getFechaFinEstado() {
		return fechaFinEstado;
	}

	public void setFechaFinEstado(Date fechaFinEstado) {
		this.fechaFinEstado = fechaFinEstado;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
	
}
