package com.bodytech.reporte.controladoras;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bodytech.reporte.dtos.PrecargaResponse;
import com.bodytech.reporte.servicios.impl.GuardarDatosServiceImpl;
import com.javainuse.DtoEntrada;

@Controller
public class GuardarDatosController {
	
	@Autowired private GuardarDatosServiceImpl servicio;
		
			
	@RequestMapping(
			value = "/guardarDatos", 
			method = RequestMethod.POST, 
			consumes ="application/json")
	@CrossOrigin(origins = "*")
	public JSONObject guardarDatos(@RequestBody(required = true) DtoEntrada dto) {		
		return servicio.guardarDatosDepuradoConCargaDeAgentes(dto);
	}	
	
	@RequestMapping(
			value = "/obtenerInformacionDeLaPrecarga", 
			method = RequestMethod.GET, 
			consumes ="application/json")
	@CrossOrigin(origins = "*")
	@ResponseBody
	public PrecargaResponse obtenerInformacionDeLaPrecarga() {		
		return servicio.obtenerInformacionDeLaPrecarga();
	}
	
	@RequestMapping("/welcome.html")
	public ModelAndView firstPage() {
		return new ModelAndView("welcome");
	}
	
	@RequestMapping("/welcome2.html")
	public ModelAndView firstPage2() {
		PrecargaResponse precarga = servicio.obtenerInformacionDeLaPrecarga();
		
		ModelAndView mav = new ModelAndView("welcome2");
		mav.addObject("msg1", precarga.getFechaInicio());
		mav.addObject("msg2", precarga.getFechaFin());
		mav.addObject("msg3", precarga.getEstado());
		
		return mav;
	}
	
}
