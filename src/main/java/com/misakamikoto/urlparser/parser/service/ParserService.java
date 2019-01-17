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
    public static final String FIND_TAG = "<[^>]*>";

    public static final String HTML_TAG_START = "<html";
    public static final String HTML_TAG_END = "/html>";


    public Response getResponseText(String url, String type, int dividingNumber) throws IOException {
        // rest call and get response text
        String responseText = restHttpGet.call(url);

        // is verification html?
        boolean isVerification = this.isVerificationResponseText(responseText);

        return isVerification ?
                this.divideParseResponseTextToResult(this.parseResponseText(responseText, type), dividingNumber) : new Response();
    }

    private boolean isVerificationResponseText(String responseText) {
        Stack<String> verificationStack = new Stack<>();

        // check html
        boolean isCorrectHtmlText = this.isCorrectHtmlText(responseText);

        if(isCorrectHtmlText) {
            Matcher m = this.createMatcher(FIND_TAG, responseText);
            while (m.find()) {
                char[] charArray = m.group().toCharArray();

                if (charArray[0] == '<') {
                    verificationStack.push("open");
                }

                if (charArray[charArray.length - 1] == '>') {
                    verificationStack.pop();
                }
            }
            return verificationStack.isEmpty();

        } else {
            return false;
        }
    }

    public String createCleanText(String responseText, String type) {
        Whitelist whitelist = Whitelist.none();
        return type.equals("html") ? Jsoup.clean(responseText, whitelist) : responseText;
    }

    public Matcher createMatcher(String regex, String responseText) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(responseText);
    }

    public LinkedList createAscList(Matcher m) {
        LinkedList<String> ascList = new LinkedList<>();
        while (m.find()) {
            ascList.add(m.group());
        }
        ascList.sort(new ParserSortComparator());
        return ascList;
    }

    private String[] mergeAscList(LinkedList<String> alphabetAscList, LinkedList<String> numberAscList) {
        String[] arr = new String[alphabetAscList.size() + numberAscList.size()];

        // loop alphabet + number length
        for (int i = 0; i < arr.length; i++) {

            // number setting
            if (i % 2 > 0) {
                if (!numberAscList.isEmpty()) {
                    arr[i] = numberAscList.pop();

                } else {
                    arr[i] = alphabetAscList.pop();
                }

            // alphabet s etting
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

        String str = String.join("", parseResponseText);
        String quotientStr = str.substring(0, (quotient * dividingNumber));
        String remainderStr = str.substring(quotient * dividingNumber, parseResponseText.length);

        Response response = new Response();
        response.setResult(true);
        response.setQuotient(quotientStr);
        response.setRemainder(remainderStr);

        return response;
    }

    private boolean isCorrectHtmlText(String responseText) {
        if(responseText.indexOf(HTML_TAG_START) == -1 || responseText.indexOf(HTML_TAG_END) == -1) {
            return false;

        } else {
            return true;
        }
    }
}
