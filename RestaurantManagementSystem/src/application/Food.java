package application;

public class Food {
	
	private String menuItem;
	private Double price;
	
	public Food(String dishName, Double cost) {
		this.setMenuItemName(dishName);
		this.setPrice(cost);
	}

	public String getMenuItemName() {
		return menuItem;
	}

	public void setMenuItemName(String dishLabel) {
		this.menuItem = dishLabel;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
