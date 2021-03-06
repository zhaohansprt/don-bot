package com.wire.bots.don;

import com.wire.bots.don.commands.Command;
import com.wire.bots.don.commands.DefaultCommand;
import com.wire.bots.don.db.Manager;
import com.wire.bots.don.db.User;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.models.TextMessage;

import java.util.concurrent.ConcurrentHashMap;

public class Don {
    private final Manager db;
    private final ConcurrentHashMap<String, Command> commands = new ConcurrentHashMap<>();

    public Don(DonConfig config) {
        db = new Manager(config.cryptoDir + "/don.db");
    }

    boolean onNewBot(String userId, String nameName) throws Exception {
        User user = db.getUser(userId);
        if (user == null)
            db.insertUser(userId, nameName);
        return true;
    }

    public void onMessage(WireClient client, TextMessage msg) throws Exception {
        String bot = client.getId();
        Command command = commands.computeIfAbsent(bot, k -> new DefaultCommand(client, msg.getUserId(), db));

        commands.put(bot, command.onMessage(client, msg.getText()));
    }
}
