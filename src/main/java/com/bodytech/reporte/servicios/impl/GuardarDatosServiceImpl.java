package com.bodytech.reporte.servicios.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bodytech.reporte.dtos.PrecargaResponse;
import com.bodytech.reporte.entidades.Agente;
import com.bodytech.reporte.entidades.Conversacion;
import com.bodytech.reporte.entidades.ConversacionesPorTipo;
import com.bodytech.reporte.entidades.EstadosPorAgente;
import com.bodytech.reporte.entidades.Precarga;
import com.bodytech.reporte.repositorios.AgenteRepository;
import com.bodytech.reporte.repositorios.ConversacionRepository;
import com.bodytech.reporte.repositorios.ConversacionesPorTipoRepository;
import com.bodytech.reporte.repositorios.EstadosPorAgenteRepository;
import com.bodytech.reporte.repositorios.PrecargaRepository;
import com.bodytech.reporte.servicios.GuardarDatosService;
import com.javainuse.DtoEntrada;
import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.Configuration;
import com.mypurecloud.sdk.v2.api.ConversationsApi;
import com.mypurecloud.sdk.v2.api.UsersApi;
import com.mypurecloud.sdk.v2.model.AnalyticsConversation;
import com.mypurecloud.sdk.v2.model.AnalyticsConversationQueryResponse;
import com.mypurecloud.sdk.v2.model.AnalyticsConversationSegment;
import com.mypurecloud.sdk.v2.model.AnalyticsParticipant;
import com.mypurecloud.sdk.v2.model.AnalyticsQueryFilter;
import com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate;
import com.mypurecloud.sdk.v2.model.AnalyticsRoutingStatusRecord;
import com.mypurecloud.sdk.v2.model.AnalyticsSession;
import com.mypurecloud.sdk.v2.model.AnalyticsUserDetail;
import com.mypurecloud.sdk.v2.model.AnalyticsUserPresenceRecord;
import com.mypurecloud.sdk.v2.model.ConversationQuery;
import com.mypurecloud.sdk.v2.model.PagingSpec;
import com.mypurecloud.sdk.v2.model.User;
import com.mypurecloud.sdk.v2.model.UserDetailsQuery;
import com.mypurecloud.sdk.v2.model.UserEntityListing;

@Service
public class GuardarDatosServiceImpl implements GuardarDatosService{

	private static final String ACTIVE = "active";
	private static final String DE_AYUDA = "DE AYUDA";
	private static final String SAC = "SAC";
	private static final String MDA = "MDA";
	private static final String NO_TYPE = "no type";
	private static final String NO_NAME = "no name";
	private static final String URL_MYPURECLOUD = "https://api.mypurecloud.com";
	@Autowired private ConversacionRepository conversacionRepository;
	@Autowired private EstadosPorAgenteRepository estadosPorAgenteRepository;
	@Autowired private ConversacionesPorTipoRepository conversacionesPorTipoRepository;	
	@Autowired private PrecargaRepository precargaRepository;
	@Autowired private AgenteRepository agenteRepository;
	private static final Logger logger = LoggerFactory.getLogger(GuardarDatosServiceImpl.class);
	
	@Override
	public PrecargaResponse obtenerInformacionDeLaPrecarga() {

		PrecargaResponse rta = new PrecargaResponse();
		rta.setEstado("N/A");
		rta.setFechaFin("N/A");
		rta.setFechaInicio("N/A");
		List<Precarga> listadoDePrecargas = (List<Precarga>) precargaRepository.findAll();
		if(Objects.nonNull(listadoDePrecargas) && !listadoDePrecargas.isEmpty()){
			Precarga precarga = listadoDePrecargas.get(0);
			rta.setEstado(precarga.getEstado());
			rta.setFechaFin(precarga.getFechaFin());
			rta.setFechaInicio(precarga.getFechaInicio());
		}		
		return rta;
	}
	
	
	public  void guardarDatosDepuradoConCargaDeAgentesXXXXXX(DtoEntrada dto) {
		
		Timer timer;
		timer = new Timer();
		TimerTask task = new TimerTask() {
	        int tic=0;
	        @Override
	        public void run(){
	            if (tic%2==0)
	            	System.out.println("TIC");
				else
					System.out.println("TOC");
				tic++;
			}
		};
		// Empezamos dentro de 10ms y luego lanzamos la tarea cada 60000ms es decir un minuto
		timer.schedule(task, 1, 30000);
	}
	

