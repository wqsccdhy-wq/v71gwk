package com.seeyon.apps.ldap.sso;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;

/**
 * @author zhangyong
 * @author lilong
 * @version CTP2.0
 */

public class GSContextpool {
	
	private static final Log      log       = LogFactory.getLog(GSContextpool.class);

    public GSContextpool() {
    }

    private ReentrantReadWriteLock rwl   = new ReentrantReadWriteLock();
    private Lock                   rlock = rwl.readLock();
    private Lock                   wlock = rwl.writeLock();

    private GSSManager             manager;
    private Oid                    spnegoOid;
    private GSSCredential          serverCreds;

    public void init() throws GSSException {
        wlock.lock();
        try {
            if (serverCreds != null) {
                serverCreds.dispose();
                serverCreds = null;
            }
            manager = GSSManager.getInstance();
            spnegoOid = new Oid("1.3.6.1.5.5.2");
        } finally {
            wlock.unlock();
        }
    }

    private void initCred() throws GSSException {
        GSSCredential ftemp;
        ftemp = manager.createCredential(null, GSSCredential.DEFAULT_LIFETIME, spnegoOid, GSSCredential.ACCEPT_ONLY);
        serverCreds = ftemp;
    }

    private void uninit() throws GSSException {
        if (serverCreds != null) {
            serverCreds.dispose();
            serverCreds = null;
        }
    }

    public GSSContext getContext() throws GSSException {
        rlock.lock();
        try {
            if (serverCreds == null || serverCreds.getRemainingLifetime() < 30000) {
                rlock.unlock();
                wlock.lock();
                try {
                    try {
                        uninit();
                    } catch (Exception ex) {
                    	log.error("", ex);
                    }
                    initCred();
                } finally {
                    wlock.unlock();
                    rlock.lock();
                }
            }
            GSSContext result = manager.createContext((GSSCredential) serverCreds);
            return result;
        } finally {
            rlock.unlock();
        }
    }

}
