package com.bodytech.reporte.servicios;

import java.util.List;

import org.json.JSONObject;

import com.bodytech.reporte.dtos.BSTReporteResponse;
import com.bodytech.reporte.dtos.BTSReporteMapping;
import com.bodytech.reporte.dtos.GenericBootStrapTableRequest;
import com.bodytech.reporte.dtos.ListaValores;
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

	/**
	 * generar Reporte  Sin  Paginado
	 * @param request
	 * @return
	 */
	List<BTSReporteMapping> generarReporteSinPaginado(GenericBootStrapTableRequest request);

	/**
	 * obtener Usuarios Por Tipo De Servicio
	 * @param clave
	 * @return
	 */
	List<ListaValores> obtenerUsuariosPorTipoDeServicio(String clave);
	
	
	
}
