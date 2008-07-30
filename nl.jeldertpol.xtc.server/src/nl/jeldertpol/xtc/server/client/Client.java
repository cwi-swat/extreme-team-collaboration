package nl.jeldertpol.xtc.server.client;

/**
 * Representation of a client  
 * @author Jeldert Pol
 */
public class Client {

	private String nickname;

	public Client(String nickname) {
		super();
		this.nickname = nickname;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}
	
}
