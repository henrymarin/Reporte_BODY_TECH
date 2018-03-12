package com.bodytech.reporte.dtos;

public class GenericBootStrapTableRequest {

		//el order es el ordenamiento (desc o asc)
	private String order;
		//el sort es el campo por el que se ordenara
	private String sort;
	private String searchField;
	private Integer offset;
	private Integer limit;
	private String idSearcheable;
	private String usuarioEncargado;
	private String userId;

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String seachrField) {
		this.searchField = seachrField;
	}

	public String getIdSearcheable() {
		return idSearcheable;
	}

	public void setIdSearcheable(String idSearcheable) {
		this.idSearcheable = idSearcheable;
	}

	public String getUsuarioEncargado() {
		return usuarioEncargado;
	}

	public void setUsuarioEncargado(String usuarioEncargado) {
		this.usuarioEncargado = usuarioEncargado;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	

	

}
