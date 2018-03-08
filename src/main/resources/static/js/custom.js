
function guardarDatos(){
	if(window.location.hash) {
	    console.log(location.hash);
	    var token = obtenerElToken('access_token'); 
	    //--llamado interno a restController 0002
	    $.ajax({
	        url: "http://localhost:8080/guardarDatos",
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
}

function obtenerElToken(name) {
   name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
   var regex = new RegExp("[\\#&]" + name + "=([^&#]*)"), results = regex.exec(location.hash);
   return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function generarReporte(){
	if(window.location.hash) {
	    console.log(location.hash);
	    var token = obtenerElToken('access_token'); 
	    //--llamado interno a restController 0003
	    	//--	.	.	.
	    //--
	    location.hash=''
	}
}