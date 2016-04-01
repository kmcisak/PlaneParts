package com.plane.comparators;

import java.util.Comparator;

import org.w3c.dom.Element;

public class AtrrQuantityComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		Integer el1, el2;
		el1 = Integer.parseInt(((Element) o1).getAttribute("quantity"));
		el2 = Integer.parseInt(((Element) o2).getAttribute("quantity"));
		return el1.compareTo(el2);

	}
}