

	 function main(){
	    var queryStringData = {
	        response_type : "token",
	        client_id : "6a059687-32c9-4f87-af90-89b6adf7e282",
	        redirect_uri : "http://localhost:8080/welcome2.html"
	    }
	    window.location.replace("https://login.mypurecloud.com/oauth/authorize?" + jQuery.param(queryStringData));
	 }
	
	
	function generarReporte(){
		
		$.ajax({
            url: '/generarReportePaginado',
            type: 'POST',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify(
        		{
        			"limit":	"10",
        			"offset":	"0",
        			"order":	"desc",
        			"sort":		"fecha"
        		}
            ),
            success: function (data) {                
                $('#TBSReporte').bootstrapTable({data: data.rows});
                $('#TBSReporte').bootstrapTable('load', data.rows);  
           }
        });
		
	}