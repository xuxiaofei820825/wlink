package com.iauto.wlink.core.message;

import com.iauto.wlink.core.Message;

public interface BroadcastMessage<T> extends Message<T> {
	long from();
}
