import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

//Wendy Shen, Nov 18, 2022
//Description: Navigation tool for shows but in graphics

public class DriverButFancy extends JPanel implements MouseListener, Runnable, MouseMotionListener, KeyListener{
	static double scene = 0;
	static JFrame frame = new JFrame();
	static JButton menuShowB, menuEpsB, back;
	static JPanel panel;
	static int mouseX = -1, mouseY = -1;
	static int mouseX2 = -1, mouseY2 = -1;
	static Font buttonFont, ani;
	long startTime, timeElapsed;
	int frameCount = 0;
	int FPS = 60;
	Thread thread;
	final static int W = 1200;
	static final int H = 700;
	static String tempString = "";
	static Color boldPink = new Color(241, 95, 192), lightPink = new Color(255, 224, 245), good = new Color(41, 133, 61);
	static boolean notFound = false, fileFound = false;
	static int scroll = 0;

	static int showNum = -1, seasonNum = -1;
	static boolean scrollable = false;
	static int scrollListSize = 0;

	static boolean error = false, typing = false, submit = false;

	static String fileName = "", newTitle = "", newEpNum = "", newTime = "", newSeason = "";
	static double returnScene = -1;//TODO

	static int epNum = -1, startInd = 0, endInd = 0;

	static int ep1 = -1, ep2 = -1;
	static ArrayList <TVShows> showsList = new ArrayList<>();

	static int addEpCounter = 1;

