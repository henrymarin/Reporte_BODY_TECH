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
import com.bodytech.reporte.dtos.PrecargaResponse;
import com.bodytech.reporte.servicios.impl.GenerarReporteServiceImpl;
import com.bodytech.reporte.servicios.impl.GuardarDatosServiceImpl;
import com.javainuse.DtoEntrada;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RestController()
public class GenerarReporteRestController {
	
	@Autowired private GenerarReporteServiceImpl servicio;
	@Autowired private GuardarDatosServiceImpl servicioGuardarDatos;
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
	public void crearXLS(@RequestBody(required = true) DtoEntrada dto,HttpServletResponse response) {
		
		if( Objects.isNull(dto) || Objects.isNull(dto.getFechaUno()) || Objects.isNull(dto.getFechaDos()) || Objects.isNull(dto.getListadoDeAgentesStr()) || dto.getListadoDeAgentesStr().isEmpty()){
			logger.error("erro exporting....");
		}else{	
			GenericBootStrapTableRequest request =  new GenericBootStrapTableRequest();
			request.setFechaInicial(dto.getFechaUno());
			request.setFechaFinal(dto.getFechaDos());
			request.setLista(dto.getLista());
			request.setListadoDeAgentesStr(dto.getListadoDeAgentesStr());
			List<BTSReporteMapping> lista = servicio.generarReporteSinPaginado(request);
			//--
			List<String> headers = Arrays.asList(
					"Fecha","Nombre del Agente", "Hora de ingreso a la Cola", "N\u00famero de Interacciones por Voz", "N\u00famero de Interacciones por Chat", 
					"N\u00famero de Interacciones por Email", "Tiempo de Intervalo por Voz", "Tiempo de Intervalo por Chat", "Tiempo de Intervalo por Email", 
					"Tiempo de Pausa", "Tiempo de Almuerzo", "Tiempo de Break", "Tiempo Promedio por Voz", "Tiempo Promedio por Chat",
					"Tiempo Promedio por Email", "Hora de Cierre de Sesion", "Tiempo Productivo del Agente", "Porcentaje de Productividad del Agente");
			
			try {
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.flush();
				//--
				response.addHeader("Content-disposition", "attachment; filename=People.xls");
				response.setContentType("application/vnd.ms-excel");
				new SimpleExporter().gridExport(
						headers, lista, 
								"fecha, nombreAgente, horaIngresoCola, numeroInteraccionesVoz, numeroInteraccionesChat,"+ 
								"numeroInteraccionesEmail, tiempoIntervaloVoz, tiempoIntervaloChat, tiempoIntervaloEmail,"+ 
								"tiempoPausa, tiempoAlmuerzo, tiempoBreak, tiempoPromedioVoz, tiempoPromedioChat,"+
								"tiempoPromedioEmail, horaCierreSesion, tiempoProductivoAgente, porcentajeProductividadAgente",
						response.getOutputStream());
				//--			
		        servletOutputStream.flush();
		        servletOutputStream.close();	
				response.flushBuffer();
			} catch (IOException e) {
				logger.error("erro exporting....", e);
			}	
		}

	}
	
	
	@RequestMapping(
			value = "/crearPDF", 
			method = RequestMethod.POST, 
			consumes ="application/json",
			produces ="application/pdf")
	@CrossOrigin(origins = "*")
	public void crearPDF(@RequestBody(required = true) DtoEntrada dto,HttpServletResponse response) {

		if( Objects.isNull(dto) || Objects.isNull(dto.getFechaUno()) || Objects.isNull(dto.getFechaDos()) || Objects.isNull(dto.getListadoDeAgentesStr()) || dto.getListadoDeAgentesStr().isEmpty()){
			logger.error("erro exporting....");
		}else{
			GenericBootStrapTableRequest request =  new GenericBootStrapTableRequest();
			request.setFechaInicial(dto.getFechaUno());
			request.setFechaFinal(dto.getFechaDos());
			request.setLista(dto.getLista());
			request.setListadoDeAgentesStr(String.join(",", dto.getLista()));
						
			try {
				List<BTSReporteMapping> lista = servicio.generarReporteSinPaginado(request);
								
	            JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(lista);
	            
	            Map<String, Object> parameters = new HashMap<String, Object>();
	            parameters.put("ItemDataSource", itemsJRBean);
	            parameters.put("fechaInicial", request.getFechaInicial());
	            parameters.put("fechaFinal", request.getFechaFinal());
	            
	            Resource plantillaCompilada = new ClassPathResource("/reportes/jasper/reportePdf.jasper");
	            
	            ServletOutputStream servletOutputStream = response.getOutputStream();
		        servletOutputStream.flush();
	            
		        JasperRunManager.runReportToPdfStream(plantillaCompilada.getInputStream(), servletOutputStream, parameters, new JREmptyDataSource());
				
		        servletOutputStream.flush();
		        servletOutputStream.close();	
				response.flushBuffer();		        	            
			} catch (Exception e) {
				logger.error("erro exporting....", e);
			} 
		}
	}
	
	
	@RequestMapping(
			value = "/guardarDatos", 
			method = RequestMethod.POST, 
			consumes ="application/json")
	@CrossOrigin(origins = "*")
	public PrecargaResponse guardarDatos(@RequestBody(required = true) DtoEntrada dto) {

		PrecargaResponse precarga = new PrecargaResponse();
		precarga.setEstado("En Proceso");
		precarga.setFechaInicio(dto.getFechaUno());
		precarga.setFechaFin(dto.getFechaDos());
		//--
		servicioGuardarDatos.guardarDatosDepuradoConCargaDeAgentes(dto);
		
		return precarga;
	}
}
