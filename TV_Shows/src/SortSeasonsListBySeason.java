import java.util.Comparator;

//Wendy Shen, Nov 20, 2022
//Description: sort the list of seasons by season

public class SortSeasonsListBySeason implements Comparator <Integer[][]>{

	@Override
	//Description: compare a an int[][] to int[][]
	//Parameters: int[][] 1 and int[][] 2
	//Return: int for which is greater
	public int compare(Integer[][] o1, Integer[][] o2) {
		return o1[0][0] - o2[0][0];
	}

}
