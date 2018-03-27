package com.bodytech.reporte.servicios;


import com.bodytech.reporte.dtos.PrecargaResponse;
import com.javainuse.DtoEntrada;

public interface GuardarDatosService {

	PrecargaResponse obtenerInformacionDeLaPrecarga();

	void guardarDatosDepuradoConCargaDeAgentes(DtoEntrada dto);
	
}
