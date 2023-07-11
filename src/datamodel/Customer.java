package datamodel;


import java.util.*;

/**
 * Class of entity type <i>Customer</i>.
 * <p>
 * Customer is an individual (a person, not a business) who creates and holds (owns) orders in the system.
 * </p>
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author Hamza
 */
public class Customer {

	/**
	 * Unique Customer id attribute, {@code id < 0} is invalid, id can only be set once.
	 */
	private long id = -1;

	/**
	 * Customer's surname attribute, never null.
	 */
	private String lastName = "";

	/**
	 * None-surname name parts, never null.
	 */
	private String firstName = "";

	/**
	 * Customer contact information with multiple contacts.
	 */
	private final List<String> contacts;

	/**
	 * Default constructor.
	 */
	public Customer() {
		this.contacts = new ArrayList<>();
		// TODO implement here
	}

	/**
	 * Constructor with single-String name argument.
	 * @param name single-String Customer name, e.g. "Eric Meyer".
	 * @throws IllegalArgumentException if name argument is null.
	 */
	public Customer(String name) {
		this.contacts = new ArrayList<>();
		if(name == null) {
			throw new IllegalArgumentException("name null.");
		}
		if(name == "") {
			throw new IllegalArgumentException("name empty.");
		}
		if(!name.trim().contains(" ")) {
			lastName = name;
			return;
		}
		setName(name.trim());
	}

	/**
	 * Id getter.
	 * @return customer id, returns {@code null}, if id is unassigned.
	 */
	public Long getId() {
		if(id<0)return null;
		return id;
	}

	/**
	 * Id setter. Id can only be set once with valid id, id is immutable after assignment.
	 * @param id value to assign if this.id attribute is still unassigned {@code id < 0} and id argument is valid.
	 * @throws IllegalArgumentException if id argument is invalid ({@code id < 0}).
	 * @return chainable self-reference.
	 */
	public Customer setId(long id) {
		if(id<0) {
			throw new IllegalArgumentException("invalid id (negative).");
		}
		if(this.id<0) {
			this.id = id;
		}
		return this;
	}

	/**
	 * LastName getter.
	 * @return value of lastName attribute, never null, mapped to "".
	 */
	public String getLastName() {
		if(lastName==null)return "";
		return lastName;
	}

	/**
	 * FirstName getter.
	 * @return value of firstName attribute, never null, mapped to "".
	 */
	public String getFirstName() {
		if(firstName==null)return "";
		return firstName;
	}

	/**
	 * 
	 * @return lastName and firstName as one String seperated by ", "
	 */

	public String getName() {
		String name = lastName + ", " + firstName;
		return name;
	} 

	/**
	 * Setter that splits a single-String name (for example, "Eric Meyer") into first-
	 * ("Eric") and lastName ("Meyer") parts and assigns parts to corresponding attributes.
	 * @param first value assigned to firstName attribute, null is ignored.
	 * @param last value assigned to lastName attribute, null is ignored.
	 * @return chainable self-reference.
	 */
	public Customer setName(String first, String last) {
		firstName = first.trim();
		lastName = last.trim();
		return this;
	}

	/**
	 * Setter that splits a single-String name (e.g. "Eric Meyer") into first- and
	 * lastName parts and assigns parts to corresponding attributes.
	 * @param name single-String name to split into first- and lastName parts.
	 * @throws IllegalArgumentException if name argument is null.
	 * @return chainable self-reference.
	 */
	public Customer setName(String name) {
		if(name == null) {
			throw new IllegalArgumentException("name is null");
		}
		return splitName(name.trim());
	}

	/**
	 * Return number of contacts.
	 * @return number of contacts.
	 */
	public int contactsCount() {
		return contacts.size();
	}

	/**
	 * Contacts getter (as {@code String[]}).
	 * @return contacts (as {@code String[]}).
	 */
	public String[ ] getContacts() {
		String[] array = contacts.toArray(new String[0]);
		//warum klappt das nicht return (String[]) contacts.toArray();
		return array;
	}

	/**
	 * Add Customer contact (no duplicates).
	 * @param contact new Customer contact. //Email I guess.
	 * @return chainable self-reference.
	 * @throws IllegalArgumentException if contact argument is null or empty String
	 */
	public Customer addContact(String contact) {		
		if(contact == null || contact.equals("")){
			throw new IllegalArgumentException("argument is null");
		}
		String input = contact
				.replaceAll("\"","")
				.replaceAll("\t", "")
				.replaceAll("\n", "")
				.replaceAll(";", "")
				.replaceAll("'", "")
				.replaceAll("\'", "")
				.replaceAll(",", "")
				.trim();	
		if(input.length()<6) {
			throw new IllegalArgumentException("contact less than 6 characters: \"" + contact +
					"\".");
		}
		if(!contacts.contains(input)) {
			contacts.add(input);
		}
		return this;
	}

	/**
	 *  Delete the i'th contact with i >= 0 and i < contactsCount(), otherwise
	 * method has no effect.
	 * @param i i-th contact deleted.
	 */
	public void deleteContact(int i) {
		if(i>=0 && i<contactsCount()) {
			contacts.remove(i);
		}
	}

	/**
	 * Delete all contacts.
	 */
	public void deleteAllContacts() {
		contacts.clear();
	}

	/**
	 * Split single-String name into last- and first name parts.
	 * @param name single-String name to split into first- and lastName parts.
	 * @throws IllegalArgumentException if name argument is null.
	 * @return chainable self-reference.
	 */
	private Customer splitName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null");
		}

		String temp = name;
		String[] tempSplit;

		if (name.contains(",")) {
			tempSplit = name.split(",");
			temp = exchange(name, ",");
		}

		if (name.contains(";")) {
			tempSplit = name.split(";");
			temp = exchange(name, ";");
		}

		tempSplit = temp.split(" ");
		int length = tempSplit.length;

		firstName = Arrays.stream(tempSplit, 0, length - 1)
				.reduce("", (res, next) -> res + next + " ").trim();
		lastName = tempSplit[length - 1];
		
		return this;
	}

	private String exchange(String name, String regex) {
		String[] values = name.split(regex);
		String result;

		result = values[1].trim() + " " + values[0].trim();
		return result;
	}
	
	public String toString() {
		String output =String.format("| %d | %s, %s | %s |", 
				getId(), getLastName(), getFirstName(), getContacts());

		return output;
	}

}