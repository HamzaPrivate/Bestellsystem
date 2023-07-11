package system;

import datamodel.Article;
import datamodel.Customer;
import datamodel.Order;
import datamodel.TAX;
//import system.impl.DatamodelFactoryImpl;

public interface OrderBuilder {


	/**
	 * Method to build a first set of Customer, Article and Order objects.
	 * 
	 * @return chainable self-reference.
	 */
	public OrderBuilder buildOrders();


	/**
	 * Method to build another set of Customer, Article and Order objects.
	 * 
	 * @return chainable self-reference.
	 */
	public OrderBuilder buildMoreOrders();
}
