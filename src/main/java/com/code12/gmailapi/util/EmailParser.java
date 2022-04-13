package com.code12.gmailapi.util;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;

public class EmailParser {

    private final Message message;

    private final String id;
    private final String treadId;
    private final ArrayList<String> labelIds = new ArrayList<>();
    private final String snippet;
    private final BigInteger historyId;
    private final Long internalData;
    private final MessagePart payLoad;
    private final int sizeEstimate;

    private String subject = null;
    private String bodyMessage = null;
    private String date;
    private String to;
    private String from;

    public EmailParser(Message message) {
        this.message = message;

        id = message.getId();
        treadId = message.getThreadId();
        labelIds.addAll(message.getLabelIds());
        snippet = message.getSnippet();
        historyId = message.getHistoryId();
        internalData = message.getInternalDate();
        payLoad = message.getPayload();
        sizeEstimate = message.getSizeEstimate();
    }

    /**
     *
     * @return The subject of the Message
     */

    public String getSubject() {
        if (subject == null) {
            this.subject = calculateHeader("subject");
        }
        return subject;
    }

    /**
     *
     * @return The date the message was received
     */

    public String getDate(){
        if(date == null){
            this.date = calculateHeader("date");
        }
        return date;
    }

    /**
     *
     * @return The email address of the recipient of the message
     */

    public String getTo(){
        if(to == null){
            this.to = calculateHeader("to");
        }
        return to;
    }

    /**
     *
     * @return The sender of the message
     */

    public String getFrom(){
        if(from == null){
            this.from = calculateHeader("from");
        }
        return from;
    }

    /**
     *
     * @return The main text in the message
     */

    public String getBodyMessage() {
        if (bodyMessage == null) {
            this.bodyMessage = calculateBodyMessage();
        }
        return bodyMessage;
    }

    private String calculateBodyMessage(){
        String jsonData = getPayLoad().toString();
        JSONObject payload = new JSONObject(jsonData);

        String mimeType = getPayLoad().getMimeType();

        switch (mimeType){
            case ("text/plain"):
                bodyMessage = decode(getBodyData(payload));
                break;

            case("multipart/alternative"):
                JSONArray parts = payload.getJSONArray("parts");

                int n = parts.length();
                for(int i = 0; i < n; i++){
                    JSONObject partPayload = parts.getJSONObject(i);
                    String partMimeType = partPayload.getString("mimeType");
                    if(partMimeType.equalsIgnoreCase("text/plain")){
                        bodyMessage = decode(getBodyData(partPayload));
                        break;
                    }
                }
                break;

            default:
                bodyMessage = "ERROR WHILE LOADING MESSAGE! Message mimeType was not text/plain or multipart/alternative";
                break;
        }
        return bodyMessage;
    }

    private String getBodyData(JSONObject messagePart){
        JSONObject body = messagePart.getJSONObject("body");
        return body.getString("data");
    }

    private String decode(String encodedMessage){
        Base64.Decoder decoder = Base64.getMimeDecoder();

        byte[] decodedBytes = decoder.decode(encodedMessage);
        String decodedString = new String(decodedBytes);
        return decodedString;
    }

    private String calculateHeader(String value){
        String key = "name";

        String jsonData = getPayLoad().getHeaders().toString();
        JSONArray jsonArray = new JSONArray(jsonData);
        String result = null;

        int n = jsonArray.length();
        for(int i = 0; i < n; i++){
            JSONObject jsonObjectHeader = jsonArray.getJSONObject(i);

            if(jsonObjectHeader.getString(key).equalsIgnoreCase(value)){
                result = jsonObjectHeader.getString("value");
            }
        }
        return result;
    }

    public Message getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public String getTreadId() {
        return treadId;
    }

    public ArrayList<String> getLabelIds() {
        return labelIds;
    }

    public String getSnippet() {
        return snippet;
    }

    public BigInteger getHistoryId() {
        return historyId;
    }

    public Long getInternalData() {
        return internalData;
    }

    public MessagePart getPayLoad() {
        return payLoad;
    }

    public int getSizeEstimate() {
        return sizeEstimate;
    }
}