	@Override
	public  void guardarDatosDepuradoConCargaDeAgentes(DtoEntrada dto) {
		
		
			//--1

        	long startTotal = System.currentTimeMillis();
        	System.out.println("<<<<<<<-------------------------------------OO------------------------------------>>>>>>");
        	System.out.println("<                                                                                       >");
        	System.out.println("<                                                                                       >");
        	System.out.println("<                                                                                       >");
        	System.out.println("<                                                                                       >");
        	System.out.println("<                                                                                       >");
        	System.out.println("<                                                                                       >");
        	System.out.println("<<<<<<<-------------------------------------OO------------------------------------>>>>>>");
        	System.out.println("        INICIO DEL PROCESO DE LA CARGA---------->>>>>>");
        	//--I N I C I O		Proceso de eliminacion de registros existente
        	conversacionRepository.deleteAll();
        	conversacionesPorTipoRepository.deleteAll();
        	precargaRepository.deleteAll();
        	//-- F I N   		Proceso de eliminacion de registros existente
        	//--
        	Precarga precarga = new Precarga();
        	precarga.setFechaInicio(dto.getFechaUno());
        	precarga.setFechaFin(dto.getFechaDos());
			precarga.setEstado("En Proceso");
			precargaRepository.save(precarga);
        	//--
			System.out.println("        		PROCESO DE LA CARGA DE CONVERSACIONES Y DE SUS TIPOS");
        	System.out.println("Intervalo de Fechas---->>  " + dto.getFechaUno()+"/"+dto.getFechaDos());
        	// Start the clock
        	long start = System.currentTimeMillis();
			// Configure SDK settings
			String accessToken = dto.getEntradaUno();
			Configuration.setDefaultApiClient(ApiClient.Builder.standard().withAccessToken(accessToken).withBasePath(URL_MYPURECLOUD).build());
			
			// Instantiate APIs ConversationsApi
			ConversationsApi apiInstance = new ConversationsApi();
			//--
			Integer paginaConversationQuery = 1;
			boolean conversationQueryObtuvoResultados = true;
			int totalConversacionesInsertadas = 0;
			int maximosRegistrosAInsertar = 250;
	        do {
	        	//--
				ConversationQuery body = new ConversationQuery(); // ConversationQuery | query
				guardarDatosCrearFiltroConversationQuery(body, dto, paginaConversationQuery);
				    try {
				    	//						<<<<--- R E S P O N S E 	ConversationsDetailsQuery---->>>>>	    	
						AnalyticsConversationQueryResponse result = apiInstance.postAnalyticsConversationsDetailsQuery(body);
						if(Objects.nonNull(result) && Objects.nonNull(result.getConversations()) && !result.getConversations().isEmpty()){
							List<AnalyticsConversation> conversations = result.getConversations();
							for (AnalyticsConversation conversacionPC : conversations) {
								guardarDatosProcesoPrincipal(conversacionPC, dto);
							}
							paginaConversationQuery+=1;
							//--
							totalConversacionesInsertadas = totalConversacionesInsertadas + 1;
							if( totalConversacionesInsertadas==maximosRegistrosAInsertar ){
								try {
									//cada n saves espera 30 segundos (30000 ==  a 1 minuto y medio; 60000 == a un minuto)
									//cada n saves espera 30 segundos (30000 ==  a 30 segundos; 60000 == a 60 segundos, 15000 == a 25 segundos)
									totalConversacionesInsertadas = 0;
									System.out.println("----------------------------------------------------------INICIANDO ESPERA 0001");
									Thread.sleep(15000);
								} catch (InterruptedException e) {
									logger.error("erro.", e);
								}
							}
							//--
						}else{
							conversationQueryObtuvoResultados = false;
						}
				} catch (ApiException | IOException e) {
					logger.error("erro.", e);
				}
				//--			            
	        } while (conversationQueryObtuvoResultados);
		    // Print results, including elapsed time
			System.out.println("   Elapsed time in minutes: " + ( (System.currentTimeMillis() - start) * (0.0000167) ) );
			System.out.println("	FIN DE LA CARGA DE CONVERSACIONES Y DE SUS TIPOS");					
			//--

        	System.out.println("<                                                                                       >");
        	System.out.println("<                                                                                       >");
        	
        	long startAgentes = System.currentTimeMillis();
        	agenteRepository.deleteAll();	 
        	System.out.println("        		PROCESO DE LA CARGA DE AGENTES");
			//			INFORMACION DE TODOS LOS AGENTES
			UsersApi UsersApiInstance = new UsersApi();
			Integer pageSize = 100; // Integer | Page size
			String sortOrder = "ASC"; // String | Ascending or descending sort order
			try {
				Integer paginaGetUsersQuery = 1;
				boolean getUsersQueryObtuvoResultados = true;
				try {
					//cada n saves espera 30 segundos (30000 ==  a 1 minuto y medio; 60000 == a un minuto)
					//cada n saves espera 30 segundos (30000 ==  a 30 segundos; 60000 == a 60 segundos, 15000 == a 25 segundos)
					System.out.println("----------------------------------------------------------INICIANDO ESPERA 0002");
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					logger.error("erro.", e);
				}
		        do {
					UserEntityListing entidadConUsuarios = UsersApiInstance .getUsers(pageSize, paginaGetUsersQuery, new ArrayList<>(), sortOrder, new ArrayList<>());
					if(Objects.nonNull(entidadConUsuarios) && Objects.nonNull(entidadConUsuarios.getEntities()) && !entidadConUsuarios.getEntities().isEmpty()){
					    List<User> usuarios = entidadConUsuarios.getEntities();
					    for (User user : usuarios) {
					    	if(user.getState().toString().equals(ACTIVE)){
								Agente agente = new Agente();
								agente.setIdAgente(user.getId());
								agente.setNombreAgente(NO_NAME);
								agente.setTipoAgente(NO_TYPE);
								if(Objects.nonNull(user.getName())){
									agente.setNombreAgente(user.getName());
									if(user.getName().toUpperCase().contains(DE_AYUDA)){
										agente.setTipoAgente(MDA);
									}else{
										agente.setTipoAgente(SAC);
									}
								}
								agenteRepository.save(agente);									    		
					    	}
						}
					}else{
						getUsersQueryObtuvoResultados = false;
					}
		        	paginaGetUsersQuery += 1;
		        }while (getUsersQueryObtuvoResultados);
			    // Print results, including elapsed time
				System.out.println("   Elapsed time in minutes: " + ( (System.currentTimeMillis() - startAgentes) * (0.0000167) ) );
				System.out.println("	FIN DE LA CARGA DE AGENTES");
	        	System.out.println("<                                                                                       >");
	        	System.out.println("<                                                                                       >");
			} catch (ApiException | IOException e) {
				logger.error("Exception when calling UsersApi#getUsers", e);
			}
			//FIN poceso de carga de agentes

			//inicio E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
//        	long startEstadosAgentes = System.currentTimeMillis();
//        	estadosPorAgenteRepository.deleteAll();	 
//        	System.out.println("        		PROCESO DE LA CARGA DE ESTADOS DE LOS AGENTES");
//        	List<Agente> listadoDeAgentes =  (List<Agente>) agenteRepository.findAll();
//        	if(Objects.nonNull(listadoDeAgentes) && !listadoDeAgentes.isEmpty()){
//            	for (Agente agente : listadoDeAgentes) {
//					// 			E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
//					Integer paginaUserDetailsQuery = 1;
//					boolean userDetailsQueryObtuvoResultados = true;
//			        do {										
//			        	UserDetailsQuery userBody = guardarDatosProcesoPrincipalConfigurarParticipantesCrearFiltroUserDetail(agente.getIdAgente(), dto, paginaUserDetailsQuery);
//						try {
//							UsersApi userApiInstance = new UsersApi();
//					    	//						<<<<--- R E S P O N S E 	postAnalyticsUsersDetailsQuery---->>>>>						
//						    AnalyticsUserDetailsQueryResponse userResult = userApiInstance.postAnalyticsUsersDetailsQuery(userBody);
//						    if(Objects.nonNull(userResult) && Objects.nonNull(userResult.getUserDetails()) && !userResult.getUserDetails().isEmpty()){
//							    List<AnalyticsUserDetail> userDetails = userResult.getUserDetails();											    
//							    for (AnalyticsUserDetail userDetail : userDetails) {
//							    	guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(agente.getId().toString(), agente.getIdAgente(), userDetail);
//								}
//							    paginaUserDetailsQuery += 1;
//						    }else{
//						    	userDetailsQueryObtuvoResultados = false;
//						    }
//						} catch (ApiException | IOException e) {
//							logger.error("E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]" + e.getMessage(), e);
//						}								        	
//			        } while (userDetailsQueryObtuvoResultados);
//			        //fin estado de los agentes
//            	}
//			    // Print results, including elapsed time
//				System.out.println("   Elapsed time in minutes: " + ( (System.currentTimeMillis() - startEstadosAgentes) * (0.0000167) ) );
//				System.out.println("	FIN DE LA CARGA DE ESTADOS POR AGENTE");
//				//--
//	        	System.out.println("<                                                                                       >");
//	        	System.out.println("<                                                                                       >");
//				List<Precarga> listadoDePrecargas = (List<Precarga>) precargaRepository.findAll();
//				if(Objects.nonNull(listadoDePrecargas) && !listadoDePrecargas.isEmpty()){
//					Precarga precargaDB = listadoDePrecargas.get(0);
//					precargaDB.setEstado("Completada");
//					precargaRepository.save(precargaDB);
//				}
//				//--
//        	}
			System.out.println("   Elapsed time in minutes: " + ( (System.currentTimeMillis() - startTotal) * (0.0000167) ) );
			System.out.println("	FIN DE LAX CARGAX");
	}


				
				

	
	
