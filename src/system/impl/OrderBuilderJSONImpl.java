package system.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import datamodel.Article;
import datamodel.Customer;
import datamodel.InventoryItem;
import datamodel.Order;
import datamodel.TAX;
import system.DatamodelFactory;
import system.InventoryManager;
import system.IoC;
import system.OrderBuilder;
import system.Repository;


/**
 * OrderBuilder builds sample orders reading objects from JSON files.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */

class OrderBuilderJSONImpl implements OrderBuilder {

	/**
	 * Factory from which objects are built.
	 */
	private final DatamodelFactory factory;

	/**
	 * Application properties.
	 */
	private final Properties properties;

	/**
	 * Indicator that orders have been loaded.
	 */
	private boolean loaded = false;


	/**
	 * Constructor with injected dependencies for DatamodelFactory component.
	 * 
	 * @param factory injected dependency of factory from which objects are created.
	 */
	OrderBuilderJSONImpl(DatamodelFactory factory, Properties properties) {
		this.factory = factory;
		this.properties = properties;
	}


	/**
	 * Method to build a first set of Customer, Article and Order objects.
	 * 
	 * @return chainable self-reference.
	 */
	@Override
	public OrderBuilder buildOrders() {
		return loadFromJSON(properties);
	}


	/**
	 * Method to build another set of Customer, Article and Order objects.
	 * 
	 * @return chainable self-reference.
	 */
	@Override
	public OrderBuilder buildMoreOrders() {
		return loadFromJSON(properties);
	}


	/**
	 * Load {@link Customer}, {@link Article} and {@link Order} objects from
	 * JSON files specified in application.properties. Method loads objects
	 * only once.
	 * <p>
	 * Example to read JSON from "data/customers.json".
	 * <pre>
	 * data.path = data
	 * data.customers = customers.json
	 * data.articles = articles.json
	 * data.orders = orders.json
	 * </pre>
	 * 
	 * @param props application.properties.
	 * @return chainable self-reference.
	 */
	private OrderBuilder loadFromJSON(Properties props) {
		if( ! loaded) {
			String customerFile = buildFilePath("data.path", "data.customers");
			String articleFile = buildFilePath("data.path", "data.articles");
			String orderFile = buildFilePath("data.path", "data.orders");
			String inventoryFile = buildFilePath("data.path", "data.inventory");
			//
			read(customerFile, jn -> createCustomer(jn));
			read(articleFile, jn -> createArticle(jn));
			read(orderFile, jn -> createOrder(jn));
			read(inventoryFile, jn -> createInventory(jn));
			loaded = true;
		}
		return this;
	}


	/**
	 * Build path to file by keys looked up in application.properties.
	 * Example: "data.path = data" and "data.customers = customers.json"
	 * builds "data/customers.json".
	 * 
	 * @param keyPath key in properties to look up path.
	 * @param keyFile key in properties to look up file name.
	 * @return built file path.
	 */
	private String buildFilePath(final String keyPath, final String keyFile) {
		String path = properties.getProperty(keyPath);
		String file = properties.getProperty(keyFile);
		path = path==null? "" : path;
		path = path.endsWith("/")? path.substring(0, path.length()-1) : path;
		file = file==null? "" : file;
		file = file.startsWith("/")? file.substring(1) : file;
		return String.format("%s/%s", path, file);
	}


	/**
	 * Create Customer object from JsonNode.
	 * 
	 * @param jn JsonNode of Customer object to create.
	 * @return Optional with created Customer object.
	 */
	private Optional<Customer> createCustomer(final JsonNode jn) {
		//
		long id = Optional.ofNullable(jn.get("id")).map(jn2 -> jn2.asLong()).orElse(-1L);
		String name = Optional.ofNullable(jn.get("name")).map(jn2 -> jn2.asText()).orElse(null);
		//
		if(id >= 0 && name != null) {
			//
			Customer customer = factory.createCustomer(name).setId(id);
			//
			Optional.ofNullable(jn.get("contacts"))
				.filter(ja -> ja.isArray())
				.ifPresent(ja ->
					ja.forEach(jn2 -> customer.addContact(jn2.asText()))
				);
			return Optional.of(customer);
		}
		return Optional.empty();
	}


	/**
	 * Create Article object from JsonNode.
	 * 
	 * @param jn JsonNode of Article object to create.
	 * @return Optional with created Article object.
	 */
	private Optional<Article> createArticle(final JsonNode jn) {
		//
		String id = Optional.ofNullable(jn.get("id")).map(jn2 -> jn2.asText()).orElse(null);
		String description = Optional.ofNullable(jn.get("description")).map(jn2 -> jn2.asText()).orElse(null);
		long unitPrice = Optional.ofNullable(jn.get("price")).map(jn2 -> jn2.asLong()).orElse(-1L);
		String tax = Optional.ofNullable(jn.get("tax")).map(jn2 -> jn2.asText()).orElse("");
		//
		if(id != null && id.length() > 0 && description != null && unitPrice >= 0) {
			//
			Article article = factory.createArticle(description, unitPrice).setId(id);
			if(tax.equals("reduced")) {
				article.setTax(TAX.GER_VAT_REDUCED);
			}
			//
			return Optional.of(article);
		}
		return Optional.empty();
	}


