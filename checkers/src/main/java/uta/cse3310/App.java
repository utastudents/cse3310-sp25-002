// This is example code provided to CSE3310 Spring 2025
//
// You are free to use as is, or change any of the code provided

// Please comply with the licensing requirements for the
// open source packages being used.

// This code is based upon, and derived from the this repository
//            https:/thub.com/TooTallNate/Java-WebSocket/tree/master/src/main/example

// http server include is a GPL licensed package from
//            http://www.freeutils.net/source/jlhttp/

/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package uta.cse3310;


import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.JsonObject;

import uta.cse3310.PageManager.PageManager;
import uta.cse3310.PageManager.UserEventReply;
import uta.cse3310.PageManager.UserEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.util.Vector;
import java.time.Instant;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Hashtable;

public class App extends WebSocketServer {

  Hashtable<WebSocket, Integer> con2id = new Hashtable<>();
  Hashtable<Integer, WebSocket> id2con = new Hashtable<>();
  Hashtable<String, WebSocket> playerName2con = new Hashtable<>();

  int nextClientId = 2; // start the index at one becuase 0 and 1 are being used by GameManger as Bot I&II
  PageManager PM;

  private final ConcurrentLinkedQueue<UserEventReply> outboundMessageQueue = new ConcurrentLinkedQueue<>();

  private final Gson gson = new Gson();

  private Timer queueTimer;

  class id {
    int clientId;
    String type = "assign_id";
  }

  public App(int port) {
    super(new InetSocketAddress(port));
    initializePageManager();
  }

  public App(InetSocketAddress address) {
    super(address);
    initializePageManager();
  }

  public App(int port, Draft_6455 draft) {
    super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    initializePageManager();
  }


  private void initializePageManager() {
      if (this.PM == null) {
          this.PM = new PageManager(this);
          System.out.println("PageManager initialized with App reference.");
      }
  }


  public void queueMessage(UserEventReply reply) {
      if (reply != null && reply.recipients != null && !reply.recipients.isEmpty()) {
        if(reply.status != null && reply.status.type != null) {
          System.out.println("[DEBUG App] Queuing message type " + reply.status.type + " for recipients: " + reply.recipients);
        } else {
          System.out.println("[DEBUG App] Queuing message with null status or type for recipients: " + reply.recipients);
        }
        outboundMessageQueue.offer(reply);
      } else {
        System.out.println("[WARN App] Attempted to queue invalid or empty reply.");
      }
  }



  private void processOutboundQueue() {
    UserEventReply reply;
    while ((reply = outboundMessageQueue.poll()) != null) {
      System.out.println("[DEBUG App] Processing queued message type " + (reply.status != null ? reply.status.type : "N/A")); // Optional: Verbose logging
      for (Integer id : reply.recipients) {
        WebSocket destination = id2con.get(id);
        if (destination != null && destination.isOpen()) {
          if (reply.status != null) {
            reply.status.clientId = id;
            String jsonString = gson.toJson(reply.status);
            destination.send(jsonString);
            System.out.println("sending(queued) " + jsonString + " to " + id);
          } else {
              System.err.println("Attempted to send queued message with null status to client ID: " + id);
          }
        } else {
          System.err.println("Could not send queued message to client ID: " + id + " (Connection not found or not open)");
        }
      }
    }
  }


  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    System.out.println("A new connection has been opened");
    int newClientId = nextClientId++;
    System.out.println("Connection opened: " + conn.getRemoteSocketAddress() + ", assigning Client ID: " + newClientId);

    // save off the ID and conn ptr so they can be easily fetched
    con2id.put(conn, newClientId);
    id2con.put(newClientId, conn);

