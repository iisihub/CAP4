package com.iisigroup.colabase.demo.sslclient.service.impl;

import com.iisigroup.colabase.model.ResponseContent;
import com.iisigroup.colabase.service.impl.SslClientImpl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/18 AndyChen,new
 *          </ul>
 * @since 2018/5/18
 */
@Service
public class DemoSslClientService extends SslClientImpl<ResponseContent>{

    public DemoSslClientService() {
    }

    public DemoSslClientService(String keyStorePath, String keyStorePWD, String trustStorePath) throws
            CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException, IOException {
        super(keyStorePath, keyStorePWD, trustStorePath);
    }
}
