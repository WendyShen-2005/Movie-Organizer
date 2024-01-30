import java.util.Comparator;

//Wendy Shen
//Oct 5, 2022
//To sort episodes by time

public class SortByTime implements Comparator<Episodes>{

	//Purpose: sort by time
	//Parameters: episode 1, episode 2
	//Return: int for which one is greater
	public int compare(Episodes ep1, Episodes ep2) {

		return ep1.getTime().compareTo(ep2.getTime());
	}


}
