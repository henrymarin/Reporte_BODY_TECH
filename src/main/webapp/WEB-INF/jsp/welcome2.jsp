<!DOCTYPE html>
<html>
	<head>
		<style>
		</style>	
		
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
			<script src="http://momentjs.com/downloads/moment.min.js"></script>
				    
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
			   
			   $("#datepicker3").datepicker();
			   $("#datepicker4").datepicker();
			 } );	    
			 
			 </script>			 
			<script src="js/bootstrap-table.js"></script> 
	
	</head>
	<body>	
	<div class="wrapper">
			<div class="container-fluid">
				<section>
					<div class="container">
						<header>
							<div class="row">
								<div class="col-md-4">
									<a href="welcome.html">
										<img src="images/bdt-logo.png">
									</a>
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
				<section id="panelConInformacionDeLaPrecargaExistente">
					<div class="container">
						<div id="">
							<div class="row">
								<div class="col-md-4">
									<h5  style="text-align:  center;"><strong>INFORMACIÓN DE LA PRECARGA EXISTENTE</strong></h5>
								</div>
							</div>
							<p/>
							<div class="row">
								<div class="col-md-2">Fecha inicial:</div>
								<div class="col-md-2"><strong><input type="text" readonly="readonly" style="border-bottom: 0px;" id="msg1" value=${msg1}></strong></div>																
							</div>
							<div class="row">
								<div class="col-md-2">Fecha final:</div>
								<div class="col-md-2"><strong><input type="text" readonly="readonly" style="border-bottom: 0px;" id="msg2"  value=${msg2}></strong></div>
							</div>
							<div class="row">
								<div class="col-md-2">Estado:</div>
								<div class="col-md-2"><strong><input type="text" readonly="readonly" style="border-bottom: 0px;" id="msg3"  value='${msg3}'></strong></div>
							</div>
						</div>
				
					</div>
				</section><br><br>
				<section id="formularioDePrecarga">
					<div class="container">
						<div class="row">
							<div class="col-md-12">				
																<!-- D E F N I C I O N      GLOBAL DE LOS TABS -->							
								<div class="tab">
								  <button class="tablinks" onclick="openTab(event, 'London')" id="defaultOpen">Precarga de Información</button>
								  <button class="tablinks" onclick="openTab(event, 'Paris')">Generación del Reporte</button>
								</div>
<!-- T A B 		P A R A    E L    P R O C E S O    D E       L A     P R E C A R G A -->								
								<div id="London" class="tabcontent">
								  	<h3>Proceso para Precargar la Información</h3>
									<section id="mensajeLuegoDePrecargar" style="background: whitesmoke;">
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
									<section id="formularioPrecarga">
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
														<button style=" border:1px solid; text-transform: uppercase;  width: 100%;  padding: 10px;"  onclick="guardarDatos();">REALIZAR PRECARGA</button>
													</div>
												</div>
											</div>
										</div>
									</section>
									<section style="background: whitesmoke;">
										<div class="container"><div><div class="row"><div class="col-md-12"></div></div></div></div>
									</section>	
									<p></p><p></p><p></p>
								</div>
								
								
<!-- T A B 		P A R A    E L    P R O C E S O    D E       L A     G E N E R A C I O N    D E L     R E P O R T E -->								
								<div id="Paris" class="tabcontent">
								  <h3>Proceso para Generar el Reporte</h3>
									<section id="formularioDeGeneracionDelReporte" style="background: whitesmoke;">
										<div class="container">
											<div id="precarga">
												<div class="row">
													<div class="col-md-3">
														 <input placeholder="Fecha Inicial" title="Fecha Inicial" type="text" name="datepicker3" id="datepicker3" readonly="readonly" size="12" />
													</div>
													<div class="col-md-3">
														 <input placeholder="Fecha Final" title="Fecha Final" type="text" name="datepicker4" id="datepicker4" readonly="readonly" size="12" />
													</div>
					
													<div class="col-md-3" style="width: 10%;">
														Servicio:
														<select id="tiposDeServicios" onchange="obtenerAgentes();">
															<option value="0">Seleccione</option>
															<option value="SAC">Servicio al Cliente</option>
															<option value="MDA">Mesa de Ayuda</option>
														</select>	
													</div>								
													<div class="col-md-3" style="width: 4%;"></div>								
													
													<div class="col-md-3">
														Agentes:
														<select id="showSubCats" multiple onclick="seleccionaTodosLosAgentes(this);" style="width:  240px;height:  100px;">
															<option value="0">Todos</option>
														</select>	
													</div>
												</div>						
												
												<a name="irArribaDeLReporte"></a>
												<div align="center">														
													<div>															
														
													</div>
												</div>													
												<div align="center">
													<div>
														<br/><br/>
														<button style=" border:1px solid; text-transform: uppercase;  width: 25%;  padding: 10px;" onclick="obtenerAgentes();generarReporte();">VISUALIZAR REPORTE</button>
														<button style=" border:1px solid; text-transform: uppercase;  width: 20%;  padding: 10px;" onclick="generarReportePdf();">Exportar a PDF</button>
														<button style=" border:1px solid; text-transform: uppercase;  width: 20%;  padding: 10px;" onclick="generarReporteExcel();">Exportar a EXCEL</button>
													</div>
												</div>
					
					
											<div id="report">
												<div class="box-table">
													<div class="row">
														<div class="col-md-12">
															<h3>Reporte</h3>
														</div>
													</div>
													<div class="row" >
														<div class="col-md-12">														
															<div class="table-responsive" style="width:  1085px;">
															 <table 
															     id="tablePaginada" 
															     data-pagination="true"  
															     data-show-toggle="true"
															     data-method="post"  
															     class="table table-hover cursor-pointer" 
															     data-side-pagination="server" 
															     data-page-list="[15, 20, 25, 30, 35, 40, 45, 50, ALL]" 
															     data-buttons-class="btn btn-info btn-block" 
															     data-locale="es-MX" 
															     data-query-params="queryParams">
															     <thead>
															         
															     </thead>
															 </table>
															</div>
														</div>
													</div>
												</div>												
											</div>
										</div>
										</div>
									</section>
																						
								  <p></p> 
								</div>
							</div>
						</div>
					</div>					
					<a name="irAlPieDeLReporte"></a>
					<div align="center">														
						<div>															
							
						</div>
					</div>
					<br/><br/><br/><br/><br/>
				</section>				
				
			</div>
		</div>
</body>
</html> 
