package datamodel;


/**
 * Class of entity type <i>InventoryItem</i>.
 * <p>
 * Inventory items represent currently available units of articles.
 * </p>
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
public class InventoryItem {

	/**
	 * Unique id, null or "" are invalid values, id can be set only once.
	 */
	private final Article article;

	/**
	 * Units in store, negative values are illegal.
	 */
	private long unitsInStore = 0L;


	/**
	 * Constructor with description and number of units in store.
	 * 
	 * @param description descriptive text for article.
	 * @param unitsInStore price (in cent) for one unit of the article.
	 * @throws IllegalArgumentException when article is null or empty "" or unitsInStore is {@code < 0}.
	 */
	public InventoryItem(Article article, long unitsInStore) {
		if(article==null)
			throw new IllegalArgumentException("article is null");
		//
		this.article = article;
		setUnitsInStore(unitsInStore);
	}

	
	/**
	 * Article attribute getter.
	 * 
	 * @return article of inventory item.
	 */
	public Article getArticle() {
		return this.article;
	}


	/**
	 * unitsInStore attribute getter.
	 * @return unitPrice.
	 */
	public long getUnitsInStore() {
		return this.unitsInStore;
	}


	/**
	 * unitsInStore attribute setter.
	 * 
	 * @param unitsInStore number of units available in inventory, must not be negative.
	 */
	public void setUnitsInStore(long unitsInStore) {
		if(unitsInStore < 0)
			throw new IllegalArgumentException("unitsInStore < 0");
		//
		this.unitsInStore = unitsInStore;
	}

}