    id ID = new id();
    ID.clientId = newClientId;
    String jsonString = gson.toJson(ID);
    System.out.println("sending assigned ID: " + jsonString + " to " + newClientId);
    conn.send(jsonString);
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    Integer closedId = con2id.get(conn);;
    System.out.println("Connection closed: " + conn.getRemoteSocketAddress() + ", ID: " + (closedId != null ? closedId : "N/A") + ", Code: " + code + ", Reason: " + reason + ", Remote: " + remote);
    removePlayerMappings(closedId, conn, null);
  }


  private void removePlayerMappings(Integer clientId, WebSocket conn, String playerName) {
      if (clientId != null) {
          id2con.remove(clientId);
          if (playerName == null && conn != null) {
            String foundName = null;
            for (String pName : playerName2con.keySet()) {
                if (playerName2con.get(pName) == conn) {
                    foundName = pName;
                    break;
                }
            }
            playerName = foundName;
          }
          if (playerName != null) {
              playerName2con.remove(playerName);
              System.out.println("Removed playerName mapping for: " + playerName);
          }
      }
      if (conn != null) {
          con2id.remove(conn);
      }
      System.out.println("Cleaned up mappings for Client ID: " + (clientId != null ? clientId : "N/A"));
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    UserEventReply Reply = null;
    UserEvent U = null;
    Gson gson = new GsonBuilder().create();
    try {

      U = gson.fromJson(message, UserEvent.class);

      // where did this message come from?
      U.id = con2id.get(conn);


      if ("join".equals(U.type) && U.playerName != null && !U.playerName.isEmpty()) {
            System.out.println("Processing join request for player: " + U.playerName + " (ID: " + U.id + ")");
            WebSocket existingConn = playerName2con.get(U.playerName);

            if (existingConn != null && existingConn != conn && existingConn.isOpen()) {
                System.out.println("Player " + U.playerName + " already connected. Closing old connection: " + existingConn.getRemoteSocketAddress());
                JsonObject kickMsg = new JsonObject();
                kickMsg.addProperty("type", "force_disconnect");
                kickMsg.addProperty("msg", "You have connected from another location. This session is closing.");
                existingConn.send(gson.toJson(kickMsg));
                existingConn.close(1008, "Duplicate login");

                Integer oldId = con2id.get(existingConn);
                removePlayerMappings(oldId, existingConn, U.playerName);
            }
        }


      // now, need to call a function to get this processed.
      Reply = PM.ProcessInput(U);

      if ("join".equals(U.type) && Reply != null && Reply.status != null && "Success".equals(Reply.status.Status)) {
          if(U.playerName != null && !U.playerName.isEmpty()){
              playerName2con.put(U.playerName, conn); // Map name to the *current* connection
              System.out.println("Mapped playerName " + U.playerName + " to connection ID " + U.id);
          }
      }

      // Send it to all that need it
      if (Reply != null && Reply.recipients != null && !Reply.recipients.isEmpty()) {
          for (Integer recipientId : Reply.recipients) {
              WebSocket destination = id2con.get(recipientId);
              if (destination != null && destination.isOpen()) {
                  if (Reply.status != null) {
                      Reply.status.clientId = recipientId;
                      String jsonString = gson.toJson(Reply.status);
                      destination.send(jsonString);
                      System.out.println("sending " + jsonString + " to ID " + recipientId);
                  } else {
                    System.err.println("Reply has null status for recipient ID: " + recipientId);
                  }
              } else {
                System.err.println("Could not send reply to client ID: " + recipientId + " (Connection not found or not open)");
              }
          }
        }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[ERROR App] Failed to process incoming message: " + message);
    }
  }


  @Override
  public void onMessage(WebSocket conn, ByteBuffer message) {
    System.out.println(conn + ": " + message);
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    ex.printStackTrace();
    if (conn != null) {
      // some errors like port binding failed may not be assignable to a specific
      // websocket
      Integer errorId = con2id.get(conn);
      removePlayerMappings(errorId, conn, null);
      if (errorId != null) {
        id2con.remove(errorId);
        con2id.remove(conn);
      }
    }
  }

  @Override
  public void onStart() {
    setConnectionLostTimeout(50);
    if (queueTimer == null) {
        queueTimer = new Timer(true); // Use daemon thread
        queueTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
              try {
                processOutboundQueue();
              } catch (Exception e) {
                System.err.println("Error processing outbound queue: " + e.getMessage());
                e.printStackTrace();
              }
            }
        }, 50, 100);
        System.out.println("Outbound message queue processor started.");
    }
  }

  @Override
  public void stop(int timeout) throws InterruptedException {
      if (queueTimer != null) {
          queueTimer.cancel();
          queueTimer = null;
      }
      for (WebSocket conn : con2id.keySet()) {
        if (conn != null && conn.isOpen()) {
          conn.close(1001, "Server shutdown");
        }
      }
      super.stop(timeout);
  }


  public static void main(String[] args) {

    String HttpPort = System.getenv("HTTP_PORT");
    int port = 9080;
    if (HttpPort != null) {
      port = Integer.valueOf(HttpPort);
    }

    // Set up the http server

    HttpServer H = new HttpServer(port, "./src/main/webapp/");
    H.start();
    System.out.println("http Server started on port: " + port);

    // create and start the websocket server

    port = 9180;
    String WSPort = System.getenv("WEBSOCKET_PORT");
    if (WSPort != null) {
      port = Integer.valueOf(WSPort);
    }

    App A = new App(port);
    A.setReuseAddr(true);
    A.start();
    System.out.println("websocket Server started on port: " + port);

    // PageManager pm;
    // pm = new PageManager();
    System.out.println("Hello World!");
  }
}
