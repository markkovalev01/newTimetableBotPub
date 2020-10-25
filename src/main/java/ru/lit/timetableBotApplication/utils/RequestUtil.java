package ru.lit.timetableBotApplication.utils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestUtil {
    public static HashMap<String, String> parseRequest(String request) {
        if (Strings.isEmpty(request) || request == null) {
            return null;
        }
        HashMap<String, String> result = new HashMap<>();
        String[] commandAndData = request.split("\\?");
        result.put("command", commandAndData[0]);
        if (commandAndData.length == 1) {
            return result;
        }
        String[] params = commandAndData[1].split("&");
        String[] data;
        for (String param : params) {
            data = param.split("=");
            result.put(data[0], data[1]);
        }
        return result;
    }

    public static HashMap<String, List<String>> parseRequestWithMultipleParam(String request) {
        if (Strings.isEmpty(request) || request == null) {
            return null;
        }
        HashMap<String, List<String>> result = new HashMap<>();
        String[] commandAndData = request.split("\\?");
        if (commandAndData.length >= 2) {
            result = Arrays.stream(commandAndData[1].split("&"))
                .map(str -> {
                    String[] params = str.split("=");
                    AbstractMap.SimpleImmutableEntry<String, String> res;
                    if (params.length >= 2) {
                        res = new AbstractMap.SimpleImmutableEntry<>(params[0], params[1]);
                    } else {
                        res = new AbstractMap.SimpleImmutableEntry<>(params[0], null);
                    }
                    return res;
                })
                .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, HashMap::new,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        }
        result.put("command", Arrays.asList(commandAndData[0]));
        return result;
    }

    public static String paramsToRequest(String key, List<String> params) {
        if (params.isEmpty()) {
            return Strings.EMPTY;
        }
        return params.stream().collect(Collectors.joining("&" + key + "=", key + "=", ""));
    }
}
