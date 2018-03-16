package com.bodytech.reporte.servicios.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bodytech.reporte.dtos.BSTReporteResponse;
import com.bodytech.reporte.dtos.BTSReporteMapping;
import com.bodytech.reporte.dtos.GenericBootStrapTableRequest;
import com.bodytech.reporte.dtos.ListaValores;
import com.bodytech.reporte.entidades.Agente;
import com.bodytech.reporte.repositorios.AgenteRepository;
import com.bodytech.reporte.servicios.GenerarReporteService;
import com.javainuse.DtoEntrada;

@Service
public class GenerarReporteServiceImpl implements GenerarReporteService {

	private Query query;
	@PersistenceContext	private EntityManager entityManager;
	private static final Logger logger = LoggerFactory.getLogger(GenerarReporteServiceImpl.class);
	@Autowired private AgenteRepository agenteRepository;
	
	@Override
	public JSONObject generarReporte(DtoEntrada dto) {
		
		String testData = "{\"tiempoIntervaloChat\":\"00: 48: 15\",\"item\":\"Voice\",\"tiempoIntervaloEmail\":\"00: 00: 56\",\"horaIngresoCola\":\"2018-03-01 08:25:16\",\"tiempoPromedioChat\":\"05: 10: 45\",\"tiempoPromedioEmail\":\"03: 30: 12\",\"horaCierreSesion\":\"19:20:15\",\"numeroInteraccionesChat\":\"35\",\"tiempoIntervaloVoz\":\"01: 36: 12\",\"tiempoPausa\":\"00: 45: 00\",\"tiempoPromedioVoz\":\"04: 25: 12\",\"numeroInteraccionesVoz\":\"50\",\"tiempoBreak\":\"00: 43: 00\",\"tiempoProductivoAgente\":\"06: 30: 00\",\"numeroInteraccionesEmail\":\"24\",\"tiempoAlmuerzo\":\"01: 15: 13\",\"nombreAgente\":\"Alejandra Velasquez\"}";
							
		return new JSONObject(testData);
	}

	@Override
	public BSTReporteResponse generarReportePaginado(GenericBootStrapTableRequest request) {
		return new BSTReporteResponse(obtenerLosRegistrosDelReporte(request), obtenerElTotalDeRegistrosDelReporte(request.getSearch(), request.getFechaInicial(), request.getFechaFinal()));
	}
	
	
	@SuppressWarnings("unchecked")
	private List<BTSReporteMapping> obtenerLosRegistrosDelReporte(GenericBootStrapTableRequest request) {
		query = configurarElSQL(false, request.getOrder(), request.getSort(), request.getSearch(), request.getFechaInicial(), request.getFechaFinal());
		query.setFirstResult(getPageRequest(request.getLimit(), request.getOffset()).getOffset());
		query.setMaxResults(getPageRequest(request.getLimit(), request.getOffset()).getPageSize());
				
		return realizarCalculosReporte(query.getResultList());
	}
	
	
	private List<BTSReporteMapping> realizarCalculosReporte(List<BTSReporteMapping> resultList) {
		
		if (Objects.nonNull(resultList) && !resultList.isEmpty()) {
			for (BTSReporteMapping btsReporteMapping : resultList) {
				
				// Calcular tiempo promedio voz
				if (!Objects.isNull(btsReporteMapping.getTiempoIntervaloVoz())) {
					double tiempoIntervaloVoz = Double.parseDouble(btsReporteMapping.getTiempoIntervaloVoz());
					int numeroInteraccionesVoz = Integer.parseInt(btsReporteMapping.getNumeroInteraccionesVoz());
					
					btsReporteMapping.setTiempoPromedioVoz(String.valueOf(tiempoIntervaloVoz / numeroInteraccionesVoz));
				}				
				
				// Calcular tiempo promedio chat
				
				// Calcular tiempo promedio mail
				
				// Calcular porcentaje productividad agente
			}
		}
		
		return resultList;
	}

