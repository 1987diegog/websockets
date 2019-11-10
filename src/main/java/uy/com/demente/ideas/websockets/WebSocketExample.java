package uy.com.demente.ideas.websockets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/currentDateTime")
public class WebSocketExample {

	private Map<Integer, Session> mapSessions = new HashMap<>();

	private final String TAG = "[WEB_SOCKET] - ";
	private final int TIME_SLEEP = 5000;
	private static Integer sessionNumber = 0;

	@OnOpen
	public void open(Session session) {

		System.out.println(TAG + "Session number => " + sessionNumber
				+ " opened: " + getCurrentDateTime());

		mapSessions.put(sessionNumber, session);
		sessionNumber++;
		System.out.println(TAG + "Session number => " + sessionNumber
				+ " added: " + getCurrentDateTime());

		Thread threadWebSocket = new Thread(new Runnable() {
			@Override
			public void run() {
				sendMessage(session);
			}
		});

		threadWebSocket.start();
	}

	/**
	 * @param session
	 */
	public void sendMessage(Session session) {
		try {
			while (true) {

				Integer currentSessionNumber = getKeyForSession(session);
				System.out.println(TAG + "Session number => "
						+ currentSessionNumber + " send message...");

				session.getBasicRemote().sendText(getCurrentDateTime());
				Thread.sleep(TIME_SLEEP);
			}
		} catch (IOException e) {
			System.out.println(TAG + "Session number => " + sessionNumber
					+ " error: " + getCurrentDateTime());

			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println(TAG + "Session number => " + sessionNumber
					+ " error: " + getCurrentDateTime());

			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	public <K, V> Stream<K> keys(Map<K, V> map, V value) {
		return map.entrySet().stream()
				.filter(entry -> value.equals(entry.getValue()))
				.map(Map.Entry::getKey);
	}

	public Integer getKeyForSession(Session session) {

		Stream<Integer> keyStream = keys(mapSessions, session);
		Integer currentSessionNumber = keyStream.findFirst().get();
		return currentSessionNumber;
	}

	private String getCurrentDateTime() {

		LocalDateTime localDateTime = LocalDateTime.now();
		String dateTime = localDateTime
				.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		return "The local date time is: " + dateTime;
	}

	@OnMessage
	public String handleMessage(String mensaje) {
		return "Session number => " + sessionNumber + " Welcome!!! "
				+ getCurrentDateTime();
	}

	@OnClose
	public void close(Session session) {

		System.out.println(TAG + "Session number => " + sessionNumber
				+ " closed: " + getCurrentDateTime());

		Integer currentSessionNumber = getKeyForSession(session);
		mapSessions.remove(currentSessionNumber);

		System.out.println(TAG + "Session number => " + sessionNumber
				+ " removed: " + getCurrentDateTime());
	}

	@OnError
	public void onError(Throwable e) {

		System.out.println(TAG + "Session number => " + sessionNumber
				+ " error: " + getCurrentDateTime());

		System.out.println(e.getMessage());
		e.printStackTrace();
	}
}
