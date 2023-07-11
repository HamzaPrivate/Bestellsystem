package system;

import datamodel.Order;


/**
 * InventoryManager is a component of the order processing system
 * that manages the inventory of articles that is available for sale.
 * 
 * An order can be filled only when sufficient inventory (current
 * number of available units for each article) is available for all
 * items on an order.
 * 
 * For example, with inventory:
 *  {@code id: "SKU-458362", "description": "Tasse", "units": 48 },
 *  {@code id: "SKU-693856", "description": "Becher", "units": 4 }.
 * 
 * Order:
 *   {"id": 8592356245, "customer_id": 892474,
 *     "items": [
 *       {"article_id": "SKU-458362", "units": 4 },
 *       {"article_id": "SKU-693856", "units": 6 },
 *     ]
 *   }
 * cannot be filled since inventory of the second item: "SKU-693856"
 * is only 4 units, insufficient to meet the 6 units ordered.
 * 
 */
public interface InventoryManager {

    /**
     * Verify that sufficient inventory is available for all items
     * of an order.
     * 
     * @param order to verify sufficient inventory for all order items.
     * @return true if sufficient inventory is available for all order items.
     * @throws AssertionError if order is not fillable.
     */
    public boolean isFillable(Order order);


    /**
     * Reduce inventory by the number of items of a ordered.
     * 
     * @param order order to fill.
     * @return true if inventory was reduced by ordered units. 
     */
    public void fill(Order order);

}
