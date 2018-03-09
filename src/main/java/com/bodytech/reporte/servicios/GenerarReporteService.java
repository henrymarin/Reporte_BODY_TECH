package com.bodytech.reporte.servicios;

import org.json.JSONObject;

import com.javainuse.DtoEntrada;

@FunctionalInterface
public interface GenerarReporteService {
	JSONObject generarReporte(DtoEntrada dto);
}
