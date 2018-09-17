package tw.com.citi.webatm.txn;

import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.TmlPool;

public class TestTxnProcess_01 implements Runnable {

    public void run() {
        try {
            long starTime = 0;

            TxnTerminal txn = null;
            boolean flag = true;

            txn = TmlPool.getTerminal();
            Thread.sleep(1000);
            while (txn == null) {
                txn = TmlPool.getTerminal();
                Thread.sleep(100);
            }

            TxnProcess01 txn01 = new TxnProcess01(txn);
            txn01.setOnUsCard(true);
            // txn01.setICCSEQ("00000499");
            txn01.setIssuerBankCode("021");
            txn01.setIssuerAccount("0000005304884510"); // for log in txnlog
            txn01.setPCode("2590");
            txn01.setSourceAccount("0000005304884510");
            txn01.setICCTxnNo("700");
            txn01.setICCRemark("B5887850083174560D491212060620213F01AD8F8F8F49ABEFADBC9D9875");
            txn01.setTmlCheckCode("00000000");
            txn01.setTmlType("6534");
            txn01.setTAC("7456782B5EDE781A");
            txn01.setMAC("30303030303030303030303030303030");
            txn01.setSessionID("1laskjdfiuo23i9120310923");
            txn01.setClientIP("255.255.255.0");
            txn01.setClientDT("20080707120000");
            txn01.setAuthFlag("0");
            if (txn01.doTxn()) {
                System.out.println(txn01.getActionCode());
                System.out.println(txn01.getRespCode());
                System.out.println(txn01.getSTAN());
                System.out.println(txn01.getAccountBalance());
                System.out.println(txn01.getAvailableBalance());
                System.out.println(txn01.getHostDT());
            } else {
                System.out.println(txn01.getTrnsStatus());
            }

            /*
             * 
             * TxnProcess02 txn02 = new TxnProcess02(txn); txn02.setICCSEQ("00000685"); txn02.setIssuerBankCode("010"); txn02.setIssuerAccount("0012345678901234"); txn02.setPCode("2580");
             * txn02.setSourceAccount("0012345678901234"); txn02.setDestBankCode("010"); txn02.setDestAccount("0090200202000040"); txn02.setTxnAmount("100"); txn02.setICCTxnNo("12345678");
             * txn02.setICCRemark("30303030303030303030303030"); txn02.setTmlCheckCode("12341234"); txn02.setTmlType("6534"); txn02.setTAC("<19=406<4471<>?7");
             * txn02.setMAC("30303030303030303030303030303030"); txn02.setSessionID("1laskjdfiuo23i9120310923"); txn02.setClientIP("255.255.255.0"); txn02.setClientDT("20080707120000"); txn02.doTxn();
             * /* TxnProcess03 txn03 = new TxnProcess03(txn); txn03.setOnUsCard(true); txn03.setTransactionType(TxnProcess03.TransactionType_Payment);
             * 
             * txn03.setICCSEQ("00000685"); txn03.setIssuerBankCode("010"); txn03.setIssuerAccount("0012345678901234"); txn03.setPCode("2521"); txn03.setSourceAccount("0012345678901234");
             * txn03.setDestBankCode("010"); txn03.setDestAccount("0090200202000040"); txn03.setTxnAmount("100"); txn03.setICCTxnNo("12345678");
             * txn03.setICCRemark("303130303032353030363136343730313145313232313133373136313220"); txn03.setTmlCheckCode("12341234"); txn03.setTmlType("6534"); txn03.setTAC("<19=406<4471<>?7");
             * txn03.setMAC("30303030303030303030303030303030"); txn03.setSessionID("1laskjdfiuo23i9120310923"); txn03.setClientIP("255.255.255.0"); txn03.setClientDT("20080707120000"); txn03.doTxn();
             */
            TmlPool.returnTerminal(txn);
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

        TestTxnProcess_01 test = new TestTxnProcess_01();
        for (int i = 0; i < 1; i++) {
            (new Thread(test, "ClientThread-" + i)).start();
        }
    }

}