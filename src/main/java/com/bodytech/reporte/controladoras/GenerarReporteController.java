package com.bodytech.reporte.controladoras;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jxls.template.SimpleExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bodytech.reporte.dtos.BSTReporteResponse;
import com.bodytech.reporte.dtos.BTSReporteMapping;
import com.bodytech.reporte.dtos.GenericBootStrapTableRequest;
import com.bodytech.reporte.servicios.impl.GenerarReporteServiceImpl;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperRunManager;

@Controller
public class GenerarReporteController {
	
	@Autowired private GenerarReporteServiceImpl servicio;
	private static final String APPLICATION_JSON = "application/json";
	private static final Logger logger = LoggerFactory.getLogger(GenerarReporteController.class);
	
	
	
	@CrossOrigin
	@RequestMapping(
			value = "/generarReportePaginado", 
			method = RequestMethod.POST, 
			consumes = APPLICATION_JSON, 
			produces = APPLICATION_JSON)
	@ResponseBody
	public BSTReporteResponse generarReportePaginado(@RequestBody GenericBootStrapTableRequest request) {
		BSTReporteResponse respuesta = new BSTReporteResponse(null,0);
		if( Objects.isNull(request) || Objects.isNull(request.getFechaInicial()) || Objects.isNull(request.getFechaFinal()) || Objects.isNull(request.getListadoDeAgentesStr()) || request.getListadoDeAgentesStr().isEmpty()){
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
		List<String> headers = Arrays.asList(
				"Item", "Nombre del Agente", "Hora de ingreso a la Cola", "N\u00famero de Interacciones por Voz", "N\u00famero de Interacciones por Chat", 
				"N\u00famero de Interacciones por Email", "Tiempo de Intervalo por Voz", "Tiempo de Intervalo por Chat", "Tiempo de Intervalo por Email", 
				"Tiempo de Pausa", "Tiempo de Almuerzo", "Tiempo de Break", "Tiempo Promedio por Voz", "Tiempo Promedio por Chat",
				"Tiempo Promedio por Email", "Hora de Cierre de Sesión", "Tiempo de Productivo del Agente");
		try {
			response.addHeader("Content-disposition", "attachment; filename=reporteExcel.xls");
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			//--
			new SimpleExporter().gridExport(
					headers, lista, 
					"item, nombreAgente, horaIngresoCola, numeroInteraccionesVoz, numeroInteraccionesChat,"+ 
							"numeroInteraccionesEmail, tiempoIntervaloVoz, tiempoIntervaloChat, tiempoIntervaloEmail,"+ 
							"tiempoPausa, tiempoAlmuerzo, tiempoBreak, tiempoPromedioVoz, tiempoPromedioChat,"+
							"tiempoPromedioEmail, horaCierreSesion, tiempoProductivoAgente,",
					response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("erro exportXLS.", e);
		}
	}
	

	/**
	 * @param fechaInicial: yyyy-MM-dd
	 * @param fechaFinal: yyyy-MM-dd
	 * @param response
	 */
	@RequestMapping(
			value = "/generarReportePDFGet/{fechaInicial}/{fechaFinal}", 
			method = RequestMethod.GET)
	@CrossOrigin(origins = "*")
	public void generarReportePDFGet(
			@PathVariable("fechaInicial") String fechaInicial, 
			@PathVariable("fechaFinal") String fechaFinal, 
			HttpServletResponse response) {
		
		//--
		try {
			ServletOutputStream servletOutputStream = response.getOutputStream();
	        servletOutputStream.flush();
	        //--
			Resource plantillaCompilada = new ClassPathResource("/reportes/jasper/reporte000001.jasper");
			//--
			Map<String, Object> parametrosZ = new HashMap<>();		
			parametrosZ.put("DEVELOPER", "Valor a remplazar en el PARAM: DEVELOPER");
			//--
			JasperRunManager.runReportToPdfStream(plantillaCompilada.getInputStream(), servletOutputStream, parametrosZ, new JREmptyDataSource());
			//--
	        servletOutputStream.flush();
	        servletOutputStream.close();	
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("erro exportPDF.", e);
		} 
	}	
	
}
