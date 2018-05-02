package encoderDecoder;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import model.Message;
import model.NotificationDTO;

public class NotificationDTODecoder implements Decoder.Text<NotificationDTO> {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NotificationDTO decode(String text) throws DecodeException {
		// TODO Auto-generated method stub
		return new NotificationDTO(text);
	}

	@Override
	public boolean willDecode(String arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
