package cz.jaybee.intelhex.listeners;

import cz.jaybee.intelhex.IntelHexException;
import cz.jaybee.intelhex.Record;
import cz.jaybee.intelhex.RecordType;
import cz.jaybee.intelhex.Region;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HexWriter extends Writer {

    public HexWriter(Region outputRegion, OutputStream destination) {
        super(outputRegion, destination);
    }

    public void appendPrefix(byte[] data) throws IntelHexException {
        byte[] newBuffer = new byte[buffer.length + data.length];

        //copy whole buffer to bigger
        System.arraycopy(buffer, 0, newBuffer, data.length, buffer.length);
        buffer = newBuffer;

        replaceData(0, data);
    }

    public void replaceData(int address, byte[] data) throws IntelHexException {

        if(address < 0 ) {
            throw new IntelHexException("Replace error. Invalid address. Address=(" + address + ") < 0");
        }

        if(address + data.length > buffer.length) {
            throw new IntelHexException("Replace error. Invalid address. Address+data.length=(" + address+data.length + ") > buffer length=(" + buffer.length+")");
        }

        System.arraycopy(data, 0, buffer, address, data.length);
    }

    @Override
    public void eof() {
        //do nothing when eof is detected
    }

    @Override
    public void write() throws IOException {

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(destination))) {

            //cuts data to 16 bytes blocks
            //if 0xFF are redundant, skip it
            int i = 0;
            while (i < buffer.length) {
                byte[] recordData = new byte[16]; //line block of data

                int len = 0;
                boolean detected0xFF = false;
                boolean first0xFF = true;

                int skipIndex = 0;
                for (int j = 0; j < 16; j++, skipIndex++) {

                    if (buffer[i + j] != (byte) 0xff) {

                        if (detected0xFF) {
                            //if 0xFF is detected
                            //and then NON 0xFF is detected
                            //go out of for and print line
                            break;
                        }

                        recordData[j] = buffer[i + j];
                        len++;

                    } else {


                        if (first0xFF) {
                            first0xFF = false;
                            if (buffer[i + j + 1] != (byte) 0xff) {
                                //if first 0xFF is detected and after it
                                //is normal non 0xFF, then 0xFF doesn't skip
                                recordData[j] = buffer[i + j];
                                len++;
                                continue;
                            }
                        }

                        detected0xFF = true;
                    }
                }

                Record record = new Record();
                record.length = len;
                record.address = i;
                record.data = recordData;
                record.type = RecordType.DATA;
                sumCalculator(record);

                if (len > 0) {
                    i += len;
                    bw.write(getLine(record));
                    continue;
                }

                //if len == 0, meaning only 0xFF lines
                //so skip it
                i += skipIndex;
            }


            //EOF record
            Record record = new Record();
            record.length = 0;
            record.address = 0x0000;
            record.data = new byte[0];
            record.type = RecordType.EOF;
            sumCalculator(record);

            bw.write(getLine(record));
        }
    }

    public void sumCalculator(Record record) {
        int sum = 0;

        //data
        for (int i = 0; i < record.length; i++) {
            sum += record.data[i] & 0xff;
        }

        //address
        sum += record.address & 0xff;
        sum += (record.address >> 8) & 0xff;

        //type
        sum += record.type.toInt() & 0xff;

        //length
        sum += record.length;

        //sum
        record.checksum = (byte) (~(sum & 0xff) + 1);
    }

    private byte[] intTo1BytesArr(int data) {
        return new byte[]{
                (byte) ((data) & 0xff),
        };
    }

    private byte[] intTo2BytesArr(int data) {
        return new byte[]{
                (byte) ((data >> 8) & 0xff),
                (byte) ((data) & 0xff),
        };
    }


    public String getLine(Record record) {

        StringBuilder sb = new StringBuilder(":");
        get(sb, intTo1BytesArr(record.length));
        get(sb, intTo2BytesArr(record.address));
        get(sb, intTo1BytesArr(record.type.toInt()));
        get(sb, record.data, record.length);
        get(sb, intTo1BytesArr(record.checksum));

        sb.append("\r\n"); //old windows line separator
         return sb.toString().toUpperCase();
    }

    private void get(StringBuilder sb, byte[] arr) {
        get(sb, arr, arr.length);
    }

    private void get(StringBuilder sb, byte[] arr, int len) {
        for (int i = 0; i < len; i++) {
            byte b = arr[i];
            String hex = Integer.toHexString(b & 0xFF).trim();
            sb.append(new String(new char[2 - hex.length()]).replace('\0', '0'))
                    .append(hex);
        }
    }

    public void save() {
        try {
            write();
        } catch (IOException ex) {
            Logger.getLogger(BinWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
