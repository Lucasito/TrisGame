package sfs2x.extensions.games.tris;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import sfs2x.extensions.games.tris.data.Position;
import sfs2x.extensions.games.tris.enums.Tile;
import sfs2x.extensions.games.tris.evthandlers.room.UserJoinRoomEventHandler;
import sfs2x.extensions.games.tris.reqhandlers.MoveEventHandler;
import sfs2x.extensions.games.tris.reqhandlers.RestartEventHandler;

public class TrisRoomExtension extends SFSExtension {
    private static final String REQ_MOVE = "move";
    private static final String REQ_RESTART = "restart";
    private static final String RES_START_GAME = "start";

    private TrisGameBoard gameBoard;
    private volatile boolean gameStarted;
    private int moveCount;

    @Override
    public void init() {
        trace("Tris game Extension started");

        moveCount = 0;
        gameBoard = new TrisGameBoard();

        addRequestHandler(REQ_MOVE, MoveEventHandler.class);
        addRequestHandler(REQ_RESTART, RestartEventHandler.class);

        addEventHandler(SFSEventType.USER_JOIN_ROOM, UserJoinRoomEventHandler.class);

    }

    @Override
    public void destroy() {
        super.destroy();
        trace("Tris game destroyed!");
    }

    public TrisGameBoard getGameBoard() {
        return gameBoard;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void increaseMoveCount() {
        ++moveCount;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        if (gameStarted)
            throw new IllegalStateException("Game is already started!");

        gameStarted = true;
        gameBoard.reset();

        trace("Start game. Users count: " + getParentRoom().getUserList().size());
        send(RES_START_GAME, new SFSObject(), getParentRoom().getUserList());
    }

    public void stopGame() {
        gameStarted = false;
        moveCount = 0;
    }

    public Room getGameRoom() {
        return this.getParentRoom();
    }

    public Position aiMove() {
        return gameBoard.optimalMove(Tile.RED);
    }
}
