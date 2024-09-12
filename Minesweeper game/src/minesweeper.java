import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//class to represent the game
class Game extends World {
  int gridx;
  int gridy;
  int mines;
  ArrayList<ArrayList<Tile>> grid;
  Random rand;
  Random rand2;
  boolean showNum;
  boolean showFilled;
  boolean gameOver;
  int score;
  boolean tabPressed;
  WorldScene scene;
  boolean startGame;
  boolean gameWin;
  boolean firstGame;
  boolean testEnder;

  int newGridx;
  int newGridy;
  int newMines;

  Game(int gridx, int gridy, int mines) {
    this(gridx, gridy, mines, new Random(), new Random(), false, false, true, false);

  }

  Game(int gridx, int gridy, int mines, Random rand, Random rand2) {
    this(gridx, gridy, mines, rand, rand2, false, false, false, true);
  }


  Game(int gridx, int gridy, int mines, Random rand, Random rand2, 
      boolean showNum, boolean showFilled, boolean firstGame, boolean testEnder) {
    this.gridx = gridx;
    this.gridy = gridy;
    this.mines = mines;
    this.grid = new ArrayList<ArrayList<Tile>>();
    this.rand = rand;
    this.rand2 = rand2;
    this.showNum = showNum;
    this.showFilled = showFilled;
    this.score = 0;
    tabPressed = true;
    this.newGridx = 0;
    this.newGridy = 0;
    this.newMines = 0;
    this.scene = new WorldScene(0, 0);
    this.startGame = false;
    this.gameWin = false;
    this.firstGame = firstGame;
    this.testEnder = testEnder;


    if (!this.firstGame) {
      this.resetGame(mines);
    }
  }



  //creates the grid
  void gridMaker(int sizex, int sizey, boolean showFilled, boolean showNum) {
    for (int i = 0 ; i < sizex ; i++) {
      ArrayList<Tile> tileArray = new ArrayList<Tile>();

      for (int j = 0 ; j < sizey ; j++) {

        Tile falseTile = new Tile(false, false, false, false, false); 
        Tile borderT = new Tile(false, true, false, false, false); 

        if (i == 0 || j == 0 || j == (sizey - 1) || i == (sizex - 1)) {
          tileArray.add(borderT);
        } else {
          tileArray.add(falseTile);
        }
      }

      grid.add(tileArray);
    }
  }

  //sets the mines on the grid
  void mineMaker(int sizex, int sizey, int mineNum) {
    for (int i = 0 ; i < mineNum ; i++) {
      int randx = (this.rand.nextInt(sizex));
      int randy = (this.rand2.nextInt(sizey));
      Tile trueTile = new Tile(true, false, false, false, false); 

      if (!(grid.get(randx).get(randy).mine) 
          && 
          !(grid.get(randx).get(randy).borderTile) 
          && 
          !(grid.get(randx).get(randy).filled) 
          && 
          !(grid.get(randx).get(randy).displayNum)) {
        grid.get(randx).set(randy, trueTile);
      } else {
        i--;
      }
    }
  }

  //retrieves the neighbors of a tile to determine whether or not there is a bomb
  void findNeighbors(int sizex, int sizey) {
    for (int i = 0 ; i < sizex ; i++) {
      for (int j = 0 ; j < sizey ; j++) {
        if (!grid.get(i).get(j).borderTile) {
          for (int k = -1; k < 7 ; k++) {
            if (k < 2) {
              grid.get(i).get(j).neighbors.add(grid.get(i - 1).get(j + k));
            } else if (k == 2) {
              grid.get(i).get(j).neighbors.add(grid.get(i).get(j - 1));
            } else if (k == 3) {
              grid.get(i).get(j).neighbors.add(grid.get(i).get(j + 1));
            } else {
              grid.get(i).get(j).neighbors.add(grid.get(i + 1).get(j + (k - 5)));
            }
          }
        } else { 
          j += 0; 
        }
      }
    } 
  }

