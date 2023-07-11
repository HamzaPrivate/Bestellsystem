package system.impl;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import datamodel.Article;
import datamodel.Customer;
import datamodel.InventoryItem;
import datamodel.Order;
import datamodel.TAX;
import system.Calculator;
import system.Formatter;
import system.Printer;
import system.TablePrinter;


/**
 * Class that implements the Printer interface.
 * 
 * Class has two dependencies on: Calculator and Formatter, which are injected
 * through the constructor.
 */

class PrinterImpl implements Printer {

	/**
	 * Dependency for Calculator component.
	 */
	private final Calculator calculator;

	/**
	 * Dependency for Formatter component.
	 */
	private final Formatter formatter;


	/**
	 * Constructor with injected dependencies for Calculator and Formatter components.
	 * 
	 * @param calculator reference to calculator component.
	 * @param formatter reference to formatter component.
	 */
	PrinterImpl(Calculator calculator, Formatter formatter) {
		this.calculator = calculator;
		this.formatter = formatter;
	}


	/**
	 * Factory method to create TablePrinter instances.
	 * 
	 * @param sb StringBuffer to collect table content.
	 * @param builder initialize table at creation with columns, widths and alignment.
	 * @return TablePrinter instance.
	 */
	@Override
	public TablePrinter createTablePrinter(StringBuffer sb, Consumer<TablePrinter.Builder> builder) {
		return new TablePrinterImpl(sb, builder);
	}


	/**
	 * Print attributes of one Customer object into StringBuffer as column-separated line.
	 * <pre>
	 * Example:
	 * | 892474 | Meyer, Eric                    | eric98@yahoo.com, (030) 3945-642298          |
	 * </pre>
	 * 
	 * @param sb StringBuffer that will contains the formatted result. A new StringBuffer is created when sb is null.
	 * @param c Customer object.
	 * @return StringBuffer that contains formatted result (same sb).
	 */
	@Override
	public StringBuffer printCustomer(final StringBuffer sb, final Customer c) {
		if(c==null)
			return sb;
		//
		final StringBuffer contacts = new StringBuffer();
		IntStream.range(0, c.contactsCount()).forEach(i ->
			contacts.append(i==0? "" : ", ").append(c.getContacts()[i])
		);
		//
		int nameStyle = 0;
		return (sb==null? new StringBuffer() : sb)
			.append(String.format("| %6d ", c.getId()))
			.append(String.format("| %-31s", formatter.fmtName(c.getFirstName(), c.getLastName(), nameStyle)))
			.append(String.format("| %-44s ", contacts))
			.append("|\n");
	}


	/**
	 * Print collection of Customer objects into StringBuffer as lines with Customer attributes.
	 * <pre>
	 * Example:
	 * | 643270 | Bayer, Anne              | anne24@yahoo.de, (030) 3481-23352   |
	 * | 412396 | Blumenfeld, Nadine-Ulla  | +49 152-92454                       |
	 * | 892474 | Meyer, Eric              | eric98@yahoo.com, (030) 3945-642298 |
	 * </pre>
	 * 
	 * @param sb StringBuffer that will contains the formatted result. A new StringBuffer is created when sb is null.
	 * @param customers collection of Customers (null argument is ignored).
	 * @return StringBuffer that contains formatted result (same sb).
	 */
	@Override
	public StringBuffer printCustomers(final StringBuffer sb, final Collection<Customer> customers) {
		if(customers==null)
			return sb;
		//
		final StringBuffer sb_ = sb==null? new StringBuffer() : sb;
//		var cit = customers.iterator();
//		while(cit.hasNext()) {		// iterate through Customer collection
//			Customer c = cit.next();
//			printCustomer(sb, c);		// format each Customer object as line into StringBuffer
//		}
//		return sb;
		return process(sb_, customers, s -> s, c -> printCustomer(sb_, c));	// calling generic print method
	}


	/**
	 * Print attributes of one Article object into StringBuffer as column-separated line.
	 * <pre>
	 * Example:
	 * | SKU-458362 | Tasse                      |    2.99€|  19% MwSt|
	 * </pre>
	 * 
	 * @param sb StringBuffer that will contains the formatted result. A new StringBuffer is created when sb is null.
	 * @param a Article object
	 * @return StringBuffer that contains formatted result (same sb).
	 */
	@Override
	public StringBuffer printArticle(final StringBuffer sb, final Article a) {
		if(a==null)
			return sb;
		//
		return (sb==null? new StringBuffer() : sb)
			.append(String.format("| %10s ", a.getId()))
			.append(String.format("| %-27s", a.getDescription()))
			.append(String.format("| %6d ", a.getUnitPrice())).append("\u20ac")	// Unicode for Euro
			.append(String.format("| %4s MwSt", a.getTax()==TAX.GER_VAT_REDUCED? "7%" : "19%"))
			.append("|\n");
	}


