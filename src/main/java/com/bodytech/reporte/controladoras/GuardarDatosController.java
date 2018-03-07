package com.bodytech.reporte.controladoras;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bodytech.reporte.entidades.Conversacion;
import com.bodytech.reporte.entidades.ConversacionesPorTipo;
import com.bodytech.reporte.entidades.EstadosPorAgente;
import com.bodytech.reporte.repositorios.ConversacionRepository;
import com.bodytech.reporte.repositorios.ConversacionesPorTipoRepository;
import com.bodytech.reporte.repositorios.EstadosPorAgenteRepository;
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

import net.minidev.json.JSONObject;

@Controller
public class GuardarDatosController {
	
	@Autowired private ConversacionRepository conversacionRepository;
	@Autowired private TaskExecutor taskExecutor;
	@Autowired private EstadosPorAgenteRepository estadosPorAgenteRepository;
	@Autowired private ConversacionesPorTipoRepository conversacionesPorTipoRepository;	
			
	@RequestMapping(
			value = "/guardarDatos", 
			method = RequestMethod.POST, 
			consumes ="application/json")
	@CrossOrigin(origins = "*")
	public JSONObject guardarDatos(@RequestBody(required = true) DtoEntrada dto) {
		JSONObject jsonResponse = new JSONObject();		
		
		 taskExecutor.execute(new Runnable() {
	            @Override
	            public void run() {
					Conversacion conversacion = new Conversacion();
					// Configure SDK settings
					String accessToken = dto.getEntradaUno();
					Configuration.setDefaultApiClient(ApiClient.Builder.standard().withAccessToken(accessToken).withBasePath("https://api.mypurecloud.com").build());
					
					// Instantiate APIs ConversationsApi
					ConversationsApi apiInstance = new ConversationsApi();
					//--
					ConversationQuery body = new ConversationQuery(); // ConversationQuery | query
					body.setInterval("2018-03-06T05:00:00.000Z/2018-03-07T05:00:00.000Z");
					//--
					PagingSpec paging = new PagingSpec();
					paging.setPageNumber(Integer.valueOf("1"));
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
					    try {
					    	//						<<<<--- R E S P O N S E 	ConversationsDetailsQuery---->>>>>	    	
							AnalyticsConversationQueryResponse result = apiInstance.postAnalyticsConversationsDetailsQuery(body);
							if(Objects.nonNull(result) && Objects.nonNull(result.getConversations()) && !result.getConversations().isEmpty()){
								List<AnalyticsConversation> conversations = result.getConversations();
								for (AnalyticsConversation conversacionPC : conversations) {
									conversacion = new Conversacion();
									conversacion.setIdConversacion(conversacionPC.getConversationId());
									conversacion.setFechaInicioConversacion(conversacionPC.getConversationStart());
									conversacion.setFechaFinConversacion(conversacionPC.getConversationEnd());
									//--
									if(Objects.nonNull(conversacionPC.getParticipants()) && !conversacionPC.getParticipants().isEmpty()){
										List<AnalyticsParticipant> participants = conversacionPC.getParticipants();
										for (AnalyticsParticipant participante : participants) {
											if (participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.AGENT.toString()) ){
												//-- 			I D     D E L     A G E N T E
												if(Objects.nonNull(participante) && Objects.nonNull(participante.getUserId())){
													conversacion.setIdAgente(participante.getUserId());
													UsersApi userApiInstance = new UsersApi();
										 			//			N O M B R E 		DEL AGENTE, columna [B] 
													try {
														List<String> expand = Arrays.asList("all");
														com.mypurecloud.sdk.v2.model.User userPureCloud = userApiInstance.getUser(participante.getUserId(), expand);
														conversacion.setNombreAgente(userPureCloud.getName());
													} catch (ApiException e) {
													    System.err.println("Exception when calling UsersApi#getUser");
													    e.printStackTrace();
													}
													
													// 			E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
													UserDetailsQuery userBody = new UserDetailsQuery(); // UserDetailsQuery | query
													userBody.setInterval("2018-03-06T05:00:00.000Z/2018-03-07T05:00:00.000Z");					
													userBody.setInterval("2018-03-06T05:00:00.000Z/2018-03-07T05:00:00.000Z");
												    //--
													PagingSpec UserPaging = new PagingSpec();
													UserPaging.setPageNumber(Integer.valueOf("1"));
													UserPaging.setPageSize(Integer.valueOf("100"));
													userBody.setPaging(UserPaging);
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
													try {
												    	//						<<<<--- R E S P O N S E 	postAnalyticsUsersDetailsQuery---->>>>>						
													    AnalyticsUserDetailsQueryResponse userResult = userApiInstance.postAnalyticsUsersDetailsQuery(userBody);
													    if(Objects.nonNull(userResult) && Objects.nonNull(userResult.getUserDetails()) && !userResult.getUserDetails().isEmpty()){
														    List<AnalyticsUserDetail> userDetails = userResult.getUserDetails();					    
														    for (AnalyticsUserDetail userDetail : userDetails) {
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
													    }													    
													} catch (ApiException e) {
													    System.err.println("Exception when calling UsersApi#postAnalyticsUsersDetailsQuery");
													    e.printStackTrace();
													}
													//fin estado de los agentes
													
													//			N U M E R O 		de interacciones de {voz, chat, email}, columnas [D, E y F]	
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
											}
										}
									}
									conversacionRepository.save(conversacion);
								}	        	
							}
					} catch (ApiException | IOException e) {
					    System.err.println("Exception when calling ConversationsApi#postAnalyticsConversationsDetailsQuery");
					    e.printStackTrace();
					}	               
	            }
	        });
		return jsonResponse;
	}	
	
}
