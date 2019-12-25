package sfs2x.extensions.games.tris;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;
import sfs2x.extensions.games.tris.data.Position;
import sfs2x.extensions.games.tris.enums.GameState;
import sfs2x.extensions.games.tris.enums.Tile;

public final class TrisGameBoard {
    private static final int BOARD_SIZE = 4;
    private final Tile[][] board;
    private int winner = 0;

    public TrisGameBoard() {
        board = new Tile[BOARD_SIZE][BOARD_SIZE];
        reset();
    }

    public Tile getTileAt(Position position) {
        return getTileAt(position.x, position.y);
    }

    public Tile getTileAt(int x, int y) {
        return board[y][x];
    }

    public void setTileAt(Position position, Tile tile) {
        setTileAt(position.x, position.y, tile);
    }

    public void setTileAt(int x, int y, Tile tile) {
        checkCoords(x, y);
        board[y][x] = tile;
    }

    public int getWinner() {
        return winner;
    }

    public GameState getGameStatus(int moveCount) {
        Iterator<String> iter = getAllSolutions().iterator();
        GameState state = GameState.RUNNING;

        while (iter.hasNext()) {
            String solution = iter.next();
            iter.remove();

            // Player 1 wins
            if (solution.equals("111")) {
                state = GameState.END_WITH_WINNER;
                winner = 1;
                break;
            } else if (solution.equals("222")) {
                state = GameState.END_WITH_WINNER;
                winner = 2;
                break;
            }
        }

        if (winner == 0 && moveCount == 9)
            state = GameState.END_WITH_TIE;

        return state;
    }

    public void reset() {
        winner = 0;

        for (int y = 1; y < BOARD_SIZE; y++) {
            Tile[] boardRow = board[y];

            for (int x = 1; x < BOARD_SIZE; x++) {
                boardRow[x] = Tile.EMPTY;
            }
        }
    }

    private void checkCoords(int x, int y) {
        if (x < 1 || x > 3)
            throw new IllegalArgumentException("Tile X position out of range: " + x);

        if (y < 1 || y > 3)
            throw new IllegalArgumentException("Tile Y position out of range: " + y);
    }

    private List<String> getAllSolutions() {
        List<String> solutions = new ArrayList<String>();

        //--- All rows ------------------------------------------------
        for (int y = 1; y <= 3; y++) {
            solutions.add
                    (
                            String.format
                                    (
                                            "%s%s%s",
                                            String.valueOf(board[y][1].getId()),
                                            String.valueOf(board[y][2].getId()),
                                            String.valueOf(board[y][3].getId())
                                    )
                    );
        }

        //--- All cols ------------------------------------------------
        for (int x = 1; x <= 3; x++) {
            solutions.add
                    (
                            String.format
                                    (
                                            "%s%s%s",
                                            String.valueOf(board[1][x].getId()),
                                            String.valueOf(board[2][x].getId()),
                                            String.valueOf(board[3][x].getId())
                                    )
                    );
        }

        //--- All diagonals ------------------------------------------
        solutions.add
                (
                        String.format
                                (
                                        "%s%s%s",
                                        String.valueOf(board[1][1].getId()),
                                        String.valueOf(board[2][2].getId()),
                                        String.valueOf(board[3][3].getId())
                                )
                );

        solutions.add
                (
                        String.format
                                (
                                        "%s%s%s",
                                        String.valueOf(board[1][3].getId()),
                                        String.valueOf(board[2][2].getId()),
                                        String.valueOf(board[3][1].getId())
                                )
                );
        System.out.println("SOLUTIONS: " + solutions);
        return solutions;
    }

    public ISFSArray toSFSArray() {
        ISFSArray sfsa = new SFSArray();

        for (int y = 0; y < 3; y++) {
            // Use 1-based indexes for the board
            sfsa.addShortArray(getRowAsList(y + 1));
        }

        return sfsa;
    }

    private List<Short> getRowAsList(int y) {
        List<Short> row = new ArrayList<Short>();

        for (int x = 1; x <= 3; x++)
            row.add((short) board[y][x].getId());

        return row;
    }

    public Position randomMove(Tile tile) {
        List<Position> emptyTiles = new ArrayList();
        for (int x = 1; x <= 3; x++)
            for (int y = 1; y <= 3; y++)
                if (getTileAt(x, y) == Tile.EMPTY) {
                    emptyTiles.add(new Position(x, y));
                }
        if (emptyTiles.size() > 0) {
            return emptyTiles.get((int) (Math.random() * emptyTiles.size()));
        } else {
            throw new IllegalArgumentException("Not found empty tiles");
        }
    }

    public Position optimalMove(Tile tile) {
        Position optimalPosition = null;
        int counter;
        int offsetPosition;

        //check rows
        for (int y = 1; y <= 3; y++) {
            counter = 0;
            offsetPosition = 0;
            for (int x = 1; x <= 3; x++) {
                if (board[y][x] == tile) {
                    counter++;
                } else if (board[y][x] == Tile.EMPTY) {
                    offsetPosition = x;
                } else {
                    counter = 0;
                    break;
                }
            }
            if (counter == 2) {
                optimalPosition = new Position(offsetPosition, y);
                break;
            }
        }

        //check cols
        if (optimalPosition == null) {
            for (int x = 1; x <= 3; x++) {
                counter = 0;
                offsetPosition = 0;
                for (int y = 1; y <= 3; y++) {
                    if (board[y][x] == tile) {
                        counter++;
                    } else if (board[y][x] == Tile.EMPTY) {
                        offsetPosition = y;
                    } else {
                        counter = 0;
                        break;
                    }
                }
                if (counter == 2) {
                    optimalPosition = new Position(x, offsetPosition);
                    break;
                }
            }
        }

        //check first diagonal
        if (optimalPosition == null) {
            counter = 0;
            offsetPosition = 0;
            for (int x = 1; x <= 3; x++) {
                if (board[x][x] == tile) {
                    counter++;
                } else if (board[x][x] == Tile.EMPTY) {
                    offsetPosition = x;
                } else {
                    counter = 0;
                    break;
                }
            }

            if (counter == 2) {
                optimalPosition = new Position(offsetPosition, offsetPosition);
            }
        }

        //check second diagonal
        if (optimalPosition == null) {
            counter = 0;
            offsetPosition = 0;
            for (int x = 1; x <= 3; x++) {
                if (board[x][4 - x] == tile) {
                    counter++;
                } else if (board[x][4 - x] == Tile.EMPTY) {
                    offsetPosition = x;
                } else {
                    counter = 0;
                    break;
                }
            }

            if (counter == 2) {
                optimalPosition = new Position(offsetPosition, 4 - offsetPosition);
            }
        }

        if (optimalPosition == null) {
            optimalPosition = randomMove(tile);
        }
        setTileAt(optimalPosition, tile);
        return optimalPosition;
    }
}
