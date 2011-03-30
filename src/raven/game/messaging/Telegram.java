package raven.game.messaging;

public class Telegram implements Comparable<Telegram> {
	public long dispatchTime;
	public int senderID;
	public int receiverID;
	public RavenMessage msg;
	public Object extraInfo;
	
	public Telegram() {
		dispatchTime = -1;
		senderID = -1;
		receiverID = -1;
		msg = RavenMessage.MSG_BLANK;
		
	}
	public Telegram(long dispatchTime, int senderID, int receiverID, RavenMessage msg, Object extraInfo) {
		this.dispatchTime = dispatchTime;
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.msg = msg;
		this.extraInfo = extraInfo;
	}
	
	@Override
	public int compareTo(Telegram other) {
		return (int)(this.dispatchTime - other.dispatchTime);
	}
}
