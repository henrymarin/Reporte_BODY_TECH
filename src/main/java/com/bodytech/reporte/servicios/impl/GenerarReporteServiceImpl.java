package com.bodytech.reporte.servicios.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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

	private static DecimalFormat decimalFormat = new DecimalFormat(".#");
	
	@Override
	public JSONObject generarReporte(DtoEntrada dto) {
		
		String testData = "{\"tiempoIntervaloChat\":\"00: 48: 15\",\"item\":\"Voice\",\"tiempoIntervaloEmail\":\"00: 00: 56\",\"horaIngresoCola\":\"2018-03-01 08:25:16\",\"tiempoPromedioChat\":\"05: 10: 45\",\"tiempoPromedioEmail\":\"03: 30: 12\",\"horaCierreSesion\":\"19:20:15\",\"numeroInteraccionesChat\":\"35\",\"tiempoIntervaloVoz\":\"01: 36: 12\",\"tiempoPausa\":\"00: 45: 00\",\"tiempoPromedioVoz\":\"04: 25: 12\",\"numeroInteraccionesVoz\":\"50\",\"tiempoBreak\":\"00: 43: 00\",\"tiempoProductivoAgente\":\"06: 30: 00\",\"numeroInteraccionesEmail\":\"24\",\"tiempoAlmuerzo\":\"01: 15: 13\",\"nombreAgente\":\"Alejandra Velasquez\"}";
							
		return new JSONObject(testData);
	}

	@Override
	public BSTReporteResponse generarReportePaginado(GenericBootStrapTableRequest request) {
		return new BSTReporteResponse(obtenerLosRegistrosDelReporte(request), obtenerElTotalDeRegistrosDelReporte(request));
	}	
	
	@SuppressWarnings("unchecked")
	private List<BTSReporteMapping> obtenerLosRegistrosDelReporte(GenericBootStrapTableRequest request) {
		query = configurarElSQL(false, request);
		query.setFirstResult(getPageRequest(request.getLimit(), request.getOffset()).getOffset());
		query.setMaxResults(getPageRequest(request.getLimit(), request.getOffset()).getPageSize());
				
		return realizarCalculosReporte(query.getResultList());
	}

	private Query configurarElSQL(boolean conteo, GenericBootStrapTableRequest request) {
		
		String sentenciaSQL;		
		String sentenciaSQLReporte =
		"SELECT "+ 
            "    idAgente, "+
            "    sum(case when UPPER(tipo) = 'VOICE' then 1 else 0 end) as numeroInteraccionesVoz, "+
            "    sum(case when UPPER(tipo) = 'CHAT' then 1 else 0 end) as numeroInteraccionesChat, "+
            "    sum(case when UPPER(tipo) = 'EMAIL' then 1 else 0 end) as numeroInteraccionesEmail, "+
            "    SUM(CASE WHEN UPPER(tipo) = 'VOICE' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento) ELSE 0 END) AS tiempoIntervaloVoz, "+
            "    SUM(CASE WHEN UPPER(tipo) = 'CHAT' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento) ELSE 0 END) AS tiempoIntervaloChat, "+
            "    SUM(CASE WHEN UPPER(tipo) = 'EMAIL' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento) ELSE 0 END) AS tiempoIntervaloEmail, "+
            "    SUM(CASE WHEN UPPER(segmento) = 'HOLD' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento) ELSE 0 END) AS tiempoPausa, "+
            "   (SUM(CASE WHEN UPPER(tipo) = 'VOICE' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento) ELSE 0 END) + "+
            "    SUM(CASE WHEN UPPER(tipo) = 'CHAT' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento) ELSE 0 END) + "+
            "    SUM(CASE WHEN UPPER(tipo) = 'EMAIL' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_segmento, fecha_fin_segmento) ELSE 0 END)  ) as tiempoProductivoAgenteUno, "+
            "    fecha," +
            "	 nombreAgente, " +
            "    segmento," +
            "    null AS tiempoPromedioVoz," +
            "    null AS tiempoPromedioChat, " +
            "    null AS tiempoPromedioEmail, "+
            "    null AS horaIngresoCola, " +
            "	 null AS tiempoBreak, " +
            "	 null AS horaCierreSesion, " +
            "	 null AS porcentajeProductividadAgente," +
            "	 null AS tiempoAlmuerzo, " +
            "	 null AS tiempoProductivoAgente  "+
        "FROM  "+
        	"( "+
        	"        SELECT "+
        	"                date(C.fecha_inicio_conversacion) as fecha, "+
        	"                C.fecha_fin_conversacion, C.fecha_inicio_conversacion, C.id_agente as idAgente, C.id_conversacion, " +
        	"				 C.nombre_agente as nombreAgente, CPT.fecha_fin_segmento, CPT.fecha_inicio_segmento, CPT.tipo, CPT.segmento "+
        	"          FROM  "+
        	"                conversacion C "+
        	"          INNER JOIN conversaciones_por_tipo CPT ON C.id_conversacion = CPT.id_conversacion AND C.id_agente = CPT.id_agente "+                
        	"         WHERE "+        	
        	" 				(DATE(c.fecha_inicio_conversacion) BETWEEN '" + request.getFechaInicial() + "' AND '" + request.getFechaFinal() + "') "+        	
        	" 				AND C.id_agente IN (" + generarClausulaIn(request.getListadoDeAgentesStr()) + ")";        	
    	if (Objects.nonNull(request.getIdSearcheable()) && !request.getIdSearcheable().isEmpty()) {
    		sentenciaSQLReporte += " AND ( upper(nombre_agente) like upper('%" + request.getIdSearcheable() + "%') )  ";
    	}        	
    	sentenciaSQLReporte +=
        ") as TB00001   "+
        "GROUP BY fecha, nombreAgente ";		
    	//--
		String sentenciaCountSQLReporte = 				
		"SELECT COUNT(*) "+
        "FROM  "+
        	"( "+
        		sentenciaSQLReporte +
        "	)as TB00002"; 
		//--
		if (conteo) {
			sentenciaSQL = sentenciaCountSQLReporte;
		} else {
			sentenciaSQL = sentenciaSQLReporte;			
		}
		//--
		ejecucionDelSQL(conteo, sentenciaSQL);						
		return query;
	}
	
	private String generarClausulaIn(String listadoDeAgentesStr) {
		StringBuffer sbAgentes = new StringBuffer();
		
		if (!Objects.isNull(listadoDeAgentesStr) && !listadoDeAgentesStr.isEmpty()) {			
			String[] filtroAgentes = listadoDeAgentesStr.split(",");
											
			for (String agente : filtroAgentes) {
				sbAgentes.append("'");
				sbAgentes.append(agente);
				sbAgentes.append("'");
				sbAgentes.append(",");
			}		
			sbAgentes.deleteCharAt(sbAgentes.length() - 1);						
		}
		return sbAgentes.toString();
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
		
	private Integer obtenerElTotalDeRegistrosDelReporte(GenericBootStrapTableRequest request) {
		request.setOrder(null);
		request.setSort(null);
		query = configurarElSQL(true, request);
		return ((Number)query.getSingleResult()).intValue();
	}
	
	@Override
	public List<BTSReporteMapping> generarReporteSinPaginado(GenericBootStrapTableRequest request) {
		return obtenerLosRegistrosDelReporteSinPaginacion(request);
	}
	
	@SuppressWarnings("unchecked")
	private List<BTSReporteMapping> obtenerLosRegistrosDelReporteSinPaginacion(GenericBootStrapTableRequest request) {
		query = configurarElSQL(false, request);
		return realizarCalculosReporte(query.getResultList());
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
	
	private List<BTSReporteMapping> realizarCalculosReporte(List<BTSReporteMapping> resultList) {		
		if (Objects.nonNull(resultList) && !resultList.isEmpty()) {
			for (BTSReporteMapping btsReporteMapping : resultList) {
				double tiempoIntervaloVoz = 0;
				double tiempoIntervaloChat = 0;
				double tiempoIntervaloMail = 0;
				int numeroInteracciones = 0;
				
				
				//obtener hora de ingreso a la cola				
				String sql = "SELECT min(TIME_FORMAT(fecha_inicio_estado, '%H : %i : %s') ) as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'ON_QUEUE'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
				Query q = entityManager.createNativeQuery(sql);
				String fechaBase = btsReporteMapping.getFecha();				
				q.setParameter("fechaBase", fechaBase+" 00:00:00");
				q.setParameter("idAgente", btsReporteMapping.getIdAgente());
				String horaDeIngresoALaCola =  (String) q.getSingleResult();
				btsReporteMapping.setHoraIngresoCola("00:00");
				if(Objects.nonNull(horaDeIngresoALaCola)){
					btsReporteMapping.setHoraIngresoCola(horaDeIngresoALaCola);	
				}
				
				
								
				//obtener hora de cierre de session
				sql = "SELECT min(TIME_FORMAT(fecha_inicio_estado, '%H : %i : %s') ) as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'OFFLINE'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
				q = entityManager.createNativeQuery(sql);								
				q.setParameter("fechaBase", fechaBase+" 00:00:00");
				q.setParameter("idAgente", btsReporteMapping.getIdAgente());
				String horaDeCierreDeSession =  (String) q.getSingleResult();
				btsReporteMapping.setHoraCierreSesion("00:00");
				if(Objects.nonNull(horaDeCierreDeSession)){
					btsReporteMapping.setHoraCierreSesion(horaDeCierreDeSession);
				}
				
				//obtener Tiempo de Almuerzo
				sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'MEAL'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
				q = entityManager.createNativeQuery(sql);								
				q.setParameter("fechaBase", fechaBase+" 00:00:00");
				q.setParameter("idAgente", btsReporteMapping.getIdAgente());
				BigDecimal tiempoDeAlmuerzo =  (BigDecimal) q.getSingleResult();
				btsReporteMapping.setTiempoAlmuerzo("0");
				if(Objects.nonNull(tiempoDeAlmuerzo)){
					btsReporteMapping.setTiempoAlmuerzo(tiempoDeAlmuerzo.toString());
				}
				
				//obtener Tiempo de break
				sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'BREAK'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
				q = entityManager.createNativeQuery(sql);								
				q.setParameter("fechaBase", fechaBase+" 00:00:00");
				q.setParameter("idAgente", btsReporteMapping.getIdAgente());
				BigDecimal tiempoDeDescanso =  (BigDecimal) q.getSingleResult();
				btsReporteMapping.setTiempoBreak("0");				
				if(Objects.nonNull(tiempoDeDescanso)){
					btsReporteMapping.setTiempoBreak(tiempoDeDescanso.toString());
				}
				
				//obtener Tiempo productivo
				
				//obtener Tiempo no productivo
				btsReporteMapping.setTiempoProductivoAgente("0");
				btsReporteMapping.setPorcentajeProductividadAgente("0 %");
				sql = "SELECT IFNULL(SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado)),0)  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'IDLE'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
				q = entityManager.createNativeQuery(sql);								
				q.setParameter("fechaBase", fechaBase+" 00:00:00");
				q.setParameter("idAgente", btsReporteMapping.getIdAgente());		
				BigDecimal tiempoProductivoIdle =  (BigDecimal) q.getSingleResult();
				if(Objects.nonNull(tiempoProductivoIdle) && Objects.nonNull(btsReporteMapping.getTiempoProductivoAgenteUno())){
					double tiempoProductivoIdleDouble = Double.parseDouble(tiempoProductivoIdle.toString());
					double tiempoProductivoResto = Double.parseDouble(btsReporteMapping.getTiempoProductivoAgenteUno());
					double tiempoProductivoTotal = tiempoProductivoIdleDouble + tiempoProductivoResto;					
					btsReporteMapping.setTiempoProductivoAgente(String.valueOf(tiempoProductivoTotal));
				}
	
				
				// Calcular tiempo promedio voz
				btsReporteMapping.setTiempoPromedioVoz("0");
				if (!Objects.isNull(btsReporteMapping.getTiempoIntervaloVoz())  && !btsReporteMapping.getTiempoIntervaloVoz().equals("0") ) {
					tiempoIntervaloVoz = Double.parseDouble(btsReporteMapping.getTiempoIntervaloVoz());
					numeroInteracciones = Integer.parseInt(btsReporteMapping.getNumeroInteraccionesVoz());
					if(numeroInteracciones>0){
						btsReporteMapping.setTiempoPromedioVoz(String.valueOf(tiempoIntervaloVoz / numeroInteracciones));	
					}
					
				}				
				
				// Calcular tiempo promedio chat
				btsReporteMapping.setTiempoPromedioChat("0");
				if (!Objects.isNull(btsReporteMapping.getTiempoIntervaloChat()) && !btsReporteMapping.getTiempoIntervaloChat().equals("0") ) {
					tiempoIntervaloChat = Double.parseDouble(btsReporteMapping.getTiempoIntervaloChat());
					numeroInteracciones = Integer.parseInt(btsReporteMapping.getNumeroInteraccionesChat());
					if(numeroInteracciones>0){
						btsReporteMapping.setTiempoPromedioChat(String.valueOf(tiempoIntervaloChat / numeroInteracciones));
					}
				}	
				
				// Calcular tiempo promedio mail
				btsReporteMapping.setTiempoPromedioEmail("0");
				if (!Objects.isNull(btsReporteMapping.getTiempoIntervaloEmail()) && !btsReporteMapping.getTiempoIntervaloEmail().equals("0") ) {
					tiempoIntervaloMail = Double.parseDouble(btsReporteMapping.getTiempoIntervaloEmail());
					numeroInteracciones = Integer.parseInt(btsReporteMapping.getNumeroInteraccionesEmail());
					if(numeroInteracciones>0){
						btsReporteMapping.setTiempoPromedioEmail(String.valueOf(tiempoIntervaloMail / numeroInteracciones));
					}
				}
				
				// Calcular porcentaje productividad agente			
				btsReporteMapping.setPorcentajeProductividadAgente("0");
				if (!Objects.isNull(btsReporteMapping.getTiempoProductivoAgente()) && Double.parseDouble(btsReporteMapping.getTiempoProductivoAgente()) > 0) {
					double totalProductivoNoProductivo = Double.parseDouble(btsReporteMapping.getTiempoProductivoAgente());
					double totalProductivo = tiempoIntervaloVoz + tiempoIntervaloChat + tiempoIntervaloMail; 
					
					double porcentajeProductividadAgente = (totalProductivo / totalProductivoNoProductivo) * 100;
					btsReporteMapping.setPorcentajeProductividadAgente(decimalFormat.format(porcentajeProductividadAgente) + "%");
				}
							
			}
		}
		
		return resultList;
	}
	
}							
