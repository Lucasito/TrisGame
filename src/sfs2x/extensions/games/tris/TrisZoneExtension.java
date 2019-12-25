package sfs2x.extensions.games.tris;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;
import sfs2x.extensions.games.tris.evthandlers.zone.UserJoinZoneEventHandler;

public class TrisZoneExtension extends SFSExtension {

    @Override
    public void init() {
        trace("Tris Zone Extension started");
        addEventHandler(SFSEventType.USER_JOIN_ZONE, UserJoinZoneEventHandler.class);

    }

    @Override
    public void destroy() {
        super.destroy();
        trace("Tris Zone destroyed!");
    }

}