	private void guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(
			String conversacionId, String agenteId,AnalyticsUserDetail userDetail) {
		EstadosPorAgente estadosPorAgente = new EstadosPorAgente(); 
		//--routingStatus -->>
		if(Objects.nonNull(userDetail.getRoutingStatus()) && !userDetail.getRoutingStatus().isEmpty() && (Objects.nonNull(agenteId) || !agenteId.isEmpty()) ){
			List<AnalyticsRoutingStatusRecord> routingStatus = userDetail.getRoutingStatus();
			for (AnalyticsRoutingStatusRecord routingSt : routingStatus) {
				estadosPorAgente = new EstadosPorAgente();
				estadosPorAgente.setEstado(routingSt.getRoutingStatus().toString());
				estadosPorAgente.setFechaFinEstado(routingSt.getEndTime());
				estadosPorAgente.setFechaInicioEstado(routingSt.getStartTime());
				estadosPorAgente.setIdAgente(agenteId);
				estadosPorAgente.setIdConversacion(conversacionId);
				estadosPorAgenteRepository.save(estadosPorAgente);
			}	
		}
		
		//--primaryPresences -->>
		if(Objects.nonNull(userDetail.getPrimaryPresence()) && !userDetail.getPrimaryPresence().isEmpty() && (Objects.nonNull(agenteId) || !agenteId.isEmpty())){
			List<AnalyticsUserPresenceRecord> primaryPresences = userDetail.getPrimaryPresence();
			for (AnalyticsUserPresenceRecord presencia : primaryPresences) {
					//---		columnas [K, L, C, P, Q]
				estadosPorAgente = new EstadosPorAgente();
				estadosPorAgente.setEstado(presencia.getSystemPresence().toString());
				estadosPorAgente.setFechaFinEstado(presencia.getEndTime());
				estadosPorAgente.setFechaInicioEstado(presencia.getStartTime());
				estadosPorAgente.setIdAgente(agenteId);
				estadosPorAgente.setIdConversacion(conversacionId);
				estadosPorAgenteRepository.save(estadosPorAgente);
			}
		}
	}
	
