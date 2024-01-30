import java.util.Comparator;

//Wendy shen, nov 20, 2022
//Description: sort by watched

public class SortByWatched implements Comparator<Episodes>{

	//Purpose: compare this ep to that ep by watched
	//Parameters: ep 1, ep 2
	//Return: which is greater (int)
	public int compare(Episodes ep1, Episodes ep2) {
		if(ep1.getWatched() == ep2.getWatched())
			return 0;
		else if(ep1.getWatched())
			return 1;
		return -1;
	}

}