	/**
	 * Print collection of Article objects into StringBuffer as lines with Article attributes.
	 * <pre>
	 * Example:
	 * | SKU-458362 | Tasse                      |    2.99€|  19% MwSt|
	 * | SKU-693856 | Becher                     |    1.49€|  19% MwSt|
	 * | SKU-278530 | Buch "Java"                |   49.90€|   7% MwSt|
	 * </pre>
	 * 
	 * @param sb StringBuffer that will contains the formatted result. A new StringBuffer is created when sb is null.
	 * @param articles collection of Articles (null argument is ignored).
	 * @return StringBuffer that contains formatted result (same sb).
	 */
	@Override
	public StringBuffer printArticles(final StringBuffer sb, final Collection<Article> articles) {
		if(articles==null)
			return sb;
		//
		final StringBuffer sb_ = sb==null? new StringBuffer() : sb;
		return process(sb_, articles, a -> printArticle(sb_, a));
	}


	/**
	 * Print attributes of one Order object into StringBuffer as column-separated line.
	 * <pre>
	 * Example:
	 * | 8592356245 | Meyer, Eric                | 4 items | created: 2022-05-16 08:16:42 |
	 * </pre>
	 * 
	 * @param sb StringBuffer that will contains the formatted result. A new StringBuffer is created when sb is null.
	 * @param order Order object
	 * @return StringBuffer that contains formatted result (same sb).
	 */
	@Override
	public StringBuffer printOrder(final StringBuffer sb, final Order order) {
		if(order==null)
			return sb;
		//
		final String creationDate = formatter.fmtDate(order.getCreationDate(), 0, "");
		final Customer c = order.getCustomer();
		return (sb==null? new StringBuffer() : sb)
			.append(String.format("| %10s ", order.getId()))
			.append(String.format("| %-27s", formatter.fmtName(c.getFirstName(), c.getLastName(), 0)))
			.append(String.format("| %1d items ", order.itemsCount()))
			.append(String.format("| created: %s ", creationDate))
			.append("|\n");
	}


	/**
	 * Print collection of Order objects into StringBuffer as lines with Order attributes.
	 * <pre>
	 * Example:
	 * | 8592356245 | Meyer, Eric                | 4 items | created: 2022-05-16 08:16:42 |
	 * | 3563561357 | Bayer, Anne                | 2 items | created: 2022-05-16 08:16:42 |
	 * | 5234968294 | Meyer, Eric                | 1 items | created: 2022-05-16 08:16:42 |
	 * | 6135735635 | Blumenfeld, Nadine-Ulla    | 3 items | created: 2022-05-16 08:16:42 |
	 * </pre>
	 * 
	 * @param sb StringBuffer that will contains the formatted result. A new StringBuffer is created when sb is null.
	 * @param orders collection of Orders (null argument is ignored).
	 * @return StringBuffer that contains formatted result (same sb).
	 */
	@Override
	public StringBuffer printOrders(final StringBuffer sb, final Collection<Order> orders) {
		if(orders==null)
			return sb;
		//
		final StringBuffer sb_ = sb==null? new StringBuffer() : sb;
		return process(sb_, orders, a -> printOrder(sb_, a));
	}


