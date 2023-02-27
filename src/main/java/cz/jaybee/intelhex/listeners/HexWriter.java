package cz.jaybee.intelhex.listeners;

import cz.jaybee.intelhex.Record;
import cz.jaybee.intelhex.RecordType;
import cz.jaybee.intelhex.Region;

import java.io.*;

public class HexWriter extends Writer {

    public HexWriter(Region outputRegion, OutputStream destination) {
        super(outputRegion, destination);
    }

    public void appendPrefix(byte[] data) {
        byte[] newBuffer = new byte[buffer.length + data.length];

        //copy whole buffer to bigger
        System.arraycopy(buffer, 0, newBuffer, data.length, newBuffer.length);
        buffer = newBuffer;

        replaceData(0, data);
    }

    public void replaceData(int address, byte[] data) {
        //TODO test address
        System.arraycopy(data, 0, buffer, address, data.length);
    }

    @Override
    void write() throws IOException {

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
        //sb.append("L>");

        get(sb, intTo1BytesArr(record.length));
        //sb.append(" A>");

        get(sb, intTo2BytesArr(record.address));
        //sb.append(" T>");

        get(sb, intTo1BytesArr(record.type.toInt()));
        //sb.append(" D>");
        get(sb, record.data, record.length);

        //sb.append(" C>");
        get(sb, intTo1BytesArr(record.checksum));

        sb.append("\r\n"); //old windows line separator

        System.out.print(sb.toString().toUpperCase());

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
}
