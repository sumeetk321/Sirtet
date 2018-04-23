import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;

//MUSIC: Alexey Pajitnov (Tetris theme), Smash Mouth (All Star), Cherry Network (Remix)
public class Sirtet extends JPanel {
	static Color currentColor;
	ArrayList<Integer> xs;
	ArrayList<Integer> ys;
	int greatestnumber;
	static int numofpieces = 0;
	static int level = 1;
	static int faster = 50;
	static int more;
	static boolean gameOver = false;

	private final Point[][][] Pieces = { { { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
			{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
			{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
			{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) } },

			{ { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
					{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) } },

			{ { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
					{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) } },

			{ { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) } },

			{ { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
					{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) } },

			{ { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
					{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
					{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
					{ new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) } },

			{ { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
					{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
					{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
					{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) } } };

	private final Color[] pieceColors = { Color.cyan, Color.blue, Color.magenta, Color.yellow, Color.green, Color.pink,
			Color.red };

	private Point pieceOrigin;
	private int currentPiece;
	private int rotation;
	private ArrayList<Integer> nextPieces = new ArrayList<Integer>();

	private static long score;
	private Color[][] grid;

	private void init() {
		grid = new Color[12][24];
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 24; j++) {
				if (i == 0 || i == 11 || j == 23) {
					grid[i][j] = Color.GRAY;
				} else {
					grid[i][j] = Color.BLACK;
				}
			}
		}
		currentColor = pieceColors[new Random().nextInt(7)];
		newPiece();
	}

	public void newPiece() {
		if (pieceOrigin != null && pieceOrigin.y <= 1) {
			gameOver = true;

		}
		pieceOrigin = new Point(5, 1);

		rotation = 0;
		if (nextPieces.isEmpty()) {
			Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
			Collections.shuffle(nextPieces);
		}
		currentPiece = nextPieces.get(0);
		nextPieces.remove(0);
		numofpieces++;
	}

	private boolean collidesAt(int x, int y, int rotation) {
		for (Point p : Pieces[currentPiece][rotation]) {
			if (grid[p.x + x][p.y + y] != Color.BLACK) {
				return true;
			}
		}
		return false;
	}

	public void rotate(int i) {
		int newRotation = (rotation + i) % 4;
		if (newRotation < 0) {
			newRotation = 3;
		}
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
			rotation = newRotation;
		}
		repaint();
	}

	public void move(int i) {
		if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
			pieceOrigin.x += i;
		}
		repaint();
	}

	public void dropDown() {
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			pieceOrigin.y += 1;
		} else {
			enableCollision();
		}
		repaint();
	}

	public void hardDrop() {

		while (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			dropDown();
		}
		enableCollision();
	}

