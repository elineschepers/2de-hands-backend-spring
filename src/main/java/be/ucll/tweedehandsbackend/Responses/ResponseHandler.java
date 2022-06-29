/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 02/03/2022 19:49
 */

package be.ucll.tweedehandsbackend.Responses;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ResponseHandler {

    private static ModelMapper modelMapper;

    @Autowired
    private ResponseHandler(ModelMapper modelMapper) {
        ResponseHandler.modelMapper = modelMapper;
    }

    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<>();

        // if message is not null or not empty, add it to the map
        if (message != null && !message.isEmpty()) {
            map.put("message", message);
        }

        map.put("data", responseObj);

        return new ResponseEntity<>(map, status);
    }

    public static ResponseEntity<Object> generateResponse(String message, Object responseObj) {
        return generateResponse(message, HttpStatus.OK, responseObj);
    }

    public static ResponseEntity<Object> generateResponse(Object responseObj) {
        return generateResponse(null, HttpStatus.OK, responseObj);
    }

    public static <T> ResponseEntity<Object> generateResponse(List<?> responseObj, Class<T> type) {
        if (responseObj == null) {
            return generateResponse(null);
        }

        List<T> dtos = responseObj
                .stream()
                .map(obj -> ResponseHandler.modelMapper.map(obj, type))
                .collect(Collectors.toList());

        return generateResponse(null, dtos);
    }

    public static <S, T> ResponseEntity<Object> generateResponse(S responseObj, Class<T> type) {
        return generateResponse(null, ResponseHandler.modelMapper.map(responseObj, type));
    }
}
