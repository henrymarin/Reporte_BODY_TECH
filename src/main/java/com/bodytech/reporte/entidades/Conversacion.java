package com.bodytech.reporte.entidades;

import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

import com.bodytech.reporte.dtos.BTSReporteMapping;

@Entity // This tells Hibernate to make a table out of this class
@SqlResultSetMapping(name = "BTSReporteMapping", 
classes = {
	@ConstructorResult(targetClass = BTSReporteMapping.class, columns = {
			@ColumnResult(name = "id", 								type = Long.class), 
			@ColumnResult(name = "fecha", 							type = String.class),
			@ColumnResult(name = "nombreAgente", 					type = String.class),
			@ColumnResult(name = "horaIngresoCola", 				type = String.class),
			@ColumnResult(name = "numeroInteraccionesVoz", 			type = String.class),
			@ColumnResult(name = "numeroInteraccionesChat",			type = String.class),
			@ColumnResult(name = "numeroInteraccionesEmail", 		type = String.class),
			@ColumnResult(name = "tiempoIntervaloVoz", 				type = String.class),
			@ColumnResult(name = "tiempoIntervaloChat", 			type = String.class),
			@ColumnResult(name = "tiempoIntervaloEmail", 			type = String.class),
			@ColumnResult(name = "tiempoPausa", 					type = String.class),
			@ColumnResult(name = "tiempoAlmuerzo", 					type = String.class),
			@ColumnResult(name = "tiempoBreak", 					type = String.class),
			@ColumnResult(name = "tiempoPromedioVoz", 				type = String.class),
			@ColumnResult(name = "tiempoPromedioChat", 				type = String.class),
			@ColumnResult(name = "tiempoPromedioEmail", 			type = String.class),
			@ColumnResult(name = "horaCierreSesion", 				type = String.class),
			@ColumnResult(name = "tiempoProductivoAgente", 			type = String.class),
			@ColumnResult(name = "porcentajeProductividadAgente",	type = String.class)					
		})
	
})
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
