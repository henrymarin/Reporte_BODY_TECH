package com.bodytech.reporte.servicios.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

@Service
public class GenerarReporteServiceImpl implements GenerarReporteService {

	private static final String CERO = "0";
	private static final Logger logger = LoggerFactory.getLogger(GenerarReporteServiceImpl.class);
	private static DecimalFormat decimalFormat = new DecimalFormat(".#");	
		
	private Query query;
	private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	
	@PersistenceContext	
	private EntityManager entityManager;
	
	@Autowired 
	private AgenteRepository agenteRepository;

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
            "    SUM(CASE WHEN UPPER(tipo) = 'VOICE' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_conversacion, fecha_fin_conversacion) ELSE 0 END) AS tiempoIntervaloVoz, "+
            "    SUM(CASE WHEN UPPER(tipo) = 'CHAT' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_conversacion, fecha_fin_conversacion) ELSE 0 END) AS tiempoIntervaloChat, "+
            "    SUM(CASE WHEN UPPER(tipo) = 'EMAIL' THEN TIMESTAMPDIFF(SECOND, fecha_inicio_conversacion, fecha_fin_conversacion) ELSE 0 END) AS tiempoIntervaloEmail, "+
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
        	"                C.fecha_fin_conversacion, C.fecha_inicio_conversacion, CPT.id_agente as idAgente, C.id_conversacion, " +
        	"				 (SELECT ci.nombre_agente FROM conversacion ci WHERE ci.id_agente = CPT.id_agente LIMIT 1 ) as nombreAgente, CPT.fecha_fin_segmento, CPT.fecha_inicio_segmento, CPT.tipo, CPT.segmento "+
        	"          FROM  "+
        	"                conversacion C "+
        	"          INNER JOIN conversaciones_por_tipo CPT ON C.id_conversacion = CPT.id_conversacion "+                
        	"         WHERE "+        	
        	" 				(DATE(c.fecha_inicio_conversacion) BETWEEN '" + request.getFechaInicial() + "' AND '" + request.getFechaFinal() + "') "+        	
        	" 				AND CPT.id_agente IN (" + generarClausulaIn(request.getListadoDeAgentesStr()) + ") GROUP BY C.id_conversacion";        	
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
		System.out.println("Sentencia reporte "+sentenciaSQLReporte);
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
				String fechaBase = btsReporteMapping.getFecha();									
								
				//obtener hora ingreso a la cola			
				realizarCalculosReporteHoraIngresoCola(btsReporteMapping, fechaBase);
								
				//obtener hora de cierre de session
				realizarCalculosReporteHoraCierreSesion(btsReporteMapping, fechaBase);
				
				// Obtener hora inicio sesión
				realizarCalculosReporteHoraInicioSesion(btsReporteMapping, fechaBase);
				
				//obtener Tiempo de Almuerzo
				realizarCalculosReporteTiempoAlmuerzo(btsReporteMapping, fechaBase);
				
				//obtener Tiempo de break
				realizarCalculosReporteTiempoBreak(btsReporteMapping, fechaBase);
				
				// Obtener Tiempo Disponible
				realizarCalculosReporteTiempoAVAILABLE(btsReporteMapping, fechaBase);
				
				// Obtener Tiempo Ocupado
				realizarCalculosReporteTiempoBUSY(btsReporteMapping, fechaBase);
				
				// Obtener Tiempo Ausente
				realizarCalculosReporteTiempoAWAY(btsReporteMapping, fechaBase);
				
				// Obtener Tiempo Reunion
				realizarCalculosReporteTiempoMEETING(btsReporteMapping, fechaBase);
				
				// Obtener Tiempo Capacitación
				realizarCalculosReporteTiempoTRAINING(btsReporteMapping, fechaBase);
				
				// Obtener Tiempo En Cola
				realizarCalculosReporteTiempoON_QUEUE(btsReporteMapping, fechaBase);
				
				// Obtener Tiempo En Cola
				realizarCalculosReporteTiempoHOLD(btsReporteMapping, fechaBase);
				
					
				// Calcular tiempo promedio voz
				btsReporteMapping.setTiempoPromedioVoz(CERO);
				if (!Objects.isNull(btsReporteMapping.getTiempoIntervaloVoz())  && !CERO.equals(btsReporteMapping.getTiempoIntervaloVoz()) ) {
					tiempoIntervaloVoz = Double.parseDouble(btsReporteMapping.getTiempoIntervaloVoz());
					numeroInteracciones = Integer.parseInt(btsReporteMapping.getNumeroInteraccionesVoz());
					if(numeroInteracciones>0){
						btsReporteMapping.setTiempoPromedioVoz(String.valueOf((tiempoIntervaloVoz / numeroInteracciones)));	
					}					
				}				
				
