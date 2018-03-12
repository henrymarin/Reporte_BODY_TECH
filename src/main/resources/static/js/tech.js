$(document).ready(function() {
	
	
	$("#generar-reporte").hide();
	$("#report").hide();
	$("#mensajePrecarga").hide();




	$(".precarga").click(function(event){
     event.preventDefault();

	});

	$(".volver").click(function(event){
     event.preventDefault();
        $("#precarga").show();
        $("#generar-reporte").hide();
        $("#report").hide();
	});


	$(".generate").click(function(event){
     event.preventDefault();
        $("#report").show();
        
	});



 });