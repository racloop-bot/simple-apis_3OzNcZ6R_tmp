package com.jio.fabric.demo.lowercase.service;

import com.ril.fabric.demo.LowerCaseText;
import com.ril.fabric.demo.PlainText;
import com.ril.fabric.executor.annotations.JfApi;
import org.springframework.stereotype.Service;

@Service
public class ToLowerCase {

    @JfApi
    public LowerCaseText changeCase(PlainText plainText) {
        String text = plainText.getText();
        String lowercase = text.toLowerCase();
        LowerCaseText lowerCaseText = LowerCaseText.newBuilder().setText(lowercase).build();
        return lowerCaseText;
    }

}
