
	function guardarDatos(){
		if(window.location.hash) {
			
			//--Validacion de fechas
			if( laFechaDesdeSupereLaFechaDeHoy($('#datepicker').val()) ){
				//la fecha desde debe ser menor a la actual
				alert("La Fecha Inicial debe ser menor a la Fecha de Hoy");
				return false;
			}
			//--
			if( $('#datepicker').val().trim().length <= 0 ){
				alert("Debe diligenciar un valor para la Fecha Inicial.");
				return false;
			}	
			if( $('#datepicker2').val().trim().length <= 0 ){
				alert("Debe diligenciar un valor para la Fecha Final.");
				return false;
			}
			//--
			if(validarFechaHastaSupereLaFechaDesde($('#datepicker').val(),$('#datepicker2').val()) == 0){
				alert("La Fecha Inicial debe ser menor a la Fecha Final.");
				return false;
			}	
			//--
		    var token = obtenerElToken('access_token'); 
		    //--llamado interno a restController 0002
		    $.ajax({
		        url: "http://localhost:8080/guardarDatos",
		        type: "POST",
		        dataType: "json",
		        contentType: "application/json",       
		        data: JSON.stringify(
		        	{        
		        		"entradaUno": 	token,
		        		"fechaUno": 	convertirFecha($('#datepicker').val()),
		        		"fechaDos":		convertirFecha($('#datepicker2').val())
		        	}
		        ),
		        success: function(data) {
		        	 var $table = $('#table');
		        	 
		        	 $(function () {
		        		    $('#table').bootstrapTable({
		        		        data: mydata
		        		    });
		        	 });
		        	 
		            console.log(data);
		        }
		    });
		}
	}

	function obtenerElToken(name) {
	   name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	   var regex = new RegExp("[\\#&]" + name + "=([^&#]*)"), results = regex.exec(location.hash);
	   return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
	}

	function generarReporte() {
		if (window.location.hash) {
			
			//--Validacion de fechas
			if( laFechaDesdeSupereLaFechaDeHoy($('#datepicker').val()) ){
				//la fecha desde debe ser menor a la actual
				alert("La Fecha Inicial debe ser menor a la Fecha de Hoy");
				return false;
			}
			//--
			if( $('#datepicker').val().trim().length <= 0 ){
				alert("Debe diligenciar un valor para la Fecha Inicial.");
				return false;
			}	
			if( $('#datepicker2').val().trim().length <= 0 ){
				alert("Debe diligenciar un valor para la Fecha Final.");
				return false;
			}
			//--
			if(validarFechaHastaSupereLaFechaDesde($('#datepicker').val(),$('#datepicker2').val()) == 0){
				alert("La Fecha Inicial debe ser menor a la Fecha Final.");
				return false;
			}	
			//--
		    var token = obtenerElToken('access_token'); 
	
		    $.ajax({
		        url: "http://localhost:8080/generarReporte",
		        type: "POST",
		        dataType: "json",
		        contentType: "application/json",       
		        data: JSON.stringify(
		        	{        
		        		"entradaUno": 	token,
		        		"fechaUno": 	convertirFecha($('#datepicker').val()),
		        		"fechaDos":		convertirFecha($('#datepicker2').val())
		        	}
		        ),
		        success: function(json) {		        	
		        	alert('success');
		            console.log(data);
		        },
		        error : function(XMLHttpRequest, textStatus, errorThrown) {
					console.log('token: ' + token + ' Error: ' + errorThrown);					
				}
		    });
		}
	}


	/**
	 * Funcion que dadas dos fechas, valida que la fecha final sea
	 * superior a la fecha inicial.
	 * Tiene que recibir las fechas en formato español dd/mm/yyyy
	 * No valida que las fechas sean correctas
	 * Devuelve 1 si es mayor
	 *
	 * Para validar si una fecha es correcta, utilizar la función:
	 * http://www.lawebdelprogramador.com/codigo/JavaScript/1757-Validar_una_fecha.html
	 */
	function validarFechaHastaSupereLaFechaDesde(fechaDesde,fechaHasta){
	    var valuesStart	=	fechaDesde.split("/");
	    var valuesEnd	=	fechaHasta.split("/");
	
	    // Verificamos que la fecha no sea posterior a la actual
	    var dateStart = new Date(valuesStart[2],(valuesStart[1]-1),valuesStart[0]);
	    var dateEnd   = new Date(valuesEnd[2],(valuesEnd[1]-1),valuesEnd[0]);
	    
	    if(dateStart >= dateEnd){
	        return 0;
	    }
	    return 1;
	}


	function laFechaDesdeSupereLaFechaDeHoy(date){			
		
		if(  date && (date.indexOf("/") >= 0)  ){
			var laFechaDesde = new Date();
			var fecha = date.split("/");
			laFechaDesde.setFullYear(fecha[2],fecha[1]-1,fecha[0]);
			//--
			var today = new Date();		      
			//--
			var dateObj = new Date();
			var month = dateObj.getUTCMonth() + 1; //months from 1-12
			var day = dateObj.getUTCDate();
			var year = dateObj.getUTCFullYear();
			//--
			var monthDesde = fecha[1]; 
			var dayDesde = fecha[0];
			var yearDesde = fecha[2];
		
			//--
			if (monthDesde == month && dayDesde == day && yearDesde == year){
				return true;
			}
		    //--		 
		    if (laFechaDesde >= today){
		      return true;
		    }else{
		      return false;
		    }
		}else{
		      return false;
	    }	
	}
	
	
	function convertirFecha(fechaZ) {
		
		//"2018-03-08T05:00:00.000Z";
	    var valueFechaZ	=	fechaZ.split("/");	    
		return valueFechaZ[2] + "-" + valueFechaZ[1] + "-" + valueFechaZ[0] + "T00:00:00.000z";
		
	}
	
	