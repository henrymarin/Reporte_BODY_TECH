
	 function main(){
	    var queryStringData = {
	        response_type : "token",
	        client_id : "6a059687-32c9-4f87-af90-89b6adf7e282",	        
	        redirect_uri : window.location.origin + "/welcome2.html"
	    }
	    //--
        sessionStorage.removeItem("listadoX");
        sessionStorage.removeItem("listadoZ");
        sessionStorage.removeItem("arregloX");
	    //--
	    window.location.replace("https://login.mypurecloud.com/oauth/authorize?" + jQuery.param(queryStringData));
	 }