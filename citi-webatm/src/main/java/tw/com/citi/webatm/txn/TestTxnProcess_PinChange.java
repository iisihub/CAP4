package tw.com.citi.webatm.txn;

import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.TmlPool;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_CHK_CODE;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_CHK_USR;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_SYS_ID;

import org.springframework.beans.factory.annotation.Autowired;

import com.iisigroup.colabase.webatm.service.APSystemService;

import tw.com.citi.ws.hsm.WSSecurity;

public class TestTxnProcess_PinChange implements Runnable {
    @Autowired
    private APSystemService APSystem;

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

            TxnProcess04 txn04 = new TxnProcess04(txn);
            txn04.setIssuerBankCode("021");
            txn04.setIssuerAccount("5887850019007017");
            txn04.setPCode("2599");
            WSSecurity wss = null;
            if (wss == null) {
                wss = new WSSecurity((String) APSystem.getSYS_PRAM_MAP().get("WS_ADDR"), (String) APSystem.getSYS_PRAM_MAP().get("EC_SLOT"), WSS_SYS_ID, WSS_CHK_CODE, WSS_CHK_USR,
                        (String) APSystem.getSYS_PRAM_MAP().get("EC_PWD"));
            }
            String NewPinBlk = "7331FFFFFFFFFFFF";
            String NewPinBlk_Encrypt = wss.do3DESEncrypt(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + "03", NewPinBlk);
            if (NewPinBlk_Encrypt == null) {
                NewPinBlk_Encrypt = "0000000000000000";
            } else {
                NewPinBlk_Encrypt = NewPinBlk_Encrypt.substring(0, 16);
            }
            System.out.println(NewPinBlk_Encrypt);
            txn04.setNewPinBlk(NewPinBlk_Encrypt);
            // txn04.setOldPinBlk("525E81573CF726DC");
            txn04.setClientDT("20080707120000");
            txn04.doTxn();

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

        TestTxnProcess_PinChange test = new TestTxnProcess_PinChange();
        for (int i = 0; i < 50; i++) {
            (new Thread(test, "ClientThread-" + i)).start();
        }
    }

}
