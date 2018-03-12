<!DOCTYPE html>
<html lang="es">
	<head>
		<title>Bodytech</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />		
		<link href="css/style.css" rel="stylesheet">
		<link rel="shortcut icon" href="/images/favicon.ico" type="image/x-icon" />
		<script type="text/javascript" src="js/jquery-3.3.1.min.js"></script> 
 		<script src="js/custom.js"></script>
 		<link href="css/bootstrap.min.css" rel="stylesheet">
		<script type="text/javascript" src="js/tech.js"></script>
		<link href="css/jquery-ui-1.7.2.custom.css" rel="stylesheet">		
		
		
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

<table 
	id="TBSReporte" 
	class="table-responsive table table-hover table-actions" 
	data-unique-id="id" 
	data-pagination="true" 
	data-method="post" 
	data-side-pagination="server" 
	data-page-list="[5, 10, 20, 50, 100, 200]" 
	data-buttons-class="btn btn-info btn-block" 
	data-locale="es-MX" 
	data-sort-name="fecha" 
	data-sort-order="asc" 
	data-query-params="queryParams">
	<thead>
	    <tr>
	        <th data-visible="false" 										data-field="idCobertura"></th>
			<th data-rowspan="2" data-halign="center" data-valign="middle"  data-field="nombreAgente" data-align="left"></th>	       	
			<th data-rowspan="2" data-halign="center" data-valign="middle"  data-field="horaIngresoCola" data-align="left"></th>
			<th data-rowspan="2" data-halign="center" data-valign="middle"  data-field="horaIngresoCola" data-align="left"></th>
			<th data-rowspan="2" data-halign="center" data-valign="middle"  data-field="numeroInteraccionesVoz" data-align="left"></th>
			<th data-rowspan="2" data-halign="center" data-valign="middle"  data-field="numeroInteraccionesChat" data-align="left"></th>			
	   </tr>
	</thead>
	<tbody> 
	    
	</tbody>
</table>			


<table id="table">
            <thead>
            <tr>
                <th data-field="id">ID</th>
                <th data-field="name">Item Name</th>
                <th data-field="price">Item Price</th>
            </tr>
            </thead>
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
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/bootstrap-table.js"></script>
</html>

 	