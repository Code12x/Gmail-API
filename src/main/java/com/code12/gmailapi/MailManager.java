package com.code12.gmailapi;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.code12.gmailapi.GmailAPI.service;

public class MailManager {

    public List<Message> getMail(String label) throws IOException {
        Gmail.Users.Messages.List request = service.users().messages().list("me").setLabelIds(Arrays.asList(label));

        List<Message> list = new LinkedList<>();
        ListMessagesResponse response = null;

        do {
            response = request.execute();
            list.addAll(response.getMessages());
            request.setPageToken(response.getNextPageToken());
        } while (request.getPageToken() != null && request.getPageToken().length() > 0);

        return list;
    }

    public static Message getFullyQualifiedMessage(String id) throws IOException {
        return service.users().messages().get("me", id).setFormat("FULL").execute();
    }
}
