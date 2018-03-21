

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
		return valueFechaZ[2] + "-" + valueFechaZ[1] + "-" + valueFechaZ[0] + "T05:00:00.000Z";
		
	}
	
	
	function convertirFechaUno(fechaA,fechaB) {
		var valueFechaA	=	fechaA.split("/");
		var valueFechaB	=	fechaB.split("/");
		if(fechaA == fechaB){
			return valueFechaA[2] + "-" + valueFechaA[1] + "-" + valueFechaA[0] + "T17:05:00.000Z";
		}else{
			return valueFechaA[2] + "-" + valueFechaA[1] + "-" + valueFechaA[0] + "T05:00:00.000Z";
		}
	}
	
	function convertirFechaDos(fechaA,fechaB) {
		var valueFechaA	=	fechaA.split("/");
		var valueFechaB	=	fechaB.split("/");
		if(fechaA == fechaB){
			return valueFechaB[2] + "-" + valueFechaB[1] + "-" + valueFechaB[0] + "T17:00:00.000Z";
		}else{
			return valueFechaB[2] + "-" + valueFechaB[1] + "-" + valueFechaB[0] + "T05:00:00.000Z";
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
	
	//BOTOn PRINCIPAL
	function generarReporte(){
		
		//--Validando las fechas:
			//--deben ser diligenciadas
		if( $('#datepicker3').val().trim().length <= 0 ){
			alert("Debe diligenciar un valor para la Fecha Inicial.");
			return false;
		}	
		if( $('#datepicker4').val().trim().length <= 0 ){
			alert("Debe diligenciar un valor para la Fecha Final.");
			return false;
		}			
			//--la fecha inicial debe ser menor a la fecha final
		if(validarFechaHastaSuperOIgualeALaFechaDesde($('#datepicker3').val(),$('#datepicker4').val()) == 0){
			alert("La Fecha Inicial debe ser menor a la Fecha Final.");
			return false;
		}
			//la fecha inicial NO puede ser mayor q hoy
		if( laFechaDesdeSupereLaFechaDeHoy($('#datepicker3').val()) ){
			//la fecha desde debe ser menor a la actual
			alert("La Fecha Inicial NO puede ser mayor a Hoy.");
			return false;
		}
		//--
		//VALIDANDO EL SERVICIO y los AGentes
		 var elTipoDeServicio = $("#tiposDeServicios").val();
		 var elOLosAgentes = $("#showSubCats").val();
		 if(elTipoDeServicio == "" || elTipoDeServicio == "0" || elTipoDeServicio.length <= 1){
			alert("Debe seleccionar un Tipo de Servicio");
			return false;
		 }
		 var listadoz = sessionStorage.getItem("listadoZ");
		 var listadoDeAgentesTmp = sessionStorage.getItem("listadoX");
		 if(listadoDeAgentesTmp == null || listadoDeAgentesTmp == "" || listadoDeAgentesTmp.length <= 1){
			alert("Debe seleccionar, por lo menos, un Agente");
			return false;
		}
		 var rq = {
					limit: 				10,
					offset: 			0,
					order: 				'desc',
					sort:				'fecha',
					lista: 				listadoz.split(','),
					fechaInicial:		convertirFechaExportarExcel($('#datepicker3').val()),
					fechaFinal:			convertirFechaExportarExcel($('#datepicker4').val()),
					listadoDeAgentesStr:listadoz
			 };
		 
		//--
		$.ajax({
            url: '/generarReportePaginado',
            type: 'POST',
            contentType: "application/json",
            data: JSON.stringify(rq),
            success: function (data) {                
                $('#TBSReporte').bootstrapTable({data: data.rows});
                $('#TBSReporte').bootstrapTable('load', data.rows);
                //--
                $("#report").show();
                //--
                $('#datepicker3').val("");
                $('#datepicker4').val("");
                document.getElementById('tiposDeServicios').value=0;
           }
        });
		//--
		
	}
	
	function generarReporteExcel(){
		if(window.location.hash) {
			reporteXLSPOST();
		}
	}
	
	function convertirFechaExportarExcel(fechaZ) {
		
		//"2018-03-08";
	    var valueFechaZ	=	fechaZ.split("/");	    
		return valueFechaZ[2] + "-" + valueFechaZ[1] + "-" + valueFechaZ[0] ;		
	}
	
	
	function generarReportePdf(){
		if(window.location.hash) {	
		   reportePDFPOST();
		}
	}
	
	function obtenerAgentes() {
		if($("#tiposDeServicios").val() != "0"){
			var select = document.getElementById("showSubCats");
			select.options.length = 0;		
			$("#tiposDeServicios").val();
			//--
			$.ajax({
	            url: '/obtenerUsuariosPorTipoDeServicio/' + $("#tiposDeServicios").val(),
	            type: 'GET',
	            contentType: "application/json",
	            dataType: 'json',
	            success: function (data) {
	            	select.options[select.options.length] = new Option("Todos", "0");
	            	for(var i=0;i<data.listaValores.length;i++) {
	            		select.options[select.options.length] = new Option(data.listaValores[i].nombre, data.listaValores[i].codigo);
	            	}
	           }
	        });	
		}
	}
	
	function seleccionaTodosLosAgentes(obj) {
		
		var listadoDeAgentes = {};
		var arregloDeAgentes = [];
		var arregloX = [];
		
		if(obj.selectedIndex == 0){
			var select = document.getElementById("showSubCats");
		    for (var i = 0; i < obj.options.length; i++) {
		    	select.options[i].selected = true;	    	
		    }
		}
	    //--
		for (var i = 0; i < obj.options.length; i++) {
			var opt = obj.options[i];
			if (opt.selected && opt.value != "0") {
				var agente	=  {
					id: opt.value
			    };
				arregloDeAgentes.push(agente);
				arregloX.push(opt.value);
			}			 
		}
	    //--		    
	    listadoDeAgentes = arregloX.toString();
	    sessionStorage.setItem("listadoX", JSON.stringify(listadoDeAgentes));
	    sessionStorage.setItem("listadoZ", listadoDeAgentes);
	    sessionStorage.setItem("arregloX", JSON.stringify(arregloX));
		
	}
	
	
function openTab(evt, cityName) {
    // Declare all variables
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the button that opened the tab
    document.getElementById(cityName).style.display = "block";
    evt.currentTarget.className += " active";
}
	
	
function reporteXLSPOST(){
	if(window.location.hash) {
		var listadoz = sessionStorage.getItem("listadoZ");
		var rq = {        
			"entradaUno": 	'token',
			"fechaUno": 	convertirFechaExportarExcel($('#datepicker3').val()),
			"fechaDos":		convertirFechaExportarExcel($('#datepicker4').val()),
			"lista":		listadoz.split(','),
		};
		//--			
		var request = new XMLHttpRequest();
		request.open('POST', 'http://localhost:8080/crearXLS', true);
		request.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
		request.responseType = 'blob';

		request.onload = function(e) {
			var fileName = "ReporteExportadoEnFormatoEXCEL.xls";
		    if (this.status === 200) {
		        var blob = this.response;
		        if(window.navigator.msSaveOrOpenBlob) {
		            window.navigator.msSaveBlob(blob, fileName);
		        }
		        else{
		            var downloadLink = window.document.createElement('a');
		            var contentTypeHeader = request.getResponseHeader("Content-Type");
		            downloadLink.href = window.URL.createObjectURL(new Blob([blob], { type: contentTypeHeader }));
		            downloadLink.download = fileName;
		            document.body.appendChild(downloadLink);
		            downloadLink.click();
		            document.body.removeChild(downloadLink);
		           }
		       }
		   };
		   request.send(JSON.stringify(rq));
		
	}
}
	
	
function reportePDFPOST(){
	if(window.location.hash) {
		var listadoz = sessionStorage.getItem("listadoZ");
		var rq = {        
			"entradaUno": 	'token',
			"fechaUno": 	convertirFechaExportarExcel($('#datepicker3').val()),
			"fechaDos":		convertirFechaExportarExcel($('#datepicker4').val()),
			"lista":		listadoz.split(','),
		};
		//--			
		var request = new XMLHttpRequest();
		request.open('POST', 'http://localhost:8080/crearPDF', true);
		request.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
		request.responseType = 'blob';
		//--
		request.onload = function(e) {
			var fileName = "ReporteExportadoEnFormatoPDF.pdf";
			if (this.status === 200) {
				var blob = this.response;
				if(window.navigator.msSaveOrOpenBlob) {
					window.navigator.msSaveBlob(blob, fileName);
				}else{
					var downloadLink = window.document.createElement('a');
					var contentTypeHeader = request.getResponseHeader("Content-Type");
			        downloadLink.href = window.URL.createObjectURL(new Blob([blob], { type: contentTypeHeader }));
			        downloadLink.download = fileName;
			        document.body.appendChild(downloadLink);
			        downloadLink.click();
			        document.body.removeChild(downloadLink);
				 }
			  }
		};
		request.send(JSON.stringify(rq));
	}
}
	
	