package encoderDecoder;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import model.Message;

public class MessageDecoder implements Decoder.Text<Message> {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Message decode(String text) throws DecodeException {
		// TODO Auto-generated method stub
		return new Message(text);
	}

	@Override
	public boolean willDecode(String arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
