import java.util.Comparator;

//Wendy Shen
//Oct 5, 2022
//To sort episodes by title

public class SortByTitle implements Comparator<Episodes> {

	//Purpose: sort by title
	//Parameters: episode 1, episode 2
	//Return: int for which one is greater
	public int compare(Episodes ep1, Episodes ep2) {
		return ep1.getTitle().toLowerCase().compareTo(ep2.getTitle().toLowerCase());
	}

}
