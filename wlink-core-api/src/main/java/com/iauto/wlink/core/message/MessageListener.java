package com.iauto.wlink.core.message;

import com.iauto.wlink.core.session.Session;

public interface MessageListener {

	void onMessage( Session session, CommunicationMessage message );

}
