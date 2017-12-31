package com.tree.NukaBot;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class NukaBotUtils {
    static void sendMessage(IChannel channel, String message){
        //
        RequestBuffer.request(() -> {
            try{

                channel.sendMessage(message);
            } catch (DiscordException e){
                System.err.println("Message could not be sent with error: ");
                e.printStackTrace();
            }
        });
    }

    static long sendEmbedWithReactions(IChannel channel, EmbedObject embed) {
        final long[] messageID = new long[1];
        RequestBuffer.request(() -> {
            try{
                messageID[0] = channel.sendMessage(embed).getLongID();
            } catch (DiscordException e){
                System.err.println("File could not be sent with error: ");
                e.printStackTrace();
            }
        }).get();
        System.err.println(messageID[0] + " MSG snt with ID");
        return messageID[0];

    }
}
