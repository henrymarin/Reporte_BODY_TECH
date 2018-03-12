

<!DOCTYPE html>
<html lang="es">
	<head>
		<title>Bodytech</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<link href="css/bootstrap-table.css" rel="stylesheet">
		
		<link href="css/style.css" rel="stylesheet">
		<link rel="shortcut icon" href="/images/favicon.ico" type="image/x-icon" />
 		<script src="js/getAuth2.js"></script>
 		<script type="text/javascript" src="js/jquery-3.3.1.min.js"></script>		
		<script type="text/javascript" src="js/tech.js"></script> 
		
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
									<a onclick="main();"><h2 style="text-align:center;">Reporte de Productividad</h2></a>
									<a onclick="generarReporte();"><h2 style="text-align:center;">Ir al reporte paginado!!!!</h2></a>
								</div>							
						</div>
					</div>
				</section>
			</div>





<table 
	id="TBSReporte" 

           data-search="true"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-export="true"
           data-detail-view="true"
           data-detail-formatter="detailFormatter"
           data-minimum-count-columns="2"
           data-show-pagination-switch="true"
           data-pagination="true"
           data-id-field="id"
           data-page-list="[10, 25, 50, 100, ALL]"
           data-show-footer="false"
           data-side-pagination="server"
           data-response-handler="responseHandler">	
	
	>
	
	<thead>
	    <tr>
			 <th data-visible="false" 										data-field="id"></th>
			<th data-halign="center" data-valign="middle"  data-field="nombreAgente" data-align="left">nombreAgente</th>	       	
			<th data-halign="center" data-valign="middle"  data-field="tiempoIntervaloVoz" data-align="left">tiempoIntervaloVoz</th>
			<th data-halign="center" data-valign="middle"  data-field="nombreAgente" data-align="left">nombreAgente</th>
			<th data-halign="center" data-valign="middle"  data-field="nombreAgente" data-align="left">nombreAgente</th>
			<th data-halign="center" data-valign="middle"  data-field="nombreAgente" data-align="left">nombreAgente</th>		
	   </tr>
	</thead>
	<tbody> 
	    
	</tbody>
</table>

			
			
			
		</div>
	</body>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/bootstrap-table.js"></script>	
</html>

 