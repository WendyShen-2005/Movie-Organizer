import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.sound.sampled.Line;

//Wendy Shen, nov 20, 2022
//Description: to organize files of shows

public class Driver {
	public static int displayMenu (int menuNum, BufferedReader stdIn) throws IOException {

		int numOptions = 0;
		if (menuNum == 0) {
			System.out.println ("----------  MAIN MENU  -----------");
			System.out.println ("1) Accessing your TV shows list");
			System.out.println ("2) Accessing within a particular TV show");
			System.out.println ("3) Exit");
			System.out.println ("----------------------------------");
			numOptions = 3;
		}
		else if (menuNum == 1) {
			System.out.println ("\n---------  SUB-MENU #1  ----------");
			System.out.println ("1) Display a list of all your TV shows"); // Just the CD titles, numbered in order
			System.out.println ("2) Display info on a particular TV show"); 
			System.out.println ("3) Add a TV show"); // Adds a CD by reading from input file
			System.out.println ("4) Remove (show or season)");
			System.out.println ("5) Show status");
			System.out.println ("6) Return back to main menu.");
			System.out.println ("----------------------------------");
			numOptions = 6;
		}
		else {
			System.out.println ("\n---------  SUB-MENU #2  ----------");
			System.out.println ("1) Display all episodes (in the last sorted order) ");
			System.out.println ("2) Display info on a particular episode ");
			System.out.println ("3) Watch an episode");
			System.out.println ("4) Add an episode");
			System.out.println ("5) Remove episode(s) (4 options)");
			System.out.println ("6) Sort episodes (3 options)");
			System.out.println ("7) Return back to main menu");
			System.out.println ("----------------------------------");
			numOptions = 7;
		}

		System.out.print ("\n\nPlease enter your choice:  ");

		int choice = -1;

		while(choice == -1) {
			try {
				choice = Integer.parseInt (stdIn.readLine ());
				if(choice < 1 || choice > numOptions)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				System.out.print("Please enter an integer that is listed: ");
			}
		}
		return choice;
	}

	//Purpose: add shows
	//Parameters: file name, arraylist to add to
	//Return: non
	public static void addShow(File filename, ArrayList<TVShows> arr) throws IOException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String title = br.readLine();
			String genre = br.readLine();
			TVShows tv = new TVShows(title, genre);
			arr.add(tv);
			String line = br.readLine();
			int seaLen;
			int seaNum;
			int epNum;
			while(line != null) {//while not at end of file
				String[] la = line.split(" ");//since next line is the season num, read that in
				seaNum = Integer.parseInt(la[1]);
				seaLen = Integer.parseInt(br.readLine());//num of eps in next season

				arr.get(arr.size()-1).addSeason(seaNum, seaLen);//add to total num of seasons

				while((line = br.readLine()) != null && seaLen > 0) {//while still in same season
					String[] laa = line.split(" ");
					epNum = Integer.parseInt(laa[1]);//parse the ep num
					String titleNew = br.readLine();
					Time time = new Time(br.readLine());
					tv.addEp(titleNew, seaNum, epNum, time);
					seaLen--;
				}
			}

