package com.bodytech.reporte.controladoras;

import java.io.IOException;
import java.util.ArrayList;
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
import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.Configuration;
import com.mypurecloud.sdk.v2.api.ConversationsApi;
import com.mypurecloud.sdk.v2.model.AnalyticsConversation;
import com.mypurecloud.sdk.v2.model.AnalyticsConversationQueryResponse;
import com.mypurecloud.sdk.v2.model.AnalyticsParticipant;
import com.mypurecloud.sdk.v2.model.AnalyticsQueryFilter;
import com.mypurecloud.sdk.v2.model.AnalyticsQueryPredicate;
import com.mypurecloud.sdk.v2.model.ConversationQuery;
import com.mypurecloud.sdk.v2.model.PagingSpec;

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
	
}
