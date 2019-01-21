package com.example;


import java.util.Objects;

/**
 * Implements Map interface based on single linked list with String as both key and value types.
 */
public class ListMap {
	private ListElement head;

	/**
	 * Simple immutable structure for storing both key and value.
	 */
	static public class Entry {
		final public String key;
		final public String value;

		/**
		 * Constructs Entry with the specified key and value
		 */
		public Entry(String key, String value) {
			this.value = value;
			this.key = key;
		}
	}

	private class ListElement {
		final String key;
		String value;
		private ListElement prev, next;

		private ListElement(String key, String value, ListElement prev, ListElement next) {
			this.key = key;
			this.value = value;
			this.prev = prev;
			this.next = next;
		}

		private void eraseFromList() {
			changeNextsPrev(prev);
			changePrevsNext(next);
		}

		private void insertBefore(ListElement elem) {
			elem.prev = prev;
			elem.next = this;
			changePrevsNext(elem);
			prev = elem;
		}

		private void changeNextsPrev(ListElement newPrev) {
			if (next != null) {
				next.prev = newPrev;
			}
		}

		private void changePrevsNext(ListElement newNext) {
			if (prev != null) {
				prev.next = newNext;
			}
		}

	}

	/**
	 * Constructs an empty ListMap
	 */
	public ListMap() {}

	/**
	 * Checks if specified key is present among stored keys.
	 * @return     <code>true</code> if this ListMap contains such key;
	 *             <code>false</code> otherwise.
	 */
	public boolean contains(String key) {
		for (ListElement p = head; p != null; p = p.next) {
			if (Objects.equals(p.key, key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns value mapped to the specified key or null if this ListMap contains no mapping for the key.
	 * @return  the value, mapped to the specified key if it is present;
	 * 			<code>null</code> otherwise
	 */
	public String get(String key) {
		for (ListElement p = head; p != null; p = p.next) {
			if (Objects.equals(p.key, key)) {
				return p.value;
			}
		}
		return null;
	}

	/**
	 * Stores value by key. Rewrites existing value.
	 * @return  previous value stored by the specified key,
	 * 			or <code>null</code> if there were no previous value
	 */
	public String put(String key, String value) {
		String oldValue = remove(key);
		ListElement newHead = new ListElement(key, value, null, null);

		if (head != null) {
			head.insertBefore(newHead);
			head = newHead;
		} else {
			head = newHead;
		}
		return oldValue;
	}

	/**
	 * Removes the mapping for the specified key (if present)
	 * @return 	the value stored by the specified key,
 	 * 			or <code>null</code> if there were no previous value
	 */
	public String remove(String key) {
		for (ListElement p = head; p != null; p = p.next) {
			if (Objects.equals(p.key, key)) {
				eraseListElement(p);
				return p.value;
			}
		}
		return null;
	}

	/**
	 * Removes all mappings from ListMap
	 */
	public void clear() {
		head = null;
	}

	/**
	 * Returns the number of mappings stored.
	 * @return	the number of mappings stored
	 */
	public int size() {
		int size = 0;

		for (ListElement p = head; p != null; p = p.next) {
			size++;
		}
		return size;
	}

	/**
	 * Removes one mapping from ListMap.
	 * @return 	an entry for removed mapping or <code>null</code> if empty
	 */
	public Entry pop() {
		var headOld = head;

		if (head == null) {
			return null;
		}
		eraseListElement(head);
		return new Entry(headOld.key, headOld.value);
	}

	private void eraseListElement(ListElement element) {
		if (head == element) {
			head = element.next;
		}
		element.eraseFromList();
	}
}

