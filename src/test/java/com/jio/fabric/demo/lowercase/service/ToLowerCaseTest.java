package com.jio.fabric.demo.lowercase.service;

import com.ril.fabric.demo.LowerCaseText;
import com.ril.fabric.demo.PlainText;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToLowerCaseTest {

    @Test
    public void testChangeInCase() {
        ToLowerCase toLowerCase = new ToLowerCase();
        PlainText plainText = PlainText.newBuilder().setText("Success is How High You Bounce When You Hit Bottom.").build();
        LowerCaseText lowerCaseText = toLowerCase.changeCase(plainText);
        Assertions.assertEquals("success is how high you bounce when you hit bottom.", lowerCaseText.getText());
    }

}