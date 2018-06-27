package tw.com.citi.webatm.txn;

import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.TmlPool;

public class TestTxnProcess_E63 implements Runnable {

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
            /*
             * TxnProcess01 txn01 = new TxnProcess01(txn); txn01.setOnUsCard(true); //txn01.setICCSEQ("00000499"); txn01.setIssuerBankCode("021"); txn01.setIssuerAccount("0100223910"); //for log in
             * txnlog txn01.setPCode("2590"); txn01.setSourceAccount("0100223910"); txn01.setICCTxnNo("00000499"); txn01.setICCRemark("B5887850083174560D491212060620213F01AD8F8F8F49ABEFADBC9D9875");
             * txn01.setTmlCheckCode("12341234"); txn01.setTmlType("6534"); txn01.setTAC("0AC0FAE6F6B7CBE6"); txn01.setMAC("30303030303030303030303030303030");
             * txn01.setSessionID("1laskjdfiuo23i9120310923"); txn01.setClientIP("255.255.255.0"); txn01.setClientDT("20080707120000"); txn01.setAuthFlag("0"); if (txn01.doTxn()){
             * System.out.println(txn01.getActionCode()); System.out.println(txn01.getRespCode()); System.out.println(txn01.getSTAN()); System.out.println(txn01.getAccountBalance());
             * System.out.println(txn01.getAvailableBalance()); System.out.println(txn01.getHostDT()); }else{ System.out.println(txn01.getTrnsStatus()); }
             * 
             * 
             * 
             * TxnProcess02 txn02 = new TxnProcess02(txn); //txn02.setICCSEQ("00000685"); txn02.setTransactionType(TxnProcess02.TransactionType_Transfer); txn02.setIssuerBankCode("021");
             * txn02.setIssuerAccount("0100223910"); txn02.setPCode("2580"); txn02.setSourceAccount("0100223910"); txn02.setDestBankCode("021"); txn02.setDestAccount("0100188996");
             * txn02.setTxnAmount("800"); txn02.setICCTxnNo("560"); txn02.setICCRemark("B5887850083174560D491212060620213F01AD8F8F8F49ABEFADBC9D9875"); txn02.setTmlCheckCode("00000000");
             * txn02.setTmlType("6534"); txn02.setTAC("C5BDE63F544BFE82"); txn02.setMAC("30303030303030303030303030303030"); txn02.setSessionID("1laskjdfiuo23i9120310923");
             * txn02.setClientIP("255.255.255.0"); txn02.setClientDT("20080707120000"); txn02.doTxn();
             */
            TxnProcess03 txn03 = new TxnProcess03(txn);
            txn03.setOnUsCard(false);
            txn03.setTransactionType(TxnProcess03.TransactionType_Payment);

            // txn03.setICCSEQ("00000685");
            txn03.setIssuerBankCode("005");
            txn03.setIssuerAccount("0000033005538080");
            txn03.setPCode("2563");
            txn03.setSourceAccount("0000033005538080");
            txn03.setDestBankCode("005");
            txn03.setDestAccount("6000278674");
            txn03.setTxnAmount("900");
            txn03.setICCTxnNo("621");
            txn03.setICCRemark("303035303030303030333330303535333830383030303030303030303031");
            txn03.setTmlCheckCode("00000000");
            txn03.setTmlType("6534");
            txn03.setTAC("91DD32DAEA5802CE");
            txn03.setMAC("30303030303030303030303030303030");
            txn03.setSessionID("1laskjdfiuo23i9120310923");
            txn03.setClientIP("255.255.255.0");
            txn03.setClientDT("20080707120000");
            txn03.doTxn();

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

        TestTxnProcess_E63 test = new TestTxnProcess_E63();
        for (int i = 0; i < 1; i++) {
            (new Thread(test, "ClientThread-" + i)).start();
        }
    }

}
