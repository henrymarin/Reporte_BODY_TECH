package com.bodytech.reporte.dtos;

public class BTSReporteMapping {

	
	private String fecha;	
	private String nombreAgente;
	private String horaIngresoCola;
	private String numeroInteraccionesVoz;
	private String numeroInteraccionesChat;
	private String numeroInteraccionesEmail;
	private String tiempoIntervaloVoz;
	private String tiempoIntervaloChat;
	private String tiempoIntervaloEmail;
	private String tiempoPausa;
	private String tiempoAlmuerzo;
	private String tiempoBreak;
	private String tiempoPromedioVoz;
	private String tiempoPromedioChat;
	private String tiempoPromedioEmail;
	private String horaCierreSesion;
	private String horaInicioSesion;
	private String tiempoProductivoAgente;
	private String porcentajeProductividadAgente;
	private String idAgente;
	private String tiempoProductivoAgenteUno;
	private String tiempoDisponible;
	private String tiempoOcupado;
	private String tiempoAusente;
	private String tiempoEnReunion;
	private String tiempoEnCapacitacion;
	private String tiempoEnCola;
	
	public BTSReporteMapping() {
		super();
	}

	public BTSReporteMapping(String fecha, String nombreAgente, String horaIngresoCola,
			String numeroInteraccionesVoz, String numeroInteraccionesChat, String numeroInteraccionesEmail,
			String tiempoIntervaloVoz, String tiempoIntervaloChat, String tiempoIntervaloEmail, String tiempoPausa,
			String tiempoAlmuerzo, String tiempoBreak, String tiempoPromedioVoz, String tiempoPromedioChat,
			String tiempoPromedioEmail, String horaCierreSesion, String tiempoProductivoAgente, 
			String porcentajeProductividadAgente, String idAgente, String tiempoProductivoAgenteUno) {
		super();
		
		this.fecha = fecha;
		this.nombreAgente = nombreAgente;
		this.horaIngresoCola = horaIngresoCola;
		this.numeroInteraccionesVoz = numeroInteraccionesVoz;
		this.numeroInteraccionesChat = numeroInteraccionesChat;
		this.numeroInteraccionesEmail = numeroInteraccionesEmail;
		this.tiempoIntervaloVoz = tiempoIntervaloVoz;
		this.tiempoIntervaloChat = tiempoIntervaloChat;
		this.tiempoIntervaloEmail = tiempoIntervaloEmail;
		this.tiempoPausa = tiempoPausa;
		this.tiempoAlmuerzo = tiempoAlmuerzo;
		this.tiempoBreak = tiempoBreak;
		this.tiempoPromedioVoz = tiempoPromedioVoz;
		this.tiempoPromedioChat = tiempoPromedioChat;
		this.tiempoPromedioEmail = tiempoPromedioEmail;
		this.horaCierreSesion = horaCierreSesion;
		this.tiempoProductivoAgente = tiempoProductivoAgente;
		this.porcentajeProductividadAgente = porcentajeProductividadAgente;
		this.idAgente = idAgente;
		this.tiempoProductivoAgenteUno = tiempoProductivoAgenteUno;
	}
				
	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getNombreAgente() {
		return nombreAgente;
	}

	public void setNombreAgente(String nombreAgente) {
		this.nombreAgente = nombreAgente;
	}

	public String getHoraIngresoCola() {
		return horaIngresoCola;
	}

	public void setHoraIngresoCola(String horaIngresoCola) {
		this.horaIngresoCola = horaIngresoCola;
	}

	public String getNumeroInteraccionesVoz() {
		return numeroInteraccionesVoz;
	}

	public void setNumeroInteraccionesVoz(String numeroInteraccionesVoz) {
		this.numeroInteraccionesVoz = numeroInteraccionesVoz;
	}

	public String getNumeroInteraccionesChat() {
		return numeroInteraccionesChat;
	}

	public void setNumeroInteraccionesChat(String numeroInteraccionesChat) {
		this.numeroInteraccionesChat = numeroInteraccionesChat;
	}

	public String getNumeroInteraccionesEmail() {
		return numeroInteraccionesEmail;
	}

	public void setNumeroInteraccionesEmail(String numeroInteraccionesEmail) {
		this.numeroInteraccionesEmail = numeroInteraccionesEmail;
	}

	public String getTiempoIntervaloVoz() {
		return tiempoIntervaloVoz;
	}

	public void setTiempoIntervaloVoz(String tiempoIntervaloVoz) {
		this.tiempoIntervaloVoz = tiempoIntervaloVoz;
	}

