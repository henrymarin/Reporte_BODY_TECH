package com.bodytech.reporte.servicios.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
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
import com.mypurecloud.sdk.v2.model.AnalyticsUserDetailsQueryResponse;
import com.mypurecloud.sdk.v2.model.AnalyticsUserPresenceRecord;
import com.mypurecloud.sdk.v2.model.ConversationQuery;
import com.mypurecloud.sdk.v2.model.PagingSpec;
import com.mypurecloud.sdk.v2.model.User;
import com.mypurecloud.sdk.v2.model.UserDetailsQuery;
import com.mypurecloud.sdk.v2.model.UserEntityListing;

@Service
public class GuardarDatosServiceImpl implements GuardarDatosService{

	private static final String URL_MYPURECLOUD = "https://api.mypurecloud.com";
	@Autowired private ConversacionRepository conversacionRepository;
	@Autowired private TaskExecutor taskExecutor;
	@Autowired private EstadosPorAgenteRepository estadosPorAgenteRepository;
	@Autowired private ConversacionesPorTipoRepository conversacionesPorTipoRepository;	
	@Autowired private PrecargaRepository precargaRepository;
	@Autowired private AgenteRepository agenteRepository;
	private static final Logger logger = LoggerFactory.getLogger(GuardarDatosServiceImpl.class);
	
	@Override
	public JSONObject guardarDatos(DtoEntrada dto) {
		
		JSONObject jsonResponse = new JSONObject();
		taskExecutor.execute(new Runnable() {
	            @Override
	            public void run() {
	            	//--I N I C I O		Proceso de eliminacion de registros existente
	            	conversacionRepository.deleteAll();
	            	conversacionesPorTipoRepository.deleteAll();
	            	estadosPorAgenteRepository.deleteAll();	            	
	            	//-- F I N   		Proceso de eliminacion de registros existente
	            	
	            	System.out.println("INICIA LA CARGA!!!");
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
								}else{
									conversationQueryObtuvoResultados = false;
								}
						} catch (ApiException | IOException e) {
							logger.error("erro.", e);
						}
						//--			            
			        } while (conversationQueryObtuvoResultados);
				    // Print results, including elapsed time
					System.out.println("   같같같Elapsed time in minutes: " + ( (System.currentTimeMillis() - start) * (0.0000167) ) );
					System.out.println("FIN DE LA CARGA!!!");
	            }

				private void guardarDatosProcesoPrincipal(AnalyticsConversation conversacionPC, DtoEntrada dto) throws IOException {
					Conversacion conversacion = new Conversacion();
					conversacion.setIdConversacion(conversacionPC.getConversationId());
					conversacion.setFechaInicioConversacion(conversacionPC.getConversationStart());
					conversacion.setFechaFinConversacion(conversacionPC.getConversationEnd());
					//--
					if(Objects.nonNull(conversacionPC.getParticipants()) && !conversacionPC.getParticipants().isEmpty()){
						List<AnalyticsParticipant> participants = conversacionPC.getParticipants();
						for (AnalyticsParticipant participante : participants) {
							guardarDatosProcesoPrincipalConfigurarParticipantes(conversacionPC, conversacion,participante, dto);
						}
					}
					conversacionRepository.save(conversacion);
				}