				// Calcular tiempo promedio chat
				btsReporteMapping.setTiempoPromedioChat(CERO);
				if (!Objects.isNull(btsReporteMapping.getTiempoIntervaloChat()) && !CERO.equals(btsReporteMapping.getTiempoIntervaloChat()) ) {
					tiempoIntervaloChat = Double.parseDouble(btsReporteMapping.getTiempoIntervaloChat());
					numeroInteracciones = Integer.parseInt(btsReporteMapping.getNumeroInteraccionesChat());
					if(numeroInteracciones>0){
						btsReporteMapping.setTiempoPromedioChat(String.valueOf((tiempoIntervaloChat / numeroInteracciones)));
					}
				}	
				
				// Calcular tiempo promedio mail
				btsReporteMapping.setTiempoPromedioEmail(CERO);
				if (!Objects.isNull(btsReporteMapping.getTiempoIntervaloEmail()) && !CERO.equals(btsReporteMapping.getTiempoIntervaloVoz()) ) {
					tiempoIntervaloMail = Double.parseDouble(btsReporteMapping.getTiempoIntervaloEmail());
					numeroInteracciones = Integer.parseInt(btsReporteMapping.getNumeroInteraccionesEmail());
					if(numeroInteracciones>0){
						btsReporteMapping.setTiempoPromedioEmail(String.valueOf((tiempoIntervaloMail / numeroInteracciones)));
					}
				}
				
				String tiempoProductividadAgenteUno = String.valueOf(tiempoIntervaloVoz + tiempoIntervaloChat + tiempoIntervaloMail);
				btsReporteMapping.setTiempoProductivoAgenteUno(tiempoProductividadAgenteUno);
								
				// Calcular porcentaje productividad agente			
				btsReporteMapping.setPorcentajeProductividadAgente(CERO);
								
				double porcentajeProductividadAgente = 0;
					double tiempoEfectivoDia = 0;
					long tiempoTotalDiaAgente = 0;
																		
					Date dateOff = null;
					Date dateIn = null;
					try {
						dateOff = formatter.parse(btsReporteMapping.getHoraCierreSesion().trim().replaceAll("\\s",""));
					} catch (ParseException e) {						
						logger.error(GenerarReporteServiceImpl.class.toString() + " Error formateando hora logoff - Agente=" + btsReporteMapping.getIdAgente() + " - Fecha=" + btsReporteMapping.getFecha(), e);
					}
					
					try {
						dateIn = formatter.parse(btsReporteMapping.getHoraInicioSesion().trim().replaceAll("\\s",""));
					} catch (ParseException e) {
						logger.error(GenerarReporteServiceImpl.class.toString() + " Error formateando hora login - Agente=" + btsReporteMapping.getIdAgente() + " - Fecha=" + btsReporteMapping.getFecha(), e);
					}
					