	private Query configurarElSQL(boolean conteo, String order,String sort, String idSearcheable, String fechaInicial, String fechaFinal) {
		String sentenciaSQL = "";
		
		String sentenciaSQLReporte = 
			"SELECT " + 
				"DATE(c.fecha_inicio_conversacion) AS fecha," +
				"c.nombre_agente AS nombreAgente," +
				"(SELECT TIME_FORMAT(fecha_inicio_estado, '%H : %m : %s') FROM estados_por_agente WHERE UPPER(estado) = 'ON_QUEUE' AND DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente ORDER BY fecha_inicio_estado LIMIT 1) AS horaIngresoCola," +
				"(SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE DATE(ct.fecha_inicio_segmento) = FECHA AND ct.id_agente = c.id_agente AND UPPER(ct.tipo) = 'VOICE') AS numeroInteraccionesVoz," +
				"(SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE DATE(ct.fecha_inicio_segmento) = FECHA AND ct.id_agente = c.id_agente AND UPPER(ct.tipo) = 'CHAT') AS numeroInteraccionesChat," +
				"(SELECT COUNT(*) FROM conversaciones_por_tipo ct WHERE DATE(ct.fecha_inicio_segmento) = FECHA AND ct.id_agente = c.id_agente AND UPPER(ct.tipo) = 'MAIL') AS numeroInteraccionesEmail," +
				"(SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(tipo) = 'VOICE') AS tiempoIntervaloVoz," +
				"(SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(tipo) = 'CHAT') AS tiempoIntervaloChat," +
				"(SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(tipo) = 'MAIL') AS tiempoIntervaloEmail," +
				"(SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(segmento) = 'HOLD') AS tiempoPausa," +
				"(SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) FROM estados_por_agente WHERE DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente AND UPPER(estado) = 'MEAL') AS tiempoAlmuerzo," +
				"(SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)) FROM estados_por_agente WHERE DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente AND UPPER(estado) = 'BREAK') AS tiempoBreak," +
				"null AS tiempoPromedioVoz," +
				"null AS tiempoPromedioChat," +
				"null AS tiempoPromedioEmail," +
				"(SELECT TIME_FORMAT(fecha_inicio_estado, '%H : %m : %s') FROM estados_por_agente WHERE DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente AND UPPER(estado) = 'OFFLINE' ORDER BY fecha_inicio_estado desc LIMIT 1) AS horaCierreSesion," +
				"(SELECT (SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento)) FROM conversaciones_por_tipo WHERE DATE(fecha_inicio_segmento) = FECHA AND id_agente = c.id_agente AND UPPER(tipo) IN ('VOICE', 'CHAT', 'MAIL')) + (SELECT IFNULL(SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)),0) FROM estados_por_agente WHERE DATE(fecha_inicio_estado) = FECHA AND id_agente = c.id_agente AND UPPER(estado) = 'IDLE')) AS tiempoProductivoAgente," +
				"null AS porcentajeProductividadAgente ";
		
		if (conteo) {
			sentenciaSQL += "SELECT COUNT(*) FROM ( " + sentenciaSQLReporte;
		} else {
			sentenciaSQL += sentenciaSQLReporte;			
		}
		
		sentenciaSQL += " FROM conversacion c " ;
		
		sentenciaSQL += " WHERE (DATE(c.fecha_inicio_conversacion) BETWEEN '" + fechaInicial + "' AND '" + fechaFinal + "') ";
		
		if (Objects.nonNull(idSearcheable) && !idSearcheable.isEmpty()) {
			sentenciaSQL += " AND ( upper(nombre_agente) like upper('%"+idSearcheable+"%') )  ";
		}
		
		sentenciaSQL += " AND id_agente IN ('c4a291b1-153d-4150-a4e3-8dad40c48f42','8ab09adb-56ec-43ef-ba9f-811d9cc5b6d6','8c977b15-994a-4895-9671-53d4e455ecb7','b2caca88-2b32-49ab-98f6-90cdfbb048ab','a29851e1-a466-45e8-a7c1-dba718a23bcf','304cc51b-f91d-4b5e-8188-2ec1b6e2479f','4d0c5f3d-8618-4c00-a87f-4de882fd2f75','f3dfd192-557f-49f8-b51e-bd6e967e08db','8ffe0bb5-e0e1-4ffa-9de2-9d9ce196da39','ab15299f-1a0c-4b49-a1d0-76fa00ec2574','41f73f05-906a-4871-a389-0708395949fb','13d7aee4-7390-4ade-8e74-d1daaed46cd4','9d29df2a-86ae-4826-b0af-f073c74bdf40','670c5ee4-7faf-49e6-ac83-9fa46cc516fc','5c580489-6447-4b53-bb3f-76edd6dae6bc','e6d343d8-0973-4bb8-bb5c-f10f45ddd8ae','8a60b964-d599-4cd7-a561-69eeb98881a7','f5579fd1-1873-4943-888a-d5ab1567a2d8','98d344f9-79f8-47b5-9532-fb01a97a4921','cf18bd98-e6ca-41d5-a70c-0600cdb19ad7','345c6e29-28c0-4960-8cb1-6c4501918b14','aa5a7a2a-5d01-4308-a1a7-122ec2079dd7','9eb6bea1-64ce-4d9d-8af1-27b34f5aaba5','0d5fc836-390d-4976-b06e-89b8d2f90191','88d35683-591c-4245-9e1c-d2dbe7d1808f','9cc35a58-7c1e-4905-a7b4-0509a8e85c3d','97055e56-4378-4e6d-86df-78d4f8241e67','58b88f85-b281-4f74-b6e1-b0a3d4f581f2','42aa51bf-a18d-4595-894a-8628c345ea41','1f5d559f-2316-4d2a-9685-7c07e4520821','777a6fc7-a97e-4f6a-ab38-bb44d5fff6fc','c889ba2e-3f06-4bdb-92b0-f4816da953a5','93cf48e8-de8f-4961-b678-5342e7a050b0','27458d36-6fd1-4195-b3f4-0e0b399eebdf','f5902c4d-5ab2-4b83-af8f-a4c7769dfe58','c417dd32-58fd-4c5f-a5ed-86d311d66000','217e3050-2d37-468d-b313-6e296f113baa','4e3c08cc-68f9-41ed-93bc-cc55ab2c2369','5a742768-af40-4637-86e8-04d54fd4decd','e92b1031-b9a8-436b-9a97-71d861f68e56')";		
		sentenciaSQL += " GROUP BY fecha, nombreAgente ";				
		sentenciaSQL += " ORDER BY fecha ";		
		
		if (conteo) {
			sentenciaSQL += " ) AS conteo";
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
			logger.error("erro ejecucionDelSQL.", e);
		}
	}
	