	//constructor
	public DriverButFancy () throws FontFormatException, IOException {
		thread = new Thread(this);
		thread.start();

		buttonFont = Font.createFont(Font.TRUETYPE_FONT, new File("Gamer.ttf")).deriveFont(70f);
		ani = Font.createFont(Font.TRUETYPE_FONT, new File("retganon.ttf")).deriveFont(40f);

		addMouseListener (this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setFocusable(true);
	}

	//Description: draws a button 
	//Parameters: graphics, buttName = text that will be on button, xPos & yPos = coordinates, sceene = what scene/screen to change to
	//Return: none
	public static void button(Graphics g, String buttName, int xPos, int yPos, double sceene) throws FontFormatException, IOException {
		g.setFont(buttonFont);
		int w = g.getFontMetrics().stringWidth("  " + buttName + " ");
		Image butt1, butt2, butt3;
		int buttW = 53;

		if(mouseX2 >= xPos && mouseX2 <= xPos + w && mouseY2 >= yPos && mouseY2 <= yPos + 70) {//if hovering
			butt1 = Toolkit.getDefaultToolkit().getImage("buttOnleft.png");
			butt2 = Toolkit.getDefaultToolkit().getImage("buttOnmid.png");
			butt3 = Toolkit.getDefaultToolkit().getImage("buttOnright.png");
			g.drawImage(butt1, xPos - 10, yPos - 10, buttW + 10, 90, null);
			g.drawImage(butt2, xPos + buttW, yPos - 10, w - buttW - 10, 90, null);
			g.drawImage(butt3, w + xPos - buttW + 10, yPos - 10, buttW + 10, 90, null);
		} else {
			butt1 = Toolkit.getDefaultToolkit().getImage("buttOffleft.png");
			butt2 = Toolkit.getDefaultToolkit().getImage("buttOffmid.png");
			butt3 = Toolkit.getDefaultToolkit().getImage("buttOffright.png");
			g.drawImage(butt1, xPos, yPos, buttW, 70, null);
			g.drawImage(butt2, xPos + buttW, yPos, w - buttW - 10, 70, null);
			g.drawImage(butt3, w + xPos - buttW, yPos, buttW, 70, null);
		}

		g.setColor(boldPink);
		g.drawString(" " + buttName, xPos + 15, yPos + 48);

		if(mouseX >= xPos && mouseX <= xPos + w && mouseY >= yPos && mouseY <= yPos + 70) {//if clicked, change scene
			scene = sceene;
			mouseX = -1;
			mouseY = -1;
		}
	}

	//Description: shortcut to draw a back button
	//Parameters: graphics, back = what scene/screen to change to
	//Return: none
	public void back (Graphics g, double back) throws FontFormatException, IOException {
		button(g, "< Back", 50, H - 130, back);
	}

	//Description: draws the text field to enter stuff
	//Parameters: graphics, input = changes scene to user input screen
	//Return: none
	public String textField(Graphics g, double sceene) throws FontFormatException, IOException {
		typing = true;
		int x = 575, y = 450;
		//text writing area
		g.setColor(boldPink);
		g.fillRoundRect(x, y, 500, 100, 50, 50);
		g.setColor(lightPink);
		g.fillRoundRect(x + 7, y + 7, 486, 86, 40, 40);

		g.setFont(ani);

		//text
		if(g.getFontMetrics().stringWidth(tempString) + 40 > 475) {

			String tinyString = overFlowText(g, tempString, 450, false);

			g.setColor(boldPink);
			g.fillRect(x + 20 + g.getFontMetrics().stringWidth(tinyString), y + 30, 20, 40);

			g.drawString(tinyString, x + 20, y + 65);

		} else {
			g.setColor(boldPink);
			g.fillRect(x + 20 + g.getFontMetrics().stringWidth(tempString), y + 30, 20, 40);

			g.setColor(boldPink);

			g.drawString(tempString, x + 20, y + 65);
		}
		button(g, "Submit", x, y + 120, 100);
		returnScene = sceene;
		if(submit) {
			//System.out.println(tempString);
			scene = 100;
			return tempString;
		}
		return "";
	}

	//Description: displays the window for everything
	//Parameters: graphics
	//Return: none
	public void dispScreen(Graphics g, boolean watched) {
		int x = 575;
		g.setColor(boldPink);
		g.fillRoundRect(x, 40, 500, 400, 50, 50);
		g.setColor(lightPink);
		g.fillRoundRect(x + 10, 50, 480, 380, 40, 40);
		if(watched) {
			menuText(g, "Green = watched", 0, true);
			menuText(g, "Pink = not watched", 1);
		}
		typing = false;

	}

	//Description: draws titles in the window from above to navigate between shows & seasons & eps
	//Parameters: graphics, title = title of the show/season/ep, clickable = if its clickable or just a title, 
	//num = index of what we're clicking and whether it's the 1st, 2nd, etc. item on window, epWatched = if we're display an episode & its been watched
	//Return: int of which button we clicked
	public int dispTitle(Graphics g, String title, boolean clickable, int num, int epColor) {
		//epColor: 1 = pink, 2 = green, 3 = red
		int y = 60 + 90 * num;

		if(clickable) {
			g.setColor(Color.white);
			g.fillRoundRect(600, y, 420, 70, 20, 20);
			if(mouseX2 >= 600 && mouseX2 <= 1020 && mouseY2 >= y && mouseY2 < y + 70) {//if hovering
				if(epColor == 1)
					g.setColor(boldPink);
				else if(epColor == 2)
					g.setColor(good);
				else if(epColor == 3)
					g.setColor(Color.red);
				g.fillRoundRect(600, y, 420, 70, 20, 20);
				if(mouseX >= 600 && mouseX <= 1020 && mouseY >= y && mouseY < y + 70) {//if clicked
					//reset mousepos
					mouseX = -1;
					mouseY = -1;
					return num;
				}
				g.setColor(lightPink);
			} else {//if not hovering
				if(epColor == 1)
					g.setColor(boldPink);
				else if(epColor == 2)
					g.setColor(good);
				else if(epColor == 3)
					g.setColor(Color.red);
				g.fillRoundRect(600, y, 420, 70, 20, 20);
				g.setColor(lightPink);
				g.fillRoundRect(605, y + 5, 410, 60, 10, 10);
				if(epColor == 1)
					g.setColor(boldPink);
				else if(epColor == 2)
					g.setColor(good);
				else if(epColor == 3)
					g.setColor(Color.red);
			}
		} else {
			if(epColor == 1)
				g.setColor(boldPink);
			else if(epColor == 2)
				g.setColor(good);
			else if(epColor == 3)
				g.setColor(Color.red);
		}

		g.setFont(ani);
		title = overFlowText(g, title, 360, true);
		g.drawString("> " + title, 625, 100 + 95 * num);

		return -1;
	}

	//Description: displays multiple show titles and if the num of titles exceed the max list size (4), make it scrollable
	//Parameters:  graphics, on = if clickable or not
	//Return: int of index
	public int dispTitles(Graphics g, boolean on) {

		int click = -1;

		if(showsList.size() == 0){//if no titles
			g.setColor(boldPink);
			g.setFont(ani);
			g.drawString("No shows in here.", 625, 175);
			g.drawString("Add some by: ", 625, 225);
			g.drawString("> Shows Opts. --> Add Show or", 625, 275);
			g.drawString("> Eps Opts. --> Add Ep", 625, 325);

		} else if(showsList.size() <= 4) {//if num of titles does not exceed size limit
			for(int i = 0; i < Math.min(4, showsList.size()); i++) {
				click = dispTitle(g, "#" + (i + 1) + ": " + showsList.get(i).getTitle(), on, i, 1);//index of what we click
				if(click != -1)
					return click;
			}
		} else{//if size does exceed
			scrollListSize = showsList.size();
			scrollable = true;

			//scroll bar
			g.setColor(boldPink);
			g.fillRoundRect(1035, 60, 20, 350, 20, 20);
			g.setColor(lightPink);

			int size = 350 / (showsList.size() - 2);//size of little scroll rect thing
			g.fillRoundRect(1037, 62 + scroll * size, 15, size - 2, 15, 15); 

			for(int i = scroll; i < Math.min(scroll + 4, showsList.size()); i++) //for every title option
				if(dispTitle(g, "#" + i + ": " + showsList.get(i).getTitle(), on, i - scroll, 1) != -1) {//if clicking on button
					click = i;
					if(click != -1)
						return click;
				}
		} 

		return click;
	}

	//Description: displays multiple show seasons and if the num of seasons exceed the max list size (4), make it scrollable
	//Parameters:  graphics, on = if clickable or not
	//Return: int of index
	public int dispSeasons(Graphics g, boolean on) {
		dispTitle(g, showsList.get(showNum).getTitle(), false, 0, 1);
		if(showsList.get(showNum).getSeasons() <= 3) //if num of titles does not exceed size limit
			for(int i = 1; i < showsList.get(showNum).getSeasonsList().size(); i++) {//for num seasons
				int temp = dispTitle(g, "Season " + showsList.get(showNum).getSeasonsList().get(i)[0][0], on, i, 1);//display option, temp = index of option selected (IN DISPLAY LIST, NOT ACTUALY ARRAY LIST)
				if (temp != -1)  //if picked season, get out
					return temp;
			}
		else{//if size does exceed
			scrollListSize = showsList.get(showNum).getSeasons();
			scrollable = true;

			//scroll bar
			g.setColor(boldPink);
			g.fillRoundRect(1035, 60, 20, 350, 20, 20);
			g.setColor(lightPink);

			int size = 350 / (showsList.get(showNum).getSeasonsList().size() - 3);//size of little scroll rect thing
			g.fillRoundRect(1037, 62 + scroll * size, 15, size - 2, 15, 15); 
			for(int i = Math.max(1, scroll); i < Math.min(scroll + 4, showsList.get(showNum).getSeasonsList().size()); i++) { //for every title option
				int temp = dispTitle(g, "Season " + showsList.get(showNum).getSeasonsList().get(i)[0][0], on, i - scroll, 1);//display option, temp = index of option selected (IN DISPLAY LIST, NOT ACTUALY ARRAY LIST)
				if (temp != -1) //if picked season, get out
					return temp + scroll;				
			}
		} 

		return -1;
	}

	//Description: displays multiple ep titles and if the num of titles exceed the max list size (4), make it scrollable
	//Parameters:  graphics, on = if clickable or not
	//Return: int of index
	public int dispEps(Graphics g, boolean on, int removeSelect) {

		int click = -1;
		int season = showsList.get(showNum).getSeasonsList().get(seasonNum)[0][0];
		startInd = showsList.get(showNum).indexOfSeason(season);
		endInd = showsList.get(showNum).lastIndexOfSeason(season);
		int len = endInd - startInd + 1;

		if(showsList.size() == 0){//if no titles
			g.setColor(boldPink);
			g.setFont(ani);
			g.drawString("No episodes.", 625, 175);
			g.drawString("Add some by: ", 625, 225);
			g.drawString("> Shows Opts. --> Add Show or", 625, 275);
			g.drawString("> or Eps Opts. --> Add Ep", 625, 325);

		} else if(len <= 4) {//if num of titles does not exceed size limit
			for(int i = startInd; i < endInd; i++) {
				System.out.println(endInd);//TODO
				boolean watched = showsList.get(showNum).getEpisodes().get(i).getWatched();
				int color = 0;
				if(i == removeSelect)
					color = 3;
				else if(watched)
					color = 2;
				else
					color = 1;

				int epiNum = showsList.get(showNum).getEpisodes().get(i).getEpsisodeNum();
				String title = showsList.get(showNum).getEpisodes().get(i).getTitle();
				click = dispTitle(g, "Ep. " + epiNum + ": " + title, on, i - startInd, color);//index of what we click
				if(click != -1) {
					return click + startInd;
				}
			}
		} else{//if size does exceed
			scrollListSize = len;
			scrollable = true;

			//scroll bar
			g.setColor(boldPink);
			g.fillRoundRect(1035, 60, 20, 350, 20, 20);
			g.setColor(lightPink);

			int size = 350 / (len - 2);//size of little scroll rect thing
			g.fillRoundRect(1037, 62 + scroll * size, 15, size - 2, 15, 15); 

			for(int i = scroll + startInd; i < Math.min(endInd, scroll + startInd + 4); i++) { //for every title option
				boolean watched = showsList.get(showNum).getEpisodes().get(i).getWatched();
				int color = 0;
				if(i == removeSelect)
					color = 3;
				else if(watched)
					color = 2;
				else
					color = 1;

				int epiNum = showsList.get(showNum).getEpisodes().get(i).getEpsisodeNum();
				String title = showsList.get(showNum).getEpisodes().get(i).getTitle();
				click = dispTitle(g, "Ep. " + epiNum + ": " + title, on, i - scroll - startInd, color);//index of what we click
				if(click != -1) {//if clicking on button
					click = i;
					if(click != -1) {
						return click;
					}
				}
			}
		} 

		return click;
	}

	//Description: takes text and makes it fit the set window size
	//Parameters:  graphics, s = text we want to shrink, len = max size, dots = whether we want to add ... at the end or not
	//Return: new string
	public String overFlowText(Graphics g, String s, int len, boolean dots) {
		int ind = 0;//index in string
		if(g.getFontMetrics().stringWidth(s) > len) {//if string width is greater than len
			if(dots) {//if add dots
				while(g.getFontMetrics().stringWidth(s.substring(0, ind) + "...") <= len)//start with 1 letter, make longer until reaches max len
					ind++;
				return s.substring(0, ind) + "...";
			} else {
				while(g.getFontMetrics().stringWidth(s.substring(ind, s.length() - 1)) > len)
					ind++;
				return s.substring(ind, s.length() - 1);
			}
		} else
			return s;
	}

	//Description: takes text and wraps it over (only works if string has '>' to indicate new list item cause too lazy to do anymore...)
	//Parameters:  graphics, s = text, y = y pos
	//Return: none
	public void wrapText(Graphics g, String s) {
		g.setFont(ani);
		g.setColor(boldPink);
		int iter = 0;//trying to center y pos
		int prevPrevLn = 0;//start index of each list item
		int prevLn = s.indexOf('>', 1);//end index of each list item

		while(prevLn >= 0) {
			g.drawString(s.substring(prevPrevLn, prevLn), 625, 150 + 50 *(iter - 1));
			prevPrevLn = prevLn;
			prevLn = s.indexOf('>', prevPrevLn + 1);
			iter++;
		}
	}

	//Description: shortcut to draw text under the window for instructions about how to navigate
	//Parameters:  graphics, s = text, line = what line to put it on
	//Return: none
	public void menuText(Graphics g, String s, int line) {
		g.setFont(buttonFont);
		g.setColor(boldPink);
		g.drawString(s, 550, 500 + 50 * line);
	}

	//Description: shortcut to draw text on side of display window
	//Parameters:  graphics, s = text, line = what line to put it on
	//Return: none
	public void sideText(Graphics g, String s, int line) {
		g.setFont(buttonFont);
		g.setColor(boldPink);
		g.drawString(s, 100, 100 + 50 * line);
	}

	//Description: MENU TEXT BUT GREEN shortcut to draw text under the window for instructions about how to navigate
	//Parameters:  graphics, s = text, line = what line to put it on
	//Return: none
	public void menuText(Graphics g, String s, int line, boolean green) {
		g.setFont(buttonFont);
		g.setColor(good);
		g.drawString(s, 550, 500 + 50 * line);
	}

	//Description: short cut to draw error text
	//Parameters: graphics, error = whether or not display error, option = which error to display
	public void error(Graphics g, boolean error, int option) {
		if(error) {
			if(option == 1) {
				menuText(g, "ERROR", 0);
				menuText(g, "2nd ep. must be > 1st ep.", 1);
			} else if(option == 2) {
				sideText(g, "ERROR, either: ", 0);
				sideText(g, "1) This ep. num", 1);
				sideText(g, "already exists. or", 2);
				sideText(g, "2) This is not an", 3);
				sideText(g, "integer", 4);
			} else if(option == 3) {
				sideText(g, "ERROR", 0);
				sideText(g, "Please enter the", 1);
				sideText(g, "time in proper", 2);
				sideText(g, "format.", 3);
			} else if(option == 4) {
				sideText(g, "ERROR, either: ", 0);
				sideText(g, "1) This sea. num", 1);
				sideText(g, "already exists. or", 2);
				sideText(g, "2) This is not an", 3);
				sideText(g, "integer", 4);
			}
		}
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Image bg = Toolkit.getDefaultToolkit().getImage("bg.jpg");
		g.drawImage(bg, 0, 0, W, H, null);

		if(!typing)
			tempString = "";

		try {
			if(scene == 0) {//main menu
				int offSet = 90;
				button(g, "Show Options", 125, 100 + offSet, 1);
				button(g, "Episode Options", 100, 200 + offSet, 2);
				button(g, "Exit", 230, 300 + offSet, -1);

				Image girl = Toolkit.getDefaultToolkit().getImage("angela28.gif");
				g.drawImage(girl, 630, 75, 400, 400, null);
				g.setFont(ani);
				g.drawString("S-senpai... y-you want me", 670, 520);
				g.drawString("to o-organize your s-shows for", 630, 570);
				g.drawString("you...? Ok! I'll do my best!", 670, 620);

			} else if(scene == -1) {//exit
				System.exit(0);
			} else if(scene == 1) {//show options
				scroll = 0;
				button(g, "Display Shows", 100, 215, 1.2);
				button(g, "  Add a Show ", 100, 315, 1.1);
				back(g, 0);

			} else if(scene == 1.1) {//file name
				dispScreen(g, false);
				if(fileName.equals("")) {//if nothing typed
					fileName = textField(g, scene);
				} else {
					submit = false;
					try {//try importing file
						new FileInputStream(fileName);
						notFound = false;
						Driver.addShow(new File(fileName), showsList);
						fileFound = true;//if typed something
					} catch(FileNotFoundException e) {
						notFound = true;
					}
					tempString = "";
					fileName = "";
				}
				g.setFont(buttonFont);
				g.setColor(boldPink);
				g.drawString("Please enter your", 100, 225);
				g.drawString("file name", 100, 295);
				g.drawString("(Ex: text.txt)", 100, 395);
				if(!notFound) {//if found file
					if(fileFound) {//if... typed something i think?... whatever i just need it in order for this to work. why? :D idk :D
						g.setColor(good);
						g.setFont(ani);
						g.drawString("File successfully", 625, 225);
						g.drawString("imported!", 625, 275);
					}
				} else {
					g.setColor(Color.red);
					g.setFont(ani);
					g.drawString("ERROR", 625, 225);
					g.drawString("File not found", 625, 275);
				}

				back(g, 1);
			} else if(scene == 1.2) {//display show names
				dispScreen(g, false);
				button(g, " Info on Show", 100, 115, 1.21);
				button(g, " Remove Show ", 100, 215, 1.22);
				button(g, "Remove season", 100, 315, 1.23);
				button(g, " Watch Status", 100, 415, 1.24);

				menuText(g, "Use up and down keys", 0);
				menuText(g, "to scroll through list", 1);

				showNum = -1;
				dispTitles(g, false);
				back(g, 1);
			}
			//(shows) SUB SUB MENU --------------------------------------------------------------------------------------------
			else if(scene == 1.21) {//display info on a show
				dispScreen(g, false);
				menuText(g, "Choose a show from the", 0);
				menuText(g, "list above.", 1);
				if(showNum == -1) {//if none selected
					showNum = dispTitles(g, true);
				} else {//display info
					String info = showsList.get(showNum).toString();
					g.setColor(boldPink);
					g.setFont(ani);
					wrapText(g, info + ">");
				}
				back(g, 1.2);

			} else if(scene == 1.22) {//remove entire show
				dispScreen(g, false);
				menuText(g, "Choose a show from the", 0);
				menuText(g, "list above.", 1);

				if(showNum == -1) {//if none selected
					showNum = dispTitles(g, true);
				} else {
					showsList.remove(showNum);
					showNum = -1;
					mouseX = -1;
					mouseY = -1;
					scene = 1.2;
				}
				back(g, 1.2);
			} else if(scene == 1.23) {//remove season of show
				back(g, 1.2);
				dispScreen(g, false);
				menuText(g, "Choose a show from the", 0);
				menuText(g, "list above.", 1);
				if(showNum == -1) {//picking show
					showNum = dispTitles(g, true);
				} else if (showsList.get(showNum).getEpisodes().size() > 0){//if removing a season
					if (seasonNum == -1) {//if no season selected
						dispTitle(g, showsList.get(showNum).getTitle(), false, 0, 1);
						seasonNum = dispSeasons(g, true);
						back(g, 1.2);
					} else {//once selected a season to remove
						Driver.removeShow(showsList.get(showNum).getSeasonsList().get(seasonNum)[0][0], showNum, showsList);

						if(showsList.get(showNum).getEpisodes().size() == 0) {//if nothing left, remove show
							showsList.remove(showNum);
							scene = 1.2;
						}
						seasonNum = -1;
						showNum = -1;
					}
					mouseX = -1;
					mouseY = -1;

				} else//if no episodes, return to shows list
					scene = 1.2;

			} else if(scene == 1.24) {//see watch status
				dispScreen(g, false);
				menuText(g, "Choose a show from the", 0);
				menuText(g, "list above.", 1);

				g.setColor(boldPink);
				g.setFont(ani);
				back(g, 1.2);

				if(showNum == -1) {//if no show selected
					showNum = dispTitles(g, true);
				} else {
					g.setFont(ani);
					String status = Driver.watchStatus(showsList, showNum + 1);
					wrapText(g, status + ">");
				}
			}
//(shows) SUB SUB MENU end --------------------------------------------------------------------------------------------
//START EPISODES OPTIONS -------------------------------------------------------------------------------------------------
			else if(scene == 2 && showsList.size() > 0) {//selecting show and season first
				back(g, 0);
				dispScreen(g, false);
				if(showNum == -1) {//select show
					showNum = dispTitles(g, true);
					menuText(g, "Please select a show.", 0);
				} else if(seasonNum == -1) {//select season
					seasonNum = dispSeasons(g, true);
				}else//once selected
					scene = 2.01;
			} else if(scene == 2) {//if no shows to selecte from
				g.setFont(buttonFont);
				g.setColor(boldPink);
				g.drawString("No shows. Please add", 100, 235);
				g.drawString("some shows under the", 100, 305);
				g.drawString("shows menu. ", 100, 375);
				back(g, 0);
			} else if(scene == 2.01) {//display eps
				epNum = -1;
				dispScreen(g, true);
				int offSet = 40, space = 100;
				dispEps(g, false, -1);
				button(g, " Info on Ep.", 100, 0 * space + offSet, 2.1);
				button(g, "  Watch Ep.", 100, 1 * space + offSet, 2.2);
				button(g, "   Add Ep.  ", 100, 2 * space + offSet, 2.3);
				button(g, " Remove Ep.", 100, 3 * space + offSet, 2.4);
				button(g, "  Sort Eps.", 100, 4 * space + offSet, 2.5);
				back(g, 2.1111);
				
				//(episodes) SUB MENU----------------------------------------------------------------------------------------------------------
			} else if(scene == 2.1) {//info on specific ep
				dispScreen(g, true);
				if(epNum == -1)//if ep not selected
					epNum = dispEps(g, true, -1);
				else {
					String info = showsList.get(showNum).getEpisodes().get(epNum) + ">";
					wrapText(g, info);
				}

				back(g, 2.01);
			} else if(scene == 2.2) {//watch ep
				dispScreen(g, true);
				if(epNum == -1)//if ep not selected
					epNum = dispEps(g, true, -1);
				else {
					showsList.get(showNum).getEpisodes().get(epNum).watched();
					epNum = -1;
				}

				back(g, 2.01);
			} else if(scene == 2.3) {//add ep
				dispScreen(g, false);
				
				button(g, " Add one Ep. ", 100, 215, 2.31);
				button(g, " Add Ep.&Sea.", 100, 315, 2.32);
				
				back(g, 2.01);
				dispEps(g, false, -1);

			} else if(scene == 2.31) {
				dispScreen(g, false);
				back(g, 2.01);
				dispEps(g, false, -1);
				
				if(newSeason.equals(""))//if we didn't try to add a new season
					newSeason = seasonNum + "";
				
				int sea = Integer.parseInt(newSeason);

				if(newTitle.equals("") && addEpCounter == 1) {//IF TITLE IS BLANK
					sideText(g, "Please enter your", 0);
					sideText(g, "title.", 1);
					newTitle = textField(g, scene);
				} else if(!newTitle.equals("") && addEpCounter == 1) {//if title not blank
					submit = false;
					tempString = "";
					addEpCounter = 2;
				} else if(newEpNum.equals("") && addEpCounter == 2) {//IF EP NUM IS BLANK
					if(!error) {
						sideText(g, "Please enter your", 0);
						sideText(g, "episode num.", 1);
					} else
						error(g, error, 2);
					newEpNum = textField(g, scene);
				} else if(!newEpNum.equals("") && addEpCounter == 2){//if ep num not blank
					submit = false;
					try {
						epNum = Integer.parseInt(newEpNum);
						if(seasonNum == sea && showsList.get(showNum).indexOfEpInSeasonList(sea, epNum) != -1)
							throw new NumberFormatException();
						error = false;
						addEpCounter = 3;
					} catch(NumberFormatException e) {
						error = true;
						newEpNum = "";
					}
					tempString = "";
				} else if(newTime.equals("") && addEpCounter == 3) {//IF TIME BLANK
					if(!error) {
						sideText(g, "Please enter your", 0);
						sideText(g, "time (hh:mm:ss).", 1);
					} else
						error(g, error, 3);
					newTime = textField(g, scene);
				} else {//if time not blank
					submit = false;
					try {
						new Time(newTime);
						error = false;
						addEpCounter = 0;
						
						showsList.get(showNum).addEpAndSeason(newTitle, sea, Integer.parseInt(newEpNum), new Time(newTime));
						
						if(Integer.parseInt(newSeason) == seasonNum)
							scene = 2.01;
						else
							scene = 2;
						
						newTitle = "";
						newEpNum = "";
						newTime = "";
						newSeason = "";
					} catch(NumberFormatException | IndexOutOfBoundsException e) {
						error = true;
						newTime = "";
					}
					tempString = "";
				}
			} else if(scene == 2.32) {//add season
				dispScreen(g, false);
				back(g, 2.3);
				dispEps(g, false, -1);
				
				if(!error) {
					sideText(g, "Please enter your", 0);
					sideText(g, "season num.", 1);
				} else
					error(g, error, 4);
				
				if(newSeason.equals("")) {//IF SEASON BLANK
					newSeason = textField(g, scene);
				} else {//not blank
					submit = false;
					try {
						if(Integer.parseInt(newSeason) < 1 || showsList.get(showNum).indexOfSeasonInList(Integer.parseInt(newSeason)) != -1)
							throw new NumberFormatException();
						error = false;
						scene = 2.31;
					} catch(NumberFormatException e) {
						error = true;
						newSeason = "";
					}
					tempString = "";
				}
			}
			
			else if(scene == 2.4) {//remove ep
				dispScreen(g, false);
				dispEps(g, false, -1);

				button(g, "     1 ep.    ", 100, 115, 2.41);
				button(g, " Range of eps", 100, 215, 2.42);
				button(g, "    Title     ", 100, 315, 2.43);
				button(g, " Watched eps ", 100, 415, 2.44);

				menuText(g, "Select how you wish", 0);
				menuText(g, "to remove from the list.", 1);

				if(showsList.get(showNum).getSeasonsList().get(showsList.get(showNum).indexOfSeasonInList(seasonNum))[0][1] == 0) {//if no eps left in the season
					showsList.get(showNum).getSeasonsList().remove(showsList.get(showNum).indexOfSeasonInList(seasonNum));//remove season
					scene = 2;
					seasonNum = -1;
				}

				back(g, 2.01);
			} else if(scene == 2.5) {//sort ep
				dispScreen(g, false);
				dispEps(g, false, -1);
				int offSet = 40, space = 100;
				button(g, "   Ep Num   ", 100, 1 * space + offSet, 2.52);
				button(g, "    Time    ", 100, 2 * space + offSet, 2.53);
				button(g, "    Title   ", 100, 3 * space + offSet, 2.54);

				menuText(g, "Select how you wish", 0);
				menuText(g, "to sort the list.", 1);
				back(g, 2.01);
			} else if(scene == 2.1111) {//after exiting episodes sublist, reset show choice
				scene = 2;
				showNum = -1;
				seasonNum = -1;
			} 
			//REMOVING EPISODES-----------------
			else if(scene == 2.41) {//remove 1 ep
				back(g, 2.4);
				dispScreen(g, false);
				if(epNum == -1)//if ep not selected
					epNum = dispEps(g, true, -1);//TECHNICALLY ep index
				else {  
					showsList.get(showNum).removeEp(epNum, seasonNum);
					epNum = -1;
					scene = 2.4;
				}
			} else if(scene == 2.42) {//remove range of eps
				back(g, 2.4);
				dispScreen(g, false);

				error(g, error, 1);

				if(ep1 == -1) {//if ep1 not selected
					ep1 = dispEps(g, true, -1);
				} else if(ep2 == -1) {//if ep2 not selected
					ep2 = dispEps(g, true, ep1);
				} else if(ep2 < ep1) {//if ep2 is less than or equal to ep1
					error = true;
					ep2 = -1;
				} else {
					showsList.get(showNum).removeEp(seasonNum, ep1, ep2);
					error = false;
					scene = 2.4;
					ep1 = -1;
					ep2 = -1;
				}
			} else if(scene == 2.43) {//remove by title
				back(g, 2.4);
				dispScreen(g, false);			
				textField(g, -2.43);
				dispEps(g, false, -1);

			} else if(scene == -2.43) {
				showsList.get(showNum).removeEp(seasonNum, tempString, startInd, endInd);
				scene = 2.4;
			} else if(scene == 2.44) {//remove watched
				back(g, 2.4);
				showsList.get(showNum).removeEp(showsList.get(showNum).getSeasonsList().get(seasonNum)[0][0]);
				scene = 2.4;
			}

			//SORTING EPISODES------------------
			else if(scene == 2.52) {
				showsList.get(showNum).sort(1, startInd, endInd);
				scene = 2.5;
			} else if(scene == 2.53) {
				showsList.get(showNum).sort(2, startInd, endInd);
				scene = 2.5;
			} else if(scene == 2.54) {
				showsList.get(showNum).sort(3, startInd, endInd);
				scene = 2.5;
			}

			else if(scene == 100) {
				submit = true;
				scene = returnScene;
			}
			//(episodes) SUB MENU----------------------------------------------------------------------------------------------------------
		} catch (FontFormatException | IOException e) {
		} 
						catch(IndexOutOfBoundsException e) {
							System.exit(0);
						}
		//		catch(NullPointerException e) {
		//			System.exit(0);
		//		}
	}

	public static void main (String [] args) throws FontFormatException, IOException{
		JFrame frame = new JFrame("TV Shows");
		DriverButFancy panel = new DriverButFancy();
		panel.setPreferredSize (new Dimension(WIDTH, HEIGHT));
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(W, H);
		panel.setBackground(new Color(242, 175, 193));

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}


	@Override
	public void mousePressed(MouseEvent e) {
	}


	@Override
	public void mouseReleased(MouseEvent e) {
	}


	@Override
	public void mouseEntered(MouseEvent e) {
	}


	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void run() {
		System.out.println("Thread: Starting thread");
		initialize();
		while(true) {
			update();
			this.repaint();
			try {
				Thread.sleep(1000/FPS);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}		
	}


	public void update() {
		timeElapsed = System.currentTimeMillis() - startTime;
		frameCount++;
	}

	public void initialize() {
		System.out.println("Thread: Initializing game");
		startTime = System.currentTimeMillis();
		timeElapsed = 0;
		FPS = 60;
		for(int i = 0; i < 100000; i++) {
			String s = "set up stuff blah blah blah";
			s.toUpperCase();
		}
		System.out.println("Thread: Done initializing game");
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX2 = e.getX();
		mouseY2 = e.getY();

	}

	@Override
	public void keyTyped(KeyEvent e) {
		char chara = e.getKeyChar();
		if(typing) {//if using text field
			if(chara != KeyEvent.VK_ENTER) {
				if(chara == KeyEvent.VK_BACK_SPACE && tempString.length() > 0)
					tempString = tempString.substring(0, tempString.length() - 1);
				else if(chara != KeyEvent.VK_BACK_SPACE)
					tempString += chara;
			} else 
				scene = 100;

		} 
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int chara = e.getKeyCode();
		if(scrollable) {
			if(chara == KeyEvent.VK_DOWN) {
				if(scroll + 4 <= scrollListSize)
					scroll++;
			} else if(chara == KeyEvent.VK_UP) {
				if(scroll > 0)
					scroll--;
			}
		} else
			scroll = 0;
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}


}