	public String getTiempoIntervaloChat() {
		return tiempoIntervaloChat;
	}

	public void setTiempoIntervaloChat(String tiempoIntervaloChat) {
		this.tiempoIntervaloChat = tiempoIntervaloChat;
	}

	public String getTiempoIntervaloEmail() {
		return tiempoIntervaloEmail;
	}

	public void setTiempoIntervaloEmail(String tiempoIntervaloEmail) {
		this.tiempoIntervaloEmail = tiempoIntervaloEmail;
	}

	public String getTiempoPausa() {
		return tiempoPausa;
	}

	public void setTiempoPausa(String tiempoPausa) {
		this.tiempoPausa = tiempoPausa;
	}

	public String getTiempoAlmuerzo() {
		return tiempoAlmuerzo;
	}

	public void setTiempoAlmuerzo(String tiempoAlmuerzo) {
		this.tiempoAlmuerzo = tiempoAlmuerzo;
	}

	public String getTiempoBreak() {
		return tiempoBreak;
	}

	public void setTiempoBreak(String tiempoBreak) {
		this.tiempoBreak = tiempoBreak;
	}

	public String getTiempoPromedioVoz() {
		return tiempoPromedioVoz;
	}

	public void setTiempoPromedioVoz(String tiempoPromedioVoz) {
		this.tiempoPromedioVoz = tiempoPromedioVoz;
	}

	public String getTiempoPromedioChat() {
		return tiempoPromedioChat;
	}

	public void setTiempoPromedioChat(String tiempoPromedioChat) {
		this.tiempoPromedioChat = tiempoPromedioChat;
	}

	public String getTiempoPromedioEmail() {
		return tiempoPromedioEmail;
	}

	public void setTiempoPromedioEmail(String tiempoPromedioEmail) {
		this.tiempoPromedioEmail = tiempoPromedioEmail;
	}

	public String getHoraCierreSesion() {
		return horaCierreSesion;
	}

	public void setHoraCierreSesion(String horaCierreSesion) {
		this.horaCierreSesion = horaCierreSesion;
	}

	public String getTiempoProductivoAgente() {
		return tiempoProductivoAgente;
	}

	public void setTiempoProductivoAgente(String tiempoProductivoAgente) {
		this.tiempoProductivoAgente = tiempoProductivoAgente;
	}

	public String getPorcentajeProductividadAgente() {
		return porcentajeProductividadAgente;
	}

	public void setPorcentajeProductividadAgente(String porcentajeProductividadAgente) {
		this.porcentajeProductividadAgente = porcentajeProductividadAgente;
	}

	public String getIdAgente() {
		return idAgente;
	}

	public void setIdAgente(String idAgente) {
		this.idAgente = idAgente;
	}

	public String getTiempoProductivoAgenteUno() {
		return tiempoProductivoAgenteUno;
	}

	public void setTiempoProductivoAgenteUno(String tiempoProductivoAgenteUno) {
		this.tiempoProductivoAgenteUno = tiempoProductivoAgenteUno;
	}

	public String getTiempoDisponible() {
		return tiempoDisponible;
	}

	public void setTiempoDisponible(String tiempoDisponible) {
		this.tiempoDisponible = tiempoDisponible;
	}

	public String getTiempoOcupado() {
		return tiempoOcupado;
	}

	public void setTiempoOcupado(String tiempoOcupado) {
		this.tiempoOcupado = tiempoOcupado;
	}

	public String getTiempoAusente() {
		return tiempoAusente;
	}

	public void setTiempoAusente(String tiempoAusente) {
		this.tiempoAusente = tiempoAusente;
	}

	public String getTiempoEnReunion() {
		return tiempoEnReunion;
	}

	public void setTiempoEnReunion(String tiempoEnReunion) {
		this.tiempoEnReunion = tiempoEnReunion;
	}

	public String getTiempoEnCapacitacion() {
		return tiempoEnCapacitacion;
	}

	public void setTiempoEnCapacitacion(String tiempoEnCapacitacion) {
		this.tiempoEnCapacitacion = tiempoEnCapacitacion;
	}

	public String getTiempoEnCola() {
		return tiempoEnCola;
	}

	public void setTiempoEnCola(String tiempoEnCola) {
		this.tiempoEnCola = tiempoEnCola;
	}

	public String getHoraInicioSesion() {
		return horaInicioSesion;
	}

	public void setHoraInicioSesion(String horaInicioSesion) {
		this.horaInicioSesion = horaInicioSesion;
	}
		
}