	/**
	 * Print order into TablePrinter with order item separated lines.
	 * <pre>
	 * Example:
	 * +----------+---------------------------------------------+--------------------+
	 * |Bestell-ID|Bestellungen                  MwSt      Preis|     MwSt     Gesamt|
	 * +----------+---------------------------------------------+--------------------+
	 * |8592036245|Eric's Bestellung                            |                    |
	 * |          | - 4 Teller, 4x 6.49          4.14     25.96€|                    |
	 * |          | - 8 Becher, 8x 1.49          1.90     11.92€|                    |
	 * |          | - 1 Buch "OOP"               5.23*    79.95€|                    |
	 * |          | - 4 Tasse, 4x 2.99           1.91     11.96€|   13.18€    129.79€|
	 * +----------+---------------------------------------------+--------------------+
	 * </pre>
	 * 
	 * @param orderTable to print order into.
	 * @param order order printed into {@code orderTable}, (null argument is ignored).
	 * @return {@link TablePrinter} that contains formatted result.
	 */
	@Override
	public TablePrinter printOrder(final TablePrinter orderTable, final Order order) {
		//
		if(orderTable != null && order != null) {
			String id = order.getId();	// retrieve order attributes
			String name = order.getCustomer().getFirstName();
			orderTable.row(id, name + "'s Bestellung: ");
			//
			long totalPrice = 0;	// compounded price over all order items
			long totalVAT = 0;		// compounded tax over all order items
			var items = order.getItems().iterator();
			for(int i = order.itemsCount(); --i >= 0; ) {
				var item = items.next();
				int units = item.getUnitsOrdered();
				long unitPrice = item.getArticle().getUnitPrice();
				long itemPrice = unitPrice * units;
				long vat = calculator.calculateIncludedVAT(itemPrice, item.getArticle().getTax());
				totalPrice += itemPrice;
				totalVAT += vat;
				//
				String unitPriceStr = formatter.fmtPrice(unitPrice);
				String descr = item.getArticle().getDescription();
				String lineItem = units > 1?
						// multi-line item: " - 4 Teller, 4x 6.49"
						String.format(" - %d %s, %dx %s", units, descr, units, unitPriceStr) :
						//
						// single item: " - 1 Buch OOP"
						String.format(" - %d %s", units, descr);
				//
				String vatStr = formatter.fmtPrice(vat);
				String vatR = item.getArticle().getTax()==TAX.GER_VAT_REDUCED? "*" : "";
				String itemPriceStr = formatter.fmtPrice(itemPrice, 1);
				//
				if(i > 0) {
					orderTable.row("", lineItem, vatStr, vatR, itemPriceStr);
				} else {
					String totalPriceStr = formatter.fmtPrice(totalPrice, 1);
					String totalVATStr = formatter.fmtPrice(totalVAT, 1);
					orderTable.row("", lineItem, vatStr, vatR, itemPriceStr, totalVATStr, totalPriceStr);
				}
			}
		}
		return orderTable;
	}


	/**
	 * Print collection of order objects into TablePrinter.
	 * <pre>
	 * Example:
	 * +----------+---------------------------------------------+--------------------+
	 * |Bestell-ID|Bestellungen                  MwSt      Preis|     MwSt     Gesamt|
	 * +----------+---------------------------------------------+--------------------+
	 * |8592356245|Eric's Bestellung:                           |                    |
	 * |          | - 4 Teller, 4x 6.49          4.14     25.96€|                    |
	 * |          | - 8 Becher, 8x 1.49          1.90     11.92€|                    |
	 * |          | - 1 Buch "OOP"               5.23*    79.95€|                    |
	 * |          | - 4 Tasse, 4x 2.99           1.91     11.96€|   13.18€    129.79€|
	 * +----------+---------------------------------------------+--------------------+
	 * |3563561357|Anne's Bestellung:                           |                    |
	 * |          | - 2 Teller, 2x 6.49          2.07     12.98€|                    |
	 * |          | - 2 Tasse, 2x 2.99           0.95      5.98€|    3.02€     18.96€|
	 * +----------+---------------------------------------------+--------------------+
	 *  {@code >>>>>>>>>>}                                       Gesamt:|   16.20€    148.75€|
	 *                                                          +====================+
	 * </pre>
	 * 
	 * @param orderTable to print orders into.
	 * @param orders collection of orders printed into {@code orderTable}, (null argument is ignored).
	 * @return {@link TablePrinter} that contains formatted result.
	 */
	@Override
	public TablePrinter printOrders(final TablePrinter orderTable, final Collection<Order> orders) {
		long[] totals = {0L, 0L};	// tuple with compounded price and VAT tax values over all orders.
		//
		process(orderTable, orders,
//				// descending by order value
//				s -> s.sorted((o1, o2) -> Long.compare(calculateValueAndTax(o2)[0], calculateValueAndTax(o1)[0])),
//				s -> s.sorted((o1, o2) -> Long.compare(o1.getCustomer().getId(), o2.getCustomer().getId())),
//				s -> s.filter(o -> o.getCustomer().getId()==892474L),
//				s -> s.filter(o -> o.getId().equals("8592356245") || o.getId().equals("3563561357")),
				order -> {
					long[] oval = calculator.calculateValueAndTax(order);
					totals[0] += oval[0];	// compound price
					totals[1] += oval[1];	// compound VAT tax
					printOrder(orderTable, order).line();
				});
		//
		String totalPrice = formatter.fmtPrice(totals[0], 1);
		String totalVAT = formatter.fmtPrice(totals[1], 1);
		//
		return orderTable
			.row( "@ >        |   |", "", "", "", "", "Gesamt:", totalVAT, totalPrice)
			.line("@          +=+=+");
	}


