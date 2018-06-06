package tw.com.citi.ws.hsm;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

public class StubClient extends Thread {

    /**
     * @param args
     *            Creation date:(2007/12/31 上午 10:52:20)
     */
    public static void main(String[] args) throws ServiceException, RemoteException, InterruptedException {
        int round = 1; // run幾遍
        for (int ii = 1; ii <= round; ii++) {
            int count = 1; // 起幾個Thread
            StubClient[] t = new StubClient[count];
            for (int i = 0; i < count; i++) {
                t[i] = new StubClient();
                t[i].start();
            }

            for (int i = 0; i < count; i++) {
                try {
                    t[i].join();
                } catch (Exception e) {
                }
            }

            if (ii != round) {
                System.out.println("round " + ii + " over, start sleep");
                Thread.sleep(10);
            }
        }
    }

    public void run() {
        HsmService_PortType service;
        try {
            service = new HsmService_ServiceLocator().getHsmServiceSOAP();

            DoDESRequest desRequest = new DoDESRequest();
            desRequest.setSlot("0");
            desRequest.setSysID("eATM");
            desRequest.setCheckCode("eATM1234");
            desRequest.setUser("Kevin");
            desRequest.setPasswd("1234");
            desRequest.setKeyType("1");
            desRequest.setKeyName("T-DES-ATM-W93");
            desRequest.setData1("000000000000000000000000000000000000000000000000");
            desRequest.setData2("");
            /*
             * desRequest.setKeyType("3"); desRequest.setKeyName("3desCom"); desRequest.setData1("000000000000000000000000000000000000000000000000");
             * desRequest.setData2("000000000000000000000000000000000000000000000000");
             */

            DoDESResponse desResponse = service.doDES(desRequest);
            System.out.println(desResponse.getData1());
            System.out.println(desResponse.getData2());
            System.out.println(desResponse.getTraceno());
            System.out.println(desResponse.getReturnData());
            System.out.println(desResponse.getRv());
            System.out.println(desResponse.getRvString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