				private void guardarDatosProcesoPrincipalConfigurarParticipantes(AnalyticsConversation conversacionPC,Conversacion conversacion, AnalyticsParticipant participante, DtoEntrada dto) throws IOException {
					if (participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.AGENT.toString()) ){
						//-- 			I D     D E L     A G E N T E
						if(Objects.nonNull(participante) && Objects.nonNull(participante.getUserId())){
							UsersApi userApiInstance = guardarDatosProcesoPrincipalConfigurarParticipantesNombreDelAgente(conversacion, participante);
							// 			E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
							Integer paginaUserDetailsQuery = 1;
							boolean userDetailsQueryObtuvoResultados = true;
					        do { 
								UserDetailsQuery userBody = guardarDatosProcesoPrincipalConfigurarParticipantesCrearFiltroUserDetail(participante, dto, paginaUserDetailsQuery);													
								try {
							    	//						<<<<--- R E S P O N S E 	postAnalyticsUsersDetailsQuery---->>>>>						
								    AnalyticsUserDetailsQueryResponse userResult = userApiInstance.postAnalyticsUsersDetailsQuery(userBody);
								    if(Objects.nonNull(userResult) && Objects.nonNull(userResult.getUserDetails()) && !userResult.getUserDetails().isEmpty()){
									    List<AnalyticsUserDetail> userDetails = userResult.getUserDetails();					    
									    for (AnalyticsUserDetail userDetail : userDetails) {
									    	guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(conversacionPC, participante, userDetail);
										}
									    paginaUserDetailsQuery += 1;
								    }else{
								    	userDetailsQueryObtuvoResultados = false;
								    }
								} catch (ApiException e) {
									logger.error("erro.", e);
								}								        	
					        } while (userDetailsQueryObtuvoResultados);
					        //fin estado de los agentes						
							//			N U M E R O 		de interacciones de {voz, chat, email}, columnas [D, E y F]	
							guardarDatosProcesoPrincipalConfigurarParticipantesNumeroDeIteraciones(conversacionPC,participante);
						}												
					}
				}

				private void guardarDatosProcesoPrincipalConfigurarParticipantesNumeroDeIteraciones(AnalyticsConversation conversacionPC, AnalyticsParticipant participante) {
					if(Objects.nonNull(participante) && Objects.nonNull(participante.getSessions()) && !participante.getSessions().isEmpty()){
						List<AnalyticsSession> sessions = participante.getSessions();
						for (AnalyticsSession sesion : sessions) {
							ConversacionesPorTipo conversacionesPorTipo = new ConversacionesPorTipo(); 
							if(Objects.nonNull(sesion) && Objects.nonNull(sesion.getSegments()) && !sesion.getSegments().isEmpty()){
								conversacionesPorTipo.setTipo(sesion.getMediaType().toString());																
								List<AnalyticsConversationSegment> segments = sesion.getSegments();
								for (AnalyticsConversationSegment segmento : segments) {
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

				private void guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(AnalyticsConversation conversacionPC, AnalyticsParticipant participante,AnalyticsUserDetail userDetail) {
					EstadosPorAgente estadosPorAgente = new EstadosPorAgente(); 
					//--routingStatus -->>
					if(Objects.nonNull(userDetail.getRoutingStatus()) && !userDetail.getRoutingStatus().isEmpty()){
						List<AnalyticsRoutingStatusRecord> routingStatus = userDetail.getRoutingStatus();
						for (AnalyticsRoutingStatusRecord routingSt : routingStatus) {															    		
							estadosPorAgente.setEstado(routingSt.getRoutingStatus().toString());
							estadosPorAgente.setFechaFinEstado(routingSt.getEndTime());
							estadosPorAgente.setFechaInicioEstado(routingSt.getStartTime());
							estadosPorAgente.setIdAgente(participante.getUserId());
							estadosPorAgente.setIdConversacion(conversacionPC.getConversationId());
							estadosPorAgenteRepository.save(estadosPorAgente);
						}	
					}
					
					//--primaryPresences -->>
					List<AnalyticsUserPresenceRecord> primaryPresences = userDetail.getPrimaryPresence();
					for (AnalyticsUserPresenceRecord presencia : primaryPresences) {
							//---		columnas [K, L, C, P, Q]
						estadosPorAgente.setEstado(presencia.getSystemPresence().toString());
						estadosPorAgente.setFechaFinEstado(presencia.getEndTime());
						estadosPorAgente.setFechaInicioEstado(presencia.getStartTime());
						estadosPorAgente.setIdAgente(participante.getUserId());
						estadosPorAgente.setIdConversacion(conversacionPC.getConversationId());
						estadosPorAgenteRepository.save(estadosPorAgente);
					}
				}

				private UserDetailsQuery guardarDatosProcesoPrincipalConfigurarParticipantesCrearFiltroUserDetail(AnalyticsParticipant participante,DtoEntrada dto, Integer paginaUserDetailsQuery) {
					UserDetailsQuery userBody = new UserDetailsQuery(); // UserDetailsQuery | query
					userBody.setInterval(dto.getFechaUno()+"/"+dto.getFechaDos());
					//userBody.setInterval("2018-03-06T05:00:00.000Z/2018-03-07T05:00:00.000Z");
					//userBody.setInterval("2018-03-08T05:00:00.000Z/2018-03-08T13:00:00.000Z");
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
					userPredicate.setValue(participante.getUserId());
					userPredicates.add(userPredicate);
					userFilter.setPredicates(userPredicates);
					userFilters.add(userFilter);
					userBody.setUserFilters(userFilters);
					return userBody;
				}

				private UsersApi guardarDatosProcesoPrincipalConfigurarParticipantesNombreDelAgente(Conversacion conversacion, AnalyticsParticipant participante) throws IOException {
					conversacion.setIdAgente(participante.getUserId());
					UsersApi userApiInstance = new UsersApi();
					//			N O M B R E 		DEL AGENTE, columna [B] 
					try {
						List<String> expand = Arrays.asList("all");
						com.mypurecloud.sdk.v2.model.User userPureCloud = userApiInstance.getUser(participante.getUserId(), expand);
						conversacion.setNombreAgente(userPureCloud.getName());
					} catch (ApiException e) {
						logger.error("Exception when calling UsersApi#getUser:  " + e.getMessage(), e);
					}
					return userApiInstance;
				}

				private void guardarDatosCrearFiltroConversationQuery(ConversationQuery body,DtoEntrada dto, Integer paginaConversationQuery) {
					body.setInterval(dto.getFechaUno()+"/"+dto.getFechaDos());
					//body.setInterval("2018-03-06T05:00:00.000Z/2018-03-07T05:00:00.000Z");
					//body.setInterval("2018-03-08T05:00:00.000Z/2018-03-08T13:00:00.000Z");					
					//--
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
	        });
		return jsonResponse;
		
	}
	
	
	@Override
	public JSONObject guardarDatosDepurado(DtoEntrada dto) {
		
		JSONObject jsonResponse = new JSONObject();
		taskExecutor.execute(new Runnable() {
	            @Override
	            public void run() {
	            	//--I N I C I O		Proceso de eliminacion de registros existente
	            	conversacionRepository.deleteAll();
	            	conversacionesPorTipoRepository.deleteAll();
	            	precargaRepository.deleteAll();
	            	//-- F I N   		Proceso de eliminacion de registros existente
	            	//--
	            	Precarga precarga = new Precarga();
	            	precarga.setFechaInicio(dto.getFechaUno());
	            	precarga.setFechaFin(dto.getFechaDos());
					precarga.setEstado("Completa");
					precargaRepository.save(precarga);
	            	//--
	            	System.out.println("<<<<----guardarDatosDepurado--->>>> INICIA LA PRIMERA CARGA!!!");
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
								}else{
									conversationQueryObtuvoResultados = false;
								}
						} catch (ApiException | IOException e) {
							logger.error("erro.", e);
						}
						//--			            
			        } while (conversationQueryObtuvoResultados);
				    // Print results, including elapsed time
					System.out.println("   같같같Elapsed time in minutes: " + ( (System.currentTimeMillis() - start) * (0.0000167) ) );
					System.out.println("FIN DE LA 1era CARGA!!!");					
					//--
					taskExecutor.execute(new Runnable() {
			            @Override
			            public void run() {
			            	estadosPorAgenteRepository.deleteAll();	 
			            	System.out.println("<<<<----guardarDatosDepurado--->>>> INICIA LA SEGUNDA CARGA  <<<----ESTADOS DEL AGENTE--->>>>!!!");
			            	List<Conversacion> listadoDeConversaciones =  (List<Conversacion>) conversacionRepository.findAll();
			            	if(Objects.nonNull(listadoDeConversaciones) && !listadoDeConversaciones.isEmpty()){
				            	for (Conversacion conversacion : listadoDeConversaciones) {
									// 			E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
									Integer paginaUserDetailsQuery = 1;
									boolean userDetailsQueryObtuvoResultados = true;
							        do {
//--

										UserDetailsQuery userBody = guardarDatosProcesoPrincipalConfigurarParticipantesCrearFiltroUserDetail(conversacion.getIdAgente(), dto, paginaUserDetailsQuery);
										try {
											UsersApi userApiInstance = new UsersApi();
									    	//						<<<<--- R E S P O N S E 	postAnalyticsUsersDetailsQuery---->>>>>						
										    AnalyticsUserDetailsQueryResponse userResult = userApiInstance.postAnalyticsUsersDetailsQuery(userBody);
										    if(Objects.nonNull(userResult) && Objects.nonNull(userResult.getUserDetails()) && !userResult.getUserDetails().isEmpty()){
											    List<AnalyticsUserDetail> userDetails = userResult.getUserDetails();
if(conversacion.getIdAgente().equalsIgnoreCase("0d5fc836-390d-4976-b06e-89b8d2f90191")){
	System.out.println("XXXXXXXXXXXXXXXXXXXXXXX__getIdConversacion:" + conversacion.getIdConversacion() + " userDetails:" + userDetails);
}											    
											    for (AnalyticsUserDetail userDetail : userDetails) {
											    	guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(conversacion.getIdConversacion(), conversacion.getIdAgente(), userDetail);
												}
											    paginaUserDetailsQuery += 1;
										    }else{
										    	userDetailsQueryObtuvoResultados = false;
										    }
										} catch (ApiException | IOException e) {
											logger.error("E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]" + e.getMessage(), e);
										}								        	
							        } while (userDetailsQueryObtuvoResultados);
							        //fin estado de los agentes
				            	}
							    // Print results, including elapsed time
								System.out.println("   같같같Elapsed time in minutes: " + ( (System.currentTimeMillis() - start) * (0.0000167) ) );
								System.out.println("FIN DE LA 2da CARGA!!!");
			            	}			            	
			            }
			        });	
	            }

				private void guardarDatosProcesoPrincipal(AnalyticsConversation conversacionPC, DtoEntrada dto) throws IOException {
					Conversacion conversacion = new Conversacion();
					conversacion.setIdConversacion(conversacionPC.getConversationId());
					conversacion.setFechaInicioConversacion(conversacionPC.getConversationStart());
					conversacion.setFechaFinConversacion(conversacionPC.getConversationEnd());
					if(Objects.nonNull(conversacionPC.getParticipants()) && !conversacionPC.getParticipants().isEmpty()){
						List<AnalyticsParticipant> participants = conversacionPC.getParticipants();
						for (AnalyticsParticipant participante : participants) {
							if (participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.AGENT.toString()) ){
								//-- 			I D     D E L     A G E N T E
								if(Objects.nonNull(participante) && Objects.nonNull(participante.getUserId())){
									guardarDatosProcesoPrincipalConfigurarParticipantesNombreDelAgente(conversacion, participante);									
								}
								//			N U M E R O 		de interacciones de {voz, chat, email}, columnas [D, E y F]
								guardarDatosProcesoPrincipalConfigurarParticipantesNumeroDeIteraciones(conversacionPC,participante);
							}
						}
					}
					conversacionRepository.save(conversacion);
				}
				
				private UsersApi guardarDatosProcesoPrincipalConfigurarParticipantesNombreDelAgente(Conversacion conversacion, AnalyticsParticipant participante) throws IOException {
					conversacion.setIdAgente(participante.getUserId());
					UsersApi userApiInstance = new UsersApi();
					//			N O M B R E 		DEL AGENTE, columna [B] 
					try {
						List<String> expand = Arrays.asList("all");
						com.mypurecloud.sdk.v2.model.User userPureCloud = userApiInstance.getUser(participante.getUserId(), expand);
						conversacion.setNombreAgente("no name");
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
				
				private void guardarDatosProcesoPrincipalConfigurarParticipantesNumeroDeIteraciones(AnalyticsConversation conversacionPC, AnalyticsParticipant participante) {
					if(Objects.nonNull(participante) && Objects.nonNull(participante.getSessions()) && !participante.getSessions().isEmpty()){
						List<AnalyticsSession> sessions = participante.getSessions();
						for (AnalyticsSession sesion : sessions) {
							ConversacionesPorTipo conversacionesPorTipo = new ConversacionesPorTipo(); 
							if(Objects.nonNull(sesion) && Objects.nonNull(sesion.getSegments()) && !sesion.getSegments().isEmpty()){
								conversacionesPorTipo.setTipo(sesion.getMediaType().toString());																
								List<AnalyticsConversationSegment> segments = sesion.getSegments();
								for (AnalyticsConversationSegment segmento : segments) {
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
				
				private void guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(String conversacionId, String agenteId,AnalyticsUserDetail userDetail) {					
					EstadosPorAgente estadosPorAgente = new EstadosPorAgente(); 
					//--routingStatus -->>
					if(Objects.nonNull(userDetail.getRoutingStatus()) && !userDetail.getRoutingStatus().isEmpty()){
						List<AnalyticsRoutingStatusRecord> routingStatus = userDetail.getRoutingStatus();
						for (AnalyticsRoutingStatusRecord routingSt : routingStatus) {															    		
							estadosPorAgente.setEstado(routingSt.getRoutingStatus().toString());
							estadosPorAgente.setFechaFinEstado(routingSt.getEndTime());
							estadosPorAgente.setFechaInicioEstado(routingSt.getStartTime());
							estadosPorAgente.setIdAgente(agenteId);
							estadosPorAgente.setIdConversacion(conversacionId);
							estadosPorAgenteRepository.save(estadosPorAgente);
						}	
					}
					
					//--primaryPresences -->>
					List<AnalyticsUserPresenceRecord> primaryPresences = userDetail.getPrimaryPresence();
					for (AnalyticsUserPresenceRecord presencia : primaryPresences) {
							//---		columnas [K, L, C, P, Q]
						estadosPorAgente.setEstado(presencia.getSystemPresence().toString());
						estadosPorAgente.setFechaFinEstado(presencia.getEndTime());
						estadosPorAgente.setFechaInicioEstado(presencia.getStartTime());
						estadosPorAgente.setIdAgente(agenteId);
						estadosPorAgente.setIdConversacion(conversacionId);
						estadosPorAgenteRepository.save(estadosPorAgente);
					}
				}
				
				

	        });
		return jsonResponse;
		
	}


	@Override
	public PrecargaResponse obtenerInformacionDeLaPrecarga() {

		PrecargaResponse rta = new PrecargaResponse();
		rta.setEstado("Sin estado");
		rta.setFechaFin("");
		rta.setFechaInicio("");
		List<Precarga> listadoDePrecargas = (List<Precarga>) precargaRepository.findAll();
		if(Objects.nonNull(listadoDePrecargas) && !listadoDePrecargas.isEmpty()){
			Precarga precarga = listadoDePrecargas.get(0);
			rta.setEstado(precarga.getEstado());
			rta.setFechaFin(precarga.getFechaFin());
			rta.setFechaInicio(precarga.getFechaInicio());
		}		
		return rta;
	}
	
	
	
	@Override
	public JSONObject guardarDatosDepuradoConCargaDeAgentes(DtoEntrada dto) {
		
		JSONObject jsonResponse = new JSONObject();
		taskExecutor.execute(new Runnable() {
	            @Override
	            public void run() {
	            	long startTotal = System.currentTimeMillis();
	            	System.out.println("        <<<<----INICIO DEL PROCESO DE LA CARGA--->>>>");
	            	//--I N I C I O		Proceso de eliminacion de registros existente
	            	conversacionRepository.deleteAll();
	            	conversacionesPorTipoRepository.deleteAll();
	            	precargaRepository.deleteAll();
	            	//-- F I N   		Proceso de eliminacion de registros existente
	            	//--
	            	Precarga precarga = new Precarga();
	            	precarga.setFechaInicio(dto.getFechaUno());
	            	precarga.setFechaFin(dto.getFechaDos());
					precarga.setEstado("Completa");
					precargaRepository.save(precarga);
	            	//--
					System.out.println("        		<<<<----PROCESO DE LA CARGA DE CONVERSACIONES--->>>>");
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
								}else{
									conversationQueryObtuvoResultados = false;
								}
						} catch (ApiException | IOException e) {
							logger.error("erro.", e);
						}
						//--			            
			        } while (conversationQueryObtuvoResultados);
				    // Print results, including elapsed time
					System.out.println("   같같같Elapsed time in minutes: " + ( (System.currentTimeMillis() - start) * (0.0000167) ) );
					System.out.println("같같같같같FIN DE LA CARGA DE CONVERSACIONES");					
					//--
					taskExecutor.execute(new Runnable() {
			            @Override
			            public void run() {
			            	long startAgentes = System.currentTimeMillis();
			            	agenteRepository.deleteAll();	 
			            	System.out.println("        		<<<<----PROCESO DE LA CARGA DE AGENTES--->>>>");
							//			INFORMACION DE TODOS LOS AGENTES
							UsersApi apiInstance = new UsersApi();
							Integer pageSize = 100; // Integer | Page size
							String sortOrder = "ASC"; // String | Ascending or descending sort order
							try {
								Integer paginaGetUsersQuery = 1;
								boolean getUsersQueryObtuvoResultados = true;
						        do {
									UserEntityListing entidadConUsuarios = apiInstance.getUsers(pageSize, paginaGetUsersQuery, new ArrayList<>(), sortOrder, new ArrayList<>());
									if(Objects.nonNull(entidadConUsuarios) && Objects.nonNull(entidadConUsuarios.getEntities()) && !entidadConUsuarios.getEntities().isEmpty()){
									    List<User> usuarios = entidadConUsuarios.getEntities();
									    for (User user : usuarios) {
											Agente agente = new Agente();
											agente.setIdAgente(user.getId());
											agente.setNombreAgente("no name");
											agente.setTipoAgente("no type");
											if(Objects.nonNull(user.getName())){
												agente.setNombreAgente(user.getName());
												if(user.getName().toUpperCase().contains("MESA")){
													agente.setTipoAgente("MDA");
												}else{
													agente.setTipoAgente("SAC");
												}
											}
											agenteRepository.save(agente);
										}
									}else{
										getUsersQueryObtuvoResultados = false;
									}
						        	paginaGetUsersQuery += 1;
						        }while (getUsersQueryObtuvoResultados);
							    // Print results, including elapsed time
								System.out.println("   같같같Elapsed time in minutes: " + ( (System.currentTimeMillis() - startAgentes) * (0.0000167) ) );
								System.out.println("같같같같같FIN DE LA CARGA DE LA CARGA DE AGENTES");
							} catch (ApiException | IOException e) {
								logger.error("Exception when calling UsersApi#getUsers", e);
							}
							//FIN poceso de carga de agentes

							//inicio E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
			            	long startEstadosAgentes = System.currentTimeMillis();
			            	estadosPorAgenteRepository.deleteAll();	 
			            	System.out.println("        		<<<<----PROCESO DE LA CARGA DE ESTADOS DE LOS AGENTES--->>>>");
			            	List<Agente> listadoDeAgentes =  (List<Agente>) agenteRepository.findAll();
			            	if(Objects.nonNull(listadoDeAgentes) && !listadoDeAgentes.isEmpty()){
				            	for (Agente agente : listadoDeAgentes) {
									// 			E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
									Integer paginaUserDetailsQuery = 1;
									boolean userDetailsQueryObtuvoResultados = true;
							        do {										
							        	UserDetailsQuery userBody = guardarDatosProcesoPrincipalConfigurarParticipantesCrearFiltroUserDetail(agente.getIdAgente(), dto, paginaUserDetailsQuery);
										try {
											UsersApi userApiInstance = new UsersApi();
									    	//						<<<<--- R E S P O N S E 	postAnalyticsUsersDetailsQuery---->>>>>						
										    AnalyticsUserDetailsQueryResponse userResult = userApiInstance.postAnalyticsUsersDetailsQuery(userBody);
										    if(Objects.nonNull(userResult) && Objects.nonNull(userResult.getUserDetails()) && !userResult.getUserDetails().isEmpty()){
											    List<AnalyticsUserDetail> userDetails = userResult.getUserDetails();											    
											    for (AnalyticsUserDetail userDetail : userDetails) {
											    	guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(agente.getId().toString(), agente.getIdAgente(), userDetail);
												}
											    paginaUserDetailsQuery += 1;
										    }else{
										    	userDetailsQueryObtuvoResultados = false;
										    }
										} catch (ApiException | IOException e) {
											logger.error("E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]" + e.getMessage(), e);
										}								        	
							        } while (userDetailsQueryObtuvoResultados);
							        //fin estado de los agentes
				            	}
							    // Print results, including elapsed time
								System.out.println("   같같같Elapsed time in minutes: " + ( (System.currentTimeMillis() - startEstadosAgentes) * (0.0000167) ) );
								System.out.println("같같같같같FIN DE LA CARGA DE ESTADOS POR AGENTE");
			            	}						
			            }
			        });
					System.out.println("   같같같Elapsed time in minutes: " + ( (System.currentTimeMillis() - startTotal) * (0.0000167) ) );
					System.out.println("같같같같같FIN DE LAX CARGAX");
	            }

				private void guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(String conversacionId, String agenteId,AnalyticsUserDetail userDetail) {					
					EstadosPorAgente estadosPorAgente = new EstadosPorAgente(); 
					//--routingStatus -->>
					if(Objects.nonNull(userDetail.getRoutingStatus()) && !userDetail.getRoutingStatus().isEmpty()){
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
					if(Objects.nonNull(userDetail.getPrimaryPresence()) && !userDetail.getPrimaryPresence().isEmpty()){
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
				
				private UsersApi guardarDatosProcesoPrincipalConfigurarParticipantesNombreDelAgente(Conversacion conversacion, AnalyticsParticipant participante) throws IOException {
					conversacion.setIdAgente(participante.getUserId());
					UsersApi userApiInstance = new UsersApi();
					//			N O M B R E 		DEL AGENTE, columna [B] 
					try {
						List<String> expand = Arrays.asList("all");
						com.mypurecloud.sdk.v2.model.User userPureCloud = userApiInstance.getUser(participante.getUserId(), expand);
						conversacion.setNombreAgente("no name");
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
				
				private void guardarDatosProcesoPrincipalConfigurarParticipantesNumeroDeIteraciones(AnalyticsConversation conversacionPC, AnalyticsParticipant participante) {
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
	        });
		return jsonResponse;
		
	}

}
