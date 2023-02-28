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

import cz.jaybee.intelhex.IntelHexException;
import cz.jaybee.intelhex.Parser;
import cz.jaybee.intelhex.listeners.RangeDetector;
import cz.jaybee.intelhex.listeners.Writer;

import java.io.*;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrintBin {

    static long a = 0;


    public static void main(String[] args) throws IOException, IntelHexException {

        String fileIn = "input.hex";

        if (args.length == 0) {
            System.out.println("Just print binary data from IntelHex file to console");
            System.out.println();
            System.out.println("usage:");
            System.out.println("    PrintBin <file_in>");
            System.out.println();
            return;
        }

        if (args.length >= 1) {
            fileIn = args[0];
        }

        // create input stream of some IntelHex data
        FileInputStream is = new FileInputStream(Paths.get(fileIn).toFile());

        // create IntelHexParserObject
        Parser parser = new Parser(is);

        // 1st iteration - calculate maximum output range
        RangeDetector rangeDetector = new RangeDetector();
        parser.setDataListener(rangeDetector);
        parser.parse();
        is.getChannel().position(0);

        // register parser listener
        parser.setDataListener(new Writer(rangeDetector.getFullRangeRegion()) {


            @Override
            public void write() throws IOException {
                printHumanMessage(buffer, " ");
            }

            @Override
            public void eof() {
                try {
                    write();
                } catch (IOException e) {
                    Logger.getLogger(Hex2bin.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        });

        System.out.println();
        parser.parse();

    }

    public static void printHumanMessage(byte[] inputArray, String determiner) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < inputArray.length; i++) {
            String hex = Integer.toHexString(inputArray[i] & 0xFF).toUpperCase().trim();
            sb.append(new String(new char[2 - hex.length()]).replace('\0', '0'))
                    .append(hex)
                    .append(determiner);

            if((i+1)%16==0) {
                System.out.println(sb);
                sb.delete(0, sb.length());
            }
        }

        if (sb.length() > 0) {
            System.out.println(sb);
        }
    }
}
