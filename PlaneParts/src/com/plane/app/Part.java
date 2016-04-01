package com.plane.app;

public class Part {

	private String name;
	private int quantity;

	public Part(String name, int quantity) {
		this.name = name;
		this.quantity = quantity;
	}

	public Part() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
