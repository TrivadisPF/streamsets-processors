package com.trivadis.streamsets.pipeline.stage.util;

import java.util.Locale;

public class StringUtil {

    // Our method
    static public String toCamelCase(String s, boolean useUpperCamelCase){

        // create a StringBuilder to create our output string
        StringBuilder sb = new StringBuilder();

        // determine when the next capital letter will be
        Boolean nextCapital = useUpperCamelCase;

        // lowercase as a starting case
        s = s.toLowerCase();

        // loop through the string
        for (int i=0; i<s.length(); i++) {

            // if the current character is a letter
            if (Character.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i))) {

                // get the current character
                char tmp = s.charAt(i);

                // make it a capital if required
                if (nextCapital)
                    tmp = Character.toUpperCase(tmp);

                // add it to our output string
                sb.append(tmp);

                // make sure the next character isn't a capital
                nextCapital = false;

            } else {
                // otherwise the next letter should be a capital
                nextCapital = true;
            }
        }

        // return our output string
        return sb.toString();
    }
}
