package system;

import java.util.List;
import java.util.Optional;

import datamodel.Article;
import datamodel.Customer;
import datamodel.InventoryItem;
import datamodel.Order;


/**
 * Interface of a factory that creates instances of classes of the {@link datamodel} package.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */

public interface DatamodelFactory {

	/**
	 * Customer factory method using default constructor.
	 * 
	 * @return Customer object created with default constructor.
	 */
	public Customer createCustomer();


	/**
	 * Customer factory method using constructor with name argument.
	 * 
	 * @param name single-String Customer name, e.g. "Eric Meyer".
	 * @return Customer object created with constructor with name argument.
	 */
	public Customer createCustomer(String name);


	/**
	 * Article factory method using default constructor.
	 * 
	 * @return Article object created with default constructor.
	 */
	public Article createArticle();


	/**
	 * Article factory method using constructor with description and unitPrice arguments.
	 * 
	 * @param description descriptive text for article.
	 * @param unitPrice price (in cent) for one unit of the article.
	 * @return Article object created with constructor with description and unitPrice arguments.
	 */
	public Article createArticle(String description, long unitPrice);


	/**
	 * Order factory method using constructor with owning customer as argument.
	 * 
	 * @param customer owning customer who created the order.
	 * @return Order object created with constructor with owning customer as argument.
	 * @throws IllegalArgumentException when customer argument is null or has invalid id.
	 */
	public Order createOrder(Customer customer);


	/**
	 * Factory method to construct InventoryItem. Inventory items represent currently
	 * available units of articles.
	 * 
	 * @param article for which currently available units are represented.
	 * @param unitsInStock units of article in store, negative values are illegal
	 * @return inventory item for article.
	 * @throws IllegalArgumentException when article is null or unitsInStock is negative.
	 */
	public InventoryItem createInventoryItem(Article article, long unitsInStock);


	/**
	 * Getter method to return created InventoryItems.
	 * 
	 * @return inventory items.
	 */
	public List<InventoryItem> getInventoryItems();


	/**
	 * Getter method to return created Customer objects.
	 * 
	 * @return created Customer objects.
	 */
	public List<Customer> getCustomers();


	/**
	 * Getter method to return created Article objects.
	 * 
	 * @return created Article objects.
	 */
	public List<Article> getArticles();


	/**
	 * Getter method to return created Order objects.
	 * 
	 * @return created Order objects.
	 */
	public List<Order> getOrders();


	/**
	 * Return number of created Customer objects.
	 * 
	 * @return number of created Customer objects.
	 */
	public int customersCount();


	/**
	 * Return number of created Article objects.
	 * 
	 * @return number of created Article objects.
	 */
	public int articlesCount();


	/**
	 * Return number of created inventory items.
	 * 
	 * @return number of created InventoryItem objects.
	 */
	public int inventoryCount();


	/**
	 * Find a created Customer object by its id.
	 * 
	 * @param id customer id.
	 * @return Optional with found object or empty Optional.
	 */
	public Optional<Customer> findCustomerById(long id);


	/**
	 * Find a created Article object by its id.
	 * 
	 * @param id article id.
	 * @return Optional with found object or empty Optional.
	 */
	public Optional<Article> findArticleById(String id);


	/**
	 * Find a created Order object by its id.
	 * 
	 * @param id order id.
	 * @return Optional with found object or empty Optional.
	 */
	public Optional<Order> findOrderById(String id);


	/**
	 * Return number of created Order objects.
	 * 
	 * @return number of created Order objects.
	 */
	public int ordersCount();

}