	public void enableCollision() {
		for (Point p : Pieces[currentPiece][rotation]) {
			grid[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = currentColor;
		}
		currentColor = pieceColors[new Random().nextInt(7)];
		clearChunks();
		newPiece();
	}

	public void clearChunks() {
		int numofSameColor = 0;

		Color[][] temp = new Color[12][24];

		int i, j, k;
		for (i = 0; i < temp.length; i++) {
			for (j = 0; j < temp[0].length; j++) {
				temp[i][j] = grid[i][j];
			}
		}

		for (i = 0; i < temp.length; i++) {
			for (j = 0; j < temp[0].length; j++) {
				if ((temp[i][j] != (Color.BLACK)) && (temp[i][j] != (Color.GRAY))) {
					xs = new ArrayList<Integer>();
					ys = new ArrayList<Integer>();
					greatestnumber = 0;
					int samecolortmp = recurse(i, j, temp[i][j], 0, temp);
					xs.add(i);
					ys.add(j);
					if (samecolortmp > 10) {
						numofSameColor = samecolortmp;
						for (k = 0; k < xs.size(); k++) {
							grid[xs.get(k)][ys.get(k)] = Color.BLACK;
						}
						score += (10 * numofSameColor);
					}
				}
			}
		}
	}

	public int recurse(int x, int y, Color color, int number, Color[][] grid) {
		greatestnumber++;
		grid[x][y] = Color.BLACK;
		if (x + 1 < 12 && grid[x + 1][y].equals(color) && (!grid[x + 1][y].equals(Color.BLACK))
				&& (!grid[x + 1][y].equals(Color.GRAY))) {
			recurse(x + 1, y, color, number + 1, grid);
			xs.add(x + 1);
			ys.add(y);
		}
		if (x - 1 >= 0 && grid[x - 1][y].equals(color) && (!grid[x - 1][y].equals(Color.BLACK))
				&& (!grid[x - 1][y].equals(Color.GRAY))) {
			recurse(x - 1, y, color, number + 1, grid);
			xs.add(x - 1);
			ys.add(y);
		}
		if (y + 1 < 24 && grid[x][y + 1].equals(color) && (!grid[x][y + 1].equals(Color.BLACK))
				&& (!grid[x][y + 1].equals(Color.GRAY))) {
			recurse(x, y + 1, color, number + 1, grid);
			xs.add(x);
			ys.add(y + 1);
		}
		if (y - 1 >= 0 && grid[x][y - 1].equals(color) && (!grid[x][y - 1].equals(Color.BLACK))
				&& (!grid[x][y - 1].equals(Color.GRAY))) {
			recurse(x, y - 1, color, number + 1, grid);
			xs.add(x);
			ys.add(y - 1);
		}
		return greatestnumber;
	}

	private void drawPiece(Graphics g) {
		g.setColor(currentColor);
		for (Point p : Pieces[currentPiece][rotation]) {
			g.fillRect((p.x + pieceOrigin.x) * 26, (p.y + pieceOrigin.y) * 26, 25, 25);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g.fillRect(0, 0, 26 * 12, 26 * 23);
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 24; j++) {
				g.setColor(grid[i][j]);
				g.fillRect(26 * i, 26 * j, 25, 25);
			}
		}

		if (score >= level * 1000) {
			more = 4;
			level++;
			faster += 100;
		}

		g.setColor(Color.WHITE);
		g.drawString("SCORE: " + score, 126, 10);
		g.drawString("LEVEL: " + level, 129, 28);
		if (gameOver) {
			g.drawString("GAME OVER! FINAL", 15, 10);
		} else {
			drawPiece(g);
		}
		if (more > 0) {
			g.setColor(Color.WHITE);
			g.drawString("LEVEL UP!", 126, 200);
			more--;
		}
	}

	public static void main(String[] args) throws LineUnavailableException, IOException, UnsupportedAudioFileException {

		String soundName = "AllStarbutitstheTetristhemesong.wav";
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
		Clip clip = AudioSystem.getClip();
		clip.open(audioInputStream);
		clip.start();
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		JFrame f = new JFrame("Sirtet");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(12 * 26 + 10, 26 * 23 + 40);
		f.setVisible(true);

		final Sirtet game = new Sirtet();
		game.init();
		f.add(game);

		f.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {

				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (!gameOver) {
						game.rotate(+1);
					}
					break;
				case KeyEvent.VK_DOWN:
					if (!gameOver) {
						game.dropDown();
						game.score += 1;
					}
					break;

				case KeyEvent.VK_LEFT:
					if (!gameOver) {
						game.move(-1);
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (!gameOver) {
						game.move(+1);
					}
					break;
				case KeyEvent.VK_SPACE:
					if (!gameOver) {
						int tmp = game.pieceOrigin.y;
						game.hardDrop();
						game.score += 2 * (24 - tmp);
					}
					break;
				}
			}

			public void keyReleased(KeyEvent e) {
				if(gameOver){
					clip.stop();
				}
			}
		});

		new Thread() {
			@Override
			public void run() {
				while (!gameOver) {
					try {
						if (1000 - faster > 0)
							Thread.sleep(1000 - faster);
						else
							Thread.sleep(1);
						game.dropDown();
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
	}
}