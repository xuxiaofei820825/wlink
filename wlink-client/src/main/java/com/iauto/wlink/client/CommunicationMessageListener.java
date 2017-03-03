package com.iauto.wlink.client;

import com.iauto.wlink.core.message.CommunicationMessage;

public interface CommunicationMessageListener {

	public void onMessage( CommunicationMessage commMessage );

}
