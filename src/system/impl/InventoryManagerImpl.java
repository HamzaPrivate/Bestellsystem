package system.impl;

import java.util.HashMap;
import java.util.Map;

import datamodel.Article;
import datamodel.InventoryItem;
import datamodel.Order;
//import datamodel.generated.OrderItem;
import system.DatamodelFactory;
import system.InventoryManager;
import system.Repository;


/**
 * MOCK implementation of InventoryManager interface.
 * 
 */
class InventoryManagerImpl implements InventoryManager {

	/**
	 * Reference to IoC-managed OrderBuilder component that creates instances of
	 * classes of the {@link datamodel} package.
	 */
	private final DatamodelFactory datamodelFactory;

	/**
	 * Reference to Repository of accepted orders.
	 */
	private final Repository<Order, String> acceptedOrdersRepository;


	/**
	 * Non-public constructor.
	 * 
	 * @param datamodelFactory injected reference to DatamodelFactory.
	 * @param acceptedOrdersRepository injected reference to acceptedOrdersRepository.
	 */
	InventoryManagerImpl(DatamodelFactory datamodelFactory, Repository<Order, String> acceptedOrdersRepository) {
		this.datamodelFactory = datamodelFactory;
		this.acceptedOrdersRepository = acceptedOrdersRepository;
	}


    /**
     * Verify that sufficient inventory is available for all items
     * of an order.
     * 
     * @param order to verify sufficient inventory for all order items.
     * @return true if sufficient inventory is available for all order items.
     * @throws AssertionError if order is not fillable.
     */
	@Override
    public boolean isFillable(Order order) {
		if(order==null)
            throw new IllegalArgumentException("order is null");
		//
		boolean isfillable = true;
		Map<Article, Long> compounds = new HashMap<Article, Long>();
		//
		// iterate over ordered items and compound ordered amounts
		for(var item : order.getItems()) {
			Article article = item.getArticle();
			Long compound = compounds.get(article);
			if(compound==null) {
				compound = 0L;
			}
			compounds.put(article, compound + item.getUnitsOrdered());
		}
		StringBuffer err = new StringBuffer();
		// iterate over ordered items and probe inventory against compound amounts
		for(var item : order.getItems()) {
			Article article = item.getArticle();
			InventoryItem invItem = lookUp(article);
			long compoundUnitsOrdered = compounds.get(article);
			if(compoundUnitsOrdered > invItem.getUnitsInStore()) {
				// ordered item is not fillable from units in store
				err.append(String.format(" - item: %dx \"%s\" exceeds inventory\n",
					item.getUnitsOrdered(), article.getDescription()
				));
			}
		}
		if(err.length() > 0) {
			isfillable = false;
			throw new AssertionError(err);
		}
		return isfillable;
    }


    /**
     * Reduce inventory by the number of items of a ordered.
     * 
     * @param order order to fill.
     * @return true if inventory was reduced by ordered units. 
     */
	@Override
    public void fill(Order order) {
		if(order==null)
            throw new IllegalArgumentException("order is null");
		//
		// iterate over ordered items and reduce inventory by ordered amounts
		order.getItems().forEach(item -> {
			Article article = item.getArticle();
			InventoryItem invItem = lookUp(article);
			long newUnitsInStore = invItem.getUnitsInStore() - item.getUnitsOrdered();
			invItem.setUnitsInStore(newUnitsInStore);
		});
		//
		// save order to acceptedOrdersRepository
		acceptedOrdersRepository.save(order);
	}


	/**
	 * Look up InventoryItem for article.
	 * 
	 * @param article to look up.
	 * @return inventory item.
	 */
	private InventoryItem lookUp(Article article) {
		for(var invItem : datamodelFactory.getInventoryItems()) {
			if(invItem.getArticle().getId().equals(article.getId()))
				return invItem;
		}
		return null;
	}

}
