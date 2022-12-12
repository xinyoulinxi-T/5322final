package com.hlju.Tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tetris extends JPanel {

	private static final long serialVersionUID = -807909536278284335L;
	private static final int BlockSize = 10;
	private static final int BlockWidth = 16;
	private static final int BlockHeigth = 26;
	private static final int TimeDelay = 1000;

	private static final String[] AuthorInfo = {
			"zhouhe","Hello"
	};


	private boolean[][] BlockMap = new boolean[BlockHeigth][BlockWidth];


	private int Score = 0;
	

	private boolean IsPause = false;


	static boolean[][][] Shape = BlockV4.Shape;


	private Point NowBlockPos;


	private boolean[][] NowBlockMap;

	private boolean[][] NextBlockMap;

	private int NextBlockState;
	private int NowBlockState;
	

	private Timer timer;

	public Tetris() {

		this.Initial();
		timer = new Timer(Tetris.TimeDelay, this.TimerListener);
		timer.start();
		this.addKeyListener(this.KeyListener);
	}
	
	public void SetMode(String mode){
		if (mode.equals("v6")){
			Tetris.Shape = BlockV6.Shape;
		}
		else{
			Tetris.Shape = BlockV4.Shape;
		}
		this.Initial();
		this.repaint();
	}


	private void getNextBlock() {

		this.NowBlockState = this.NextBlockState;
		this.NowBlockMap = this.NextBlockMap;

		this.NextBlockState = this.CreateNewBlockState();
		this.NextBlockMap = this.getBlockMap(NextBlockState);

		this.NowBlockPos = this.CalNewBlockInitPos();
	}
	

	private boolean IsTouch(boolean[][] SrcNextBlockMap,Point SrcNextBlockPos) {
		for (int i = 0; i < SrcNextBlockMap.length;i ++){
			for (int j = 0;j < SrcNextBlockMap[i].length;j ++){
				if (SrcNextBlockMap[i][j]){
					if (SrcNextBlockPos.y + i >= Tetris.BlockHeigth || SrcNextBlockPos.x + j < 0 || SrcNextBlockPos.x + j >= Tetris.BlockWidth){
						return true;
					}
					else{
						if (SrcNextBlockPos.y + i < 0){
							continue;
						}
						else{
							if (this.BlockMap[SrcNextBlockPos.y + i][SrcNextBlockPos.x + j]){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	

	private boolean FixBlock(){
		for (int i = 0;i < this.NowBlockMap.length;i ++){
			for (int j = 0;j < this.NowBlockMap[i].length;j ++){
				if (this.NowBlockMap[i][j])
					if (this.NowBlockPos.y + i < 0)
						return false;
					else
						this.BlockMap[this.NowBlockPos.y + i][this.NowBlockPos.x + j] = this.NowBlockMap[i][j];
			}
		}
		return true;
	}
	

	private Point CalNewBlockInitPos(){
		return new Point(Tetris.BlockWidth / 2 - this.NowBlockMap[0].length / 2, - this.NowBlockMap.length);
	}


	public void Initial() {
		//Map
		for (int i = 0;i < this.BlockMap.length;i ++){
			for (int j = 0;j < this.BlockMap[i].length;j ++){
				this.BlockMap[i][j] = false;
			}
		}

		this.Score = 0;

		this.NowBlockState = this.CreateNewBlockState();
		this.NowBlockMap = this.getBlockMap(this.NowBlockState);
		this.NextBlockState = this.CreateNewBlockState();
		this.NextBlockMap = this.getBlockMap(this.NextBlockState);

		this.NowBlockPos = this.CalNewBlockInitPos();
		this.repaint();
	}
	
	public void SetPause(boolean value){
		this.IsPause = value;
		if (this.IsPause){
			this.timer.stop();
		}
		else{
			this.timer.restart();
		}
		this.repaint();
	}


	private int CreateNewBlockState() {
		int Sum = Tetris.Shape.length * 4;
		return (int) (Math.random() * 1000) % Sum;
	}

	private boolean[][] getBlockMap(int BlockState) {
		int Shape = BlockState / 4;
		int Arc = BlockState % 4;
		System.out.println(BlockState + "," + Shape + "," + Arc);
		return this.RotateBlock(Tetris.Shape[Shape], Arc);
	}

	
	private boolean[][] RotateBlock(boolean[][] shape, int time) {
		if(time == 0) {
			return shape;
		}
		int heigth = shape.length;
		int width = shape[0].length;
		boolean[][] ResultMap = new boolean[heigth][width];
		int tmpH = heigth - 1, tmpW = 0;
		for(int i = 0; i < heigth && tmpW < width; i++) {
			for(int j = 0; j < width && tmpH > -1; j++) {
				ResultMap[i][j] = shape[tmpH][tmpW];
				tmpH--;
			}
			tmpH = heigth - 1;
			tmpW++;
		}
		for(int i = 1; i < time; i++) {
			ResultMap = RotateBlock(ResultMap, 0);
		}
		return ResultMap;
	}


	static public void main(String... args) {
		boolean[][] SrcMap = Tetris.Shape[3];
		Tetris.ShowMap(SrcMap);

		
		Tetris tetris = new Tetris();
		boolean[][] result = tetris.RotateBlock(SrcMap, 1);
		Tetris.ShowMap(result);
		
	}
	

	static private void ShowMap(boolean[][] SrcMap){
		System.out.println("-----");
		for (int i = 0;i < SrcMap.length;i ++){
			for (int j = 0;j < SrcMap[i].length;j ++){
				if (SrcMap[i][j])
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println("-----");
	}

	/**
	 *
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//
		for (int i = 0; i < Tetris.BlockHeigth + 1; i++) {
			g.drawRect(0 * Tetris.BlockSize, i * Tetris.BlockSize, Tetris.BlockSize, Tetris.BlockSize);
			g.drawRect((Tetris.BlockWidth + 1) * Tetris.BlockSize, i * Tetris.BlockSize, Tetris.BlockSize,
					Tetris.BlockSize);
		}
		for (int i = 0; i < Tetris.BlockWidth; i++) {
			g.drawRect((1 + i) * Tetris.BlockSize, Tetris.BlockHeigth * Tetris.BlockSize, Tetris.BlockSize,
					Tetris.BlockSize);
		}
		//
		for (int i = 0; i < this.NowBlockMap.length; i++) {
			for (int j = 0; j < this.NowBlockMap[i].length; j++) {
				if (this.NowBlockMap[i][j])
					g.fillRect((1 + this.NowBlockPos.x + j) * Tetris.BlockSize, (this.NowBlockPos.y + i) * Tetris.BlockSize,
						Tetris.BlockSize, Tetris.BlockSize);
			}
		}
		//
		for (int i = 0; i < Tetris.BlockHeigth; i++) {
			for (int j = 0; j < Tetris.BlockWidth; j++) {
				if (this.BlockMap[i][j])
					g.fillRect(Tetris.BlockSize + j * Tetris.BlockSize, i * Tetris.BlockSize, Tetris.BlockSize,
						Tetris.BlockSize);
			}
		}
		//
		for (int i = 0;i < this.NextBlockMap.length;i ++){
			for (int j = 0;j < this.NextBlockMap[i].length;j ++){
				if (this.NextBlockMap[i][j])
					g.fillRect(190 + j * Tetris.BlockSize, 30 + i * Tetris.BlockSize, Tetris.BlockSize, Tetris.BlockSize);
			}
		}
		//
		g.drawString("final:", 190, 10);
		for (int i = 0;i < Tetris.AuthorInfo.length;i ++){
			g.drawString(Tetris.AuthorInfo[i], 190, 100 + i * 20);
		}

		//������ͣ
		if (this.IsPause){
			g.setColor(Color.green);
			g.fillRect(70, 100, 50, 20);
			g.setColor(Color.cyan);
			g.drawRect(70, 100, 50, 20);
			g.drawString("PAUSE", 75, 113);
		}
	}
	/**
	 * 
	 * @return
	 */
	private int ClearLines(){
		int lines = 0;
		for (int i = 0;i < this.BlockMap.length;i ++){
			boolean IsLine = true;
			for (int j = 0;j < this.BlockMap[i].length;j ++){
				if (!this.BlockMap[i][j]){
					IsLine = false;
					break;
				}
			}
			if (IsLine){
				for (int k = i;k > 0;k --){
					this.BlockMap[k] = this.BlockMap[k - 1];
				}
				this.BlockMap[0] = new boolean[Tetris.BlockWidth];
				lines ++;
			}
		}
		return lines;
	}
	
	// ��ʱ������
	ActionListener TimerListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO
			if (Tetris.this.IsTouch(Tetris.this.NowBlockMap, new Point(Tetris.this.NowBlockPos.x, Tetris.this.NowBlockPos.y + 1))){
				if (Tetris.this.FixBlock()){
					Tetris.this.Score += Tetris.this.ClearLines() * 10;
					Tetris.this.getNextBlock();
				}
				else{
					JOptionPane.showMessageDialog(Tetris.this.getParent(), "GAME OVER");
					Tetris.this.Initial();
				}
			}
			else{
				Tetris.this.NowBlockPos.y ++;
			}
			Tetris.this.repaint();
		}
	};
	
	//
	java.awt.event.KeyListener KeyListener = new java.awt.event.KeyListener(){

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO
			if (!IsPause){
				Point DesPoint;
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
					DesPoint = new Point(Tetris.this.NowBlockPos.x, Tetris.this.NowBlockPos.y + 1);
					if (!Tetris.this.IsTouch(Tetris.this.NowBlockMap, DesPoint)){
						Tetris.this.NowBlockPos = DesPoint;
					}
					break;
				case KeyEvent.VK_UP:
					boolean[][] TurnBlock = Tetris.this.RotateBlock(Tetris.this.NowBlockMap,1);
					if (!Tetris.this.IsTouch(TurnBlock, Tetris.this.NowBlockPos)){
						Tetris.this.NowBlockMap = TurnBlock;
					}
					break;
				case KeyEvent.VK_RIGHT:
					DesPoint = new Point(Tetris.this.NowBlockPos.x + 1, Tetris.this.NowBlockPos.y);
					if (!Tetris.this.IsTouch(Tetris.this.NowBlockMap, DesPoint)){
						Tetris.this.NowBlockPos = DesPoint;
					}
					break;
				case KeyEvent.VK_LEFT:
					DesPoint = new Point(Tetris.this.NowBlockPos.x - 1, Tetris.this.NowBlockPos.y);
					if (!Tetris.this.IsTouch(Tetris.this.NowBlockMap, DesPoint)){
						Tetris.this.NowBlockPos = DesPoint;
					}
					break;
				}
				//System.out.println(Tetris.this.NowBlockPos);
				repaint();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO
			
		}
		
	};
}
