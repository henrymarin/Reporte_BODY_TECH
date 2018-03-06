

if(window.location.hash) {
    console.log(location.hash);
    var token = obtenerElToken('access_token');
    console.log('token:   ' + token);
    //--llamado directo a pureCloud
    $.ajax({
        url: "https://api.mypurecloud.com/api/v2/users/me",
        type: "GET",
        beforeSend: function(xhr){xhr.setRequestHeader('Authorization', 'bearer ' + token);},
        success: function(data) {			
            console.log(data);
        }
    });
    //--llamado interno a restController 0001
    $.ajax({
        url: "http://localhost:8080/probarSDK",
        type: "POST",
        dataType: "json",
        contentType: "application/json",       
        data: JSON.stringify(
        	{        
            "entradaUno": token            
        	}
        ),
        success: function(data) {			
            console.log(data);
        }
    });
    //--llamado interno a restController 0002
    $.ajax({
        url: "http://localhost:8080/iniciarProcesoDePrecarga",
        type: "POST",
        dataType: "json",
        contentType: "application/json",       
        data: JSON.stringify(
        	{        
            "entradaUno": token            
        	}
        ),
        success: function(data) {			
            console.log(data);
        }
    });

    
    //--
    location.hash=''
}

function obtenerElToken(name) {
   name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
   var regex = new RegExp("[\\#&]" + name + "=([^&#]*)"), results = regex.exec(location.hash);
   return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}