  //find the amount of mines on the grid
  void mineFinder(int sizex, int sizey) {
    for (int i = 0 ; i < sizex ; i++) {
      for (int j = 0 ; j < sizey ; j++) {
        if (!grid.get(i).get(j).borderTile) {
          for (int k = 0 ; k < 8 ; k++) {
            if (grid.get(i).get(j).neighbors.get(k).mine) {
              grid.get(i).get(j).numMines += 1;
            } 
          }
        } else {
          j += 0;
        }
      }
    }
  }

  //reset the game
  public void resetGame(int newMines) {

    int sizey = this.gridy + 2;
    int sizex = this.gridx + 2;

    this.mines = newMines;
    this.grid = new ArrayList<ArrayList<Tile>>();
    this.score = 0;
    this.newMines = 0;
    this.gameOver = false;
    this.tabPressed = false;
    this.scene = new WorldScene(0, 0);
    this.startGame = false;
    this.gameWin = false;
    this.firstGame = false;

    //creates the grid based off the x and y
    this.gridMaker(sizex, sizey, showNum, showNum);

    //sets the mines on the grid
    this.mineMaker(sizex, sizey, mines);

    //retrieves the neighbors of a tile to determine whether or not there is a bomb
    this.findNeighbors(sizex, sizey);

    //find the amount of mines on the grid
    this.mineFinder(sizex, sizey);

  }

  //onKeyEvent handler
  public void onKeyEvent(String key) {

    //handle all of the possible key events
    if (key.equals("tab") || key.equals("enter") || key.equals("1") 
        || key.equals("2") || key.equals("3") || key.equals("4") 
        || key.equals("5")) {

      //reset the game with new number of mines
      if (this.newMines != 0 && this.startGame && !this.testEnder) {
        this.resetGame(newMines);
      }

      //if tab is clicked restart the game
      if (key.equals("tab")) {
        this.tabPressed = true;
      }

      //if enter is clicked start the game
      if (key.equals("enter")) {
        this.startGame = true;
      }

      //create a new amount of mines based on user input
      if (!key.equals("enter") && !key.equals("tab")) {
        newMines = Integer.parseInt(key) * 7;
      }
    }
  }

