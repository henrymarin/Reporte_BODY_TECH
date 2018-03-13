package consumos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.bodytech.reporte.entidades.Conversacion;
import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.Configuration;
import com.mypurecloud.sdk.v2.api.UsersApi;
import com.mypurecloud.sdk.v2.model.AnalyticsQueryFilter;
import com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate;
import com.mypurecloud.sdk.v2.model.AnalyticsRoutingStatusRecord;
import com.mypurecloud.sdk.v2.model.AnalyticsUserDetail;
import com.mypurecloud.sdk.v2.model.AnalyticsUserDetailsQueryResponse;
import com.mypurecloud.sdk.v2.model.AnalyticsUserPresenceRecord;
import com.mypurecloud.sdk.v2.model.PagingSpec;
import com.mypurecloud.sdk.v2.model.UserDetailsQuery;

public class EjecutarServicios {

	public static void main(String[] args) {		
	 
		// Configure SDK settings
		String accessToken = "EmhN6hDSw4ASPnw1NvevrEP2SePrBIyZkGAL4Q7_2wZDeNu1_uIldm0XD99o64PrwcsT7wLHWrphfZcJ_Zu9rw";
		Configuration.setDefaultApiClient(ApiClient.Builder.standard().withAccessToken(accessToken).withBasePath("https://api.mypurecloud.com").build());
		
		//--
    	System.out.println("<<<<----guardarDatosDepurado--->>>> INICIA LA SEGUNDA CARGA  <<<----ESTADOS DEL AGENTE--->>>>!!!");
    	List<Conversacion> listadoDeConversaciones =  new ArrayList<>();
    	Conversacion conv = new Conversacion();
    	conv.setIdAgente("0d5fc836-390d-4976-b06e-89b8d2f90191");
    	listadoDeConversaciones.add(conv);    	
    	if(Objects.nonNull(listadoDeConversaciones) && !listadoDeConversaciones.isEmpty()){
        	for (Conversacion conversacion : listadoDeConversaciones) {
				// 			E S T A D  O S 		DE LOS AGENTES, columnas [K, L, C, P, Q]
				Integer paginaUserDetailsQuery = 1;
				boolean userDetailsQueryObtuvoResultados = true;
		        do {
					UserDetailsQuery userBody = guardarDatosProcesoPrincipalConfigurarParticipantesCrearFiltroUserDetail(conversacion.getIdAgente(), paginaUserDetailsQuery);
					try {
						UsersApi userApiInstance = new UsersApi();
				    	//						<<<<--- R E S P O N S E 	postAnalyticsUsersDetailsQuery---->>>>>						
					    AnalyticsUserDetailsQueryResponse userResult = userApiInstance.postAnalyticsUsersDetailsQuery(userBody);
					    if(Objects.nonNull(userResult) && Objects.nonNull(userResult.getUserDetails()) && !userResult.getUserDetails().isEmpty()){
						    List<AnalyticsUserDetail> userDetails = userResult.getUserDetails();					    
						    for (AnalyticsUserDetail userDetail : userDetails) {
						    	//-----
						    	if(Objects.nonNull(userDetail.getRoutingStatus()) && !userDetail.getRoutingStatus().isEmpty()){
									List<AnalyticsRoutingStatusRecord> routingStatus = userDetail.getRoutingStatus();
									for (AnalyticsRoutingStatusRecord routingSt : routingStatus) {															    		
										routingSt.getRoutingStatus().toString();
										routingSt.getEndTime();
										routingSt.getStartTime();										
									}	
								}								
								//--primaryPresences -->>
								List<AnalyticsUserPresenceRecord> primaryPresences = userDetail.getPrimaryPresence();
								for (AnalyticsUserPresenceRecord presencia : primaryPresences) {
										//---		columnas [K, L, C, P, Q]									
									if(presencia.getSystemPresence().toString().equalsIgnoreCase("MEAL")){
										System.out.println("<<<<<<------------------XXXXXXXXXXXXXXXX-------------------->>>>");
										System.out.println("getStartTime: " + presencia.getEndTime());
										System.out.println("getEndTime: " + presencia.getStartTime());
										System.out.println("SystemPresence: " + presencia.getSystemPresence().toString());
									}
									presencia.getSystemPresence().toString();
									presencia.getEndTime();
									presencia.getStartTime();
								}
							}
						    paginaUserDetailsQuery += 1;
					    }else{
					    	userDetailsQueryObtuvoResultados = false;
					    }
					} catch (ApiException | IOException e) {
						
					}								        	
		        } while (userDetailsQueryObtuvoResultados);
		        //fin estado de los agentes
        	}
    	}			            	
    

	}
	
	private static UserDetailsQuery guardarDatosProcesoPrincipalConfigurarParticipantesCrearFiltroUserDetail(String agenteId,Integer paginaUserDetailsQuery) {
		UserDetailsQuery userBody = new UserDetailsQuery(); // UserDetailsQuery | query
		userBody.setInterval("2018-02-11T05:00:00.000Z/2018-02-15T05:00:00.000Z");
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

}
