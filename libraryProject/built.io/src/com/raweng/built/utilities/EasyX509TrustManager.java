package com.raweng.built.utilities;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class EasyX509TrustManager implements X509TrustManager {

	private X509TrustManager standardTrustManager = null;

	public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
		super();
		try {
			TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			factory.init(keystore);
			TrustManager[] trustmanagers = factory.getTrustManagers();
			if (trustmanagers.length == 0) {
				throw new NoSuchAlgorithmException("no trust manager found");
			}
			this.standardTrustManager = (X509TrustManager) trustmanagers[0];
		} catch (Exception e) {}		
	}

	public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		try {
			standardTrustManager.checkClientTrusted(certificates, authType);

		} catch (CertificateException ex) {
			System.out.println("checkClientTrusted => CertificateException : " + ex.getMessage());

		} catch (Exception ex) {
			System.out.println("checkClientTrusted =>  Exception : " + ex.getMessage());
		}
	}

	public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		if ((certificates != null) && (certificates.length == 1)) {
			try {
				certificates[0].checkValidity();

			} catch (CertificateExpiredException ex) {
				System.out.println("CertificateExpiredException  : " + ex.getMessage());

			} catch (CertificateNotYetValidException ex) {
				System.out.println("CertificateNotYetValidException   : " + ex.getMessage());

			} catch (Exception ex) {
				System.out.println("Exception : " + ex.getMessage());
			}
		} else {
			try {
				standardTrustManager.checkServerTrusted(certificates, authType);

			} catch (CertificateException ex) {
				System.out.println("CertificateException : " + ex.getMessage());

			} catch (Exception ex) {
				System.out.println("Exception : " + ex.getMessage());
			}
		}
	}

	public X509Certificate[] getAcceptedIssuers() {
		return this.standardTrustManager.getAcceptedIssuers();
	}
}