	private UserDetailsQuery guardarDatosProcesoPrincipalConfigurarParticipantesCrearFiltroUserDetail(String agenteId,DtoEntrada dto, Integer paginaUserDetailsQuery) {
		UserDetailsQuery userBody = new UserDetailsQuery(); // UserDetailsQuery | query
		userBody.setInterval(dto.getFechaUno()+"/"+dto.getFechaDos());
		//--
		PagingSpec userPaging = new PagingSpec();
		userPaging.setPageNumber(paginaUserDetailsQuery);
		userPaging.setPageSize(Integer.valueOf("100"));
		userBody.setPaging(userPaging);
		List<AnalyticsQueryFilter> userFilters = new ArrayList<>();
		AnalyticsQueryFilter userFilter = new AnalyticsQueryFilter();
		userFilter.setType(com.mypurecloud.sdk.v2.model.AnalyticsQueryFilter.TypeEnum.OR);
		List<AnalyticsQueryPredicate> userPredicates = new ArrayList<>();
		AnalyticsQueryPredicate userPredicate = new AnalyticsQueryPredicate();
		userPredicate.setType(com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate.TypeEnum.DIMENSION);
		userPredicate.setDimension(com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate.DimensionEnum.USERID);
		userPredicate.setOperator(com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate.OperatorEnum.MATCHES);
		userPredicate.setValue(agenteId);
		userPredicates.add(userPredicate);
		userFilter.setPredicates(userPredicates);
		userFilters.add(userFilter);
		userBody.setUserFilters(userFilters);
		return userBody;
	}
	
