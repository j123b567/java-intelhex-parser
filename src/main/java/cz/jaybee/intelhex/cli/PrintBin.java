/**
 * Copyright (c) 2015, Jan Breuer All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package cz.jaybee.intelhex.cli;

import cz.jaybee.intelhex.DataListener;
import cz.jaybee.intelhex.IntelHexException;
import cz.jaybee.intelhex.Parser;
import cz.jaybee.intelhex.Region;
import cz.jaybee.intelhex.listeners.BinWriter;
import cz.jaybee.intelhex.listeners.RangeDetector;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to demonstrate usage of Intel HEX parser
 *
 * @author Jan Breuer
 */
public class PrintBin {

    static long a = 0;


    public static void main(String[] args) throws IOException, IntelHexException {

        // create input stream of some IntelHex data
        InputStream is = new FileInputStream("/home/zerog/work/telesoft/master_slave/nrf_16.hex");

        // create IntelHexParserObject
        Parser ihp = new Parser(is);

        // register parser listener
        ihp.setDataListener(new DataListener() {

            @Override
            public void data(long address, byte[] data) {
                System.out.println(address-a+" - "+getHumanMessage(data, " "));
                a = address;
            }

            @Override
            public void eof() {
                // do some action
            }
        });
        ihp.parse();

    }

    public static String getHumanMessage(byte[] inputArray, String determiner) {

        if(inputArray==null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < inputArray.length; i++) {
            String hex = Integer.toHexString(inputArray[i] & 0xFF).toUpperCase().trim();
            sb.append(new String(new char[2 - hex.length()]).replace('\0', '0'))
                    .append(hex)
                    .append(determiner);
        }

        if (sb.length() > 0) {
            return (sb.subSequence(0, sb.length() - determiner.length())).toString();
        }

        return sb.append(" (").append(inputArray.length).append(")").toString();
    }
}
