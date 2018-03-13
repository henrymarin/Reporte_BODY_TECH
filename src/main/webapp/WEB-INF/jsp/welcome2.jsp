<!DOCTYPE html>
<html lang="es">
	<head>
		<title>Bodytech</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<link rel="shortcut icon" href="/images/favicon.ico" type="image/x-icon" />
		
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<link href="css/style.css" rel="stylesheet">
		<link rel="stylesheet" href="css/bootstrap-table.css">		
		
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
 		<script src="js/custom.js"></script> 		
		<script type="text/javascript" src="js/tech.js"></script>
	    
		<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
		<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
		<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>	    
		<script>
		 $.datepicker.regional['es'] = {
					closeText: 'Cerrar',
					prevText: '&#x3c;Ant',
					nextText: 'Sig&#x3e;',
					currentText: 'Hoy',
					monthNames: ['Enero','Febrero','Marzo','Abril','Mayo','Junio',
					'Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'],
					monthNamesShort: ['Ene','Feb','Mar','Abr','May','Jun',
					'Jul','Ago','Sep','Oct','Nov','Dic'],
					dayNames: ['Domingo','Lunes','Martes','Mi&eacute;rcoles','Jueves','Viernes','S&aacute;bado'],
					dayNamesShort: ['Dom','Lun','Mar','Mi&eacute;','Juv','Vie','S&aacute;b'],
					dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','S&aacute;'],
					weekHeader: 'Sm',
					dateFormat: 'dd/mm/yy',
					firstDay: 1,
					isRTL: false,
					showMonthAfterYear: false,
					yearSuffix: ''};
				$.datepicker.setDefaults($.datepicker.regional['es']);
				
		 $( function() {
		   $("#datepicker").datepicker();
		   $("#datepicker2").datepicker();
		 } );	    
		 </script>
		 
		<script src="js/bootstrap-table.js"></script> 
		 
		<style type="text/css">
		    .dgcAlert {top: 0;position: absolute;width: 100%;display: block;height: 1000px; background: url(http://www.dgcmedia.es/recursosExternos/fondoAlert.png) repeat; text-align:center; opacity:0; display:none; z-index:999999999999999;}
			.dgcAlert .dgcVentana{width: 500px; background: white;min-height: 150px;position: relative;margin: 0 auto;color: black;padding: 10px;border-radius: 10px;}
			.dgcAlert .dgcVentana .dgcCerrar {height: 25px;width: 25px;float: right; cursor:pointer; background: url(http://www.dgcmedia.es/recursosExternos/cerrarAlert.jpg) no-repeat center center;}
			.dgcAlert .dgcVentana .dgcMensaje { margin: 0 auto; padding-top: 45px; text-align: center; width: 400px;font-size: 20px;}
			.dgcAlert .dgcVentana .dgcAceptar{background:#dc8302; bottom:20px; display: inline-block; font-size: 12px; font-weight: bold; height: 24px; line-height: 24px; padding-left: 5px; padding-right: 5px;text-align: center; text-transform: uppercase; width: 75px;cursor: pointer; color:#FFF; margin-top:50px;}
	    </style>
 
 	    		
	</head>
	<body>
		<div class="wrapper">
			<div class="container-fluid">
				<section>
					<div class="container">
						<header>
							<div class="row">
								<div class="col-md-4">
									<img src="images/bdt-logo.png">
								</div>
							</div>
						</header>
					</div>
				</section>
				<section>
					<div class="container">
						<div class="row">
							<div class="col-md-12">
								<h2>Reporte de Productividad</h2>								
							</div>
						</div>
					</div>
				</section>
				<section>
					<div class="container">
						<div id="mensajePrecarga">
							<div class="row">
								<div class="col-md-12">
									El proceso de precarga se ha iniciado satisfactoriamente, una vez termine, podrá ejecutar el proceso de generación del reporte.									
								</div>								
							</div>
						</div>
					</div>
				</section>	

				<section>
					<div class="container">
						<div id="">
							<div class="row">
								<div class="col-md-3">
									<b>Proceso de Precarga Existente:</b>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4" align="center">
									Fecha Inicial: ${msg1}
								</div>									
							</div>
							<div class="row">
								<div class="col-md-4" align="center">
									Fecha Final: ${msg2}
								</div>						
							</div>						
						</div>
					</div>
				</section><br><br>
				<section>
					<div class="container">
						<div id="precarga">
							<div class="row">
								<div class="col-md-3">
									 <input placeholder="Fecha Inicial" title="Fecha Inicial" type="text" name="datepicker" id="datepicker" readonly="readonly" size="12" />
								</div>
								<div class="col-md-3">
									 <input placeholder="Fecha Final" title="Fecha Final" type="text" name="datepicker2" id="datepicker2" readonly="readonly" size="12" />
								</div>
								<div class="col-md-3">
									<button class="precarga" onclick="guardarDatos();">REALIZAR PRECARGA</button>
								</div>
								<div class="col-md-3">
									<button class="btn-action-primary generate" onclick="generarReporte();">GENERAR REPORTE</button>
								</div>
							</div>
						</div>
						<div id="generar-reporte">
							<div class="row labels-block">
								<div class="col-md-2">
									<label>Fecha Inicio</label>
									<label><b>12 Dic 2018</b></label>
								</div>
								<div class="col-md-2">
									<label>Fecha Fin</label>
									<label><b>12 Dic 2019</b></label>
								</div>
								<div class="col-md-4">
									<label>Correo Eléctronico</label>
									<label><b>Vladimiyr.kiev@correo.com</b></label>
								</div>
								<div class="col-md-2">
									<button class="volver">NUEVA PRECARGA</button>
								</div>
								<div class="col-md-2">
									<button class="btn-action-primary generate">GENERAR REPORTE</button>
								</div>
							</div>
						</div>
						<div id="report">
							<div class="box-table">
								<div class="row">
									<div class="col-md-12">
										<h3>Reporte</h3>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="table-responsive">
											<table class="table table-bordered"
												id="TBSReporte" 
												data-search="true"
												data-show-refresh="false"
												data-show-toggle="true"
												data-show-columns="false"
												data-detail-formatter="detailFormatter"
												data-minimum-count-columns="2"
												data-show-pagination-switch="false"
												data-pagination="true"
												data-id-field="id"
												data-page-list="[10, 25, 50, 100, ALL]"
												data-show-footer="false"
												data-side-pagination="server"
												data-url="/generarReportePaginado"
												data-method="post">
												<thead>
												    <tr>
														<th data-visible="false" 										data-field="id"></th>
														<th data-halign="center" data-valign="middle"  data-field="item" data-align="left">Item</th>	       	
														<th data-halign="center" data-valign="middle"  data-field="nombreAgente" data-align="left">Nombre del Agente</th>
														<th data-halign="center" data-valign="middle"  data-field="horaIngresoCola" data-align="left">Hora de ingreso a la Cola</th>
														<th data-halign="center" data-valign="middle"  data-field="numeroInteraccionesVoz" data-align="left">Número de Interacciones por Voz</th>
														<th data-halign="center" data-valign="middle"  data-field="numeroInteraccionesChat" data-align="left">Número de Interacciones por Chat</th>
														<th data-halign="center" data-valign="middle"  data-field="numeroInteraccionesEmail" data-align="left">Número de Interacciones por Email</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoIntervaloVoz" data-align="left">Tiempo de Intervalo por Voz</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoIntervaloChat" data-align="left">Tiempo de Intervalo por Chat</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoIntervaloEmail" data-align="left">Tiempo de Intervalo por Email</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoPausa" data-align="left">Tiempo de Pausa</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoAlmuerzo" data-align="left">Tiempo de Almuerzo</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoBreak" data-align="left">Tiempo de Break</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoPromedioVoz" data-align="left">Tiempo Promedio por Voz</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoPromedioChat" data-align="left">Tiempo Promedio por Chat</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoPromedioEmail" data-align="left">Tiempo Promedio por Email</th>
														<th data-halign="center" data-valign="middle"  data-field="horaCierreSesion" data-align="left">Hora de Cierre de Sesion</th>
														<th data-halign="center" data-valign="middle"  data-field="tiempoProductivoAgente" data-align="left">Tiempo Productivo del Agente</th>		
												   </tr>
												</thead>
												<tbody> 
												    
												</tbody>
											</table>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2 pull-right">
										<button>PDF</button>
									</div>
									<div class="col-md-2 pull-right">
										<button onclick="generarReporteExcel();">EXCEL</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
		</div>
	</body>
</html>

 	