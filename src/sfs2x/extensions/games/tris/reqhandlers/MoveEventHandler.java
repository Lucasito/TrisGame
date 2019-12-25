package sfs2x.extensions.games.tris.reqhandlers;

import com.smartfoxserver.v2.annotations.Instantiation;
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSRuntimeException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import sfs2x.extensions.games.tris.TrisRoomExtension;
import sfs2x.extensions.games.tris.data.Position;
import sfs2x.extensions.games.tris.enums.Tile;
import sfs2x.extensions.games.tris.TrisGameBoard;
import sfs2x.extensions.games.tris.enums.GameState;

@Instantiation(InstantiationMode.SINGLE_INSTANCE)
public class MoveEventHandler extends BaseClientRequestHandler {
    private static final String CMD_WIN = "win";
    private static final String CMD_TIE = "tie";
    private static final String CMD_MOVE = "move";
    private static final int USER_ID = 1;
    private static final int AI_ID = 2;

    @Override
    public void handleClientRequest(User user, ISFSObject params) {
        // Check params
        if (!params.containsKey("x") || !params.containsKey("y"))
            throw new SFSRuntimeException("Invalid request, one mandatory param is missing. Required 'x' and 'y'");

        TrisRoomExtension gameExt = (TrisRoomExtension) getParentExtension();
        TrisGameBoard board = gameExt.getGameBoard();

        Position movePosition = new Position(params.getInt("x"), params.getInt("y"));

        if (board.getTileAt(movePosition) == Tile.EMPTY) {
            // Set game board tile
            board.setTileAt(movePosition, Tile.GREEN);

            SendMove(movePosition, USER_ID);

            // Increse move count and check game status
            gameExt.increaseMoveCount();

            // Check if game is over
            checkBoardState(gameExt);
            if (gameExt.isGameStarted()) {
                Position aiMove = gameExt.aiMove();

                SendMove(aiMove, AI_ID);

                gameExt.increaseMoveCount();
                checkBoardState(gameExt);
            }
        }
    }

    private void SendMove(Position position, int player) {
        // Send response
        ISFSObject respObj = new SFSObject();
        respObj.putInt("x", position.x);
        respObj.putInt("y", position.y);
        respObj.putInt("t", player);

        send(CMD_MOVE, respObj, ((TrisRoomExtension) getParentExtension()).getGameRoom().getUserList());
    }

    private void checkBoardState(TrisRoomExtension gameExt) {
        GameState state = gameExt.getGameBoard().getGameStatus(gameExt.getMoveCount());

        if (state == GameState.END_WITH_WINNER) {
            int winnerId = gameExt.getGameBoard().getWinner();

            gameExt.trace("Winner found: ", winnerId);

            // Stop game
            gameExt.stopGame();

            // Send update
            ISFSObject respObj = new SFSObject();
            respObj.putInt("w", winnerId);
            gameExt.send(CMD_WIN, respObj, gameExt.getGameRoom().getUserList());

        } else if (state == GameState.END_WITH_TIE) {
            gameExt.trace("TIE!");

            // Stop game
            gameExt.stopGame();

            // Send update
            ISFSObject respObj = new SFSObject();
            gameExt.send(CMD_TIE, respObj, gameExt.getGameRoom().getUserList());
        }
    }
}
