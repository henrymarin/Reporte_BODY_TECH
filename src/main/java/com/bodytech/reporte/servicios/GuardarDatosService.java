package com.bodytech.reporte.servicios;
import org.json.JSONObject;

import com.bodytech.reporte.dtos.PrecargaResponse;
import com.javainuse.DtoEntrada;

public interface GuardarDatosService {

	JSONObject guardarDatos(DtoEntrada dto);

	JSONObject guardarDatosDepurado(DtoEntrada dto);

	PrecargaResponse obtenerInformacionDeLaPrecarga();

	JSONObject guardarDatosDepuradoConCargaDeAgentes(DtoEntrada dto);
	
}
