package com.bodytech.reporte.controladoras.rest;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bodytech.reporte.dtos.BTSReporteMapping;
import com.bodytech.reporte.dtos.ConsultarTiposDeServicioResponse;
import com.bodytech.reporte.dtos.GenericBootStrapTableRequest;
import com.bodytech.reporte.dtos.ListaValores;
import com.bodytech.reporte.servicios.impl.GenerarReporteServiceImpl;
import com.javainuse.DtoEntrada;

import net.minidev.json.JSONObject;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperRunManager;

@RestController()
public class GenerarReporteRestController {
	
	@Autowired private GenerarReporteServiceImpl servicio;
	private static final String APPLICATION_JSON = "application/json";
	private static final Logger logger = LoggerFactory.getLogger(GenerarReporteRestController.class);
	
	
	@CrossOrigin
    @RequestMapping(
    		value="/obtenerUsuariosPorTipoDeServicio/{clave}",
    		method = RequestMethod.GET, 
    		produces = APPLICATION_JSON)
    public ConsultarTiposDeServicioResponse obtenerUsuariosPorTipoDeServicio(@PathVariable("clave") String clave) {
    	List<ListaValores> listaParametrizadaConsultada = servicio.obtenerUsuariosPorTipoDeServicio(clave);    	
    	//--
    	ConsultarTiposDeServicioResponse listaRespuesta = new ConsultarTiposDeServicioResponse();
    	listaRespuesta.setListaValores(listaParametrizadaConsultada);
    	return listaRespuesta;
    }
	
	
	@RequestMapping(
			value = "/crearXLS", 
			method = RequestMethod.POST, 
			consumes ="application/json")
	@CrossOrigin(origins = "*")
	public JSONObject crearXLS(@RequestBody(required = true) DtoEntrada dto,HttpServletResponse response) {
		JSONObject jsonResponse = new JSONObject();
		
		if(Objects.nonNull(dto) && Objects.nonNull(dto.getFechaUno()) && Objects.nonNull(dto.getFechaDos()) && Objects.nonNull(dto.getLista()) ){
			GenericBootStrapTableRequest request =  new GenericBootStrapTableRequest();
			request.setFechaInicial(dto.getFechaUno());
			request.setFechaFinal(dto.getFechaDos());
			request.setLista(dto.getLista());
			List<BTSReporteMapping> lista = servicio.generarReporteSinPaginado(request);
			//--
			List<String> headers = Arrays.asList(
					"Item", "Nombre del Agente", "Hora de ingreso a la Cola", "Número de Interacciones por Voz", "Número de Interacciones por Chat", 
					"Número de Interacciones por Email", "Tiempo de Intervalo por Voz", "Tiempo de Intervalo por Chat", "Tiempo de Intervalo por Email", 
					"Tiempo de Pausa", "Tiempo de Almuerzo", "Tiempo de Break", "Tiempo Promedio por Voz", "Tiempo Promedio por Chat",
					"Tiempo Promedio por Email", "Hora de Cierre de Sesion", "Tiempo de Productivo del Agente");
			
			try {
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.flush();
				//--
				response.addHeader("Content-disposition", "attachment; filename=People.xls");
				response.setContentType("application/vnd.ms-excel");
				new SimpleExporter().gridExport(
						headers, lista, 
						"item, nombreAgente, horaIngresoCola, numeroInteraccionesVoz, numeroInteraccionesChat,"+ 
								"numeroInteraccionesEmail, tiempoIntervaloVoz, tiempoIntervaloChat, tiempoIntervaloEmail,"+ 
								"tiempoPausa, tiempoAlmuerzo, tiempoBreak, tiempoPromedioVoz, tiempoPromedioChat,"+
								"tiempoPromedioEmail, horaCierreSesion, tiempoProductivoAgente,",
						response.getOutputStream());
				//--			
		        servletOutputStream.flush();
		        servletOutputStream.close();	
				response.flushBuffer();
			} catch (IOException e) {
				logger.error("erro exporting....", e);
			}	
		}
		
		return jsonResponse;
	}
	
	
	@RequestMapping(
			value = "/crearPDF", 
			method = RequestMethod.POST, 
			consumes ="application/json",
			produces ="application/pdf")
	@CrossOrigin(origins = "*")
	public void crearPDF(@RequestBody(required = true) DtoEntrada dto,HttpServletResponse response) {

		if(Objects.nonNull(dto) && Objects.nonNull(dto.getFechaUno()) && Objects.nonNull(dto.getFechaDos()) && Objects.nonNull(dto.getLista()) ){
			GenericBootStrapTableRequest request =  new GenericBootStrapTableRequest();
			request.setFechaInicial(dto.getFechaUno());
			request.setFechaFinal(dto.getFechaDos());
			request.setLista(dto.getLista());
			
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
				logger.error("erro exporting....", e);
			} 
		}
	}
}
