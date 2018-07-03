package com.bodytech.reporte.servicios.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import com.mypurecloud.sdk.v2.model.AnalyticsUserDetailsQueryResponse;
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
	

	@Override
	public  void guardarDatosDepuradoConCargaDeAgentes(DtoEntrada dto) {
		
		
			//--1

        	long startTotal = System.currentTimeMillis();
        	System.out.println("<<<<<<<-------------------------------------OO------------------------------------->>>>>>");
        	System.out.println("<                                                                                       >");
        	System.out.println("<                                        MAYO 25                                        >");
        	System.out.println("<                                                                                       >");
        	System.out.println("<<<<<<<-------------------------------------OO------------------------------------->>>>>>");
        	System.out.println("        INICIO DEL PROCESO DE LA CARGA_00001002------------------------------------>>>>>>");
        	System.out.println("                                                                                       ");
        	System.out.println("                                                                                       ");
        	System.out.println("Intervalo de Fechas---->>  " + dto.getFechaUno()+"/"+dto.getFechaDos());
        	System.out.println("                                                                                       ");
        	System.out.println("                                                                                       ");
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
        	// Start the clock
        	long start = System.currentTimeMillis();
			// Configure SDK settings
			String accessToken = dto.getEntradaUno();
			Configuration.setDefaultApiClient(ApiClient.Builder.standard().withAccessToken(accessToken).withBasePath(URL_MYPURECLOUD).build());
			
			// Instantiate APIs ConversationsApi
			ConversationsApi apiInstance = new ConversationsApi();
			//--
			
			//--
			//			INFORMACION DE TODOS LOS AGENTES
			UsersApi UsersApiInstance = new UsersApi();
			Integer pageSize = 100; // Integer | Page size
			String sortOrder = "ASC"; // String | Ascending or descending sort order
			List<User> agentesPureC = new ArrayList<>();
			try {	
				Integer paginaGetUsersQuery = 1;
				boolean getUsersQueryObtuvoResultados = true;
				        do {
							UserEntityListing entidadConUsuarios = UsersApiInstance .getUsers(pageSize, paginaGetUsersQuery, new ArrayList<>(), sortOrder, new ArrayList<>());
							if(Objects.nonNull(entidadConUsuarios) && Objects.nonNull(entidadConUsuarios.getEntities()) && !entidadConUsuarios.getEntities().isEmpty()){
								agentesPureC = entidadConUsuarios.getEntities();				    
							    
					}else{
						getUsersQueryObtuvoResultados = false;
					}
			    	paginaGetUsersQuery += 1;
			    }while (getUsersQueryObtuvoResultados);	
			}catch (ApiException e) {System.out.println("Error_000000014.444: " + e);}				
			//--			
			 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			//---
			Integer paginaConversationQuery = 1;
			boolean conversationQueryObtuvoResultados = true;
			int totalConversacionesInsertadas = 0;
			int maximosRegistrosAInsertar = 250;
			int totalDeConversacionesInsertadasConteo = 0;
			int vecesEsperandoUno = 1;
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
								try{								
								totalDeConversacionesInsertadasConteo = totalDeConversacionesInsertadasConteo + 
										guardarDatosProcesoPrincipal(conversacionPC, dto, agentesPureC);
								//--
								totalConversacionesInsertadas+=1;
								if( totalConversacionesInsertadas==maximosRegistrosAInsertar ){
									try {
										//cada n saves espera 30 segundos (30000 ==  a 1 minuto y medio; 60000 == a un minuto)
										//cada n saves espera 30 segundos (30000 ==  a 30 segundos; 60000 == a 60 segundos, 15000 == a 15 segundos)
										totalConversacionesInsertadas = 0;
										System.out.println("---------------------------------------------------------------------INICIANDO la 1er ESPERA ("+vecesEsperandoUno+") , y van " + totalDeConversacionesInsertadasConteo + " conversaciones insertadas acumuladas");
										vecesEsperandoUno = vecesEsperandoUno + 1;
										Thread.sleep(10000);
									} catch (InterruptedException e) {
										System.out.println("Error_00000001: " + e);
									}
								}
								//--
								}catch(Exception w){System.out.println("Error_00000002: " + w);}
							}
							paginaConversationQuery+=1;
						}else{
							conversationQueryObtuvoResultados = false;
						}
				} catch (ApiException | IOException e) {
					System.out.println("Error_00000003: " + e);
				}
				//--			            
	        } while (conversationQueryObtuvoResultados);
	        
	        System.out.println("------------------------------------------------------------------------");		
	        System.out.println("                                                                                ");
	        System.out.println("                                                                                ");

	        System.out.println("T O T A L    de    C O N V E R S  A C I O N E S:  " + totalDeConversacionesInsertadasConteo);

	        System.out.println("                                                                                ");
	        System.out.println("                                                                                ");
	        System.out.println("------------------------------------------------------------------------");
	        
	        
		    // Print results, including elapsed time
			System.out.println("	FIN DE LA CARGA DE CONVERSACIONES Y DE SUS TIPOS; " + "   Tiempo de Carga: " + ( (System.currentTimeMillis() - start) * (0.0000167) ));					
			//--

        	System.out.println("<                                                                                       >");
        	System.out.println("<                                                                                       >");
        	
        	long startAgentes = System.currentTimeMillis();
        	agenteRepository.deleteAll();	 
        	System.out.println("        		PROCESO DE LA CARGA DE AGENTES");
			//			INFORMACION DE TODOS LOS AGENTES
