package com.iauto.wlink.server;

import org.apache.commons.lang.StringUtils;

public class AppConfig {
	private int port;
	private int heartbeatInterval;
	private boolean isSSLUsed = false;
	private String cerFile;
	private String keyFile;
	private String keyPassword;

	public int getPort() {
		return port;
	}

	public void setPort( int port ) {
		this.port = port;
	}

	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval( int heartbeatInterval ) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public boolean isSSLUsed() {
		return isSSLUsed;
	}

	public void setSSLUsed( boolean isSSLUsed ) {
		this.isSSLUsed = isSSLUsed;
	}

	public String getCerFile() {
		return cerFile;
	}

	public void setCerFile( String cerFile ) {
		this.cerFile = cerFile;
	}

	public String getKeyFile() {
		return keyFile;
	}

	public void setKeyFile( String keyFile ) {
		this.keyFile = keyFile;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public void setKeyPassword( String keyPassword ) {
		this.keyPassword = keyPassword;
	}

	public String toString() {
		return String.format( "{port: %d, heartbeatInterval: %d, isSSLUsed: %b, cerFile: %s}",
			this.port, this.heartbeatInterval, this.isSSLUsed,
			this.cerFile, this.keyFile );
	}

	public static final class Builder {

		private int port;
		private int heartbeatInterval;
		private boolean isSSLUsed = true;
		private String cerFile;
		private String keyFile;
		private String keyPassword;

		public static Builder newBuilder() {
			return new Builder();
		}

		public Builder port( int port ) {
			this.port = port;
			return this;
		}

		public Builder heartbeatInterval( int heartbeatInterval ) {
			this.heartbeatInterval = heartbeatInterval;
			return this;
		}

		public Builder useSSL( boolean isSSLUsed ) {
			this.isSSLUsed = isSSLUsed;
			return this;
		}

		public Builder cerFile( String cerFile ) {
			this.cerFile = cerFile;
			return this;
		}

		public Builder keyFile( String keyFile ) {
			this.keyFile = keyFile;
			return this;
		}

		public Builder keyPassword( String keyPassword ) {
			this.keyPassword = keyPassword;
			return this;
		}

		public AppConfig build() {
			AppConfig config = new AppConfig();

			if ( port > 0 ) {
				config.setPort( port );
			}
			if ( heartbeatInterval > 0 ) {
				config.setHeartbeatInterval( heartbeatInterval );
			}
			config.setSSLUsed( isSSLUsed );
			if ( StringUtils.isNotBlank( cerFile ) ) {
				config.setCerFile( cerFile );
			}
			if ( StringUtils.isNotBlank( keyFile ) ) {
				config.setKeyFile( keyFile );
			}
			if ( StringUtils.isNotBlank( keyPassword ) ) {
				config.setKeyPassword( keyPassword );
			}

			return config;
		}
	}
}