	private Pageable getPageRequest(Integer limitRequest, Integer offSetRequest) {
		Integer limit = (Objects.nonNull(limitRequest) && limitRequest != 0) ? limitRequest : 10;
		Integer offset = Objects.nonNull(offSetRequest) ? offSetRequest : 1;
		Integer page = offset / limit;
		return new PageRequest(page, limit);		
	}
	
	private Integer obtenerElTotalDeRegistrosDelReporte(String idSearcheable, String fechaInicial, String fechaFinal) {
		query = configurarElSQL(true, null, null, idSearcheable, fechaInicial, fechaFinal);
		return ((Number)query.getSingleResult()).intValue();
	}
	
	@Override
	public List<BTSReporteMapping> generarReporteSinPaginado(GenericBootStrapTableRequest request) {
		return obtenerLosRegistrosDelReporteSinPaginacion(request);
	}
	
	@SuppressWarnings("unchecked")
	private List<BTSReporteMapping> obtenerLosRegistrosDelReporteSinPaginacion(GenericBootStrapTableRequest request) {
		query = configurarElSQL(false, request.getOrder(), request.getSort(), request.getSearch(), request.getFechaInicial(), request.getFechaFinal());		
		return  query.getResultList();
	}

	@Override
	public List<ListaValores> obtenerUsuariosPorTipoDeServicio(String clave) {
		List<ListaValores> listaRetorno = new ArrayList<>();
		Iterable<Agente> listadoDeAgentes = agenteRepository.findAll();
		for (Agente agente : listadoDeAgentes) {
			ListaValores valor = new ListaValores(); 
			if(agente.getTipoAgente().equalsIgnoreCase(clave)){
				valor.setCodigo(agente.getIdAgente());
				valor.setNombre(agente.getNombreAgente());
				listaRetorno.add(valor);				
			}
		}
		return listaRetorno;
	}
	
	
	
	
	
}
