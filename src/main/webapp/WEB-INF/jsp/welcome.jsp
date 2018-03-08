

<!DOCTYPE html>
<html lang="es">
	<head>
		<title>Bodytech</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<link href="css/style.css" rel="stylesheet">		
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script> 
 		<script src="js/getAuth2.js"></script>
		<script type="text/javascript" src="js/bootstrap.min.js"></script> 
		
		<link href="css/jquery-ui-1.7.2.custom.css" rel="stylesheet">
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
		
		<script type="text/javascript" src="js/tech.js"></script> 
		
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
						<div id="precarga">
							<div class="row">
								<div class="col-md-3">
									 <input placeholder="Fecha Inicial" title="Fecha Inicial" type="text" name="datepicker" id="datepicker" readonly="readonly" size="12" />
								</div>
								<div class="col-md-3">
									 <input placeholder="Fecha Final" title="Fecha Final" type="text" name="datepicker2" id="datepicker2" readonly="readonly" size="12" />
								</div>
								<div class="col-md-3">
									<button class="precarga" onclick="main('REALIZAR_PRECARGA');">REALIZAR PRECARGA</button>
								</div>
								<div class="col-md-3">
									<button class="btn-action-primary generate" onclick="main('GENERAR REPORTE');">GENERAR REPORTE</button>
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
											<table class="table table-bordered">
											  <thead>
											    <tr>
											      <th scope="col">First</th>
											      <th scope="col">Last</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											      <th scope="col">Handle</th>
											    </tr>
											  </thead>
											  <tbody>
											    <tr>
											      <td>Otoo</td>
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

 