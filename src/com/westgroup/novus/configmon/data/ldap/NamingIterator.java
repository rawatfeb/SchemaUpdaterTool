package com.westgroup.novus.configmon.data.ldap;

import java.util.Iterator;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Implements the Iterator and Iterable interfaces for a NamingEnumeration. Allows fancy loop traversal. 
 * 
 * @author David S. Sundry
 * @since 04/05/2010
 * @param <T>
 */
public class NamingIterator<T> implements Iterator<T>, Iterable<T> {
	private NamingEnumeration<T> iter;
	
	/**
	 * Save a copy of a NamingEnumeration object to be used for traversal
	 * 
	 * @param e A NamingEnumeration
	 */
	public NamingIterator(NamingEnumeration<T> e) {
		this.iter = e;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		try {
			return iter.hasMore();
		}
		catch (NamingException x) {
			return false;//TODO
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public T next() {
		try {
			return iter.next();
		}
		catch (NamingException x) {
			return null;//TODO
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException("Not supported");
	}
}
