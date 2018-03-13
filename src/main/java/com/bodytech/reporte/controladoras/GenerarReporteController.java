package com.bodytech.reporte.controladoras;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.jxls.template.SimpleExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bodytech.reporte.dtos.BSTReporteResponse;
import com.bodytech.reporte.dtos.GenericBootStrapTableRequest;
import com.bodytech.reporte.entidades.Conversacion;
import com.bodytech.reporte.repositorios.ConversacionRepository;
import com.bodytech.reporte.servicios.impl.GenerarReporteServiceImpl;
import com.javainuse.DtoEntrada;

@Controller
public class GenerarReporteController {
	
	@Autowired private GenerarReporteServiceImpl servicio;
	private static final String APPLICATION_JSON = "application/json";
	@Autowired private ConversacionRepository conversacionRepository;
	
	
	@RequestMapping(
			value = "/generarReporte", 
			method = RequestMethod.POST, 
			consumes ="application/json")
	@CrossOrigin(origins = "*")
	public JSONObject generarReporte(@RequestBody(required = true) DtoEntrada dto) {		
		return servicio.generarReporte(dto);
	}
	
	
	@CrossOrigin
	@RequestMapping(value = "/generarReportePaginado", 
					method = RequestMethod.POST, 
					consumes = APPLICATION_JSON, 
					produces = APPLICATION_JSON)
	@ResponseBody
	public BSTReporteResponse generarReportePaginado(@RequestBody GenericBootStrapTableRequest request) {
		BSTReporteResponse respuesta = new BSTReporteResponse(null,0);
		if( Objects.isNull(request)){
			return respuesta;
		}
		return servicio.generarReportePaginado(request);
	}
	
	@RequestMapping(value = "/exportXLS", method = RequestMethod.GET)
	public void exportXLS(HttpServletResponse response) {
		List<Conversacion> persons = (List<Conversacion>) conversacionRepository.findAll();
		List<String> headers = Arrays.asList("ID de la Conversa", "Nombre del Agente");
		try {
			response.addHeader("Content-disposition", "attachment; filename=reporteExcel.xls");
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			
			new SimpleExporter().gridExport(headers, persons, "idConversacion, nombreAgente, ", response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {

		}
	}
	
	
}
