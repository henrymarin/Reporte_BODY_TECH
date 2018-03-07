package com.javainuse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bodytech.reporte.entidades.User;
import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.Configuration;
import com.mypurecloud.sdk.v2.api.ConversationsApi;
import com.mypurecloud.sdk.v2.api.PresenceApi;
import com.mypurecloud.sdk.v2.api.UsersApi;
import com.mypurecloud.sdk.v2.api.request.GetPresencedefinitionsRequest;
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
import com.mypurecloud.sdk.v2.model.OrganizationPresence;
import com.mypurecloud.sdk.v2.model.OrganizationPresenceEntityListing;
import com.mypurecloud.sdk.v2.model.PagingSpec;
import com.mypurecloud.sdk.v2.model.UserDetailsQuery;
import com.mypurecloud.sdk.v2.model.UserMe;

import net.minidev.json.JSONObject;

@Controller
public class TestController {
	
	
	@RequestMapping("/welcome.html")
	public ModelAndView firstPage() {
		return new ModelAndView("welcome");
	}
	
	@RequestMapping("/welcome2.html")
	public ModelAndView firstPage2() {
		return new ModelAndView("welcome2");
	}

	@RequestMapping(
			value = "/probarSDK", 
			method = RequestMethod.POST, 
			consumes ="application/json")
	@CrossOrigin(origins = "*")
	public JSONObject main(@RequestBody(required = true) DtoEntrada dto) {
		JSONObject jsonResponse = new JSONObject();
        try {
            // Configure SDK settings
            String accessToken = dto.getEntradaUno();
            Configuration.setDefaultApiClient(ApiClient.Builder.standard()
                    .withAccessToken(accessToken)
                    .withBasePath("https://api.mypurecloud.com")
                    .build());

            // Instantiate APIs
            UsersApi usersApi = new UsersApi();

            // Validate token with GET /api/v2/users/me (throws an exception if unauthorized)
            UserMe me = usersApi.getUsersMe(Arrays.asList("presence"));
            System.out.println("<<<<------XXXXXXXXXXX----------->>>>");
            System.out.println("Hello " + me.getName());
            System.out.println("<<<<------XXXXXXXXXXX----------->>>>");
            System.out.print("Application complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
	
		@RequestMapping(
			value = "/iniciarProcesoDePrecarga", 
			method = RequestMethod.POST, 
			consumes ="application/json")
		@CrossOrigin(origins = "*")
		public JSONObject iniciarProcesoDePrecarga(@RequestBody(required = true) DtoEntrada dto) {
			JSONObject jsonResponse = new JSONObject();
			try {
			    // Configure SDK settings
			    String accessToken = dto.getEntradaUno();
			    Configuration.setDefaultApiClient(ApiClient.Builder.standard()
			            .withAccessToken(accessToken)
			            .withBasePath("https://api.mypurecloud.com")
			            .build());
			
			    // Instantiate APIs
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
			    	//						<<<<--- R E S P O N S E postAnalyticsConversationsDetailsQuery---->>>>>
			    	
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
							List<AnalyticsSession> sessions = participante.getSessions();
							
							
								//-- 			I D     D E L     A G E N T E
							participante.getUserId();
							if(Objects.nonNull(participante) && Objects.nonNull(participante.getUserId())){
								// 			E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q] 
								UsersApi userApiInstance = new UsersApi();
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
		
		@GetMapping(path="/add") // Map ONLY GET Requests
		public @ResponseBody String addNewUser (@RequestParam String name , @RequestParam String email) {
			User n = new User();
			n.setName(name);
			n.setEmail(email);
			return "Saved";
		}
		
		@RequestMapping(
				value = "/iniciarProcesoDePrecarga2", 
				method = RequestMethod.POST, 
				consumes ="application/json")
			@CrossOrigin(origins = "*")
			public JSONObject iniciarProcesoDePrecarga2(@RequestBody(required = true) DtoEntrada dto) {
				JSONObject jsonResponse = new JSONObject();
				try {
				    // Configure SDK settings
				    String accessToken = dto.getEntradaUno();
				    Configuration.setDefaultApiClient(ApiClient.Builder.standard()
				            .withAccessToken(accessToken)
				            .withBasePath("https://api.mypurecloud.com")
				            .build());
				    
				    
				    // Get presences
				    PresenceApi presenceApi = new PresenceApi();
		            GetPresencedefinitionsRequest presencedefinitionsRequest = GetPresencedefinitionsRequest.builder()
		                    .withPageNumber(1)
		                    .withPageSize(25)
		                    .build();
		            OrganizationPresenceEntityListing presences = presenceApi.getPresencedefinitions(presencedefinitionsRequest);

		            // Find Available and Break org presences
		            String availablePresence = "0";
		            String breakPresence = "0";
		            for (int i = 0; i < presences.getEntities().size(); i++) {
		                OrganizationPresence presence = presences.getEntities().get(i);

		                // Ignore non-primary (i.e. user-defined presences) for this tutorial
		                	if (!presence.getPrimary()) continue;

		                // Check system presences
		                if (presence.getSystemPresence().equalsIgnoreCase("available")) {
		                	availablePresence = presence.getId();
		                } else if (presence.getSystemPresence().equalsIgnoreCase("break")) {		                    
		                	breakPresence = presence.getId();
		                }
		            }
				
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