  //draws the grid 
  public WorldScene makeScene() {

    //first image of the game
    if (this.firstGame) {
      int sizey = this.gridy + 2;
      int sizex = this.gridx + 2;

      WorldScene firstScene = new WorldScene(sizex * 40, sizey * 40);
      firstScene.placeImageXY(new RectangleImage(sizex * 22, sizey * 12, 
          OutlineMode.SOLID, Color.BLACK), sizex * 20, sizey * 20);
      TextImage restartMessage = new TextImage("Input a level 1 - 5", 
          20, FontStyle.BOLD, Color.GREEN);
      firstScene.placeImageXY(restartMessage, sizex * 20, sizey * 18);
      TextImage enterMessage = new TextImage("and then press enter/return", 
          20, FontStyle.BOLD, Color.GREEN);
      firstScene.placeImageXY(enterMessage, sizex * 20, sizey * 20);
      TextImage numberOfNewMines = new TextImage("Mine Amount: " + String.valueOf(newMines),
          20, FontStyle.BOLD, Color.GREEN);
      firstScene.placeImageXY(numberOfNewMines, sizex * 20, sizey * 22);

      return firstScene;

    } else {

      int sizey = this.gridy + 2;
      int sizex = this.gridx + 2;
      scene = new WorldScene(sizex * 40, sizey * 40);
      scene.placeImageXY(new RectangleImage(sizex * 40, sizey * 40, 
          OutlineMode.SOLID, Color.gray), sizex * 20, sizey * 20);

      //traverse the board
      for (int i = 0 ; i < sizex ; i++) {
        for (int j = 0 ; j < sizey ; j++) {

          //border tile image
          if (grid.get(i).get(j).borderTile) {
            scene.placeImageXY(new RectangleImage(40, 40, 
                OutlineMode.SOLID, Color.white), i * 40 + 20, j * 40 + 20);

            //flag image
          } else if (grid.get(i).get(j).flagged) {
            scene.placeImageXY(new RectangleImage(40, 40, 
                OutlineMode.SOLID, Color.GRAY), i * 40 + 20, j * 40 + 20);
            scene.placeImageXY(new RectangleImage(10, 10, 
                OutlineMode.SOLID, Color.BLUE), i * 40 + 20, j * 40 + 20);

          } else if (grid.get(i).get(j).mine && grid.get(i).get(j).filled) {
            // Create an exploded bomb image with rotated star-shaped explosions
            WorldImage bombWithExplosions = new CircleImage(20, 
                OutlineMode.SOLID, Color.BLACK);
            WorldImage small = new RotateImage(new StarImage(10, 5, 
                OutlineMode.SOLID, Color.RED), 75);
            WorldImage medium = new RotateImage(new StarImage(15, 5, 
                OutlineMode.SOLID, Color.orange), 50);
            WorldImage large = new RotateImage(new StarImage(20, 5, 
                OutlineMode.SOLID, Color.yellow), 25);

            WorldImage first = new OverlayImage(small, medium);
            WorldImage second = new OverlayImage(first, large);
            WorldImage last = new OverlayImage(second, bombWithExplosions);
            scene.placeImageXY(last, i * 40 + 20, j * 40 + 20); 

            //unfilled cell image
          } else if (grid.get(i).get(j).mine && !grid.get(i).get(j).filled) {
            scene.placeImageXY(new RectangleImage(40, 40, 
                OutlineMode.SOLID, Color.GRAY), i * 40 + 20, j * 40 + 20);

            //filled cell image
          } else if (grid.get(i).get(j).displayNum) {
            scene.placeImageXY(new RectangleImage(40, 40, 
                OutlineMode.SOLID, Color.LIGHT_GRAY), i * 40 + 20, j * 40 + 20);

            //number that displays amount of bombs adjacent
            if (grid.get(i).get(j).numMines != 0) {
              TextImage mineDisplay = new TextImage(String.valueOf(grid.get(i).get(j).numMines), 
                  20, FontStyle.BOLD, this.numColor(grid.get(i).get(j).numMines));
              scene.placeImageXY(mineDisplay, i * 40 + 20, j * 40 + 20);
            }

            //filled cell image
          } else if (grid.get(i).get(j).filled) {
            scene.placeImageXY(new RectangleImage(40, 40, 
                OutlineMode.SOLID, Color.LIGHT_GRAY), i * 40 + 20, j * 40 + 20);

          }

          if (!grid.get(i).get(j).borderTile) {
            scene.placeImageXY(new RectangleImage(40, 40, 
                OutlineMode.OUTLINE, Color.BLACK), i * 40 + 20, j * 40 + 20);
          }
        }
      }

      //level picker scene
      if ((this.gameOver || this.gameWin) && this.tabPressed) {

        scene.placeImageXY(new RectangleImage(sizex * 22, sizey * 12, 
            OutlineMode.OUTLINE, Color.CYAN), sizex * 20, sizey * 20);
        TextImage restartMessage = new TextImage("Input a level 1 - 5", 
            20, FontStyle.BOLD, Color.CYAN);
        scene.placeImageXY(restartMessage, sizex * 20, sizey * 18);
        TextImage enterMessage = new TextImage("and then press enter/return", 
            20, FontStyle.BOLD, Color.CYAN);
        scene.placeImageXY(enterMessage, sizex * 20, sizey * 20);
        TextImage numberOfNewMines = new TextImage("New Mine Amount: " + String.valueOf(newMines),
            20, FontStyle.BOLD, Color.CYAN);
        scene.placeImageXY(numberOfNewMines, sizex * 20, sizey * 22);


        //game over scene
      } else if (this.gameOver) {

        scene.placeImageXY(new RectangleImage(sizex * 22, sizey * 12, 
            OutlineMode.OUTLINE, Color.CYAN), sizex * 20, sizey * 20);
        TextImage endMessage = new TextImage("SORRY, YOU LOST", 
            20, FontStyle.BOLD, Color.CYAN);
        scene.placeImageXY(endMessage, sizex * 20, sizey * 18);
        TextImage scoreImage = new TextImage("Score: " + String.valueOf(score), 
            20, FontStyle.BOLD, Color.CYAN);
        scene.placeImageXY(scoreImage, sizex * 20, sizey * 20);
        TextImage tabMessage = new TextImage("Press tab To restart", 
            20, FontStyle.BOLD, Color.CYAN);
        scene.placeImageXY(tabMessage, sizex * 20, sizey * 22);

        //game win Scene
      } else if (this.gameWin) {

        scene.placeImageXY(new RectangleImage(sizex * 22, sizey * 12, 
            OutlineMode.OUTLINE, Color.CYAN), sizex * 20, sizey * 20);
        TextImage endMessage = new TextImage("WOOO, YOU WON", 
            20, FontStyle.BOLD, Color.CYAN);
        scene.placeImageXY(endMessage, sizex * 20, sizey * 18);
        TextImage scoreImage = new TextImage("Score: " + String.valueOf(score), 
            20, FontStyle.BOLD, Color.CYAN);
        scene.placeImageXY(scoreImage, sizex * 20, sizey * 20);
        TextImage tabMessage = new TextImage("Press tab To restart", 
            20, FontStyle.BOLD, Color.CYAN);
        scene.placeImageXY(tabMessage, sizex * 20, sizey * 22);
      }
      return scene;
    }
  }

