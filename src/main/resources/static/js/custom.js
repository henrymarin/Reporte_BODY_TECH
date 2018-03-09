
	window.alert = function (message) {
	  alertDGC(message);
	};

	function guardarDatos(){
		if(window.location.hash) {
			
			//--Validando las fechas:
				//--deben ser diligenciadas
			if( $('#datepicker').val().trim().length <= 0 ){
				alert("Debe diligenciar un valor para la Fecha Inicial.");
				return false;
			}	
			if( $('#datepicker2').val().trim().length <= 0 ){
				alert("Debe diligenciar un valor para la Fecha Final.");
				return false;
			}			
				//--la fecha inicial debe ser menor a la fecha final
			if(validarFechaHastaSuperOIgualeALaFechaDesde($('#datepicker').val(),$('#datepicker2').val()) == 0){
				alert("La Fecha Inicial debe ser menor a la Fecha Final.");
				return false;
			}
				//la fecha inicial NO puede ser mayor q hoy
			if( laFechaDesdeSupereLaFechaDeHoy($('#datepicker').val()) ){
				//la fecha desde debe ser menor a la actual
				alert("La Fecha Inicial NO puede ser mayor a Hoy.");
				return false;
			}
			//--
			$("#mensajePrecarga").show();
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
		        		"fechaDos":		convertirFechaDos($('#datepicker').val(),$('#datepicker2').val())
		        	}
		        ),
		        success: function(data) {			
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


	/**
	 * Funcion que dadas dos fechas, valida que la fecha final sea
	 * superior a la fecha inicial.
	 * Tiene que recibir las fechas en formato español dd/mm/yyyy
	 * No valida que las fechas sean correctas
	 * Devuelve 1 si es mayor o igual
	 *
	 * Para validar si una fecha es correcta, utilizar la función:
	 * http://www.lawebdelprogramador.com/codigo/JavaScript/1757-Validar_una_fecha.html
	 */
	function validarFechaHastaSuperOIgualeALaFechaDesde(fechaDesde,fechaHasta){
	    var valuesStart	=	fechaDesde.split("/");
	    var valuesEnd	=	fechaHasta.split("/");
	
	    // Verificamos que la fecha no sea posterior a la actual
	    var dateStart = new Date(valuesStart[2],(valuesStart[1]-1),valuesStart[0]);
	    var dateEnd   = new Date(valuesEnd[2],(valuesEnd[1]-1),valuesEnd[0]);
	    
	    if(fechaDesde == fechaHasta){
	    	return 1;
	    }
	    if(dateStart > dateEnd){
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
		    if (laFechaDesde > today){
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
	
	
	function convertirFechaUno(fechaA,fechaB) {
		var valueFechaA	=	fechaA.split("/");
		var valueFechaB	=	fechaB.split("/");
		if(fechaA == fechaB){
			return valueFechaA[2] + "-" + valueFechaA[1] + "-" + valueFechaA[0] + "T17:05:00.000z";
		}else{
			return valueFechaA[2] + "-" + valueFechaA[1] + "-" + valueFechaA[0] + "T05:00:00.000z";
		}
	}
	
	function convertirFechaDos(fechaA,fechaB) {
		var valueFechaA	=	fechaA.split("/");
		var valueFechaB	=	fechaB.split("/");
		if(fechaA == fechaB){
			return valueFechaB[2] + "-" + valueFechaB[1] + "-" + valueFechaB[0] + "T17:00:00.000z";
		}else{
			return valueFechaB[2] + "-" + valueFechaB[1] + "-" + valueFechaB[0] + "T05:00:00.000z";
		}
	}
	
	function alertDGC(mensaje)
	{
	    var dgcTiempo=500
	    var ventanaCS='<div class="dgcAlert"><div class="dgcVentana"><div class="dgcCerrar"></div><div class="dgcMensaje">'+mensaje+'<br><div class="dgcAceptar">Aceptar</div></div></div></div>';
	    $('body').append(ventanaCS);
	    var alVentana=$('.dgcVentana').height();
	    var alNav=$(window).height();
	    var supNav=$(window).scrollTop();
	    $('.dgcAlert').css('height',$(document).height());
	    $('.dgcVentana').css('top',((alNav-alVentana)/2+supNav-100)+'px');
	    $('.dgcAlert').css('display','block');
	    $('.dgcAlert').animate({opacity:1},dgcTiempo);
	    $('.dgcCerrar,.dgcAceptar').click(function(e) {
	        $('.dgcAlert').animate({opacity:0},dgcTiempo);
	        setTimeout("$('.dgcAlert').remove()",dgcTiempo);
	    });
	}
	
	