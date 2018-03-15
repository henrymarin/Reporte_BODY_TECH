package com.bodytech.reporte.controladoras.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bodytech.reporte.dtos.ConsultarTiposDeServicioResponse;
import com.bodytech.reporte.dtos.ListaValores;
import com.bodytech.reporte.servicios.impl.GenerarReporteServiceImpl;

@RestController()
public class GenerarReporteRestController {
	
	@Autowired private GenerarReporteServiceImpl servicio;
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
}
