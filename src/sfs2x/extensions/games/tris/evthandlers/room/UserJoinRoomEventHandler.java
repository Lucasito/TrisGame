package sfs2x.extensions.games.tris.evthandlers.room;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import sfs2x.extensions.games.tris.TrisRoomExtension;

public class UserJoinRoomEventHandler extends BaseServerEventHandler {

    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException {
        User user = (User) event.getParameter(SFSEventParam.USER);
        Room room = (Room) event.getParameter(SFSEventParam.ROOM);

        TrisRoomExtension gameExt = (TrisRoomExtension) getParentExtension();

        gameExt.startGame();
    }

}
