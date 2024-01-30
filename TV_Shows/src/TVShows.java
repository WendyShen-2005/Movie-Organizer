import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class TVShows implements Comparable<TVShows>{

	Scanner in = new Scanner(System.in);

	private String title;
	private String genre;
	private int numSeasons;
	private static int numShows;
	private Time time = new Time("00:00:00");

	Time stndTime = new Time("00:00:00");

	private ArrayList <Episodes> epsList = new ArrayList<>();
	private ArrayList <Integer[][]> seasons = new ArrayList<>();//how many eps in each season

	//constructor
	public TVShows(String title, String genre){
		Integer[][] a = new Integer[1][2];
		a[0][0] = 0;
		a[0][1] = 0;
		seasons.add(a);
		this.title = title;
		this.genre = genre;
		numShows++;
	}
	
	public String dispSeasons() {
		String s = "";
		for(int i = 1; i < seasons.size(); i++)
			s += "Season " + seasons.get(i)[0][0] + "\n";
		return s;
	}

	//Description: add a season
	//Parameters: season num, len
	//Return: none
	public void addSeason(int season, int seaLen) {
		Integer[][] a = new Integer[1][2];
		a[0][0] = season;
		a[0][1] = seaLen;
		int index = indexOfSeasonInList(season);//see if season num exists

		if(index < 0) {//if season not there
			seasons.add(a);
			numSeasons++;
			Collections.sort(seasons, new SortSeasonsListBySeason());
		} else
			seasons.get(index)[0][1]++;
	}

	//Description: subtract an ep from the seasons array
	//Parameters: season num
	//Return: none
	public void subtractFromSeason(int season) {
		int index = 0;
		while(seasons.get(index)[0][0] != season)//while have not found season
			index++;

		Integer[][] b = new Integer[1][2];
		b[0][0] = season;
		b[0][1] = seasons.get(index)[0][1] - 1;
		seasons.set(index, b);
	}

	//Description: add an ep to seasons array
	//Parameters: season num
	//Return: none
	public void addFromSeason(int season) {
		int index = 0;
		while(seasons.get(index)[0][0] != season)//while not found season
			index++;

		Integer[][] b = new Integer[1][2];
		b[0][0] = season;
		b[0][1] = seasons.get(index)[0][1] + 1;
		System.out.println(b[0][1] + " " + seasons.get(index)[0][1]);
		seasons.set(index, b);
	}

	//Description: start index of season in eps list
	//Parameters: season num
	//Return: index of where season starts
	public int indexOfSeason(int season) {
		int index = 0;
		for(int i = 1; i < seasons.size(); i++) {
			index += seasons.get(i - 1)[0][1];//since list items before store how long their things are, add up total len to get index
			if(seasons.get(i)[0][0] == season)
				break;
		}
		return index;
	}

	//Description: index that season ends at in eps lit
	//Parameters: season num
	//Return: none
	public int lastIndexOfSeason(int season) {
		int index = 0;
		for(int i = 1; i < seasons.size(); i++) {
			index += seasons.get(i)[0][1];

			if(seasons.get(i)[0][0] == season) 
				break;
		}
		return index;
	}

	//Description: index of season in seasons list
	//Parameters: season num
	//Return: index of where season is in seasons list
	public int indexOfSeasonInList(int season) {
		for(int i = 1; i < seasons.size(); i++) {
			if(seasons.get(i)[0][0] == season)
				return i;
		}
		return -1;
	}

	//Description: index of where the episode in that season (aka sees if there's an ep 1 in season 2 so that it doesn't conflict with the ep 1 in season 3)
	//Parameters: season num, ep num
	//Return: 
	public int indexOfEpInSeasonList(int season, int ep) {
		int index = indexOfSeason(season);
		sort(1, indexOfSeason(season), lastIndexOfSeason(season));
		for(int i = index; i < lastIndexOfSeason(season); i++) {//between the start and end of season index in eps list
			if(epsList.get(i).getEpsisodeNum() == ep) {
				return i;
			}
		}
		return -1;
	}

	//Description: episode info to string
	//Parameters: none
	//Return: string of info
	public String toString() {
		return String.format("> Title: %s%n> Genre: %s%n> Num of Seasons: %d%n> Num of Episodes: %d%n> Total Time: %s", 
				title, genre, numSeasons, epsList.size(), time.toString());
	}


	//Description: displays ep names between start and end
	//Parameters: start ind and end ind
	//Return: none
	public void dispEpNames(int start, int end) {//sub2, op1
		for(int i = start; i < end + 1; i++) {
			System.out.print("Episode " + epsList.get(i).getEpsisodeNum() + ". ");
			System.out.println(epsList.get(i).getTitle());	
		}
	}

	//Description: add an episode 
	//Parameters: title, season num, ep num, time
	//Return: none
	public void addEp(String title, int sea, int ep, Time time) {
		epsList.add(new Episodes(title, sea, ep, time));
		this.time.addTime(time.getHours(), time.getMins(), time.getSecs());
		Collections.sort(epsList, new SortBySeason());
	}

	//Description: if adding an ep and new season
	//Parameters: title, season num, ep num, time
	//Return: nothing
	public void addEpAndSeason(String title, int sea, int ep, Time time) {
		addSeason(sea, 1);
		addEp(title, sea, ep, time);
	}

	//Description: remove 1 ep
	//Parameters: ep num, season num
	//Return: none
	public void removeEp(int epNum, int seasonNum) {//sub2, op5 -- one episode
		decreaseTime(epsList.get(epNum).getTime());
		epsList.remove(epNum);
		subtractFromSeason(seasonNum);
	}

	//Description: remove eps by title
	//Parameters: season num, title, start ind, end ind
	//Return: none
	public void removeEp(int seasonNum, String title, int startInd, int endInd) {//sub2, op5 -- by title
		sort(3, startInd, endInd);

		ArrayList<Episodes> eps = new ArrayList<>();
		for(int i = startInd; i < endInd; i++) 
			eps.add(epsList.get(i));

		int startNum = Collections.binarySearch(eps, new Episodes(title, seasonNum, 1, stndTime), new SortByTitle());

		while(startNum >= 0) {//while can still find that ep num
			decreaseTime(epsList.get(startNum + startInd).getTime());
			epsList.remove(startNum + startInd);
			eps.remove(startNum);
			startNum =  Collections.binarySearch(epsList, new Episodes(title, seasonNum, 1, stndTime), new SortByTitle());
			subtractFromSeason(seasonNum);
		}
	}

	//Description: remove range of eps
	//Parameters: season num, start ind, end ind
	//Return: none
	public void removeEp(int season, int epInd1, int epInd2) {//sub2, op5 -- range
		while(epInd1 <= epInd2) {//while between ind 1 and ind 2
			decreaseTime(epsList.get(epInd1).getTime());

			epsList.remove(epInd1);
			subtractFromSeason(season);
			epInd2--;
			if(epInd2 == -1)
				break;
		}
	}

	//Description: remove watched eps
	//Parameters: season
	//Return: nothing
	public void removeEp(int season) {//watched
		sort(4, indexOfSeason(season), lastIndexOfSeason(season));

		int index = lastIndexOfSeason(season) - 1;
		while(epsList.get(index).getWatched()) {
			decreaseTime(epsList.get(index).getTime());
			subtractFromSeason(season);
			epsList.remove(index);
			index--;
		}
	}

	//Description: 
	//Parameters: 
	//Return: 
	public void removeSea() {
		numSeasons--;
	}

	//Description: decrease time here
	//Parameters: time that we decrease our time by
	//Return: nothing
	public void decreaseTime(Time time) {
		this.time.decreaseTime(time.getHours(), time.getMins(), time.getSecs());
	}

	//Description: sort eps by season
	//Parameters: none
	//Return: none
	public void sortBySea() {
		Collections.sort(epsList, new SortBySeason());
	}

	//Description: compare this tv show to that tv show
	//Parameters: other tv show
	//Return: int of which is greater
	public int compareTo(TVShows tv) {
		return this.title.compareTo(tv.title);
	}

	//Description: sort eps in this season by: ep num/time/title/watched
	//Parameters: option, start ind, end ind
	//Return: none
	public void sort(int option, int startInd, int endInd) {
		if(option == 1) {//sort by ep num
			Episodes[] tempSort = new Episodes[endInd - startInd];//array to store portion of list we're sorting
			for(int i = startInd; i < endInd; i++) //add season to new array
				tempSort[i - startInd] = epsList.get(i);

			Arrays.sort(tempSort);

			for(int i = startInd; i < endInd; i++) //add everything back to original arraylist
				epsList.set(i, tempSort[i - startInd]);

		} else if(option == 2) {//sort by time
			Episodes[] tempSort = new Episodes[endInd - startInd];//array to store portion of list we're sorting
			for(int i = startInd; i < endInd; i++) //add season to new array
				tempSort[i - startInd] = epsList.get(i);

			Arrays.sort(tempSort, new SortByTime());

			for(int i = startInd; i < endInd; i++) //add everything back to original arraylist
				epsList.set(i, tempSort[i - startInd]);
		} else if(option == 3) {//sort by title
			Episodes[] tempSort = new Episodes[endInd - startInd];//array to store portion of list we're sorting
			for(int i = startInd; i < endInd; i++) //add season to new array
				tempSort[i - startInd] = epsList.get(i);

			Arrays.sort(tempSort, new SortByTitle());

			for(int i = startInd; i < endInd; i++) //add everything back to original arraylist
				epsList.set(i, tempSort[i - startInd]);
		} else if(option == 4) {//sort by watched
			Episodes[] tempSort = new Episodes[endInd - startInd];//array to store portion of list we're sorting
			for(int i = startInd; i < endInd; i++) //add season to new array
				tempSort[i - startInd] = epsList.get(i);
			Arrays.sort(tempSort, new SortByWatched());

			for(int i = startInd; i < endInd; i++) //add everything back to original arraylist
				epsList.set(i, tempSort[i - startInd]);
		}
	}

	//getters and setters
	public String getTitle() {
		return title;
	}

	public ArrayList <Episodes> getEpisodes(){
		return epsList;
	}

	public int getSeasons() {
		return numSeasons;
	}


	public ArrayList<Integer[][]> getSeasonsList(){
		return seasons;
	}

}