/*			UsersApi UsersApiInstance = new UsersApi();
			Integer pageSize = 100; // Integer | Page size
			String sortOrder = "ASC"; // String | Ascending or descending sort order */
			try {
				Integer paginaGetUsersQuery = 1;
				boolean getUsersQueryObtuvoResultados = true;
				try {
					//cada n saves espera 30 segundos (30000 ==  a 1 minuto y medio; 60000 == a un minuto)
					//cada n saves espera 30 segundos (30000 ==  a 30 segundos; 60000 == a 60 segundos, 15000 == a 25 segundos)
					System.out.println("---------------------------------------------------------------------INICIANDO ESPERA 0002");
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					System.out.println("Error_00000004: " + e);
				}
		        do {
					UserEntityListing entidadConUsuarios = UsersApiInstance .getUsers(pageSize, paginaGetUsersQuery, new ArrayList<>(), sortOrder, new ArrayList<>());
					if(Objects.nonNull(entidadConUsuarios) && Objects.nonNull(entidadConUsuarios.getEntities()) && !entidadConUsuarios.getEntities().isEmpty()){
					    List<User> usuarios = entidadConUsuarios.getEntities();
					    for (User user : usuarios) {
					    	try{					    	
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
					    	}catch(Exception w){System.out.println("Error_00000005: " + w);}
						}
					}else{
						getUsersQueryObtuvoResultados = false;
					}
		        	paginaGetUsersQuery += 1;
		        }while (getUsersQueryObtuvoResultados);
			    // Print results, including elapsed time
				System.out.println("	FIN DE LA CARGA DE AGENTES; " + "   Tiempo de Carga: " + ( (System.currentTimeMillis() - startAgentes) * (0.0000167) ) );
	        	System.out.println("<                                                                                       >");
	        	System.out.println("<                                                                                       >");
			} catch (ApiException | IOException e) {				
				System.out.println("Error_00000006: " + e);
			}
			//FIN poceso de carga de agentes

			//inicio E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
        	long startEstadosAgentes = System.currentTimeMillis();
        	estadosPorAgenteRepository.deleteAll();	 
        	System.out.println("        		PROCESO DE LA CARGA DE ESTADOS DE LOS AGENTES");
        	List<Agente> listadoDeAgentes =  (List<Agente>) agenteRepository.findAll();
        	if(Objects.nonNull(listadoDeAgentes) && !listadoDeAgentes.isEmpty()){
            	for (Agente agente : listadoDeAgentes) {
            		try{            		
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
							    	try{							    	
							    	guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(agente.getId().toString(), agente.getIdAgente(), userDetail);
							    	}catch(Exception w){System.out.println("Error_00000007: " + w);}
								}
							    paginaUserDetailsQuery += 1;
						    }else{
						    	userDetailsQueryObtuvoResultados = false;
						    }
						} catch (ApiException | IOException e) {
							System.out.println("Error_00000007.1: " + e);
							logger.error("E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]" + e.getMessage(), e);
						}								        	
			        } while (userDetailsQueryObtuvoResultados);
			        //fin estado de los agentes
            		}catch(Exception w){System.out.println("Error_00000008: " + w);}
            	}
			    // Print results, including elapsed time
				System.out.println("	FIN DE LA CARGA DE ESTADOS POR AGENTE; " + "   Tiempo de Carga: " + ( (System.currentTimeMillis() - startEstadosAgentes) * (0.0000167) ) );
				//--
	        	System.out.println("<                                                                                       >");
	        	System.out.println("<                                                                                       >");
				List<Precarga> listadoDePrecargas = (List<Precarga>) precargaRepository.findAll();
				if(Objects.nonNull(listadoDePrecargas) && !listadoDePrecargas.isEmpty()){
					Precarga precargaDB = listadoDePrecargas.get(0);
					precargaDB.setEstado("Completada");
					precargaRepository.save(precargaDB);
				}
				//--
        	}
        	System.out.println("<<<<<<<-------------------------------------OO------------------------------------>>>>>>");
        	System.out.println("<                                                                                       >");
        	System.out.println("<                                                                                       >");			
			System.out.println("	___________________________00______________________FIN DE LAX CARGAX");
			System.out.println("   T I E M P O      T O T A L     D E    C A R G A: " + ( (System.currentTimeMillis() - startTotal) * (0.0000167) ) );
	}


				
				

	
	
	private void guardarDatosProcesoPrincipalConfigurarParticipantesEstadosPorAgenteProcesoPrincipal(
			String conversacionId, String agenteId,AnalyticsUserDetail userDetail) {
		EstadosPorAgente estadosPorAgente = new EstadosPorAgente();
		int maximosRegistrosAInsertar = 250;
		//--routingStatus -->>
		if(Objects.nonNull(userDetail.getRoutingStatus()) && !userDetail.getRoutingStatus().isEmpty() && (Objects.nonNull(agenteId) || !agenteId.isEmpty()) ){
			List<AnalyticsRoutingStatusRecord> routingStatus = userDetail.getRoutingStatus();
			int totalEstadosInsertados = 0;
			for (AnalyticsRoutingStatusRecord routingSt : routingStatus) {
try{				
				estadosPorAgente = new EstadosPorAgente();
				estadosPorAgente.setEstado(routingSt.getRoutingStatus().toString());
				estadosPorAgente.setFechaFinEstado(routingSt.getEndTime());
				estadosPorAgente.setFechaInicioEstado(routingSt.getStartTime());
				estadosPorAgente.setIdAgente(agenteId);
				estadosPorAgente.setIdConversacion(conversacionId);
				estadosPorAgenteRepository.save(estadosPorAgente);
				//--
		    	totalEstadosInsertados+=1;
				if( totalEstadosInsertados==maximosRegistrosAInsertar ){
					try {
						//cada n saves espera 30 segundos (30000 ==  a 1 minuto y medio; 60000 == a un minuto)
						//cada n saves espera 30 segundos (30000 ==  a 30 segundos; 60000 == a 60 segundos, 15000 == a 15 segundos)
						totalEstadosInsertados = 0;
						System.out.println("---------------------------------------------------------------------INICIANDO ESPERA 0003");
						Thread.sleep(10000);
					} catch (InterruptedException e) {	
						System.out.println("Error_00000008.1: " + e);
					}
				}
				//--
}catch(Exception w){System.out.println("Error_00000009: " + w);}
			}	
		}
		
		//--primaryPresences -->>
		if(Objects.nonNull(userDetail.getPrimaryPresence()) && !userDetail.getPrimaryPresence().isEmpty() && (Objects.nonNull(agenteId) || !agenteId.isEmpty())){
			List<AnalyticsUserPresenceRecord> primaryPresences = userDetail.getPrimaryPresence();
			int totalEstadosInsertados = 0;
			for (AnalyticsUserPresenceRecord presencia : primaryPresences) {
try{				
					//---		columnas [K, L, C, P, Q]
				estadosPorAgente = new EstadosPorAgente();
				estadosPorAgente.setEstado(presencia.getSystemPresence().toString());
				estadosPorAgente.setFechaFinEstado(presencia.getEndTime());
				estadosPorAgente.setFechaInicioEstado(presencia.getStartTime());
				estadosPorAgente.setIdAgente(agenteId);
				estadosPorAgente.setIdConversacion(conversacionId);
				estadosPorAgenteRepository.save(estadosPorAgente);
				//--
		    	totalEstadosInsertados+=1;
				if( totalEstadosInsertados==maximosRegistrosAInsertar ){
					try {
						//cada n saves espera 30 segundos (30000 ==  a 1 minuto y medio; 60000 == a un minuto)
						//cada n saves espera 30 segundos (30000 ==  a 30 segundos; 60000 == a 60 segundos, 15000 == a 15 segundos)
						totalEstadosInsertados = 0;
						System.out.println("---------------------------------------------------------------------INICIANDO ESPERA 0003");
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						System.out.println("Error_00000009.1: " + e);
					}
				}
				//--
}catch(Exception w){System.out.println("Error_000000010: " + w);}
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
	
	private int guardarDatosProcesoPrincipal(AnalyticsConversation conversacionPC, DtoEntrada dto, List<User> usuarios) throws IOException {
		
		
		int totalDeConversacionesInsertadasConteo = 0;
		if(Objects.nonNull(conversacionPC.getParticipants()) && !conversacionPC.getParticipants().isEmpty()){
			List<AnalyticsParticipant> participants = conversacionPC.getParticipants();
			for (AnalyticsParticipant participante : participants) {
				if( 
					participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.AGENT.toString()) ||
					participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.USER.toString())
				  ){
try{
					if(participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.USER.toString())){
						//System.out.println("es una session de tipo user!!!!!   id:::::  " + conversacionPC.getConversationId());
					}
					
					Conversacion conversacion = new Conversacion();
					conversacion.setIdConversacion(conversacionPC.getConversationId());
					//---
					 	/*Calendar c = Calendar.getInstance();
				     c.setTime(conversacionPC.getConversationStart());
				     c.add(Calendar.HOUR, 5);
				     Date currentDatePlusOne = c.getTime();
				     conversacion.setFechaInicioConversacion(currentDatePlusOne);*/
				     conversacion.setFechaInicioConversacion(conversacionPC.getConversationStart());
					//---
					 	/*Calendar c2 = Calendar.getInstance();
				     c2.setTime(conversacionPC.getConversationEnd());
				     c2.add(Calendar.HOUR, 5);
				     Date currentDatePlusOne2 = c2.getTime();
				     conversacion.setFechaFinConversacion(currentDatePlusOne2);*/
				     conversacion.setFechaFinConversacion(conversacionPC.getConversationEnd());
					//--
				     //--duracion de la llamada
				     	//??
				     
				     
				     
				     
					//-- 			I D     D E L     A G E N T E
					//if(Objects.nonNull(participante) && Objects.nonNull(participante.getUserId()) && participante.getUserId().equalsIgnoreCase("c4a291b1-153d-4150-a4e3-8dad40c48f42")  ){
					if(Objects.nonNull(participante) && Objects.nonNull(participante.getUserId())){
						guardarDatosProcesoPrincipalConfigurarParticipantesNombreDelAgente(conversacion, participante, usuarios);
						//			N U M E R O 		de interacciones de {voz, chat, email}, columnas [D, E y F]
						guardarDatosProcesoPrincipalConfigurarParticipantesNumeroDeIteraciones(conversacionPC,participante);
					}

					if(!conversacion.getNombreAgente().equalsIgnoreCase(NO_NAME)){
						totalDeConversacionesInsertadasConteo = totalDeConversacionesInsertadasConteo + 1;
						conversacionRepository.save(conversacion);	
					}else{
						System.out.println("QUE RARO!!!; Id de la conversacion rara:  " + conversacionPC.getConversationId());
					}
					
					
					if ( participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.AGENT.toString()) ||
						participante.getPurpose().toString().equalsIgnoreCase(com.mypurecloud.sdk.v2.model.AnalyticsParticipant.PurposeEnum.ACD.toString()) ){
						
					}
}catch(Exception w){System.out.println("Error_000000011: " + w); System.out.println("error en la conversation: " + conversacionPC.getConversationId() + "  fechas de la conversacion, inicial: " + conversacionPC.getConversationStart() + "  final: " + conversacionPC.getConversationEnd()); }					
				}
			}
		}
		return totalDeConversacionesInsertadasConteo;
	}
	
	
	private void guardarDatosProcesoPrincipalConfigurarParticipantesNumeroDeIteraciones(
			AnalyticsConversation conversacionPC, AnalyticsParticipant participante) {
		if(Objects.nonNull(participante) && Objects.nonNull(participante.getSessions()) && !participante.getSessions().isEmpty()){
			List<AnalyticsSession> sessions = participante.getSessions();
			for (AnalyticsSession sesion : sessions) {
try{				
				if(Objects.nonNull(sesion) && Objects.nonNull(sesion.getSegments()) && !sesion.getSegments().isEmpty()){																
					List<AnalyticsConversationSegment> segments = sesion.getSegments();
					String elIdDeSessionAnterior = "0";
					for (AnalyticsConversationSegment segmento : segments) {
try{						
						ConversacionesPorTipo conversacionesPorTipo = new ConversacionesPorTipo();
												
						if(!sesion.getSessionId().equalsIgnoreCase(elIdDeSessionAnterior)){
							conversacionesPorTipo.setTipo(sesion.getMediaType().toString());
							elIdDeSessionAnterior = sesion.getSessionId();
						}else{
							conversacionesPorTipo.setTipo("N_A");
						}
						
						//		T I E M P O 		en pausa  columna [J]
						conversacionesPorTipo.setFechaFinSegmento(segmento.getSegmentEnd());
						conversacionesPorTipo.setFechaInicioSegmento(segmento.getSegmentStart());
						conversacionesPorTipo.setIdAgente(participante.getUserId());
						conversacionesPorTipo.setIdConversacion(conversacionPC.getConversationId());
						conversacionesPorTipo.setIdSession(sesion.getSessionId());
						conversacionesPorTipo.setSegmento(segmento.getSegmentType().toString());
						conversacionesPorTipoRepository.save(conversacionesPorTipo);
}catch(Exception w){System.out.println("Error_000000012: " + w);}
					}	
				}
}catch(Exception w){System.out.println("Error_000000013: " + w);}
			}
		}
	}
	
	private void guardarDatosProcesoPrincipalConfigurarParticipantesNombreDelAgente(Conversacion conversacion, AnalyticsParticipant participante, List<User> usuarios) throws IOException {
		conversacion.setIdAgente(participante.getUserId());
//--
conversacion.setNombreAgente(NO_NAME);
try {	
    User result1 = usuarios.stream()                        				// Convert to steam
            .filter(x -> participante.getUserId().equals(x.getId()))        // we want "jack" only
            .findAny()                                      				// If 'findAny' then return found
            .orElse(null);                   								//else return null
    if(Objects.nonNull(result1) && Objects.nonNull(result1.getName())){
    	conversacion.setNombreAgente(result1.getName());
    }
}catch (Exception e) {System.out.println("Error_000000014.444: " + e);}				
//--		
		
		
		
		
		
		
		
		
		
		//			N O M B R E 		DEL AGENTE, columna [B] 
		/*try {
			List<String> expand = Arrays.asList("");
			com.mypurecloud.sdk.v2.model.User userPureCloud = userApiInstance.getUser(participante.getUserId(), expand);
			conversacion.setNombreAgente(NO_NAME);
			if( Objects.nonNull(userPureCloud) &&  Objects.nonNull(userPureCloud.getName()) ){
				conversacion.setNombreAgente(userPureCloud.getName());
			}else{
				//if(conversacion.getIdConversacion().equalsIgnoreCase("365904d5-bb27-482d-a77f-6e132bc21b07")){
					System.out.println("--------------------------------------XX------------------------------------------------");
					System.out.println("agente de conversacion sin NOMBRE en PureCloud:");
					System.out.println("Id de la conversacion:  " + conversacion.getIdConversacion());
					System.out.println("participante Id de la conversacion:  " + participante.getUserId());
					System.out.println("userPureCloud:  " + userPureCloud);
					System.out.println("participante Name de la conversacion:  " + userPureCloud.getName());
					System.out.println("--------------------------------------XX------------------------------------------------");					
				//}
			}
		} catch (ApiException e) {
			System.out.println("Error_000000014: " + e);
		}
		return userApiInstance;*/
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
		
		AnalyticsQueryPredicate predicado2 = new AnalyticsQueryPredicate();    	
		predicado2.setType(com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate.TypeEnum.DIMENSION);
		predicado2.setDimension(com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate.DimensionEnum.PURPOSE);
		predicado2.setOperator(com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate.OperatorEnum.MATCHES);
		predicado2.setValue("user");
		
		
		predicates.add(predicado);
		predicates.add(predicado2);
		
		filtro.setPredicates(predicates);
		segmentFilters.add(filtro);
		
		
		//body.setSegmentFilters(segmentFilters);
	}

}