package com.bodytech.reporte.servicios.impl;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bodytech.reporte.dtos.BSTReporteResponse;
import com.bodytech.reporte.dtos.BTSReporteMapping;
import com.bodytech.reporte.dtos.GenericBootStrapTableRequest;
import com.bodytech.reporte.servicios.GenerarReporteService;
import com.javainuse.DtoEntrada;

@Service
public class GenerarReporteServiceImpl implements GenerarReporteService {

	private Query query;
	@PersistenceContext	private EntityManager entityManager;
	private static final Logger logger = LoggerFactory.getLogger(GenerarReporteServiceImpl.class);
	
	@Override
	public JSONObject generarReporte(DtoEntrada dto) {
		
		String testData = "{\"tiempoIntervaloChat\":\"00: 48: 15\",\"item\":\"Voice\",\"tiempoIntervaloEmail\":\"00: 00: 56\",\"horaIngresoCola\":\"2018-03-01 08:25:16\",\"tiempoPromedioChat\":\"05: 10: 45\",\"tiempoPromedioEmail\":\"03: 30: 12\",\"horaCierreSesion\":\"19:20:15\",\"numeroInteraccionesChat\":\"35\",\"tiempoIntervaloVoz\":\"01: 36: 12\",\"tiempoPausa\":\"00: 45: 00\",\"tiempoPromedioVoz\":\"04: 25: 12\",\"numeroInteraccionesVoz\":\"50\",\"tiempoBreak\":\"00: 43: 00\",\"tiempoProductivoAgente\":\"06: 30: 00\",\"numeroInteraccionesEmail\":\"24\",\"tiempoAlmuerzo\":\"01: 15: 13\",\"nombreAgente\":\"Alejandra Velasquez\"}";
							
		return new JSONObject(testData);
	}

	@Override
	public BSTReporteResponse generarReportePaginado(GenericBootStrapTableRequest request) {
		return new BSTReporteResponse(obtenerLosRegistrosDelReporte(request), obtenerElTotalDeRegistrosDelReporte(request.getSearch()));
	}
	
	
	@SuppressWarnings("unchecked")
	private List<BTSReporteMapping> obtenerLosRegistrosDelReporte(GenericBootStrapTableRequest request) {
		query = configurarElSQL(false, request.getOrder(), request.getSort(), request.getSearch());
		query.setFirstResult(getPageRequest(request.getLimit(), request.getOffset()).getOffset());
		query.setMaxResults(getPageRequest(request.getLimit(), request.getOffset()).getPageSize());
		return  query.getResultList();
	}
	
	
	private Query configurarElSQL(boolean conteo, String order,String sort, String idSearcheable) {
		String sentenciaSQL = "";
		if (conteo) {
			sentenciaSQL += "SELECT COUNT(*) ";
		} else {
			sentenciaSQL += 
			"SELECT " + 
				"id as id," +
				"nombre_agente as item," +
				"nombre_agente as nombreAgente," +
				"nombre_agente as horaIngresoCola," +
				"nombre_agente as numeroInteraccionesVoz," +
				"nombre_agente as numeroInteraccionesChat," +
				"nombre_agente as numeroInteraccionesEmail," +
				"id_conversacion as tiempoIntervaloVoz," +
				"id_conversacion as tiempoIntervaloChat," +
				"id_conversacion as tiempoIntervaloEmail," +
				"id_conversacion as tiempoPausa," +
				"id_conversacion as tiempoAlmuerzo," +
				"id_conversacion as tiempoBreak," +
				"nombre_agente as tiempoPromedioVoz," +
				"nombre_agente as tiempoPromedioChat," +
				"nombre_agente as tiempoPromedioEmail," +
				"id_conversacion as horaCierreSesion," +
				"id_conversacion as tiempoProductivoAgente " ;
		}
		sentenciaSQL += " FROM CONVERSACION " ;
		//--
		if (Objects.nonNull(idSearcheable) && !idSearcheable.isEmpty()) {
			sentenciaSQL += " WHERE ( upper(nombre_agente) like upper('%"+idSearcheable+"%') )  ";
		}
		
		//--
		if (Objects.nonNull(sort)  && !sort.isEmpty()) {
			sentenciaSQL += " ORDER BY " + configurarElOrdenamientoDelSQL(sort);
			if (Objects.nonNull(order)  && !sort.isEmpty() && ("desc".equalsIgnoreCase(order) || "asc".equalsIgnoreCase(order)) ) {
				sentenciaSQL += " " + order;
			}
		} else {
			sentenciaSQL += " ORDER BY ID desc ";
		}
		ejecucionDelSQL(conteo, sentenciaSQL);
		return query;
	}
	
	private String configurarElOrdenamientoDelSQL(String sort) {
		if("fecha".equals(sort)){
			return "fecha_inicio_conversacion";
		}else if("nombre".equals(sort)){
			return "nombre_agente";
		}
		return "id";
	}
	
	private void ejecucionDelSQL(boolean conteo, String sentenciaSQL) {
		try {
			if (conteo) {
				query = entityManager.createNativeQuery(sentenciaSQL);
			} else {
				query = entityManager.createNativeQuery(sentenciaSQL, "BTSReporteMapping");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param limitRequest
	 * @param offSetRequest
	 * @return
	 */
	private Pageable getPageRequest(Integer limitRequest, Integer offSetRequest) {
		Integer limit = (Objects.nonNull(limitRequest) && limitRequest != 0) ? limitRequest : 10;
		Integer offset = Objects.nonNull(offSetRequest) ? offSetRequest : 1;
		Integer page = offset / limit;
		return new PageRequest(page, limit);		
	}
	
	private Integer obtenerElTotalDeRegistrosDelReporte(String idSearcheable) {
		query = configurarElSQL(true, null, null, idSearcheable);
		return ((Number)query.getSingleResult()).intValue();
	}
	
	
	
}