  //onMouseClicked handler
  public void onMouseClicked(Posn pos, String key) {
    if ("LeftButton".equals(key) && !this.gameOver) {
      this.cellClicked(pos);
    } else if ("RightButton".equals(key) && !this.gameOver) {
      this.flagCell(pos);
    }

    int sizey = this.gridy + 2;
    int sizex = this.gridx + 2;
    int correctCounter = 0;
    for (int i = 0 ; i < sizex ; i++) {
      for (int j = 0 ; j < sizey ; j++) {
        if ((grid.get(i).get(j).mine && !grid.get(i).get(j).filled && grid.get(i).get(j).flagged) 
            || (!grid.get(i).get(j).mine && 
                grid.get(i).get(j).filled && !grid.get(i).get(j).flagged) 
            || grid.get(i).get(j).borderTile) {
          correctCounter++;
        }
      }
    }

    if (correctCounter == sizex * sizey) {
      this.gameWin = true;
    }
  }

  //if the cell is flagged set flagged to true
  public void flagCell(Posn pos) {

    Tile position = grid.get((int)Math.floor(pos.x / 40)).get((int)Math.floor(pos.y / 40));

    if (!position.flagged && !position.borderTile) {
      position.flagged = true;
    } else {
      position.flagged = false;
    }
  }

  //if the cell is clicked run the fill method
  public void cellClicked(Posn pos) {
    Tile position = grid.get((int)Math.floor(pos.x / 40)).get((int)Math.floor(pos.y / 40));

    if (!position.borderTile) {
      position.fill();
    }

    if (position.mine) {
      this.gameOver = true;
    } else {
      score += 10;
    }
  }

  //returns a color based off the given integer
  Color numColor(int num) {
    if (num == 1) {
      return Color.BLUE;
    } else if (num == 2) {
      return Color.GREEN;
    } else if (num == 3) {
      return Color.YELLOW;
    } else {
      return Color.RED;
    }
  }
}

//class to represent a tile
class Tile {
  boolean mine;
  boolean borderTile;
  int numMines;
  boolean filled;
  boolean displayNum;
  ArrayList<Tile> neighbors;
  boolean flagged;

  Tile(boolean mine, boolean borderTile, boolean filled, boolean displayNum, boolean flagged) {
    this(mine, borderTile, filled, displayNum, flagged, new ArrayList<Tile>());
  }

