package mazegen;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MazeGenerator {
	
	public static void main(String[] args) {
		int height = 100;
		int width = 100;
		
		generate(width, height);
	}
	
	public static int[][] generateMaze(int width, int height){
		
		Random rng = new Random();
		
		int[][] maze = new int[width][height];
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				maze[row][col] = 0;
			}
		}
		
		boolean reachedEnd = false;
		int startCol = rng.nextInt(width);
		int endCol = -1;
		int[] startPos = new int[] {startCol,0};
		int[] endPos = null;	
		int[] currentPos = startPos;	
		ArrayList<int[]> availableNeighbors = getAvailableNeighbors(maze, currentPos);
		ArrayList<int[]> path = new ArrayList<>();
		ArrayList<int[]> endPath = new ArrayList<>();
		
		maze[currentPos[0]][currentPos[1]] = 1;	
		path.add(currentPos);
		
		do{
			
				if(!reachedEnd && currentPos[1] == height-1)
				reachedEnd = true;
			
			if(availableNeighbors.size() == 0) {
				if(reachedEnd && currentPos[1] == height-1) {
					endCol = currentPos[0];
					endPos = currentPos;
					
					if(currentPos != path.get(path.size()-1))
						path.add(currentPos);
					
					endPath = new ArrayList<>(path);
					path = new ArrayList<>();
					break;
				}		
				
				if(reachedEnd)
					maze[currentPos[0]][currentPos[1]] = 0;
				currentPos = path.get(path.size()-1);
				if(!reachedEnd) {
					availableNeighbors = getAvailableNeighbors(maze, currentPos);	
				}
				if(availableNeighbors.size() == 0)
					path.remove(path.size()-1);
				continue;
			}
			
			int next = rng.nextInt(availableNeighbors.size());
			currentPos = availableNeighbors.get(next);
			path.add(currentPos);
			maze[currentPos[0]][currentPos[1]] = 1;
			availableNeighbors = getAvailableNeighbors(maze, currentPos);			
		} while(true);
		

		//printMaze(maze,startCol,endCol,endPath);
		
		currentPos = startPos;
		availableNeighbors = getAvailableNeighbors(maze, currentPos);
		int currentPathPos = 0;
		do {
			if(availableNeighbors.size() == 0) {
				if(path.size() == 0) {
					currentPos = endPath.get(currentPathPos);
					currentPathPos++;
					availableNeighbors = getAvailableNeighbors(maze, currentPos);	
					if(currentPos == endPos)
						break;
					else 
						continue;
				}
				
				currentPos = path.get(path.size()-1);
				path.remove(path.size()-1);
				availableNeighbors = getAvailableNeighbors(maze, currentPos);	
				continue;
			}
			
			int next = rng.nextInt(availableNeighbors.size());
			currentPos = availableNeighbors.get(next);
			path.add(currentPos);
			maze[currentPos[0]][currentPos[1]] = 1;
			availableNeighbors = getAvailableNeighbors(maze, currentPos);	
		} while (true);
		
		
		
		//change me
		int[][] newmaze = new int[maze.length+2][maze[0].length+2];
		
		for (int x = 0; x < maze.length; x++) {
			for (int y = 0; y < maze[0].length; y++) {
				newmaze[x+1][y+1] = maze[x][y];
			}
		}

		for (int x = 0; x < newmaze.length; x++) {
			newmaze[x][0] = 0;
		}
		
		for (int y = 0; y < newmaze[0].length; y++) {
			newmaze[0][y] = 0;
		}
		
		newmaze[startCol+1][0] = 1;
		newmaze[endCol+1][newmaze[0].length-1] = 1;
		
		return newmaze;
	}
	
	private static ArrayList<int[]> getAvailableNeighbors(int[][] maze, int[] currentPos){
		ArrayList<int[]> availableNeighbors = new ArrayList<>();
		
		for (int[] neighbor : getNeighbors(maze, currentPos)) {			
			int nx = neighbor[0];
			int ny = neighbor[1];
			int sum = 0;		
			

			if(maze[nx][ny] == 1) {
				continue;
			}

			if(nx+1 < maze.length)
				sum += maze[nx+1][ny];	
			if(ny+1 < maze[0].length)
				sum += maze[nx][ny+1];		
			if(nx-1 >= 0)
				sum += maze[nx-1][ny];		
			if(ny-1 >= 0)
				sum += maze[nx][ny-1];	
			
			if(sum<2) {
				availableNeighbors.add(new int[] {nx,ny});
			}
		}
			
		
		return availableNeighbors;
	}
	
	private static ArrayList<int[]> getNeighbors(int[][] maze, int[] currentPos) {
		ArrayList<int[]> neighbors = new ArrayList<>();
		int x = currentPos[0];
		int y = currentPos[1];

		if(x+1 < maze.length)
			neighbors.add(new int[] {x+1,y});
		if(y+1 < maze[0].length)
			neighbors.add(new int[] {x,y+1});
		if(x-1 >= 0)
			neighbors.add(new int[] {x-1,y});
		if(y-1 >= 0)
			neighbors.add(new int[] {x,y-1});	
		
		return neighbors;
	}

	public static File drawMaze(int[][] maze,int width,int height) 
    {
		int pixelHeight = 1;
		int pixelWidth = 1;
		
	    JFrame jf = new JFrame();
	    JLabel jl = new JLabel();

	    //3 bands in TYPE_INT_RGB
	    int NUM_BANDS = 3;
	    int[] arrayimage = new int[(width) * (height) * pixelHeight * pixelWidth * NUM_BANDS];

	    for (int i = 0; i < height; i++)
	    {
		    for (int k = 0; k < pixelHeight; k++) {
		    	for (int j = 0; j < width; j++) {
			        for (int band = 0; band < NUM_BANDS * pixelWidth; band++)
			        	arrayimage[((i * width * pixelWidth + k * width) + j)*NUM_BANDS*pixelWidth + band] = maze[j][height-i-1]*255;
			      }
			}	      
	    }
	    
	    Image image = getImageFromArray(arrayimage, (width) * pixelWidth, (height) * pixelHeight);

	    try {
		    File file = new File("maze.gif");
			ImageIO.write(imageToBufferedImage(image),"gif",file);
			System.out.println("printed image");
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		    return null;
		}
	    
    }
	
	public static BufferedImage imageToBufferedImage(Image im) {
	     BufferedImage bi = new BufferedImage
	        (im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
	     Graphics bg = bi.getGraphics();
	     bg.drawImage(im, 0, 0, null);
	     bg.dispose();
	     return bi;
	  }
	
	 public static Image getImageFromArray(int[] pixels, int width, int height)
	  {
	    BufferedImage image =
	        new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    WritableRaster raster = (WritableRaster) image.getData();
	    raster.setPixels(0, 0, width, height, pixels);
	    image.setData(raster);
	    return image;
	  }
	 
	 public static File generate(int width, int height) {
		 int[][] maze = generateMaze(width, height);
		 return drawMaze(maze, maze.length, maze[0].length);
	 }
	
}
