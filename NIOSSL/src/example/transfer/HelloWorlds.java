package example.transfer;

public class HelloWorlds {
	public static String getHelloWorlds(){
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE html>\n");
		sb.append("\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("<title>Echo Chamber</title>\n");
		sb.append("<meta charset=\"UTF-8\">\n");
		sb.append("<meta name=\"viewport\" content=\"width=device-width\">\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("	<div>\n");
		sb.append("		<input type=\"text\" id=\"messageinput\" />\n");
		sb.append("	</div>\n");
		sb.append("	<div>\n");
		sb.append("		<button type=\"button\" onclick=\"openSocket();\">Open</button>\n");
		sb.append("		<button type=\"button\" onclick=\"send();\">Send</button>\n");
		sb.append("		<button type=\"button\" onclick=\"closeSocket();\">Close</button>\n");
		sb.append("	</div>\n");
		sb.append("	<!-- Server responses get written here -->\n");
		sb.append("	<div id=\"messages\"></div>\n");
		sb.append("\n");
		sb.append("	<!-- Script to utilise the WebSocket -->\n");
		sb.append("	<script type=\"text/javascript\">\n");
		sb.append("		var webSocket;\n");
		sb.append("		var messages = document.getElementById(\"messages\");\n");
		sb.append("\n");
		sb.append("		function d2h(d) {\n");
		sb.append("		    return d.toString(16);\n");
		sb.append("		}\n");
		sb.append("		function h2d (h) {\n");
		sb.append("			return parseInt(h, 16);\n");
		sb.append("		}\n");
		sb.append("		function stringToHex (tmp) {\n");
		sb.append("			var str = '',\n");
		sb.append("				i = 0,\n");
		sb.append("				tmp_len = tmp.length,\n");
		sb.append("				c;\n");
		sb.append("		 \n");
		sb.append("			for (; i < tmp_len; i += 1) {\n");
		sb.append("				c = tmp.charCodeAt(i);\n");
		sb.append("				str += d2h(c) + ' ';\n");
		sb.append("			}\n");
		sb.append("			return str;\n");
		sb.append("		}\n");
		sb.append("		function hexToString (tmp) {\n");
		sb.append("			var arr = tmp.split(' '),\n");
		sb.append("				str = '',\n");
		sb.append("				i = 0,\n");
		sb.append("				arr_len = arr.length,\n");
		sb.append("				c;\n");
		sb.append("		 \n");
		sb.append("			for (; i < arr_len; i += 1) {\n");
		sb.append("				c = String.fromCharCode( h2d( arr[i] ) );\n");
		sb.append("				str += c;\n");
		sb.append("			}\n");
		sb.append("		 \n");
		sb.append("			return str;\n");
		sb.append("		}\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("		function openSocket() {\n");
		sb.append("			if (webSocket !== undefined	&& webSocket.readyState !== WebSocket.CLOSED) {\n");
		sb.append("				writeResponse(\"WebSocket is already opened.\");\n");
		sb.append("				return;\n");
		sb.append("			}\n");
		sb.append("			console.log('------------ start open socket() -----------------------');\n");
		sb.append("			webSocket = new WebSocket(\"wss://localhost:9090/websocket\");\n");
		sb.append("			\n");
		sb.append("			webSocket.binaryType = 'arraybuffer';\n");
		sb.append("			\n");
		sb.append("			console.log('------------ end open socket() -----------------------');\n");
		sb.append("\n");
		sb.append("			webSocket.onopen = function(event) {\n");
		sb.append("				writeResponse(\"Socket Connected\");\n");
		sb.append("				if (event.data === undefined) return;\n");
		sb.append("				writeResponse(event.data);\n");
		sb.append("			};\n");
		sb.append("\n");
		sb.append("			webSocket.onmessage = function(event) {\n");
		sb.append("				console.log('----StringToHex Start-------------------------------');\n");
		sb.append("				console.log( stringToHex(event.data));\n");
		sb.append("				console.log('----StringToHex End-------------------------------');\n");
		sb.append("\n");
		sb.append("				//@TODO ���⿡ Typed Array �� �߰��Ѵ�.\n");
		sb.append("				var buffer = new ArrayBuffer(20);\n");
		sb.append("				var uint8View = new Uint8Array(buffer);\n");
		sb.append("				console.log(uint8View.length); // 20\n");
		sb.append("				var buffer = new ArrayBuffer(20);\n");
		sb.append("				var uint32View = new Uint32Array(buffer);\n");
		sb.append("				console.log(uint32View.length); // 5\n");
		sb.append("\n");
		sb.append("				writeResponse(event.data);\n");
		sb.append("			};\n");
		sb.append("\n");
		sb.append("			webSocket.onclose = function(event) {\n");
		sb.append("				writeResponse(\"Socket Disconnected\");\n");
		sb.append("			};\n");
		sb.append("		}\n");
		sb.append("		function send() {\n");
		sb.append("			var text = document.getElementById(\"messageinput\").value;\n");
		sb.append("			webSocket.send(text);\n");
		sb.append("		}\n");
		sb.append("		function closeSocket() {\n");
		sb.append("			webSocket.close();\n");
		sb.append("		}\n");
		sb.append("		function writeResponse(text) {\n");
		sb.append("			messages.innerHTML += \"<br/>\" + text; \n");
		sb.append("		}\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("		window.onload=function(){\n");
		sb.append("			console.log('------onload()---------------------');\n");
		sb.append("		}\n");
		sb.append("\n");
		sb.append("	</script>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");


		return sb.toString();
	}
}
