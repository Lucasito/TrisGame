package sfs2x.extensions.games.tris.evthandlers.zone;

import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class UserJoinZoneEventHandler extends BaseServerEventHandler {
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException {
        User user = (User) event.getParameter(SFSEventParam.USER);
        Zone zone = (Zone) event.getParameter(SFSEventParam.ZONE);

        createTrisRoom(user);
    }

    private void createTrisRoom(User user) {

        CreateRoomSettings cfg = new CreateRoomSettings();

        DateFormat dateFormat = new SimpleDateFormat("dd HH:mm:ss");
        cfg.setName("Battle#" + dateFormat.format(Calendar.getInstance().getTime()) + " " + new Random().nextInt(999));
        cfg.setMaxUsers(1);
        cfg.setDynamic(true);
        cfg.setGame(true);
        cfg.setHidden(false);
        cfg.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY_AND_CREATOR_IS_GONE);

        cfg.setExtension(new CreateRoomSettings.RoomExtensionSettings("Tris", "sfs2x.extensions.games.tris.TrisRoomExtension"));

        Room newRoom = null;
        try {
            newRoom = getApi().createRoom(getParentExtension().getParentZone(), cfg, null);
        } catch (SFSCreateRoomException e) {
            trace(ExtensionLogLevel.ERROR, e.getMessage());
        }
        try {
            getApi().joinRoom(user, newRoom);
        } catch (SFSJoinRoomException e) {
            trace(ExtensionLogLevel.ERROR, e.getMessage());
        }
        trace("Created room " + newRoom.getName());
    }

}