	/**
	 * Create Inventory object from JsonNode.
	 * 
	 * @param jn JsonNode of Inventory object to create.
	 * @return Optional with created Inventory object.
	 */
	private Optional<InventoryItem> createInventory(final JsonNode jn) {
		//
		String article_id = Optional.ofNullable(jn.get("article_id")).map(jn2 -> jn2.asText()).orElse(null);
		// String descr = Optional.ofNullable(jn.get("descr")).map(jn2 -> jn2.asText()).orElse(null);
		long unitsInStock = Optional.ofNullable(jn.get("unitsInStock")).map(jn2 -> jn2.asLong()).orElse(-1L);
		Optional<Article> article = article_id != null? factory.findArticleById(article_id) : Optional.empty();
		//
		if(article.isPresent() && unitsInStock >= 0) {
			//
			InventoryItem inventory_item = factory.createInventoryItem(article.get(), unitsInStock);
			//
			return Optional.of(inventory_item);
		}
		return Optional.empty();
	}


	/**
	 * Create Order object from JsonNode.
	 * 
	 * @param jn JsonNode of Order object to create.
	 * @return Optional with created Order object.
	 */
	private Optional<Order> createOrder(final JsonNode jn) {
		//
		String id = Optional.ofNullable(jn.get("id")).map(jn2 -> jn2.asText()).orElse(null);
		long customer_id = Optional.ofNullable(jn.get("customer_id")).map(jn2 -> jn2.asLong()).orElse(-1L);
		Optional<Customer> copt = factory.findCustomerById(customer_id);
		boolean hasItems = Optional.ofNullable(jn.get("items")).map(ja -> ja.isArray() && ja.size() > 0).orElse(false);
		//
		if(id != null && id.length() > 0 && copt.isPresent() && hasItems) {
			Order order = factory.createOrder(copt.get()).setId(id);
			jn.get("items").forEach(jn2 -> {
				int units = Optional.ofNullable(jn2.get("units")).map(jn3 -> jn3.asInt()).orElse(-1);
				Optional.ofNullable(jn2.get("article_id"))
					.filter(jn3 -> units > 0)
					.ifPresent(jn3 -> factory.findArticleById(jn3.asText())
							.ifPresent(a -> order.addItem(a, units))
					);
			});
			return Optional.of(order);
		}
		//
		return Optional.empty();
	}


	/**
	 * Load objects from JSON File with array structure: [ {obj1}, {obj2}, ... ]
	 * 
	 * @param <T> generic type of object to read from JSON.
	 * @param jsonFileName name of the JSON file.
	 * @param creator lambda to create object from calling code.
	 * @param limit limit number of objects to create.
	 * @return number of objects created from JSON file.
	 */
	private <T> long read(String jsonFileName,
		Function<JsonNode,Optional<T>> creator,
//		Consumer<T> collector,
		Integer... limit )
	{
		if(jsonFileName==null)
			return 0;
		//
		int lim = Math.max( limit.length > 0? limit[0].intValue() : Integer.MAX_VALUE, 0 );
		long count = 0;
		try (
				// auto-close on exception, InputStream implements the java.lang.AutoClosable interface
				InputStream fis = new FileInputStream(jsonFileName);
			) {
				//
				count = StreamSupport
					// stream source: read JSON array and split into stream of JsonNode's
					.stream(new ObjectMapper().readTree(fis).spliterator(), false)
					//
					// cut stream to limited number of objects
					.limit(lim)
					//
					// map JsonNode to new Optional<T> Object
					.map(jsonNode -> {
						//
						Optional<T> opt = creator.apply(jsonNode);
						if( opt.isEmpty() ) {
							System.out.println("dropping: " + jsonNode.toString());
						}
						return opt;
					})
					//
					// filter valid article objects (remove invalid ones from stream)
					.filter(a -> a.isPresent())
					//
					// map remaining valid objects to T objects
					.map(opt -> {
						T entity = opt.get();
//						if(collector != null) {
//							collector.accept(entity);
//						}
						return entity;
					})
					//
					// collect and return valid article objects only
					.count();
			//
			} catch(FileNotFoundException e) {
				System.err.println("File not found: " + jsonFileName);
			//
			} catch(Exception e) {
				e.printStackTrace();
			}
		//
		return count;
	}
}
