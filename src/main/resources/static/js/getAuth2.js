

 function main(funcionalidad){
    var queryStringData = {
        response_type : "token",
        client_id : "6a059687-32c9-4f87-af90-89b6adf7e282",
        redirect_uri : "http://localhost:8080/welcome2.html"
    }
    window.location.replace("https://login.mypurecloud.com/oauth/authorize?" + jQuery.param(queryStringData));
 }