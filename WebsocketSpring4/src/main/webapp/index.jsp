<!DOCTYPE html>

<html>
<head>
<title>Echo Chamber</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width">
</head>
<body>
	<div>
		<input type="text" id="messageinput" />
	</div>
	<div>
		<button type="button" onclick="openSocket();">Open</button>
		<button type="button" onclick="send();">Send</button>
		<button type="button" onclick="closeSocket();">Close</button>
	</div>
	<!-- Server responses get written here -->
	<div id="messages"></div>

	<!-- Script to utilise the WebSocket -->
	<script type="text/javascript">
		var webSocket;
		var messages = document.getElementById("messages");

		function openSocket() {
			if (webSocket !== undefined
					&& webSocket.readyState !== WebSocket.CLOSED) {
				writeResponse("WebSocket is already opened.");
				return;
			}
			webSocket = new WebSocket("ws://localhost:8080/WebsocketSpring4/wsinit");

			webSocket.onopen = function(event) {
				if (event.data === undefined) return;
				writeResponse(event.data);
			};

			webSocket.onmessage = function(event) {
				writeResponse(event.data);
			};

			webSocket.onclose = function(event) {
				writeResponse("Connection closed");
			};
		}
		function send() {
			var text = document.getElementById("messageinput").value;
			webSocket.send(text);
		}
		function closeSocket() {
			webSocket.close();
		}
		function writeResponse(text) {
			messages.innerHTML += "<br/>" + text;
		}
	</script>
</body>
</html>
