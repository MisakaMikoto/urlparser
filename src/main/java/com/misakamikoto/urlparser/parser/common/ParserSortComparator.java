package com.misakamikoto.urlparser.parser.common;

import java.util.Comparator;

public class ParserSortComparator implements Comparator<String> {
    public int compare(String s1, String s2) {
        if (s1.toLowerCase().equals(s2.toLowerCase())) {
            return s1.compareTo(s2);

        } else {
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }
}
