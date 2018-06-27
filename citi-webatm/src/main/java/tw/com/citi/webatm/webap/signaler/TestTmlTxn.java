package tw.com.citi.webatm.webap.signaler;

import tw.com.citi.elf.TxnP4501;
import tw.com.citi.webatm.txn.TxnTerminal;

public class TestTmlTxn implements Runnable {
    public static TerminalPool TmlPool;

    public void run() {
        try {
            long starTime = 0;

            TxnTerminal txn = null;
            boolean flag = true;
            while (txn == null) {
                txn = TmlPool.getTerminal();
                // Thread.sleep(200);
                if (flag) {
                    flag = false;
                    // starTime = System.currentTimeMillis();
                }
            }
            // System.out.println("��O�ɶ�-1:"+(System.currentTimeMillis() - starTime));

            // starTime = System.currentTimeMillis();

            String MsgSeqNo = txn.getTml_SeqNo(txn.getTmlID());
            // System.out.println("��O�ɶ�-2:"+(System.currentTimeMillis() - starTime));
            // starTime = System.currentTimeMillis();
            TxnP4501 txn4501 = new TxnP4501(txn.getTmlID(), MsgSeqNo);
            txn.doTxnP4501(txn4501);
            TmlPool.returnTerminal(txn);
            // System.out.println("��O�ɶ�-3:"+(System.currentTimeMillis() - starTime));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * @param args
     *            Creation date:(2008/8/8 �U�� 03:14:19)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        TestTmlTxn test = new TestTmlTxn();
        for (int i = 0; i < 10; i++) {
            (new Thread(test, "ClientThread-" + i)).start();
        }
    }

}
