package encoderDecoder;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import model.NotificationDTO;

public class NotificationDTOEncoder implements Encoder.Text<NotificationDTO> {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String encode(NotificationDTO n) throws EncodeException {
		// TODO Auto-generated method stub
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json="";
		try {
			json = ow.writeValueAsString(n);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	
	}

}
