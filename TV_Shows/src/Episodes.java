//Wendy Shen
//Oct 5, 2022
//To store all the info about each episode

public class Episodes implements Comparable<Episodes>{
	private String title;
	private int seasonNum;
	private int episodeNum;
	private boolean watched = false;
	private Time time;

	//constructor
	public Episodes(String title, int seasonNum, int episodeNum, Time time) {
		this.title = title;
		this.seasonNum = seasonNum;
		this.episodeNum = episodeNum;
		this.time = time;
	}

	//Purpose: to watch an episode
	//Parameters: n/a
	//Return: nothing, just change the watched variable
	public void watched() {//sub2, op3
		watched = true;
	}

	//Purpose: to compare episodes by season num and episode num
	//Parameters: the other episode we're comparing it to
	//Return: int for whether this thing is greater or less than other thing
	public int compareTo(Episodes episode) {
		if(this.seasonNum == episode.seasonNum)
			return this.episodeNum - episode.episodeNum;
		return this.seasonNum - episode.seasonNum;
	}

	//Purpose: episode info to string
	//Parameters: none
	//Return: String of info
	public String toString() {//sub2, op2
		return String.format(">Title: %s%n>Season: %d%n>Episode #: %d%n>Watched: %b%n>Time: %s", title, seasonNum, episodeNum, watched, time.toString());
	}

	//getters and setters
	public Time getTime() {
		return time;
	}

	public String getTitle() {
		return title;
	}

	public boolean getWatched() {
		return watched;
	}

	public int getSeason() {
		return seasonNum;
	}

	public int getEpsisodeNum() {
		return episodeNum;
	}
}
