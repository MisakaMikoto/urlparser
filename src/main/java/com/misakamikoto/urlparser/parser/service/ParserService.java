package com.misakamikoto.urlparser.parser.service;

import com.misakamikoto.urlparser.parser.common.ParserSortComparator;
import com.misakamikoto.urlparser.parser.common.RestHttpGet;
import com.misakamikoto.urlparser.parser.dto.Response;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParserService {

    @Autowired
    RestHttpGet restHttpGet;

    public static final String A_TO_Z = "[A-Za-z]";
    public static final String ZERO_TO_NINE = "[0-9]";

    public Response getResponseText(String url, String type, int dividingNumber) throws IOException {
        //http://localhost:8081/cc
        //https://comic.naver.com/webtoon/detail.nhn?titleId=183559&no=418&weekday=mon
//        Document doc = Jsoup.connect("http://localhost:8081/cc").get().normalise();


        // rest call and get response text
        String responseText = restHttpGet.call(url);
        boolean isVerification = this.isVerificationResponseText(responseText);

        return isVerification ?
                this.divideParseResponseTextToResult(this.parseResponseText(responseText, type), dividingNumber) : new Response();
    }

    private boolean isVerificationResponseText(String responseText) {
        Stack<String> verificationStack = new Stack<>();
        char[] charArray = responseText.toCharArray();

        for(int i = 0; i < charArray.length; i++) {
            if(charArray[i] == '<') {
                verificationStack.push("v");
            }
            if(charArray[i] == '>') {
                verificationStack.pop();
            }
        }
        return verificationStack.isEmpty();
    }

    private String createCleanText(String responseText, String type) {
        Whitelist whitelist = Whitelist.none();
        return type.equals("html") ? Jsoup.clean(responseText, whitelist) : responseText;
    }

    private Matcher createMatcher(String regex, String responseText) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(responseText);
    }

    private LinkedList createAscList(Matcher m) {
        LinkedList<String> ascList = new LinkedList<>();
        while (m.find()) {
            ascList.add(m.group());
        }
        ascList.sort(new ParserSortComparator());
        return ascList;
    }

    private String[] mergeAscList(LinkedList<String> alphabetAscList, LinkedList<String> numberAscList) {
        String[] arr = new String[alphabetAscList.size() + numberAscList.size()];

        for (int i = 0; i < arr.length; i++) {
            if (i % 2 > 0) {
                if (!numberAscList.isEmpty()) {
                    arr[i] = numberAscList.pop();

                } else {
                    arr[i] = alphabetAscList.pop();
                }

            } else {
                if (!alphabetAscList.isEmpty()) {
                    arr[i] = alphabetAscList.pop();

                } else {
                    if (!numberAscList.isEmpty()) {
                        arr[i] = numberAscList.pop();

                    } else {
                        arr[i] = alphabetAscList.pop();
                    }
                }
            }
        }
        return arr;
    }

    private String[] parseResponseText(String responseText, String type) {
        String cleanText = this.createCleanText(responseText, type);

        // create alphabet asc list
        LinkedList<String> alphabetAscList = this.createAscList(this.createMatcher(A_TO_Z, cleanText));

        // create number asc list
        LinkedList<String> numberAscList = this.createAscList(this.createMatcher(ZERO_TO_NINE, cleanText));
        String[] array = this.mergeAscList(alphabetAscList, numberAscList);

        return array;
    }

    private Response divideParseResponseTextToResult(String[] parseResponseText, int dividingNumber) {
        int quotient = parseResponseText.length / dividingNumber;
        String quotientStr = Arrays.toString(parseResponseText).substring(0, (quotient * dividingNumber));
        String remainderStr = Arrays.toString(parseResponseText).substring(quotient * dividingNumber, parseResponseText.length);

        Response response = new Response();
        response.setResult(true);
        response.setQuotient(quotientStr);
        response.setRemainder(remainderStr);

        return response;
    }
}
