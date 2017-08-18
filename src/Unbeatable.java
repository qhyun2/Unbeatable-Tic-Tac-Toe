import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Unbeatable{

	// 0, 1, 2
	// 3, 4, 5
	// 6, 7, 8

	// 9 long int array to moves, 0 - blank, 1 - X, 2 - O
	public static int gameState[] = new int[9];
	public static JButton buttons[] = new JButton[9];
	public static Font textFont = new Font("vernada", Font.BOLD, 29);
	public static Random ran; // for randomizes first move
	public static JButton action;
	public static ActionListener boardListener; // handles clicks on buttons on the board
	public static ActionListener headerListener; // handles clicks on the header button
	private JFrame frame;

	public static void main(String[] args){

		EventQueue.invokeLater(new Runnable(){

			public void run(){

				try{
					Unbeatable window = new Unbeatable();
					window.frame.setVisible(true);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public Unbeatable(){


		// handles clicks on the board
		boardListener = new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e){

				// determine which button has been clicked
				int button = Integer.parseInt(((AbstractButton)e.getSource()).getActionCommand());

				// ensures valid move - tile is blank and game is not in reset pending mode
				if(gameState[button] == 0 && action.getActionCommand() != "reset"){

					// disable header button after first valid move
					action.setEnabled(false);

					// update value of desired location
					gameState[button] = 2;

					// create move object as the result of minimaxing the current board state
					Move result = miniMax(gameState, 1);

					// if game is not ended play ai move
					if(result.index != -1){
						gameState[result.index] = 1;
					}

					// updates the display
					updateButtons();

					// game is tied until proven not
					boolean draw = true;

					// empty squares mean game is not tied
					for (int j = 0; j < gameState.length && draw; j++){
						if(gameState[j] == 0){
							draw = false;
						}
					}

					// if game a tie output tie
					if(draw){
						action.setText("You Tied, Click to Play Again");
						// re-enable button
						action.setEnabled(true);
						// reset action command tells button to reset when pressed
						action.setActionCommand("reset");
					}

					// if game is lost, output lost
					if(winning(gameState, 1)){
						action.setText("You Lost, Click to Play Again");
						action.setEnabled(true);
						action.setActionCommand("reset");
					}
				}
			}
		};

		// handles clicks in the header button
		headerListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){

				// when button is for resetting
				if(action.getActionCommand() == "reset"){
					reset();
				}
				else{
					// randomized computer first move
					// best first move in tic tac toe is always corner
					int options[] = {0, 2, 6, 8};

					// random corner out of 4
					int choice = ran.nextInt(4);

					// sets the corner
					gameState[options[choice]] = 1;

					// update board and disable action button
					updateButtons();
					action.setEnabled(false);
				}
			}
		};
		
		ran = new Random();

		// main frame
		frame = new JFrame();
		frame.setTitle("Unbeatable Tic Tac Toe");
		frame.setBounds(100, 100, 400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setLocationRelativeTo(null);

		// gui header panel
		JPanel header = new JPanel();
		header.setBorder(new EmptyBorder(20, 100, 20, 100));
		frame.getContentPane().add(header, BorderLayout.NORTH);

		// action button
		action = new JButton();
		action.addActionListener(headerListener);
		header.add(action);

		// panel for board
		JPanel board = new JPanel();
		board.setBorder(new EmptyBorder(10, 60, 30, 60));
		frame.getContentPane().add(board);
		board.setLayout(new GridLayout(0, 1, 0, 0));

		// background panel for create black lines
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		board.add(panel);
		panel.setLayout(new GridLayout(3, 3, 3, 3));

		// 9 buttons
		for (int i = 0; i < buttons.length; i++){
			buttons[i] = new JButton("");
			buttons[i].setActionCommand(Integer.toString(i));
			buttons[i].setFont(textFont);
			buttons[i].setMargin(new Insets(0, 0, 0, 0));
			buttons[i].addActionListener(boardListener);
			panel.add(buttons[i]);
		}

		// reset board
		reset();
	}

	// empties board
	private void reset(){

		for (int i = 0; i < gameState.length; i++){
			gameState[i] = 0;
		}
		action.setText("Allow Computer First Move");
		updateButtons();
		action.setActionCommand("def");
	}

	// move class used to store both a move on the board and the resulting score of the move
	// for example a move on the 8th square would result in a score of -10;
	class Move{

		public int index;
		public int score;
	};

	// minimax algorithm takes current board states and outputs best move for given player
	public Move miniMax(int board[], int player){


		// list to store all possible moves
		List<Move> moves = new LinkedList<Move>();

		// create a list of possible moves
		for (int i = 0; i < board.length; i++){

			// possible moves on all empty squares
			if(board[i] == 0){

				// create and add a new move with index of empty square
				Move temp = new Move();
				temp.index = i;
				moves.add(temp);
			}
		}

		// terminal cases, if there is a winning losing or drawing state
		Move score = new Move();
		score.index = -1;

		if(winning(board, 1)){
			score.score = 10;
			return score;
		}
		else if(winning(board, 2)){
			score.score = -10;
			return score;
		}
		else if(moves.size() == 0){
			score.score = 0;
			return score;
		}

		// evaluate all cases and store values into score variable
		for (int i = 0; i < moves.size(); i++){

			// copy of board for simulating states
			int copy[] = board.clone();

			// make the move in question on the copy of the board
			copy[moves.get(i).index] = player;

			// get score depending on player
			if(player == 1){
				// 1 is subtracted to deter trolling,
				// algorithm sometimes makes a non winning move when there
				// is one available because there is a guaranteed
				// win later down the line.
				// subtracting 1 makes winning asap the most appealing
				moves.get(i).score = miniMax(copy, 2).score - 1;
			}
			else{
				moves.get(i).score = miniMax(copy, 1).score - 1;
			}
		}

		// return best scoring one if ai turn, return worst scoring one if player turn

		// stores the index of the best move
		int bestMove = 0;

		// maximize if ai
		if(player == 1){
			int bestScore = -100;
			// look through possible moves and store best one
			for (int i = 0; i < moves.size(); i++){
				if(moves.get(i).score > bestScore){
					bestMove = i;
					bestScore = moves.get(i).score;
				}
			}
		}
		// minimize if player
		else{
			int bestScore = 100;
			for (int i = 0; i < moves.size(); i++){
				if(moves.get(i).score < bestScore){
					bestMove = i;
					bestScore = moves.get(i).score;
				}
			}
		}
		// return move
		return moves.get(bestMove);
	}

	public boolean winning(int board[], int player){

		// checks all 8 win conditions
		if((board[0] == player && board[1] == player && board[2] == player) || (board[3] == player && board[4] == player && board[5] == player) || (board[6] == player && board[7] == player && board[8] == player) || (board[0] == player && board[3] == player && board[6] == player) || (board[1] == player && board[4] == player && board[7] == player) || (board[2] == player && board[5] == player && board[8] == player) || (board[0] == player && board[4] == player && board[8] == player) || (board[2] == player && board[4] == player && board[6] == player)){
			return true;
		}
		return false;
	}

	public void updateButtons(){

		// sets button labels
		for (int i = 0; i < gameState.length; i++){
			switch (gameState[i]) {
				case 1:
					buttons[i].setText("X");
					break;
				case 2:
					buttons[i].setText("O");
					break;
				default:
					buttons[i].setText("");
					break;
			}
		}
	}
}