					if (!Objects.isNull(dateOff) && !Objects.isNull(dateIn)) {
						
						tiempoTotalDiaAgente = (dateOff.getTime() - dateIn.getTime()) / 1000;
						
						tiempoEfectivoDia = (tiempoTotalDiaAgente - 
											Double.parseDouble(btsReporteMapping.getTiempoPausa()) - 
											Double.parseDouble(btsReporteMapping.getTiempoAlmuerzo()) - 
											Double.parseDouble(btsReporteMapping.getTiempoBreak()) - 
											Double.parseDouble(btsReporteMapping.getTiempoAusente()));
												
						double tiempoSumatorioIntervaloVozChatEmail = Double.parseDouble(btsReporteMapping.getTiempoIntervaloVoz()) + Double.parseDouble(btsReporteMapping.getTiempoIntervaloChat()) +
								Double.parseDouble(btsReporteMapping.getTiempoIntervaloEmail());
						
						btsReporteMapping.setTiempoProductivoAgente(String.valueOf(tiempoEfectivoDia));
						
						porcentajeProductividadAgente = ( ( tiempoSumatorioIntervaloVozChatEmail / (tiempoEfectivoDia) ) * 100)  ;
						
						btsReporteMapping.setPorcentajeProductividadAgente(String.valueOf(Double.valueOf(porcentajeProductividadAgente).intValue()) + "%");
					}					
				 //Formatear campos
				realizarCalculosReporteFormatearFormatoSegundos(btsReporteMapping);							
				formatearFechaReporte(btsReporteMapping);
			}
		}
		
		return resultList;
	}

	private void formatearFechaReporte(BTSReporteMapping btsReporteMapping){
		DateFormat formatOld = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatNew = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date fechaOld = formatOld.parse(btsReporteMapping.getFecha());
			btsReporteMapping.setFecha(formatNew.format(fechaOld));
		} catch (ParseException e) {
			logger.error(GenerarReporteServiceImpl.class.toString() + " Error formateando fecha Reporte - Agente=" + btsReporteMapping.getIdAgente() + " - Fecha=" + btsReporteMapping.getFecha(), e);
		}
	}
	
	private void realizarCalculosReporteTiempoBreak(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'BREAK'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		BigDecimal tiempoDeDescanso =  (BigDecimal) q.getSingleResult();
		btsReporteMapping.setTiempoBreak(CERO);				
		if(Objects.nonNull(tiempoDeDescanso)){
			btsReporteMapping.setTiempoBreak(tiempoDeDescanso.toString());
		}
	}
	
	private void realizarCalculosReporteTiempoAVAILABLE(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'AVAILABLE'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		BigDecimal tiempoDisponible =  (BigDecimal) q.getSingleResult();
		btsReporteMapping.setTiempoDisponible(CERO);				
		if(Objects.nonNull(tiempoDisponible)){
			btsReporteMapping.setTiempoDisponible(tiempoDisponible.toString());
		}
	}
	
	private void realizarCalculosReporteTiempoBUSY(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'BUSY'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		BigDecimal tiempoOcupado =  (BigDecimal) q.getSingleResult();
		btsReporteMapping.setTiempoOcupado(CERO);				
		if(Objects.nonNull(tiempoOcupado)){
			btsReporteMapping.setTiempoOcupado(tiempoOcupado.toString());
		}
	}
	
	private void realizarCalculosReporteTiempoHOLD(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'HOLD'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		BigDecimal tiempoPausa =  (BigDecimal) q.getSingleResult();
		System.out.println("Tiempo pausa ___ "+tiempoPausa);
		btsReporteMapping.setTiempoPausa(CERO);				
		if(Objects.nonNull(tiempoPausa)){
			btsReporteMapping.setTiempoPausa(tiempoPausa.toString());
		}
	}
	
	private void realizarCalculosReporteTiempoAWAY(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'AWAY'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		BigDecimal tiempoAusente =  (BigDecimal) q.getSingleResult();
		btsReporteMapping.setTiempoAusente(CERO);				
		if(Objects.nonNull(tiempoAusente)){
			btsReporteMapping.setTiempoAusente(tiempoAusente.toString());
		}
	}
	
	private void realizarCalculosReporteTiempoMEETING(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'MEETING'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		BigDecimal tiempoEnReunion =  (BigDecimal) q.getSingleResult();
		btsReporteMapping.setTiempoEnReunion(CERO);				
		if(Objects.nonNull(tiempoEnReunion)){
			btsReporteMapping.setTiempoEnReunion(tiempoEnReunion.toString());
		}
	}
	
	private void realizarCalculosReporteTiempoTRAINING(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'TRAINING'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql); 								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");  
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		BigDecimal tiempoEnCapacitacion =  (BigDecimal) q.getSingleResult();
		btsReporteMapping.setTiempoEnCapacitacion(CERO);				
		if(Objects.nonNull(tiempoEnCapacitacion)){
			btsReporteMapping.setTiempoEnCapacitacion(tiempoEnCapacitacion.toString());
		}
	}
	
	private void realizarCalculosReporteTiempoON_QUEUE(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'ON_QUEUE'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		BigDecimal tiempoEnCola =  (BigDecimal) q.getSingleResult();
		btsReporteMapping.setTiempoEnCola(CERO);				
		if(Objects.nonNull(tiempoEnCola)){
			btsReporteMapping.setTiempoEnCola(tiempoEnCola.toString());
		}
	}

	private void realizarCalculosReporteFormatearFormatoSegundos(BTSReporteMapping btsReporteMapping) {
		btsReporteMapping.setTiempoIntervaloVoz(formatearSegundos(btsReporteMapping.getTiempoIntervaloVoz()));
		btsReporteMapping.setTiempoIntervaloChat(formatearSegundos(btsReporteMapping.getTiempoIntervaloChat()));
		btsReporteMapping.setTiempoIntervaloEmail(formatearSegundos(btsReporteMapping.getTiempoIntervaloEmail()));				
		btsReporteMapping.setTiempoAlmuerzo(formatearSegundos(btsReporteMapping.getTiempoAlmuerzo()));
		btsReporteMapping.setTiempoBreak(formatearSegundos(btsReporteMapping.getTiempoBreak()));
		btsReporteMapping.setTiempoPausa(formatearSegundos(btsReporteMapping.getTiempoPausa()));				
		btsReporteMapping.setTiempoProductivoAgente(formatearSegundos(btsReporteMapping.getTiempoProductivoAgente()));
		btsReporteMapping.setTiempoDisponible(formatearSegundos(btsReporteMapping.getTiempoDisponible()));
		btsReporteMapping.setTiempoOcupado(formatearSegundos(btsReporteMapping.getTiempoOcupado()));
		btsReporteMapping.setTiempoAusente(formatearSegundos(btsReporteMapping.getTiempoAusente()));
		btsReporteMapping.setTiempoEnReunion(formatearSegundos(btsReporteMapping.getTiempoEnReunion()));
		btsReporteMapping.setTiempoEnCapacitacion(formatearSegundos(btsReporteMapping.getTiempoEnCapacitacion()));
		btsReporteMapping.setTiempoEnCola(formatearSegundos(btsReporteMapping.getTiempoEnCola()));
		btsReporteMapping.setTiempoPromedioVoz(formatearSegundos(String.valueOf(Double.valueOf(btsReporteMapping.getTiempoPromedioVoz()).intValue())));
		btsReporteMapping.setTiempoPromedioEmail(formatearSegundos(String.valueOf(Double.valueOf(btsReporteMapping.getTiempoPromedioEmail()).intValue())));
		btsReporteMapping.setTiempoPromedioChat(formatearSegundos(String.valueOf(Double.valueOf(btsReporteMapping.getTiempoPromedioChat()).intValue())));
	}

	private void realizarCalculosReporteTiempoAlmuerzo(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, fecha_inicio_estado, fecha_fin_estado))  as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'MEAL'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		BigDecimal tiempoDeAlmuerzo =  (BigDecimal) q.getSingleResult();
		btsReporteMapping.setTiempoAlmuerzo(CERO);
		if(Objects.nonNull(tiempoDeAlmuerzo)){
			btsReporteMapping.setTiempoAlmuerzo(tiempoDeAlmuerzo.toString());
		}
	}

	private void realizarCalculosReporteHoraCierreSesion(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT max(TIME_FORMAT(fecha_inicio_estado, '%H : %i : %s') ) as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'OFFLINE'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		String horaDeCierreDeSession =  (String) q.getSingleResult();
		btsReporteMapping.setHoraCierreSesion("00:00");
		if(Objects.nonNull(horaDeCierreDeSession)){
			btsReporteMapping.setHoraCierreSesion(horaDeCierreDeSession);
		}
	}
	
	private void realizarCalculosReporteHoraInicioSesion(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql;
		Query q;
		sql = "SELECT min(TIME_FORMAT(fecha_fin_estado, '%H : %i : %s') ) as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'OFFLINE'  AND ( DATE(m.fecha_inicio_estado) = :fechaBase OR DATE(fecha_fin_estado) = :fechaBase ) AND m.id_agente = :idAgente";
		q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		String horaDeInicioDeSession =  (String) q.getSingleResult();
		btsReporteMapping.setHoraInicioSesion("00:00");
		if(Objects.nonNull(horaDeInicioDeSession)){
			btsReporteMapping.setHoraInicioSesion(horaDeInicioDeSession);
		}
	}

	private void realizarCalculosReporteHoraIngresoCola(BTSReporteMapping btsReporteMapping, String fechaBase) {
		String sql = "SELECT min(TIME_FORMAT(fecha_inicio_estado, '%H : %i : %s') ) as hora  FROM estados_por_agente m WHERE UPPER(estado) = 'ON_QUEUE'  AND DATE(m.fecha_inicio_estado) = :fechaBase AND m.id_agente = :idAgente";
		Query q = entityManager.createNativeQuery(sql);								
		q.setParameter("fechaBase", fechaBase+" 00:00:00");
		q.setParameter("idAgente", btsReporteMapping.getIdAgente());
		String horaDeIngresoALaCola =  (String) q.getSingleResult();
		btsReporteMapping.setHoraIngresoCola("00:00");
		if(Objects.nonNull(horaDeIngresoALaCola)){
			btsReporteMapping.setHoraIngresoCola(horaDeIngresoALaCola);	
		}
	}

	private String formatearSegundos(String valor) {
		try{ 
			if(Objects.nonNull(valor)){
				Double valorD = Double.parseDouble(valor);
				int d = valorD.intValue();
				int hr = d / 3600;
				int rem = d % 3600;
				int mn = rem / 60;
				int sec = rem % 60;
				String hrStr = (hr < 10 ? CERO : "") + hr;
				String mnStr = (mn < 10 ? CERO : "") + mn;
				String secStr = (sec < 10 ? CERO : "") + sec;
				return hrStr + ":" + mnStr + ":" + secStr;	
			} 
			return "00:00:00";			
		}catch (Exception e) {
			logger.error("erro en formatearSegundos.", e);
			return "00:00:00";
		}
	}
	
}							
