package com.bodytech.reporte.servicios;

import org.json.JSONObject;

import com.bodytech.reporte.dtos.BSTReporteResponse;
import com.bodytech.reporte.dtos.GenericBootStrapTableRequest;
import com.javainuse.DtoEntrada;

/**
 * 
 * @author IG
 *
 */
public interface GenerarReporteService {
	
	/**
	 * generar Reporte
	 * @param dto
	 * @return
	 */
	JSONObject generarReporte(DtoEntrada dto);

	/**
	 * generar Reporte Paginado
	 * @param request
	 * @return
	 */
	BSTReporteResponse generarReportePaginado(GenericBootStrapTableRequest request);
}
