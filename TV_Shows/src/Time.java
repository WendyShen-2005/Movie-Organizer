//Wendy Shen
//Oct 5, 2022
//To store all the time info

public class Time {
	private int hours = 0;
	private int mins = 0;
	private int secs = 0;

	//constructor
	public Time(String time) {

		int firstInd = time.indexOf(':');//in case you somehow have an episode that is 100 hours long (ex: 100:59:02)
		int secondInd = firstInd + 3;
		
		if(Integer.parseInt(time.substring(0, firstInd)) < 0)
			throw new NumberFormatException();
		if(Integer.parseInt(time.substring(firstInd + 1, secondInd)) < 0)
			throw new NumberFormatException();
		if(Integer.parseInt(time.substring(secondInd + 1)) < 0)
			throw new NumberFormatException();
		
		addTime(Integer.parseInt(time.substring(0, firstInd)), Integer.parseInt(time.substring(firstInd + 1, secondInd)), Integer.parseInt(time.substring(secondInd + 1)));
	}

	//Purpose: to compare time
	//Parameters: the other time we're comparing it to
	//Return: int for whether this thing is greater or less than other thing
	public int compareTo(Time time) {
		if(this.hours == time.hours) {
			if(this.mins == time.mins)
				return this.secs - time.secs;
			return this.mins - time.mins;
		}
		return this.hours - time.hours;
	}

	//Purpose: to see if time is equal to other time
	//Parameters: object
	//Return: true if thing is equal, false if... not
	public boolean equals(Object o) {
		Time time = (Time)o;
		if(this.hours == time.hours)
			if(this.mins == time.mins)
				if(this.secs == time.secs)
					return true;
		return false;
	}

	//Purpose: add time
	//Parameters: hours, mins, secs
	//Return: none
	public void addTime(int hours, int mins, int secs) {		
		this.hours += hours;
		this.mins += mins; 
		this.secs += secs;
		
		if(this.secs > 59) {
			this.mins += (this.secs/60);
			this.secs = this.secs%60;
		}
		if(this.mins > 59) {
			this.hours += (this.mins/60);
			this.mins = this.mins%60;
		}
		
	}
	
	//Purpose: decrese time
	//Parameters: hours, mins, secs
	//Return: none
	public void decreaseTime(int hours, int mins, int secs) {
		this.hours -= hours;
		this.mins -= mins; 
		this.secs -= secs;
		
		if(this.secs < 0) {
			this.mins--;
			this.secs += 60;
		}
		if(this.mins < 0) {
			this.hours--;
			this.mins += 60;
		}
	}

	//Purpose: time info to string
	//Parameters: none
	//Return: string of time info
	public String toString() {
		return String.format("%02d:%02d:%02d", hours, mins, secs);
	}
	
	//getters and setters
	public int getHours() {
		return hours;
	}
	
	public int getMins() {
		return mins;
	}
	
	public int getSecs() {
		return secs;
	}

}
