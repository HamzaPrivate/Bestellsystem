package datamodel;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Class of entity type <i>Order</i>.
 * <p>
 * Order represents a contractual relationship with a Customer for purchased (ordered) items.
 * </p>
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
public class Order {

    /**
     * Unique id, null or "" are invalid values, id can be set only once.
     */
    private String id = null;

    /**
     * Reference to owning Customer, final, never null.
     */
    private final Customer customer;

    /**
     * Date/time the order was created.
     */
    private final Date creationDate;

    /**
     * Items that are ordered as part of this order.
     */
    private final List<OrderItem> items;
    
    /**
     * Constructor with customer owning the order.
     * @param customer customer as owner of order, customer who placed that order.
     * @throws IllegalArgumentException when customer argument is null or has invalid id.
     */
    public Order(Customer customer) {
    	if(customer == null) {
    		throw new IllegalArgumentException("customer argument is null or has invalid id");
    	}
		this.customer = customer;
		this.items = new ArrayList<>();
		this.creationDate = new Date(System.currentTimeMillis());
    }

    /**
     * Id getter.
     * @return order id, returns {@code null}, if id is unassigned.
     */
    public String getId() {
        return id;
    }

    /**
     * Id setter. Id can only be set once with valid id, id is immutable after assignment.
     * @param id only valid id (not null or "") updates id attribute on first invocation.
     * @throws IllegalArgumentException if id argument is invalid ({@code id==null} or {@code id==""}).
     * 
     * @return chainable self-reference.
     */
    public Order setId(String id) {
    	if(id == null || id.trim() == "") {
			throw new IllegalArgumentException("invalid id.");
		}
		if(this.id==null) {
			this.id = id;
		}
		return this;
    }

    /**
     * Customer getter.
     * @return owning customer, cannot be null.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * CreationDate getter, returns the time/date when the order was created.
     * @return time/date when order was created as long in ms since 01/01/1970.
     */
    public long getCreationDate() {
        return creationDate.getTime();
    }

    /**
     * CreationDate setter for date/time, which is valid for {@code 01/01/2020 <= datetime <= now() + 1day}.
     * Orders cannot be older than the lower bound and younger than the current datetime (+1day).
     * @param datetime time/date when order was created (in milliseconds since 01/01/1970).
     * @throws IllegalArgumentException if datetime is outside valid range {@code 01/01/2020 <= datetime <= now() + 1day}.
     * @return chainable self-reference.
     */
    public Order setCreationDate(long datetime) {
//    	LocalDateTime todayDate = LocalDateTime.now();
//    	LocalDateTime tomorrowDate = todayDate.plusDays(1);
//    	Date date = new Date(datetime);
//    	Date oldest = new Date(01/01/2020);
//    	Date current = new Date();
//        if(date.after(oldest) && date.before(current)) {
//        	this.creationDate = date;
//        }
        return this;
    }

    /**
     * Number of items that are part of the order.
     * @return number of ordered items.
     */
    public int itemsCount() {
        return items.size();
    }

    /**
     * Ordered items getter.
     * @return ordered items.
     */
    public Iterable<OrderItem> getItems() {
        return items;
    }

    /**
     * Create new item and add to order.
     * @param article article ordered from catalog.
     * @param units units ordered.
     * @throws IllegalArgumentException if article is null or units not a positive {@code units > 0} number.
     * @return chainable self-reference.
     */
    public Order addItem(Article article, int units) {
        if(article == null || units <= 0) {
        	throw new IllegalArgumentException("article is null or units not a positive.");
        }
        OrderItem orderItem = new OrderItem(article, units);
        items.add(orderItem);
        return this;
    }

    /**
     * Delete i-th item from order, {@code i >= 0 && i < items.size()}, otherwise method has no effect.
     * @param i index of item to delete, only a valid index deletes item.
     */
    public void deleteItem(int i) {
        if(i>=0 && i<items.size()) {
        	items.remove(i);
        }
    }

    /**
     * Delete all ordered items.
     */
    public void deleteAllItems() {
        items.clear();
    }

}