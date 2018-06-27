package tw.com.citi.webatm.webap.signaler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.citi.utils.Misc;

public class NewDataStuffer {
    private static byte[] data = new byte[255];
    private static int iTestCaseCount = 0;
    private static int MSGFUNC = 0;
    private static int Txn4801Count = 0;
    private static boolean isResetTerminal = false;
    private static String TmlID;
    private static String MSGSEQ;
    private static int len;
    private static String ConnMsg;
    private static long startTime = 0;

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < data.length; i++)
            data[i] = (byte) i;
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.socket().bind(new InetSocketAddress(9000));
        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {

            selector.select(200);
            Set readyKeys = selector.selectedKeys();
            Iterator iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        if (client.isConnectionPending()) {
                            client.finishConnect();
                        }
                        ByteBuffer source = ByteBuffer.wrap(data);
                        SelectionKey key2 = client.register(selector, SelectionKey.OP_READ);
                        key2.attach(source);
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        System.out.println("<---Write data TestCaseCount=" + iTestCaseCount);
                        // ByteBuffer output = (ByteBuffer) key.attachment( );
                        String sendMsg = "80005300010053000D001300000030303030303021A6FFFFFFFF0005003C796500381EF0000679651F42000621A61F450008FFFFFFFF1F410005301F47000930303030301F4000123230303830373037313230303030";
                        switch (MSGFUNC) {
                        case 143:
                            sendMsg = "8F";
                            sendMsg = "";
                            key = client.register(selector, SelectionKey.OP_READ);
                            break;
                        case 31077:
                            // sendMsg =
                            // "80005300010053000D001364000030303030303121A6FFFFFFFF0005003C796500381EF0000679651F42000621A61F450008FFFFFFFF1F410005301F47000930303030301F4000123230303830373037313230303030";
                            sendMsg = "80005300010053000D0013001000303030303030" + TmlID + MSGSEQ + "0005003C796500381EF0000679651F420006" + TmlID
                                    + "1F450008FFFFFFFF1F410005301F47000930303030301F400012" + new String(Misc.bin2Hex(Misc.genDate(Misc.DT_DATETIME).getBytes()));
                            key = client.register(selector, SelectionKey.OP_READ);
                            // Thread.sleep(200);
                            break;
                        case 4101:
                            if (isResetTerminal) {
                                // sendMsg = "80004800010048000D0013640000303030303031000100000009000500321005002D1EF0000610051F42000600011F450008000000091F410005301C84000930303030301C860007323030";
                                sendMsg = "80004800010048000D0013001000303030303030" + TmlID + MSGSEQ + "000500311005002D1EF0000610051F420006" + TmlID + "1F450008" + MSGSEQ
                                        + "1F410005301C84000930303030301C860007303030";
                                // sendMsg =
                                // "80004800010048000D0013001000303030303030"+TmlID+MSGSEQ+"000500311005002D1EF0000610051F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301C84000930303030311C860007303030";
                                // //respCode = 00001
                                isResetTerminal = false;
                                key = client.register(selector, SelectionKey.OP_READ);
                            } else {
                                // sendMsg = "80004800010048000D001364000030303030303121A600000001000500321005002D1EF0000610051F42000621A61F450008000000011F410005301C84000930303030301C860007323030";
                                sendMsg = "80004800010048000D0013001000303030303030" + TmlID + MSGSEQ + "000500311005002D1EF0000610051F420006" + TmlID + "1F450008" + MSGSEQ
                                        + "1F410005301C84000930303030301C860007303030";
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                MSGFUNC = 4801;
                                MSGSEQ = Misc.padZero(Long.toHexString(Integer.parseInt(MSGSEQ, 16) + 1), 8).toUpperCase();
                                // Thread.sleep(200);
                            }
                            break;
                        case 4105:
                            // sendMsg = "80004800010048000D001364000030303030303121A600000001000500321009002D1EF0000610091F42000621A61F450008000000011F410005301C84000930303030301C860007323030";
                            sendMsg = "80004800010048000D0013001000303030303030" + TmlID + MSGSEQ + "000500321009002D1EF0000610091F420006" + TmlID + "1F450008" + MSGSEQ
                                    + "1F410005301C84000930303030301C860007303030";
                            key = client.register(selector, SelectionKey.OP_WRITE);
                            MSGFUNC = 4801;
                            MSGSEQ = Misc.padZero(Long.toHexString(Integer.parseInt(MSGSEQ, 16) + 1), 8).toUpperCase();
                            Txn4801Count = 0;
                            break;

                        case 4113:
                            // sendMsg = "80004800010048000D0013640000303030303031000100000009000500321005002D1EF0000610051F42000600011F450008000000091F410005301C84000930303030301C860007323030";
                            sendMsg = "80004800010048000D0013001000303030303030" + TmlID + MSGSEQ + "000500311011002D1EF0000610111F420006" + TmlID + "1F450008" + MSGSEQ
                                    + "1F410005301C84000930303030301C860007303030";
                            // sendMsg =
                            // "80004800010048000D0013001000303030303030"+TmlID+MSGSEQ+"000500311005002D1EF0000610051F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301C84000930303030311C860007303030";
                            // //respCode = 00001
                            isResetTerminal = false;
                            key = client.register(selector, SelectionKey.OP_READ);
                            break;
                        case 4404:
                            // sendMsg =
                            // "80008200010082000D001364000030303030303121A6000000010005003C119500671EF0000611951F42000621A61F450008000000011F410005301C84000930303030301C8600073030301D52001D1C2F00075457441BC2000931303030301BC0000931303030301D06001232303038303730313132303030301C42000B30303030303030";
                            String hostDateTime04 = new String(Misc.bin2Hex(Misc.genDate(Misc.DT_DATETIME).getBytes()));
                            // 成功case
                            sendMsg = "8000A0000100A0000D0013001000303030303030" + TmlID + MSGSEQ + "0005007A113400671EF0000611341F420006" + TmlID + "1F450008" + MSGSEQ
                                    + "1F410005301C84000930303030301C8600073030301D52001D1C2F00075457441BC2000931303030301BC0000931303030301D060012" + hostDateTime04
                                    + "1B91000B303030313730301CD300053146DC000E313233343536373839301C42000B31303030303637";
                            // 失敗case
                            // sendMsg =
                            // "80004800010048000D0013001000303030303030"+TmlID+MSGSEQ+"000500311134002D1EF0000611341F410005301F450008"+MSGSEQ+"1F420006"+TmlID+"1C84000932303530341C860007303939";
                            key = client.register(selector, SelectionKey.OP_READ);
                            break;
                        case 4411:
                            // sendMsg =
                            // "80008200010082000D001364000030303030303021A6000000010005003C119500671EF0000611951F42000621A61F450008000000011F410005301C84000930303030301C8600073030301D52001D1C2F00075457441BC2000931303030301BC0000931303030301D06001232303038303730313132303030301C42000B30303030303030";
                            String hostDateTime11 = new String(Misc.bin2Hex(Misc.genDate(Misc.DT_DATETIME).getBytes()));
                            sendMsg = "80009500010095000D0013001000303030303030" + TmlID + MSGSEQ + "0005007E113B007A1EF00006113B1F420006" + TmlID + "1F450008" + MSGSEQ
                                    + "1F410005301C84000930303030301C8600073030301D52001D1C2F00075457441BC2000931303030301BC0000931303030301D060012" + hostDateTime11
                                    + "1B91000B303030303030301CD300053046DC000E31323334353637383930";

                            // ActionCode=141 RespCode=20048
                            // sendMsg =
                            // "80009500010095000D0013001000303030303030"+TmlID+MSGSEQ+"0005007E113B007A1EF00006113B1F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301C84000932303034381C8600073134311D52001D1C2F00075457441BC2000931303030301BC0000931303030301D060012"+hostDateTime11+"1B91000B303030303030301CD300053046DC000E31323334353637383930";

                            // ActionCode = 04
                            // sendMsg =
                            // "80009400010094000D0013001000303030303030"+TmlID+MSGSEQ+"0005007D113B00791EF00006113B1F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301C84000930303030301C86000630341D52001D1C2F00075457441BC2000931303030301BC0000931303030301D060012"+hostDateTime11+"1B91000B303030303030301CD30005301E57000E31323334353637383930";

                            key = client.register(selector, SelectionKey.OP_READ);
                            break;
                        case 4412:
                            // sendMsg =
                            // "80008200010082000D001364000030303030303021A6000000010005003C119500671EF0000611951F42000621A61F450008000000011F410005301C84000930303030301C8600073030301D52001D1C2F00075457441BC2000931303030301BC0000931303030301D06001232303038303730313132303030301C42000B30303030303030";
                            sendMsg = "80004800010048000D0013001000303030303030" + TmlID + MSGSEQ + "0005007E113C007A1EF00006113C1F420006" + TmlID + "1F450008" + MSGSEQ
                                    + "1F410005301C84000930303030301C860007303030";

                            // 回覆沖正失敗
                            // sendMsg =
                            // "80004800010048000D0013001000303030303030"+TmlID+MSGSEQ+"0005007E113C007A1EF00006113C1F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301C84000930303030301C860007303031";

                            key = client.register(selector, SelectionKey.OP_READ);
                            break;
                        case 4501:
                            // sendMsg =
                            // "80008200010082000D001364000030303030303021A6000000010005003C119500671EF0000611951F42000621A61F450008000000011F410005301C84000930303030301C8600073030301D52001D1C2F00075457441BC2000931303030301BC0000931303030301D06001232303038303730313132303030301C42000B30303030303030";
                            String hostDateTime = new String(Misc.bin2Hex(Misc.genDate(Misc.DT_DATETIME).getBytes()));
                            System.out.println(hostDateTime);
                            sendMsg = "80008200010082000D0013001000303030303030" + TmlID + MSGSEQ + "0005003C119500671EF0000611951F420006" + TmlID + "1F450008" + MSGSEQ
                                    + "1F410005301C84000930303030301C8600073030301D52001D1C2F00075457441BC2000931303030301BC0000931303030301D06001232303038303730313132303030301C42000B31323334353637";
                            // case 1
                            sendMsg = "80008200010082000D0013001000303030303030" + TmlID + MSGSEQ + "0005003C119500671EF0000611951F420006" + TmlID + "1F450008" + MSGSEQ
                                    + "1F410005301C84000930303030301C8600073030301D52001D1C2F00075457441BC2000931303030301BC0000931303030301D060012" + hostDateTime + "1C42000B31323334353637";
                            // sendMsg =
                            // "80008000010080000D0013001000303030303030"+TmlID+MSGSEQ+"0005003C119500671EF0000611951F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301C84000930303030301C8600073030301D52001D1C2F00075457441BC20008303030301BC00008303030301D060012"+hostDateTime+"1C42000B31323334353637";
                            // case 2
                            // sendMsg =
                            // "80004800010048000D0013001000303030303030"+TmlID+MSGSEQ+"0005003C119500671EF0000611951F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301C84000930303030301C860007303030";
                            // case 3
                            // sendMsg =
                            // "80004700010047000D0013001000303030303030"+TmlID+MSGSEQ+"0005003C119500671EF0000611951F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301C84000932303032301C8600063939";
                            // case 4 RespCode = 17210
                            // sendMsg =
                            // "80008200010082000D0013001000303030303030"+TmlID+MSGSEQ+"0005003C119500671EF0000611951F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301C84000931373231301C8600073030301D52001D1C2F00075457441BC2000931303030301BC0000931303030301D060012"+hostDateTime+"1C42000B31323334353637";
                            key = client.register(selector, SelectionKey.OP_READ);
                            break;
                        case 4801:
                            if (Txn4801Count == 0) {
                                // sendMsg =
                                // "80005000010050000D001364000030303030303021A6000000010005003912C100351EF0000612C11F42000621A61F450008000000011F410005301EDD00063034277700123230303830373037313230303030";
                                // sendMsg = "80003E0001003E000D0013000000303030303030"+TmlID+MSGSEQ+"0005002712C100351EF0000612C11F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301EDD00063034";
                                sendMsg = "80007800010072000D0013000000303030303030" + TmlID + MSGSEQ + "0005003F12C100351EF0000612C11F420006" + TmlID + "1F450008" + MSGSEQ
                                        + "1F410005301EDD00063034277700343843413634444539433142313233413738434136344445394331423132334137384341363444453943314231323341373A9C00063031";
                                Txn4801Count++;
                                // Thread.sleep(200);
                            } else {
                                // MSGSEQ = Misc.padZero(Integer.parseInt(MSGSEQ,16)+1,8);
                                MSGSEQ = Misc.padZero(Long.toHexString(Integer.parseInt(MSGSEQ, 16) + 1), 8).toUpperCase();
                                // sendMsg =
                                // "80005A0001005A000D0013000000303030303030"+TmlID+MSGSEQ+"0005003912C100351EF0000612C11F420006"+TmlID+"1F450008"+MSGSEQ+"1F410005301EDD000630332777001C8CA64DE9C1B123A78CA64DE9C1B123A78CA64DE9C1B123A7";
                                sendMsg = "80007800010072000D0013000000303030303030" + TmlID + MSGSEQ + "0005003F12C100351EF0000612C11F420006" + TmlID + "1F450008" + MSGSEQ
                                        + "1F410005301EDD00063033277700343843413634444539433142313233413738434136344445394331423132334137384341363444453943314231323341373A9C00063031";
                                Txn4801Count++;
                                // Thread.sleep(200);
                            }
                            key = client.register(selector, SelectionKey.OP_READ);
                            break;
                        case 4802:
                            // MSGSEQ = Misc.padZero(Integer.parseInt(MSGSEQ,16)+1,8);
                            MSGSEQ = Misc.padZero(Long.toHexString(Integer.parseInt(MSGSEQ, 16) + 1), 8).toUpperCase();
                            sendMsg = "80004400010044000D0013000000303030303030" + TmlID + MSGSEQ + "0005002D12C200351EF0000612C21F420006" + TmlID + "1F450008" + MSGSEQ
                                    + "1F410005301EDC000C3030303030303030";
                            key = client.register(selector, SelectionKey.OP_READ);
                            /*
                             * String sendMsg1 = sendMsg.substring(0,20); String sendMsg2 = sendMsg.substring(20);
                             * 
                             * ByteBuffer output = (ByteBuffer) ByteBuffer.allocate(sendMsg1.length()/2); output.put(Misc.hex2Bin(sendMsg1.getBytes()));
                             * 
                             * output.flip(); System.out.println("write length="+client.write(output));
                             * 
                             * output = (ByteBuffer) ByteBuffer.allocate(sendMsg2.length()/2); output.put(Misc.hex2Bin(sendMsg2.getBytes()));
                             * 
                             * output.flip(); System.out.println("write length="+client.write(output)); sendMsg = "";
                             */
                            // Thread.sleep(200);
                            break;
                        default:
                            break;
                        }

                        // return Connect Msg
                        if (len == 23) {
                            sendMsg = ConnMsg;
                            key = client.register(selector, SelectionKey.OP_READ);
                            // Thread.sleep(200);

                            String sendMsg1 = sendMsg.substring(0, 4);
                            String sendMsg2 = sendMsg.substring(4, 6);
                            String sendMsg3 = sendMsg.substring(6, 16);
                            String sendMsg4 = sendMsg.substring(16);

                            ByteBuffer output = (ByteBuffer) ByteBuffer.allocate(sendMsg1.length() / 2);
                            output.put(Misc.hex2Bin(sendMsg1.getBytes()));

                            output.flip();
                            System.out.println("write length=" + client.write(output));

                            Thread.sleep(1000);

                            output = (ByteBuffer) ByteBuffer.allocate(sendMsg2.length() / 2);
                            output.put(Misc.hex2Bin(sendMsg2.getBytes()));

                            output.flip();
                            System.out.println("write length=" + client.write(output));

                            Thread.sleep(200);
                            output = (ByteBuffer) ByteBuffer.allocate(sendMsg3.length() / 2);
                            output.put(Misc.hex2Bin(sendMsg3.getBytes()));

                            output.flip();
                            System.out.println("write length=" + client.write(output));

                            Thread.sleep(1200);

                            output = (ByteBuffer) ByteBuffer.allocate(sendMsg4.length() / 2);
                            output.put(Misc.hex2Bin(sendMsg4.getBytes()));

                            output.flip();
                            System.out.println("write length=" + client.write(output));
                            break;
                        }

                        /*
                         * if (iTestCaseCount==1) sendMsg =
                         * "80005500010053000D001364000030303030303021A6FFFFFFFF0005003C796500381EF0000679651F42000621A61F450008FFFFFFFF1F410005301F47000930303030301F4000123230303830373037313230303030";
                         * else if(iTestCaseCount==3){ sendMsg =
                         * "80004A00010048000D001364000030303030303021A600000001000500321005002D1EF0000610051F42000621A61F450008000000011F410005301C84000930303030301C860007323030"; SelectionKey key2 =
                         * client.register(selector, SelectionKey.OP_WRITE); }else if(iTestCaseCount==4){ sendMsg =
                         * "80005200010050000D001364000030303030303021A6000000010005003912C100351EF0000612C11F42000621A61F450008000000011F410005301EDD00063034277700123230303830373037313230303030";
                         * }else if(iTestCaseCount==6){ sendMsg =
                         * "80005200010050000D001364000030303030303021A6000000020005003912C100351EF0000612C11F42000621A61F450008000000021F410005301EDD00063033277700123230303830373037313230303030";
                         * }else if(iTestCaseCount==8){ sendMsg =
                         * "80004600010044000D001364000030303030303021A6000000030005003912C200351EF0000612C21F42000621A61F450008000000031F410005301EDC000C3030303030303030"; try {
                         * //Thread.sleep(10000); } catch (Exception e) { // TODO: handle exception } }
                         */
                        ByteBuffer output = null;
                        if (sendMsg.length() != 0) {
                            output = (ByteBuffer) ByteBuffer.allocate(sendMsg.length() / 2);
                            output.put(Misc.hex2Bin(sendMsg.getBytes()));
                        }

                        if (output != null) {
                            // if (!output.hasRemaining( )) {
                            // output.rewind( );
                            // }
                            output.flip();
                            System.out.println("write length=" + client.write(output));
                            System.out.println("write data  = " + sendMsg + "\n");
                        } else {
                            output = (ByteBuffer) ByteBuffer.allocate(0);
                            output.flip();
                            client.write(output);
                            System.out.println(output);
                        }
                        /*
                         * if(iTestCaseCount!=3){ SelectionKey key2 = client.register(selector, SelectionKey.OP_READ); } iTestCaseCount++;
                         */ /*
                            * if(iTestCaseCount==5){ iTestCaseCount = 0; }
                            */
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        int iReadLen;
                        System.out.println("--->Read data TestCaseCount=" + iTestCaseCount);

                        ByteBuffer bbReceive = (ByteBuffer) ByteBuffer.allocate(3);
                        System.out.println("read length = " + (iReadLen = client.read(bbReceive)));
                        if (iReadLen == -1) {
                            key.cancel();
                            client.close();
                            selector.selectNow();

                            break;
                        }
                        String sTemp = new String(Misc.bin2Hex(bbReceive.array()));
                        System.out.println("TCP Format code = 0x" + sTemp.substring(0, 2));
                        if (sTemp.substring(0, 2).equals("80")) {
                            len = Integer.parseInt(sTemp.substring(2), 16);
                            bbReceive = (ByteBuffer) ByteBuffer.allocate(len);
                            System.out.println("read length = " + client.read(bbReceive));
                            String Body = new String(Misc.bin2Hex(bbReceive.array()));
                            System.out.println("receive data = " + Body);
                            if (len == 23) { // this is connect msg
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                ConnMsg = "800017" + Body.substring(0, 18) + "1101" + Body.substring(22);
                                System.out.println("RetConnMsg data = " + ConnMsg);
                                break;
                            }
                            MSGFUNC = Integer.parseInt(Body.substring(54, 58), 16);
                            System.out.println("MSGFunc data = " + MSGFUNC + "\n");
                            System.out.println(TmlID = Body.substring(82, 86));
                            System.out.println(MSGSEQ = Body.substring(94, 102));
                            switch (MSGFUNC) {
                            case 31077:
                                System.out.println(MSGSEQ = Body.substring(38, 46));
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                break;
                            case 4101:
                                System.out.println(Body.substring(34, 38));
                                // if (!Body.substring(34,38).equals("2260")){ //2260為8800指定換KEY TML
                                if (!(Body.substring(34, 38).equals("2260") || Body.substring(34, 38).equals("1770"))) { // 0x2260,0x1770為8800,6000指定換KEY TML
                                    isResetTerminal = true;
                                    if (MSGSEQ.equals("00000000")) {
                                        isResetTerminal = true;
                                    }
                                } /*
                                   * if (MSGSEQ.equals("00000000")){ isResetTerminal=true; }
                                   */
                                // System.out.println(Misc.padZero(Integer.parseInt(MSGSEQ,16)+1,8));
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                break;
                            case 4105:
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                break;
                            case 4113:
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                break;
                            case 4404:
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                break;
                            case 4411:
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                break;
                            case 4412:
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                break;
                            case 4501:
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                break;
                            case 4801:
                                key = client.register(selector, SelectionKey.OP_WRITE);
                                if (Txn4801Count == 2) {
                                    Txn4801Count = 0;
                                    MSGFUNC = 4802;
                                }
                            case 4802:
                                // key = client.register(selector, SelectionKey.OP_WRITE);
                                break;
                            }
                        } else if (sTemp.substring(0, 2).equals("8F")) {
                            key = client.register(selector, SelectionKey.OP_WRITE);
                            // key.channel().close( );
                            System.out.println("KeepAlive");
                            MSGFUNC = 143;
                        }

                        // ByteBuffer output = (ByteBuffer) ByteBuffer.allocate(200);
                        // client.read(output);
                        /*
                         * try { Thread.sleep(1000); } catch (Exception e) { // TODO: handle exception }
                         */

                        // System.out.println(new String (Misc.bin2Hex(output.array()))+"\n");
                        /*
                         * if(iTestCaseCount!=9){ SelectionKey key2 = client.register(selector, SelectionKey.OP_WRITE); } iTestCaseCount++; if(iTestCaseCount==10){ iTestCaseCount = 0; }
                         */

                    } else if (key.isConnectable()) {
                        System.out.println("connectable");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}