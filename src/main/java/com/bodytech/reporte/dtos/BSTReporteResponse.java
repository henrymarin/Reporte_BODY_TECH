package com.bodytech.reporte.dtos;

import java.util.List;

public class BSTReporteResponse {

	private Integer total;
	private List<BTSReporteMapping> rows;
	
	public BSTReporteResponse(List<BTSReporteMapping> rows, Integer total) {
		super();
		this.rows = rows;
		this.total = total;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<BTSReporteMapping> getRows() {
		return rows;
	}

	public void setRows(List<BTSReporteMapping> rows) {
		this.rows = rows;
	}
	
	
}