  //constructor for a tile
  Tile(boolean mine, boolean borderTile, boolean filled, boolean displayNum, 
      boolean flagged, ArrayList<Tile> neighbors) {
    this.mine = mine;
    this.borderTile = borderTile;
    this.filled = filled;
    this.displayNum = displayNum;
    this.numMines = 0;
    this.flagged = flagged;
    this.neighbors = neighbors;
  }

  //fill the neighbors of each tile until a mine is adjacent
  public void fill() {

    if (!this.filled) {
      this.filled = true;
      this.displayNum = true;
      if (this.numMines == 0) {
        for (int i = 0; i < this.neighbors.size(); i++) {
          if (!neighbors.get(i).borderTile) {
            neighbors.get(i).fill();
          }
        }
      }
    }
  }
}

//examples class
class ExamplesMinesweeper {
  Random rand;
  Random rand2;

  //constructor for random
  ExamplesMinesweeper() {
    this(new Random(), new Random());
  }

  //constructor for random
  ExamplesMinesweeper(Random rand, Random rand2) {
    this.rand = rand;
    this.rand2 = rand2;
  }

  //examples of a world
  World world;
  World worldCheck;
  World worldCheck1;

  //examples of tiles
  Tile falseTile;
  Tile trueTile;
  Tile borderT;
  Tile fillTile;

  //examples of ArrayList<Tile>
  ArrayList<Tile> borderList;
  ArrayList<Tile> list1;
  ArrayList<Tile> list2;
  ArrayList<Tile> neighborList;

  //ArrayList of tiles
  ArrayList<ArrayList<Tile>> gridCheck;

  //examples of a game
  Game game1;
  Game game2;
  Game game2Mines;

  //initial conditions
  void initConditions() {

    //examples of a world
    world = new Game(5, 5, 1);
    worldCheck = new Game(12, 12, 5, (new Random(14)), (new Random(15)), true, true, false, true);
    worldCheck1 = new Game(2, 2, 1, (new Random(15)), (new Random(15)));
    game1 = new Game(4, 4, 4, (new Random(15)), (new Random(15)));
    game2Mines = new Game(4, 4, 4, (new Random(15)), (new Random(15)));


    game2 = new Game(12, 12, 5);

    //examples of tiles
    falseTile = new Tile(false, false, false, false, false); 
    trueTile = new Tile(false, false, true, false, false); 
    borderT = new Tile(false, true, false, false, false);

    neighborList = new ArrayList<Tile>(Arrays.asList(trueTile));

    fillTile = new Tile(false, false, false, false, false, neighborList); 

    //examples of ArrayList<Tile>
    borderList = new ArrayList<Tile>();
    borderList.add(borderT);
    borderList.add(borderT);
    borderList.add(borderT);
    borderList.add(borderT);

    list1 = new ArrayList<Tile>();
    list1.add(borderT);

    gridCheck = new ArrayList<ArrayList<Tile>>();

  }

  //running big bang
  void testWordClass(Tester t) {

    //setting up the initial conditions
    initConditions(); 

    //checking that the expected changes have occurred
    this.game2.bigBang((14) * 40, (14) * 40, 0);
  }

  //testing the onMouseClicked method
  void testOnMouseClicked(Tester t) {

    // Setting up the initial conditions
    this.initConditions();

    // Test when the rightButton is clicked on a cell
    this.game1.onMouseClicked(new Posn(40, 40), "RightButton"); 

    //check that the expected changes have occurred
    t.checkExpect(this.game1.grid.get(1).get(1).flagged, true);

    //test when the leftButton is clicked on a cell
    this.game1.onMouseClicked(new Posn(80, 80), "LeftButton");

    //check that the expected changes have occurred
    t.checkExpect(this.game1.grid.get(2).get(2).filled, true);
  }

