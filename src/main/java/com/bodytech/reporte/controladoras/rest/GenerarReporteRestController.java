package com.bodytech.reporte.controladoras.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jxls.template.SimpleExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bodytech.reporte.dtos.ConsultarTiposDeServicioResponse;
import com.bodytech.reporte.dtos.ListaValores;
import com.bodytech.reporte.entidades.User;
import com.bodytech.reporte.repositorios.UserRepository;
import com.bodytech.reporte.servicios.impl.GenerarReporteServiceImpl;
import com.javainuse.DtoEntrada;

import net.minidev.json.JSONObject;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperRunManager;

@RestController()
public class GenerarReporteRestController {
	
	@Autowired private GenerarReporteServiceImpl servicio;
	@Autowired private UserRepository userRepository;
	private static final String APPLICATION_JSON = "application/json";
	
	
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
		List<User> persons = (List<User>) userRepository.findAll();
		List<String> headers = Arrays.asList("First Name", "Last Name");		        
		try {
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.flush();
			//--
			response.addHeader("Content-disposition", "attachment; filename=People.xls");
			response.setContentType("application/vnd.ms-excel");
			new SimpleExporter().gridExport(headers, persons, "idAgente, idConversacion, ", servletOutputStream);
			//--			
	        servletOutputStream.flush();
	        servletOutputStream.close();	
			response.flushBuffer();
		} catch (IOException e) {

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
			
		} 
	}
}
