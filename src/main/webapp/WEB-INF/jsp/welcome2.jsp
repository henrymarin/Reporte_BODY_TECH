<!DOCTYPE html>
<html lang="es">
	<head>
		<title>Bodytech</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<link href="css/style.css" rel="stylesheet">
		<link rel="shortcut icon" href="/images/favicon.ico" type="image/x-icon" />
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script> 
 		<script src="js/custom.js"></script>
		<script type="text/javascript" src="js/bootstrap.min.js"></script> 
		<script type="text/javascript" src="js/tech.js"></script> 
		
		<link href="css/jquery-ui-1.7.2.custom.css" rel="stylesheet">
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
		
	 	<script type="text/javascript">
			jQuery(function($){
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
			});    
			//--
	        $(document).ready(function() {
	           $("#datepicker").datepicker();
	           $("#datepicker2").datepicker();
	        });
	    </script>
	    
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
											<table id=table class="table table-bordered">
											  <thead>
											    <tr>
											      <th scope="col">Item</th>
											      <th scope="col">Nombre agente</th>
											      <th scope="col">Hora ingreso cola</th>
											      <th scope="col">Número interacciones voz</th>
											      <th scope="col">Número interacciones chat</th>
											      <th scope="col">Número interacciones email</th>
											      <th scope="col">Tiempo intervalo voz</th>
											      <th scope="col">Tiempo intervalo chat</th>
											      <th scope="col">Tiempo intervalo email</th>
											      <th scope="col">Tiempo pausa</th>
											      <th scope="col">Tiempo almuerzo</th>
											      <th scope="col">Tiempo break</th>
											      <th scope="col">Tiempo promedio voz</th>
											      <th scope="col">Tiempo promedio chat</th>
											      <th scope="col">Tiempo promedio email</th>
											      <th scope="col">Hora cierre sesión</th>
											      <th scope="col">Tiempo productivo agente</th>
											    </tr>
											  </thead>
											  <tbody>
											    <tr>
											      <td>Voice</td>
											      <td>Alejandra Velasquez</td>
											      <td>2018-03-01 08:25:16</td>
											      <td>50</td>
											      <td>35</td>
											      <td>24</td>
											      <td>01: 36: 12</td>
											      <td>00: 48: 15</td>
											      <td>00: 00: 56</td>
											      <td>00: 45: 00</td>
											      <td>01: 15: 13</td>
											      <td>00: 43: 00</td>
											      <td>04: 25: 12</td>
											      <td>05: 10: 45</td>
											      <td>03: 30: 12</td>
											      <td>19:20:15</td>
											      <td>06: 30: 00</td>
											  	</tr>
											  	<tr>
											      <td>Voice</td>
											      <td>Alejandra Velasquez</td>
											      <td>2018-03-02 08:10:06</td>
											      <td>75</td>
											      <td>20</td>
											      <td>31</td>
											      <td>01: 36: 12</td>
											      <td>00: 48: 15</td>
											      <td>00: 00: 56</td>
											      <td>00: 45: 00</td>
											      <td>01: 15: 13</td>
											      <td>00: 43: 00</td>
											      <td>04: 25: 12</td>
											      <td>05: 10: 45</td>
											      <td>03: 30: 12</td>
											      <td>18:45:15</td>
											      <td>06: 45: 00</td>
											  	</tr>
											  	<tr>
											       <td>Chat</td>
											      <td>Mesa de ayuda 2</td>
											      <td>2018-03-02 08:10:06</td>
											      <td>65</td>
											      <td>45</td>
											      <td>38</td>
											      <td>01: 36: 12</td>
											      <td>00: 48: 15</td>
											      <td>00: 00: 56</td>
											      <td>00: 45: 00</td>
											      <td>01: 15: 13</td>
											      <td>00: 43: 00</td>
											      <td>04: 25: 12</td>
											      <td>05: 10: 45</td>
											      <td>03: 30: 12</td>
											      <td>18:55:27</td>
											      <td>06: 52: 00</td>
											  	</tr>
											  	<tr>
											      <td>Mark</td>
											      <td>Otto</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											  	</tr>
											  	<tr>
											      <td>Mark</td>
											      <td>Otto</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											  	</tr>
											  	<tr>
											      <td>Mark</td>
											      <td>Otto</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											  	</tr>
											  	<tr>
											      <td>Mark</td>
											      <td>Otto</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											      <td>@mdo</td>
											  	</tr>
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
										<button>EXCEL</button>
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

 	