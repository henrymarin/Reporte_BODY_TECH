package com.bodytech.reporte.servicios;
import org.json.JSONObject;
import com.javainuse.DtoEntrada;

@FunctionalInterface
public interface GuardarDatosService {

	JSONObject guardarDatos(DtoEntrada dto);
	
}
