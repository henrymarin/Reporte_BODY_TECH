package com.javainuse;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.Configuration;
import com.mypurecloud.sdk.v2.api.UsersApi;
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
	
	
}
