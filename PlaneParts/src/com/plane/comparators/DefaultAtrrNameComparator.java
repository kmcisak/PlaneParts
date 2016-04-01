package com.plane.comparators;

import java.util.Comparator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DefaultAtrrNameComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		return ((Element) o1).getAttribute("name").compareTo(((Element) o2).getAttribute("name"));

	}
}