
public class ClashPlayer {
	String id;
	int lossCount;
	
	public ClashPlayer(String identifier)
	{
		id = identifier;
	}
	
	public int incrementLossCount()
	{
		lossCount++;
		return lossCount;
	}
}