			Collections.sort(arr.get(arr.size() - 1).getEpisodes());
			Collections.sort(arr.get(arr.size() - 1).getEpisodes(), new SortBySeason());
			Collections.sort(arr.get(arr.size() - 1).getSeasonsList(), new SortSeasonsListBySeason());

		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
	}
	
	//Purpose: remove show
	//Parameters: title = show index, arr arraylist we remove from
	//Return: none
	public static void removeShow(int title, ArrayList <TVShows> arr) {
		arr.remove(title);
	}

	//Purpose: remove season of show
	//Parameters: season, show index, array list we remove from
	//Return: none
	public static void removeShow(int season, int showNum, ArrayList <TVShows> arr) {
		arr.get(showNum).removeSea();//minus 1 season

		int index = 0;//index of where seasons is

		for(int i = 0; i < arr.get(showNum).getSeasonsList().size(); i++) //index of season in seasons list
			if(arr.get(showNum).getSeasonsList().get(i)[0][0] == season) {//once we find season in seasons lsit
				index = i;
				break;
			}

		int remove = arr.get(showNum).indexOfSeason(season);

		for (int i = remove; i < remove + arr.get(showNum).getSeasonsList().get(index)[0][1]; i++) { //while below end index
			arr.get(showNum).decreaseTime(arr.get(showNum).getEpisodes().get(remove).getTime());
			arr.get(showNum).getEpisodes().remove(remove);
		}

		arr.get(showNum).getSeasonsList().remove(index);//remove current season
	}

	//Purpose: display shows
	//Parameters: arraylist we read from
	//Return: none
	public static void dispShows(ArrayList <TVShows> arr) {
		for(int i = 0; i < arr.size(); i++) {
			System.out.print("Show #" + (i + 1) + " : ");
			System.out.println(arr.get(i).getTitle());
		}
	}

	//Purpose: show watch status of a show
	//Parameters: arraylist we read from, show index
	//Return: string of watch status
	public static String watchStatus(ArrayList <TVShows> arr, int showNum) {
		showNum--;
		arr.get(showNum).sortBySea();

		int watchedEps[][] = new int[3][arr.get(showNum).getEpisodes().get(arr.get(showNum).getEpisodes().size() - 1).getSeason()];
		int index = 0, end = 0;
		int prevSeason = arr.get(showNum).getEpisodes().get(0).getSeason();
		for(int i = 0; i < watchedEps[0].length; i++) {//going through each column (each season)
			prevSeason = arr.get(showNum).getEpisodes().get(index).getSeason();

			watchedEps[0][i] = prevSeason;//write in each season to first row
			while(arr.get(showNum).getEpisodes().get(index).getSeason() == prevSeason) {//while the next eps are in the same season
				watchedEps[2][i]++;//adding how many eps there are

				if(arr.get(showNum).getEpisodes().get(index).getWatched())//if watched
					watchedEps[1][i]++;//add 1 to watched

				if(index == arr.get(showNum).getEpisodes().size() - 1){//if we at end of arraylist, get out cause its not guarenteed watchedEps[0].len = num of seasons cause some seasons might be missing
					i = watchedEps[0].length;
					break;
				}
				index++;
			}
			end++;
		}

		int watchedEpss = 0;
		int eps = 0;

		String s = "";
		s += arr.get(showNum).getTitle() + " Watch Status: \n";	


		for(int i = 0; i < end; i++) {//add each season's num of eps watched/total eps to string
			//System.out.println(watchedEps[0].length);
			s += "> Season " + watchedEps[0][i] + ": " + watchedEps[1][i] + " out of " + watchedEps[2][i] + " watched \n";
			watchedEpss += watchedEps[1][i];
			eps += watchedEps[2][i];
			//System.out.println(i + " " + watchedEps[1][i]);
		}

		int completedSea = 0;
		for(int i = 0; i < end; i++) {//num of seasons completed
			if(watchedEps[1][i] == watchedEps[2][i])
				completedSea++;
		}
		s+= "\n> Seasons completed: " + completedSea;

		s += "\n> Episodes watched: " + watchedEpss + " out of " + eps;
		return s;

	}

	//Purpose: make sure integer inputs are valid
	//Parameters: q = string we read in, bufferedReader, max value that the input can be
	//Return: valid integer input
	public static int invalidInt(String q, BufferedReader br, int maxValue) throws IOException {
		int numInput = 0; 

		System.out.print(q);		
		while(numInput == 0) {
			try {
				numInput = Integer.parseInt(br.readLine());
				if(numInput > maxValue || numInput < 1)
					throw new NumberFormatException ();
			} catch(NumberFormatException e) {
				System.out.println("Invalid, " + q.toLowerCase());
				numInput = 0;
			}
		}

		return numInput;
	}


	@SuppressWarnings("resource")
	public static void main (String[] args) throws IOException {
		BufferedReader stdIn = new BufferedReader (new InputStreamReader (System.in));
		int mainMenuChoice, subMenuChoice;
		mainMenuChoice = displayMenu (0, stdIn);

		ArrayList <TVShows> showsList = new ArrayList<>();

		while(mainMenuChoice != 3) {//if not choosing to exit
			if (mainMenuChoice == 1) {//if show menu 
				subMenuChoice = displayMenu (1, stdIn);
				if(subMenuChoice == 3) {//add shows
					String fileName = "";
					while(fileName.equals("")) {//while we have not entered a file name
						try {
							System.out.print("Please enter your file name (ex: umbrella.txt): ");
							fileName = stdIn.readLine();
							new FileInputStream(fileName);
						} catch(FileNotFoundException e) {
							System.out.print("File not found. ");
							fileName = "";

						}
					}
					addShow(new File(fileName), showsList);

				} else if(showsList.size() == 0) {
					System.out.println("ERROR. No shows. Please select option 3 in the shows menu.");
					subMenuChoice = -1;
				}

				if(subMenuChoice == 1) {//display shows
					dispShows(showsList);
				} else if(subMenuChoice == 2) {//info on a show
					dispShows(showsList);
					int showNum = invalidInt("Please enter what show # you want: ", stdIn, showsList.size());
					System.out.println(showsList.get(showNum - 1));
				} else if(subMenuChoice == 4) {//remove show
					System.out.println("Remove by...\n1. Show title\n2. Season #\n");
					int choiceNum = invalidInt("Please enter your choice #: ", stdIn, 2);
					if(choiceNum == 1) {//remove 1 show
						dispShows(showsList);
						int title = invalidInt("Please enter the show # you want to remomve: ", stdIn, showsList.size());
						removeShow(title - 1, showsList);
					} else {//remove by season
						dispShows(showsList);
						int title = invalidInt("Please enter the show # you want to remomve: ", stdIn, showsList.size());
						showsList.get(title - 1).sortBySea();
						System.out.println(showsList.get(title - 1).dispSeasons());
						int seasonNum = showsList.get(title - 1).getSeasonsList().get(showsList.get(title - 1).getSeasonsList().size() - 1)[0][0];
						int season = invalidInt("Please enter what season of this show you want to remove: ", stdIn, seasonNum);

						while(showsList.get(title - 1).indexOfSeasonInList(season) == -1) {
							System.out.println("Sesaon does not exist. ");
							season = invalidInt("Please enter what season of this show you want to remove: ", stdIn, seasonNum);
						}

						removeShow(season, title - 1, showsList);
					}
				} else if(subMenuChoice == 5) {//view status 
					dispShows(showsList);
					int title = invalidInt("Please enter what show you want to view watch status for: ", stdIn, showsList.size());
					System.out.println(watchStatus(showsList, title));
				} else if(subMenuChoice == 6) {//back
					mainMenuChoice = displayMenu(0, stdIn);
				}
				//EPISODE INFO----------------------------------------------------------------------------------------
			} else if (mainMenuChoice == 2 && !showsList.isEmpty()) {
				dispShows(showsList);

				int showNum = invalidInt("Please enter your show number: ", stdIn, showsList.size());
				showNum--;
				System.out.println(showsList.get(showNum).dispSeasons());
				int seasonNum = invalidInt("Please enter your season number: ", stdIn, showsList.get(showNum).getSeasonsList().get(showsList.get(showNum).getSeasonsList().size() - 1)[0][0]);
				while(showsList.get(showNum).indexOfSeasonInList(seasonNum) == -1)
					seasonNum = invalidInt("Please enter your season number: ", stdIn, showsList.get(showNum).getSeasonsList().get(showsList.get(showNum).getSeasonsList().size() - 1)[0][0]);

				int startNum = showsList.get(showNum).indexOfSeason(seasonNum);
				int endNum = showsList.get(showNum).lastIndexOfSeason(seasonNum) - 1;

				subMenuChoice = -1;

				while(subMenuChoice != 7) {//display episodes / specific info
					subMenuChoice = displayMenu (2, stdIn);

					if(subMenuChoice == 1)//display all eps
						showsList.get(showNum).dispEpNames(startNum, endNum);
					else if(subMenuChoice == 2) {//display info on 1 ep
						showsList.get(showNum).dispEpNames(startNum, endNum);
						int epsNum = invalidInt("Please enter which ep # you wish to see info on: ", stdIn, showsList.get(showNum).getSeasonsList().get(showsList.get(showNum).indexOfSeasonInList(seasonNum))[0][1]);
						while(epsNum < 1) {
							System.out.println("Episode must be in list.");
							epsNum = invalidInt("Please enter which ep # you wish to see info on: ", stdIn, endNum - 1);
						}
						System.out.println();
						showsList.get(showNum).dispEpNames(startNum, endNum);
						System.out.println();
						System.out.println(showsList.get(showNum).getEpisodes().get(epsNum + startNum - 1));
					} else if (subMenuChoice == 3) {//set ep to watched
						showsList.get(showNum).dispEpNames(startNum, endNum);	
						int choice = invalidInt("Enter which episode you watched: ", stdIn, showsList.get(showNum).getEpisodes().size());
						showsList.get(showNum).getEpisodes().get(choice - 1 + startNum).watched();
					} else if (subMenuChoice == 4) {// add ep
						//ask for title, ep, time
						System.out.println("1. Add ep. to current season\n2. Add ep. to new season");
						int choice = invalidInt("Please enter your option #: ", stdIn, 2);
						String newTitle = "", newTime = "";
						int newSeason = -1, newEpNum = -1;
						while(newTime.equals(""))
							if(choice == 1) {
								if(newSeason == -1)
									newSeason = seasonNum;
								//new title
								System.out.print("Please enter your title: ");
								newTitle = stdIn.readLine();
								//new ep num
								newEpNum = invalidInt("Please enter your new episode #: ", stdIn, Integer.MAX_VALUE);
								while(Collections.binarySearch(showsList.get(showNum).getEpisodes(), new Episodes("", newSeason, newEpNum, new Time("00:00:00"))) >= 0) {
									System.out.print("Ep num already exists. ");
									newEpNum = invalidInt("Please enter your new episode #: ", stdIn, Integer.MAX_VALUE);
								}
								//new time
								System.out.print("Please enter the time of this episode (hh:mm:ss): ");
								while(newTime.equals("")) {
									try {
										newTime = stdIn.readLine();
										new Time(newTime);
									} catch(NumberFormatException | IndexOutOfBoundsException e) {
										System.out.print("Please enter the time of this episode (hh:mm:ss): ");
										newTime = "";
									}
								}
								
								showsList.get(showNum).addEpAndSeason(newTitle, newSeason, newEpNum, new Time(newTime));
								endNum = showsList.get(showNum).lastIndexOfSeason(seasonNum) - 1;
							} else {
								newSeason = invalidInt("Please enter your new season: ", stdIn, Integer.MAX_VALUE);
								while(showsList.get(showNum).indexOfSeasonInList(newSeason) >= 0) {//while ep is listed
									System.out.print("Season already exists. ");
									newSeason = invalidInt("Please enter your new season: ", stdIn, Integer.MAX_VALUE);
								}
								//newSeason = showsList.get(showNum).index
								choice = 1;
							}

					} else if (subMenuChoice == 5) {//remove ep num, title, range, watched
						System.out.println("Remove by...\n1. Episode #\n2. Title\n3. Range (ex: ep #1 - 3)\n4. Watched episodes");
						int choice = invalidInt("Please enter your option #: ", stdIn, 4);
						if(choice == 1) {//1 ep num
							showsList.get(showNum).dispEpNames(startNum, endNum);
							int ep = invalidInt("Please enter your episode #: ", stdIn, showsList.get(showNum).getEpisodes().get(showsList.get(showNum).getEpisodes().size() - 1).getEpsisodeNum());
							while(Collections.binarySearch(showsList.get(showNum).getEpisodes(), new Episodes("", 1, ep, new Time("00:00:00"))) < 0) {//while ep not in list
								System.out.print("Episode # not found. ");
								ep = invalidInt("Please enter your episode #: ", stdIn, showsList.get(showNum).getEpisodes().size());
							}

							showsList.get(showNum).removeEp(showsList.get(showNum).indexOfEpInSeasonList(seasonNum, ep), seasonNum);
						} else if(choice == 2) {//ep title
							showsList.get(showNum).dispEpNames(startNum, endNum);
							System.out.print("Please enter what title you wish to remove: ");
							String title = stdIn.readLine();

							showsList.get(showNum).removeEp(seasonNum, title, startNum, endNum);

						} else if(choice == 3) {//range
							showsList.get(showNum).dispEpNames(startNum, endNum);
							int ep1 = invalidInt("Please enter your first episode #: ", stdIn, showsList.get(showNum).getSeasonsList().get(showsList.get(showNum).getSeasonsList().size() - 1)[0][1]);

							while(showsList.get(showNum).indexOfEpInSeasonList(seasonNum, ep1) < 0) {//while ep is not in list
								System.out.print("Episode does not exist. ");
								ep1 = invalidInt("Please enter your first episode #: ", stdIn, showsList.get(showNum).getSeasonsList().get(showsList.get(showNum).getSeasonsList().size() - 1)[0][1]);
							}

							int ep2 = invalidInt("Please enter your second episode #: ", stdIn, showsList.get(showNum).getSeasonsList().get(showsList.get(showNum).getSeasonsList().size() - 1)[0][1]);
							while(showsList.get(showNum).indexOfEpInSeasonList(seasonNum, ep1) < 0 || ep2 < ep1) {//while ep is not in list and is less than ep 1
								System.out.print("Episode does not exist or is less than ep 1. ");
								ep2 = invalidInt("Please enter your first episode #: ", stdIn, showsList.get(showNum).getSeasonsList().get(showsList.get(showNum).getSeasonsList().size() - 1)[0][1]);
							}
							showsList.get(showNum).removeEp(seasonNum, showsList.get(showNum).indexOfEpInSeasonList(seasonNum, ep1), showsList.get(showNum).indexOfEpInSeasonList(seasonNum, ep2));
						} else//watched
							showsList.get(showNum).removeEp(seasonNum);
						endNum = showsList.get(showNum).lastIndexOfSeason(seasonNum) - 1;
					} else if (subMenuChoice == 6) {//sort by num, title, time
						System.out.println("How do you wish to sort your episodes: \n1. Episode #\n2. Title\n3. Time");
						int choice = invalidInt("Please enter your option #: ", stdIn, 3);
						System.out.println(choice);
						if(choice == 1) {
							showsList.get(showNum).sort(1, startNum, endNum);
						} else if(choice == 2) {
							showsList.get(showNum).sort(3, startNum, endNum);
						} else 
							showsList.get(showNum).sort(2, startNum, endNum);

						showsList.get(showNum).dispEpNames(startNum, endNum);

					}
				} 
				mainMenuChoice = displayMenu(0, stdIn);
			} else if(mainMenuChoice == 2) {
				System.out.println("Error, no shows to choose from.");
				mainMenuChoice = displayMenu(0, stdIn);
			} else {
				System.out.println("Invalid. Please enter again: ");
				mainMenuChoice = displayMenu (0, stdIn);
			}
		}
	}
}
