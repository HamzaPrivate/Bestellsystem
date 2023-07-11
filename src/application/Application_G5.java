package application;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import datamodel.Article;
import datamodel.Customer;
import datamodel.Order;
import system.DatamodelFactory;
import system.InventoryManager;
import system.IoC;
import system.Printer;
import system.Repository;
import system.TablePrinter;
import system.OrderBuilder;


/**
 * Runnable application class that creates and outputs simple orders using the
 * {@link datamodel} and {@link system} packages after refactoring.
 * <code>
 * <a href="{@docRoot}/index.html">{@value application.package_info#RootName}</a>.
 * </code>
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */

public class Application_G5 {

	/**
	 * Reference to "Inversion-of-Control" container that manages system component objects.
	 */
	private final IoC ioc;

	/**
	 * Reference to IoC-managed OrderBuilder component that builds sample orders
	 * using objects from the {@link datamodel} package.
	 */
	private final OrderBuilder orderBuilder;

	/**
	 * Reference to IoC-managed OrderBuilder component that creates instances of
	 * classes of the {@link datamodel} package.
	 */
	private final DatamodelFactory datamodelFactory;

	/**
	 * Reference to InventoryManager component.
	 */
	private final InventoryManager inventoryManager;

	/**
	 * Reference to Repository of accepted orders.
	 */
	private final Repository<Order, String> acceptedOrdersRepository;


	/**
	 * Private constructor to initialize local attributes.
	 */
	private Application_G5() {
		System.out.println(package_info.RootName + ": "
			+ this.getClass().getSimpleName()
			+ ", feat732");
		//
		this.ioc = IoC.getInstance();	// obtain ioc-reference from IoC interface
		String propertyFile = "resources/application.properties";
		int count = this.ioc.loadProperties(propertyFile);
		//
		System.out.println(String.format("\"%s\": %d properties loaded.", propertyFile, count));
		//
		this.ioc.getProperties()	// show loaded properties
			.forEach((k, v) -> System.out.println(String.format(" - %-16s = %s", k, v)));
		//
		this.orderBuilder = ioc.getOrderBuilder();
		this.datamodelFactory = ioc.getDatamodelFactory();
		this.inventoryManager = ioc.getInventoryManager();
		this.acceptedOrdersRepository = ioc.getOrderRepository();
	}

	/**
	 * Public main() function.
	 * 
	 * @param args arguments passed from command line.
	 */
	public static void main(String[] args) {
		var appInstance = new Application_G5();
		appInstance.run();
	}


	/**
	 * Private method that runs with application instance.
	 */
	private void run() {
		//
		orderBuilder
			.buildOrders()
			.buildMoreOrders();
		//
		System.out.println("(" + datamodelFactory.customersCount() + ") Customer objects built.");
		System.out.println("(" + datamodelFactory.articlesCount() + ") Article objects built.");
		System.out.println("(" + datamodelFactory.ordersCount() + ") Order objects built.");
		System.out.println("(" + datamodelFactory.inventoryCount() + ") Inventory items built.");

		Printer printer = ioc.getPrinter();
		StringBuffer sb = new StringBuffer();
		//
		printInventory(sb, printer);

		/*
		 * Retrieve Eric's order:
		 * {"id": 8592356245, "customer_id": 892474, "items": [
		 *   {"article_id": "SKU-638035", "units": 4 },		// item: 4x "Teller"
		 *   {"article_id": "SKU-693856", "units": 8 },		// item: 8x "Becher"
		 *   {"article_id": "SKU-425378", "units": 1 },		// item: 1x "Buch OOP"
		 *   {"article_id": "SKU-458362", "units": 4 }		// item: 4x "Tasse"
		 * ]},
		 */	
		Article teller = datamodelFactory.findArticleById("SKU-638035").get();
		Article book_OOP = datamodelFactory.findArticleById("SKU-425378").get();
		//
		// Order o8592 = datamodelFactory.findOrderById("8592356245").get()
		// 	// add items to order that exceed inventory
		// 	.addItem(teller, 2)
		// 	.addItem(book_OOP, 1)
		// ;
		//
			for (Order order : datamodelFactory.getOrders()) {
				try {
					//
				if(inventoryManager.isFillable(order)) {
					//
					inventoryManager.fill(order);
				}
			}
			catch(AssertionError ae) {
				var err = String.format("Order: %s is not fillable from current inventory", order.getId());
				System.err.println(err);
				System.err.println(ae.getMessage());
			}
		//
		}

		printAcceptedOrders(sb, printer, acceptedOrdersRepository.findAll());
		
		printInventory(sb, printer);

		System.out.println(sb);
	}


	/**
	 * Print Inventory table to StringBuffer.
	 * 
	 * @param sb StringBuffer to print to.
	 * @param printer to create Table.
	 * @return StringBuffer with printed table.
	 */
	private StringBuffer printInventory(StringBuffer sb, Printer printer) {
		//
		sb.append("\\\\\nInventory of available articles:\n");
		//
		final TablePrinter inventoryTable = printer.createTablePrinter(
			sb, builder -> builder
				// build table columns with width and alignment (R: right aligned)
				.column("||",   12)	// "Inv.-Id"
				.column("||",   32)	// "Article / Unit", descriptions
				.column(" |R",  13)	// "Unit Price"
				.column(" |R",  10)	// "Units in-Stock", units available for sale
				.column(" |R",  12)	// "Value" of units in-stock
			)
			.line()	// insert table header
			.row("Inv.-Id", "Article / Unit", "Unit", "Units", "Value")
			.row("", "", "Price", "in-Stock", "(in €)")
			.line();
		//
		printer.printInventoryItems(inventoryTable, datamodelFactory.getInventoryItems());
		//
		return sb;
	}


	/**
	 * Print table of Accepted orders to StringBuffer.
	 * 
	 * @param sb StringBuffer to print to.
	 * @param printer to create Table.
	 * @param acceptedOrders collection of accepted orders.
	 * @return
	 */
	private StringBuffer printAcceptedOrders(StringBuffer sb, Printer printer, Iterable<Order> acceptedOrders) {
		//
		sb.append("\\\\\nAccepted orders:\n");
		//
		TablePrinter orderTable =
			printer.createTablePrinter(
				sb, builder -> builder
					// build table columns with width and alignment (R: right aligned)
					.column("|",  11)	// "Bestell-ID"
					.column("|",  28)	// "Bestellungen", descriptions
					.column("R",   7)	// "MwSt", VAT tax for each item
					.column(" ",   1)	// " ", marker (*) for reduced VAT tax rate
					.column("R",  10)	// "Preis", price for each item
					.column("|R", 10)	// "MwSt", VAT tax for whole order
					.column(" |R",12)	// "Gesamt", price for whole order
			)
			.line()	// insert table header
			.row("Bestell-ID", "Bestellungen", "MwSt", "", "Preis", "MwSt", "Gesamt")
			.line();
		//
		Collection<Order> acceptedOrdersAsCollection =
			// convert Iterable<Order> to Collection<Order>
			StreamSupport.stream(
				acceptedOrdersRepository.findAll().spliterator(), false)
				    .collect(Collectors.toList());
		//
		printer.printOrders(orderTable, acceptedOrdersAsCollection);
		//
		return sb;
	}

}
