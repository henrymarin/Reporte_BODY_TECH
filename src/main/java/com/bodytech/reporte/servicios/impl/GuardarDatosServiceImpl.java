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

import com.bodytech.reporte.entidades.Conversacion;
import com.bodytech.reporte.entidades.ConversacionesPorTipo;
import com.bodytech.reporte.entidades.EstadosPorAgente;
import com.bodytech.reporte.repositorios.ConversacionRepository;
import com.bodytech.reporte.repositorios.ConversacionesPorTipoRepository;
import com.bodytech.reporte.repositorios.EstadosPorAgenteRepository;
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
import com.mypurecloud.sdk.v2.model.UserDetailsQuery;

@Service
public class GuardarDatosServiceImpl implements GuardarDatosService{

	private static final String URL_MYPURECLOUD = "https://api.mypurecloud.com";
	@Autowired private ConversacionRepository conversacionRepository;
	@Autowired private TaskExecutor taskExecutor;
	@Autowired private EstadosPorAgenteRepository estadosPorAgenteRepository;
	@Autowired private ConversacionesPorTipoRepository conversacionesPorTipoRepository;	
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
							logger.error(e.getMessage());
						}
						//--			            
			        } while (conversationQueryObtuvoResultados);
				    // Print results, including elapsed time
					System.out.println("   °°°°°°Elapsed time in minutes: " + ( (System.currentTimeMillis() - start) * (0.0000167) ) );
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
									logger.error(e.getMessage());
								}
								//fin estado de los agentes						        	
					        } while (userDetailsQueryObtuvoResultados);						
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
					userBody.setInterval("2018-03-08T05:00:00.000Z/2018-03-08T13:00:00.000Z");
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
						logger.error("Exception when calling UsersApi#getUser:  " + e.getMessage());
					}
					return userApiInstance;
				}

				private void guardarDatosCrearFiltroConversationQuery(ConversationQuery body,DtoEntrada dto, Integer paginaConversationQuery) {
					body.setInterval(dto.getFechaUno()+"/"+dto.getFechaDos());
					//body.setInterval("2018-03-06T05:00:00.000Z/2018-03-07T05:00:00.000Z");
					body.setInterval("2018-03-08T05:00:00.000Z/2018-03-08T13:00:00.000Z");					
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

}
