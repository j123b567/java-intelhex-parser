package cz.jaybee.intelhex.cli;

import cz.jaybee.intelhex.IntelHexException;
import cz.jaybee.intelhex.Parser;
import cz.jaybee.intelhex.listeners.HexWriter;
import cz.jaybee.intelhex.listeners.RangeDetector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HexEditor {

    public static void main(String[] args) throws IOException, IntelHexException {
        String fileIn = "input.hex";
        String fileOut = "output.hex";
        int address = 0x00;
        boolean replace = false;
        byte[] data = new byte[0];

        if (args.length == 0) {
            System.out.println("usage:");
            System.out.println("    hexEditor <hex_in> <hex_out> -replace <start address> <data>");
            System.out.println();
            return;
        }

        if (args.length >= 1) {
            fileIn = args[0];
        }

        if (args.length >= 2) {
            fileOut = args[1];
        }

        if (args.length >= 3 && "-replace".equals(args[2])) {
            replace = true;

            if (args.length >= 4) {
                address = Integer.parseInt(args[3].substring(2),16);
            }

            if (args.length >= 5) {
                data =  fromHexString(args[4].substring(2));
            }
        }


        try (FileInputStream is = new FileInputStream(fileIn)) {
            OutputStream os = Files.newOutputStream(Paths.get(fileOut));
            Parser parser = new Parser(is);

            // 1st iteration - calculate maximum output range
            RangeDetector rangeDetector = new RangeDetector();
            parser.setDataListener(rangeDetector);
            parser.parse();
            is.getChannel().position(0);

            // 2nd iteration - actual write of the output
            HexWriter hexWriter = new HexWriter(rangeDetector.getFullRangeRegion(), os);

            parser.setDataListener(hexWriter);
            parser.parse();

            if(replace) {
                hexWriter.replaceData(address, data);
            }


            //save into file
            hexWriter.save();

        } catch (IOException ex) {
            Logger.getLogger(Hex2bin.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static byte[] fromHexString(final String encoded) throws IntelHexException {
        if ((encoded.length() % 2) != 0)
            throw new IntelHexException("Input string must contain an even number of characters");

        final byte[] result = new byte[encoded.length()/2];
        final char[] enc = encoded.toCharArray();

        for (int i = 0; i < enc.length; i += 2) {
            result[i/2] = (byte) Integer.parseInt(String.valueOf(enc[i]) + enc[i + 1], 16);
        }

        return result;
    }
}
