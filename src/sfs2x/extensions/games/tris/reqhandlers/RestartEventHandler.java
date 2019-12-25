package sfs2x.extensions.games.tris.reqhandlers;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import sfs2x.extensions.games.tris.TrisRoomExtension;

public class RestartEventHandler extends BaseClientRequestHandler {
    @Override
    public void handleClientRequest(User user, ISFSObject params) {
        TrisRoomExtension gameExt = (TrisRoomExtension) getParentExtension();

        gameExt.startGame();
    }
}
