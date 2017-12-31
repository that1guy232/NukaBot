package com.tree.NukaBot;

import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.Discord4JHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class NukaBotMain {
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("Please enter the bots token as the first argument e.g java -jar thisjar.jar tokenhere");
            return;
        }

        NukaCommands nukaCommands = new NukaCommands();

        IDiscordClient client = new ClientBuilder().withToken(args[0]).setMaxReconnectAttempts(Integer.MAX_VALUE).online("!help").build();
        client.getDispatcher().registerListener(nukaCommands);
        CommandHandler commandHandler = new Discord4JHandler(client);
        commandHandler.registerCommand(nukaCommands);










        client.login();
    }


}
