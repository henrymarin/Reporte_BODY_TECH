package com.bodytech.reporte.controladoras;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bodytech.reporte.servicios.impl.GenerarReporteServiceImpl;
import com.javainuse.DtoEntrada;

@Controller
public class GenerarReporteController {
	
	@Autowired private GenerarReporteServiceImpl servicio;
	
	@RequestMapping(value = "/generarReporte", 
			method = RequestMethod.POST, 
			consumes ="application/json")
	@CrossOrigin(origins = "*")
	public JSONObject generarReporte(@RequestBody(required = true) DtoEntrada dto) {		
		return servicio.generarReporte(dto);
	}	
}