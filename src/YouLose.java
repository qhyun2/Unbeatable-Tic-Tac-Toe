
public class YouLose {
	public static int board[] = new int[9];
	public static void main(String args[]) {
		for (int i = 0; i < board.length; i++) {
			board[i] = 2;
		}
		
		board[4] = 1;
		board[8] = 2;
		
		displayBoard();
	}
	
	public static void displayBoard(){
		for (int i = 0; i < board.length; i++) {
			switch (board[i]) {
			case -1:
				System.out.print("   ");
				break;
			case 1:
				System.out.print(" X ");
				break;
			case 2:
				System.out.print(" O ");
				break;
			default:
				break;
			}
			if((i + 1) % 3 == 0)
				System.out.println();
		}
	}
}
