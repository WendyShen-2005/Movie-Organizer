import java.util.Comparator;


//Wendy Shen, nov 20, 2022
//Description: sort by season
public class SortBySeason implements Comparator<Episodes> {

	//Purpose: compare episodes by season
	//Parameters: ep 1, ep 2
	//Return: which is greater (int)
	public int compare(Episodes ep1, Episodes ep2) {
		return ep1.getSeason() - ep2.getSeason();
	}

}
