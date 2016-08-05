package com.iauto.wlink.core;

public interface Message<T> {
	String type();

	T payload();
}
