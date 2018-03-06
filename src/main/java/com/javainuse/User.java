package com.javainuse;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class User {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	private String  IdConversacion;
	
	private String  IdAgente;
	
	private String fechaInicioConversacion;
	
	private String fechaFinConversacion;
	
	private String tipoInteraccion;
	
	private String tipoSegmento;
	
	private String tiempoAlmuerzo;
	
	private String tiempoDescanso;
	
	private String tiempoEnPausa;
	
	private String horaSalida;
	
    private String name;

    private String email;
    
    private String estadosDelAgente;
    
    private String segmentosDelAgente;
    
    private String mediasType;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIdConversacion() {
		return IdConversacion;
	}

	public void setIdConversacion(String idConversacion) {
		IdConversacion = idConversacion;
	}

	public String getFechaInicioConversacion() {
		return fechaInicioConversacion;
	}

	public void setFechaInicioConversacion(String fechaInicioConversacion) {
		this.fechaInicioConversacion = fechaInicioConversacion;
	}

	public String getFechaFinConversacion() {
		return fechaFinConversacion;
	}

	public void setFechaFinConversacion(String fechaFinConversacion) {
		this.fechaFinConversacion = fechaFinConversacion;
	}

	public String getTipoInteraccion() {
		return tipoInteraccion;
	}

	public void setTipoInteraccion(String tipoInteraccion) {
		this.tipoInteraccion = tipoInteraccion;
	}

	public String getTipoSegmento() {
		return tipoSegmento;
	}

	public void setTipoSegmento(String tipoSegmento) {
		this.tipoSegmento = tipoSegmento;
	}

	public String getTiempoAlmuerzo() {
		return tiempoAlmuerzo;
	}

	public void setTiempoAlmuerzo(String tiempoAlmuerzo) {
		this.tiempoAlmuerzo = tiempoAlmuerzo;
	}

	public String getTiempoDescanso() {
		return tiempoDescanso;
	}

	public void setTiempoDescanso(String tiempoDescanso) {
		this.tiempoDescanso = tiempoDescanso;
	}

	public String getHoraSalida() {
		return horaSalida;
	}

	public void setHoraSalida(String horaSalida) {
		this.horaSalida = horaSalida;
	}

	public String getIdAgente() {
		return IdAgente;
	}

	public void setIdAgente(String idAgente) {
		IdAgente = idAgente;
	}

	public String getEstadosDelAgente() {
		return estadosDelAgente;
	}

	public void setEstadosDelAgente(String estadosDelAgente) {
		this.estadosDelAgente = estadosDelAgente;
	}

	public String getSegmentosDelAgente() {
		return segmentosDelAgente;
	}

	public void setSegmentosDelAgente(String segmentosDelAgente) {
		this.segmentosDelAgente = segmentosDelAgente;
	}

	public String getTiempoEnPausa() {
		return tiempoEnPausa;
	}

	public void setTiempoEnPausa(String tiempoEnPausa) {
		this.tiempoEnPausa = tiempoEnPausa;
	}

	public String getMediasType() {
		return mediasType;
	}

	public void setMediasType(String mediasType) {
		this.mediasType = mediasType;
	}

	

	
	
}