  //testing the onKeyEvent method
  void testOnKeyEvent(Tester t) {

    // Setting up the initial conditions
    this.initConditions();

    // Checking the behavior when "tab" key is pressed
    t.checkExpect(this.game1.tabPressed, false);
    this.game1.onKeyEvent("tab");
    t.checkExpect(this.game1.tabPressed, true);
  }

  //testing the flagCell method
  void testFlagCell(Tester t) {

    // Setting up the initial conditions
    this.initConditions();

    //modify the gameBoard
    this.game1.flagCell(new Posn(40, 40));

    //check that the expected changes have occurred
    t.checkExpect(this.game1.grid.get(1).get(1).flagged, true);
  }

  //testing the cellClicked method
  void testCellClicked(Tester t) {

    // Setting up the initial conditions
    this.initConditions();

    //modify the gameBoard
    this.game1.cellClicked(new Posn(40, 40));

    //check that the expected changes have occurred
    t.checkExpect(this.game1.grid.get(1).get(1).filled, true);
  }

  //testing the fillMethod
  void testFill(Tester t) {

    // Setting up the initial conditions
    this.initConditions();

    //check what the tile is before begin modified
    t.checkExpect(this.fillTile.filled, false);

    //modify the falseTile to be filed
    this.fillTile.fill();

    //check that the expected changes have occurred
    t.checkExpect(this.fillTile.filled, true);

  }

  //testing the gridMaker method
  void testGridMaker(Tester t) {

    //setting up the initial conditions
    initConditions();

    //checking the original size of the grid
    t.checkExpect(game1.grid.size(), 6);

    //modifying the data
    this.game1.gridMaker(10, 10, true, true);

    //check that the expected changes have occurred
    t.checkExpect(game1.grid.size(), 16);

  }

  //testing the findNeighbors method
  void testFindNeighbors(Tester t) {

    //setting up the initial conditions
    initConditions();

    //modifying the data
    this.game1.findNeighbors(game1.gridx, game1.gridy);

    //check that the expected changes have occurred
    t.checkExpect(game1.grid.get(1).get(1).neighbors.contains(game1.grid.get(1).get(1)), false);
    t.checkExpect(game1.grid.get(1).get(2).neighbors.contains(game1.grid.get(2).get(3)), true);
    t.checkExpect(game1.grid.get(4).get(5).neighbors.contains(game1.grid.get(5).get(1)), false);
    t.checkExpect(game1.grid.get(5).get(5).neighbors.contains(game1.grid.get(5).get(5)), false);
  }

  //testing the worldScene method
  boolean testWorldScene(Tester t) {

    //setting up the initial conditions
    initConditions();

    //places tiles on a world scene
    WorldScene scene = new WorldScene(4 * 40, 4 * 40);
    scene.placeImageXY(new RectangleImage(4 * 40, 4 * 40, 
        OutlineMode.SOLID, Color.gray), 4 * 20, 4 * 20);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 20, 20);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 20, 60);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 20, 100);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 20, 140);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 20, 180);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 60, 20);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.BLACK), 60, 60);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.BLACK), 60, 100);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 60, 140);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 60, 180);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 100, 20);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.BLACK), 100, 60);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.BLACK), 100, 100);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 100, 140);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 100, 180);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 140, 20);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 140, 60);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 140, 100);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 140, 140);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 140, 180);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 180, 20);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 180, 60);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 180, 100);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 180, 140);
    scene.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE), 180, 180);

    //check that the makeScene method returns the correct scene
    return t.checkExpect(this.worldCheck1.makeScene(), scene);
  }

  //testing the numColor method
  void testNumColor(Tester t) {

    //setting up the initial conditions
    initConditions();

    //checking the numColor method
    t.checkExpect(this.game1.numColor(1), Color.BLUE);
    t.checkExpect(this.game1.numColor(2), Color.GREEN);
    t.checkExpect(this.game1.numColor(3), Color.YELLOW);
    t.checkExpect(this.game1.numColor(4), Color.RED);
  }
}

