package com.bodytech.reporte.servicios;

import java.util.List;

import com.bodytech.reporte.dtos.BSTReporteResponse;
import com.bodytech.reporte.dtos.BTSReporteMapping;
import com.bodytech.reporte.dtos.GenericBootStrapTableRequest;
import com.bodytech.reporte.dtos.ListaValores;

/**
 * 
 * @author IG
 *
 */
public interface GenerarReporteService {
	

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
