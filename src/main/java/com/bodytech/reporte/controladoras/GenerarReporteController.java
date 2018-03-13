package com.bodytech.reporte.controladoras;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.jxls.template.SimpleExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bodytech.reporte.dtos.BSTReporteResponse;
import com.bodytech.reporte.dtos.BTSReporteMapping;
import com.bodytech.reporte.dtos.GenericBootStrapTableRequest;
import com.bodytech.reporte.servicios.impl.GenerarReporteServiceImpl;
import com.javainuse.DtoEntrada;

@Controller
public class GenerarReporteController {
	
	@Autowired private GenerarReporteServiceImpl servicio;
	private static final String APPLICATION_JSON = "application/json";
	private static final Logger logger = LoggerFactory.getLogger(GenerarReporteController.class);
	
	
	@RequestMapping(
			value = "/generarReporte", 
			method = RequestMethod.POST, 
			consumes ="application/json")
	@CrossOrigin(origins = "*")
	public JSONObject generarReporte(@RequestBody(required = true) DtoEntrada dto) {		
		return servicio.generarReporte(dto);
	}
	
	
	@CrossOrigin
	@RequestMapping(
			value = "/generarReportePaginado", 
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
	
	/**
	 * 
	 * @param fechaInicial: yyyy-MM-dd
	 * @param fechaFinal: yyyy-MM-dd
	 * @param response
	 */
	@RequestMapping(
			value = "/generarReporteExcelGet/{fechaInicial}/{fechaFinal}", 
			method = RequestMethod.GET)
	public void generarReporteExcelGet(
				@PathVariable("fechaInicial") String fechaInicial, 
				@PathVariable("fechaFinal") String fechaFinal, 
				HttpServletResponse response) {
		
		GenericBootStrapTableRequest request =  new GenericBootStrapTableRequest();
		request.setFechaInicial(fechaInicial);
		request.setFechaFinal(fechaFinal);
		List<BTSReporteMapping> lista = servicio.generarReporteSinPaginado(request);		
		//--
		List<String> headers = Arrays.asList("ID de la Conversa", "Nombre del Agente");
		try {
			response.addHeader("Content-disposition", "attachment; filename=reporteExcel.xls");
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			//--
			new SimpleExporter().gridExport(headers, lista, "nombreAgente, horaIngresoCola, ", response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("erro exportXLS.", e);
		}
	}
	
	@RequestMapping(
			value = "/generarReporteExcelGetDateTimeFormat", 
			method = RequestMethod.GET)
	public void generarReporteExcelGetDateTimeFormat(
				@RequestParam("fechaInicial") @DateTimeFormat(pattern="yyyy-MM-dd") Date fechaInicial, 
				@RequestParam("fechaFinal") @DateTimeFormat(pattern="yyyy-MM-dd") Date fechaFinal,
				HttpServletResponse response) {
		
		GenericBootStrapTableRequest request =  new GenericBootStrapTableRequest();
		request.setFechaInicial(fechaInicial.toString());
		request.setFechaFinal(fechaFinal.toString());
		List<BTSReporteMapping> lista = servicio.generarReporteSinPaginado(request);		
		//--
		List<String> headers = Arrays.asList("ID de la Conversa", "Nombre del Agente");
		try {
			response.addHeader("Content-disposition", "attachment; filename=reporteExcel.xls");
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			//--
			new SimpleExporter().gridExport(headers, lista, "nombreAgente, horaIngresoCola, ", response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("erro exportXLS.", e);
		}
	}
	
	@CrossOrigin
	@RequestMapping(
			value = "/generarReporteExcel", 
			method = RequestMethod.POST, 
			consumes = APPLICATION_JSON, 
			produces = APPLICATION_JSON)
	public void generarReporteExcel(@RequestBody GenericBootStrapTableRequest request, HttpServletResponse response) {
		List<BTSReporteMapping> lista = servicio.generarReporteSinPaginado(request);		
		//--
		List<String> headers = Arrays.asList("ID de la Conversa", "Nombre del Agente");
		try {
			response.addHeader("Content-disposition", "attachment; filename=reporteExcel.xls");
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			//--
			new SimpleExporter().gridExport(headers, lista, "nombreAgente, horaIngresoCola, ", response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("erro exportXLS.", e);
		}
	}
	
	
}
