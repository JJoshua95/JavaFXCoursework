package application;

/**
 * This class is used to instantiate food objects which are both saved in order lists and moved to and from menus
 * Each food object has a name and a price allowing for ease of calculaing totals and searching for items.
 * @author jarrod joshua
 */
public class Food {
	
	private String menuItem;
	private Double price;
	
	/**
	 * Returns a new food object
	 * @param dishName
	 * @param cost
	 */
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
