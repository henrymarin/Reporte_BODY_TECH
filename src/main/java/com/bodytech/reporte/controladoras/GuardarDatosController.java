package com.bodytech.reporte.controladoras;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bodytech.reporte.entidades.Conversacion;
import com.bodytech.reporte.repositorios.ConversacionRepository;
import com.javainuse.DtoEntrada;
import com.javainuse.User;
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
			
	@RequestMapping(
			value = "/guardarDatos", 
	method = RequestMethod.POST, 
	consumes ="application/json")
	@CrossOrigin(origins = "*")
	public JSONObject guardarDatos(@RequestBody(required = true) DtoEntrada dto) {
		JSONObject jsonResponse = new JSONObject();
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
	        	conversacion = new Conversacion();
		        List<AnalyticsConversation> conversations = result.getConversations();
		        for (AnalyticsConversation conversacionPC : conversations) {
		        	conversacion.setIdConversacion(conversacionPC.getConversationId());
		        	conversacion.setFechaInicioConversacion(conversacionPC.getConversationStart());
		        	conversacion.setFechaFinConversacion(conversacionPC.getConversationEnd());
		        	//--
		        	if(Objects.nonNull(conversacionPC.getParticipants()) && !conversacionPC.getParticipants().isEmpty()){
			        	List<AnalyticsParticipant> participants = conversacionPC.getParticipants();
						for (AnalyticsParticipant participante : participants) {
							if (participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.AGENT.toString()) ){
								//-- 			I D     D E L     A G E N T E
								conversacion.setIdAgente(participante.getUserId());
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
		
		return jsonResponse;
	}
		
		@RequestMapping(
				value = "/guardarDatos222", 
				method = RequestMethod.POST, 
				consumes ="application/json")
			@CrossOrigin(origins = "*")
			public JSONObject guardarDatos222(@RequestBody(required = true) DtoEntrada dto) {
				JSONObject jsonResponse = new JSONObject();				
				try {
				    // Configure SDK settings
				    String accessToken = dto.getEntradaUno();
				    Configuration.setDefaultApiClient(ApiClient.Builder.standard()
				            .withAccessToken(accessToken)
				            .withBasePath("https://api.mypurecloud.com")
				            .build());
				
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
				    	User n = new User();
				    	//						<<<<--- R E S P O N S E 	ConversationsDetailsQuery---->>>>>
				    	
				        AnalyticsConversationQueryResponse result = apiInstance.postAnalyticsConversationsDetailsQuery(body);
				        List<AnalyticsConversation> conversations = result.getConversations();
				        for (AnalyticsConversation conversacion : conversations) {				        	
				        	//			T I E M P O S 		por intervalo  de {voz, chat, email}, columnas [G, H , I]
							System.out.println(conversacion.getConversationId());
							System.out.println("T I E M P O S 		por intervalo  de {voz, chat, email}, columnas [G, H , I]");
							conversacion.getConversationStart();
							conversacion.getConversationEnd();
							List<AnalyticsParticipant> participants = conversacion.getParticipants();
							for (AnalyticsParticipant participante : participants) {
								
								
				                // Ignore non-agent-tPurpose 
				                if (!participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.AGENT.toString()) ){
				                	continue;
				                }								
								List<AnalyticsSession> sessions = participante.getSessions();
									//-- 			I D     D E L     A G E N T E
								participante.getUserId();
								if(Objects.nonNull(participante) && Objects.nonNull(participante.getUserId())){
									UsersApi userApiInstance = new UsersApi();
						 			//			N O M B R E 		DEL AGENTE, columna [B] 
									try {
										List<String> expand = Arrays.asList("all");
									    //User UserNaeResult = userApiInstance.getUser(participante.getUserId(), expand, "active");
										com.mypurecloud.sdk.v2.model.User userPureCloud = userApiInstance.getUser(participante.getUserId(), expand);
										userPureCloud.getName();
									} catch (ApiException e) {
									    System.err.println("Exception when calling UsersApi#getUser");
									    e.printStackTrace();
									}
									
									// 			E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
									UserDetailsQuery userBody = new UserDetailsQuery(); // UserDetailsQuery | query
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
								    	//						<<<<--- R E S P O N S E postAnalyticsUsersDetailsQuery---->>>>>						
									    AnalyticsUserDetailsQueryResponse userResult = userApiInstance.postAnalyticsUsersDetailsQuery(userBody);
									    List<AnalyticsUserDetail> userDetails = userResult.getUserDetails();					    
									    for (AnalyticsUserDetail userDetail : userDetails) {
									    	//--routingStatus -->>
									    	List<AnalyticsRoutingStatusRecord> routingStatus = userDetail.getRoutingStatus();
									    	for (AnalyticsRoutingStatusRecord routingSt : routingStatus) {
									    		routingSt.getRoutingStatus().toString();
											}
									    	
									    	//--primaryPresences -->>
									    	List<AnalyticsUserPresenceRecord> primaryPresences = userDetail.getPrimaryPresence();
									    	for (AnalyticsUserPresenceRecord presencia : primaryPresences) {
									    			//---		columnas [K, L, C, P, Q] 
												presencia.getSystemPresence().toString();
											}
										}
									    
									} catch (ApiException e) {
									    System.err.println("Exception when calling UsersApi#postAnalyticsUsersDetailsQuery");
									    e.printStackTrace();
									}	
								}
									//FIN			ESTADOS DE LOS AGENTES
								
								
								
								for (AnalyticsSession sesion : sessions) {
									//		N U M E R O 		de interacciones de {voz, chat, email}, columnas [D, E y F]	
									//System.out.println(sesion.getMediaType().toString());
									List<AnalyticsConversationSegment> segments = sesion.getSegments();
									for (AnalyticsConversationSegment segmento : segments) {
										//		T I E M P O 		en pausa  columna [J]
										segmento.getSegmentEnd();
										segmento.getSegmentStart();
										//System.out.println(segmento.getSegmentType().toString());
									}
								}
							}
							n = new User();
							n.setName(conversacion.getConversationId());
							n.setEmail(conversacion.getConversationEnd().toGMTString());
						}
				    } catch (ApiException e) {
				        System.err.println("Exception when calling ConversationsApi#postAnalyticsConversationsDetailsQuery");
				        e.printStackTrace();
				    }
				} catch (Exception e) {
				    e.printStackTrace();
				}
				return jsonResponse;
			}
	
	
}
