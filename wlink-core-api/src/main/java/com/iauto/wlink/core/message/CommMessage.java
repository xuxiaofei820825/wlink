package com.iauto.wlink.core.message;

import com.iauto.wlink.core.Message;

public interface CommMessage<T> extends Message<T> {
	long from();

	long to();
}