	/**
	 * Print inventory into TablePrinter with order item separated lines.
	 * <pre>
	 * Example:
	 * +----------+-----------------------------+---------+-----------+-----------+
	 * |Inv.-Id   |Article / Unit               |     Unit|      Units|      Value|
	 * |          |                             |    Price|   in-Stock|     (in €)|
	 * +----------+-----------------------------+---------+-----------+-----------+
	 * |SKU-693856|Becher                       |    2.49€|        428|  1,065.72€|
	 * |SKU-638035|Teller                       |    8.49€|          5|     42.45€|
	 * |SKU-425378|Buch "OOP"                   |   99.95€|          1|     99.95€|
	 * |SKU-300926|Pfanne                       |   69.99€|         86|  6,019.14€|
	 * |SKU-458362|Tasse                        |    4.99€|        137|    683.63€|
	 * |SKU-278530|Buch "Java"                  |   69.90€|          0|      0.00€|
	 * |SKU-518957|Kanne                        |   29.99€|         96|  2,879.04€|
	 * |SKU-663942|Fahrradhelm                  |  199.00€|         49|  9,751.00€|
	 * |SKU-583978|Fahrradkarte                 |    8.95€|         38|    340.10€|
	 * +----------+-----------------------------+---------+-----------+-----------+
	 *  >>>>>>>>>>                                              Value:| 20,881.03€|
	 *                                                                +===========+
	 * </pre>
	 * 
	 * @param inventoryTable to print order into.
	 * @param items items printed into {@code inventoryTable}, (null argument is ignored).
	 * @return {@link TablePrinter} with formatted result.
	 */
	@Override
	public TablePrinter printInventoryItems(final TablePrinter inventoryTable, final Collection<InventoryItem> items) {
		long[] total = {0L};	// tuple with compounded price and VAT tax values over all orders.
		//
		process(inventoryTable, items,
//			// descending by order value
//			s -> s.sorted((o1, o2) -> Long.compare(calculateValueAndTax(o2)[0], calculateValueAndTax(o1)[0])),
//			s -> s.sorted((o1, o2) -> Long.compare(o1.getCustomer().getId(), o2.getCustomer().getId())),
//			s -> s.filter(o -> o.getCustomer().getId()==892474L),
//			s -> s.filter(o -> o.getId().equals("8592356245") || o.getId().equals("3563561357")),
			item -> {
				Article a = item.getArticle();
				String unitPrice = formatter.fmtPrice(a.getUnitPrice(), 1);
				String unitsInStore = item.getUnitsInStore() + "";
				long value = item.getUnitsInStore() * a.getUnitPrice();
				String valueStr = formatter.fmtPrice(value, 1);
				total[0] += value;
				inventoryTable.row(
					a.getId(), a.getDescription(), unitPrice, unitsInStore, valueStr
				);
			});
		//
		String value = formatter.fmtPrice(total[0], 1);
		//
		return inventoryTable
			.line()
			.row( "@ >      | |", "", "", "", "Value:", value)
			.line("@        +=+");
	}


	/**
	 * Generic method that converts a {@code Collection<T>} to {@code Stream<T>}
	 * and applies a function @{code applyEach} to each element.
	 * 
	 * @param <T> generic {@code Collection} and {@code Stream} element type.
	 * @param <R> generic return type of a collector.
	 * @param collector collector with result. 
	 * @param collection elements to be processed.
	 * @param applyEach functional interface to process each element {@code t}.
	 * @return collector with result.
	 */
	<T,R> R process(final R collector, final Collection<T> collection,
			final Consumer<T> applyEach
	) {
		return process(collector, collection, null, applyEach);
	}


	/**
	 * Generic method that converts a {@code Collection<T>} to {@code Stream<T>},
	 * allows a callout to be applied to the stream before a function @{code applyEach}
	 * is applied to each remaining element.
	 * 
	 * @param <T> generic {@code Collection} and {@code Stream} element type.
	 * @param <R> generic return type of a collector.
	 * @param collector collector with result. 
	 * @param collection elements to be processed.
	 * @param callout functional interface to process stream of elements before processing function is applied.
	 * @param applyEach functional interface to process each element {@code t}.
	 * @return collector with result.
	 */
	<T,R> R process(final R collector, final Collection<T> collection,
			final Function<Stream<T>, Stream<T>> callout,
			final Consumer<T> applyEach
	) {
		if(collection != null) {
			(callout != null? callout.apply(collection.stream()) : collection.stream())
				.forEach(t -> applyEach.accept(t));
		}
		return collector;
	}
}