	private void guardarDatosProcesoPrincipal(AnalyticsConversation conversacionPC, DtoEntrada dto) throws IOException {
		Conversacion conversacion = new Conversacion();
		conversacion.setIdConversacion(conversacionPC.getConversationId());
		conversacion.setFechaInicioConversacion(conversacionPC.getConversationStart());
		conversacion.setFechaFinConversacion(conversacionPC.getConversationEnd());
		if(Objects.nonNull(conversacionPC.getParticipants()) && !conversacionPC.getParticipants().isEmpty()){
			List<AnalyticsParticipant> participants = conversacionPC.getParticipants();
			for (AnalyticsParticipant participante : participants) {
				//-- 			I D     D E L     A G E N T E
				if(Objects.nonNull(participante) && Objects.nonNull(participante.getUserId())){
					guardarDatosProcesoPrincipalConfigurarParticipantesNombreDelAgente(conversacion, participante);									
				}
				//			N U M E R O 		de interacciones de {voz, chat, email}, columnas [D, E y F]
				guardarDatosProcesoPrincipalConfigurarParticipantesNumeroDeIteraciones(conversacionPC,participante);
				if ( participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.AGENT.toString()) ||
					participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.ACD.toString()) ){
					
				}
			}
		}
		conversacionRepository.save(conversacion);
	}
	
	
	private void guardarDatosProcesoPrincipalConfigurarParticipantesNumeroDeIteraciones(
			AnalyticsConversation conversacionPC, AnalyticsParticipant participante) {
		if(Objects.nonNull(participante) && Objects.nonNull(participante.getSessions()) && !participante.getSessions().isEmpty()){
			List<AnalyticsSession> sessions = participante.getSessions();
			for (AnalyticsSession sesion : sessions) {
				if(Objects.nonNull(sesion) && Objects.nonNull(sesion.getSegments()) && !sesion.getSegments().isEmpty()){																
					List<AnalyticsConversationSegment> segments = sesion.getSegments();
					for (AnalyticsConversationSegment segmento : segments) {
						ConversacionesPorTipo conversacionesPorTipo = new ConversacionesPorTipo();
						conversacionesPorTipo.setTipo(sesion.getMediaType().toString());
						//		T I E M P O 		en pausa  columna [J]
						conversacionesPorTipo.setFechaFinSegmento(segmento.getSegmentEnd());
						conversacionesPorTipo.setFechaInicioSegmento(segmento.getSegmentStart());
						conversacionesPorTipo.setIdAgente(participante.getUserId());
						conversacionesPorTipo.setIdConversacion(conversacionPC.getConversationId());
						conversacionesPorTipo.setIdSession(sesion.getSessionId());
						conversacionesPorTipo.setSegmento(segmento.getSegmentType().toString());
						conversacionesPorTipoRepository.save(conversacionesPorTipo);
					}	
				}
			}
		}
	}
	
	private UsersApi guardarDatosProcesoPrincipalConfigurarParticipantesNombreDelAgente(Conversacion conversacion, AnalyticsParticipant participante) throws IOException {
		conversacion.setIdAgente(participante.getUserId());
		UsersApi userApiInstance = new UsersApi();
		//			N O M B R E 		DEL AGENTE, columna [B] 
		try {
			List<String> expand = Arrays.asList("all");
			com.mypurecloud.sdk.v2.model.User userPureCloud = userApiInstance.getUser(participante.getUserId(), expand);
			conversacion.setNombreAgente(NO_NAME);
			if( Objects.nonNull(userPureCloud) &&  Objects.nonNull(userPureCloud.getName()) ){
				conversacion.setNombreAgente(userPureCloud.getName());
			}						
		} catch (ApiException e) {
			logger.error("Exception when calling UsersApi#getUser:  " + e.getMessage(), e);
		}
		return userApiInstance;
	}
	
	private void guardarDatosCrearFiltroConversationQuery(ConversationQuery body,DtoEntrada dto, Integer paginaConversationQuery) {
		body.setInterval(dto.getFechaUno()+"/"+dto.getFechaDos());
		PagingSpec paging = new PagingSpec();
		paging.setPageNumber(paginaConversationQuery);
		paging.setPageSize(Integer.valueOf("100"));
		body.setPaging(paging);
		//--
		List<AnalyticsQueryFilter> segmentFilters = new ArrayList<>();
		AnalyticsQueryFilter filtro = new AnalyticsQueryFilter();
		filtro.setType(com.mypurecloud.sdk.v2.model.AnalyticsQueryFilter.TypeEnum.OR);
		//--
		List<AnalyticsQueryPredicate> predicates = new ArrayList<>(); 
		AnalyticsQueryPredicate predicado = new AnalyticsQueryPredicate();    	
		predicado.setType(com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate.TypeEnum.DIMENSION);
		predicado.setDimension(com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate.DimensionEnum.PURPOSE);
		predicado.setOperator(com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate.OperatorEnum.MATCHES);
		predicado.setValue("agent");
		predicates.add(predicado);
		filtro.setPredicates(predicates);
		segmentFilters.add(filtro);
		body.setSegmentFilters(segmentFilters);
	}

}
