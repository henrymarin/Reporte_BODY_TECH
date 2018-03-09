package com.bodytech.reporte.servicios.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.bodytech.reporte.servicios.GenerarReporteService;
import com.javainuse.DtoEntrada;

@Service
public class GenerarReporteServiceImpl implements GenerarReporteService {

	@Override
	public JSONObject generarReporte(DtoEntrada dto) {
		
		String testData = "{\"tiempoIntervaloChat\":\"00: 48: 15\",\"item\":\"Voice\",\"tiempoIntervaloEmail\":\"00: 00: 56\",\"horaIngresoCola\":\"2018-03-01 08:25:16\",\"tiempoPromedioChat\":\"05: 10: 45\",\"tiempoPromedioEmail\":\"03: 30: 12\",\"horaCierreSesion\":\"19:20:15\",\"numeroInteraccionesChat\":\"35\",\"tiempoIntervaloVoz\":\"01: 36: 12\",\"tiempoPausa\":\"00: 45: 00\",\"tiempoPromedioVoz\":\"04: 25: 12\",\"numeroInteraccionesVoz\":\"50\",\"tiempoBreak\":\"00: 43: 00\",\"tiempoProductivoAgente\":\"06: 30: 00\",\"numeroInteraccionesEmail\":\"24\",\"tiempoAlmuerzo\":\"01: 15: 13\",\"nombreAgente\":\"Alejandra Velasquez\"}";
							
		return new JSONObject(testData);
	}
}
