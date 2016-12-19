package application;

/**
 * This class is used to instantiate food objects which are both saved in order lists and moved to and from menus
 * Each food object has a name and a price allowing for ease of calculating totals and searching for items.
 * The methods and structure of this class are derived from the tutorials available from the ProgrammingKnowledge playlist 
 * JavaFX tutorial for beginners : https://www.youtube.com/watch?v=9YrmON6nlEw&list=PLS1QulWo1RIaUGP446_pWLgTZPiFizEMq
 * @author jarrod joshua
 */
public class Food {
	
	private String menuItem;
	private Double price;
	
	/**
	 * Returns a new food object
	 * @param dishName : String describing the name of a food object for instance 'pizza'
	 * @param cost : double detailing the price of a food object 
	 */
	public Food(String dishName, Double cost) {
		this.setMenuItemName(dishName);
		this.setPrice(cost);
	}
	
	/**
	 * Gets the name of the food object as a String
	 * @return menuItem : String detailing the name of the food object
	 */
	public String getMenuItemName() {
		return menuItem;
	}
	
	/**
	 * Sets the name of a food object with an input String
	 * @param dishLabel : String input which you want to name the object as
	 */
	public void setMenuItemName(String dishLabel) {
		this.menuItem = dishLabel;
	}
	
	/**
	 * Gets the price of the food object as a double
	 * @return price : double specifying the price the food item will be sold for
	 */
	public Double getPrice() {
		return price;
	}
	
	/**
	 * Sets the price of a food object with an input double.
	 * @param price : double specifying the new price you want to set the food at
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

}
